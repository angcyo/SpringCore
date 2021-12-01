package com.angcyo.spring.security.service

import com.angcyo.java.mail.dslSendMail
import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.servlet.request
import com.angcyo.spring.redis.Redis
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.*
import com.angcyo.spring.security.jwt.event.RegisterAccountEvent
import com.angcyo.spring.security.service.annotation.RegisterAccount
import com.angcyo.spring.security.service.annotation.SaveAccount
import com.angcyo.spring.security.table.AccountTable
import com.angcyo.spring.security.table.UserRoleReTable
import com.angcyo.spring.security.table.UserTable
import com.angcyo.spring.util.ImageCode
import com.angcyo.spring.util.isEmail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 授权服务, 保存用户, 查找用户, 设置用户角色
 */

@Service
class AuthService {

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var redis: Redis

    @Autowired
    lateinit var appProperties: AppProperties

    val userPrefix: String
        get() = "${appProperties.name}.USER"

    val tokenPrefix: String
        get() = "${appProperties.name}.TOKEN"

    val sendCodePrefix: String
        get() = "${appProperties.name}.CODE.SEND"

    //<editor-fold desc="验证码相关">

    val imageCodePrefix: String
        get() = "${appProperties.name}.CODE.IMAGE"

    fun imageCodeKey(uuid: String, type: Int): String {
        return "${imageCodePrefix}.${type}.${uuid}"
    }

    /**临时保存图形验证码
     * [time] 有效时长默认1分钟*/
    fun setImageCode(uuid: String, type: Int, code: String, time: Long = 1 * 60) {
        redis[imageCodeKey(uuid, type), time] = code
    }

    /**获取保存过的验证码*/
    fun getImageCode(uuid: String, type: Int): String? {
        return redis[imageCodeKey(uuid, type)]?.toString()
    }

    /**清除验证码缓存*/
    fun clearImageCode(uuid: String, type: Int) {
        redis.del(imageCodeKey(uuid, type))
    }

    fun sendCodeKey(uuid: String, target: String, type: Int): String {
        return "${sendCodePrefix}.${type}.${uuid}.${target}"
    }

    /**发送一个验证码
     * [target] 发送的目标, 目前支持手机号, 邮箱号
     * [uuid] 目标客户端
     * [code] 需要发送的验证码
     * [type] 验证码类型
     * [length] 验证码位数
     * [time] 有效时长默认5分钟*/
    fun sendCode(uuid: String, target: String, type: Int, length: Int, time: Long = appProperties.codeTime): Boolean {
        //code: String
        //需要发送的验证码
        val code = ImageCode.generateCode(length)

        var result = false

        if (target.isEmail()) {
            val title: String
            val content: String

            if (type == CodeType.Login.value) {
                title = "欢迎登录${appProperties.fullName ?: appProperties.name}"
                content = "本次登录验证码: $code"
            } else {
                title = "欢迎注册${appProperties.fullName ?: appProperties.name}"
                content = "本次注册验证码: $code"
            }

            result = dslSendMail(target, title, content)
        }

        return result && redis.set(sendCodeKey(uuid, target, type), time, code)
    }

    fun getSendCode(uuid: String, target: String, type: Int): String? {
        return redis[sendCodeKey(uuid, target, type)]?.toString()
    }

    fun clearSendCode(uuid: String, target: String, type: Int) {
        redis.del(sendCodeKey(uuid, target, type))
    }

    //</editor-fold desc="验证码相关">

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRoleService: UserRoleService

    @Autowired
    lateinit var applicationEventPublisher: ApplicationEventPublisher

    /**保存一个帐号*/
    @SaveAccount
    @Transactional
    fun saveAccount(req: SaveAccountReqBean): UserTable {
        val registerBean = req.registerReqBean
        val username = registerBean?.account

        //用户
        val user = UserTable()
        user.state = 1//用户状态
        user.nickname = registerBean?.nickname ?: username
        user.avatar = registerBean?.avatar
        user.phone = registerBean?.phone
        user.email = registerBean?.email
        user.sex = registerBean?.sex
        user.password = passwordEncoder.encode(registerBean?.password ?: username)
        userService.save(user)

        //创建帐号,用户登录用户
        val account = AccountTable()
        account.name = username
        account.userId = user.id //帐号关联用户
        accountService.save(account)

        //分配角色, 如果有
        if (!req.roleIdList.isNullOrEmpty()) {
            val userRoleList = mutableListOf<UserRoleReTable>()
            req.roleIdList?.forEach {
                userRoleList.add(UserRoleReTable().apply {
                    this.userId = user.id
                    this.roleId = it
                })
            }
            userRoleService.autoReset(userRoleList)
        }

        return user
    }

    /**注册用户, 写入数据库*/
    @RegisterAccount
    @Transactional
    fun register(bean: RegisterReqBean): UserTable? {

        val grantType = bean.grantType?.lowercase()

        request()?.let {
            val codeKey = it.codeKey()
            //优先验证图形验证码, 如果有. 或者传入了图形验证码
            if (!bean.imageCode.isNullOrEmpty() || redis.hasKey(imageCodeKey(codeKey, CodeType.Register.value))) {
                if (bean.imageCode?.lowercase() != getImageCode(codeKey, CodeType.Register.value)?.lowercase()) {
                    //如果获取了图形验证码, 但是不匹配
                    apiError("验证码不正确")
                }
            }
        }

        when (grantType) {
            GrantType.Password.value -> {
                //密码 授权注册方式
            }
            GrantType.Code.value -> {
                //验证码 授权注册方式
                val uuid = currentClientUuid()
                if (uuid.isNullOrEmpty()) {
                    apiError("无效的客户端")
                }
                val code = getSendCode(uuid, bean.account!!, CodeType.Register.value)?.lowercase()
                if (code == null || code != bean.code?.lowercase()) {
                    apiError("验证码不正确")
                }
            }
            else -> apiError("无效的注册方式")
        }

        if (accountService.isAccountExist(bean.account)) {
            apiError("帐号已存在")
        }

        val user = beanOf<AuthService>().saveAccount(SaveAccountReqBean().apply {
            registerReqBean = bean
        })

        //发送注册账号成功事件
        applicationEventPublisher.publishEvent(RegisterAccountEvent(bean, user))

        return user
    }

    /**临时用户对象*/
    fun tempUserTable() = UserTable().apply {
        nickname = "临时用户"
        des = "临时用户"
    }

    fun tempUserDetail() = UserDetail().apply {
        userTable = tempUserTable()
    }

    /**redis 存储token的key*/
    fun userTokenKey(username: String): String {
        //客户端类型, 允许多端登录
        val clientType = currentClientType()
        val appProperties = beanOf<AppProperties>()

        return if (appProperties.multiLogin) {
            "${tokenPrefix}.${clientType}.$username"
        } else {
            "${tokenPrefix}.$username"
        }
    }

    fun userKey(username: String): String {
        return "${userPrefix}.$username"
    }

    /**检查用户的token, 是否和redis里面的一样
     * [token] 支持包含/不包含前缀的token*/
    fun _checkTokenValid(username: String, token: String): Boolean {
        val key = userTokenKey(username)
        if (redis.hasKey(key)) {
            val redisToken = redis[key]
            if (token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                return SecurityConstants.TOKEN_PREFIX + redisToken == token
            }
            return redisToken == token
        } else {
            return false
        }
    }

    /**从redis中获取用户信息*/
    fun getUserDetail(username: String): UserDetail? {
        return redis[userKey(username)] as? UserDetail?
    }

    /**[token] 不含前缀的token
     * [time] token过期时间, 秒, 默认1天*/
    fun _loginEnd(userDetail: UserDetail, token: String, time: Long = appProperties.tokenTime) {
        val id = "${userDetail.userTable?.id}"
        //保存token, 一天超时
        redis[userTokenKey(id), time] = token

        //将用户信息保存至redis
        redis[userKey(id), time] = userDetail
    }

    /**退出登录*/
    fun _logoutEnd(userDetail: UserDetail?) {
        SecurityContextHolder.clearContext()
        val id = "${userDetail?.userTable?.id}"

        redis.del(userTokenKey(id))
        redis.del(userKey(id))
    }
}
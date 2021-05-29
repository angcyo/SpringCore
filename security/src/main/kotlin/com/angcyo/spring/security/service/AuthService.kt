package com.angcyo.spring.security.service

import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.redis.Redis
import com.angcyo.spring.security.SecurityConstants
import com.angcyo.spring.security.bean.RegisterReqBean
import com.angcyo.spring.security.bean.SaveAccountReqBean
import com.angcyo.spring.security.table.AccountTable
import com.angcyo.spring.security.table.UserTable
import com.angcyo.spring.util.oneDaySec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/07
 *
 * 授权服务, 保存用户, 查找用户, 设置用户角色
 */

@Service
class AuthService {

    companion object {
        /**注册时的验证码类型*/
        const val CODE_TYPE_REGISTER = 1

        /**登录时的验证码类型*/
        const val CODE_TYPE_LOGIN = 2
    }

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var redis: Redis

    @Autowired
    lateinit var applicationProperties: AppProperties

    val tokenPrefix: String
        get() = "${applicationProperties.name}.TOKEN"

    //<editor-fold desc="验证码相关">

    val imageCodePrefix: String
        get() = "${applicationProperties.name}.CODE.IMAGE"

    /**临时保存图形验证码*/
    fun setImageCode(request: HttpServletRequest, type: Int, code: String, time: Long = 1 * 60) {
        redis["${imageCodePrefix}.${type}.${request.codeKey()}", code] = time
    }

    /**获取保存过的验证码*/
    fun getImageCode(request: HttpServletRequest, type: Int): String? {
        return redis["${imageCodePrefix}.${type}.${request.codeKey()}"]?.toString()
    }

    /**清除验证码缓存*/
    fun clearImageCode(request: HttpServletRequest, type: Int) {
        redis.del("${imageCodePrefix}.${type}.${request.codeKey()}")
    }

    //</editor-fold desc="验证码相关">

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var userService: UserService

    /**保存一个帐号*/
    @SaveAccount
    @Transactional
    fun saveAccount(req: SaveAccountReqBean): UserTable {
        val username = req.registerReqBean?.username

        //用户
        val user = UserTable()
        user.state = 1
        user.nickname = username
        user.password = passwordEncoder.encode(req.registerReqBean?.password)
        userService.save(user)

        //创建帐号,用户登录用户
        val account = AccountTable()
        account.name = username
        account.userId = user.id //帐号关联用户
        accountService.save(account)

        return user
    }

    /**注册用户, 写入数据库*/
    @Transactional
    fun register(bean: RegisterReqBean): UserTable? {
        if (accountService.isAccountExist(bean.username)) {
            apiError("帐号已存在")
        }

        return beanOf<AuthService>().saveAccount(SaveAccountReqBean().apply {
            registerReqBean = bean
        })
    }

    /**临时用户对象*/
    fun tempUserTable() = UserTable().apply {
        nickname = "临时用户"
        description = "临时用户"
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

    /**[token] 不含前缀的token
     * [time] token过期时间, 秒, 默认1天*/
    fun _loginEnd(username: String, token: String, time: Long = oneDaySec) {
        //保存token, 一天超时
        redis[userTokenKey(username), token] = time
    }

    /**退出登录*/
    fun _logoutEnd(username: String) {
        SecurityContextHolder.clearContext()
        redis.del(userTokenKey(username))
    }
}
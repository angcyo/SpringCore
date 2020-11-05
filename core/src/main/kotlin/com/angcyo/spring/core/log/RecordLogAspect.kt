package com.angcyo.spring.core.log

import com.angcyo.spring.core.json.toJson
import com.angcyo.spring.core.nowTimeString
import com.angcyo.spring.core.util.IPUtil
import com.angcyo.spring.core.uuid
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/05
 *
 * 定义一个切面
 */

@Aspect
@Component
class RecordLogAspect {

    /**切入点*/
    @Pointcut("@annotation(com.angcyo.spring.core.log.RecordLog)")
    fun recordLog() {
    }

    /**切面环绕通知*/
    @Around("recordLog()")
    fun doAround(joinPoint: ProceedingJoinPoint): Any? {

        // 开始时间
        val startTime = System.currentTimeMillis()

        val nowTime = nowTimeString()
        val uuid = uuid()

        val signature: MethodSignature = joinPoint.signature as MethodSignature

        // 获取方法上自定义的描述
        val methodDesc: String = signature.method.getAnnotation(RecordLog::class.java).des

        // 获取Request对象
        val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        val request = requestAttributes.request

        L.i("-----------开始访问方法:$nowTime-------------")
        L.i("$uuid ↓")
        // 方法描述
        L.i("方法描述：", methodDesc)
        // 请求url
        L.i("请求url：", request.requestURL.toString())
        // 请求类型
        L.i("请求类型：", request.method)
        // 请求方法
        L.i("请求方法：", signature.declaringTypeName, signature.name)
        // 请求IP
        L.i("请求IP：", IPUtil.getIpAddress(request))
        // 请求头
        request.headerNames.iterator().forEach {
            L.i(it, request.getHeader(it))
        }
        // 请求参数
        L.i("请求参数：", joinPoint.args.toJson())

        val result = joinPoint.proceed()

        // 请求耗时
        L.i("请求耗时：${System.currentTimeMillis() - startTime}ms")
        // 请求返回
        L.i("请求返回：", result.toJson())
        L.i("------------------请求结束-----------------------")
        L.i("$uuid ↑")
        return result
    }
}
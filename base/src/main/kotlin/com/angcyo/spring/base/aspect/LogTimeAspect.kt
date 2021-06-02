package com.angcyo.spring.base.aspect

import com.angcyo.spring.util.L
import com.angcyo.spring.util.nowTime
import com.angcyo.spring.util.toElapsedTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/02
 */

@Aspect
@Component
class LogTimeAspect {

    @Around("@annotation(com.angcyo.spring.base.aspect.LogMethodTime)")
    fun logMethodTime(point: ProceedingJoinPoint): Any? {
        return if (L.isDebug) {
            val startTime = nowTime()
            val result = point.proceed(point.args)
            val nowTime = nowTime()
            val duration = (nowTime - startTime).toElapsedTime(intArrayOf(1, 1, 1))

            var des: String? = null
            val signature = point.signature
            if (signature is MethodSignature) {
                val logMethodTime = signature.method.getAnnotation(LogMethodTime::class.java)
                des = logMethodTime?.description
            }

            val method = signature.toString()
            L.w("${if (des.isNullOrEmpty()) "" else "[$des]"}调用耗时:${duration}☞${method}")
            result
        } else {
            point.proceed(point.args)
        }
    }

}
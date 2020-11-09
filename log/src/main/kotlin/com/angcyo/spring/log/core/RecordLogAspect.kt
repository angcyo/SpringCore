package com.angcyo.spring.log.core

import com.angcyo.spring.base.json.toJson
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.concurrent.atomic.AtomicLong


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/05
 *
 * 定义一个切面
 *
 * https://www.cnblogs.com/xiao-lei/p/11707222.html
 */

@Aspect
@Component
class RecordLogAspect {

    private val id = AtomicLong(1)

    /**
     * 指定切点
     * 匹配所有Controller类的所有方法
     */
    @Pointcut("execution(* com.angcyo.spring..*.*Controller*(..))")
    fun recordAll() {
    }

    /**切入点*/
    @Pointcut("@annotation(com.angcyo.spring.log.core.RecordLog)")
    fun recordLog() {
    }

    /**切面环绕通知*/
    @Around("recordLog()")
    fun doAround(joinPoint: ProceedingJoinPoint): Any? {
        val signature: MethodSignature = joinPoint.signature as MethodSignature

        // 获取方法上自定义的描述
        val methodDesc: String = signature.method.getAnnotation(RecordLog::class.java).des

        // 获取Request对象
        val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        val request = requestAttributes.request
        val response = requestAttributes.response

        val result = joinPoint.proceed()

        ServletLog.wrap(id.incrementAndGet(),
                request, response,
                StringBuilder().apply {
                    if (methodDesc.isNotEmpty()) {
                        appendLine()
                        append(methodDesc)
                    }
                    if (joinPoint.args.isNotEmpty()) {
                        appendLine()
                        append(joinPoint.args.toJson())
                    }
                },
                StringBuilder().apply {
                    if (result != null) {
                        appendLine()
                        append(result)
                    }
                },
                wrap = false) { _, _, _, _ ->
        }

        return result
    }
}
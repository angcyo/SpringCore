package com.angcyo.spring.http.test

import com.angcyo.spring.util.L
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/26
 */

@Component
class TestHttpApplicationRunner : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if (L.isDebug) {
            /*request {
                url = "https://gitee.com/angcyo/json/raw/master/accauto/memory_config.json"
                onEndAction = { response, exception ->
                    val bodyString = response?.body()?.readString()
                    L.i(bodyString)
                    L.i(exception)
                }
            }*/
        }
    }
}

@Component
class TestHttpCommandLineRunner : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (L.isDebug) {
            //L.i("TestHttpCommandLineRunner:$args")
        }
    }
}
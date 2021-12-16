package com.angcyo.spring.app

import com.angcyo.spring.app.service.FilePathService
import com.angcyo.spring.util.connectSeparator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/16
 */

@Component
class AppWebMvcConfiguration : WebMvcConfigurer {

    @Autowired
    lateinit var fileProperties: FileProperties

    @Autowired
    lateinit var filePathService: FilePathService

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        super.addResourceHandlers(registry)

        val filesDir = fileProperties.filesDir
        val downloadsDir = fileProperties.downloadsDir

        val filesPath = filePathService.filesLocation.toString().connectSeparator(File.separatorChar)
        val downloadsPath = filePathService.filesDownloadsLocation.toString().connectSeparator(File.separatorChar)

        //https://blog.csdn.net/sinat_34104446/article/details/100178488
        registry.addResourceHandler("/**").addResourceLocations(
            "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/public/",
            "classpath:/webapp/"
        )
        // 注意如果filePath是写死在这里，一定不要忘记尾部的/或者\\，这样才能读取其目录下的文件
        registry.addResourceHandler("/$filesDir/**").addResourceLocations("file:$filesPath")
        registry.addResourceHandler("/${downloadsDir}/**").addResourceLocations("file:$downloadsPath")
    }
}
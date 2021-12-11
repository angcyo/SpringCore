package com.angcyo.spring.app.service

import com.angcyo.spring.app.FileProperties
import com.angcyo.spring.base.extension.ApiException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.PostConstruct

/**
 * 文件路径服务
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/11
 */

@Service
class FilePathService {

    @Autowired
    lateinit var fileProperties: FileProperties

    lateinit var fileUploadLocation: Path
    lateinit var fileOfficeLocation: Path

    @PostConstruct
    fun init() {
        fileUploadLocation = Paths.get(fileProperties.uploadDir ?: "uploads").toAbsolutePath().normalize()
        fileOfficeLocation = Paths.get(fileProperties.officeDir ?: "office").toAbsolutePath().normalize()
        try {
            Files.createDirectories(fileUploadLocation)
            Files.createDirectories(fileOfficeLocation)
        } catch (ex: Exception) {
            throw ApiException("Could not create the directory where the uploaded files will be stored.")
        }
    }

    /**[Path.toString]*/
    fun getFileUploadPath(fileName: String): Path {
        return fileUploadLocation.resolve(fileName)
    }

    fun getOfficePath(fileName: String): Path {
        return fileOfficeLocation.resolve(fileName)
    }
}
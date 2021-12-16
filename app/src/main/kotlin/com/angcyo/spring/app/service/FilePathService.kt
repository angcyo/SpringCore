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
    lateinit var filesLocation: Path
    lateinit var filesDownloadsLocation: Path

    @PostConstruct
    fun init() {
        fileUploadLocation = Paths.get(fileProperties.uploadDir!!).toAbsolutePath().normalize()
        fileOfficeLocation = Paths.get(fileProperties.officeDir!!).toAbsolutePath().normalize()
        filesLocation = Paths.get(fileProperties.filesDir!!).toAbsolutePath().normalize()
        filesDownloadsLocation = Paths.get(fileProperties.downloadsDir!!).toAbsolutePath().normalize()
        try {
            Files.createDirectories(fileUploadLocation)
            Files.createDirectories(fileOfficeLocation)
            Files.createDirectories(filesLocation)
            Files.createDirectories(filesDownloadsLocation)
        } catch (ex: Exception) {
            throw ApiException("Could not create the directory where the uploaded files will be stored.")
        }
    }

    /**在指定路径下, 创建指定的文件夹路径*/
    fun createPath(parent: Path, folder: String): Path {
        val path = parent.resolve(folder)
        Files.createDirectories(path)
        return path
    }

    /**[Path.toString]*/
    fun getFileUploadPath(fileName: String): Path {
        return fileUploadLocation.resolve(fileName)
    }

    fun getOfficePath(fileName: String): Path {
        return fileOfficeLocation.resolve(fileName)
    }

    /**在[filesLocation]下, 创建一个文件*/
    fun getFilePath(fileName: String): Path {
        return filesLocation.resolve(fileName)
    }

    /**在下载中心路径, 创建一个文件*/
    fun getDownloadsPath(fileName: String): Path {
        return filesDownloadsLocation.resolve(fileName)
    }
}
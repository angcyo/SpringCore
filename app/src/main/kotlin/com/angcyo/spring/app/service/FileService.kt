package com.angcyo.spring.app.service

import com.angcyo.spring.app.FileProperties
import com.angcyo.spring.app.table.FileTable
import com.angcyo.spring.app.table.mapper.IFileMapper
import com.angcyo.spring.base.AppProperties
import com.angcyo.spring.base.beanOf
import com.angcyo.spring.base.classOf
import com.angcyo.spring.base.extension.ApiException
import com.angcyo.spring.base.servlet.IOssService
import com.angcyo.spring.mybatis.plus.auto.BaseAutoMybatisServiceImpl
import com.angcyo.spring.mybatis.plus.columnName
import com.angcyo.spring.util.Constant
import com.angcyo.spring.util.md5
import com.angcyo.spring.util.nowTimeString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.annotation.PostConstruct

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

@Service
class FileService : BaseAutoMybatisServiceImpl<IFileMapper, FileTable>() {

    @Autowired
    lateinit var fileProperties: FileProperties

    lateinit var fileStorageLocation: Path

    @Autowired
    lateinit var appProperties: AppProperties

    @PostConstruct
    fun init() {
        fileStorageLocation = Paths.get(fileProperties.uploadDir).toAbsolutePath().normalize()
        try {
            Files.createDirectories(fileStorageLocation)
        } catch (ex: Exception) {
            throw ApiException("Could not create the directory where the uploaded files will be stored.")
        }
    }

    /**
     * 存储文件到系统
     *
     * @param file 文件
     * @return 文件名
     */
    fun storeFile(file: MultipartFile): String {
        // Normalize file name
        val fileName = StringUtils.cleanPath(file.originalFilename!!)
        return try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw ApiException("Sorry! Filename contains invalid path sequence $fileName")
            }

            // Copy file to the target location (Replacing existing file with the same name)
            val targetLocation = fileStorageLocation.resolve(fileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            fileName
        } catch (ex: IOException) {
            ex.printStackTrace()
            throw ApiException("Could not store file $fileName. Please try again!")
        }
    }

    /**
     * 加载文件
     * @param fileName 文件名
     * @return 文件
     */
    fun loadFileAsResource(fileName: String): Resource {
        return try {
            val filePath = fileStorageLocation.resolve(fileName).normalize()
            val resource: Resource = UrlResource(filePath.toUri())
            if (resource.exists()) {
                resource
            } else {
                throw ApiException("File not found $fileName")
            }
        } catch (ex: MalformedURLException) {
            throw ApiException("File not found $fileName")
        }
    }

    /**优先保存至oss, 其次保存至服务器目录*/
    fun saveFileOrOss(file: MultipartFile): FileTable {
        val existTable = findFileByMd5(file.inputStream.md5())
        if (existTable != null) {
            return existTable
        }

        val fileName: String = StringUtils.cleanPath(file.originalFilename!!)

        //是否有oss服务
        val ossClass = classOf("com.angcyo.spring.aliyun.oss.AliyunOssService")
        val key: String

        val uri: String = if (ossClass == null) {
            storeFile(file)
            key = "/file/downloadFile/${fileName}"
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(key)
                .toUriString()
        } else {
            val oss = beanOf(ossClass) as IOssService
            key = "${appProperties.name}/${nowTimeString(Constant.DEFAULT_DATE_FORMATTER)}/$fileName"
            oss.upload(key, file.inputStream)
        }

        val table = FileTable()
        table.fileName = fileName
        table.fileSize = file.size
        table.fileType = file.contentType
        table.fileUri = uri
        table.filePath = key
        table.fileMd5 = file.inputStream.md5()

        save(table)

        return table
    }

    /**根据md5值, 查询文件记录*/
    fun findFileByMd5(md5: String?): FileTable? {
        if (md5.isNullOrEmpty()) {
            return null
        }
        return listOf(hashMapOf(FileTable::fileMd5.columnName() to md5)).firstOrNull()
    }
}
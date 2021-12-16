package com.angcyo.spring.app.controller

import com.angcyo.spring.app.service.FileService
import com.angcyo.spring.app.service.resultResource
import com.angcyo.spring.app.table.FileTable
import com.angcyo.spring.base.data.Result
import com.angcyo.spring.base.data.ok
import com.angcyo.spring.base.data.result
import com.angcyo.spring.base.extension.apiError
import com.angcyo.spring.base.servlet.param
import com.angcyo.spring.base.servlet.request
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

@Api(tags = ["文件管理的控制器"])
@RequestMapping("/file")
@RestController
class FileController {

    @Autowired
    lateinit var fileService: FileService

    @PostMapping("/checkFile")
    @ApiOperation("秒传检查")
    @ApiImplicitParam(name = "md5", paramType = "header", value = "文件的md5值, 用于检查秒传")
    fun checkFile(@RequestHeader(required = true) md5: String): Result<FileTable> {
        if (md5.isEmpty()) {
            apiError("无效的MD5值")
        }
        return fileService.findFileByMd5(md5).ok()
    }

    @PostMapping("/uploadFile")
    @ApiOperation("上传文件")
    @ApiImplicitParams(
        ApiImplicitParam(name = "md5", paramType = "header", value = "文件的md5值, 用于秒传"),
        ApiImplicitParam(name = "file", paramType = "form", value = "需要上传的文件", required = true),
    )
    fun uploadFile(@RequestParam("file", required = true) file: MultipartFile): Result<FileTable> {
        val md5 = request()?.param("md5")
        return (fileService.findFileByMd5(md5) ?: fileService.saveFileOrOss(file)).result()
    }

    @PostMapping("/uploadFiles")
    @ApiOperation("批量上传文件")
    @ApiImplicitParam(name = "files", paramType = "form", value = "批量需要上传的文件", required = true)
    fun uploadMultipleFiles(
        @RequestParam(
            "files",
            required = true
        ) files: Array<MultipartFile>
    ): Result<List<FileTable>> {
        return Arrays.stream(files)
            .map { file: MultipartFile -> uploadFile(file).data }
            .collect(Collectors.toList()).result()
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    @ApiOperation("下载文件")
    fun downloadFile(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource> {
        // Load file as Resource
        val resource: Resource = fileService.loadFileAsResource(fileName)
        return resource.resultResource(request)
    }
}
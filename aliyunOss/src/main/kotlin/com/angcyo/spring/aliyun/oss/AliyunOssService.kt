package com.angcyo.spring.aliyun.oss

import com.aliyun.oss.OSSClientBuilder
import com.angcyo.spring.base.servlet.IOssService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.InputStream


/**
 * 阿里云文件上传
 *
 * [com.angcyo.spring.app.service.FileService.saveFileOrOss]
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

@Component
class AliyunOssService : IOssService {

    @Autowired
    lateinit var ossProperties: AliyunOssProperties

    override fun upload(key: String, inputStream: InputStream): String {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        val endpoint = ossProperties.endpoint
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        val accessKeyId = ossProperties.accessKeyId
        val accessKeySecret = ossProperties.accessKeySecret

        val bucketName = ossProperties.bucketName

        // 创建OSSClient实例。
        val ossClient = OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        //val inputStream: InputStream = FileInputStream("D:\\localpath\\examplefile.txt")

        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        val result = ossClient.putObject(bucketName, key, inputStream)

        // 关闭OSSClient。
        ossClient.shutdown()

        return "${ossProperties.bucketUrl ?: ""}${key}"
    }
}
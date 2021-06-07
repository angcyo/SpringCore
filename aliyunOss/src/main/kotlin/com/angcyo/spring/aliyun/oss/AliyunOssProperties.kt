package com.angcyo.spring.aliyun.oss

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */

@Component
@ConfigurationProperties(prefix = "aliyun.oss")
class AliyunOssProperties {

    // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。

    /**
     * OSS endpoint, check out:
     *
     * http://help.aliyun.com/document_detail/oss/user_guide/endpoint_region.html
     *
     * //访问域名和数据中心
     * https://help.aliyun.com/document_detail/31837.htm
     *
     */
    var endpoint: String? = null

    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    var accessKeyId: String? = null

    var accessKeySecret: String? = null

    var bucketName: String? = null

    /**http地址, [/]结尾, 也可以不*/
    var bucketUrl: String? = null
}
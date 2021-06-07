package com.angcyo.spring.base.servlet

import java.io.InputStream

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/07
 */
interface IOssService {

    /**
     * [key] 可以保存文件路径, 也可以直接是文件名. 不需要[/]开头
     * [inputStream] 需要上传的流
     * @return 可以访问的uri
     * */
    fun upload(key: String, inputStream: InputStream): String
}
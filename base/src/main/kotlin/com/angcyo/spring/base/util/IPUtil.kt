package com.angcyo.spring.base.util

import java.net.InetAddress
import javax.servlet.http.HttpServletRequest

/**
 * @ClassName: IPUtil
 * @Description:获取ip地址工具类
 * @author: jinghx
 * @date: 2020/5/26 16:28
 */
object IPUtil {

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     */
    fun getIpAddress(request: HttpServletRequest): String? {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        var ip = request.getHeader("X-Forwarded-For")
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("Proxy-Client-IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("WL-Proxy-Client-IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("HTTP_CLIENT_IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.remoteAddr
            }
        } else if (ip.length > 15) {
            val ips = ip.split(",").toTypedArray()
            for (index in ips.indices) {
                val strIp = ips[index]
                if (!"unknown".equals(strIp, ignoreCase = true)) {
                    ip = strIp
                    break
                }
            }
        }
        if ("127.0.0.1" == ip || "0:0:0:0:0:0:0:1" == ip) {
            // 根据网卡取本机配置的IP
            ip = localIp
        }
        return ip
    }

    /**
     * 获取本机IP
     */
    val localIp: String
        get() {
            val inetAddress = InetAddress.getLocalHost()
            return inetAddress.hostAddress.toString()
        }
}
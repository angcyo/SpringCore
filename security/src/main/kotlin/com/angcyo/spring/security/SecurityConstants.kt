package com.angcyo.spring.security

/**
 * 2020-11-06
 * https://dev.to/kubadlo/spring-security-with-jwt-3j76
 */
object SecurityConstants {
    const val AUTH_LOGIN_URL = "/auth/login"
    const val AUTH_LOGOUT_URL = "/auth/logout"
    const val AUTH_LOGOUT_SUCCESS_URL = "$AUTH_LOGIN_URL?logout"
    const val AUTH_REGISTER_URL = "/auth/register"
    const val AUTH_REGISTER_CODE_URL = "/auth/code"

    // Signing key for HS512 algorithm
    // You can use the page http://www.allkeysgenerator.com/ to generate all kinds of keys
    const val JWT_SECRET = "n2r5u8x/A%D*G-KaPdSgVkYp3s6v9y\$B&E(H+MbQeThWmZq4t7w!z%C*F-J@NcRf"

    // JWT token defaults
    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val TOKEN_TYPE = "JWT"
    const val TOKEN_ISSUER = "secure-api"
    const val TOKEN_AUDIENCE = "secure-app"
    const val TOKEN_ROLES = "roles"

    //header parameter
    const val KEY_USERNAME = "username"
    const val KEY_PASSWORD = "password"
    const val KEY_TOKEN_TYPE = "token-type"
}
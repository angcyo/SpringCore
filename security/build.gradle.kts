dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    implementation(project(":SpringCore:base"))

    api("org.springframework.boot:spring-boot-starter-security")

    /*https://mvnrepository.com/artifact/org.springframework.security/spring-security-jwt*/
    api("org.springframework.security:spring-security-jwt:1.1.1.RELEASE")

    /* https://jcenter.bintray.com/io/jsonwebtoken */
    //api("io.jsonwebtoken:jjwt:0.9.1")
    api("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")

    //mysql entity
    implementation(project(":SpringCore:redis"))
    //implementation(project(":SpringCore:swagger"))

    //用户登录授权
    api(project(":SpringCore:mybatisplus"))

    //发送邮件
    api(project(":SpringCore:javamail"))
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

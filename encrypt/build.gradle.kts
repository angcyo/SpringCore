dependencies {
    implementation(project(":SpringCore:base"))

    //https://gitee.com/ishuibo/rsa-encrypt-body-spring-boot
    //https://jcenter.bintray.com/cn/shuibo/rsa-encrypt-body-spring-boot/
    //api("cn.shuibo:rsa-encrypt-body-spring-boot:1.0.1.RELEASE")

    //https://jcenter.bintray.com/com/alibaba/fastjson/
    api("com.alibaba:fastjson:1.2.75")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

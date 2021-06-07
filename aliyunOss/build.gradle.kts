dependencies {
    implementation(project(":SpringCore:base"))

    //https://github.com/aliyun/aliyun-oss-java-sdk
    api("com.aliyun.oss:aliyun-sdk-oss:3.12.0")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
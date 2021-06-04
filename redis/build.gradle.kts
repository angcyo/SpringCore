dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }

    implementation(project(":SpringCore:base"))

    //https://github.com/alibaba/fastjson
    api("com.alibaba:fastjson:1.2.75") //1.2.75

    api("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


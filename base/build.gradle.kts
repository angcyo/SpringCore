dependencies {
    //spring web
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // aspect 切面
    api("org.springframework:spring-aspects")

    //gson
    api("com.google.code.gson:gson")

    //TeeInputStream
    //https://jcenter.bintray.com/commons-io/commons-io/
    //https://mvnrepository.com/artifact/commons-io/commons-io
    api("commons-io:commons-io:2.8.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {

    //spring web
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    //kotlin
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // aspect 切面
    implementation("org.springframework:spring-aspects")

    //slf4j
    //implementation("org.springframework.boot:spring-boot-starter-logging")

    //gson
    api("com.google.code.gson:gson")

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

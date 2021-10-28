/*import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
}*/

dependencies {
    api(project(":SpringCore:util"))

    //spring web
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // aspect 切面
    //api("org.springframework:spring-aspects")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-aop")

    //gson
    api("com.google.code.gson:gson")

    //TeeInputStream
    //https://jcenter.bintray.com/commons-io/commons-io/
    //https://mvnrepository.com/artifact/commons-io/commons-io
    api("commons-io:commons-io:2.8.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }

    //数据校验
    api("org.springframework.boot:spring-boot-starter-validation")
    //https://mvnrepository.com/artifact/com.h2database
    //runtimeOnly("com.h2database:h2:1.4.200")

    //implementation(kotlin("stdlib-jdk8"))

    api(project(":SpringCore:swagger"))
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

/*
repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}*/

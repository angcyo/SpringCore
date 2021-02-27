dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }

    //lombok
    /*compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")*/

    api(project(":SpringCore:core"))
    api(project(":SpringCore:mysql"))
    api(project(":SpringCore:redis"))
    api(project(":SpringCore:security"))
    //api(project(":SpringCore:swagger"))

    /*//https://github.com/belerweb/pinyin4j
    //https://mvnrepository.com/artifact/com.belerweb/pinyin4j
    //https://jcenter.bintray.com/com/belerweb/pinyin4j/
    api("com.belerweb:pinyin4j:2.5.1")*/

    //https://github.com/promeG/TinyPinyin
    api("com.github.promeg:tinypinyin:2.0.3") // TinyPinyin核心包，约80KB
    api("com.github.promeg:tinypinyin-lexicons-java-cncity:2.0.3") // 可选，适用于Java的中国地区词典
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


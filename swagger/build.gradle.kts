dependencies {
    /* https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui */

    val version = "3.0.0"
    /* https://jcenter.bintray.com/io/springfox/springfox-swagger2 */
    //api("io.springfox:springfox-swagger2:$version")

    /* https://jcenter.bintray.com/io/springfox/springfox-swagger-ui */
    //api("io.springfox:springfox-swagger-ui:$version")

    api("io.springfox:springfox-boot-starter:$version")

    //https://doc.xiaominfo.com/
    //https://mvnrepository.com/artifact/com.github.xiaoymin
    api("com.github.xiaoymin:knife4j-spring-boot-starter:3.0.1")
    //api("com.github.xiaoymin:knife4j-spring-boot-starter:2.0.7")

    //https://mvnrepository.com/artifact/com.github.xiaoymin/knife4j-spring-ui
    //api("com.github.xiaoymin:knife4j-springdoc-ui:3.0.1")
    //api("com.github.xiaoymin:swagger-bootstrap-ui:1.9.6")

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

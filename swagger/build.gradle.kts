dependencies {
    /* https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui */

    val version = "3.0.0"
    /* https://jcenter.bintray.com/io/springfox/springfox-swagger2 */
    //api("io.springfox:springfox-swagger2:$version")

    /* https://jcenter.bintray.com/io/springfox/springfox-swagger-ui */
    //api("io.springfox:springfox-swagger-ui:$version")

    api("io.springfox:springfox-boot-starter:$version")

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

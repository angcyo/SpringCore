dependencies {
    api(project(":SpringCore:base"))
    api(project(":SpringCore:log"))
    //api(project(":SpringCore:swagger"))
    api(project(":SpringCore:mysql"))

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

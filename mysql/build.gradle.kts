//apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }

    implementation(project(":SpringCore:base"))
    implementation(project(":SpringCore:log"))
    //implementation(project(":SpringCore:swagger"))

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("mysql:mysql-connector-java")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


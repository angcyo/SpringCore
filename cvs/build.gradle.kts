dependencies {
    implementation(project(":SpringCore:base"))

    //https://github.com/apache/commons-csv
    //https://jcenter.bintray.com/org/apache/commons/commons-csv
    api("org.apache.commons:commons-csv:1.8")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

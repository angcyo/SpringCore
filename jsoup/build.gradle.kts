dependencies {
    implementation(project(":SpringCore:base"))
    api(project(":SpringCore:coroutine"))
    api(project(":SpringCore:http"))

    //https://github.com/jhy/jsoup
    //https://jsoup.org/download
    //https://jcenter.bintray.com/org/jsoup/jsoup/
    api("org.jsoup:jsoup:1.13.1")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

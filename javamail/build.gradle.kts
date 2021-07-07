dependencies {
    implementation(project(":SpringCore:base"))

    //http://www.simplejavamail.org/
    //https://jcenter.bintray.com/org/simplejavamail/simple-java-mail/
    //https://search.maven.org/search?q=g:org.simplejavamail
    //api("org.simplejavamail:simple-java-mail:6.6.1")

    //spring
    //https://www.simplejavamail.org/modules.html#navigation
    api("org.simplejavamail:spring-module:6.6.1")

    //异步处理, 需要使用的库
    //https://jcenter.bintray.com/org/simplejavamail/batch-module/
    //api "org.simplejavamail:batch-module:6.4.4"
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

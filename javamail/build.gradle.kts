dependencies {
    //http://www.simplejavamail.org/
    //https://jcenter.bintray.com/org/simplejavamail/simple-java-mail/
    api("org.simplejavamail:simple-java-mail:6.4.4")

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

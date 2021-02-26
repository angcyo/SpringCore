dependencies {
    implementation(project(":SpringCore:base"))

    //https://github.com/square/okhttp
    //https://jcenter.bintray.com/com/squareup/okhttp3/
    api("com.squareup.okhttp3:okhttp:3.9.1")

    //https://jcenter.bintray.com/com/squareup/okio/
    //api("com.squareup.okio:okio:2.9.0")

    //https://jcenter.bintray.com/com/squareup/retrofit2
    //api("com.squareup.retrofit2:converter-gson:2.9.0")

    //https://github.com/ikidou/TypeBuilder/
    //        maven { url 'https://jitpack.io' }
    api("com.github.ikidou:TypeBuilder:1.0")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

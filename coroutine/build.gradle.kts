dependencies {
    implementation(project(":SpringCore:base"))

    //api "org.jetbrains.kotlin:kotlin-reflect:$_kotlin_version"

    val coroutineVersion = "1.4.2"
    //https://jcenter.bintray.com/org/jetbrains/kotlinx/kotlinx-coroutines-core/
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

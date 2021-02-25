dependencies {
    implementation(project(":SpringCore:base"))
    implementation(project(":SpringCore:log"))
    implementation(project(":SpringCore:swagger"))
    implementation(project(":SpringCore:mysql"))

    //https://jcenter.bintray.com/com/baomidou/mybatis-plus-boot-starter
    //https://mybatis.plus/guide/
    api("com.baomidou:mybatis-plus-boot-starter:3.4.2")

//    runtimeOnly("mysql:mysql-connector-java")
    //https://mvnrepository.com/artifact/com.gitee.sunchenbin.mybatis.actable/mybatis-enhance-actable
    //https://www.yuque.com/sunchenbin/actable/lcbps5
    api("com.gitee.sunchenbin.mybatis.actable:mybatis-enhance-actable:1.4.9.RELEASE") {
        exclude("javax.persistence")
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


dependencies {
    implementation(project(":SpringCore:base"))
    implementation(project(":SpringCore:log"))
    //implementation(project(":SpringCore:swagger"))

    //https://jcenter.bintray.com/com/baomidou/mybatis-plus-boot-starter
    //https://mybatis.plus/guide/
    //https://github.com/baomidou/mybatis-plus
    api("com.baomidou:mybatis-plus-boot-starter:3.4.4-SNAPSHOT")

    //https://github.com/p6spy/p6spy
    //https://mp.baomidou.com/guide/p6spy.html
    api("p6spy:p6spy:3.9.1")

    //mysql 驱动
    runtimeOnly("mysql:mysql-connector-java")

    //https://mvnrepository.com/artifact/com.gitee.sunchenbin.mybatis.actable/mybatis-enhance-actable
    //https://www.yuque.com/sunchenbin/actable/lcbps5
    api("com.gitee.sunchenbin.mybatis.actable:mybatis-enhance-actable:1.4.9.RELEASE")

    //java.lang.NoClassDefFoundError: net/sf/cglib/beans/BeanMap
    //https://github.com/cglib/cglib
    api("cglib:cglib-nodep:3.3.0")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}


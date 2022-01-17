dependencies {

    implementation(project(":SpringCore:base"))
    implementation(project(":SpringCore:util"))

    //https://search.maven.org/artifact/cn.hutool/hutool-all
    api("cn.hutool:hutool-all:5.7.17")

    //https://www.hutool.cn/docs/#/system/Oshi%E5%B0%81%E8%A3%85-OshiUtil
    //基于JNA的操作系统和硬件信息库 https://github.com/oshi/oshi
    //https://mvnrepository.com/artifact/com.github.oshi/oshi-core
    api("com.github.oshi:oshi-core:5.8.3")

    //二维码支持, 改用jar包的形式依赖
    //https://search.maven.org/artifact/com.google.zxing/core
    //api("com.google.zxing:core:3.3.2") //3.4.1 需要android.jar
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
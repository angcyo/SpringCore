dependencies {

    implementation(project(":SpringCore:base"))
    implementation(project(":SpringCore:util"))

    //https://search.maven.org/artifact/cn.hutool/hutool-all
    api("cn.hutool:hutool-all:5.7.17")

    //https://www.hutool.cn/docs/#/poi/%E6%A6%82%E8%BF%B0?id=%e4%bb%8b%e7%bb%8d
    // https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
    api("org.apache.poi:poi-ooxml:5.0.0")

    //https://mvnrepository.com/artifact/xerces/xercesImpl
    api("xerces:xercesImpl:2.12.1")

    //https://www.hutool.cn/docs/#/system/Oshi%E5%B0%81%E8%A3%85-OshiUtil
    //基于JNA的操作系统和硬件信息库 https://github.com/oshi/oshi
    //https://mvnrepository.com/artifact/com.github.oshi/oshi-core
    api("com.github.oshi:oshi-core:5.8.3")

    //二维码支持, 改用jar包的形式依赖
    //https://search.maven.org/artifact/com.google.zxing/core
    //api("com.google.zxing:core:3.3.2") //3.4.1 需要android.jar
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
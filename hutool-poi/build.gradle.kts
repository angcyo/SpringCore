dependencies {

    implementation(project(":SpringCore:base"))
    api(project(":SpringCore:hutool"))

    //https://www.hutool.cn/docs/#/poi/%E6%A6%82%E8%BF%B0?id=%e4%bb%8b%e7%bb%8d
    // https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
    api("org.apache.poi:poi-ooxml:5.0.0")

    //https://mvnrepository.com/artifact/xerces/xercesImpl
    api("xerces:xercesImpl:2.12.1")
}
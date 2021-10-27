dependencies {
    implementation(project(":SpringCore:base"))

    //Apache Batik core module for converting SVG to other formats
    //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-transcoder
    //api("org.apache.xmlgraphics:batik-transcoder:1.14")

    //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-all
    api("org.apache.xmlgraphics:batik-all:1.14")

    //Modules required for PNG and JPEG output
    //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-codec
    runtimeOnly("org.apache.xmlgraphics:batik-codec:1.14")

    /*  //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-svggen
     api("org.apache.xmlgraphics:batik-svggen:1.14")
     //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-util
     api("org.apache.xmlgraphics:batik-util:1.14")
     //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-ext
     api("org.apache.xmlgraphics:batik-ext:1.14")
     //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-dom
     api("org.apache.xmlgraphics:batik-dom:1.14")
     //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-anim
     api("org.apache.xmlgraphics:batik-anim:1.14")
     //https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-parser
     api("org.apache.xmlgraphics:batik-parser:1.14")*/
}
package com.angcyo.batik

import org.apache.batik.dom.GenericDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import org.w3c.dom.DOMImplementation
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.io.OutputStream
import java.io.OutputStreamWriter


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/27
 */

object Batik {

    fun test(out: OutputStream = System.out) {
        val domImpl: DOMImplementation = GenericDOMImplementation.getDOMImplementation()

        val svgNS = "http://www.w3.org/2000/svg"
        val doc = domImpl.createDocument(svgNS, "svg", null)

        val svg = SVGGraphics2D(doc)

        svg.font = Font("微软雅黑", Font.BOLD, 32)
        svg.svgCanvasSize = Dimension(100, 100)

        svg.paint = Color.BLACK
        svg.fillRoundRect(0, 0, 100, 30, 10, 10)
        svg.paint = Color.RED
        svg.drawLine(0, 0, 100, 100)
        svg.drawString("aaa", 0, 0) //文本左下角为原点
        svg.drawString("bbb", 10, 10)
        svg.drawString("ccc", 100, 100)
        svg.paint = Color.red
        svg.drawOval(0, 0, 50, 50)
        svg.paint = Color.blue
        svg.drawOval(0, 0, 100, 50)

        //svg.drawRect()

        //根元素
        /*doc.documentElement.apply {
            //setAttributeNS(null, "width", "50")
            //setAttributeNS(null, "height", "50")
            svg.getRoot(this)
        }*/

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        val useCSS = true // we want to use CSS style attributes
        svg.stream(OutputStreamWriter(out, Charsets.UTF_8), useCSS)
    }
}
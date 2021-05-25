package com.angcyo.cvs

import com.angcyo.spring.util.L
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/02/28
 */
object CVS {

    /**[filePath]需要写入的cvs文件全路径*/
    fun write(filePath: String, header: Array<String>, action: CSVPrinter.() -> Unit) {
        val file = File(filePath)
        val fileOutputStream = FileOutputStream(file, false)
        fileOutputStream.bufferedWriter(Charsets.UTF_8).use {
            val csvFormat = CSVFormat.DEFAULT.withHeader(*header)
            //.withHeader("姓名", "年龄", "家乡");
            val csvPrinter = CSVPrinter(it, csvFormat)
            //csvPrinter.printRecord("张三", 20, "湖北");
            csvPrinter.action()

            L.i("CVS->${file.absolutePath}")
        }
    }

    /**从[filePath]文件中读取cvs数据*/
    fun read(filePath: String, header: Array<String>, action: CSVParser.() -> Unit) {
        val file = File(filePath)
        val fileInputStream = FileInputStream(file)
        fileInputStream.bufferedReader(Charsets.UTF_8).use {
            val csvParser = CSVFormat.EXCEL.withHeader(*header).parse(it)
            /*csvParser.records.forEach {
                //recordNumber
                //record.get(header[0])
            }*/
            csvParser.action()
            csvParser.close()

            L.i("CVS[${csvParser.recordNumber}]->${file.absolutePath}")
        }
    }

}
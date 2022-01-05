package com.angcyo.hutool

import cn.hutool.core.io.FileUtil
import cn.hutool.poi.excel.BigExcelWriter
import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.poi.excel.cell.CellUtil
import cn.hutool.poi.excel.style.StyleUtil
import com.angcyo.spring.util.encode
import org.apache.poi.ss.usermodel.*
import java.io.File
import javax.servlet.http.HttpServletResponse
import kotlin.math.max
import kotlin.math.min

/**
 * https://www.hutool.cn/docs/#/poi/Excel%E7%94%9F%E6%88%90-ExcelWriter
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/12/10
 */
class ExcelHandle(destFilePath: String) {

    /**写入excel*/
    var writer: BigExcelWriter

    /**待写入的数据¬*/
    val dataList = mutableListOf<Any>()

    /**输出的文件路径*/
    var destFilePath: String

    /**需要写入到的响应体*/
    var response: HttpServletResponse? = null

    //对应的excel文件
    val file: File

    init {
        file = FileUtil.file(destFilePath)
        if (file.exists()) {
            file.delete()
        }
        this.destFilePath = file.absolutePath
        writer = ExcelUtil.getBigWriter(destFilePath)

        //跳过当前行，既第一行，非必须，在此演示用
        //writer.passCurrentRow()
    }

    //<editor-fold desc="数据操作">

    /**追加一行的数据*/
    fun appendRow(vararg rowData: Any) {
        val data = rowData.toList()
        dataList.add(data)
    }

    /**立即写入一行的数据*/
    fun writeRow(vararg rowData: Any) {
        val data = rowData.toList()
        writer.writeRow(data)
        //writer.flush()
    }

    /**一列一列的写入数据
     * https://blog.csdn.net/Alone_in_/article/details/100061738*/
    fun appendCell(value: Any?, newRow: Boolean = false, style: CellStyle.() -> Unit = {}) {
        val rowIndex = writer.currentRow

        val row: Row = if (newRow) {
            val nextRowIndex = rowIndex + 1
            writer.currentRow = nextRowIndex
            writer.sheet.createRow(nextRowIndex)
        } else {
            writer.sheet.getRow(rowIndex) ?: writer.sheet.createRow(rowIndex)
        }

        val columnIndex = max(row.lastCellNum + 0, 0)
        val cell = row.createCell(columnIndex)

        val cellStyle = StyleUtil.cloneCellStyle(writer.workbook, writer.cellStyle)
        //cellStyle.setFont()
        cellStyle.style()
        cell.cellStyle = cellStyle

        CellUtil.setCellValue(cell, value)
    }

    /**追加一行数据
     * 一行一行写入
     * [nextRow] 结束之后, 是否切换到下一行*/
    fun appendCellRow(valueList: List<Any?>, nextRow: Boolean = true, style: CellStyle.() -> Unit = {}) {
        if (valueList.isEmpty()) {
            return
        }

        val cellStyle = StyleUtil.cloneCellStyle(writer.workbook, writer.cellStyle)
        cellStyle.style()

        val rowIndex = writer.currentRow
        val row: Row = writer.sheet.getRow(rowIndex) ?: writer.sheet.createRow(rowIndex)

        valueList.forEach {
            val columnIndex = max(row.lastCellNum + 0, 0)
            val cell = row.createCell(columnIndex)
            cell.cellStyle = cellStyle

            CellUtil.setCellValue(cell, it)
        }

        if (nextRow) {
            nextRow()
        }
    }

    /**下一行*/
    fun nextRow() {
        val nextRowIndex = writer.currentRow + 1
        writer.currentRow = nextRowIndex
    }

    //</editor-fold desc="数据操作">

    //<editor-fold desc="样式操作">

    /**定义单元格背景色, 所有类型的颜色
     * [cn.hutool.poi.excel.StyleSet.setBackgroundColor]*/
    fun setBackgroundColor(backgroundColor: IndexedColors, withHeadCell: Boolean = false) {
        val style = writer.styleSet
        // 第二个参数表示是否也设置头部单元格背景
        style.setBackgroundColor(backgroundColor, withHeadCell)
    }

    /**设置Cell的背景颜色, 之后调用[writeRow]就是设置的样式了 */
    fun setCellBackgroundColor(backgroundColor: IndexedColors) {
        val style = writer.styleSet
        StyleUtil.setColor(style.cellStyle, backgroundColor, FillPatternType.SOLID_FOREGROUND)
    }

    /**前景填充颜色*/
    fun setCellForegroundColor(foregroundColor: IndexedColors) {
        val style = writer.styleSet
        //style.cellStyle.fillBackgroundColor = foregroundColor.index
        //style.cellStyle.fillForegroundColor = foregroundColor.index
        StyleUtil.setColor(style.cellStyle, foregroundColor, FillPatternType.SOLID_FOREGROUND)
    }

    fun setFont(font: Font) {
        //第二个参数表示是否忽略头部样式
        writer.styleSet.setFont(font, true)
    }

    fun font(color: Short = Font.COLOR_NORMAL, size: Short = 12, dsl: Font.() -> Unit = {}): Font {
        //设置内容字体
        val font: Font = writer.createFont()
        //font.bold = true
        //font.italic = true
        //font.color = Font.COLOR_RED
        //font.fontHeightInPoints = 14
        font.fontHeightInPoints = size
        font.color = color //IndexedColors.WHITE.index
        font.dsl()
        return font
    }

    /**合并单元格, 第几行的第几列, 到第几行的第几列
     * 索引从0开始*/
    fun merge(
        firstColumn: Int,
        lastColumn: Int,
        firstRow: Int = writer.currentRow,
        lastRow: Int = writer.currentRow,
        value: Any?,
        nextRow: Boolean = value != null, //跳到下一行
        style: CellStyle.() -> Unit = {}
    ) {
        val cellStyle = StyleUtil.cloneCellStyle(writer.workbook, writer.cellStyle)
        cellStyle.style()
        writer.merge(firstRow, lastRow, firstColumn, lastColumn, value, cellStyle)

        // 设置内容后跳到下一行
        if (nextRow) {
            writer.currentRow = writer.currentRow + 1
        }
    }

    /**
     * 设置表格列的宽度
     * https://blog.csdn.net/duqian42707/article/details/51491312
     * [width] 像素, WPS里面显示的是字符数, 1个中文按照2个字符计算
     *
     * The maximum column width for an individual cell is 255 characters.
     * */
    fun setColumnWidth(columnIndex: Int, width: Int) {
        //255*256
        writer.sheet.setColumnWidth(columnIndex, min(256 * width + 184, 255 * 256))
        //writer.sheet.defaultRowHeight = 20
    }

    //</editor-fold desc="样式操作">

    fun doIt() {
        if (dataList.isNotEmpty()) {
            writer.write(dataList)
        }

        val excelWrite = writer
        response?.apply {
            val out = outputStream
            val fileName = file.name.encode() //test.xlsx
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
            setHeader("Content-Disposition", "attachment;filename=${fileName}")
            excelWrite.flush(out, true)
        }
        writer.close()
    }
}

/**默认格式 xlsx.*/
fun excelHandle(destFilePath: String = "temp.xlsx", dsl: ExcelHandle.() -> Unit): ExcelHandle {
    return ExcelHandle(destFilePath).apply {
        dsl()
        doIt()
    }
}


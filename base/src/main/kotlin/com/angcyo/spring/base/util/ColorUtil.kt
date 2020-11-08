package com.angcyo.spring.base.util

import com.angcyo.spring.base.util.ColorUtil.parseColor
import java.awt.Color
import java.util.*
import kotlin.random.Random.Default.nextInt


/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/11/08
 */

object ColorUtil {

    private var sColorNameMap = HashMap<String, Int>()
    val BLACK = -0x1000000
    val DKGRAY = -0xbbbbbc
    val GRAY = -0x777778
    val LTGRAY = -0x333334
    val WHITE = -0x1
    val RED = -0x10000
    val GREEN = -0xff0100
    val BLUE = -0xffff01
    val YELLOW = -0x100
    val CYAN = -0xff0001
    val MAGENTA = -0xff01
    val TRANSPARENT = 0

    init {
        sColorNameMap = HashMap()
        sColorNameMap["black"] = BLACK
        sColorNameMap["darkgray"] = DKGRAY
        sColorNameMap["gray"] = GRAY
        sColorNameMap["lightgray"] = LTGRAY
        sColorNameMap["white"] = WHITE
        sColorNameMap["red"] = RED
        sColorNameMap["green"] = GREEN
        sColorNameMap["blue"] = BLUE
        sColorNameMap["yellow"] = YELLOW
        sColorNameMap["cyan"] = CYAN
        sColorNameMap["magenta"] = MAGENTA
        sColorNameMap["aqua"] = 0xFF00FFFF.toInt()
        sColorNameMap["fuchsia"] = 0xFFFF00FF.toInt()
        sColorNameMap["darkgrey"] = DKGRAY
        sColorNameMap["grey"] = GRAY
        sColorNameMap["lightgrey"] = LTGRAY
        sColorNameMap["lime"] = 0xFF00FF00.toInt()
        sColorNameMap["maroon"] = 0xFF800000.toInt()
        sColorNameMap["navy"] = 0xFF000080.toInt()
        sColorNameMap["olive"] = 0xFF808000.toInt()
        sColorNameMap["purple"] = 0xFF800080.toInt()
        sColorNameMap["silver"] = 0xFFC0C0C0.toInt()
        sColorNameMap["teal"] = 0xFF008080.toInt()

    }

    /**
     * #9090ff
     * */
    fun parseColor(colorString: String): Int {
        if (colorString[0] == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            var color = colorString.substring(1).toLong(16)
            if (colorString.length == 7) {
                // Set the alpha value
                color = color or -0x1000000
            } else require(colorString.length == 9) { "Unknown color" }
            return color.toInt()
        } else {
            val color: Int? = sColorNameMap[colorString.toLowerCase(Locale.ROOT)]
            if (color != null) {
                return color
            }
        }
        throw IllegalArgumentException("Unknown color")
    }
}

fun String.toColorInt() = parseColor(this)

fun randomColor(min: Int, max: Int): Color {
    var f = min
    var b = max

    if (f > 255) {
        f = 255
    }
    if (b > 255) {
        b = 255
    }
    return Color(f + nextInt(b - f),
            f + nextInt(b - f),
            f + nextInt(b - f))
}
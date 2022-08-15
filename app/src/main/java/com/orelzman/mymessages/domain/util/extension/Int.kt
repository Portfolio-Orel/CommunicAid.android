package com.orelzman.mymessages.domain.util.extension

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * @author Orel Zilberman
 * 13/08/2022
 */


/**
 * Returns a string of the number as a short version.
 * 1000 - 1K
 * 10000000 - 1M
 * 100000000000 - 1B
 */

private const val thousand = 1000
private const val million = 1000000
private const val billion = 1000000000

fun Int.toFormattedNumber(): String {
    val float = this.toFloat()
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.DOWN
    return when (this) {
        in thousand until million -> {
           df.format((float / thousand)).toString() + "K"
        }
        in million until billion -> {
            df.format((float / million)).toString() + "M"
        }
        else -> this.toString()
    }
}
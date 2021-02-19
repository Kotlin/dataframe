package org.jetbrains.dataframe

import java.math.RoundingMode

fun Double.round(places: Int): Double {
    require(places >= 0)
    return toBigDecimal().setScale(places, RoundingMode.HALF_UP).toDouble()
}
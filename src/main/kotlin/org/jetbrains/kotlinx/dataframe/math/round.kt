import java.math.RoundingMode

internal fun Double.round(places: Int): Double {
    require(places >= 0)
    return toBigDecimal().setScale(places, RoundingMode.HALF_UP).toDouble()
}

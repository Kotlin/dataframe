import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.reflect.typeOf

/**
 * Assert that we have got the same data that was originally saved on example creation.
 */
internal fun assertEstimations(exampleFrame: AnyFrame) {
    /**
     * In [exampleFrame] we get two concatenated batches. To assert the estimations, we should transform frame row number to batch row number
     */
    fun iBatch(iFrame: Int): Int {
        val firstBatchSize = 100;
        return if (iFrame < firstBatchSize) iFrame else iFrame - firstBatchSize
    }
    val asciiStringCol = exampleFrame["asciiString"] as DataColumn<String?>
    asciiStringCol.type() shouldBe typeOf<String?>()
    asciiStringCol.forEachIndexed { i, element ->
        element shouldBe "Test Example ${iBatch(i)}"
    }

    val utf8StringCol = exampleFrame["utf8String"] as DataColumn<String?>
    utf8StringCol.type() shouldBe typeOf<String?>()
    utf8StringCol.forEachIndexed { i, element ->
        element shouldBe "Тестовый пример ${iBatch(i)}"
    }

    val largeStringCol = exampleFrame["largeString"] as DataColumn<String?>
    largeStringCol.type() shouldBe typeOf<String?>()
    largeStringCol.forEachIndexed { i, element ->
        element shouldBe "Test Example Should Be Large ${iBatch(i)}"
    }

    val booleanCol = exampleFrame["boolean"] as DataColumn<Boolean?>
    booleanCol.type() shouldBe typeOf<Boolean?>()
    booleanCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i) % 2 == 0)
    }

    val byteCol = exampleFrame["byte"] as DataColumn<Byte?>
    byteCol.type() shouldBe typeOf<Byte?>()
    byteCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i) * 10).toByte()
    }

    val shortCol = exampleFrame["short"] as DataColumn<Short?>
    shortCol.type() shouldBe typeOf<Short?>()
    shortCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i) * 1000).toShort()
    }

    val intCol = exampleFrame["int"] as DataColumn<Int?>
    intCol.type() shouldBe typeOf<Int?>()
    intCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i) * 100000000).toInt()
    }

    val longCol = exampleFrame["longInt"] as DataColumn<Long?>
    longCol.type() shouldBe typeOf<Long?>()
    longCol.forEachIndexed { i, element ->
        element shouldBe iBatch(i) * 100000000000000000L
    }

    val unsignedByteCol = exampleFrame["unsigned_byte"] as DataColumn<Short?>
    unsignedByteCol.type() shouldBe typeOf<Short?>()
    unsignedByteCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i) * 10 % (Byte.MIN_VALUE.toShort() * 2).absoluteValue).toShort()
    }

    val unsignedShortCol = exampleFrame["unsigned_short"] as DataColumn<Int?>
    unsignedShortCol.type() shouldBe typeOf<Int?>()
    unsignedShortCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i) * 1000 % (Short.MIN_VALUE.toInt() * 2).absoluteValue)
    }

    val unsignedIntCol = exampleFrame["unsigned_int"] as DataColumn<Long?>
    unsignedIntCol.type() shouldBe typeOf<Long?>()
    unsignedIntCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i).toLong() * 100000000 % (Int.MIN_VALUE.toLong() * 2).absoluteValue)
    }

    val unsignedLongIntCol = exampleFrame["unsigned_longInt"] as DataColumn<BigInteger?>
    unsignedLongIntCol.type() shouldBe typeOf<BigInteger?>()
    unsignedLongIntCol.forEachIndexed { i, element ->
        element shouldBe (iBatch(i).toBigInteger() * 100000000000000000L.toBigInteger() % (Long.MIN_VALUE.toBigInteger() * 2.toBigInteger()).abs())
    }

    val floatCol = exampleFrame["float"] as DataColumn<Float?>
    floatCol.type() shouldBe typeOf<Float?>()
    floatCol.forEachIndexed { i, element ->
        element shouldBe (2.0f.pow(iBatch(i).toFloat()))
    }

    val doubleCol = exampleFrame["double"] as DataColumn<Double?>
    doubleCol.type() shouldBe typeOf<Double?>()
    doubleCol.forEachIndexed { i, element ->
        element shouldBe (2.0.pow(iBatch(i).toDouble()))
    }

    val dateCol = exampleFrame["date32"] as DataColumn<LocalDate?>
    dateCol.type() shouldBe typeOf<LocalDate?>()
    dateCol.forEachIndexed { i, element ->
        element shouldBe LocalDate.ofEpochDay(iBatch(i).toLong() * 30)
    }

    val datetimeCol = exampleFrame["date64"] as DataColumn<LocalDateTime?>
    datetimeCol.type() shouldBe typeOf<LocalDateTime?>()
    datetimeCol.forEachIndexed { i, element ->
        element shouldBe LocalDateTime.ofEpochSecond(iBatch(i).toLong() * 60 * 60 * 24 * 30, 0, ZoneOffset.UTC)
    }

    val timeSecCol = exampleFrame["time32_seconds"] as DataColumn<LocalTime?>
    timeSecCol.type() shouldBe typeOf<LocalTime?>()
    timeSecCol.forEachIndexed { i, element ->
        element shouldBe LocalTime.ofSecondOfDay(iBatch(i).toLong())
    }

    val timeMilliCol = exampleFrame["time32_milli"] as DataColumn<LocalTime?>
    timeMilliCol.type() shouldBe typeOf<LocalTime?>()
    timeMilliCol.forEachIndexed { i, element ->
        element shouldBe LocalTime.ofNanoOfDay(iBatch(i).toLong() * 1000_000)
    }

    val timeMicroCol = exampleFrame["time64_micro"] as DataColumn<LocalTime?>
    timeMicroCol.type() shouldBe typeOf<LocalTime?>()
    timeMicroCol.forEachIndexed { i, element ->
        element shouldBe LocalTime.ofNanoOfDay(iBatch(i).toLong() * 1000)
    }

    val timeNanoCol = exampleFrame["time64_nano"] as DataColumn<LocalTime?>
    timeNanoCol.type() shouldBe typeOf<LocalTime?>()
    timeNanoCol.forEachIndexed { i, element ->
        element shouldBe LocalTime.ofNanoOfDay(iBatch(i).toLong())
    }

}

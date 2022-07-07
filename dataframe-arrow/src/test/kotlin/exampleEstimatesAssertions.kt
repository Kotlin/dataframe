import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
import java.math.BigInteger
import kotlin.math.absoluteValue
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

    val dateCol = exampleFrame["date32"]
    val datetimeCol = exampleFrame["date64"]

    val timeSecCol = exampleFrame["time32_seconds"]
    val timeMilliCol = exampleFrame["time32_milli"]

    val timeMicroCol = exampleFrame["time64_micro"]
    val timeNanoCol = exampleFrame["time64_nano"]
}

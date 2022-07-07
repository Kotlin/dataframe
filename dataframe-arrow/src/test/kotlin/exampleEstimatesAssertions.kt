import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
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

    val utf8StringCol = exampleFrame["utf8String"]
    val largeStringCol = exampleFrame["largeString"]

    val booleanCol = exampleFrame["boolean"]

    val byteCol = exampleFrame["byte"]
    val shortCol = exampleFrame["short"]
    val intCol = exampleFrame["int"]
    val longIntCol = exampleFrame["longInt"]

    val unsignedByteCol = exampleFrame["unsigned_byte"]
    val unsignedShortCol = exampleFrame["unsigned_short"]
    val unsignedIntCol = exampleFrame["unsigned_int"]
    val unsignedLongIntCol = exampleFrame["unsigned_longInt"]

    val dateCol = exampleFrame["date32"]
    val datetimeCol = exampleFrame["date64"]

    val timeSecCol = exampleFrame["time32_seconds"]
    val timeMilliCol = exampleFrame["time32_milli"]

    val timeMicroCol = exampleFrame["time64_micro"]
    val timeNanoCol = exampleFrame["time64_nano"]
}

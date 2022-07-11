import io.kotest.matchers.shouldBe
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readArrowIPC
import org.junit.Test
import java.net.URL
import kotlin.reflect.typeOf

internal class ArrowKtTest {

    fun testResource(resourcePath: String): URL = ArrowKtTest::class.java.classLoader.getResource(resourcePath)!!

    fun testArrowFeather(name: String) = testResource("$name.feather")
    fun testArrowIPC(name: String) = testResource("$name.ipc")

    @Test
    fun testReadingFromFile() {
        val feather = testArrowFeather("data-arrow_2.0.0_uncompressed")
        val df = DataFrame.readArrowFeather(feather)
        val a by listOf("one" as String?).toColumn()
        val b by listOf(2.0 as Double?).toColumn()
        val c by listOf(
            mapOf(
                "c1" to Text("inner"),
                "c2" to 4.0,
                "c3" to 50.0
            ) as Map<String, Any?>?
        ).toColumn()
        val d by listOf("four" as String?).toColumn()
        val expected = dataFrameOf(a, b, c, d)
        df shouldBe expected
    }

    @Test
    fun testReadingAllTypesAsEstimated() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test.arrow")), true, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test.arrow")), true, false)
    }

    @Test
    fun testReadingAllTypesAsEstimatedWithNulls() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-with-nulls.arrow")), true, true)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-with-nulls.arrow")), true, true)
    }

    @Test
    fun testReadingAllTypesAsEstimatedNotNullable() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-not-nullable.arrow")), false, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-not-nullable.arrow")), false, false)
    }

    @Test
    fun testReadingAllTypesAsEstimatedNotNullableWithNulls() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-illegal.arrow")), false, true)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-illegal.arrow")), false, true)
    }
}

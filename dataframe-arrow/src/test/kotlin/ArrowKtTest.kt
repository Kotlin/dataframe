import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readArrowIPC
import org.junit.Test
import java.net.URL

internal class ArrowKtTest {

    fun testResource(resourcePath: String): URL = ArrowKtTest::class.java.classLoader.getResource(resourcePath)!!

    fun testArrowFeather(name: String) = testResource("$name.feather")
    fun testArrowIPC(name: String) = testResource("$name.ipc")

    @Test
    fun testReadingFromFile() {
        val feather = testArrowFeather("data-arrow_2.0.0_uncompressed")
        val df = DataFrame.readArrowFeather(feather)
        val a by columnOf("one")
        val b by columnOf(2.0)
        val c by listOf(
            mapOf(
                "c1" to Text("inner"),
                "c2" to 4.0,
                "c3" to 50.0
            ) as Map<String, Any?>
        ).toColumn()
        val d by columnOf("four")
        val expected = dataFrameOf(a, b, c, d)
        df shouldBe expected
    }

    @Test
    fun testReadingAllTypesAsEstimated() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test.arrow"), NullabilityOptions.Infer), false, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test.arrow"), NullabilityOptions.Infer), false, false)

        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test.arrow"), NullabilityOptions.Checking), true, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test.arrow"), NullabilityOptions.Checking), true, false)

        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test.arrow"), NullabilityOptions.Widening), true, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test.arrow"), NullabilityOptions.Widening), true, false)
    }

    @Test
    fun testReadingAllTypesAsEstimatedWithNulls() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-with-nulls.arrow"), NullabilityOptions.Infer), true, true)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-with-nulls.arrow"), NullabilityOptions.Infer), true, true)

        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-with-nulls.arrow"), NullabilityOptions.Checking), true, true)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-with-nulls.arrow"), NullabilityOptions.Checking), true, true)

        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-with-nulls.arrow"), NullabilityOptions.Widening), true, true)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-with-nulls.arrow"), NullabilityOptions.Widening), true, true)
    }

    @Test
    fun testReadingAllTypesAsEstimatedNotNullable() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-not-nullable.arrow"), NullabilityOptions.Infer), false, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-not-nullable.arrow"), NullabilityOptions.Infer), false, false)

        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-not-nullable.arrow"), NullabilityOptions.Checking), false, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-not-nullable.arrow"), NullabilityOptions.Checking), false, false)

        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-not-nullable.arrow"), NullabilityOptions.Widening), false, false)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-not-nullable.arrow"), NullabilityOptions.Widening), false, false)
    }

    @Test
    fun testReadingAllTypesAsEstimatedNotNullableWithNulls() {
        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-illegal.arrow"), NullabilityOptions.Infer), true, true)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-illegal.arrow"), NullabilityOptions.Infer), true, true)

        shouldThrow<IllegalArgumentException> {
            assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-illegal.arrow"), NullabilityOptions.Checking), false, true)
        }
        shouldThrow<IllegalArgumentException> {
            assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-illegal.arrow"), NullabilityOptions.Checking), false, true)
        }

        assertEstimations(DataFrame.readArrowFeather(testArrowFeather("test-illegal.arrow"), NullabilityOptions.Widening), true, true)
        assertEstimations(DataFrame.readArrowIPC(testArrowIPC("test-illegal.arrow"), NullabilityOptions.Widening), true, true)
    }
}

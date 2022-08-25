import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.io.arrowWriter
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readArrowIPC
import org.junit.Test
import java.io.File
import java.net.URL
import java.time.LocalDate
import kotlin.reflect.typeOf

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

    val cities = dataFrameOf(
        DataColumn.createValueColumn("name", listOf(
            "Berlin",
            "Hamburg",
            "New York",
            "Washington",
            "Saint Petersburg",
            "Vatican"
        )),
        DataColumn.createValueColumn("affiliation", listOf(
            "Germany",
            "Germany",
            "The USA",
            "The USA",
            "Russia",
            null
        )),
        DataColumn.createValueColumn("is_capital", listOf(
            true,
            false,
            false,
            true,
            false,
            null
        )),
        DataColumn.createValueColumn("population", listOf(
            3_769_495,
            1_845_229,
            8_467_513,
            689_545,
            5_377_503,
            825
        )),
        DataColumn.createValueColumn("area", listOf(
            891.7,
            755.22,
            1223.59,
            177.0,
            1439.0,
            0.44
        )),
        DataColumn.createValueColumn("settled", listOf(
            LocalDate.of(1237, 1, 1),
            LocalDate.of(1189, 5, 7),
            LocalDate.of(1624, 1, 1),
            LocalDate.of(1790, 7, 16),
            LocalDate.of(1703, 5, 27),
            LocalDate.of(1929, 2, 11)
        ))
    )

    @Test
    fun testWritingGeneral() {
        fun assertEstimation(citiesDeserialized: DataFrame<*>) {
            citiesDeserialized["name"] shouldBe cities["name"]
            citiesDeserialized["affiliation"] shouldBe cities["affiliation"]
            citiesDeserialized["is_capital"] shouldBe cities["is_capital"]
            citiesDeserialized["population"] shouldBe cities["population"]
            citiesDeserialized["area"] shouldBe cities["area"]
            citiesDeserialized["settled"].type() shouldBe typeOf<LocalDate>() // cities["settled"].type() refers to FlexibleTypeImpl(LocalDate..LocalDate?) and does not match typeOf<LocalDate>()
            citiesDeserialized["settled"].values() shouldBe cities["settled"].values()
        }

        val testFile = File.createTempFile("cities", "arrow")
        cities.arrowWriter().writeArrowFeather(testFile)
        assertEstimation(DataFrame.readArrowFeather(testFile))

        val testByteArray = cities.arrowWriter().saveArrowIPCToByteArray()
        assertEstimation(DataFrame.readArrowIPC(testByteArray))
    }

}

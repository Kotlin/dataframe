package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convertToBoolean
import org.jetbrains.kotlinx.dataframe.api.copy
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.junit.Test
import java.io.File
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
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

    @Test
    fun testWritingGeneral() {
        fun assertEstimation(citiesDeserialized: DataFrame<*>) {
            citiesDeserialized["name"] shouldBe citiesExampleFrame["name"]
            citiesDeserialized["affiliation"] shouldBe citiesExampleFrame["affiliation"]
            citiesDeserialized["is_capital"] shouldBe citiesExampleFrame["is_capital"]
            citiesDeserialized["population"] shouldBe citiesExampleFrame["population"]
            citiesDeserialized["area"] shouldBe citiesExampleFrame["area"]
            citiesDeserialized["settled"].type() shouldBe typeOf<LocalDate>() // cities["settled"].type() refers to FlexibleTypeImpl(LocalDate..LocalDate?) and does not match typeOf<LocalDate>()
            citiesDeserialized["settled"].values() shouldBe citiesExampleFrame["settled"].values()
            citiesDeserialized["page_in_wiki"].type() shouldBe typeOf<String>() // cities["page_in_wiki"].type() is URI, not supported by Arrow directly
            citiesDeserialized["page_in_wiki"].values() shouldBe citiesExampleFrame["page_in_wiki"].values().map { it.toString() }
        }

        val testFile = File.createTempFile("cities", "arrow")
        citiesExampleFrame.writeArrowFeather(testFile)
        assertEstimation(DataFrame.readArrowFeather(testFile))

        val testByteArray = citiesExampleFrame.saveArrowIPCToByteArray()
        assertEstimation(DataFrame.readArrowIPC(testByteArray))
    }

    @Test
    fun testWritingBySchema() {
        val testFile = File.createTempFile("cities", "arrow")
        citiesExampleFrame.arrowWriter(Schema.fromJSON(citiesExampleSchema)).use { it.writeArrowFeather(testFile) }
        val citiesDeserialized = DataFrame.readArrowFeather(testFile, NullabilityOptions.Checking)
        citiesDeserialized["population"].type() shouldBe typeOf<Long?>()
        citiesDeserialized["area"].type() shouldBe typeOf<Float>()
        citiesDeserialized["settled"].type() shouldBe typeOf<LocalDateTime>()
        shouldThrow<IllegalArgumentException> { citiesDeserialized["page_in_wiki"] }
        citiesDeserialized["film_in_youtube"] shouldBe DataColumn.createValueColumn("film_in_youtube", arrayOfNulls<String>(
            citiesExampleFrame.rowsCount()).asList())
    }

    @Test
    fun testWidening() {
        val warnings = ArrayList<ConvertingMismatch>()
        val testRestrictWidening = citiesExampleFrame.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode.STRICT
        ) { warning -> warnings.add(warning) }.use { it.saveArrowFeatherToByteArray() }
        warnings.shouldContain(ConvertingMismatch.WideningMismatch.RejectedColumn("page_in_wiki"))
        shouldThrow<IllegalArgumentException> { DataFrame.readArrowFeather(testRestrictWidening)["page_in_wiki"] }

        val testAllowWidening = citiesExampleFrame.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode(
                restrictWidening = false,
                restrictNarrowing = true,
                strictType = true,
                strictNullable = true
            )
        ).use { it.saveArrowFeatherToByteArray() }
        DataFrame.readArrowFeather(testAllowWidening)["page_in_wiki"].values() shouldBe citiesExampleFrame["page_in_wiki"].values().map { it.toString() }
    }

    @Test
    fun testNarrowing() {
        val frameWithoutRequiredField = citiesExampleFrame.copy().remove("settled")

        frameWithoutRequiredField.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode.STRICT
        ).use {
            shouldThrow<ConvertingException> { it.saveArrowFeatherToByteArray() }
        }

        val warnings = ArrayList<ConvertingMismatch>()
        val testAllowNarrowing = frameWithoutRequiredField.arrowWriter(
            targetSchema = Schema.fromJSON(citiesExampleSchema),
            mode = ArrowWriter.Mode(
                restrictWidening = true,
                restrictNarrowing = false,
                strictType = true,
                strictNullable = true
            )
        ) { warning -> warnings.add(warning) }.use { it.saveArrowFeatherToByteArray() }
        warnings.shouldContain( ConvertingMismatch.NarrowingMismatch.NotPresentedColumnIgnored("settled"))
        shouldThrow<IllegalArgumentException> { DataFrame.readArrowFeather(testAllowNarrowing)["settled"] }
    }

    @Test
    fun testStrictType() {
        val frameRenaming = citiesExampleFrame.copy().remove("settled")
        val frameWithIncompatibleField = frameRenaming.add(frameRenaming["is_capital"].map { value -> value ?: false }.rename("settled").convertToBoolean())

        frameWithIncompatibleField.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode.STRICT
        ).use {
            shouldThrow<ConvertingException> { it.saveArrowFeatherToByteArray() }
        }

        val warnings = ArrayList<ConvertingMismatch>()
        val testLoyalType = frameWithIncompatibleField.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode(
                restrictWidening = true,
                restrictNarrowing = true,
                strictType = false,
                strictNullable = true
            )
        ) { warning -> warnings.add(warning) }.use { it.saveArrowFeatherToByteArray() }
        warnings.map { it.toString() }.shouldContain(
            ConvertingMismatch.TypeConversionNotFound.ConversionNotFoundIgnored("settled", TypeConverterNotFoundException(typeOf<Boolean>(), typeOf<kotlinx.datetime.LocalDateTime?>(), pathOf("settled"))).toString()
        )
        DataFrame.readArrowFeather(testLoyalType)["settled"].type() shouldBe typeOf<Boolean>()
    }

    @Test
    fun testStrictNullable() {
        val frameRenaming = citiesExampleFrame.copy().remove("settled")
        val frameWithNulls = frameRenaming.add(DataColumn.createValueColumn("settled", arrayOfNulls<LocalDate>(frameRenaming.rowsCount()).asList()))

        frameWithNulls.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode.STRICT
        ).use {
            shouldThrow<ConvertingException> { it.saveArrowFeatherToByteArray() }
        }

        val warnings = ArrayList<ConvertingMismatch>()
        val testLoyalNullable = frameWithNulls.arrowWriter(
            Schema.fromJSON(citiesExampleSchema),
            ArrowWriter.Mode(
                restrictWidening = true,
                restrictNarrowing = true,
                strictType = true,
                strictNullable = false
            )
        ) { warning -> warnings.add(warning) }.use { it.saveArrowFeatherToByteArray() }
        warnings.shouldContain(ConvertingMismatch.NullableMismatch.NullValueIgnored("settled", 0))
        DataFrame.readArrowFeather(testLoyalNullable)["settled"].type() shouldBe typeOf<LocalDateTime?>()
        DataFrame.readArrowFeather(testLoyalNullable)["settled"].values() shouldBe arrayOfNulls<LocalDate>(frameRenaming.rowsCount()).asList()
    }
}

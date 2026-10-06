package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertToString
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.replaceAll
import org.jetbrains.kotlinx.dataframe.api.times
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class ReplaceSamples : DataFrameSampleHelper("replace", "api") {

    private val df = peopleDf

    @Test
    fun replaceDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun replaceGroupWithColumn_properties() {
        // SampleStart
        df.replace { name }.with { name.firstName }
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("firstName", "age", "city", "weight", "isHappy")
                it["firstName"].values().take(3) shouldBe listOf("Alice", "Bob", "Charlie")
            }
            .saveDfHtmlSample()
    }

    @Test
    fun replaceGroupWithColumn_strings() {
        // SampleStart
        df.replace("name").with { this[pathOf("name", "firstName")] }
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("firstName", "age", "city", "weight", "isHappy") }
    }

    @Test
    fun replaceKeepName() {
        // SampleStart
        df.replace { colsOf<String?>() }.with { col -> col.map { it?.lowercase() } }
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("name", "age", "city", "weight", "isHappy")
                it["city"].values().take(3) shouldBe listOf("london", "dubai", "moscow")
                // `colsOf` selects top-level columns only, so the names inside `name` stay as they are
                it[pathOf("name", "firstName")][0] shouldBe "Alice"
            }
            .saveDfHtmlSample()
    }

    @Test
    fun replaceRename_properties() {
        // SampleStart
        df.replace { age }.with { 2021 - age named "year" }
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("name", "year", "city", "weight", "isHappy")
                it["year"].values().take(3) shouldBe listOf(2006, 1976, 2001)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun replaceRename_strings() {
        // SampleStart
        df.replace("age").with { 2021 - it.cast<Int>() named "year" }
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("name", "year", "city", "weight", "isHappy") }
    }

    @Test
    fun replaceWithColumns_properties() {
        // SampleStart
        df.replace { weight and age }.with(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
            // SampleEnd
            .also {
                // `weight` is selected first, so it gets the first new column, although `age` comes first in `df`
                it.columnNames() shouldBe listOf("name", "ageInMonths", "city", "weightInGrams", "isHappy")
                it["ageInMonths"].values().take(3) shouldBe listOf(180, 540, 240)
                it["weightInGrams"].values().take(3) shouldBe listOf(54000, 87000, null)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun replaceWithColumns_strings() {
        // SampleStart
        df.replace("weight", "age").with(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("name", "ageInMonths", "city", "weightInGrams", "isHappy") }
    }

    @Test
    fun `KDoc example with a list of new columns`() {
        val newColumns = listOf(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
        df.replace { weight and age }.with(newColumns).columnNames() shouldBe
            listOf("name", "ageInMonths", "city", "weightInGrams", "isHappy")
    }

    // TODO: once the compiler plugin tracks `replace`, the line with `ageAsSeenByThePlugin` stops compiling,
    //  and with it the whole `:samples` test source set. Then remove this test and the sentences
    //  "the compiler plugin, which does not track `replace`" from the `replace { }` and `with { }` KDoc
    //  in `core/.../api/replace.kt`, and "does not track `replace`" from `replace.md`
    @Test
    fun `the compiler plugin does not track replace`() {
        val res = df.replace { age }.with { it.convertToString() }
        // compiles only because the plugin still sees `age` as a column of `Int` values
        val ageAsSeenByThePlugin: DataColumn<Int> = res.age
        ageAsSeenByThePlugin.type() shouldBe typeOf<String>()
    }

    @Test
    fun replaceVsConvertAsColumn_properties() {
        // SampleStart
        df.convert { age }.asColumn { it.convertToString().rename("ageText") }
            // SampleEnd
            .also {
                // unlike `replace`, the name stays "age"
                it.columnNames() shouldBe listOf("name", "age", "city", "weight", "isHappy")
                it["age"].type() shouldBe typeOf<String>()
                df.replace { age }.with { it.convertToString().rename("ageText") }.columnNames() shouldBe
                    listOf("name", "ageText", "city", "weight", "isHappy")
            }
            .saveDfHtmlSample()
    }

    @Test
    fun replaceVsConvertAsColumn_strings() {
        // SampleStart
        df.convert("age").asColumn { it.convertToString().rename("ageText") }
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("name", "age", "city", "weight", "isHappy") }
    }

    @Test
    fun replaceWithFrameColumn_properties() {
        // SampleStart
        val repos = dataFrameOf("name", "contributors")(
            "dataframe", """[{"login": "abc", "contributions": 111}, {"login": "dfg", "contributions": 100}]""",
            "kotlin", """[{"login": "abc", "contributions": 180}, {"login": "dfb", "contributions": 100}]""",
        )

        val reposWithFrames = repos.replace { contributors }.with { it.map { json -> DataFrame.readJsonStr(json) } }

        reposWithFrames.asGroupBy("contributors").max("contributions")
            // SampleEnd
            .also {
                reposWithFrames["contributors"].kind() shouldBe ColumnKind.Frame
                it["contributions"].toList() shouldBe listOf(111, 180)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun replaceWithFrameColumn_strings() {
        // SampleStart
        val repos = dataFrameOf("name", "contributors")(
            "dataframe", """[{"login": "abc", "contributions": 111}, {"login": "dfg", "contributions": 100}]""",
            "kotlin", """[{"login": "abc", "contributions": 180}, {"login": "dfb", "contributions": 100}]""",
        )

        val reposWithFrames = repos.replace("contributors").with {
            it.map { json -> DataFrame.readJsonStr(json as String) }
        }

        reposWithFrames.asGroupBy("contributors").max("contributions")
            // SampleEnd
            .also { it["contributions"].toList() shouldBe listOf(111, 180) }
    }

    @Test
    fun replaceAllValues() {
        // SampleStart
        df.replaceAll("Alice" to "Alicia", "Moscow" to null)
            // SampleEnd
            .also {
                // the default columns include `name.firstName`, inside the column group
                it[pathOf("name", "firstName")].values().take(3) shouldBe listOf("Alicia", "Bob", "Charlie")
                it["city"].values().take(3) shouldBe listOf("London", "Dubai", null)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun replaceAllInColumns_properties() {
        // SampleStart
        df.replaceAll(null to "Unknown", columns = { city })
            // SampleEnd
            .also {
                it["city"][5] shouldBe "Unknown"
                it["city"].type() shouldBe typeOf<String>()
            }
            .saveDfHtmlSample()
        // without `columns`, "Unknown" would go into `weight`, a column of `Int` values with nulls
        shouldThrow<IllegalStateException> { df.replaceAll(null to "Unknown") }.message shouldBe
            "Could not update column 'weight': " +
            "Cannot add a value of class kotlin.String to a column of type kotlin.Int?. Value: 'Unknown'."
    }

    @Test
    fun replaceAllInColumns_strings() {
        // SampleStart
        df.replaceAll(null to "Unknown", columns = { "city"<String?>() })
            // SampleEnd
            .also { it["city"][5] shouldBe "Unknown" }
    }
}

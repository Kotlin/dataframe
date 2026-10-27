package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.NameValuePair
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.api.columnsCount
import org.jetbrains.kotlinx.dataframe.api.contains
import org.jetbrains.kotlinx.dataframe.api.containsKey
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.getValue
import org.jetbrains.kotlinx.dataframe.api.getValueOrNull
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.movingAverage
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.namedValues
import org.jetbrains.kotlinx.dataframe.api.namedValuesOf
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.api.relative
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.api.transpose
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.value
import org.jetbrains.kotlinx.dataframe.api.valuesOf
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test
import kotlin.reflect.typeOf

class DataRowApiSamples : DataFrameSampleHelper("dataRowApi", "api") {
    private val df = peopleDf

    private fun lastNameToColorUpdateCondition(name: String): RgbColor? =
        when (name) {
            "Cooper" -> RgbColor(189, 206, 233)
            else -> null
        }

    private fun lastNameToColorUpdateExpression(name: String): RgbColor? =
        when (name) {
            "Daniels" -> RgbColor(189, 206, 233)
            "Chaplin" -> RgbColor(242, 210, 189)
            "Wolf" -> RgbColor(233, 199, 220)
            else -> null
        }

    private fun lastNameToColorFilter(name: String): RgbColor? =
        when (name) {
            "Wolf" -> RgbColor(242, 210, 189)
            "Smith" -> RgbColor(233, 199, 220)
            else -> null
        }

    @Test
    fun dfDataRow() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun addWithExpression_properties() {
        // SampleStart
        // Row expression computes values for a new column
        df.add("fullName") { name.firstName + " " + name.lastName }
            // SampleEnd
            .defaultHeaderFormatting { fullName }
            .saveDfHtmlSample()
    }

    @Test
    fun addWithExpression_strings() {
        // SampleStart
        // Row expression computes values for a new column
        df.add("fullName") { "name"["firstName"] + " " + "name"["lastName"] }
        // SampleEnd
    }

    @Test
    fun updateWithExpression_properties() {
        // SampleStart
        // "it" refers to the current "weight" cell, and "prev()" is called on the row "this"
        df.update { weight }.at(2, 3, 5).with { it ?: prev()?.weight }
            // SampleEnd
            .format().perRowCol { row, _ ->
                val lastName = df[row.index()].name.lastName
                lastNameToColorUpdateExpression(lastName)?.let { color ->
                    background(color) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }

    @Test
    fun updateWithExpression_strings() {
        // SampleStart
        // "it" refers to the current "weight" cell, and "prev()" is called on the row "this"
        df.update("weight").at(2, 3, 5).with { it ?: prev()?.get("weight") }
        // SampleEnd
    }

    @Test
    fun convertExpression_properties() {
        // SampleStart
        // "it" refers to the current "city" cell
        df.convert { city }.notNull { it.uppercase() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun convertExpression_strings() {
        // SampleStart
        // "it" refers to the current "city" cell
        df.convert("city").notNull { (it as String).uppercase() }
        // SampleEnd
    }

    @Test
    fun pivotWithExpression_properties() {
        // SampleStart
        // Row expression computes cell content for values of pivoted column
        df.pivot { city }.with { name.lastName.uppercase() }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotWithExpression_strings() {
        // SampleStart
        // Row expression computes cell content for values of pivoted column
        df.pivot { city }.with { "name"["lastName"]<String>().uppercase() }
        // SampleEnd
    }

    @Test
    fun filterWithConditionDf() {
        // SampleStart
        df
            // SampleEnd
            .format().perRowCol { row, _ ->
                val lastName = row.name.lastName
                lastNameToColorFilter(lastName)?.let { color ->
                    background(color) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }

    @Test
    fun filterWithCondition_properties() {
        // SampleStart
        // Row filter is used to filter rows
        df.filter { name.firstName == "Alice" && age >= 18 }
            // SampleEnd
            .format().perRowCol { row, _ ->
                val lastName = row.name.lastName
                lastNameToColorFilter(lastName)?.let { color ->
                    background(color) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }

    @Test
    fun filterWithCondition_strings() {
        // SampleStart
        // Row filter is used to filter rows
        df.filter { "name"["firstName"]<String>() == "Alice" && "age"<Int>() >= 18 }
        // SampleEnd
    }

    @Test
    fun dropWithCondition_properties() {
        // SampleStart
        // Row filter is used to drop rows where `city` or `weight` is null
        df.drop { city == null || weight == null }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropWithCondition_strings() {
        // SampleStart
        // Row filter is used to drop rows where `city` or `weight` is null
        df.drop { "city"<String?>() == null || "weight"<Int?>() == null }
        // SampleEnd
    }

    @Test
    fun firstWithCondition_properties() {
        // SampleStart
        // Row filter is used to take the first row where `city` is Milan
        df.first { city == "Milan" }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun firstWithCondition_strings() {
        // SampleStart
        // Row filter is used to take the first row where `city` is Milan
        df.first { "city"<String?>() == "Milan" }
        // SampleEnd
    }

    @Test
    fun countWithCondition_properties() {
        // SampleStart
        // Row filter is used to count happy people
        df.count { isHappy } // the result is 5
        // SampleEnd
    }

    @Test
    fun countWithCondition_strings() {
        // SampleStart
        // Row filter is used to count happy people
        df.count { "isHappy"() } // the result is 5
        // SampleEnd
    }

    @Test
    fun updateWithCondition_properties() {
        // SampleStart
        // Row value filter is used to filter rows for value update
        df.update { age }.where { name.firstName == "Alice" && name.lastName == "Cooper" }.with { 16 }
            // SampleEnd
            .format().perRowCol { row, _ ->
                val lastName = df[row.index()].name.lastName
                lastNameToColorUpdateCondition(lastName)?.let { color ->
                    background(color) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }

    @Test
    fun updateWithCondition_strings() {
        // SampleStart
        // Row value filter is used to filter rows for value update
        df.update("age")
            .where {
                "name"["firstName"]<String>() == "Alice" &&
                    "name"["lastName"]<String>() == "Cooper"
            }
            .with { 16 }
        // SampleEnd
    }

    @Test
    fun gatherWithCondition_properties() {
        // SampleStart
        // Row value filter is used to gather only unfilled profile fields
        df.gather { age and city and weight and isHappy }
            .where { it == null }
            .into("field", "value")
            // SampleEnd
            .defaultHeaderFormatting { "field"<String>() and "value"<String>() }
            .saveDfHtmlSample()
    }

    @Test
    fun gatherWithCondition_strings() {
        // SampleStart
        // Row value filter is used to gather only unfilled profile fields
        df.gather("age", "city", "weight", "isHappy")
            .where { it == null }
            .into("field", "value")
        // SampleEnd
    }

    @Test
    fun formatWithCondition_properties() {
        // SampleStart
        // Row value filter is used to format only rows with minors
        df
            .format()
            .where { age < 18 }
            .with { background(RgbColor(242, 210, 189)) and textColor(black) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun formatWithCondition_strings() {
        // SampleStart
        // Row value filter is used to format only rows with minors
        df
            .format()
            .where { "age"<Int>() < 18 }
            .with { background(RgbColor(242, 210, 189)) and textColor(black) }
        // SampleEnd
    }

    // region KDoc samples

    @Test
    fun getValueAndGetValueOrNull() {
        // SampleStart
        val row = df[0]
        row.getValue<Int>("age") // 15
        row.getValue<Int?>("weight") // 54

        row.getValueOrNull<Int>("height") // null: there is no "height" column
        // row.getValue<Int>("height") would throw an IllegalArgumentException instead
        // SampleEnd
        row.getValue<Int>("age") shouldBe 15
        row.getValue<Int?>("weight") shouldBe 54
        row.getValueOrNull<Int>("age") shouldBe 15
        row.getValueOrNull<Int>("height") shouldBe null
        shouldThrow<IllegalArgumentException> { row.getValue<Int>("height") }
    }

    @Test
    fun valuesOfAndNamedValues() {
        // SampleStart
        val row = df[0] // name: (Alice, Cooper), age: 15, city: London, weight: 54, isHappy: true

        // Only the values, filtered by type
        row.valuesOf<Int>() // [15, 54]

        // All values, paired with their column names
        row.namedValues() // [(name, (Alice, Cooper)), (age, 15), (city, London), (weight, 54), (isHappy, true)]

        // Only the name-value pairs whose value is an Int
        row.namedValuesOf<Int>() // [(age, 15), (weight, 54)]
        // SampleEnd
        row.valuesOf<Int>() shouldBe listOf(15, 54)
        row.namedValues().map { it.name } shouldBe listOf("name", "age", "city", "weight", "isHappy")
        row.namedValues().drop(1).map { it.value } shouldBe listOf(15, "London", 54, true)
        row.namedValuesOf<Int>() shouldBe listOf(NameValuePair("age", 15), NameValuePair("weight", 54))
    }

    @Test
    fun nameValuePairAccessors() {
        // SampleStart
        val transposed = df[0].transpose()
        transposed.name // DataColumn<String>: [name, age, city, weight, isHappy]
        transposed.value // DataColumn<*>: [(Alice, Cooper), 15, London, 54, true]

        transposed[1].name // "age"
        transposed[1].value // 15
        // SampleEnd
        transposed.name.toList() shouldBe listOf("name", "age", "city", "weight", "isHappy")
        transposed.value.toList().drop(1) shouldBe listOf(15, "London", 54, true)
        transposed[1].name shouldBe "age"
        transposed[1].value shouldBe 15
    }

    @Test
    fun containsKeyAndContains() {
        // SampleStart
        val row = df[0]
        row.containsKey("age") // true
        row.containsKey("height") // false

        // `containsKey` only checks top-level columns, a column path can point to a nested one
        row.containsKey("firstName") // false
        pathOf("name", "firstName") in row // true
        // SampleEnd
        row.containsKey("age") shouldBe true
        row.containsKey("height") shouldBe false
        row.containsKey("firstName") shouldBe false
        (pathOf("name", "firstName") in row) shouldBe true
        (pathOf("name", "middleName") in row) shouldBe false
    }

    @Test
    fun columnsMetadata() {
        // SampleStart
        val row = df[0]
        row.columnsCount() // 5
        row.columnNames() // [name, age, city, weight, isHappy]
        row.columnTypes() // [DataRow<*>, Int, String?, Int?, Boolean]

        // A row of a column group describes the columns of that group
        row.name.columnNames() // [firstName, lastName]
        // SampleEnd
        row.columnsCount() shouldBe 5
        row.columnNames() shouldBe listOf("name", "age", "city", "weight", "isHappy")
        row.columnTypes().drop(1) shouldBe listOf(typeOf<Int>(), typeOf<String?>(), typeOf<Int?>(), typeOf<Boolean>())
        row.name.columnNames() shouldBe listOf("firstName", "lastName")
    }

    @Test
    fun relativeRows() {
        // SampleStart
        val row = df[2]
        // The previous, current, and next rows: rows 1, 2, and 3
        row.relative(-1..1)

        // Offsets outside the DataFrame are dropped: rows 2 and 3
        row.relative(listOf(-5, 0, 1))

        // The bounds of a range are coerced to the DataFrame: rows 0, 1, and 2
        row.relative(-5..0)
        // SampleEnd
        row.relative(-1..1) shouldBe df[1..3]
        row.relative(listOf(-5, 0, 1)) shouldBe df[2..3]
        row.relative(-5..0) shouldBe df[0..2]
    }

    @Test
    fun movingAverageOfAge() {
        // SampleStart
        // The average age of the current and the two previous rows.
        // The first two rows have fewer rows to average over: 15.0, 30.0, 26.67, ...
        // `it` is the row being averaged; `this` stays the current row
        df.add("ageMovingAverage") { movingAverage(3) { it.age } }
            // SampleEnd
            .let { it["ageMovingAverage"].toList().take(3) }
            .shouldBe(listOf(15.0, 30.0, (15 + 45 + 20) / 3.0))
    }

    // endregion
}

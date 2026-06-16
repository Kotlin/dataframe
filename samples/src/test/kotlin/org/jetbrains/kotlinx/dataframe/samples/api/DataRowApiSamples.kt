package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

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
        df.format().perRowCol { row, _ ->
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
}

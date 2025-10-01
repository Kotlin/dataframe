package org.jetbrains.kotlinx.dataframe.samples.api.render

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.formatHeader
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class FormatHeaderSamples: DataFrameSampleHelper("format", "api") {
    val df = peopleDf.cast<Person>()

    @DataSchema
    interface Name {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val name: DataRow<Name> // TODO Requires https://code.jetbrains.team/p/kt/repositories/kotlin/reviews/23694 to be merged
        val weight: Int?
        val isHappy: Boolean
    }

    @Test
    fun formatHeader() {
        //SampleStart
        df
            // Format all column headers with bold
            .formatHeader().with { bold }
            // Format the "name" column (including nested) header with red text
            .formatHeader { name }.with { textColor(red) }
            // Override "name"/"lastName" column formating header with blue text
            .formatHeader { name.lastName }.with { textColor(blue) }
            // Format all numeric column headers with underlines
            .formatHeader { colsOf<Number?>() }.with { underline }
        //SampleEnd
            .saveDfHtmlSample()
    }
}

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.fillNaNs
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.takeLast
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.withZero
import org.jetbrains.kotlinx.dataframe.get
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class ColumnSelectorsSamples : DataFrameSampleHelper("columnSelectors", "api") {
    @DataSchema
    interface PersonWithWeight {
        val name: String
        val weight: Double
    }

    private val dfWithNaNs: DataFrame<PersonWithWeight> = dataFrameOf(
        "name",
        "weight",
    )(
        "Alice",
        54.0,
        "Charlie",
        Double.NaN,
    ).cast()

    val df = peopleDf

    @Test
    fun columnSelectorsDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageSelect() {
        // SampleStart
        df.select { age and name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageRemove() {
        // SampleStart
        df.remove { cols { it.hasNulls() } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageGroup() {
        // SampleStart
        df.group { cols { it.data != name } }.into { "nameless" }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageUpdate() {
        // SampleStart
        df.update { city }.notNull { it.lowercase() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageGather() {
        // SampleStart
        df.gather { colsOf<Number>() }.into("key", "value")
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageMove() {
        // SampleStart
        df.move { name.firstName and name.lastName }.after { city }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageFillNaNsDf() {
        // SampleStart
        dfWithNaNs
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUsageFillNaNs() {
        // SampleStart
        dfWithNaNs.fillNaNs { colsAtAnyDepth().colsOf<Double>() }.withZero()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsByColumnNameIt_properties() {
        // SampleStart
        // by column name
        df.select { it.name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsByColumnNameIt_strings() {
        // SampleStart
        // by column name
        df.select { it["name"] }
        // SampleEnd
    }

    @Test
    fun columnSelectorsByColumnName() {
        // SampleStart
        // by column name
        df.select { name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsByColumnPath_properties() {
        // SampleStart
        // by column path
        df.select { name.firstName }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsByColumnPath_strings() {
        // SampleStart
        // by column path
        df.select { it["name"]["firstName"] } // same as df.select { "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun columnSelectorsWithNewName_properties() {
        // SampleStart
        // with a new name
        df.select { name named "Full Name" }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsWithNewName_strings() {
        // SampleStart
        // with a new name
        df.select { "name" named "Full Name" }
        // SampleEnd
    }

    @Test
    fun columnSelectorsConverted_properties() {
        // SampleStart
        // converted
        df.select { name.firstName.map { it.lowercase() } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsConverted_strings() {
        // SampleStart
        // converted
        df.select { "name"["firstName"]<String>().map { it.lowercase() } }
        // SampleEnd
    }

    @Test
    fun columnSelectorsArithmetic_properties() {
        // SampleStart
        // column arithmetics
        df.select { 2021 - age }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsArithmetic_strings() {
        // SampleStart
        // column arithmetics
        df.select { 2021 - "age"<Int>() }
        // SampleEnd
    }

    @Test
    fun columnSelectorsTwoColumns_properties() {
        // SampleStart
        // two columns
        df.select { name and age }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsTwoColumns_strings() {
        // SampleStart
        // two columns
        df.select { "name" and "age" }
        // SampleEnd
    }

    @Test
    fun columnSelectorsColumnRange_properties() {
        // SampleStart
        // range of columns
        df.select { name..age }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsColumnRange_strings() {
        // SampleStart
        // range of columns
        df.select { "name".."age" }
        // SampleEnd
    }

    @Test
    fun columnSelectorsAllColumnsOfGroup_properties() {
        // SampleStart
        // all columns of ColumnGroup
        df.select { name.allCols() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAllColumnsOfGroup_strings() {
        // SampleStart
        // all columns of ColumnGroup
        df.select { "name".allCols() }
        // SampleEnd
    }

    @Test
    fun columnSelectorsAtAnyDepthFromGroup_properties() {
        // SampleStart
        // traversal of columns at any depth from here excluding ColumnGroups
        df.select { name.colsAtAnyDepth().filter { !it.isColumnGroup() } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAtAnyDepthFromGroup_strings() {
        // SampleStart
        // traversal of columns at any depth from here excluding ColumnGroups
        df.select { "name".colsAtAnyDepth().filter { !it.isColumnGroup() } }
        // SampleEnd
    }

    @Test
    fun columnsSelectorByIndex() {
        // SampleStart
        // by index
        df.select { col(2) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnsSelectorBySeveralIndices() {
        // SampleStart
        // by several indices
        df.select { cols(0, 1, 3) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnsSelectorByIndexRange() {
        // SampleStart
        // by range of indices
        df.select { cols(1..4) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsByCondition() {
        // SampleStart
        // by condition
        df.select { cols { it.name().startsWith("a") } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsByName() {
        // SampleStart
        // by condition
        df.select { nameStartsWith("a") }
        // SampleEnd
    }

    @Test
    fun columnSelectorsByType() {
        // SampleStart
        // by type
        df.select { colsOf<Int?>() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsByTypeWithCondition() {
        // SampleStart
        // by type with condition
        df.select { colsOf<Int?> { it.hasNulls() } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAllTopLevel() {
        // SampleStart
        // all top-level columns
        df.select { all() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsTake() {
        // SampleStart
        // first n columns
        df.select { take(2) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsTakeLast() {
        // SampleStart
        // last n columns
        df.select { takeLast(2) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsDrop() {
        // SampleStart
        // all except first n columns
        df.select { drop(2) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsDropLast() {
        // SampleStart
        // all except last n columns
        df.select { dropLast(2) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsFirst() {
        // SampleStart
        // find the first column satisfying the condition
        df.select { first { it.hasNulls() } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsLastInGroup() {
        // SampleStart
        // find the last column inside a column group satisfying the condition
        df.select {
            colGroup("name").lastCol { it.name().endsWith("Name") }
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAtAnyDepthExcludingGroups() {
        // SampleStart
        // traversal of columns at any depth from here excluding ColumnGroups
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAtAnyDepthIncludingGroups() {
        // SampleStart
        // traversal of columns at any depth from here including ColumnGroups
        df.select { colsAtAnyDepth() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAtAnyDepthWithCondition() {
        // SampleStart
        // traversal of columns at any depth with condition
        df.select { colsAtAnyDepth().filter { it.name().contains("y") } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAtAnyDepthByType() {
        // SampleStart
        // traversal of columns at any depth to find columns of given type
        df.select { colsAtAnyDepth().colsOf<String>() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsAllExcept() {
        // SampleStart
        // all columns except given column set
        df.select { allExcept { colsOf<String>() } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsUnion() {
        // SampleStart
        // union of column sets
        df.select { take(2) and col(3) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsModifyTake() {
        // SampleStart
        // first n value- and frame columns in column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.take(3) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsModifyTakeLast() {
        // SampleStart
        // last n value- and frame columns in column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.takeLast(3) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsModifyDrop() {
        // SampleStart
        // all except first n value- and frame columns in column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.drop(3) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsModifyDropLast() {
        // SampleStart
        // all except last n value- and frame columns in column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.dropLast(3) }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsModifyFilter() {
        // SampleStart
        // filter column set by condition
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() && it.name().startsWith("age") } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsModifyExcept() {
        // SampleStart
        // exclude columns from column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.except { age } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnSelectorsModifyDistinct() {
        // SampleStart
        // keep only unique columns
        df.select { (colsOf<Int>() and age).distinct() }
            // SampleEnd
            .saveDfHtmlSample()
    }
}

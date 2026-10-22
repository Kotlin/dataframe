package org.jetbrains.kotlinx.dataframe.samples.api.column

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asFrameColumn
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.api.isType
import org.jetbrains.kotlinx.dataframe.api.isValueColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import kotlin.reflect.typeOf

class DataColumnTypeSamples : DataFrameSampleHelper("dataColumnType", "api") {

    private val df = peopleDf

    @Test
    fun isColumnGroup() {
        // SampleStart
        val name: AnyCol = df["name"]
        if (name.isColumnGroup()) {
            // `name` is smart-cast to `ColumnGroup<*>` here
            name.columnNames() // returns [firstName, lastName]
        }
        // SampleEnd
        name.isColumnGroup() shouldBe true
        name.asColumnGroup().columnNames() shouldBe listOf("firstName", "lastName")
        df["age"].isColumnGroup() shouldBe false
    }

    @Test
    fun isFrameColumn() {
        // SampleStart
        val group: AnyCol = df.groupBy("city").toDataFrame()["group"]
        if (group.isFrameColumn()) {
            // `group` is smart-cast to `FrameColumn<*>` here
            group.schema.value.columns.keys // returns [name, age, city, weight, isHappy]
        }
        // SampleEnd
        group.isFrameColumn() shouldBe true
        group.asFrameColumn().schema.value.columns.keys shouldBe setOf("name", "age", "city", "weight", "isHappy")
        df["name"].isFrameColumn() shouldBe false
    }

    @Test
    fun isValueColumn() {
        // SampleStart
        val age: AnyCol = df["age"]
        if (age.isValueColumn()) {
            // `age` is smart-cast to `ValueColumn<*>` here
            age.distinct() // returns a `ValueColumn` with 15, 45, 20, 40, 30, 18
        }
        // SampleEnd
        age.isValueColumn() shouldBe true
        (age.distinct() is ValueColumn<*>) shouldBe true
        age.distinct().toList() shouldBe listOf(15, 45, 20, 40, 30, 18)
        df["name"].isValueColumn() shouldBe false
    }

    @Test
    fun isSubtypeOfKType() {
        // SampleStart
        // `age: Int`
        df["age"].isSubtypeOf(typeOf<Number>()) // true
        df["age"].isSubtypeOf(typeOf<Int?>()) // true
        df["age"].isSubtypeOf(typeOf<String>()) // false

        // `weight: Int?`
        df["weight"].isSubtypeOf(typeOf<Int>()) // false
        df["weight"].isSubtypeOf(typeOf<Number?>()) // true
        // SampleEnd
        df["age"].isSubtypeOf(typeOf<Number>()) shouldBe true
        df["age"].isSubtypeOf(typeOf<Int?>()) shouldBe true
        df["age"].isSubtypeOf(typeOf<String>()) shouldBe false
        df["weight"].isSubtypeOf(typeOf<Int>()) shouldBe false
        df["weight"].isSubtypeOf(typeOf<Number?>()) shouldBe true
    }

    @Test
    fun isSubtypeOfReified() {
        // SampleStart
        // `age: Int`
        df["age"].isSubtypeOf<Number>() // true
        df["age"].isSubtypeOf<Int?>() // true
        df["age"].isSubtypeOf<String>() // false

        // `weight: Int?`
        df["weight"].isSubtypeOf<Int>() // false
        df["weight"].isSubtypeOf<Number?>() // true
        // SampleEnd
        df["age"].isSubtypeOf<Number>() shouldBe true
        df["age"].isSubtypeOf<Int?>() shouldBe true
        df["age"].isSubtypeOf<String>() shouldBe false
        df["weight"].isSubtypeOf<Int>() shouldBe false
        df["weight"].isSubtypeOf<Number?>() shouldBe true
    }

    @Test
    fun isType() {
        // SampleStart
        // `age: Int`
        df["age"].isType<Int>() // true
        df["age"].isType<Int?>() // false
        df["age"].isType<Number>() // false
        // SampleEnd
        df["age"].isType<Int>() shouldBe true
        df["age"].isType<Int?>() shouldBe false
        df["age"].isType<Number>() shouldBe false
    }

    @Test
    fun isList() {
        // SampleStart
        val ages = df.groupBy("city").aggregate { this["age"].toList() into "ages" }

        ages["ages"].isList() // true, `ages` holds lists of ages
        ages["city"].isList() // false
        // SampleEnd
        ages["ages"].isList() shouldBe true
        ages["city"].isList() shouldBe false
    }
}

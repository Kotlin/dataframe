package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class ColsOfKindTests : ColumnsSelectionDslTests() {

    @Test
    fun `colsOfKind exceptions`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".colsOfKind(Value) }
        }
    }

    @Test
    fun `colsOfKind at top-level`() {
        listOf(
            df.select { all() },
            df.select { colsOfKind(Value, Group) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, city, weight, isHappy) },
            df.select { all().colsOfKind(Value) },
            df.select { colsOfKind(Value) },
        ).shouldAllBeEqual()

        listOf(
            df.select { age },
            df.select { age }.select { colsOfKind(Value) },
            df.select { age }.select { colsOfKind(Value, Value) },
            df.select { age }.select { colsOfKind(Value).all() },
            df.select { age }.select { all().colsOfKind(Value) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, weight) },
            df.select { colsOfKind(Value, Frame) { "e" in it.name() } },
            df.select { all().colsOfKind(Value, Frame) { "e" in it.name() } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `colsOfKind at lower level`() {
        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.colsOfKind(Value) { "Name" in it.name() } },
            df.select { name.colsOf<String>().colsOfKind(Value) { "Name" in it.name() } },
            df.select { "name".colsOfKind(Value) { "Name" in it.name() } },
            df.select { Person::name.colsOfKind(Value) { "Name" in it.name() } },
            df.select { pathOf("name").colsOfKind(Value) { "Name" in it.name() } },
            df.select { it["name"].asColumnGroup().colsOfKind(Value) { "Name" in it.name() } },
        ).shouldAllBeEqual()
    }
}

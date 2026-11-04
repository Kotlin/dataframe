package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test
import kotlin.reflect.typeOf

private class AccessorPerson(val name: String, val age: Int)

private class ColumnNameHolder {
    @ColumnName("custom")
    val original by column<String>()
}

/**
 * Tests for the column reference factories (`column`, `valueColumn`, `columnGroup`, `frameColumn`):
 * property-name delegates and [ColumnName] overrides, access by name and by path, typed and untyped
 * overloads, nested references via a receiver, the deprecated [KProperty] overload, computed column
 * references, and usage in `select`, `add` and `update`.
 */
@Suppress("ktlint:standard:argument-list-wrapping")
class ColumnAccessorsTests {

    @Test
    fun `column delegate takes name from property`() {
        val name by column<String>()

        name.name() shouldBe "name"
        name.path() shouldBe pathOf("name")
    }

    @Test
    fun `column delegate respects ColumnName annotation`() {
        val accessor = ColumnNameHolder().original

        accessor.name() shouldBe "custom"
    }

    @Test
    fun `column delegate builds path from parent group`() {
        val group = columnGroup("g")
        val x by group.column<String>()

        x.path() shouldBe pathOf("g", "x")
    }

    @Test
    fun `columnGroup delegate takes name from property`() {
        val group: ColumnAccessor<DataRow<*>> by columnGroup()

        group.name() shouldBe "group"
    }

    @Test
    fun `column accessor by name and by path`() {
        val byName = column<String>("a")
        val byPath = column<String>(pathOf("a", "b"))

        byName.name() shouldBe "a"
        byPath.path() shouldBe pathOf("a", "b")
    }

    @Test
    fun `valueColumn without type argument is Any accessor`() {
        val accessor: ColumnAccessor<Any?> = valueColumn("v")

        accessor.name() shouldBe "v"
    }

    @Test
    fun `typed columnGroup and frameColumn accessors`() {
        val group: ColumnAccessor<DataRow<Unit>> = columnGroup<Unit>("g")
        val frame: ColumnAccessor<DataFrame<Unit>> = frameColumn<Unit>("f")

        group.name() shouldBe "g"
        frame.name() shouldBe "f"
    }

    @Test
    fun `untyped columnGroup and frameColumn accessors are star-typed`() {
        val group: ColumnAccessor<DataRow<*>> = columnGroup("g")
        val frame: ColumnAccessor<DataFrame<*>> = frameColumn("f")

        group.name() shouldBe "g"
        frame.name() shouldBe "f"
    }

    @Test
    fun `deprecated column accessor by property takes property name`() {
        @Suppress("DEPRECATION")
        val accessor = column(AccessorPerson::name)

        accessor.name() shouldBe "name"
    }

    @Test
    fun `computed column reference works in select and add`() {
        val df = dataFrameOf("age")(20, 15)
        val isAdult = column<Boolean>("isAdult") { (this["age"] as Int) > 18 }

        val selected = df.select { isAdult }
        selected.columnNames() shouldBe listOf("isAdult")
        selected["isAdult"].values shouldBe listOf(true, false)
        selected["isAdult"].type shouldBe typeOf<Boolean>()

        val added = df.add { add(isAdult) }
        added.columnNames() shouldBe listOf("age", "isAdult")
        added["isAdult"].values shouldBe listOf(true, false)
    }

    @Test
    fun `column delegate named overrides the name`() {
        val renamed = column<String>() named "other"

        renamed.name() shouldBe "other"
        renamed.path() shouldBe pathOf("other")
    }

    @Test
    fun `receiver accessors concatenate paths`() {
        val group = columnGroup("g")
        val byName = group.column<String>("x")
        val byPath = group.column<String>(pathOf("x", "y"))

        byName.path() shouldBe pathOf("g", "x")
        byPath.path() shouldBe pathOf("g", "x", "y")
    }

    @Test
    fun `column accessor updates values in update`() {
        val age = column<Int>("age")
        val df = dataFrameOf("age", "city")(20, "London", 15, "Milan")

        val updated = df.update { age }.with { it + 1 }

        updated["age"].values shouldBe listOf(21, 16)
    }

    @Test
    fun `computed column reference with df overload works with any frame`() {
        val df = dataFrameOf("age")(20, 15)
        val isAdult = column(df, "isAdult") { (this["age"] as Int) > 18 }

        val other = dataFrameOf("age")(30, 10)
        val addedToOther = other.add { add(isAdult) }

        addedToOther["isAdult"].values shouldBe listOf(true, false)
    }
}

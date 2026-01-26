package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class RenameTests : ColumnsSelectionDslTests() {

    companion object {
        val simpleDf = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            4, 5, 6,
        )
        val groupedDf = simpleDf.group { "a" and "b" }.into("group")

        val doubleGroupedDf = groupedDf.group { "group"["a"] }.into { "group"["aGroup"] }
    }

    @Test
    fun `simple rename`() {
        val renamedDf = dataFrameOf("a_renamed", "b_renamed", "c_renamed")(
            1, 2, 3,
            4, 5, 6,
        )

        simpleDf.rename { all() }.into { it.name + "_renamed" } shouldBe renamedDf
        simpleDf.rename { all() }.into("a_renamed", "b_renamed", "c_renamed") shouldBe renamedDf
    }

    @Test
    fun `test rename with String to String pairs`() {
        val renamedDf = dataFrameOf("a_renamed", "b_renamed", "c_renamed")(
            1, 2, 3,
            4, 5, 6,
        )

        simpleDf.rename(
            "c" to "c_renamed",
            "a" to "a_renamed",
            "b" to "b_renamed",
        ) shouldBe renamedDf
    }

    @Test
    fun `partial grouped rename`() {
        val renamedDf = dataFrameOf("a_renamed", "b", "c")(
            1, 2, 3,
            4, 5, 6,
        ).group { "a_renamed" and "b" }.into("group_renamed")

        groupedDf
            .rename { "group" and "group"["a"] }
            .into { it.name + "_renamed" } shouldBe renamedDf
    }

    @Test
    fun `grouped rename`() {
        val renamedDf = dataFrameOf("a_renamed", "b_renamed", "c_renamed")(
            1, 2, 3,
            4, 5, 6,
        ).group { "a_renamed" and "b_renamed" }.into("group_renamed")

        groupedDf
            .rename { colsAtAnyDepth() }
            .into { it.name + "_renamed" } shouldBe renamedDf
    }

    @Test
    fun `double grouped rename in 3 steps`() {
        val renamedDf = dataFrameOf("a_renamed", "b_renamed", "c_renamed")(
            1, 2, 3,
            4, 5, 6,
        )
            .group { "a_renamed" and "b_renamed" }.into("group_renamed")
            .group { "group_renamed"["a_renamed"] }.into { "group_renamed"["aGroup_renamed"] }

        doubleGroupedDf
            .rename { colsAtAnyDepth() }
            .into { it.name + "_renamed" } shouldBe renamedDf
    }

    interface Person2 {
        val age2: Int
    }

    @Test
    fun `selection dsl`() {
        val age2 by column<Int>()
        val dfRenamed = df.rename { age }.into(age2)

        listOf(
            dfRenamed.select { age2 },
            df.select { expr { age } named "age2" },
            df.select { expr { age } into "age2" },
            df.select { expr { age } named age2 },
            df.select { expr { age } into age2 },
            df.select { expr { age } named Person2::age2 },
            df.select { expr { age } into Person2::age2 },
            df.select { expr { age } named pathOf("age2") },
            df.select { expr { age } into pathOf("age2") },
            df.select { expr { age } named col("age2") },
            df.select { expr { age } into col("age2") },
            df.select { age named "age2" },
            df.select { age into "age2" },
            df.select { age named age2 },
            df.select { age into age2 },
            df.select { age named Person2::age2 },
            df.select { age into Person2::age2 },
            df.select { age named pathOf("age2") },
            df.select { age into pathOf("age2") },
            df.select { age named col("age2") },
            df.select { age into col("age2") },
            df.select { "age" named "age2" },
            df.select { "age" into "age2" },
            df.select { "age" named age2 },
            df.select { "age" into age2 },
            df.select { "age" named Person2::age2 },
            df.select { "age" into Person2::age2 },
            df.select { "age" named pathOf("age2") },
            df.select { "age" into pathOf("age2") },
            df.select { "age" named col("age2") },
            df.select { "age" into col("age2") },
            df.select { Person::age named "age2" },
            df.select { Person::age into "age2" },
            df.select { Person::age named age2 },
            df.select { Person::age into age2 },
            df.select { Person::age named Person2::age2 },
            df.select { Person::age into Person2::age2 },
            df.select { Person::age named pathOf("age2") },
            df.select { Person::age into pathOf("age2") },
            df.select { Person::age named col("age2") },
            df.select { Person::age into col("age2") },
            df.select { pathOf("age") named "age2" },
            df.select { pathOf("age") into "age2" },
            df.select { pathOf("age") named age2 },
            df.select { pathOf("age") into age2 },
            df.select { pathOf("age") named Person2::age2 },
            df.select { pathOf("age") into Person2::age2 },
            df.select { pathOf("age") named pathOf("age2") },
            df.select { pathOf("age") into pathOf("age2") },
            df.select { pathOf("age") named col("age2") },
            df.select { pathOf("age") into col("age2") },
            df.select { col("age") named "age2" },
            df.select { col("age") into "age2" },
            df.select { col("age") named age2 },
            df.select { col("age") into age2 },
            df.select { col("age") named Person2::age2 },
            df.select { col("age") into Person2::age2 },
            df.select { col("age") named pathOf("age2") },
            df.select { col("age") into pathOf("age2") },
            df.select { col("age") named col("age2") },
            df.select { col("age") into col("age2") },
        ).shouldAllBeEqual()
    }

    @Test
    fun `col by index named`() {
        val df = dataFrameOf(
            "col0" to columnOf(1, 4),
            "col1" to columnOf(2, 5),
            "col2" to columnOf(3, 6),
        )

        listOf(
            df.select { col(0) and (col(1) named "renamed") and col(2) },
            df.select { col(0) and (col(1) into "renamed") and col(2) },
            df.select { col(0) and (col<Int>(1) named "renamed") and col(2) },
            df.select { col(0) and (col<Int>(1) into "renamed") and col(2) },
        ).shouldAllBeEqual()

        val result = df.select { col(0) and (col(1) named "renamed") and col(2) }
        result.columnNames() shouldBe listOf("col0", "renamed", "col2")
        result["renamed"].toList() shouldBe listOf(2, 5)
    }

    @Test
    fun `col by index named with convert`() {
        val df = dataFrameOf(
            "a" to columnOf(1, 4),
            "b" to columnOf(2, 5),
            "c" to columnOf(3, 6),
        )

        val result = df.convert { col<Int>(0) named "newA" }.with { it * 10 }

        result.columnNames() shouldBe listOf("newA", "b", "c")
        result["newA"].toList() shouldBe listOf(10, 40)
    }
}

class RenameToCamelCaseTests {
    companion object {
        val nestedDf = dataFrameOf("test_name")(dataFrameOf("another_name")(1))
        val nestedColumnGroup = dataFrameOf("test_name")(
            dataFrameOf("another_name")(1).first(),
        )
        val doublyNestedColumnGroup = dataFrameOf("test_name")(
            dataFrameOf("another_name")(
                dataFrameOf("third_name")(1).first(),
            ).first(),
        )
        val deeplyNestedDf = kotlin.run {
            val df = dataFrameOf("another_name")(1)
            val rowWithDf = dataFrameOf("group_name")(df).first()
            dataFrameOf("test_name")(rowWithDf)
        }
        val deeplyNestedFrameColumn = kotlin.run {
            val df = dataFrameOf("col_0")(1)
            val df1 = dataFrameOf("col_1")(df)
            dataFrameOf("col_2")(df1)
        }
    }

    @Test
    fun `nested df`() {
        nestedDf.renameToCamelCase() shouldBe dataFrameOf("testName")(dataFrameOf("anotherName")(1))
    }

    @Test
    fun `nested row`() {
        val df = nestedColumnGroup.renameToCamelCase()
        df.columnNames() shouldBe listOf("testName")
        df.getColumnGroup("testName").columnNames() shouldBe listOf("anotherName")
    }

    @Test
    fun `uppercase names`() {
        val originalDf = dataFrameOf("ID", "ITEM", "ORDER_DATE")(1, "TOY", "02.03.2009")
        val renamedDf = originalDf.renameToCamelCase()
        renamedDf.columnNames() shouldBe listOf("id", "item", "orderDate")
    }

    @Test
    fun `doubly nested row`() {
        val doublyNestedColumnGroup = dataFrameOf("test_name")(
            dataFrameOf("another_name")(
                dataFrameOf("third_name")(1).first(),
            ).first(),
        )

        val df = doublyNestedColumnGroup.renameToCamelCase()
        df.columnNames() shouldBe listOf("testName")
        df["testName"].asColumnGroup().columnNames() shouldBe listOf("anotherName")
        df["testName"]["anotherName"].asColumnGroup().columnNames() shouldBe listOf("thirdName")
    }

    @Test
    fun `deeply nested df`() {
        val df = deeplyNestedDf.renameToCamelCase()
        df.schema().asClue {
            df.columnNames() shouldBe listOf("testName")
            df.getColumnGroup("testName").columnNames() shouldBe listOf("groupName")
            df["testName"]["groupName"].asAnyFrameColumn()[0].columnNames() shouldBe listOf("anotherName")
        }
    }

    @Test
    fun `deeply nested frame column`() {
        val df = deeplyNestedFrameColumn.renameToCamelCase()
        df.schema().asClue {
            shouldNotThrowAny {
                df["col2"].asAnyFrameColumn()
                    .firstOrNull()!!["col1"].asAnyFrameColumn()
                    .firstOrNull()!!["col0"]
            }
        }
    }

    @Test
    fun `rename to camelCase`() {
        val dfWithUpperCaseColumnNames = dataFrameOf("First_Column", "second_column", "ThirdColumn")(1, 2, 3)
        val df = dfWithUpperCaseColumnNames.renameToCamelCase()
        df.columnNames() shouldBe listOf("firstColumn", "secondColumn", "thirdColumn")
    }
}

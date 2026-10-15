package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.junit.Test

class RenameAffixTests {
    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Bob"),
        "age" to columnOf(20, 30),
        "score" to columnOf(1.5, null),
    )

    @Test
    fun `prefix preserves values types and order`() {
        val result = df.rename { all() }.addPrefix("person_")

        result.columnNames() shouldBe listOf("person_name", "person_age", "person_score")
        result.columnTypes() shouldBe df.columnTypes()
        result.columns().map { it.toList() } shouldBe df.columns().map { it.toList() }
        df.columnNames() shouldBe listOf("name", "age", "score")
    }

    @Test
    fun `suffix renames only selected columns`() {
        val result = df.rename("score", "name").addSuffix("_original")

        result.columnNames() shouldBe listOf("name_original", "age", "score_original")
        result.columnTypes() shouldBe df.columnTypes()
        result.columns().map { it.toList() } shouldBe df.columns().map { it.toList() }
    }

    @Test
    fun `empty affixes leave columns unchanged`() {
        df.rename { all() }.addPrefix("") shouldBe df
        df.rename { all() }.addSuffix("") shouldBe df
    }

    @Test
    fun `unicode affixes`() {
        df.rename("name").addPrefix("பெயர்_").columnNames() shouldBe listOf("பெயர்_name", "age", "score")
        df.rename("age").addSuffix("_年").columnNames() shouldBe listOf("name", "age_年", "score")
    }

    @Test
    fun `nested parent and child selections`() {
        val grouped = df.group { "name" and "age" }.into("person")
        val prefixed = grouped.rename { "person" and "person"["name"] }.addPrefix("new_")
        val suffixed = grouped.rename { "person" and "person"["age"] }.addSuffix("_old")

        prefixed shouldBe df.rename("name").addPrefix("new_")
            .group { "new_name" and "age" }.into("new_person")
        suffixed shouldBe df.rename("age").addSuffix("_old")
            .group { "name" and "age_old" }.into("person_old")
    }

    @Test
    fun `renaming a group does not implicitly rename children`() {
        val grouped = df.group { "name" and "age" }.into("person")
        val result = grouped.rename("person").addPrefix("new_")

        result.getColumnGroup("new_person").columnNames() shouldBe listOf("name", "age")
        result.getColumnGroup("new_person").columnTypes() shouldBe grouped.getColumnGroup("person").columnTypes()
    }

    @Test
    fun `collisions use existing rename exception`() {
        val original = dataFrameOf("a" to columnOf(1), "pre_a" to columnOf(2), "a_post" to columnOf(3))

        shouldThrow<DuplicateColumnNamesException> { original.rename("a").addPrefix("pre_") }
        shouldThrow<DuplicateColumnNamesException> { original.rename("a").addSuffix("_post") }
        original.columnNames() shouldBe listOf("a", "pre_a", "a_post")
    }

    @Test
    fun `empty selection and empty frame`() {
        df.rename { colsOf<Boolean>() }.addPrefix("new_") shouldBe df
        df.rename { colsOf<Boolean>() }.addSuffix("_old") shouldBe df
        DataFrame.empty().rename { all() }.addPrefix("new_") shouldBe DataFrame.empty()
        DataFrame.empty().rename { all() }.addSuffix("_old") shouldBe DataFrame.empty()
    }

    @Test
    fun `empty columns retain their types`() {
        val empty = df.take(0)
        val result = empty.rename { all() }.addSuffix("_empty")

        result.rowsCount() shouldBe 0
        result.columnNames() shouldBe listOf("name_empty", "age_empty", "score_empty")
        result.columnTypes() shouldBe empty.columnTypes()
    }
}

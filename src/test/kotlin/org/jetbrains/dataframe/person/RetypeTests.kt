package org.jetbrains.dataframe.person

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.junit.Test

class RetypeTests : BaseTest() {

    @DataFrameType
    interface NameAndCity{
        val name: String
        val city: String?
    }

    @DataFrameType
    interface GroupedPerson {
        val nameAndCity : TypedDataFrameRow<NameAndCity>
        val age: Int
        val weight: Int?
    }

    @Test
    fun retypeTest() {
        val grouped = df.groupCols { name and city }.into("nameAndCity")
        grouped[GroupedPerson::nameAndCity].type shouldBe getType<TypedDataFrameRow<Unit>>()
        val retyped = grouped.retype<GroupedPerson>()
        retyped[GroupedPerson::nameAndCity].type shouldBe getType<TypedDataFrameRow<NameAndCity>>()
    }

    @Test
    fun extensionPropertiesTest(){
        val code = CodeGenerator().generate(GroupedPerson::class)
        val dfName = DataFrameBase::class.simpleName
        val dfRowName = DataFrameRowBase::class.simpleName
        val className = GroupedPerson::class.qualifiedName
        val nameAndCity = NameAndCity::class.qualifiedName
        val expected = """
            val $dfName<$className>.age: org.jetbrains.dataframe.ColumnData<kotlin.Int> get() = this["age"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
            val $dfRowName<$className>.age: Int get() = this["age"] as Int
            val $dfName<$className>.nameAndCity: org.jetbrains.dataframe.GroupedColumn<$nameAndCity> get() = this["nameAndCity"] as org.jetbrains.dataframe.GroupedColumn<$nameAndCity>
            val $dfRowName<$className>.nameAndCity: org.jetbrains.dataframe.TypedDataFrameRow<$nameAndCity> get() = this["nameAndCity"] as org.jetbrains.dataframe.TypedDataFrameRow<$nameAndCity>
            val $dfName<$className>.weight: org.jetbrains.dataframe.ColumnData<kotlin.Int?> get() = this["weight"] as org.jetbrains.dataframe.ColumnData<kotlin.Int?>
            val $dfRowName<$className>.weight: Int? get() = this["weight"] as Int?
        """.trimIndent()
        code.joinToString("\n") shouldBe expected
    }
}
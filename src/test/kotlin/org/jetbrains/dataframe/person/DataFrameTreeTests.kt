package org.jetbrains.dataframe.person

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.person.DataFrameTreeTests.Properties.city
import org.jetbrains.dataframe.person.DataFrameTreeTests.Properties.nameAndCity
import org.junit.Test

class DataFrameTreeTests : BaseTest() {

    @DataFrameType
    interface NameAndCity {
        val name: String
        val city: String?
    }

    @DataFrameType
    interface GroupedPerson {
        val nameAndCity: TypedDataFrameRow<NameAndCity>
        val age: Int
        val weight: Int?
    }

    val grouped = df.groupCols { name and city }.into("nameAndCity").retype<GroupedPerson>()

    object Properties {
        val DataFrameRowBase<NameAndCity>.name get() = this["name"] as String
        val DataFrameRowBase<NameAndCity>.city get() = this["city"] as String?
        val DataFrameBase<NameAndCity>.name get() = this["name"].typed<String>()
        val DataFrameBase<NameAndCity>.city get() = this["city"].typed<String?>()

        val DataFrameRowBase<GroupedPerson>.age get() = this["age"] as Int
        val DataFrameRowBase<GroupedPerson>.weight get() = this["weight"] as Int?
        val DataFrameRowBase<GroupedPerson>.nameAndCity get() = this["nameAndCity"] as TypedDataFrameRow<NameAndCity>
        val DataFrameBase<GroupedPerson>.age get() = this["age"].typed<Int>()
        val DataFrameBase<GroupedPerson>.weight get() = this["weight"].typed<Int?>()
        val DataFrameBase<GroupedPerson>.nameAndCity get() = this["nameAndCity"].grouped<NameAndCity>()
    }

    @Test
    fun `group column type`() {
        grouped.nameAndCity.type shouldBe getType<TypedDataFrameRow<NameAndCity>>()
        grouped.nameAndCity.dfType shouldBe getType<NameAndCity>()
    }

    @Test
    fun `update`() {
        val updated = grouped.update { nameAndCity }.with { nameAndCity.city }
        updated shouldBe typed.select { city.rename("nameAndCity") and age and weight }
    }

    @Test
    fun `select`() {
        val selected = grouped.select { nameAndCity.city and age}
        selected shouldBe typed.select { city and age }
    }

    @Test
    fun extensionPropertiesTest() {
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
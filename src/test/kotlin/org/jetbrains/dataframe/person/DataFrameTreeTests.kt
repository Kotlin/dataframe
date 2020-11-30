package org.jetbrains.dataframe.person

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.junit.Test

class DataFrameTreeTests : BaseTest() {

    @DataFrameType
    interface NameAndCity {
        val name: String
        val city: String?
    }

    @DataFrameType
    interface GroupedPerson {
        val nameAndCity: DataFrameRow<NameAndCity>
        val age: Int
        val weight: Int?
    }

    val df2 = df.move { name and city }.into("nameAndCity")
    val typed2 = df2.typed<GroupedPerson>()

    val DataFrameRowBase<NameAndCity>.name get() = this["name"] as String
    val DataFrameRowBase<NameAndCity>.city get() = this["city"] as String?
    val DataFrameBase<NameAndCity>.name get() = this["name"].typed<String>()
    val DataFrameBase<NameAndCity>.city get() = this["city"].typed<String?>()

    val DataFrameRowBase<GroupedPerson>.age get() = this["age"] as Int
    val DataFrameRowBase<GroupedPerson>.weight get() = this["weight"] as Int?
    val DataFrameRowBase<GroupedPerson>.nameAndCity get() = this["nameAndCity"] as DataFrameRow<NameAndCity>
    val DataFrameBase<GroupedPerson>.age get() = this["age"].typed<Int>()
    val DataFrameBase<GroupedPerson>.weight get() = this["weight"].typed<Int?>()
    val DataFrameBase<GroupedPerson>.nameAndCity get() = this["nameAndCity"].grouped<NameAndCity>()

    val nameAndCity by columnGroup()

    @Test
    fun `group indexing`() {

        df2[nameAndCity][city] shouldBe typed.city
        typed2.nameAndCity.city shouldBe typed.city
        df2["nameAndCity"]["city"] shouldBe typed.city
    }

    @Test
    fun `update`() {
        val expected = typed.select { city.rename("nameAndCity") and age and weight }

        df2.update { nameAndCity }.with { it[city] } shouldBe expected
        df2.update { nameAndCity }.with { this[nameAndCity][city] } shouldBe expected
        typed2.update { nameAndCity }.with { nameAndCity.city } shouldBe expected
        typed2.update { nameAndCity }.with { it.city } shouldBe expected
    }

    @Test
    fun `slice`() {

        val expected = typed[0..2].name
        val actual = typed2[0..2].nameAndCity.name
        actual shouldBe expected
    }

    @Test
    fun `filter`() {

        val expected = typed.filter { city == null }.select { weight }
        typed2.filter { nameAndCity.city == null }.select { weight } shouldBe expected
        df2.filter { it[nameAndCity][city] == null }.select { weight } shouldBe expected
    }

    @Test
    fun `select`() {
        val expected = typed.select { name and age }
        typed2.select { nameAndCity.name and age } shouldBe expected
        df2.select { it[nameAndCity][name] and age } shouldBe expected
    }

    @Test
    fun `sort`() {
        val expected = typed.sortBy { name then age }.moveTo(1) { city }
        typed2.sortBy { nameAndCity.name then age }.ungroup { nameAndCity } shouldBe expected
    }

    @Test
    fun `groupBy`() {

        val expected = typed.groupBy { name }.max { age }
        typed2.groupBy { nameAndCity.name }.max { age } shouldBe expected
    }

    @Test
    fun splitRows() {
        val selected = typed2.select { nameAndCity }
        val nested = selected.mergeRows { nameAndCity.city }
        val mergedCity by columnList<String?>("city")
        val res = nested.splitRows { nameAndCity[mergedCity] }
        val expected = selected.sortBy { nameAndCity.name }
        val actual = res.sortBy { nameAndCity.name }
        actual shouldBe expected
    }

    @Test
    fun spread() {

        val modified = df.addRow("Alice", 55, "Moscow", 100)
        val df2 =  modified.move { name and city }.into("nameAndCity")
        val typed2 = df2.typed<GroupedPerson>()

        val expected = modified.typed<Person>().select { name and city and age }.groupBy { city }.sortBy { city.nullsLast }.map { key1, group ->
            val ages = group.groupBy { name }
            val cityName = key1.city ?: "null"
            val isList = ages.groups.asIterable().any { it.nrow > 1 }
            ages.map { key2, group ->
                val value = if(isList) group.age.toList() else group.age.single()
                (cityName to key2.name) to value
            }.sortedBy { it.first.second }
        }.flatten()

        val cities by columnGroup()

        fun <T> DataFrame<T>.check() {
            columnNames() shouldBe listOf("name", "cities")
            this[name] shouldBe typed.name.distinct()
            val group = this[cities]
            group.ncol shouldBe typed.city.ndistinct
            group.columns().forEach {
                if(it.name == "Moscow") it.type shouldBe getType<List<Int>?>()
                else it.type shouldBe getType<Int?>()
            }

            val actual = group.columns().sortedBy { it.name }.flatMap { col ->
                rows.sortedBy { it[name] }.map { row -> (col.name to row[name]) to row[col] }.filter { it.second != null }
            }
            actual shouldBe expected
        }

        typed2.select { nameAndCity and age }.spread { nameAndCity.city }.by { age }.into("cities").check()
        df2.select(nameAndCity, age).spread { it[nameAndCity][city] }.by(age).into(cities).check()
        df2.select(GroupedPerson::nameAndCity, GroupedPerson::age).spread { it[GroupedPerson::nameAndCity][NameAndCity::city] }.by(GroupedPerson::age).into("cities").check()
        df2.select("nameAndCity", "age").spread { it["nameAndCity"]["city"] }.by("age").into("cities").check()
    }

    @Test
    fun extensionPropertiesTest() {
        val code = CodeGenerator().generate(GroupedPerson::class)
        val dataFrameBase = DataFrameBase::class.simpleName
        val dataFrameRowBase = DataFrameRowBase::class.simpleName
        val dataFrameRow = DataFrameRow::class.qualifiedName
        val className = GroupedPerson::class.qualifiedName
        val shortName = GroupedPerson::class.simpleName!!
        val nameAndCity = NameAndCity::class.qualifiedName
        val groupedColumn = GroupedColumnBase::class.qualifiedName
        val columnData = ColumnData::class.qualifiedName
        val expected = """
            val $dataFrameBase<$className>.age: $columnData<kotlin.Int> @JvmName("${shortName}_age") get() = this["age"] as $columnData<kotlin.Int>
            val $dataFrameRowBase<$className>.age: Int @JvmName("${shortName}_age") get() = this["age"] as Int
            val $dataFrameBase<$className>.nameAndCity: $groupedColumn<$nameAndCity> @JvmName("${shortName}_nameAndCity") get() = this["nameAndCity"] as $groupedColumn<$nameAndCity>
            val $dataFrameRowBase<$className>.nameAndCity: $dataFrameRow<$nameAndCity> @JvmName("${shortName}_nameAndCity") get() = this["nameAndCity"] as $dataFrameRow<$nameAndCity>
            val $dataFrameBase<$className>.weight: $columnData<kotlin.Int?> @JvmName("${shortName}_weight") get() = this["weight"] as $columnData<kotlin.Int?>
            val $dataFrameRowBase<$className>.weight: Int? @JvmName("${shortName}_weight") get() = this["weight"] as Int?
        """.trimIndent()
        code.joinToString("\n") shouldBe expected
    }
}
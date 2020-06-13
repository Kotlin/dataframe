package krangl.typed

import io.kotlintest.*
import krangl.typed.tracking.trackColumnAccess
import org.junit.Test
import kotlin.math.exp

class TypedDataFrameTests {

// Column names

    val nameStr = "name"
    val ageStr = "age"
    val cityStr = "city"
    val weightStr = "weight"

// Data set

    val df = (krangl.dataFrameOf(nameStr, ageStr, cityStr, weightStr))(
            "Alice", 15, "London", 54,
            "Bob", 45, "Dubai", 87,
            "Mark", 20, "Moscow", null,
            "Mark", 30, null, 90,
            "Mark", 40, "Milan", null,
            "Bob", 30, "Tokyo", 68,
            "Alice", 20, null, 55
    ).typed<Any>()

// Generated Code

    @DataFrameType
    interface Person {
        val name: String
        val age: Int
        val city: String?
        val weight: Int?
    }

    val TypedDataFrameRow<Person>.name get() = this[nameStr] as String
    val TypedDataFrameRow<Person>.age get() = this[ageStr] as Int
    val TypedDataFrameRow<Person>.city get() = this[cityStr] as String?
    val TypedDataFrameRow<Person>.weight get() = this[weightStr] as Int?
    val TypedDataFrame<Person>.name get() = this[nameStr].cast<String>()
    val TypedDataFrame<Person>.age get() = this[ageStr].cast<Int>()
    val TypedDataFrame<Person>.city get() = this[cityStr].cast<String?>()
    val TypedDataFrame<Person>.weight get() = this[weightStr].cast<Int?>()

    val typed: TypedDataFrame<Person> = df.typed()

// Manual Column Definitions

    val name by column<String>()
    val age = column<Int>(ageStr) // way to create column descriptor with specific name
    val city by column<String?>(cityStr)
    val weight by column<Int?>(weightStr)

// Tests

    @Test
    fun `size`() {
        df.size shouldBe DataFrameSize(df.ncol, df.nrow)
    }

    @Test
    fun `slicing`() {
        val sliced = typed[1..2]
        sliced.nrow shouldBe 2
        sliced[0].name shouldBe typed[1].name
    }

    @Test
    fun `access tracking`() {
        trackColumnAccess {
            typed[2].age
        } shouldBe listOf("age")
    }

    @Test
    fun `indexing`() {
        val expected = 45
        val i = 1

        typed[i].age shouldBe expected
        typed.age[i] shouldBe expected

        df[i][age] shouldBe expected
        df[age][i] shouldBe expected

        df[i].int(ageStr) shouldBe expected
        df[i][ageStr] as Int shouldBe expected
        df[ageStr].cast<Int>()[i] shouldBe expected
        df[ageStr][i] as Int shouldBe expected
    }

    @Test
    fun `null indexing`() {

        val i = 3

        typed[i].city shouldBe null
        typed.city[i] shouldBe null

        df[i][city] shouldBe null
        df[city][i] shouldBe null

        df[i].nstring(cityStr) shouldBe null
        df[i][cityStr] as String? shouldBe null
        df[cityStr].cast<String?>()[i] shouldBe null
        df[cityStr][i] as String? shouldBe null
    }

    @Test
    fun `incorrect column nullability`() {

        val col = column<Int>(weightStr) // non-nullable column definition is incorrect here, because actual dataframe has nulls in this column
        shouldThrowAny { df[2][col] }
    }

    @Test
    fun `update`() {

        fun TypedDataFrame<*>.check(){
            this[ageStr].valuesList shouldBe typed.map { age * 2 }
        }

        typed.update { age }.with { age * 2 }.check()

        df.update(age).with { age * 2 }.check()
        df.update { age }.with { age * 2 }.check()

        df.update("age").with { int("age") * 2 }.check()
    }

    @Test
    fun `resetToNull`() {
        val updated = typed.update { allColumns }.withNull()
        updated.columns.forEach {
            it.valuesList.forEach { it shouldBe null }
        }
    }

    @Test
    fun `sort`() {
        val expected = listOf(null, "London", "Dubai", "Tokyo", "Milan", null, "Moscow")

        typed.sortBy { name and age.desc }.map { city } shouldBe expected

        df.sortBy { name and age.desc }.map { city() } shouldBe expected

        df.sortBy { "name" and "age".desc }.map { nstring("city") } shouldBe expected
    }

    @Test
    fun `filter`() {
        val expected = listOf("Bob", "Mark", "Bob")

        typed.filter { age > 20 && city != null }.map { name } shouldBe expected

        df.filter { age > 20 && city() != null }.map { name() } shouldBe expected

        df.filter { int("age") > 20 && get("city") != null }.map { nstring("name") } shouldBe expected
        df.filter { "age"<Int>() > 20 && "city"<Any?>() != null }.map { "name"<String?>() } shouldBe expected
    }

    @Test
    fun `filterNotNull 1`() {
        fun TypedDataFrame<*>.check() = forEach { get(weightStr) shouldNotBe null }

        typed.filterNotNull { weight }.check()
        typed.filterNotNull(typed.weight).check()
        df.filterNotNull { weight }.check()
        df.filterNotNull(weight).check()
        df.filterNotNull("weight").check()
    }

    @Test
    fun `filterNotNull 2`() {
        val expected = typed.rows.count {it.city != null && it.weight != null}

        typed.filterNotNull { weight and city}.nrow shouldBe expected
        typed.filterNotNull(typed.weight, typed.city).nrow shouldBe expected

        df.filterNotNull { weight and city}.nrow shouldBe expected
        df.filterNotNull(weight, city).nrow shouldBe expected
        df.filterNotNull("weight", "city").nrow shouldBe expected
    }

    @Test
    fun `select one `() {
        val expected = listOf(typed.age)
        fun TypedDataFrame<*>.check() = columns shouldBe expected

        typed.select { age }.check()

        df.select {age}.check()
        df.select(age).check()

        df.select("age").check()
        df.select {"age"()}.check()
    }

    @Test
    fun `select if`() {
        val expected = listOf(typed.name, typed.city)

        typed.selectIf { it.name.length == 4 }.columns shouldBe expected
        df.selectIf { it.name.length == 4 }.columns shouldBe expected
    }

    @Test
    fun `select two`(){
        val expected = listOf(typed.age, typed.city)

        typed.select {age and city}.columns shouldBe expected

        df.select {age and city}.columns shouldBe expected
        df.select(age, city).columns shouldBe expected

        df.select {"age" and "city"}.columns shouldBe expected
        df.select("age", "city").columns shouldBe expected
    }

    @Test
    fun `groupBy`() {

        fun TypedDataFrame<*>.check(){
            nrow shouldBe 3
            this["n"].valuesList shouldBe listOf(2, 2, 3)
            this["old count"].valuesList shouldBe listOf(0, 2, 2)
            this["median age"].valuesList shouldBe listOf(17.5, 37.5, 30.0)
            this["min age"].valuesList shouldBe listOf(15, 30, 20)
            this["oldest origin"].valuesList shouldBe listOf(null, "Dubai", "Milan")
            this["youngest origin"].valuesList shouldBe listOf("London", "Tokyo", "Moscow")
            this["all with weights"].valuesList shouldBe listOf(true, true, false)
        }

        typed.groupBy { name }.aggregate {
            count into "n"
            count { age > 25 } into "old count"
            median { age } into "median age"
            min { age } into "min age"
            all { weight != null} into "all with weights"
            maxBy { age }.map { city } into "oldest origin"
            map { sortBy { age }.first().city } into "youngest origin"
        }.check()

        df.groupBy { name }.aggregate {
            count into "n"
            count { age > 25 } into "old count"
            median(age) into "median age"
            min(age) into "min age"
            all { weight() != null} into "all with weights"
            maxBy(age).map { city() } into "oldest origin"
            map { sortBy { age }.first()[city] } into "youngest origin"
        }.check()

        df.groupBy(nameStr).aggregate {
            count into "n"
            count { int("age") > 25 } into "old count"
            median { int("age") } into "median age"
            min { int("age") } into "min age"
            all { get("weight") != null} into "all with weights"
            maxBy { int("age") }.map { get("city") } into "oldest origin"
            map { sortBy("age").first()["city"] } into "youngest origin"
        }.check()
    }

    @Test
    fun `min`(){
        val expected = 15

        typed.min { age } shouldBe expected
        typed.age.min() shouldBe expected

        df.min {age()} shouldBe expected
        df.min(age) shouldBe expected
        df[age].min() shouldBe expected

        df.min {int("age")} shouldBe expected
        df.min {"age"<Int>()} shouldBe expected
        df["age"].cast<Int>().min() shouldBe expected
    }

    @Test
    fun `max for nullable`(){
        val expected = 90

        typed.max {weight} shouldBe expected
        typed.weight.max()

        df.max {weight()} shouldBe expected
        df.max(weight) shouldBe expected
        df[weight].max() shouldBe expected

        df.max {nint("weight")} shouldBe expected
        df["weight"].cast<Int?>().max() shouldBe expected
    }

    @Test
    fun `add one column`(){
        val now = 2020
        val yearStr = "year"
        val expected = typed.map { now - age }

        fun TypedDataFrame<*>.check() = this[yearStr].valuesList shouldBe expected

        typed.add(yearStr) {now - age}.check()
        df.add(yearStr) { now - age }.check()

        df.add(yearStr) { now - int("age")}.check()
        df.add(yearStr) { now - "age"<Int>()}.check()
    }

    @Test
    fun `remove one column`(){

        val expected = listOf(nameStr, cityStr, weightStr)
        fun check(body: ()->TypedDataFrame<*>) = body().columnNames() shouldBe expected

        check { typed - {age} }
        check { typed.remove {age} }

        check { df - {age} }
        check { df - age }
        check { df.remove(age) }

        check { df - "age" }
        check { df.remove("age") }
    }

    @Test
    fun `remove two columns`(){

        val expected = listOf(nameStr, cityStr)
        fun check(body: ()->TypedDataFrame<*>) = body().columnNames() shouldBe expected

        check { typed - {age and weight} }
        check { typed - {age} - {weight} }
        check { typed.remove {age and weight} }

        check { df - {age and weight} }
        check { df - {age} - {weight} }
        check { df.remove(age, weight) }

        check { df - {"age" and "weight"} }
        check { df - "age" - "weight" }
        check { df.remove("age", "weight") }
    }
}
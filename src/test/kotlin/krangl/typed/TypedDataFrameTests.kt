package krangl.typed

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrowAny
import krangl.typed.tracking.trackColumnAccess
import org.junit.Test
import kotlin.reflect.full.memberProperties

class TypedDataFrameTests {

// Data set

    val df = dataFrameOf("name", "age", "city", "weight")(
            "Alice", 15, "London", 54,
            "Bob", 45, "Dubai", 87,
            "Mark", 20, "Moscow", null,
            "Mark", 30, null, 90,
            "Mark", 40, "Milan", null,
            "Bob", 30, "Tokyo", 68,
            "Alice", 20, null, 55
    )

// Generated Code

    @DataFrameType
    interface Person {
        val name: String
        val age: Int
        val city: String?
        val weight: Int?
    }

    val TypedDataFrameRow<Person>.name get() = this["name"] as String
    val TypedDataFrameRow<Person>.age get() = this["age"] as Int
    val TypedDataFrameRow<Person>.city get() = this["city"] as String?
    val TypedDataFrameRow<Person>.weight get() = this["weight"] as Int?
    val TypedDataFrame<Person>.name get() = this["name"].cast<String>()
    val TypedDataFrame<Person>.age get() = this["age"].cast<Int>()
    val TypedDataFrame<Person>.city get() = this["city"].cast<String?>()
    val TypedDataFrame<Person>.weight get() = this["weight"].cast<Int?>()

    val typed: TypedDataFrame<Person> = df.typed()

// Manual Column Definitions

    val name by column<String>()
    val age = column<Int>("age")
    val city by column<String?>()
    val weight by column<Int?>()

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

        df[i].int("age") shouldBe expected
        df[i]["age"] as Int shouldBe expected
        df["age"].cast<Int>()[i] shouldBe expected
        df["age"][i] as Int shouldBe expected
    }

    @Test
    fun `null indexing`() {

        val i = 3

        typed[i].city shouldBe null
        typed.city[i] shouldBe null

        df[i][city] shouldBe null
        df[city][i] shouldBe null

        df[i].nstring("city") shouldBe null
        df[i]["city"] as String? shouldBe null
        df["city"].cast<String?>()[i] shouldBe null
        df["city"][i] as String? shouldBe null
    }

    @Test
    fun `incorrect column nullability`() {

        val col = column<Int>("weight") // non-nullable column definition is incorrect here, because actual dataframe has nulls in this column
        shouldThrowAny { df[2][col] }
    }

    @Test
    fun `update`() {

        fun TypedDataFrame<*>.check() {
            this["age"].values shouldBe typed.map { age * 2 }
        }

        typed.update { age }.with { age * 2 }.check()
        typed.update(typed.age) { age * 2 }.check()

        df.update { age }.with { age * 2 }.check()
        df.update(age) { age * 2 }.check()

        df.update("age") { int("age") * 2 }.check()
        df.update("age") { "age"<Int>() * 2 }.check()
    }

    @Test
    fun `resetToNull`() {
        val updated = typed.update { allColumns }.withNull()
        updated.columns.forEach {
            it.values.forEach { it shouldBe null }
        }
    }

    @Test
    fun `sort`() {
        val expected = listOf(null, "London", "Dubai", "Tokyo", "Milan", null, "Moscow")
        fun TypedDataFrame<*>.check() = this[city].values shouldBe expected

        typed.sortByDesc(typed.age).sortBy(typed.name).check()
        typed.sortBy { name and age.desc }.check()

        df.sortBy { name and age.desc }.check()

        df.sortBy { "name" and "age".desc }.check()
    }

    @Test
    fun `filter`() {
        val expected = listOf("Bob", "Mark", "Bob")
        fun TypedDataFrame<*>.check() = this[name].values shouldBe expected

        typed.filter { age > 20 && city != null }.check()

        df.filter { age() > 20 && city() != null }.check()
        df.filter { age > 20 && city neq null }.check()

        df.filter { int("age") > 20 && get("city") != null }.check()
        df.filter { "age"<Int>() > 20 && "city"<Any?>() != null }.check()
    }

    @Test
    fun `filterNotNull 1`() {
        fun TypedDataFrame<*>.check() = forEach { get("weight") shouldNotBe null }

        typed.filterNotNull(typed.weight).check()
        typed.filterNotNull { weight }.check()

        df.filterNotNull(weight).check()
        df.filterNotNull { weight }.check()

        df.filterNotNull("weight").check()
    }

    @Test
    fun `filterNotNull 2`() {
        val expected = typed.rows.count { it.city != null && it.weight != null }
        fun TypedDataFrame<*>.check() = nrow shouldBe expected

        typed.filterNotNull(typed.weight, typed.city).check()
        typed.filterNotNull { weight and city }.check()

        df.filterNotNull(weight, city).check()
        df.filterNotNull { weight and city }.check()

        df.filterNotNull("weight", "city").check()
    }

    @Test
    fun `select one `() {
        val expected = listOf(typed.age)
        fun TypedDataFrame<*>.check() = columns shouldBe expected

        typed.select { age }.check()
        typed.select(typed.age).check()

        df.select { age }.check()
        df.select(age).check()

        df.select("age").check()
        df.select { "age"() }.check()
    }

    @Test
    fun `select if`() {
        val expected = listOf(typed.name, typed.city)

        typed.selectIf { it.name.length == 4 }.columns shouldBe expected
        df.selectIf { it.name.length == 4 }.columns shouldBe expected
    }

    @Test
    fun `select two`() {
        val expected = listOf(typed.age, typed.city)
        fun TypedDataFrame<*>.check() = columns shouldBe expected

        typed.select { age and city }.check()
        typed.select(typed.age, typed.city).check()

        df.select { age and city }.check()
        df.select(age, city).check()

        df.select { "age" and "city" }.check()
        df.select("age", "city").check()
    }

    @Test
    fun `groupBy`() {

        fun TypedDataFrame<*>.check() {
            nrow shouldBe 3
            this["n"].values shouldBe listOf(2, 2, 3)
            this["old count"].values shouldBe listOf(0, 2, 2)
            this["median age"].values shouldBe listOf(17.5, 37.5, 30.0)
            this["min age"].values shouldBe listOf(15, 30, 20)
            this["oldest origin"].values shouldBe listOf(null, "Dubai", "Milan")
            this["youngest origin"].values shouldBe listOf("London", "Tokyo", "Moscow")
            this["all with weights"].values shouldBe listOf(true, true, false)
        }

        typed.groupBy { name }.aggregate {
            count into "n"
            count { age > 25 } into "old count"
            median { age } into "median age"
            min { age } into "min age"
            all { weight != null } into "all with weights"
            maxBy { age }.map { city } into "oldest origin"
            map { sortBy { age }.first().city } into "youngest origin"
        }.check()

        df.groupBy { name }.aggregate {
            count into "n"
            count { age > 25 } into "old count"
            median(age) into "median age"
            min(age) into "min age"
            all { weight() != null } into "all with weights"
            maxBy(age).map { city() } into "oldest origin"
            map { sortBy { age }.first()[city] } into "youngest origin"
        }.check()

        df.groupBy("name").aggregate {
            count into "n"
            count { int("age") > 25 } into "old count"
            median { int("age") } into "median age"
            min { int("age") } into "min age"
            all { get("weight") != null } into "all with weights"
            maxBy { int("age") }.map { get("city") } into "oldest origin"
            map { sortBy("age").first()["city"] } into "youngest origin"
        }.check()
    }

    @Test
    fun `min`() {
        val expected = 15

        typed.min { age } shouldBe expected
        typed.age.min() shouldBe expected

        df.min { age() } shouldBe expected
        df.min(age) shouldBe expected
        df[age].min() shouldBe expected

        df.min { int("age") } shouldBe expected
        df.min { "age"<Int>() } shouldBe expected
        df["age"].cast<Int>().min() shouldBe expected
    }

    @Test
    fun `nullable max`() {
        val expected = 90

        typed.max { weight } shouldBe expected
        typed.weight.max()

        df.max { weight() } shouldBe expected
        df.max(weight) shouldBe expected
        df[weight].max() shouldBe expected

        df.max { nint("weight") } shouldBe expected
        df["weight"].cast<Int?>().max() shouldBe expected
    }

    @Test
    fun `nullable minBy`() {
        val expected = "Alice"

        fun TypedDataFrameRow<*>?.check() = this!![name] shouldBe expected

        typed.filterNotNull{weight}.minBy { weight!! }.check()

        df.filterNotNull(weight).minBy { weight()!! }.check()

        df.filterNotNull("weight").minBy { "weight"<Int>() }.check()
        df.filterNotNull("weight").minBy<Int>("weight").check()
    }

    @Test
    fun `maxBy`() {
        val expected = "Bob"

        fun TypedDataFrameRow<*>?.check() = this!![name] shouldBe expected

        typed.maxBy { age }.check()
        typed.maxBy(typed.age).check()

        df.maxBy { age() }.check()
        df.maxBy(age).check()

        df.maxBy { "age"<Int>() }.check()
        df.maxBy<Int>("age").check()
    }

    @Test
    fun `add one column`() {
        val now = 2020
        val yearStr = "year"
        val expected = typed.map { now - age }

        fun TypedDataFrame<*>.check() = this[yearStr].values shouldBe expected

        typed.add(yearStr) { now - age }.check()
        df.add(yearStr) { now - age }.check()

        df.add(yearStr) { now - int("age") }.check()
        df.add(yearStr) { now - "age"<Int>() }.check()
    }

    @Test
    fun `remove one column`() {

        val expected = listOf("name", "city", "weight")
        fun check(body: () -> TypedDataFrame<*>) = body().columnNames() shouldBe expected

        check { typed - { age } }
        check { typed.remove { age } }

        check { df - { age } }
        check { df - age }
        check { df.remove(age) }

        check { df - "age" }
        check { df.remove("age") }
    }

    @Test
    fun `remove two columns`() {

        val expected = listOf("name", "city")
        fun check(body: () -> TypedDataFrame<*>) = body().columnNames() shouldBe expected

        check { typed - { age and weight } }
        check { typed - { age } - { weight } }
        check { typed.remove { age and weight } }

        check { df - { age and weight } }
        check { df - { age } - { weight } }
        check { df.remove(age, weight) }

        check { df - { "age" and "weight" } }
        check { df - "age" - "weight" }
        check { df.remove("age", "weight") }
    }

    @Test
    fun `merge similar dataframes`(){

        val res =  typed + typed + typed
        res.name.length shouldBe 3 * typed.nrow
        res.forEach { this.values shouldBe typed[index % typed.nrow].values }
    }

    @Test
    fun `merge different dataframes`(){

        val height by column<Int>()
        val heightOrNull = height.nullable()

        val other = dataFrameOf(name, height)(
                "Bill", 135,
                "Mark", 160
        ).typed<Unit>()

        val res = typed.add(other)
        res.nrow shouldBe typed.nrow + other.nrow
        res.take(typed.nrow).forEach { heightOrNull() == null }
        val q = res.takeLast(other.nrow)
        q.forEach { name() shouldBe other[index][name] }
        q.forEach { heightOrNull() shouldBe other[index][height] }
    }

    @Test
    fun `generate marker interface`(){
        val property = TypedDataFrameTests::class.memberProperties.first { it.name == "df" }
        val code = CodeGenerator().generate(df, property)
        val expectedDeclaration = """
            @DataFrameType(isOpen = false)
            interface DataFrameType###{
                val name: String
                val age: Int
                val city: String?
                val weight: Int?
            }""".trimIndent()

        val expectedConverter = "$" +  "it.typed<DataFrameType###>()"

        code.size shouldBe 2
        code[0].trimIndent() shouldBe expectedDeclaration
        code[1] shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`(){
        val code = CodeGenerator().generate(Person::class)

        val expected = """
            val TypedDataFrame<krangl.typed.TypedDataFrameTests.Person>.age: krangl.typed.TypedColData<kotlin.Int> get() = (this["age"]) as krangl.typed.TypedColData<kotlin.Int>
            val TypedDataFrameRow<krangl.typed.TypedDataFrameTests.Person>.age: Int get() = (this["age"]) as Int
            val TypedDataFrame<krangl.typed.TypedDataFrameTests.Person>.city: krangl.typed.TypedColData<kotlin.String?> get() = (this["city"]) as krangl.typed.TypedColData<kotlin.String?>
            val TypedDataFrameRow<krangl.typed.TypedDataFrameTests.Person>.city: String? get() = (this["city"]) as String?
            val TypedDataFrame<krangl.typed.TypedDataFrameTests.Person>.name: krangl.typed.TypedColData<kotlin.String> get() = (this["name"]) as krangl.typed.TypedColData<kotlin.String>
            val TypedDataFrameRow<krangl.typed.TypedDataFrameTests.Person>.name: String get() = (this["name"]) as String
            val TypedDataFrame<krangl.typed.TypedDataFrameTests.Person>.weight: krangl.typed.TypedColData<kotlin.Int?> get() = (this["weight"]) as krangl.typed.TypedColData<kotlin.Int?>
            val TypedDataFrameRow<krangl.typed.TypedDataFrameTests.Person>.weight: Int? get() = (this["weight"]) as Int?
        """.trimIndent()
        code.joinToString("\n") shouldBe expected
    }

    @Test
    fun `generate derived interface`(){
        val codeGen = CodeGenerator()
        codeGen.generate(Person::class)
        val property = TypedDataFrameTests::class.memberProperties.first { it.name == "df" }
        val code = codeGen.generate(df.filterNotNull(), property)
        val expected = """
            @DataFrameType(isOpen = false)
            interface DataFrameType### : krangl.typed.TypedDataFrameTests.Person{
                override val city: String
                override val weight: Int
            }
        """.trimIndent()
        code[0] shouldBe expected
    }
}
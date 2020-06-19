package krangl.typed

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrowAny
import krangl.typed.tracking.trackColumnAccess
import org.junit.Test
import java.time.LocalDate
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
    val city = Person::city.toColumn()
    val weight by column<Int?>("weight")

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

        fun Int.check() = this shouldBe expected

        typed[i].age.check()
        typed.age[i].check()

        df[i][age].check()
        df[age][i].check()

        df[i][Person::age].check()
        df[Person::age][i].check()

        df[i].int("age").check()
        (df[i]["age"] as Int).check()

        df["age"].cast<Int>()[i].check()
        (df["age"][i] as Int).check()
    }

    @Test
    fun `null indexing`() {

        val i = 3

        fun String?.check() = this shouldBe null

        typed[i].city.check()
        typed.city[i].check()

        df[i][city].check()
        df[city][i].check()

        df[i][Person::city].check()
        df[Person::city][i].check()

        df[i].nstring("city").check()
        (df[i]["city"] as String?).check()

        df["city"].cast<String?>()[i].check()
        (df["city"][i] as String?).check()
    }

    @Test
    fun `incorrect column nullability`() {

        val col = column<Int>("weight") // non-nullable column definition is incorrect here, because actual dataframe has nulls in this column
        shouldThrowAny { df[2][col] }
    }

    @Test
    fun `update`() {

        fun TypedDataFrame<*>.check() {
            columns[1].name shouldBe "age"
            ncol shouldBe typed.ncol
            this["age"].values shouldBe typed.map { age * 2 }
        }

        typed.update { it.age }.with { it.age * 2 }.check()
        typed.update { age }.with { age * 2 }.check()
        typed.update(typed.age) { age * 2 }.check()

        df.update { age }.with { age * 2 }.check()
        df.update(age) { age * 2 }.check()
        df.update(age) { it[age] * 2 }.check()

        df.update(Person::age) { it[Person::age] * 2 }.check()

        df.update("age") { int("age") * 2 }.check()
        df.update("age") { "age"<Int>() * 2 }.check()
    }

    @Test
    fun `null to zero`() {
        fun TypedDataFrame<*>.check() {
            this["weight"].values.any { it == null } shouldBe false
        }

        typed.nullToZero { it.weight }.check()
        typed.nullToZero { weight }.check()
        typed.nullToZero(typed.weight).check()

        df.nullToZero { weight }.check()
        df.nullToZero(weight).check()

        df.nullToZero("weight").check()
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
        typed.sortBy { name then age.desc }.check()
        typed.sortBy { it.name then it.age.desc }.check()

        df.sortBy { name then age.desc }.check()

        df.sortBy { Person::name then Person::age.desc }.check()

        df.sortBy { "name" then "age".desc }.check()
    }

    @Test
    fun `equals`() {
        typed shouldBe typed.update { age }.with { age }
    }

    @Test
    fun `get group by single key`() {
        typed.groupBy { name }["Mark"] shouldBe typed.filter { name == "Mark" }
    }

    @Test
    fun `get group by complex key`() {
        typed.groupBy { city and name }["Tokyo", "Bob"] shouldBe typed.filter { name == "Bob" && city == "Tokyo" }
    }

    @Test
    fun `get group by partial key`() {
        typed.groupBy { city and name }["Tokyo"] shouldBe typed.filter { city == "Tokyo" }
    }

    @Test
    fun `group and sort`() {
        typed.groupBy { name }.sortBy { name.desc then age }.ungroup() shouldBe typed.sortBy { name.desc then age }
    }

    @Test
    fun `filter`() {

        val expected = listOf("Bob", "Mark", "Bob")
        fun TypedDataFrame<*>.check() = this[name].values shouldBe expected

        val limit = 20

        typed.filter { it.age > limit && it.city != null }.check()
        typed.filter { age > limit && it.city != null }.check()

        df.filter { it[Person::age] > limit && it[Person::city] != null }.check()
        df.filter { Person::age > limit && (Person::city)() != null }.check()

        df.filter { age > limit && city() != null }.check()
        df.filter { it[age] > limit && this[city] != null }.check()
        df.filter { age > limit && city neq null }.check()

        df.filter { it.int("age") > limit && it.nstring("city") != null }.check()
        df.filter { "age"<Int>() > limit && "city"<String?>() != null }.check()
    }

    @Test
    fun `filterNotNull 1`() {

        fun TypedDataFrame<*>.check() = forEach { get("weight") shouldNotBe null }

        typed.filterNotNull(typed.weight).check()
        typed.filterNotNull { weight }.check()
        typed.filterNotNull { it.weight }.check()

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
        typed.filterNotNull { it.weight and it.city }.check()

        df.filterNotNull(Person::weight, Person::city).check()

        df.filterNotNull(weight, city).check()
        df.filterNotNull { weight and city }.check()

        df.filterNotNull("weight", "city").check()
    }

    @Test
    fun `select one `() {
        val expected = listOf(typed.age)
        fun TypedDataFrame<*>.check() = columns shouldBe expected

        typed.select { age }.check()
        typed.select { it.age }.check()
        typed.select(typed.age).check()

        df.select(Person::age).check()

        df.select { age }.check()
        df.select(age).check()

        df.select("age").check()

        df.select { "age"() }.check()
        df.select { get("age") }.check()
        df.select { this["age"] }.check()
    }

    @Test
    fun `select if`() {
        val expected = listOf(typed.name, typed.city)

        fun TypedDataFrame<*>.check() = columns shouldBe expected

        typed.selectIf { it.name.length == 4 }.check()
        df.selectIf { it.name.length == 4 }.check()
    }

    @Test
    fun `select two`() {
        val expected = listOf(typed.age, typed.city)
        fun TypedDataFrame<*>.check() = columns shouldBe expected

        typed.select { age and city }.check()
        typed.select { it.age and it.city }.check()
        typed.select(typed.age, typed.city).check()

        typed.select(Person::age, Person::city).check()

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
            checkAll { weight != null } into "all with weights"
            maxBy { age }.map { city } into "oldest origin"
            compute { sortBy { age }.first().city } into "youngest origin"
        }.check()

        typed.groupBy { it.name }.aggregate {
            count into "n"
            count { it.age > 25 } into "old count"
            median { it.age } into "median age"
            min { it.age } into "min age"
            checkAll { it.weight != null } into "all with weights"
            maxBy { it.age }.map { it.city } into "oldest origin"
            compute { sortBy { it.age }.first().city } into "youngest origin"
        }.check()

        df.groupBy(name).aggregate {
            count into "n"
            count { age(it) > 25 } into "old count"
            median(age) into "median age"
            min(age) into "min age"
            checkAll { weight neq null } into "all with weights"
            maxBy(age).map { city() } into "oldest origin"
            compute { sortBy(age).first()[city] } into "youngest origin"
        }.check()

        df.groupBy(Person::name).aggregate {
            count into "n"
            count { Person::age > 25 } into "old count"
            median(Person::age) into "median age"
            min(Person::age) into "min age"
            checkAll { Person::weight neq null } into "all with weights"
            maxBy(Person::age).map { it[Person::city] } into "oldest origin"
            compute { sortBy(Person::age).first()[Person::age] } into "youngest origin"
        }.check()

        df.groupBy("name").aggregate {
            count into "n"
            count { int("age") > 25 } into "old count"
            median { int("age") } into "median age"
            min { int("age") } into "min age"
            checkAll { get("weight") != null } into "all with weights"
            maxBy { int("age") }.map { get("city") } into "oldest origin"
            compute { sortBy("age").first()["city"] } into "youngest origin"
        }.check()
    }

    @Test
    fun `min`() {
        val expected = 15

        fun Int?.check() = this shouldBe expected

        typed.min { age }.check()
        typed.min { it.age }.check()
        typed.age.min().check()

        df.min { age(it) }.check()
        df.min(age).check()
        df[age].min().check()

        df.min { int("age") }.check()
        df.min { "age"<Int>() }.check()
        df["age"].cast<Int>().min().check()
    }

    @Test
    fun `nullable max`() {
        val expected = 90

        fun Int?.check() = this shouldBe expected

        typed.max { weight }.check()
        typed.max { it.weight }.check()
        typed.weight.max().check()

        df.max { weight() }.check()
        df.max(weight).check()
        df[weight].max().check()

        df.max { nint("weight") }.check()
        df["weight"].cast<Int?>().max().check()
    }

    @Test
    fun `nullable minBy`() {
        val expected = "Alice"

        fun TypedDataFrameRow<*>?.check() = this!![name] shouldBe expected

        typed.filterNotNull { weight }.minBy { weight!! }.check()
        typed.filterNotNull { it.weight }.minBy { it.weight!! }.check()

        df.filterNotNull(weight).minBy { weight()!! }.check()

        df.filterNotNull("weight").minBy { "weight"<Int>() }.check()
        df.filterNotNull("weight").minBy<Int>("weight").check()
    }

    @Test
    fun `maxBy`() {
        val expected = "Bob"

        fun TypedDataFrameRow<*>?.check() = this!![name] shouldBe expected

        typed.maxBy { age }.check()
        typed.maxBy { it.age }.check()
        typed.maxBy(typed.age).check()

        df.maxBy { age() }.check()
        df.maxBy(age).check()

        df.maxBy { "age"<Int>() }.check()
        df.maxBy<Int>("age").check()
    }

    @Test
    fun `add one column`() {
        val now = 2020
        val expected = typed.map { now - age }

        fun TypedDataFrame<*>.check() = this["year"].values shouldBe expected

        typed.add("year") { now - age }.check()
        typed.add("year") { now - it.age }.check()

        df.add("year") { now - age }.check()

        df.add("year") { now - int("age") }.check()
        df.add("year") { now - "age"<Int>() }.check()
    }

    @Test
    fun `remove one column`() {

        val expected = listOf("name", "city", "weight")
        fun check(body: () -> TypedDataFrame<*>) = body().columnNames() shouldBe expected

        check { typed - { age } }
        check { typed - { it.age } }
        check { typed.remove { age } }
        check { typed.remove { it.age } }

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
        check { typed - { it.age and it.weight } }
        check { typed - { age } - { weight } }
        check { typed - { it.age } - { it.weight } }
        check { typed.remove { age and weight } }
        check { typed.remove { it.age and it.weight } }

        check { df - { age + weight } }
        check { df - age - weight }
        check { df - { age } - { weight } }
        check { df.remove(age, weight) }

        check { df - { "age" and "weight" } }
        check { df - "age" - "weight" }
        check { df.remove("age", "weight") }
    }

    @Test
    fun `merge similar dataframes`() {

        val res = typed + typed + typed
        res.name.length shouldBe 3 * typed.nrow
        res.forEach { this.values shouldBe typed[index % typed.nrow].values }
    }

    @Test
    fun `merge different dataframes`() {

        val height by column<Int>()
        val heightOrNull = height.nullable()

        val other = dataFrameOf(name, height)(
                "Bill", 135,
                "Mark", 160
        ).typed<Unit>()

        val res = typed.union(other)
        res.nrow shouldBe typed.nrow + other.nrow
        res.take(typed.nrow).forEach { heightOrNull() == null }
        val q = res.takeLast(other.nrow)
        q.forEach { name() shouldBe other[index][name] }
        q.forEach { heightOrNull() shouldBe other[index][height] }
    }

    @Test
    fun `row to frame`() {
        typed[1].toDataFrame().name.length shouldBe 1
    }

    @Test
    fun `compare comparable`() {
        val new = df.add("date") { LocalDate.now().minusDays(index.toLong()) }
        val date by column<LocalDate>()
        new.filter { date >= LocalDate.now().minusDays(3) }.nrow shouldBe 4
    }

    @Test
    fun `union dataframes with different type of the same column`() {
        val df2 = dataFrameOf("age")(32.6, 56.3, null)
        df2["age"].type.classifier shouldBe Double::class
        df2["age"].nullable shouldBe true
        val merged = df.union(df2)
        merged["age"].type.classifier shouldBe Number::class
        merged["age"].nullable shouldBe true
        val updated = merged.update("age") { "age"<Number?>()?.toDouble() }
        updated["age"].type.classifier shouldBe Double::class
        updated["age"].nullable shouldBe true
    }

    @Test
    fun `generate marker interface`() {
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

        val expectedConverter = "$" + "it.typed<DataFrameType###>()"

        code.size shouldBe 2
        code[0].trimIndent() shouldBe expectedDeclaration
        code[1] shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`() {
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
    fun `generate derived interface`() {
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

    @Test
    fun `render to html`() {
        val src = df.toHTML()
        println(src)
    }
}
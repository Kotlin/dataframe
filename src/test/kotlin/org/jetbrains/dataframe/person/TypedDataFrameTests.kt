package org.jetbrains.dataframe.person

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.tracking.trackColumnAccess
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDate

class TypedDataFrameTests : BaseTest() {

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

        df["age"].typed<Int>()[i].check()
        (df["age"][i] as Int).check()
    }

    @Test
    fun `null indexing`() {

        val i = 5

        fun String?.check() = this shouldBe null

        typed[i].city.check()
        typed.city[i].check()

        df[i][city].check()
        df[city][i].check()

        df[i][Person::city].check()
        df[Person::city][i].check()

        df[i].nstring("city").check()
        (df[i]["city"] as String?).check()

        df["city"].typed<String?>()[i].check()
        (df["city"][i] as String?).check()
    }

    @Test
    fun `incorrect column nullability`() {

        val col = column<Int>("weight") // non-nullable column definition is incorrect here, because actual dataframe has nulls in this column

        shouldThrow<NullPointerException> {
            println(df[2][col])
        }
    }

    @Test
    fun `update`() {

        fun TypedDataFrame<*>.check() {
            columns[1].name shouldBe "age"
            ncol shouldBe typed.ncol
            this["age"].values shouldBe typed.map { age * 2 }
        }

        typed.update { age }.with { it * 2 }.check()
        typed.update { age }.with { it * 2 }.check()
        typed.update(typed.age) { it * 2 }.check()

        df.update { age }.with { it * 2 }.check()
        df.update(age) { it * 2 }.check()
        df.update(age) { it * 2 }.check()

        df.update(Person::age) { it * 2 }.check()

        df.update("age") { int("age") * 2 }.check()
        df.update("age") { "age"<Int>() * 2 }.check()
    }

    @Test
    fun `conditional update`() {

        fun TypedDataFrame<*>.check() {
            columns[1].name shouldBe "age"
            ncol shouldBe typed.ncol
            this["age"].values shouldBe typed.map { if (age > 25) null else age }
        }

        typed.update { age }.where { it > 25 }.withNull().check()
        typed.update { age }.where { it > 25 }.withNull().check()
        typed.update(typed.age).where { it > 25 }.withNull().check()

        df.update { age }.where { it > 25 }.withNull().check()
        df.update(age).where { it > 25 }.withNull().check()
        df.update(age).where { it > 25 }.withNull().check()

        df.update(Person::age).where { it > 25 }.withNull().check()

        df.update("age").where { it as Int > 25 }.withNull().check()
        df.update("age").where { it as Int > 25 }.withNull().check()
    }

    @Test
    fun `null to zero`() {
        val expected = typed.weight.values.map { it ?: 0 }
        fun TypedDataFrame<*>.check() {
            this["weight"].values shouldBe expected
        }

        typed.fillNulls { it.weight }.with(0).check()
        typed.fillNulls { weight }.with(0).check()
        typed.fillNulls(typed.weight).with(0).check()

        df.fillNulls { weight }.with(0).check()
        df.fillNulls(weight).with(0).check()

        df.fillNulls("weight").with(0).check()

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

        // updated.forEach {  }
        updated.columns.forEach {
            it.values.forEach { it shouldBe null }
        }
    }

    @Test
    fun `sort`() {

        val expected = listOf(null, "London", "Dubai", "Tokyo", "Milan", "Moscow", "Moscow")

        fun TypedDataFrame<*>.check() = this[city].values shouldBe expected

        typed.sortByDesc(typed.age).sortBy(typed.name).check()
        typed.sortBy { name then age.desc }.check()
        typed.sortBy { it.name then it.age.desc }.check()

        df.sortBy { name then age.desc }.check()

        df.sortBy { Person::name then Person::age.desc }.check()

        df.sortBy { "name".cast<String>() then "age".desc }.check()
    }

    @Test
    fun `sort nulls first`() {

        val expected = typed.city.values.sortedBy { it }

        fun TypedDataFrame<*>.check() = this[city].values shouldBe expected

        typed.sortBy { city }.check()
        df.sortBy { city }.check()
        df.sortBy { col(Person::city) }.check()
        df.sortBy { col<String>("city") }.check()
    }

    @Test
    fun `sort nulls last`() {

        val expected = typed.city.values.filterNotNull().sortedBy { it } + listOf(null)

        fun TypedDataFrame<*>.check() = this[city].values shouldBe expected

        typed.sortBy { city.nullsLast }.check()
        df.sortBy { city.nullsLast }.check()
        df.sortBy { Person::city.nullsLast }.check()
        df.sortBy { "city".nullsLast }.check()
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

        val expected = listOf("Bob", "Bob", "Mark")
        fun TypedDataFrame<*>.check() = this[name].values shouldBe expected

        val limit = 20

        typed.filter { it.age > limit && it.weight != null }.check()
        typed.filter { age > limit && it.weight != null }.check()

        df.filter { it[Person::age] > limit && it[Person::weight] != null }.check()
        df.filter { Person::age > limit && (Person::weight)() != null }.check()

        df.filter { age > limit && weight() != null }.check()
        df.filter { it[age] > limit && this[weight] != null }.check()
        df.filter { age > limit && weight neq null }.check()

        df.filter { it.int("age") > limit && it.nint("weight") != null }.check()
        df.filter { "age"<Int>() > limit && "weight"<Int?>() != null }.check()
    }

    @Test
    fun `filterNotNull 1`() {

        fun TypedDataFrame<*>.check() = rows.forEach { get("weight") shouldNotBe null }

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

        typed.select { cols { it.name.length == 4 } }.check()
        df.select { cols { it.name.length == 4 } }.check()
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
    fun `select by type`() {
        val selected = typed.select { colsOfType<String?>() }
        selected shouldBe typed.select { name and city }
    }

    @Test
    fun `select by type not nullable`() {
        val selected = typed.select { colsOfType<String> { !it.hasNulls } }
        selected shouldBe typed.select { name }
    }

    @Test
    fun `move one column`() {
        val moved = typed.moveTo(1) { city }
        val expected = typed.select { cols(name, city, age, weight) }
        moved shouldBe expected
    }

    @Test
    fun `move several columns`() {
        val moved = typed.moveTo(2) { name and city }
        val expected = typed.select { cols(age, weight, name, city) }
        moved shouldBe expected
    }

    @Test
    fun `move several columns to left`() {
        val moved = typed.moveToLeft { weight and age }
        val expected = typed.select { cols(weight, age, name, city) }
        moved shouldBe expected
    }

    @Test
    fun `move several columns to right`() {
        val moved = typed.moveToRight { weight and name }
        val expected = typed.select { cols(age, city, weight, name) }
        moved shouldBe expected
    }

    @Test
    fun `groupBy`() {

        fun TypedDataFrame<*>.check() {
            nrow shouldBe 3
            this["name"].values shouldBe listOf("Alice", "Bob", "Mark")
            this["n"].values shouldBe listOf(2, 2, 3)
            this["old count"].values shouldBe listOf(0, 2, 2)
            this["median age"].values shouldBe listOf(17.5, 37.5, 30.0)
            this["min age"].values shouldBe listOf(15, 30, 20)
            this["oldest origin"].values shouldBe listOf(null, "Dubai", "Milan")
            this["youngest origin"].values shouldBe listOf("London", "Tokyo", "Moscow")
            this["all with weights"].values shouldBe listOf(true, true, false)
            this["from London"].values shouldBe listOf(1, 0, 0)
            this["from Dubai"].values shouldBe listOf(0, 1, 0)
            this["from Moscow"].values shouldBe listOf(0, 0, 2)
            this["from Milan"].values shouldBe listOf(0, 0, 1)
            this["from Tokyo"].values shouldBe listOf(0, 1, 0)
            this["from null"].values shouldBe listOf(1, 0, 0)
            this["ages"].values shouldBe listOf(listOf(15, 20), listOf(45, 30), listOf(20, 40, 30))
        }

        typed.groupBy { name }.aggregate {
            count into "n"
            count { age > 25 } into "old count"
            median { age } into "median age"
            min { age } into "min age"
            checkAll { weight != null } into "all with weights"
            maxBy { age }.map { city } into "oldest origin"
            compute { sortBy { age }.first().city } into "youngest origin"
            count into { "from $city" }
            merge { age } into "ages"
        }.check()

        typed.groupBy { it.name }.aggregate {
            count into "n"
            count { it.age > 25 } into "old count"
            median { it.age } into "median age"
            min { it.age } into "min age"
            checkAll { it.weight != null } into "all with weights"
            maxBy { it.age }.map { it.city } into "oldest origin"
            compute { sortBy { it.age }.first().city } into "youngest origin"
            count into { "from ${it.city}" }
            merge { it.age } into "ages"
        }.check()

        df.groupBy(name).aggregate {
            count into "n"
            count { age(it) > 25 } into "old count"
            median(age) into "median age"
            min(age) into "min age"
            checkAll { weight neq null } into "all with weights"
            maxBy(age).map { city() } into "oldest origin"
            compute { sortBy(age).first()[city] } into "youngest origin"
            count into { "from ${city()}" }
            merge { age() } into "ages"
        }.check()

        df.groupBy(Person::name).aggregate {
            count into "n"
            count { Person::age > 25 } into "old count"
            median(Person::age) into "median age"
            min(Person::age) into "min age"
            checkAll { Person::weight neq null } into "all with weights"
            maxBy(Person::age).map { it[Person::city] } into "oldest origin"
            compute { sortBy(Person::age).first()[Person::city] } into "youngest origin"
            count into { "from ${it[Person::city]}" }
            merge { it[Person::age] } into "ages"
        }.check()

        df.groupBy("name").aggregate {
            count into "n"
            count { int("age") > 25 } into "old count"
            median { int("age") } into "median age"
            min { int("age") } into "min age"
            checkAll { get("weight") != null } into "all with weights"
            maxBy { int("age") }.map { get("city") } into "oldest origin"
            compute { sortBy("age").first()["city"] } into "youngest origin"
            count into { "from " + get("city") }
            merge { int("age") } into "ages"
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
        df["age"].typed<Int>().min().check()
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
        df["weight"].typed<Int?>().max().check()
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
        res.name.size shouldBe 3 * typed.nrow
        res.rows.forEach { it.values shouldBe typed[it.index % typed.nrow].values }
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
        res.take(typed.nrow).rows.forEach { it[heightOrNull] == null }
        val q = res.takeLast(other.nrow)
        q.rows.forEach { it[name] shouldBe other[it.index][name] }
        q.rows.forEach { it[heightOrNull] shouldBe other[it.index][height] }
    }

    @Test
    fun `row to frame`() {
        typed[1].toDataFrame().name.size shouldBe 1
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
        df2["age"].type shouldBe getType<Double?>()
        val merged = df.union(df2)
        merged["age"].type shouldBe getType<Number?>()
        val updated = merged.update("age") { "age"<Number?>()?.toDouble() }
        updated["age"].type shouldBe getType<Double?>()
    }

    @Test
    fun `distinct`() {
        typed.select { name and city }.distinct().nrow shouldBe 6
    }

    @Test
    fun `distinct by`() {
        typed.distinctBy { name }.nrow shouldBe 3
    }

    @Test
    fun `addRow`() {
        val res = typed.addRow("Bob", null, "Paris", null)
        res.nrow shouldBe typed.nrow + 1
        res.name.type shouldBe getType<String>()
        res.age.type shouldBe getType<Int?>()
        res.city.type shouldBe getType<String?>()
        res.weight.type shouldBe getType<Int?>()

        val row = res.last()
        row.name shouldBe "Bob"
        row["age"] shouldBe null
        row.city shouldBe "Paris"
        row.weight shouldBe null
    }

    @Test
    fun `rename`() {
        val renamed = typed.rename("name" to "name2", "age" to "age2")
        renamed["name2"].values shouldBe typed.name.values
        renamed.tryGetColumn("age") shouldBe null
    }

    @Test
    fun `nunique`() {
        typed.name.ndistinct shouldBe 3
    }

    @Test
    fun `encode names`() {
        val encoding = typed.name.distinct().addRowNumber("name_id")
        val res = typed.leftJoin(encoding)
        res["name_id"].values shouldBe listOf(0, 1, 2, 2, 1, 0, 2)
    }

    @Test
    fun `spread exists`() {
        val spread = typed.spreadExists { city }

        spread.ncol shouldBe typed.ncol + typed.city.ndistinct - 2

        for (i in 0 until typed.nrow) {
            val city = typed[i][city]
            if (city != null) spread[i][city] == true
            for (j in typed.ncol until spread.ncol) {
                spread.columns[j].typed<Boolean>().get(i) shouldBe (spread.columns[j].name == city)
            }
        }
    }

    @Test
    fun `spreadExists equality`() {
        val res1 = typed.select { name and city }.spreadExists { city }
        val res2 = typed.groupBy { name }.spreadExists { city }
        val res3 = typed.groupBy { name }.aggregate {
            exists into { city }
        }
        res2 shouldBe res1
        res3 shouldBe res1
    }

    @Test
    fun `spread to bool with conversion`() {
        val res = typed.spreadExists { city.map { it?.decapitalize() } }
        val cities = typed.city.values.filterNotNull()
        val gathered = res.gather { colsOfType<Boolean> { cities.contains(it.name.capitalize()) } }.where { it }.into("city")
        val expected = typed.update { city }.with { it?.decapitalize() }.filterNotNull { city }.moveToRight { city }
        gathered shouldBe expected
    }

    @Test
    fun `spread to bool distinct rows`() {
        val res = typed.spreadExists { city }
        res.ncol shouldBe typed.ncol + typed.city.ndistinct - 2

        for (i in 0 until typed.nrow) {
            val city = typed[i][city]
            if (city != null) res[i][city] == true
            for (j in typed.ncol until res.ncol) {
                res.columns[j].typed<Boolean>().get(i) shouldBe (res.columns[j].name == city)
            }
        }
    }

    @Test
    fun `spread to bool merged rows`() {
        val selected = typed.select { name + city }
        val res = selected.spreadExists { city }

        res.ncol shouldBe selected.city.ndistinct
        res.nrow shouldBe selected.name.ndistinct
        val trueValuesCount = res.columns.takeLast(res.ncol - 1).sumBy { it.typed<Boolean>().values.count { it } }
        trueValuesCount shouldBe selected.filterNotNull { city }.distinct().nrow

        val pairs = (1 until res.ncol).flatMap { i ->
            val col = res.columns[i].typed<Boolean>()
            res.filter { it[col] }.map { name to col.name }
        }.toSet()

        pairs shouldBe typed.filter { city != null }.map { name to city!! }.toSet()
    }

    @Test
    fun `spread to matrix`() {

        val others by column<List<String>>("other")
        val other by column<String>()
        val sum by column<Int>()

        val names = typed.name.values.distinct()

        val src = typed.select { name }
                .add(others) { names }
                .splitRows { others }
                .add(sum) { name.length + other().length }

        val matrix = src.spread { sum }.into { other }
        matrix.ncol shouldBe 1 + names.size

        println(matrix)
    }

    @Test
    fun `gather bool`() {
        val selected = typed.select { name + city }
        val spread = selected.spreadExists { city }
        val res = spread.gather { colsOfType<Boolean>() }.where { it }.into("city")
        val sorted = res.sortBy { name then city }
        sorted shouldBe selected.filterNotNull { city }.distinct().sortBy { name then city }
    }

    @Test
    fun mergeRows() {
        val selected = typed.select { name + city }
        val res = selected.mergeRows { city }
        val cityList by column<List<String?>>("city")
        val expected = selected.map { name to city }.groupBy({ it.first }) { it.second }.mapValues { it.value.toSet() }
        val actual = res.map { name to it[cityList] }.toMap().mapValues { it.value.toSet() }
        actual shouldBe expected
    }

    @Test
    fun splitRows() {
        val selected = typed.select { name + city }
        val nested = selected.mergeRows { city }
        val mergedCity by column<List<String?>>("city")
        val res = nested.splitRows { mergedCity }
        res.sortBy { name } shouldBe selected.sortBy { name }
    }

    @Test
    fun mergeCols() {
        val merged = typed.mergeCols { age and city and weight }.into("info")
        merged.ncol shouldBe 2
        merged.nrow shouldBe typed.nrow
        for (row in 0 until typed.nrow) {
            val list = merged[row]["info"] as List<Any?>
            list.size shouldBe 3
            list[0] shouldBe typed.age[row]
            list[1] shouldBe typed.city[row]
            list[2] shouldBe typed.weight[row]
        }
    }

    @Test
    fun joinColsToString() {
        val merged = typed.mergeCols { age and city and weight }.intoStr("info")
        merged.ncol shouldBe 2
        merged.nrow shouldBe typed.nrow
        for (row in 0 until typed.nrow) {
            val joined = merged[row]["info"] as String
            joined shouldBe typed.age[row].toString() + ", " + typed.city[row] + ", " + typed.weight[row]
        }
    }

    @Test
    fun splitCol() {
        val merged = typed.mergeCols { age and city and weight }.into("info")
        val info by column<List<*>>()
        val res = merged.split { info }.into("age", "city", "weight")
        res shouldBe typed
    }

    @Test
    fun splitStringCol() {
        val merged = typed.mergeCols { age and city and weight }.intoStr("info")
        val info by column<String>()
        val res = merged.split { info }.by(",").into("age", "city", "weight")
        val expected = typed.update { age and city and weight }.with { it.toString() }
        res shouldBe expected
    }

    @Test
    fun `merge cols with conversion`() {
        val spread = typed.groupBy { name }.countBy { city }
        val res = spread.mergeCols { colsOfType<Int>() }.by { it.sum() }.into("cities")
        val expected = typed.select { name and city }.filter { city != null }.groupBy { name }.count("cities")
        res shouldBe expected
    }

    @Test
    fun `generic column type`() {
        val d = typed.update { city }.with { it?.toCharArray()?.toList() ?: emptyList() }
        println(d.city.type)
    }

    @Test
    fun `merge in groupBy`() {
        val grouped = typed.groupBy { name }

        fun TypedDataFrame<*>.check() {
            this["ages"].values shouldBe listOf(listOf(15, 20), listOf(45, 30), listOf(20, 40, 30))
            this["agesStr"].values shouldBe listOf("15, 20", "45, 30", "20, 40, 30")
            this["ages delimited"].values shouldBe listOf("15-20", "45-30", "20-40-30")
            this["min age"].values shouldBe listOf(15, 30, 20)
        }

        grouped.aggregate {
            merge { age } into "ages"
            merge { age }.asStrings() into "agesStr"
            merge { age }.by("-") into "ages delimited"
            merge { age }.by { it.minOrNull() } into "min age"
        }.check()

        listOf(
                grouped.merge { age }.into("ages"),
                grouped.merge { age }.asStrings().into("agesStr"),
                grouped.merge { age }.by("-").into("ages delimited"),
                grouped.merge { age }.by { it.minOrNull() }.into("min age")
        ).joinOrNull()!!.check()
    }

    @Test
    @Ignore
    fun `empty group by`() {
        val ungrouped = typed.filter { false }.groupBy { name }.ungroup()
        ungrouped.nrow shouldBe 0
        ungrouped.ncol shouldBe typed.ncol
    }

}
package krangl.typed.person

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import krangl.typed.*
import org.junit.Test
import java.io.Serializable

class SpreadTests {

    val df = dataFrameOf("name", "key", "value")(
            "Alice", "age", 15,
            "Alice", "city", "London",
            "Alice", "weight", 54,
            "Bob", "age", 45,
            "Bob", "city", "Dubai",
            "Bob", "weight", 87,
            "Mark", "age", 20,
            "Mark", "city", "Moscow",
            "Mark", "weight", null
    )

// Generated Code

    @DataFrameType
    interface Person {
        val name: String
        val key: String
        val value: Any?
    }

    val TypedDataFrameRow<Person>.name get() = this["name"] as String
    val TypedDataFrameRow<Person>.key get() = this["key"] as String
    val TypedDataFrameRow<Person>.value get() = this["value"] as Any?
    val TypedDataFrame<Person>.name get() = this["name"].typed<String>()
    val TypedDataFrame<Person>.key get() = this["key"].typed<String>()
    val TypedDataFrame<Person>.value get() = this["value"].typed<Any?>()

    val typed: TypedDataFrame<Person> = df.typed()

// Tests

    val keyConverter: (String) -> String = { "__$it" }
    val valueConverter: (Any?) -> Any? = { (it as? Int)?.toDouble() ?: it }

    @Test
    fun `spread to pair`() {

        val res = typed.spread { value }.into { key }
        res.ncol shouldBe 1 + typed.key.ndistinct
        res.nrow shouldBe typed.name.ndistinct

        println(res)
        val expected = typed.map { (name to key) to value }.toMap()
        val actual = res.columns.subList(1, res.ncol).flatMap {
            val columnName = it.name
            res.map { (name to columnName) to it[columnName] }
        }.toMap()

        actual shouldBe expected
        res["age"].valueClass shouldBe Int::class
        res["age"].nullable shouldBe false
        res["city"].valueClass shouldBe String::class
        res["city"].nullable shouldBe false
        res["weight"].valueClass shouldBe Int::class
        res["weight"].nullable shouldBe true
    }

    @Test
    fun `spread to pair with group key and conversion`() {

        val res = typed.spread { value.map { it.toString() }  }.into { key }

        res.ncol shouldBe 1 + typed.key.ndistinct
        res.nrow shouldBe typed.name.ndistinct

        val expected = typed.map { (name to key) to value.toString() }.toMap()
        val actual = res.columns.subList(1, res.ncol).flatMap {
            val columnName = it.name
            res.map { (name to columnName) to it[columnName] }
        }.toMap()

        actual shouldBe expected
    }

    @Test
    fun `spread exception`() {

        val first = typed[0]
        val values = first.values.toTypedArray()
        values[2] = 20
        val modified = typed.addRow(*values)
        shouldThrow<Exception> { modified.spread { value }.into { key } }
    }

    @Test
    fun gather() {

        val res = typed.spread { value }.into { key }
        val gathered = res.gather { cols[1 until ncol] }.into("key", "value")
        gathered shouldBe typed
    }

    @Test
    fun `gather with filter`() {

        val spread = typed.spread { value }.into { key }
        val gathered = spread.gather { cols[1 until ncol] }.where { it != null }.into("key", "value")
        gathered shouldBe typed.filterNotNull { value }
    }

    @Test
    fun `spread with key conversion`() {

        val spread = typed.spread { value }.into { key.map(keyConverter) }
        val gathered = spread.gather { cols[1 until ncol] }.into("key", "value")
        gathered shouldBe typed.update { key }.with { "__$it" }
    }

    @Test
    fun `spread with value conversion`() {

        val spread = typed.spread { value.map(valueConverter) }.into { key }
        val gathered = spread.gather { cols[1 until ncol] }.into("key", "value")
        val expected = typed.update { value }.with { valueConverter(it) as? Serializable }
        gathered shouldBe expected
    }

    @Test
    fun `grouped spread with key and value conversions`() {
        val grouped = typed.groupBy { name }
        val spread1 = grouped.spreadSingle { valueConverter(value) }.into { keyConverter(key) }
        val spread2 = grouped.aggregate {
            single { valueConverter(value) } into { keyConverter(key) }
        }
        spread2 shouldBe spread1
        val gathered = spread1.gather { cols[1 until ncol] }.into("key", "value")
        val expected = typed.update { key }.with { keyConverter(it) }.update { value }.with { valueConverter(it) as? Serializable }
        gathered shouldBe expected
    }

    @Test
    fun `grouped spread with key and value conversions 2`() {
        val spread = typed.groupBy { name }.aggregate {
            single { valueConverter(value) } into { keyConverter(key) }
        }
        val gathered = spread.gather { cols[1 until ncol] }.into("key", "value")
        val expected = typed.update { key }.with { keyConverter(it) }.update { value }.with { valueConverter(it) as? Serializable }
        gathered shouldBe expected
    }

    @Test
    fun `gather with value conversion`() {

        val spread = typed.spread { value.map(valueConverter) }.into { key }
        val gathered = spread.gather { cols[1 until ncol] }.map { (it as? Double)?.toInt() ?: it }.into("key", "value")
        gathered shouldBe typed
    }

    @Test
    fun `gather doubles with value conversion`() {

        val spread = typed.spread { value.map(valueConverter) }.into { key }
        val gathered = spread.remove("city").gather { colsOfType<Double?>() }.mapNotNull { it.toInt() }.into("key", "value")
        val expected = typed.filter { key != "city" }.cast { value }.to<Int>()
        gathered shouldBe expected
    }

    @Test
    fun `gather with name conversion`() {

        val spread = typed.spread { value }.into { key.map(keyConverter) }
        val gathered = spread.gather { cols[1 until ncol] }.mapNames { it.substring(2) }.into("key", "value")
        gathered shouldBe typed
    }
/*
    fun qq() {

        typed.update { name }.with { it.capitalize() }

        typed.update { name }.withNull()

        typed.update { name with { it.capitalize() } }

        typed.spread { key into value }

        typed.spread { key.map { "__$it" } into { name + value.toString() } groupedBy(name) }

        typed.spread { key }.intoFlags()

        typed.spread { key }.into { value }

        typed.gather(
                columns = { name and value },
                filter = { it != null },
                mapNames = { it.substring(1) },
                into = "key" to "value"
        )

        typed.gather { name and value }.where { it != null }.mapNames { it.substring(1) }.mapValues { it }.into("key" to "value")

        typed.gather { cols[1..5] where { it != null } mapNames { it.substring(1) } mapValues { it } into "key" to "value" }
    } */
}
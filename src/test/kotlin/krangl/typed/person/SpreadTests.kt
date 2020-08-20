package krangl.typed.person

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import krangl.typed.*
import org.junit.Test

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
    val TypedDataFrame<Person>.name get() = this["name"].cast<String>()
    val TypedDataFrame<Person>.key get() = this["key"].cast<String>()
    val TypedDataFrame<Person>.value get() = this["value"].cast<Any?>()

    val typed: TypedDataFrame<Person> = df.typed()

// Tests

    @Test
    fun `spread`() {

        val res = typed.spread { key to value }
        res.ncol shouldBe 1 + typed.key.ndistinct
        res.nrow shouldBe typed.name.ndistinct

        val expected = typed.map { (name to key) to value }.toMap()
        val actual = res.columns.subList(1, res.ncol).flatMap {
            val keyCol = it.name
            res.map { (name to keyCol) to it[keyCol] }
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
    fun `spread exception`() {

        val first = typed[0]
        val values = first.values.toTypedArray()
        values[2] = 20
        val modified = typed.addRow(*values)
        shouldThrow<Exception> { modified.spread { key to value } }
    }

    @Test
    fun `pivot`() {

        val res = typed.spread { key to value }
        val pivoted = res.gather("key", "value") { columns[1 until ncol] }
        pivoted shouldBe typed
    }
}
package krangl.typed

import io.kotlintest.shouldBe
import krangl.*
import krangl.typed.tracking.trackColumnAccess
import org.junit.Test

class TypedDataFrameTests {
    val df = dataFrameOf("name", "age")(
            "Alice", 31,
            "Bob", 45,
            "Mark", 20
    )

    // Generated code

    @DataFrameType
    interface Person {
        val name: String
        val age: Int
    }

    val TypedDataFrameRow<Person>.name get() = this["name"] as String
    val TypedDataFrameRow<Person>.age get() = this["age"] as Int
    val TypedDataFrame<Person>.name get() = this["name"] as StringCol
    val TypedDataFrame<Person>.age get() = this["age"] as IntCol

    val typed = df.typed<Person>()

    @Test
    fun `size`() {
        typed.size shouldBe DataFrameSize(df.ncol, df.nrow)
    }

    @Test
    fun `slicing`() {
        val sliced = typed[1..2]
        sliced.nrow shouldBe 2
        sliced[0].name shouldBe typed[1].name
    }

    @Test
    fun `access tracking`(){
        trackColumnAccess {
            typed[2].age
        } shouldBe listOf("age")
    }

    @Test
    fun `indexing`(){
        typed[1].age shouldBe 45
    }

    @Test
    fun `update`(){
        val updated = typed.update { age } with { age*2 }
        updated.age.values.toList() shouldBe (df["age"] * 2).values().toList()
    }

    @Test
    fun `groupBy`(){
        typed.groupBy {age}.summarize {
            //count As ""
        }
    }
}
package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.junit.Test

class SchemaTests {
    @Test
    fun `columns order test`() {
        val row = dataFrameOf("c", "b")(4, 5).first()
        val df = dataFrameOf("abc", "a", "a123", "nested")(1, 2, 3, row).cast<Schema>()
        df.schema().toString() shouldBe df.compileTimeSchema().toString()
    }
}

private interface Schema {
    val a: Int
    val abc: Int
    val a123: Int
    val nested: DataRow<Nested>
}

private interface Nested {
    val b: Int
    val c: Int
}

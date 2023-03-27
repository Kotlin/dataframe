package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.junit.Ignore
import org.junit.Test

class JoinTests {

    @Ignore
    @Test
    fun `left join frame column`() {
        val df1 = dataFrameOf("a")(1, 2)
        val df2 = dataFrameOf("a", "b")(
            1, dataFrameOf("c")(3),
            4, dataFrameOf("c")(5)
        )
        val df = df1.leftJoin(df2)

        df.rowsCount() shouldBe 2
        df.columnNames() shouldBe listOf("a", "b")
        df["a"] shouldBe df1["a"]
        val b = df["b"]
        b.kind() shouldBe ColumnKind.Frame
        b.hasNulls() shouldBe false
        val f = b.asAnyFrameColumn()
        f[0] shouldBe df2["b"][0]
        f[1].shouldNotBeNull()
        f[1].isEmpty() shouldBe true
    }
}

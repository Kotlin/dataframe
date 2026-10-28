package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class JoinTests {

    @Test
    fun `left join frame column`() {
        val df1 = dataFrameOf("a")(1, 2)
        val df2 = dataFrameOf("a", "b")(
            1, dataFrameOf("c")(3),
            4, dataFrameOf("c")(5),
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

    // https://github.com/Kotlin/dataframe/issues/2039
    @Test
    fun `join with unmatched rows fills frame column with empty frames of the same schema`() {
        val left = (0..10).toDataFrame {
            "id" from { it }
        }
        val right = (0..5).toDataFrame {
            "id" from { it }
            "frame" from {
                (0..10).toDataFrame {
                    "col" from { it }
                }
            }
        }
        val frameSchema = right["frame"].asAnyFrameColumn().schema.value

        val leftJoined = left.leftJoin(right)
        leftJoined.rowsCount() shouldBe 11
        val leftFrames = leftJoined["frame"].asAnyFrameColumn().toList()
        leftFrames.sumOf { it.nrow } shouldBe 6 * 11
        leftFrames.count { it.isEmpty() } shouldBe 5
        leftFrames.forEach { it.schema() shouldBe frameSchema }
        leftJoined.toString()

        val fullJoined = right.fullJoin(left)
        fullJoined.rowsCount() shouldBe 11
        val fullFrames = fullJoined["frame"].asAnyFrameColumn().toList()
        fullFrames.sumOf { it.nrow } shouldBe 6 * 11
        fullFrames.count { it.isEmpty() } shouldBe 5
        fullFrames.forEach { it.schema() shouldBe frameSchema }
        fullJoined.toString()
    }

    @Test
    fun `join without matched rows keeps the schema of the frame column`() {
        val left = dataFrameOf("id")(100, 101)
        val right = (0..5).toDataFrame {
            "id" from { it }
            "frame" from {
                (0..3).toDataFrame {
                    "col" from { it }
                }
            }
        }

        val frames = left.leftJoin(right)["frame"].asAnyFrameColumn()
        frames.toList().all { it.isEmpty() } shouldBe true
        frames.schema.value.columns.keys shouldBe setOf("col")
    }
}

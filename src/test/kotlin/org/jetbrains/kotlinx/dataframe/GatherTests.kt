package org.jetbrains.kotlinx.dataframe

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.explodeLists
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.valuesInto
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.withValues
import org.jetbrains.kotlinx.dataframe.codeGen.generateCode
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.junit.Test
import kotlin.reflect.typeOf

class GatherTests {

    //region Data Source

    @Language("json")
    val df = """
            [
                {
                    "name": "abc",
                    "normal": {
                        "c1": "a",
                        "c2": "b",
                        "c3": "c"
                    },
                    "reversed": {
                        "c1": "c",
                        "c2": "b",
                        "c3": "a"
                    },
                    "first": {
                        "c1": "c"
                    }
                },
                {
                    "name": "qw",
                    "normal": {
                        "c1": "q",
                        "c2": "w"
                    },
                    "reversed": {
                        "c1": "w",
                        "c2": "q"
                    },
                    "first": {
                        "c1": "q"
                    }
                }
            ]
        """.let {
        DataFrame.readJsonStr(it)
    }

    //endregion

    val generatedCode = df.generateCode("Marker")

    //region Generated code

    @DataSchema(isOpen = false)
    interface Marker1 {
        val c1: String
        val c2: String
        val c3: String?
    }
    val ColumnsContainer<Marker1>.c1: DataColumn<String>
        @JvmName("Marker1_c1")
        get() = this["c1"] as DataColumn<String>
    val DataRow<Marker1>.c1: String
        @JvmName("Marker1_c1")
        get() = this["c1"] as String
    val ColumnsContainer<Marker1>.c2: DataColumn<String>
        @JvmName("Marker1_c2")
        get() = this["c2"] as DataColumn<String>
    val DataRow<Marker1>.c2: String
        @JvmName("Marker1_c2")
        get() = this["c2"] as String
    val ColumnsContainer<Marker1>.c3: DataColumn<String?>
        @JvmName("Marker1_c3")
        get() = this["c3"] as DataColumn<String?>
    val DataRow<Marker1>.c3: String?
        @JvmName("Marker1_c3")
        get() = this["c3"] as String?

    @DataSchema(isOpen = false)
    interface Marker2 {
        val c1: String
        val c2: String
        val c3: String?
    }
    val ColumnsContainer<Marker2>.c1: DataColumn<String> @JvmName("Marker2_c1") get() = this["c1"] as DataColumn<String>
    val DataRow<Marker2>.c1: String @JvmName("Marker2_c1") get() = this["c1"] as String
    val ColumnsContainer<Marker2>.c2: DataColumn<String> @JvmName("Marker2_c2") get() = this["c2"] as DataColumn<String>
    val DataRow<Marker2>.c2: String @JvmName("Marker2_c2") get() = this["c2"] as String
    val ColumnsContainer<Marker2>.c3: DataColumn<String?> @JvmName("Marker2_c3") get() = this["c3"] as DataColumn<String?>
    val DataRow<Marker2>.c3: String? @JvmName("Marker2_c3") get() = this["c3"] as String?
    @DataSchema(isOpen = false)
    interface Marker3 {
        val c1: String
    }
    val ColumnsContainer<Marker3>.c1: DataColumn<String> @JvmName("Marker3_c1") get() = this["c1"] as DataColumn<String>
    val DataRow<Marker3>.c1: String @JvmName("Marker3_c1") get() = this["c1"] as String
    @DataSchema
    interface Marker {
        val name: String
        val normal: DataRow<Marker1>
        val reversed: DataRow<Marker2>
        val first: DataRow<Marker3>
    }
    val ColumnsContainer<Marker>.first: ColumnGroup<*> @JvmName("Marker_first") get() = this["first"] as ColumnGroup<*>
    val DataRow<Marker>.first: AnyRow @JvmName("Marker_first") get() = this["first"] as AnyRow
    val ColumnsContainer<Marker>.name: DataColumn<String> @JvmName("Marker_name") get() = this["name"] as DataColumn<String>
    val DataRow<Marker>.name: String @JvmName("Marker_name") get() = this["name"] as String
    val ColumnsContainer<Marker>.normal: ColumnGroup<*> @JvmName("Marker_normal") get() = this["normal"] as ColumnGroup<*>
    val DataRow<Marker>.normal: AnyRow @JvmName("Marker_normal") get() = this["normal"] as AnyRow
    val ColumnsContainer<Marker>.reversed: ColumnGroup<*> @JvmName("Marker_reversed") get() = this["reversed"] as ColumnGroup<*>
    val DataRow<Marker>.reversed: AnyRow @JvmName("Marker_reversed") get() = this["reversed"] as AnyRow

    //endregion

    val typed = df.cast<Marker>()

    @Test
    fun gather() {
        val mode by column<String>()
        val temp by column<String>()
        val gathered = typed.gather { except(name) }.cast<String>().into(mode, temp).ungroup(temp)

        val expected = typed.groupBy { name }.mapGroups {
            val cols = columns().drop(1).map { it.asColumnGroup() } // drop 'name' column
            val dataRows = cols.map { it[0] }

            val newDf = listOf(
                name.withValues(List(cols.size) { name[0] }),
                mode.withValues(cols.map { it.name }),
                dataRows.map { it.tryGet("c1") as? String }.toColumn("c1", Infer.Type),
                dataRows.map { it.tryGet("c2") as? String }.toColumn("c2", Infer.Type),
                column("c3", dataRows.map { it.tryGet("c3") as? String })
            ).toDataFrame()

            newDf
        }.concat()

        gathered shouldBe expected
    }

    @Test
    fun `generated code is fully typed`() {
        generatedCode.contains("<*>") shouldBe false
    }

    @Test
    fun `gather column group`() {
        val java by columnOf(1, 2, 3)
        val kotlin by columnOf(1, 2, 3)
        val languages by column<DataRow<Unit>>()

        val df = dataFrameOf(java, kotlin).group { java and kotlin }.into("languages")

        fun AnyFrame.check() {
            this["value"].kind shouldBe ColumnKind.Group
            ncol() shouldBe 2
            nrow() shouldBe 3
        }

        df.gather { languages }.into("key", "value").check()
    }

    @Test
    fun `gather mix of columns`() {
        val a by columnOf(1, 1.1)
        val b by columnOf(2, 2.2)

        val df = dataFrameOf(a, b)[0..0]

        val gathered = df.gather { a and b }
            .into("key", "value")

        gathered["value"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `gather values`() {
        val a by columnOf(1, 2)
        val b by columnOf(3, 4)

        var df = dataFrameOf(a, b).gather { a and b }.valuesInto("data")
        df.ncol() shouldBe 1
        df["data"].values() shouldBe listOf(1, 3, 2, 4)

        df = dataFrameOf(a, b).gather { a and b }.where { it % 2 == 1 }.valuesInto("data")
        df.ncol() shouldBe 1
        df["data"].values() shouldBe listOf(1, 3)
    }

    @Test
    fun `gather explode lists`() {
        val a by columnOf(1, 2)
        val b by columnOf(listOf(3, 4), listOf(5, 6))

        val df = dataFrameOf(a, b).gather { a and b }
            .explodeLists()
            .cast<Int>()
            .where { it % 2 == 1 }
            .into("key", "value")

        df shouldBe dataFrameOf("key", "value")(
            "a", 1,
            "b", 3,
            "b", 5
        )
    }
}

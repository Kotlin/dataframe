package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Ignore
import org.junit.Test
import kotlin.reflect.typeOf

class CreateDataFrameTests {

    @Test
    fun `visibility test`() {
        class Data {
            private val a = 1
            protected val b = 2
            internal val c = 3
            public val d = 4
        }

        listOf(Data()).toDataFrame() shouldBe dataFrameOf("d")(4)
    }

    @Test
    fun `exception test`() {
        class Data {
            val a: Int get() = error("Error")
            val b = 1
        }

        val df = listOf(Data()).toDataFrame()
        df.columnsCount() shouldBe 2
        df.rowsCount() shouldBe 1
        df.columnTypes() shouldBe listOf(typeOf<IllegalStateException>(), typeOf<Int>())
        (df["a"][0] is IllegalStateException) shouldBe true
        df["b"][0] shouldBe 1
    }

    @Test
    fun `create frame column`() {
        val df = dataFrameOf("a")(1)
        val res = listOf(1, 2).toDataFrame {
            "a" from { it }
            "b" from { df }
            "c" from { df[0] }
            "d" from { if (it == 1) it else null }
            "e" from { if (true) it else null }
        }
        res["a"].kind shouldBe ColumnKind.Value
        res["a"].type() shouldBe typeOf<Int>()
        res["b"].kind shouldBe ColumnKind.Frame
        res["c"].kind shouldBe ColumnKind.Group
        res["d"].type() shouldBe typeOf<Int?>()
        res["e"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `preserve fields order`() {
        class B(val x: Int, val c: String, d: Double) {
            val b: Int = x
            val a: Double = d
        }

        listOf(B(1, "a", 2.0)).toDataFrame().columnNames() shouldBe listOf("x", "c", "a", "b")
    }

    @DataSchema
    data class A(val v: Int)

    @DataSchema
    data class B(val str: String, val frame: DataFrame<A>, val row: DataRow<A>, val list: List<A>, val a: A)

    @Test
    fun `preserve properties test`() {
        val d1 = listOf(A(2), A(3)).toDataFrame()
        val d2 = listOf(A(4), A(5)).toDataFrame()

        val data = listOf(
            B("q", d1, d1[0], emptyList(), A(7)),
            B("w", d2, d2[1], listOf(A(6)), A(8))
        )

        val df = data.toDataFrame()

        df.frame.kind shouldBe ColumnKind.Frame
        df.row.kind() shouldBe ColumnKind.Group
        df.list.kind shouldBe ColumnKind.Frame
        df.a.kind() shouldBe ColumnKind.Group

        df.str[1] shouldBe "w"
        df.frame[0].v[1] shouldBe 3
        df.row[1].v shouldBe 5
        df.list[1].v[0] shouldBe 6
        df.a[0].v shouldBe 7

        val df2 = data.toDataFrame { preserve(B::row); properties { preserve(DataFrame::class) } }
        df2.frame.kind shouldBe ColumnKind.Value
        df2.frame.type shouldBe typeOf<DataFrame<A>>()
        df2["row"].kind shouldBe ColumnKind.Value
        df2["row"].type shouldBe typeOf<DataRow<A>>()
        df2.list.kind shouldBe ColumnKind.Frame
        df2.a.kind() shouldBe ColumnKind.Group
    }

    enum class DummyEnum { A }

    @Test
    fun `don't convert value types`() {
        data class Entry(val a: Int, val b: String, val c: Boolean, val e: DummyEnum)

        val df = listOf(Entry(1, "s", true, DummyEnum.A)).toDataFrame(maxDepth = 100)
        df.columns().forEach {
            it.kind shouldBe ColumnKind.Value
        }
    }

    @Test
    fun `convert type with no properties`() {
        class Child
        class Entry(val a: Int, val child: Child)

        val df = listOf(Entry(1, Child())).toDataFrame(maxDepth = 100)
        df.rowsCount() shouldBe 1

        val childCol = df[Entry::child]
        childCol.kind() shouldBe ColumnKind.Group
        childCol.asColumnGroup().columnsCount() shouldBe 0
    }

    @Test
    fun `convert child schemas`() {
        class Child2(val s: String)

        @DataSchema
        class Child1(val child: Child2)

        @DataSchema
        class Entry(val a: Int, val child: Child1)

        val df = listOf(Entry(1, Child1(Child2("s")))).toDataFrame()
        df.rowsCount() shouldBe 1

        val child1 = df[Entry::child]
        child1.kind shouldBe ColumnKind.Group

        val child2 = child1.asColumnGroup()[Child1::child]
        child2.kind shouldBe ColumnKind.Value
    }

    @Test
    fun inferredTypeForPropertyWithGenericIterableType() {
        class Container<E>(val data: Set<E>)

        val element = Container(setOf(1))
        val value = listOf(element).toDataFrame(maxDepth = 10)

        value["data"].type() shouldBe typeOf<List<Int>>()
    }

    @Test
    fun inferredNullableTypeForPropertyWithGenericIterableType() {
        class Container<E>(val data: List<E>)

        val element = Container(listOf(1, null))
        val value = listOf(element).toDataFrame(maxDepth = 10)

        value["data"].type() shouldBe typeOf<List<Int?>>()
    }

    @Suppress("unused")
    @Test
    fun treatErasedGenericAsAny() {
        class IncompatibleVersionErrorData<T>(val expected: T, val actual: T)
        class DeserializedContainerSource(val incompatibility: IncompatibleVersionErrorData<*>)
        val functions = listOf(DeserializedContainerSource(IncompatibleVersionErrorData(1, 2)))

        val df = functions.toDataFrame(maxDepth = 2)

        val col = df.getColumnGroup(DeserializedContainerSource::incompatibility)
        col[IncompatibleVersionErrorData<*>::actual].type() shouldBe typeOf<Any>()
        col[IncompatibleVersionErrorData<*>::expected].type() shouldBe typeOf<Any>()
    }

    interface Named {
        val name: String get() = "default impl"
    }

    class Data(override val name: String) : Named

    @Test
    fun simpleInheritance() {
        val name = "temp"
        val df = listOf(Data(name)).toDataFrame(maxDepth = 1)

        df["name"][0] shouldBe name
    }

    @Test
    fun builtInTypes() {
        val string = listOf("aaa", "aa", null)
        string.toDataFrame().also { it.print() } shouldBe dataFrameOf("value")(*string.toTypedArray())

        val int = listOf(1, 2, 3)
        int.toDataFrame().alsoDebug() shouldBe dataFrameOf("value")(*int.toTypedArray())
    }

    @Ignore
    @Test
    fun generateBuiltInsOverrides() {
        listOf("Byte", "Short", "Int", "Long", "String", "Char", "Boolean", "UByte", "UShort", "UInt", "ULong").forEach { type ->
            val typeParameter = type.first()
            val func = """
            @JvmName("toDataFrame$type")
            public inline fun <reified $typeParameter : $type?> Iterable<$typeParameter>.toDataFrame(): DataFrame<ValueProperty<$typeParameter>> = toDataFrame {
                ValueProperty<$typeParameter>::value from { it }
            }.cast()
            """.trimIndent()
            println(func)
            println()
        }
    }
}

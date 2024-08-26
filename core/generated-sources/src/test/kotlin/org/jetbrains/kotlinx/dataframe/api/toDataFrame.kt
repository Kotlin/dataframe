package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.util.ANY
import org.jetbrains.kotlinx.dataframe.util.INT
import org.jetbrains.kotlinx.dataframe.util.NULLABLE_INT
import org.jetbrains.kotlinx.dataframe.util.NUMBER
import org.jetbrains.kotlinx.dataframe.util.STRING
import org.junit.Ignore
import org.junit.Test
import java.io.File
import kotlin.reflect.KProperty
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
        df.columnTypes() shouldBe listOf(typeOf<IllegalStateException>(), INT)
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
        res["a"].type() shouldBe INT
        res["b"].kind shouldBe ColumnKind.Frame
        res["c"].kind shouldBe ColumnKind.Group
        res["d"].type() shouldBe NULLABLE_INT
        res["e"].type() shouldBe INT
    }

    @Test
    fun `create column with infer type`() {
        val data: List<Any> = listOf(1, 2, 3)
        val res = data.toDataFrame {
            "e" from inferType { it }
            expr(infer = Infer.Type) { it } into "d"
        }

        res["e"].type() shouldBe INT
        res["e"].kind() shouldBe ColumnKind.Value

        res["d"].type() shouldBe INT
        res["d"].kind() shouldBe ColumnKind.Value
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
    data class B(
        val str: String,
        val frame: DataFrame<A>,
        val row: DataRow<A>,
        val list: List<A>,
        val a: A,
    )

    @Test
    fun `preserve properties test`() {
        val d1 = listOf(A(2), A(3)).toDataFrame()
        val d2 = listOf(A(4), A(5)).toDataFrame()

        val data = listOf(
            B("q", d1, d1[0], emptyList(), A(7)),
            B("w", d2, d2[1], listOf(A(6)), A(8)),
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

        val df2 = data.toDataFrame {
            preserve(B::row)
            properties { preserve(DataFrame::class) }
        }
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
        data class Entry(
            val a: Int,
            val b: String,
            val c: Boolean,
            val e: DummyEnum,
        )

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
        col[IncompatibleVersionErrorData<*>::actual].type() shouldBe ANY
        col[IncompatibleVersionErrorData<*>::expected].type() shouldBe ANY
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
        string.toDataFrame() shouldBe dataFrameOf("value")(*string.toTypedArray())

        val int = listOf(1, 2, 3)
        int.toDataFrame() shouldBe dataFrameOf("value")(*int.toTypedArray())

        val doubles = listOf(1.0, 2.0, 3.0)
        doubles.toDataFrame() shouldBe dataFrameOf("value")(*doubles.toTypedArray())

        val floats = listOf(1.0f, 2.0f, 3.0f)
        floats.toDataFrame() shouldBe dataFrameOf("value")(*floats.toTypedArray())
    }

    @Ignore
    @Test
    fun generateBuiltInsOverrides() {
        listOf(
            "Byte",
            "Short",
            "Int",
            "Long",
            "String",
            "Char",
            "Boolean",
            "UByte",
            "UShort",
            "UInt",
            "ULong",
        ).forEach { type ->
            val typeParameter = type.first()
            val func =
                """
                @JvmName("toDataFrame$type")
                public inline fun <reified $typeParameter : $type?> Iterable<$typeParameter>.toDataFrame(): DataFrame<ValueProperty<$typeParameter>> = toDataFrame {
                    ValueProperty<$typeParameter>::value from { it }
                }.cast()
                """.trimIndent()
            println(func)
            println()
        }
    }

    // nullable field here - no generated unwrapping code
    @JvmInline
    internal value class Speed(val kmh: Number?)

    internal class PathSegment(val id: String, val speedLimit: Speed? = null)

    @Test
    fun valueClassNullableField() {
        val segments = listOf(PathSegment("foo", Speed(2.3)), PathSegment("bar"))

        val df = segments.toDataFrame()
        df["speedLimit"].values() shouldBe listOf(Speed(2.3), null)
    }

    @Test
    fun valueClassNullableField1() {
        val segments = listOf(PathSegment("foo", Speed(2.3)), PathSegment("bar", Speed(null)))

        val df = segments.toDataFrame()
        df["speedLimit"].values() shouldBe listOf(Speed(2.3), Speed(null))
    }

    @JvmInline
    internal value class Speed1(val kmh: Number)

    internal class PathSegment1(val id: String, val speedLimit: Speed1? = null)

    @Test
    fun valueClass() {
        val segments = listOf(PathSegment1("foo", Speed1(2.3)), PathSegment1("bar"))

        val df = segments.toDataFrame()
        df["speedLimit"].values() shouldBe listOf(Speed1(2.3), null)
    }

    @Test
    fun testKPropertyGet() {
        val segment = PathSegment("bar")
        val result = PathSegment::speedLimit.call(segment)
        result shouldBe null
    }

    fun call(kProperty0: KProperty<*>, obj: Any) = kProperty0.call(obj)

    @Test
    fun testKPropertyCallLibrary() {
        val segment = PathSegment1("bar")
        val result = call(PathSegment1::speedLimit, segment)
        // Sudden result! I cannot create this value, so toString.
        // In the test above you can see decompiled code that "fixes" this strange wrapping
        result.toString() shouldBe "Speed1(kmh=null)"
    }

    private class PrivateClass(val a: Int)

    @Test
    fun `convert private class with public members`() {
        listOf(PrivateClass(1)).toDataFrame() shouldBe dataFrameOf("a")(1)
    }

    class KotlinPojo {

        private var a: Int = 0
        private var b: String = ""

        constructor(b: String, a: Int) {
            this.a = a
            this.b = b
        }

        fun getA(): Int = a

        fun setA(a: Int) {
            this.a = a
        }

        fun getB(): String = b

        fun setB(b: String) {
            this.b = b
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is KotlinPojo) return false

            if (a != other.a) return false
            if (b != other.b) return false

            return true
        }

        override fun hashCode(): Int {
            var result = a
            result = 31 * result + b.hashCode()
            return result
        }

        override fun toString(): String = "FakePojo(a=$a, b='$b')"
    }

    @Test
    fun `convert POJO to DF`() {
        // even though the names b, a, follow the constructor order
        listOf(KotlinPojo("bb", 1)).toDataFrame() shouldBe dataFrameOf("b", "a")("bb", 1)

        // cannot read java constructor parameter names with reflection, so sort lexicographically
        listOf(JavaPojo(2.0, null, "bb", 1)).toDataFrame() shouldBe
            dataFrameOf(
                DataColumn.createValueColumn("a", listOf(1), INT),
                DataColumn.createValueColumn("b", listOf("bb"), STRING),
                DataColumn.createValueColumn("c", listOf(null), NULLABLE_INT),
                DataColumn.createValueColumn("d", listOf(2.0), NUMBER),
            )

        listOf(KotlinPojo("bb", 1)).toDataFrame { properties(KotlinPojo::getA) } shouldBe
            dataFrameOf("a")(1)
        listOf(KotlinPojo("bb", 1)).toDataFrame { properties(KotlinPojo::getB) } shouldBe
            dataFrameOf("b")("bb")

        listOf(JavaPojo(2.0, 3, "bb", 1)).toDataFrame {
            properties(JavaPojo::getA)
        } shouldBe dataFrameOf("a")(1)

        listOf(JavaPojo(2.0, 3, "bb", 1)).toDataFrame {
            properties(JavaPojo::getB)
        } shouldBe dataFrameOf("b")("bb")
    }

    data class Arrays(val a: IntArray, val b: Array<Int>, val c: Array<Int?>)

    @Test
    fun `arrays in to DF`() {
        val df = listOf(
            Arrays(intArrayOf(1, 2), arrayOf(3, 4), arrayOf(5, null)),
        ).toDataFrame(maxDepth = Int.MAX_VALUE)

        df.schema() shouldBe dataFrameOf(
            DataColumn.createValueColumn("a", listOf(intArrayOf(1, 2)), typeOf<IntArray>()),
            DataColumn.createValueColumn("b", listOf(arrayOf(3, 4)), typeOf<Array<Int>>()),
            DataColumn.createValueColumn("c", listOf(arrayOf(5, null)), typeOf<Array<Int?>>()),
        ).schema()
    }

    @DataSchema
    data class Person(
        val firstName: String,
        val lastName: String,
        val age: Int,
        val city: String?,
    ) : DataRowSchema

    @DataSchema
    data class Group(val id: String, val participants: List<Person>) : DataRowSchema

    @Test
    fun `deeply convert data schema and list of data schema`() {
        val participants1 = listOf(
            Person("Alice", "Cooper", 15, "London"),
            Person("Bob", "Dylan", 45, "Dubai"),
        )
        val participants2 = listOf(
            Person("Charlie", "Daniels", 20, "Moscow"),
            Person("Charlie", "Chaplin", 40, "Milan"),
        )
        val df = dataFrameOf(
            Group("1", participants1),
            Group("2", participants2),
        )
        shouldNotThrowAny {
            df.participants[0].firstName
            df.participants[0].city
        }
    }

    @Test
    fun toDataFrameColumn() {
        val files = listOf(File("data.csv"))
        val df = files.toDataFrame(columnName = "files")
        df["files"][0] shouldBe File("data.csv")
    }
}

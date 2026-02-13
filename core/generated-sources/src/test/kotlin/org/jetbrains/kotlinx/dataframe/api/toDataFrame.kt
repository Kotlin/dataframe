package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.time.temporal.Temporal
import kotlin.collections.toTypedArray
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
    fun `create column with infer type`() {
        val data: List<Any> = listOf(1, 2, 3)
        val res = data.toDataFrame {
            "e" from inferType { it }
            expr(infer = Infer.Type) { it } into "d"
        }

        res["e"].type() shouldBe typeOf<Int>()
        res["e"].kind() shouldBe ColumnKind.Value

        res["d"].type() shouldBe typeOf<Int>()
        res["d"].kind() shouldBe ColumnKind.Value
    }

    @Test
    fun `preserve fields order`() {
        // constructor properties will keep order, so x, c
        class B(val x: Int, val c: String, d: Double) {
            // then child properties will be sorted lexicographically by column name, so a, b
            val b: Int = x
            val a: Double = d
        }

        listOf(B(1, "a", 2.0)).toDataFrame().columnNames() shouldBe listOf("x", "c", "a", "b")
    }

    @Test
    fun `preserve fields order with @ColumnName`() {
        // constructor properties will keep order, so z, y
        class B(
            @ColumnName("z") val x: Int,
            @ColumnName("y") val c: String,
            d: Double,
        ) {
            // then child properties will be sorted lexicographically by column name, so w, x
            @ColumnName("x")
            val a: Double = d

            @ColumnName("w")
            val b: Int = x
        }

        listOf(B(1, "a", 2.0)).toDataFrame().columnNames() shouldBe listOf("z", "y", "w", "x")
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
            properties {
                preserve(DataFrame::class)
            }
        }
        df2.frame.kind shouldBe ColumnKind.Value
        df2.frame.type shouldBe typeOf<DataFrame<A>>()
        df2["row"].kind shouldBe ColumnKind.Value
        df2["row"].type shouldBe typeOf<DataRow<A>>()
        df2.list.kind shouldBe ColumnKind.Frame
        df2.a.kind() shouldBe ColumnKind.Group
    }

    class ExcludeTestData(val s: String, val data: NestedData)

    class NestedData(val i: Int, val nestedStr: String)

    @Test
    fun `preserve T test`() {
        val data = listOf(
            ExcludeTestData("test", NestedData(42, "nested")),
            ExcludeTestData("test2", NestedData(43, "nested2")),
        )

        val res = data.toDataFrame {
            preserve<NestedData>()
            properties(maxDepth = 2)
        }

        res.schema() shouldBe data.toDataFrame(maxDepth = 0).schema()
    }

    class NestedExcludeClasses(val s: String, val list1: List<String>)

    class ExcludeClasses(val i: Int, val list: List<Int>, val nested: NestedExcludeClasses)

    @Test
    fun `exclude classes`() {
        val list = listOf(
            ExcludeClasses(1, listOf(1, 2, 3), NestedExcludeClasses("str", listOf("foo", "bar"))),
        )
        val df = list.toDataFrame {
            properties(maxDepth = 2) {
                exclude(List::class)
            }
        }

        df shouldBe list.toDataFrame(maxDepth = 2).remove { "list" and "nested"["list1"] }
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
        childCol.kind() shouldBe ColumnKind.Value
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
    fun `should convert iterables of primitive arrays to DataFrame with value column`() {
        val intArrays = listOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6))
        intArrays.toDataFrame() shouldBe dataFrameOf("value")(*intArrays.toTypedArray())

        val doubleArrays = listOf(doubleArrayOf(1.1, 2.2), doubleArrayOf(3.3, 4.4))
        doubleArrays.toDataFrame() shouldBe dataFrameOf("value")(*doubleArrays.toTypedArray())

        val booleanArrays = listOf(booleanArrayOf(true, false), booleanArrayOf(false, true))
        booleanArrays.toDataFrame() shouldBe dataFrameOf("value")(*booleanArrays.toTypedArray())
    }

    @Test
    fun `should convert iterables of built-in primitive types to DataFrame with value column`() {
        val bytes: List<Byte?> = listOf(1, 2, null)
        bytes.toDataFrame() shouldBe dataFrameOf("value")(*bytes.toTypedArray())

        val shorts: List<Short?> = listOf(10, 20, null)
        shorts.toDataFrame() shouldBe dataFrameOf("value")(*shorts.toTypedArray())

        val ints: List<Int?> = listOf(100, 200, null)
        ints.toDataFrame() shouldBe dataFrameOf("value")(*ints.toTypedArray())

        val longs: List<Long?> = listOf(1000L, 2000L, null)
        longs.toDataFrame() shouldBe dataFrameOf("value")(*longs.toTypedArray())

        val floats: List<Float?> = listOf(1.1f, 2.2f, null)
        floats.toDataFrame() shouldBe dataFrameOf("value")(*floats.toTypedArray())

        val doubles: List<Double?> = listOf(1.1, 2.2, null)
        doubles.toDataFrame() shouldBe dataFrameOf("value")(*doubles.toTypedArray())

        val booleans: List<Boolean?> = listOf(true, false, null)
        booleans.toDataFrame() shouldBe dataFrameOf("value")(*booleans.toTypedArray())

        val chars: List<Char?> = listOf('A', 'B', null)
        chars.toDataFrame() shouldBe dataFrameOf("value")(*chars.toTypedArray())

        val strings: List<String?> = listOf("hello", "world", null)
        strings.toDataFrame() shouldBe dataFrameOf("value")(*strings.toTypedArray())
    }

    @Test
    fun `should convert iterables of unsigned types to DataFrame with value column`() {
        val ubytes: List<UByte?> = listOf(1u, 2u, null)
        ubytes.toDataFrame() shouldBe dataFrameOf("value")(*ubytes.toTypedArray())

        val ushorts: List<UShort?> = listOf(100u, 200u, null)
        ushorts.toDataFrame() shouldBe dataFrameOf("value")(*ushorts.toTypedArray())

        val uints: List<UInt?> = listOf(1000u, 2000u, null)
        uints.toDataFrame() shouldBe dataFrameOf("value")(*uints.toTypedArray())

        val ulongs: List<ULong?> = listOf(10000u, 20000u, null)
        ulongs.toDataFrame() shouldBe dataFrameOf("value")(*ulongs.toTypedArray())
    }

    @Test
    fun `should convert iterables of BigDecimal and BigInteger to DataFrame with value column`() {
        val bigDecimals: List<BigDecimal?> = listOf(BigDecimal("1.1"), BigDecimal("2.2"), null)
        bigDecimals.toDataFrame() shouldBe dataFrameOf("value")(*bigDecimals.toTypedArray())

        val bigIntegers: List<BigInteger?> = listOf(BigInteger("100"), BigInteger("200"), null)
        bigIntegers.toDataFrame() shouldBe dataFrameOf("value")(*bigIntegers.toTypedArray())
    }

    @Test
    fun `should convert iterables of java time types to DataFrame with value column`() {
        val localDates: List<java.time.LocalDate?> = listOf(java.time.LocalDate.of(2024, 2, 28), null)
        localDates.toDataFrame() shouldBe dataFrameOf("value")(*localDates.toTypedArray())

        val localDateTimes: List<java.time.LocalDateTime?> =
            listOf(java.time.LocalDateTime.of(2024, 2, 28, 14, 30), null)
        localDateTimes.toDataFrame() shouldBe dataFrameOf("value")(*localDateTimes.toTypedArray())

        val localTimes: List<java.time.LocalTime?> = listOf(java.time.LocalTime.of(14, 30), null)
        localTimes.toDataFrame() shouldBe dataFrameOf("value")(*localTimes.toTypedArray())

        val instants: List<java.time.Instant?> = listOf(java.time.Instant.parse("2024-02-28T14:30:00Z"), null)
        instants.toDataFrame() shouldBe dataFrameOf("value")(*instants.toTypedArray())

        val durations: List<java.time.Duration?> = listOf(java.time.Duration.ofHours(2), null)
        durations.toDataFrame() shouldBe dataFrameOf("value")(*durations.toTypedArray())

        val temporals: List<Temporal?> = listOf(
            java.time.LocalDate.of(2024, 2, 28),
            java.time.LocalDateTime.of(2024, 2, 28, 14, 30),
            null,
        )
        temporals.toDataFrame() shouldBe dataFrameOf("value")(*temporals.toTypedArray())
    }

    @Test
    fun `should convert iterables of kotlinx datetime types to DataFrame with value column`() {
        val localDates: List<LocalDate?> = listOf(LocalDate(2024, 2, 28), null)
        localDates.toDataFrame() shouldBe dataFrameOf("value")(*localDates.toTypedArray())

        val localDateTimes: List<LocalDateTime?> = listOf(LocalDateTime(2024, 2, 28, 14, 30, 0), null)
        localDateTimes.toDataFrame() shouldBe dataFrameOf("value")(*localDateTimes.toTypedArray())

        val instants: List<Instant?> = listOf(Instant.parse("2024-02-28T14:30:00Z"), null)
        instants.toDataFrame() shouldBe dataFrameOf("value")(*instants.toTypedArray())

        val timeZones: List<TimeZone?> = listOf(TimeZone.of("UTC"), TimeZone.of("Europe/Moscow"), null)
        timeZones.toDataFrame() shouldBe dataFrameOf("value")(*timeZones.toTypedArray())

        val months: List<Month?> = listOf(Month.JANUARY, Month.FEBRUARY, null)
        months.toDataFrame() shouldBe dataFrameOf("value")(*months.toTypedArray())

        val daysOfWeek: List<DayOfWeek?> = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, null)
        daysOfWeek.toDataFrame() shouldBe dataFrameOf("value")(*daysOfWeek.toTypedArray())

        val dateTimePeriods: List<DateTimePeriod?> = listOf(DateTimePeriod(years = 1, months = 2), null)
        dateTimePeriods.toDataFrame().convert("value") { it.toString() } shouldBe
            dataFrameOf("value")("P1Y2M", "null")

        val dateTimeUnits: List<DateTimeUnit?> = listOf(DateTimeUnit.MonthBased(3), DateTimeUnit.DayBased(7), null)
        dateTimeUnits.toDataFrame()
            .convert("value") { it.toString().trim() } shouldBe
            dataFrameOf("value")("QUARTER", "WEEK", "null")
    }

    enum class TestEnum { VALUE_ONE, VALUE_TWO }

    @Test
    fun `should convert iterables of Enum to DataFrame with value column`() {
        val enums: List<TestEnum?> = listOf(TestEnum.VALUE_ONE, TestEnum.VALUE_TWO, null)
        enums.toDataFrame() shouldBe dataFrameOf("value")(*enums.toTypedArray())
    }

    @Test
    fun `should convert iterables of non-JSON Map to DataFrame with value column`() {
        val maps: List<Map<*, *>?> = listOf(mapOf(1 to null, 2 to "val"), mapOf(3 to 1, 4 to true), null)
        val df = maps.toDataFrame()
        df.columnNames() shouldBe listOf("value")
        df["value"].toList() shouldBe maps
    }

    @Test
    fun `should convert iterables of maps representing rows to DataFrame with value columns`() {
        val maps: Iterable<Map<String, *>> = listOf(mapOf("a" to 1, "b" to true), mapOf("c" to 2, "d" to false))
        val df = maps.toDataFrame()
        df["a"][0] shouldBe 1
        df["b"][0] shouldBe true
        df["c"][1] shouldBe 2
        df["d"][1] shouldBe false
        df.columns().all { it.type().isMarkedNullable } shouldBe true
        df["a"].type() shouldBe typeOf<Int?>()
    }

    class NoPublicPropsClass(private val a: Int, private val b: String)

    @Test
    fun `should convert class with no public properties to DataFrame with value column`() {
        val objs: List<NoPublicPropsClass> = listOf(NoPublicPropsClass(1, "a"), NoPublicPropsClass(2, "b"))
        objs.toDataFrame() shouldBe dataFrameOf("value")(*objs.toTypedArray())
    }

    interface Animal {
        fun say(): String
    }

    class Dog(val name: String) : Animal {
        override fun say() = "bark"
    }

    class Cat(val name: String) : Animal {
        override fun say() = "meow"
    }

    @Test
    fun `should convert list of type with no properties to DataFrame with value column`() {
        val animals: List<Animal> = listOf(Dog("dog"), Cat("cat"))
        animals.toDataFrame() shouldBe dataFrameOf("value")(*animals.toTypedArray())
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
                DataColumn.createValueColumn("a", listOf(1), typeOf<Int>()),
                DataColumn.createValueColumn("b", listOf("bb"), typeOf<String>()),
                DataColumn.createValueColumn("c", listOf(null), typeOf<Int?>()),
                DataColumn.createValueColumn("d", listOf(2.0), typeOf<Number>()),
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

    @Test
    fun `convert Java Record to DF`() {
        val record = JavaRecord(42, "test", 3.14)
        val df = listOf(record).toDataFrame()

        df shouldBe dataFrameOf(
            DataColumn.createValueColumn("property1", listOf(42), typeOf<Int>()),
            DataColumn.createValueColumn("property2", listOf("test"), typeOf<String>()),
            DataColumn.createValueColumn("property3", listOf(3.14), typeOf<Double>()),
        )
    }

    @Test
    fun `convert nested Java Record to DF with maxDepth`() {
        val record = JavaRecord(1, "nested", 2.5)
        val wrapper = JavaRecordWrapper("wrapper", record)
        val df = listOf(wrapper).toDataFrame(maxDepth = 2)

        df.columnNames() shouldBe listOf("name", "record")
        df["name"][0] shouldBe "wrapper"

        val recordCol = df.getColumnGroup("record")
        recordCol.columnNames() shouldBe listOf("property1", "property2", "property3")
        recordCol["property1"][0] shouldBe 1
        recordCol["property2"][0] shouldBe "nested"
        recordCol["property3"][0] shouldBe 2.5
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

    class MyEmptyDeclaration

    class TestItem(val name: String, val containingDeclaration: MyEmptyDeclaration, val test: Int)

    @Test
    fun `preserve empty interface consistency`() {
        val df = listOf(MyEmptyDeclaration(), MyEmptyDeclaration()).toDataFrame()
        df["value"].type() shouldBe typeOf<MyEmptyDeclaration>()
    }

    @Test
    fun `preserve nested empty interface consistency`() {
        val df = List(10) {
            TestItem(
                "Test1",
                MyEmptyDeclaration(),
                123,
            )
        }.toDataFrame(maxDepth = 2)

        df["containingDeclaration"].type() shouldBe typeOf<MyEmptyDeclaration>()
    }

    @Test
    fun `preserve value type consistency`() {
        val list = listOf(mapOf("a" to 1))
        val df = list.toDataFrame(maxDepth = 1)
        df["value"].type() shouldBe typeOf<Map<String, Int>>()
    }

    class MapContainer(val map: Map<String, Int>)

    @Test
    fun `preserve nested value type consistency`() {
        val list = listOf(MapContainer(mapOf("a" to 1)))
        val df = list.toDataFrame(maxDepth = 2)
        df["map"].type() shouldBe typeOf<Map<String, Int>>()
    }

    @Test
    fun `parsing row-major lines into structured dataframe`() {
        // I think finding data in such format will be rare, so we need an optional header parameter.
        val lines = buildList {
            addAll(listOf("stamp", "header", "data"))
            repeat(33) { row ->
                add("stamp $row")
                add("header $row")
                add("data $row")
            }
        }

        val df = lines.chunked(3).toDataFrame(header = null)

        df.columnNames() shouldBe listOf("stamp", "header", "data")
        df.columnTypes() shouldBe listOf(typeOf<String>(), typeOf<String>(), typeOf<String>())
        df.rowsCount() shouldBe 33
        df[0].values() shouldBe listOf("stamp 0", "header 0", "data 0")
    }

    @Test
    fun `parsing srt lines into structured dataframe`() {
        // *.srt subtitle file format
        val lines = buildList {
            repeat(33) { row ->
                add("stamp $row")
                add("header $row")
                add("data $row")
                add("\n")
            }
        }

        val df = lines.chunked(4).map { it.dropLast(1) }.toDataFrame(header = listOf("stamp", "header", "data"))

        df.columnNames() shouldBe listOf("stamp", "header", "data")
        df.columnTypes() shouldBe listOf(typeOf<String>(), typeOf<String>(), typeOf<String>())
        df.rowsCount() shouldBe 33
        df[0].values() shouldBe listOf("stamp 0", "header 0", "data 0")

        // Different approach. I think the dropLast one is better
        lines.chunked(4)
            .toDataFrame(header = listOf("stamp", "header", "data", "whitespace"))
            .remove("whitespace") shouldBe df
    }

    @Test
    fun `parsing column-major lines into structured dataframe`() {
        val lines = buildList {
            repeat(4) { col ->
                repeat(5) { row ->
                    add("data$col $row")
                }
                add("\n")
            }
        }

        val header = List(4) { "col $it" }
        val df = lines
            .chunked(6)
            .map { it.dropLast(1) }
            .toDataFrame(header = header, containsColumns = true)
        df.columnNames() shouldBe header
        df.columnTypes() shouldBe List(4) { typeOf<String>() }
        df["col 0"].values() shouldBe listOf("data0 0", "data0 1", "data0 2", "data0 3", "data0 4")
    }

    @Test
    fun `parsing column-major lines with header into structured dataframe`() {
        val lines = buildList {
            repeat(4) { col ->
                add("col $col")
                repeat(5) { row ->
                    add("data$col $row")
                }
                add("\n")
            }
        }

        val header = List(4) { "col $it" }
        val df = lines
            .chunked(7)
            .map { it.dropLast(1) }
            .toDataFrame(header = null, containsColumns = true)
        df.columnNames() shouldBe header
        df.columnTypes() shouldBe List(4) { typeOf<String>() }
        df["col 0"].values() shouldBe listOf("data0 0", "data0 1", "data0 2", "data0 3", "data0 4")
    }
}

package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.exceptions.CellConversionException
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class DataClassesTests {

    @Test
    fun convertDataClasses() {
        data class Record(val sex: String, val grade: Int, val count: Int)

        data class PivotedRecord(val grade: Int, val male: Int, val female: Int)

        listOf(
            Record("male", 5, 10),
            Record("male", 6, 15),
            Record("female", 5, 20),
            Record("female", 6, 15),
        ).toDataFrame()
            .pivot(Record::sex, inward = false).groupBy(Record::grade).values(Record::count)
            .toListOf<PivotedRecord>() shouldBe
            listOf(
                PivotedRecord(5, 10, 20),
                PivotedRecord(6, 15, 15),
            )
    }

    data class Input(val a: Int, val b: Int)

    data class FullName(val name: String, val lastName: String)

    data class Person(val fullName: FullName, val age: Int)

    data class Student(val name: String, val age: Int)

    data class NullableAge(val name: String, val age: Int?)

    data class LongAge(val name: String, val age: Long)

    data class WithDefault(val name: String, val age: Int, val city: String = "London")

    data class Annotated(
        @ColumnName("full name") val name: String,
        val age: Int,
    )

    data class City(val city: String, val students: List<Student>)

    data class TypedCity(val city: String, val students: DataFrame<Student>)

    data class UntypedCity(val city: String, val students: AnyFrame)

    data class LongAgeCity(val city: String, val students: DataFrame<LongAge>)

    data class NotStudent(val x: Int, val y: String)

    data class NotStudentCity(val city: String, val students: DataFrame<NotStudent>)

    data class Box<V>(val value: V)

    data class NullableBox<V>(val value: V?)

    data class ListBox<V>(val items: List<V?>)

    @DataSchema
    interface StudentSchema {
        val name: String
        val age: Int
    }

    class NotADataClass(val name: String, val age: Int)

    // region toList

    // Backs the example in the `toList` KDoc.
    @Test
    fun `KDoc example toList after cast creates one instance per row`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)

        val list: List<Input> = df.cast<Input>().toList()

        list shouldBe listOf(Input(1, 2), Input(3, 4))
    }

    @Test
    fun `toList creates one instance of the type argument per row`() {
        val df = listOf(Input(1, 2), Input(3, 4)).toDataFrame()

        val list: List<Input> = df.toList()

        list shouldBe listOf(Input(1, 2), Input(3, 4))
    }

    @Test
    fun `toList throws on a dataframe whose type argument is unknown`() {
        val df: DataFrame<*> = dataFrameOf("a", "b")(1, 2)

        // `T` resolves to `Any?`, which is not a data class.
        shouldThrow<IllegalArgumentException> { df.toList() }
    }

    @Test
    fun `toList works after casting to a data class`() {
        val df: DataFrame<*> = dataFrameOf("a", "b")(1, 2)

        df.cast<Input>().toList() shouldBe listOf(Input(1, 2))
    }

    @Test
    fun `toList throws when the type argument is a DataSchema interface`() {
        val df = dataFrameOf("name", "age")("Alice", 15).cast<StudentSchema>()

        shouldThrow<IllegalArgumentException> { df.toList() }
    }

    // endregion

    // region toListOf

    // Backs the example in the `toListOf` KDoc.
    @Test
    fun `KDoc example toListOf turns a column group into a nested data class`() {
        val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
            .group("name", "lastName").into("fullName")

        df.toListOf<Person>() shouldBe listOf(Person(FullName("John", "Doe"), 21))
    }

    @Test
    fun `toListOf does not depend on the type argument of the dataframe`() {
        val df: DataFrame<Input> = listOf(Input(1, 2), Input(3, 4)).toDataFrame()

        data class OnlyB(val b: Int)

        df.toListOf<OnlyB>() shouldBe listOf(OnlyB(2), OnlyB(4))
    }

    @Test
    fun `toListOf throws when the class is not a data class`() {
        val df = dataFrameOf("name", "age")("Alice", 15)

        shouldThrow<IllegalArgumentException> { df.toListOf<NotADataClass>() }
    }

    @Test
    fun `toListOf throws when the class is a DataSchema interface`() {
        val df = dataFrameOf("name", "age")("Alice", 15)

        shouldThrow<IllegalArgumentException> { df.toListOf<StudentSchema>() }
    }

    @Test
    fun `columns are matched by name in any order and extra columns are ignored`() {
        val df = dataFrameOf("city", "age", "name")("London", 15, "Alice", "Dubai", 20, "Bob")

        df.toListOf<Student>() shouldBe listOf(Student("Alice", 15), Student("Bob", 20))
    }

    @Test
    fun `column names are case-sensitive`() {
        val df = dataFrameOf("Name", "Age")("Alice", 15)

        shouldThrow<IllegalStateException> { df.toListOf<Student>() }
    }

    @Test
    fun `ColumnName annotation gives the column name for a parameter`() {
        val df = dataFrameOf("full name", "age")("Alice", 15)

        df.toListOf<Annotated>() shouldBe listOf(Annotated("Alice", 15))
    }

    @Test
    fun `Int values are converted to a Long parameter`() {
        // "age" holds `Int` values, `LongAge.age` is a `Long`.
        val ints = dataFrameOf("name", "age")("Alice", 15)
        val longs = ints.toListOf<LongAge>()
        longs shouldBe listOf(LongAge("Alice", 15L))
        longs.single().age::class shouldBe Long::class
    }

    @Test
    fun `String values are converted to an Int parameter`() {
        // "age" holds `String` values, `Student.age` is an `Int`.
        val strings = dataFrameOf("name", "age")("Alice", "15")
        strings.toListOf<Student>() shouldBe listOf(Student("Alice", 15))
    }

    @Test
    fun `frame column becomes a list of data class instances`() {
        val df = dataFrameOf("city", "name", "age")("London", "Alice", 15, "London", "Bob", 20, "Dubai", "Charlie", 30)
            .groupBy("city").toDataFrame("students")

        df.toListOf<City>() shouldBe listOf(
            City("London", listOf(Student("Alice", 15), Student("Bob", 20))),
            City("Dubai", listOf(Student("Charlie", 30))),
        )
    }

    @Test
    fun `missing column throws when the parameter has no default value`() {
        val df = dataFrameOf("name")("Alice")

        shouldThrow<IllegalStateException> { df.toListOf<Student>() }
    }

    @Test
    fun `missing column gives the default value of the parameter`() {
        val df = dataFrameOf("name", "age")("Alice", 15)

        // There is no "city" column, so `city` gets its default value "London".
        df.toListOf<WithDefault>() shouldBe listOf(WithDefault("Alice", 15, "London"))
    }

    @Test
    fun `existing column wins over the default value`() {
        val df = dataFrameOf("name", "age", "city")("Alice", 15, "Dubai")

        df.toListOf<WithDefault>() shouldBe listOf(WithDefault("Alice", 15, "Dubai"))
    }

    @Test
    fun `null values go only into a nullable parameter`() {
        val df = dataFrameOf("name", "age")("Alice", null)

        df.toListOf<NullableAge>() shouldBe listOf(NullableAge("Alice", null))
        shouldThrow<IllegalArgumentException> { df.toListOf<Student>() }
    }

    @Test
    fun `value that cannot be converted to the parameter type throws`() {
        val df = dataFrameOf("name", "age")("Alice", "fifteen")

        shouldThrow<CellConversionException> { df.toListOf<Student>() }
    }

    @Test
    fun `toListOf creates a generic data class`() {
        val df = dataFrameOf("value")(1, 2)

        df.toListOf<Box<Int>>() shouldBe listOf(Box(1), Box(2))
    }

    @Test
    fun `toListOf creates a Pair`() {
        val df = dataFrameOf("first", "second")("Alice", 15)

        df.toListOf<Pair<String, Int>>() shouldBe listOf("Alice" to 15)
    }

    @Test
    fun `nullable generic parameter takes null values`() {
        val df = dataFrameOf("value")(1, null)

        // `NullableBox.value` is `V?`, so it is nullable even for `NullableBox<Int>`.
        df.toListOf<NullableBox<Int>>() shouldBe listOf(NullableBox(1), NullableBox(null))
    }

    @Test
    fun `frame column becomes a typed DataFrame`() {
        val df = dataFrameOf("city", "name", "age")("London", "Alice", 15, "Dubai", "Charlie", 30)
            .groupBy("city").toDataFrame("students")

        val list = df.toListOf<TypedCity>()

        list.map { it.city } shouldBe listOf("London", "Dubai")
        list.map { it.students.toList() } shouldBe listOf(listOf(Student("Alice", 15)), listOf(Student("Charlie", 30)))
    }

    @Test
    fun `frame column keeps its other columns in a DataFrame parameter`() {
        val df = dataFrameOf("city", "name", "age")("London", "Alice", 15)
            .groupBy("city").toDataFrame("students")

        // The "city" column stays in the frame, although `Student` has no such property.
        df.toListOf<TypedCity>().single().students.columnNames() shouldBe listOf("city", "name", "age")
    }

    @Test
    fun `frame column goes into an AnyFrame parameter as it is`() {
        val df = dataFrameOf("city", "name", "age")("London", "Alice", 15)
            .groupBy("city").toDataFrame("students")

        val students = df.toListOf<UntypedCity>().single().students

        students.columnNames() shouldBe listOf("city", "name", "age")
        students["age"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `frame column is converted to the schema of a DataFrame parameter`() {
        val df = dataFrameOf("city", "name", "age")("London", "Alice", 15)
            .groupBy("city").toDataFrame("students")

        // `LongAge.age` is `Long`, so the `Int` column "age" inside each frame becomes `Long`.
        val students = df.toListOf<LongAgeCity>().single().students

        students["age"].type() shouldBe typeOf<Long>()
        students.toList() shouldBe listOf(LongAge("Alice", 15L))
    }

    @Test
    fun `frame column that does not match the schema of a DataFrame parameter throws`() {
        val df = dataFrameOf("city", "name", "age")("London", "Alice", 15)
            .groupBy("city").toDataFrame("students")

        // The frames have no "x" and "y" columns, which `NotStudent` needs.
        shouldThrow<IllegalArgumentException> { df.toListOf<NotStudentCity>() }
    }

    @Test
    fun `generic parameter inside a type keeps its nullability`() {
        val df = dataFrameOf("items")(listOf(1, null))

        // `ListBox.items` is `List<V?>`, so for `ListBox<Int>` it is `List<Int?>`.
        df.toListOf<ListBox<Int>>() shouldBe listOf(ListBox(listOf(1, null)))
    }

    // endregion
}

package org.jetbrains.kotlinx.dataframe.samples.concepts

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.junit.Ignore
import org.junit.Test
import java.util.Locale

class AccessApis {

    @DataSchema
    interface Name {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Person {
        val fullName: Name
        val age: Int
    }

    @DataSchema
    interface PersonInfo {
        val name: String
        val age: Int
        val height: Double
    }

    val df = dataFrameOf(
        columnOf(
            columnOf("Alice") named "firstName",
            columnOf("Johnson") named "lastName",
        ) named "fullName",
        columnOf(20) named "age",
    ).cast<Person>()

    val personInfoDf: DataFrame<PersonInfo> = dataFrameOf(
        "name" to columnOf("Alice", "Bob"),
        "age" to columnOf(23, 27),
        "height" to columnOf(175.5, 160.2),
    ).cast()

    @Test
    fun stringApiExample1() {
        // SampleStart
        // Get the "fullName" column
        df["fullName"]

        // Rename the "fullName" column to "name"
        df.rename("fullName").to("name")
        // SampleEnd
    }

    @Test
    fun stringApiExample2() {
        // SampleStart
        // Select the "firstName" column from the "fullName" column group
        // and the "age" column
        df.select { "fullName"["firstName"]<String>() and "age"<Int>() }

        // Takes only rows where the
        // ("fullName"/"firstName") column value is equal to "Alice"
        // and "age" column value is greater or equal to 18
        df.filter {
            "fullName"["firstName"]<String>() == "Alice" && "age"<Int>() >= 18
        }
        // SampleEnd
    }

    @Test
    fun extensionPropertiesApiExample() {
        // SampleStart
        // Get "fullName" column
        df.fullName

        // Rename "fullName" column to "name"
        df.rename { fullName }.to("name")

        // Select the "firstName" column from the "fullName" column group
        // and the "age" column
        df.select { fullName.firstName and age }

        // Takes only rows where the
        // ("fullName"/"firstName") column value is equal to "Alice"
        // and "age" column value is greater or equal to 18
        df.filter {
            fullName.firstName == "Alice" && age >= 18
        }
        // SampleEnd
    }

    @Test
    fun extensionPropertiesApiExample2() {
        // SampleStart
        // Access the "name" column
        val nameCol: DataColumn<String> = personInfoDf.name

        // Take the "name" value from the first row
        val nameValue: String = personInfoDf.first().name

        // Select the "age" and "height" columns
        personInfoDf.select { age and height }

        // Filter rows where "age" > 18 and "name" starts with 'A'
        personInfoDf.filter { age > 18 && name.startsWith("A") }
        // SampleEnd
        nameCol.values() shouldBe listOf("Alice", "Bob")
        nameValue shouldBe "Alice"
    }

    @Ignore
    @Test
    fun strings() {
        // SampleStart
        DataFrame.readCsv(
            "titanic.csv",
            delimiter = ';',
            parserOptions = ParserOptions(locale = Locale.GERMANY),
        )
            .add("lastName") { "name"<String>().substringBefore(",") }
            .dropNulls("age", "homedest")
            .filter {
                "survived"<Int>() == 1 &&
                    "homedest"<String>().endsWith("NY") &&
                    "age"<Double>() in 10.0..20.0
            }
        // SampleEnd
    }

    @DataSchema
    interface TitanicPassenger {
        val survived: Int
        val homedest: String?
        val age: Double?
        val name: String
    }

    @Ignore
    @Test
    fun extensionProperties2() {
        val df =
            DataFrame.readCsv(
                "titanic.csv",
                delimiter = ';',
                parserOptions = ParserOptions(locale = Locale.GERMANY),
            ).cast<TitanicPassenger>()
        // SampleStart
        df.add("lastName") { name.substringBefore(",") }
            .dropNulls { age and homedest }
            .filter { survived == 1 && homedest.endsWith("NY") && age in 10.0..20.0 }
        // SampleEnd
    }

    @Ignore
    @Test
    fun extensionProperties1() {
        // SampleStart
        val df =
            DataFrame.readCsv(
                "titanic.csv",
                delimiter = ';',
                parserOptions = ParserOptions(locale = Locale.GERMANY),
            )
        // SampleEnd
    }
}

package org.jetbrains.kotlinx.dataframe.samples.concepts

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.io.read
import org.junit.Ignore
import org.junit.Test

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

    val df = dataFrameOf(
        columnOf(
            columnOf("Alice") named "firstName",
            columnOf("Johnson") named "lastName",
        ) named "fullName",
        columnOf(20) named "age",
    ).cast<Person>()

    @Test
    fun stringApiExample1() {
        // SampleStart
        // Get the "fullName" column
        df["fullName"]
        // Rename the "fullName" column into "name"
        df.rename("fullName").into("name")
        // SampleEnd
    }

    @Test
    fun stringApiExample2() {
        // SampleStart
        // Select the "firstName" column from the "fullName" column group
        // and the "age" column
        df.select { "fullName"["firstName"]<String>() and "age"<Int>() }
        // Takes only rows where the
        // "fullName"->"firstName" column value is equal to "Alice"
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
        // Rename "fullName" column into "name"
        df.rename { fullName }.into("name")
        // Select the "firstName" column from the "fullName" column group
        // and the "age" column
        df.select { fullName.firstName and age }
        // Takes only rows where the
        // "fullName"->"firstName" column value is equal to "Alice"
        // and "age" column value is greater or equal to 18
        df.filter {
            fullName.firstName == "Alice" && age >= 18
        }
        // SampleEnd
    }


    @Ignore
    @Test
    fun strings() {
        // SampleStart
        DataFrame.read("titanic.csv")
            .add("lastName") { "name"<String>().split(",").last() }
            .dropNulls("age")
            .filter {
                "survived"<Boolean>() &&
                    "home"<String>().endsWith("NY") &&
                    "age"<Int>() in 10..20
            }
        // SampleEnd
    }

    @DataSchema
    interface TitanicPassenger {
        val survived: Boolean
        val home: String
        val age: Int
        val name: String
    }

    @Ignore
    @Test
    fun extensionProperties2() {
        val df = DataFrame.read("titanic.csv").cast<TitanicPassenger>()
        // SampleStart
        df.add("lastName") { name.split(",").last() }
            .dropNulls { age }
            .filter { survived && home.endsWith("NY") && age in 10..20 }
        // SampleEnd
    }

    @Ignore
    @Test
    fun extensionProperties1() {
        // SampleStart
        val df /* : AnyFrame */ = DataFrame.read("titanic.csv")
        // SampleEnd
    }
}

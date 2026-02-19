package org.jetbrains.kotlinx.dataframe.samples.concepts

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
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
        // Get the "fullName" column
        df["fullName"]
        // Rename the "fullName" column into "name"
        df.rename("fullName").into("name")
    }

    @Test
    fun stringApiExample2() {
        // Select the "firstName" column from the "fullName" column group
        // and the "age" column
        df.select { "fullName"["firstName"]<String>() and "age"<Int>() }
        // Takes only rows where the
        // "fullName"->"firstName" column value is equal to "Alice"
        // and "age" column value is greater or equal to 18
        df.filter {
            "fullName"["firstName"]<String>() == "Alice" && "age"<Int>() >= 18
        }
    }

    @Test
    fun extensionPropertiesApiExample() {
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
            "fullName"["firstName"]<String>() == "Alice" && "age"<Int>() >= 18
        }
    }

    fun stringApiExampleFull() {
        // Column Selection DSL

        // Select the "firstName" subcolumn of the "name" column group
        // and the "age" column
        df.select { "name"["firstName"]<String>() and "age"<Int>() }

        // Calculate the mean value of the "age" column;
        // specify the column type as an invocation type argument
        df.mean { "age"<Int>() }

        // Row Expressions

        // Add a new "fullName" column by combining
        // the "firstName" and "lastName" column values
        df.add("fullName") {
            "name"["firstName"]<String>() + " " + "name"["lastName"]<String>()
        }

        // Takes only rows where the
        // "fullName"->"firstName" column value is equal to "Alice"
        // and "age" column value is greater or equal to 18
        df.filter {
            "fullName"["firstName"]<String>() == "Alice" && "age"<Int>() >= 18
        }
    }
}

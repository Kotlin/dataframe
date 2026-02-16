package org.jetbrains.kotlinx.dataframe.examples.exposed

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.size
import java.io.File

/**
 * Describes a simple bridge between [Exposed](https://www.jetbrains.com/exposed/) and DataFrame!
 */
fun main() {
    // defining where to find our SQLite database for Exposed
    val resourceDb = "chinook.db"
    val dbPath = File(object {}.javaClass.classLoader.getResource(resourceDb)!!.toURI()).absolutePath
    val db = Database.connect(url = "jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")

    // let's read the database!
    val df = transaction(db) {
        // addLogger(StdOutSqlLogger) // enable if you want to see verbose logs

        // tables in Exposed need to be defined, see tables.kt
        SchemaUtils.create(Customers, Artists, Albums)

        println()

        // In Exposed, we can write queries like this.
        // Here, we count per country how many customers there are and print the results:
        Customers
            .select(Customers.country, Customers.customerId.count())
            .groupBy(Customers.country)
            .orderBy(Customers.customerId.count() to SortOrder.DESC)
            .forEach {
                println("${it[Customers.country]}: ${it[Customers.customerId.count()]} customers")
            }

        println()

        // Perform the specific query you want to read into the DataFrame.
        // Note: DataFrames are in-memory structures, so don't make it too large if you don't have the RAM ;)
        val query = Customers.selectAll() // .where { Customers.company.isNotNull() }

        println()

        // read and convert the query to a typed DataFrame
        // see compatibilityLayer.kt for how we created convertToDataFrame<>()
        // and see tables.kt for how we created DfCustomers!
        query.convertToDataFrame<DfCustomers>()
    }

    println(df.size())

    // now we have a DataFrame, we can perform DataFrame operations,
    // like doing the same operation as we did in Exposed above
    df.groupBy { country }.count()
        .sortByDesc { "count"<Int>() }
        .print(columnTypes = true, borders = true)

    // or just general statistics
    df.describe()
        .print(columnTypes = true, borders = true)

    // or make plots using Kandy! It's all up to you

    // writing a DataFrame back into an SQL database with Exposed can also be done easily!
    transaction(db) {
        // addLogger(StdOutSqlLogger) // enable if you want to see verbose logs

        // first delete the original contents
        Customers.deleteAll()

        println()

        // batch-insert our dataframe back into the SQL database as a sequence of rows
        Customers.batchInsert(df.asSequence()) { dfRow ->
            // we simply go over each value in the row and put it in the right place in the Exposed statement
            for (column in Customers.columns) {
                @Suppress("UNCHECKED_CAST")
                this[column as Column<Any?>] = dfRow[column.name]
            }
        }
    }
}

package org.jetbrains.kotlinx.dataframe.examples.jdbc

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

@DataSchema
interface Movies {
    val id: Int
    val name: String
    val year: String
    val rank: String
}

fun main() {
    // define the database configuration
    val dbConfig = DatabaseConfiguration(URL, USER_NAME, PASSWORD)

    // read the table
    val df = DataFrame.readSqlTable(dbConfig, TABLE_NAME_MOVIES, 10000).cast<Movies>()

    // print the dataframe
    df.print()

    // print the dataframe metadata and statistics
    df.describe().print()

    // print names of top-10 rated films
    df.sortByDesc { it[Movies::rank] }
        .take(10)
        .select { it[Movies::name] }
        .print()
}

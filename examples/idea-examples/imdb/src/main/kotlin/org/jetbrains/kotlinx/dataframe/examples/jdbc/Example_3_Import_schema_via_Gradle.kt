package org.jetbrains.kotlinx.dataframe.examples.jdbc

import java.sql.DriverManager
import java.util.Properties
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlQuery
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlTable
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable

/**
 * For this example, the schema importing was configured in Gradle in the
 * ```kotlin
 * dataframes {
 *     schema {
 *       ...
 *   }
 * }
 * ```
 * section.
 *
 * Open the `build.gradle.kts` to see or edit.
 *
 * NOTE: The idea of this example is to cover more available functionality
 * and demonstrate different ways to establish connection to the database.
 */
fun main() {
    val props = Properties()
    props.setProperty("user", USER_NAME)
    props.setProperty("password", PASSWORD)

    // Part 1: Getting the data from the SQL table with an explicit announcement of the Connection object from the JDBC driver.
    println("---------------------------- Part 1: SQL Table ------------------------------------")
    DriverManager.getConnection(URL, props).use { connection ->
        // read the data from the SQL table
        val actors = DataFrame.readSqlTable(connection,  TABLE_NAME_ACTORS, 100).cast<Actors>()
        actors.print()

        // filter and print the data
        actors.filter { it[Actors::firstName]!= null && it[Actors::firstName]!!.contains("J") }.print()

        // extract the schema of the SQL table
        val actorSchema = DataFrame.getSchemaForSqlTable(connection,  TABLE_NAME_ACTORS)
        actorSchema.print()
    }

    // Part 2: Getting the data from the SQL query with an explicit announcement of the Connection object from the JDBC driver.
    println("---------------------------- Part 2: SQL Query ------------------------------------")
    DriverManager.getConnection(URL, props).use { connection ->
        // read the data from as a result of an executed SQL query
        val tarantinoFilms = DataFrame.readSqlQuery(connection, TARANTINO_FILMS_SQL_QUERY, 100).cast<TarantinoFilms>()
        tarantinoFilms.print()

        // transform and print the data
        tarantinoFilms.filter { it[TarantinoFilms::year]!= null && it[TarantinoFilms::year]!! > 2000 }.print()

        // extract the schema of the SQL table
        val tarantinoFilmsSchema = DataFrame.getSchemaForSqlQuery(connection, TARANTINO_FILMS_SQL_QUERY)
        tarantinoFilmsSchema.print()
    }

    // Part 3: Getting the data from the SQL query with an explicit announcement of the ResultSet object from the JDBC driver.
    println("---------------------------- Part 3: ResultSet ------------------------------------")
    DriverManager.getConnection(URL, props).use { connection ->
        connection.createStatement().use { st ->
            st.executeQuery(TARANTINO_FILMS_SQL_QUERY).use { rs ->
                // read the data from as a result of an executed SQL query
                val tarantinoFilms = DataFrame.readSqlQuery(connection, TARANTINO_FILMS_SQL_QUERY, 100).cast<TarantinoFilms>()
                tarantinoFilms.print()

                // transform and print the data
                tarantinoFilms.filter { it[TarantinoFilms::year]!= null && it[TarantinoFilms::year]!! > 2000 }.print()

                // extract the schema of the SQL table
                val tarantinoFilmsSchema = DataFrame.getSchemaForSqlQuery(connection, TARANTINO_FILMS_SQL_QUERY)
                tarantinoFilmsSchema.print()
            }
        }
    }

    // Part 4: Getting the bunch of dataframes (one per each non-system SQL table)
    // with an explicit announcement of the Connection object from the JDBC driver.
    println("---------------------------- Part 4: readAllSqlTables ------------------------------------")
    DriverManager.getConnection(URL, props).use { connection ->
        val dataFrames = DataFrame.readAllSqlTables(connection, limit = 100)
        dataFrames.forEach {
            it.print()
            it.describe()
        }
    }
}

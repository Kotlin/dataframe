package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.print
import org.junit.Test
import java.sql.DriverManager
import java.util.Properties
import org.junit.Ignore

private const val URL = "jdbc:mariadb://localhost:3307/imdb"
private const val URL2 = "jdbc:mariadb://localhost:3307"
private const val USER_NAME = "root"
private const val PASSWORD = "pass"

@DataSchema
interface ActorKDF {
    val id: Int
    val firstName: String
    val lastName: String
    val gender: String
}

@DataSchema
interface RankedMoviesWithGenres {
    val name: String
    val year: Int
    val rank: Float
    val genres: String
}

@Ignore
class ImdbTestTest {
    @Test
    fun `read table`() {
        val props = Properties()
        props.setProperty("user", USER_NAME)
        props.setProperty("password", PASSWORD)

        // generate kdf schemas by database metadata (as interfaces or extensions)
        // for gradle or as classes under the hood in KNB

        DriverManager.getConnection(URL, props).use { connection ->
            val df = DataFrame.readSqlTable(connection, "actors", 100).cast<ActorKDF>()
            df.print()
        }
    }

    @Test
    fun `read table with schema`() {
        val props = Properties()
        props.setProperty("user", USER_NAME)
        props.setProperty("password", PASSWORD)

        // generate kdf schemas by database metadata (as interfaces or extensions)
        // for gradle or as classes under the hood in KNB

        DriverManager.getConnection(URL2, props).use { connection ->
            val df = DataFrame.readSqlTable(connection, "imdb.actors", 100).cast<ActorKDF>()
            df.print()
        }
    }


    @Test
    fun `read sql query`() {
        val sqlQuery = "select name, year, rank,\n" +
            "GROUP_CONCAT (genre) as \"genres\"\n" +
            "from movies join movies_directors on  movie_id = movies.id\n" +
            "     join directors on directors.id=director_id left join movies_genres on movies.id = movies_genres.movie_id \n" +
            "where directors.first_name = \"Quentin\" and directors.last_name = \"Tarantino\"\n" +
            "group by name, year, rank\n" +
            "order by year"
        val props = Properties()
        props.setProperty("user", USER_NAME)
        props.setProperty("password", PASSWORD)

        // generate kdf schemas by database metadata (as interfaces or extensions)
        // for gradle or as classes under the hood in KNB

        DriverManager.getConnection(URL, props).use { connection ->
            val df = DataFrame.readSqlQuery(connection, sqlQuery).cast<RankedMoviesWithGenres>()
            //df.filter { year > 2000 }.print()
            df.print()

            val schema = DataFrame.getSchemaForSqlQuery(connection, sqlQuery)
            schema.print()
        }
    }
}

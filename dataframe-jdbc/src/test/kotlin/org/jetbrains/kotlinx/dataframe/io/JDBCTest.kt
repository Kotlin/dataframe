package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.print
import org.junit.Test
import java.sql.DriverManager
import java.util.*


const val URL = "jdbc:mariadb://localhost:3306/imdb"
const val USER_NAME = "root"
const val PASSWORD = "pass"

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
    val rank: Int
    val genres: String
}

class JDBCTest {
    @Test
    fun `setup connection and select from one table` () {
        val props = Properties()
        props.setProperty("user", USER_NAME)
        props.setProperty("password", PASSWORD)

        // generate kdf schemas by database metadata (as interfaces or extensions)
        // for gradle or as classes under the hood in KNB

        DriverManager.getConnection(URL, props).use { connection ->
            val df = DataFrame.readFromDB(connection, "actors").cast<ActorKDF>()
            df.print()
        }
    }

    @Test
    fun `convert result of SQL-query` () {

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
            val df = DataFrame.readFromDBViaSQLQuery(connection, sqlQuery = sqlQuery).cast<RankedMoviesWithGenres>()
            //df.filter { year > 2000 }.print()
            df.print()
        }
    }
}

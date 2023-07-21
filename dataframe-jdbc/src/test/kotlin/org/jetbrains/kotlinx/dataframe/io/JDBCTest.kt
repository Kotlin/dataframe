package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.print
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
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

@DataSchema
interface Customer {
    val id: Int
    val name: String
    val age: Int
}

@DataSchema
interface Sale {
    val id: Int
    val customerId: Int
    val amount: Double
}

@DataSchema
interface CustomerSales {
    val customerName: String
    val totalSalesAmount: Double
}


class JDBCTest {
    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            // Подключение к тестовой базе данных H2 в памяти
            connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false")

            // Создание таблицы Customer
            connection.createStatement().execute("""
                CREATE TABLE Customer (
                    id INT PRIMARY KEY,
                    name VARCHAR(50),
                    age INT
                )
            """.trimIndent())

            // Создание таблицы Sale
            connection.createStatement().execute("""
                CREATE TABLE Sale (
                    id INT PRIMARY KEY,
                    customerId INT,
                    amount DECIMAL(10, 2)
                )
            """.trimIndent())

            // add data to the Customer table
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (1, 'John', 40)")
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (2, 'Alice', 25)")
            connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (3, 'Bob', 47)")

            // add data to the Sale table
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (1, 1, 100.50)")
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (2, 2, 50.00)")
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (3, 1, 75.25)")
            connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (4, 3, 35.15)")
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.close()
            } catch (e: SQLException) {
                // Обработка ошибок закрытия соединения
                e.printStackTrace()
            }
        }
    }

    @Test
    fun `setup connection and select from one table` () {
        val props = Properties()
        props.setProperty("user", USER_NAME)
        props.setProperty("password", PASSWORD)

        // generate kdf schemas by database metadata (as interfaces or extensions)
        // for gradle or as classes under the hood in KNB

        DriverManager.getConnection(URL, props).use { connection ->
            val df = DataFrame.readFromDB(connection, "imdb", "imdb.actors").cast<ActorKDF>()
            df.print()
        }
    }

    @Test
    fun `basic test for reading join from two table` () {
        connection.use { connection ->
            val sqlQuery = """
            SELECT c.name as customerName, SUM(s.amount) as totalSalesAmount
            FROM Sale s
            INNER JOIN Customer c ON s.customerId = c.id
            WHERE c.age > 35
            GROUP BY s.customerId, c.name
        """.trimIndent()

            val df = DataFrame.readFromDBViaSQLQuery(connection,  sqlQuery = sqlQuery).cast<CustomerSales>()
            df.print()
            assertEquals(2, df.rowsCount())
        }
    }

    @Test
    fun `basic test for reading one table` () {
        connection.use { connection ->
            val df = DataFrame.readFromDB(connection, "dsdfs", "Customer").cast<Customer>()
            df.print()
            assertEquals(3, df.rowsCount())
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

    @Test
    fun `sql native types mapping to JDBC types` () {
        // TODO: need to add test with very diverse table with all different column and fake data with conversion to JDBC and DataFrame types
    }
}

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
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

class JDBCTest {
    @Test
    fun `setup connection and select` () {
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
}

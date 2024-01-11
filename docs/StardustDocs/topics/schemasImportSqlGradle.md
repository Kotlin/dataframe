[//]: # (title: Import SQL metadata as a schema in Gradle project)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

Each SQL database keeps the metadata for all the tables, and 
this metadata could be used for the schema generation.

**NOTE:** Visit the [page](readSqlDatabases.md) to see how to set up all Gradle dependencies for your project.

### With `@file:ImportDataSchema`

To generate schema for existing SQL table,
you need to define a few parameters to establish JDBC connection:
URL, username, and password.

Also, the `tableName` parameter could be specified.

You should also specify the name of the generated Kotlin class 
as a first parameter of annotation `@file:ImportDataSchema`.

```kotlin
@file:ImportDataSchema(
    "ActorSchema",
    URL,
    jdbcOptions = JdbcOptions(USER_NAME, PASSWORD, tableName = TABLE_NAME)
)

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
```

```kotlin
const val URL = "jdbc:mariadb://localhost:3306/imdb"

const val USER_NAME = "root"

const val PASSWORD = "pass"

const val TABLE_NAME = "actors"
```
To generate schema for the result of an SQL query,
you need to define the SQL query itself
and the same parameters to establish connection with the database.

You should also specify the name of the generated Kotlin class
as a first parameter of annotation `@file:ImportDataSchema`.

```kotlin
@file:ImportDataSchema(
    "TarantinoFilmSchema",
    URL,
    jdbcOptions = JdbcOptions(USER_NAME, PASSWORD, sqlQuery = TARANTINO_FILMS_SQL_QUERY)
)

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
```

```kotlin
const val URL = "jdbc:mariadb://localhost:3306/imdb"

const val USER_NAME = "root"

const val PASSWORD = "pass"

const val TARANTINO_FILMS_SQL_QUERY = "select name, year, rank,\n" +
    "GROUP_CONCAT (genre) as \"genres\"\n" +
    "from movies join movies_directors on  movie_id = movies.id\n" +
    "     join directors on directors.id=director_id left join movies_genres on movies.id = movies_genres.movie_id \n" +
    "where directors.first_name = \"Quentin\" and directors.last_name = \"Tarantino\"\n" +
    "group by name, year, rank\n" +
    "order by year"
```

### With Gradle Task 

To generate schema for existing SQL table,
you need to define a few parameters to establish JDBC connection:
URL (passing to `data` field), username, and password.

Also, the `tableName` parameter should be specified.

```kotlin
dataframes {
    schema {
        data = "jdbc:mariadb://localhost:3306/imdb"
        name = "org.example.imdb.Actors"
        jdbcOptions {
            user = "root"
            password = "pass" 
            tableName = "actors"
        }
    }
}
```

To generate schema for the result of an SQL query,
you need to define the SQL query itself
and the same parameters to establish connection with the database.

```kotlin
dataframes {
    schema {
        data = "jdbc:mariadb://localhost:3306/imdb"
        name = "org.example.imdb.TarantinoFilms"
        jdbcOptions {
            user = "root" 
            password = "pass"
            sqlQuery = "select name, year, rank,\n" +
                    "GROUP_CONCAT (genre) as \"genres\"\n" +
                    "from movies join movies_directors on  movie_id = movies.id\n" +
                    "     join directors on directors.id=director_id left join movies_genres on movies.id = movies_genres.movie_id \n" +
                    "where directors.first_name = \"Quentin\" and directors.last_name = \"Tarantino\"\n" +
                    "group by name, year, rank\n" +
                    "order by year"
        }
    }
}
```

After importing the data schema, you can now start to import any data from SQL table or as a result of an SQL query
you like using the generated schemas.

Now you will have a correctly typed [`DataFrame`](DataFrame.md)!

If you experience any issues with the SQL databases support (since there are many edge-cases when converting
SQL types from different databases to Kotlin types), please open an issue on
the [GitHub repo](https://github.com/Kotlin/dataframe/issues), specifying the database and the problem.

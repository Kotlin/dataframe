package org.jetbrains.kotlinx.dataframe.examples.jdbc

const val URL = "jdbc:mariadb://localhost:3307/imdb"
const val USER_NAME = "root"
const val PASSWORD = "pass"
const val TABLE_NAME_ACTORS = "actors"
const val TABLE_NAME_MOVIES = "movies"
const val TABLE_NAME_DIRECTORS = "directors"

const val TARANTINO_FILMS_SQL_QUERY = """
    SELECT name, year, rank,
    GROUP_CONCAT (genre) as "genres"
    FROM movies JOIN movies_directors ON movie_id = movies.id
    JOIN directors ON directors.id=director_id LEFT JOIN movies_genres ON movies.id = movies_genres.movie_id
    WHERE directors.first_name = "Quentin" AND directors.last_name = "Tarantino"
    GROUP BY name, year, rank
    ORDER BY year
    """

// Let's find all actors who played a role in movies released starting from 2000 year:
const val ACTORS_IN_LATEST_MOVIES = """
    SELECT a.first_name, a.last_name, r.role, m.name AS movie_name, m.year
    FROM actors a
    INNER JOIN roles r ON a.id = r.actor_id
    INNER JOIN movies m ON m.id = r.movie_id
    WHERE m.year > 2000
    """

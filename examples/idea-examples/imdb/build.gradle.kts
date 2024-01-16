import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlinx.dataframe")
}

repositories {
    mavenLocal() // in case of local dataframe development
    mavenCentral()
}

kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

dependencies {
    implementation ("org.jetbrains.kotlinx:kandy-lets-plot:0.5.0")
    implementation ("org.jetbrains.kotlinx:kandy-api:0.5.0")
    implementation ("org.mariadb.jdbc:mariadb-java-client:3.1.4")
    implementation(project(":"))
}

dataframes {
    schema {
        data = "jdbc:mariadb://localhost:3307/imdb"
        name = "org.jetbrains.kotlinx.dataframe.examples.jdbc.Actors"
        jdbcOptions {
            user = "root"
            password = "pass"
            tableName = "actors"
        }
    }
    schema {
        data = "jdbc:mariadb://localhost:3307/imdb"
        name = "org.jetbrains.kotlinx.dataframe.examples.jdbc.TarantinoFilms"
        jdbcOptions {
            user = "root"
            password = "pass"
            sqlQuery = """
                SELECT name, year, rank,
                GROUP_CONCAT (genre) as "genres"
                FROM movies JOIN movies_directors ON movie_id = movies.id
                JOIN directors ON directors.id=director_id LEFT JOIN movies_genres ON movies.id = movies_genres.movie_id
                WHERE directors.first_name = "Quentin" AND directors.last_name = "Tarantino"
                GROUP BY name, year, rank
                ORDER BY year
                """
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

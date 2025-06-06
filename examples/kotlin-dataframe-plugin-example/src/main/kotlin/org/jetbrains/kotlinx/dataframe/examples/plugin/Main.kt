package org.jetbrains.kotlinx.dataframe.examples.plugin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import java.net.URL

/*
 * Declare data schema for the DataFrame from jetbrains_repositories.csv.
 */
@DataSchema
data class Repositories(
    @ColumnName("full_name")
    val fullName: String,
    @ColumnName("html_url")
    val htmlUrl: URL,
    @ColumnName("stargazers_count")
    val stargazersCount: Int,
    val topics: String,
    val watchers: Int
)

/*
 * Define kinds of repositories:
 */
enum class RepoKind {
    Kotlin, IntelliJ, Other
}

/*
 * A rule for determining the kind of repository based on its name and topics.
 */
fun getKind(fullName: String, topics: List<String>): RepoKind {
    fun checkContains(name: String) = name in topics || fullName.lowercase().contains(name)

    return when {
        checkContains("kotlin") -> RepoKind.Kotlin
        checkContains("idea") || checkContains("intellij") -> RepoKind.IntelliJ
        else -> RepoKind.Other
    }
}


fun main() {
    val repos = DataFrame
        // Read DataFrame from the CSV file.
        .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
        // And convert it to match the `Repositories` schema.
        .convertTo<Repositories>()

    // Rename columns to CamelCase
    val reposUpdated = repos.renameToCamelCase()
        // Convert values in the "topic" column (which were `String` initially)
        // to the list of topics.
        .convert { topics }.with {
            val inner = it.removePrefix("[").removeSuffix("]")
            if (inner.isEmpty()) emptyList() else inner.split(',').map(String::trim)
        }
        // Filter rows, keeping only repos with more than 150 stars.
        .filter { it.stargazersCount > 150 }
        // Add a new column with the number of topics.
        .add("topicCount") { topics.size }
        // Add a new column with the kind of repository.
        .add("kind") { getKind(fullName, topics) }

    // Write an updated DataFrame to a CSV file.
    reposUpdated.writeCsv("jetbrains_repositories_new.csv")

    // TODO: Add Kandy Plot
    /*reposUpdated.groupBy { kind }.max { stargazersCount  }.plot {
        bars {
            x(kind)
            y(stargazersCount)
        }
    }*/
}

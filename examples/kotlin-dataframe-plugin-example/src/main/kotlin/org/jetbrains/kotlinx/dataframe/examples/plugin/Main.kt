package org.jetbrains.kotlinx.dataframe.examples.plugin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.renameToCamelCase
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import java.net.URL

// Declare data schema for the DataFrame from jetbrains_repositories.csv.
@DataSchema
data class Repositories(
    val full_name: String,
    val html_url: URL,
    val stargazers_count: Int,
    val topics: String,
    val watchers: Int,
)

// Define kinds of repositories.
enum class RepoKind {
    Kotlin,
    IntelliJ,
    Other,
}

// A rule for determining the kind of repository based on its name and topics.
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

    // With Compiler Plugin, the DataFrame schema changes immediately after each operation:
    // For example, if a new column is added or the old one is renamed (or its type is changed)
    // during the operation, you can use the new name immediately in the following operations:
    repos
        // Add a new "name" column...
        .add("name") { full_name.substringAfterLast("/") }
        // ... and now we can use "name" extension in DataFrame operations, such as `filter`.
        .filter { name.lowercase().contains("kotlin") }

    // Let's update the DataFrame with some operations using these features.
    val reposUpdated = repos
        // Rename columns to CamelCase.
        // Note that after that, in the following operations, extension properties will have
        // new names corresponding to the column names.
        .renameToCamelCase()
        // Rename "stargazersCount" column to "stars".
        .rename { stargazersCount }.into("stars")
        // And we can immediately use the updated name in the filtering.
        .filter { stars > 50 }
        // Convert values in the "topic" column (which were `String` initially)
        // to the list of topics.
        .convert { topics }.with {
            val inner = it.removeSurrounding("[", "]")
            if (inner.isEmpty()) emptyList() else inner.split(',').map(String::trim)
        }
        // Now "topics" is a `List<String>` column.
        // Add a new column with the number of topics.
        .add("topicCount") { topics.size }
        // Add a new column with the kind of repository.
        .add("kind") { getKind(fullName, topics) }

    // Write the updated DataFrame to a CSV file.
    reposUpdated.writeCsv("jetbrains_repositories_new.csv")

    // TODO: Add Kandy Plot
    //  reposUpdated.groupBy { kind }.max { stargazersCount  }.plot {
    //      bars {
    //          x(kind)
    //          y(stargazersCount)
    //      }
    //  }
}

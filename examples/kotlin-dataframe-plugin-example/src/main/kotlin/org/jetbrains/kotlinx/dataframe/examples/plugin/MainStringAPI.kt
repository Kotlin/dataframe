package org.jetbrains.kotlinx.dataframe.examples.plugin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.renameToCamelCase
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import java.net.URL

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
        .add("name") { "full_name"<String>().substringAfterLast("/") }
        // ... and now we can use "name" extension in DataFrame operations, such as `filter`.
        .filter { name.lowercase().contains("kotlin") }

    // Let's update the DataFrame with some operations using these features.
    val reposUpdated = repos
        // Rename columns to CamelCase.
        // Note that after that, in the following operations, extension properties will have
        // new names corresponding to the column names.
        .renameToCamelCase()
        // Rename "stargazersCount" column to "stars".
        .rename { "stargazersCount"<String>() }.into("stars")
        // And we can immediately use the updated name in the filtering.
        .filter { "stars"<Int>() > 50 }
        // Convert values in the "topic" column (which were `String` initially)
        // to the list of topics.
        .convert { "topics"<String>() }.with {
            val inner = it.removeSurrounding("[", "]")
            if (inner.isEmpty()) emptyList() else inner.split(',').map(String::trim)
        }
        // Now "topics" is a `List<String>` column.
        // Add a new column with the number of topics.
        .add("topicCount") { "topics"<List<String>>().size }
        // Add a new column with the kind of repository.
        .add("kind") { getKind("full_name"(), "topics"()) }

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

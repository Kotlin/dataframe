package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.requireColumn
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.testJson
import org.junit.Test

class RequireSamples : DataFrameSampleHelper("require", "api") {

    @Test
    fun requireColumnNested() {
        val file = testJson("people")
        // SampleStart
        val df = DataFrame.readJson(file)
        // Without `requireColumn` the extension property will not resolve:
        // df.select { name.firstName }
        val typed = df.requireColumn { "name"["firstName"]<String>() }
        val v: String = typed.name.firstName[0]
        // SampleEnd
        v shouldBe "Alice"
        df.saveDfHtmlSample()
    }

    @Test
    fun requireColumnStringApi() {
        // SampleStart
        val repos = DataFrame.readCsv(
            "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
        )

        repos
            .filter { "stargazers_count"<Int>() > 100 }
            .sortByDesc("stargazers_count")
            .select("full_name", "stargazers_count")
            // SampleEnd
            .also { it.rowsCount() shouldBe 91 }
            .saveDfHtmlSample()
    }

    @Test
    fun requireColumnTyped() {
        // SampleStart
        val repos = DataFrame.readCsv(
            "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
        )
            .requireColumn { "stargazers_count"<Int>() }

        repos
            .filter { stargazers_count > 100 }
            .sortByDesc { stargazers_count }
            .select { "full_name" and stargazers_count }
            // SampleEnd
            .also { it.rowsCount() shouldBe 91 }
            .saveDfHtmlSample()
    }

    @Test
    fun requireColumnRename() {
        // SampleStart
        val repos = DataFrame.readCsv(
            "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
        )
            .requireColumn { "stargazers_count"<Int>() }
            .rename { stargazers_count }.to("stars")

        // usages of the `stargazers_count` property no longer resolve:
        // repos.filter { stargazers_count > 100 }
        repos.filter { stars > 100 }
            // SampleEnd
            .also { it.rowsCount() shouldBe 91 }
            .saveDfHtmlSample()
    }

    @Test
    fun requireColumnCast() {
        // SampleStart
        val df = dataFrameOf("name" to columnOf<Any>("Joe"))
        val typed = df.requireColumn { name.cast<String>() }
        val filtered = typed.filter { name.length > 2 }
        // SampleEnd
        filtered.also { it.rowsCount() shouldBe 1 }.saveDfHtmlSample()
    }
}

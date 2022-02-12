@file:ImportDataSchema(
    "SearchResponse",
    "$basePath/search?q=cats&part=snippet&key=$apiKey"
)
@file:ImportDataSchema(
    "StatisticsResponse",
    "$basePath/videos?part=statistics&id=uHKfrz65KSU&key=$apiKey"
)

package org.jetbrains.kotlinx.dataframe.examples.youtube

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.chunked
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dfsOf
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.flatten
import org.jetbrains.kotlinx.dataframe.api.getValueOrNull
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.toTop
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.io.read
import java.net.URL
import kotlinx.datetime.Instant
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.toLong
import org.jetbrains.kotlinx.dataframe.io.toHTML
import java.awt.Desktop
import java.io.File

fun load(path: String) = DataRow.read("$basePath/$path&key=$apiKey")

fun load(path: String, maxPages: Int): AnyFrame {
    val rows = mutableListOf<AnyRow>()
    var pagePath = path
    do {
        val row = load(pagePath)
        rows.add(row)
        val next = row.getValueOrNull<String>("nextPageToken")
        pagePath = path + "&pageToken=" + next

    } while (next != null && rows.size < maxPages)
    return rows.concat()
}

fun main() {

    val searchRequest = "cute%20cats"
    val resultsPerPage = 50
    val maxPages = 5

    val videoId by column<String>("id")
    val channel by columnGroup()

    val videos = load("search?q=$searchRequest&maxResults=$resultsPerPage&part=snippet", maxPages)
        .cast<SearchResponse>()
        .items.concat()
        .dropNulls { id.videoId }
        .select { id.videoId into videoId and snippet }
        .distinct()
        .parse()
        .convert { dfsOf<URL>() }.with {
            IMG(it, maxHeight = 150)
        }.add("video") {
            val id = videoId()
            IFRAME("http://www.youtube.com/embed/$id")
        }.move { snippet.title and snippet.publishTime }.toTop()
        .move { snippet.channelId and snippet.channelTitle }.under(channel)
        .remove { snippet }

    val stats = videos[videoId]
        .chunked(50)
        .map {
            val ids = it.joinToString("%2C")
            load("videos?part=statistics&id=$ids").cast<StatisticsResponse>()
        }.asColumnGroup()
        .items.concat()
        .select { id and statistics.all() }
        .parse()

    val withStat = videos.join(stats) { videoId match right.id }

    val viewCount by column<Int>()
    val publishTime by column<Instant>()

    val channels = withStat
        .groupBy { channel }.sum { viewCount }
        .sortByDesc { viewCount }
        .flatten()

    channels.print()

    val growth = withStat
        .select { publishTime and viewCount }
        .convert { publishTime and viewCount }.toLong()
        .sortBy { publishTime }
        .cumSum { viewCount }

    growth.print()
}

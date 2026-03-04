package org.jetbrains.kotlinx.dataframe.examples.youtube

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.getValueOrNull
import org.jetbrains.kotlinx.dataframe.dataTypes.*
import org.jetbrains.kotlinx.dataframe.io.read
import java.net.URL

fun load(path: String) = DataRow.read("$basePath/$path&key=$apiKey")

fun load(path: String, maxPages: Int): AnyFrame =
    buildList {
        var pagePath = path
        do {
            val row = load(pagePath)
            this += row
            val next = row.getValueOrNull<String>("nextPageToken")
            pagePath = "$path&pageToken=$next"
        } while (next != null && this.size < maxPages)
    }.concat()

fun main() {
    val searchRequest = "cute%20cats"
    val resultsPerPage = 50
    val maxPages = 5

    val videos = load("search?q=$searchRequest&maxResults=$resultsPerPage&part=snippet", maxPages)
        .convertTo<SearchResponse> {
            convert<String?>().with { it.toString() }
            convert<Int?>().with { it ?: 0 }
        }
        .items.concat()
        .dropNulls { id.videoId }
        .select { id.videoId into "id" and snippet }
        .distinct()
        .parse()
        .convert { colsAtAnyDepth().colsOf<URL>() }.with {
            IMG(it, maxHeight = 150)
        }
        .add("video") {
            IFRAME("http://www.youtube.com/embed/$id")
        }
        .move { snippet.title and snippet.publishTime }.toTop()
        .move { snippet.channelId and snippet.channelTitle }.under("channel")
        .remove { snippet }

    val stats = videos.id
        .chunked(50)
        .map {
            val ids = it.joinToString("%2C")
            load("videos?part=statistics&id=$ids").cast<StatisticsResponse>()
        }.asColumnGroup()
        .items.concat()
        .select { id and statistics.allCols() }
        .parse()
        // TODO replace with requireColumn {} when available
        .cast<ParsedStats>(verify = false)

    val withStat = videos.join(stats) { id match right.id }

    val channels = withStat
        .groupBy { channel }.sumFor { viewCount }
        .sortByDesc { viewCount }
        .flatten()

    channels.print(borders = true, columnTypes = true)

    val growth = withStat
        .select { publishTime and viewCount }
        .convert { publishTime and viewCount }.toLong()
        .sortBy { publishTime }
        .cumSum { viewCount }

    growth.print(borders = true, columnTypes = true)
}

@DataSchema
internal interface ParsedStats {
    val id: String
    val commentCount: Int
    val favoriteCount: Int
    val likeCount: Int
    val viewCount: Int
}

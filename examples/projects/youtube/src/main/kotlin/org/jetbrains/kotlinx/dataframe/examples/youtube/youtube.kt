package org.jetbrains.kotlinx.dataframe.examples.youtube

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castTo
import org.jetbrains.kotlinx.dataframe.api.chunked
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.flatten
import org.jetbrains.kotlinx.dataframe.api.getValueOrNull
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.toInt
import org.jetbrains.kotlinx.dataframe.api.toLong
import org.jetbrains.kotlinx.dataframe.api.toTop
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.io.read
import java.net.URL

fun load(path: String) = DataRow.read("$BASE_PATH/$path&key=$API_KEY")

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
        .let { df ->
            // TODO replace simply with `parse()` in Kotlin 2.4.0+
            val fixedParsedTypes = df.convert { colsAtAnyDepth().colsOf<String>() }.to<Any>()
            df.parse().castTo(fixedParsedTypes, verify = false)
        }
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
        .let { df ->
            // TODO replace simply with `parse()` in Kotlin 2.4.0+
            val fixedParsedTypes = df.convert { colsAtAnyDepth().colsOf<String>() }.to<Any>()
            df.parse().castTo(fixedParsedTypes, verify = false)
        }
        // TODO replace with requireColumn {} when available
        // parse cannot know what type the columns will be at runtime,
        // so we need to cast the columns ourselves.
        .convert { all().nameEndsWith("Count") }.toInt()

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

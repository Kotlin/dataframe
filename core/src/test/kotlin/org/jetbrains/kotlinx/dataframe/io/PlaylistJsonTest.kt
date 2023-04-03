package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.junit.Test
import kotlin.reflect.typeOf

class PlaylistJsonTest {

    @DataSchema(isOpen = false)
    interface DataFrameType4 {
        val url: String
        val width: Int
        val height: Int
    }

    @DataSchema(isOpen = false)
    interface DataFrameType5 {
        val url: String
        val width: Int
        val height: Int
    }

    @DataSchema(isOpen = false)
    interface DataFrameType6 {
        val url: String
        val width: Int
        val height: Int
    }

    @DataSchema(isOpen = false)
    interface DataFrameType7 {
        val url: String?
        val width: Int?
        val height: Int?
    }

    @DataSchema(isOpen = false)
    interface DataFrameType8 {
        val url: String?
        val width: Int?
        val height: Int?
    }

    @DataSchema(isOpen = false)
    interface DataFrameType3 {
        val default: DataRow<DataFrameType4>
        val medium: DataRow<DataFrameType5>
        val high: DataRow<DataFrameType6>
        val standard: DataRow<DataFrameType7>
        val maxres: DataRow<DataFrameType8>
    }

    @DataSchema(isOpen = false)
    interface DataFrameType9 {
        val kind: String
        val videoId: String
    }

    @DataSchema(isOpen = false)
    interface DataFrameType2 {
        val publishedAt: String
        val channelId: String
        val title: String
        val description: String
        val thumbnails: DataRow<DataFrameType3>
        val channelTitle: String
        val playlistId: String
        val position: Int
        val resourceId: DataRow<DataFrameType9>
    }

    @DataSchema(isOpen = false)
    interface DataFrameType1 {
        val kind: String
        val etag: String
        val id: String
        val snippet: DataRow<DataFrameType2>
    }

    @DataSchema(isOpen = false)
    interface DataFrameType10 {
        val totalResults: Int
        val resultsPerPage: Int
    }

    @DataSchema
    interface DataRecord {
        val kind: String
        val etag: String
        val nextPageToken: String
        val items: DataFrame<DataFrameType1>
        val pageInfo: DataRow<DataFrameType10>
    }

    val path = "../data/playlistItems.json"
    val df = DataFrame.read(path)
    val typed = df.cast<DataRecord>()
    val item = typed.items[0]

    @Test
    fun `deep update`() {
        val updated = item.convert { snippet.thumbnails.default.url }.with { IMG(it) }
        updated.snippet.thumbnails.default.url.type() shouldBe typeOf<IMG>()
    }

    @Test
    fun `deep update group`() {
        val updated = item.convert { snippet.thumbnails.default }.with { it.url }
        updated.snippet.thumbnails["default"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `deep batch update`() {
        val updated = item.convert { snippet.thumbnails.default.url and snippet.thumbnails.high.url }.with { IMG(it) }
        updated.snippet.thumbnails.default.url.type() shouldBe typeOf<IMG>()
        updated.snippet.thumbnails.high.url.type() shouldBe typeOf<IMG>()
    }

    @Test
    fun `deep batch update all`() {
        val updated = item.convert { cols { it.name() == "url" }.recursively() }.with { (it as? String)?.let { IMG(it) } }
        updated.snippet.thumbnails.default.url.type() shouldBe typeOf<IMG>()
        updated.snippet.thumbnails.maxres.url.type() shouldBe typeOf<IMG?>()
        updated.snippet.thumbnails.standard.url.type() shouldBe typeOf<IMG?>()
        updated.snippet.thumbnails.medium.url.type() shouldBe typeOf<IMG>()
        updated.snippet.thumbnails.high.url.type() shouldBe typeOf<IMG>()
    }

    @Test
    fun `select group`() {
        item.select { snippet.thumbnails.default }.columnsCount() shouldBe 1
        item.select { snippet.thumbnails.default.all() }.columnsCount() shouldBe 3
    }

    @Test
    fun `deep remove`() {
        val item2 = item.remove { snippet.thumbnails.default and snippet.thumbnails.maxres and snippet.channelId and etag }
        item2.columnsCount() shouldBe item.columnsCount() - 1
        item2.snippet.columnsCount() shouldBe item.snippet.columnsCount() - 1
        item2.snippet.thumbnails.columnsCount() shouldBe item.snippet.thumbnails.columnsCount() - 2
    }

    @Test
    fun `remove all from group`() {
        val item2 = item.remove { snippet.thumbnails.default and snippet.thumbnails.maxres and snippet.thumbnails.medium and snippet.thumbnails.high and snippet.thumbnails.standard }
        item2.snippet.columnsCount() shouldBe item.snippet.columnsCount() - 1
        item2.snippet.getColumnGroupOrNull("thumbnails") shouldBe null
    }

    @Test
    fun `deep move with rename`() {
        val moved = item.move { snippet.thumbnails.default }.into { snippet.path() + "movedDefault" }
        moved.snippet.thumbnails.columnNames() shouldBe item.snippet.thumbnails.remove { default }.columnNames()
        moved.snippet.columnsCount() shouldBe item.snippet.columnsCount() + 1
        (moved.snippet["movedDefault"] as ColumnGroup<*>).columnsCount() shouldBe item.snippet.thumbnails.default.columnsCount()
    }

    @Test
    fun `union`() {
        val merged = item.concat(item)
        merged.rowsCount() shouldBe item.rowsCount() * 2
        val group = merged.snippet
        group.rowsCount() shouldBe item.snippet.rowsCount() * 2
        group.columnNames() shouldBe item.snippet.columnNames()
    }

    @Test
    fun `select with rename`() {
        val selected = item.select { snippet.thumbnails.default.url into "default" and (snippet.thumbnails.maxres.url named "maxres") }
        selected.columnNames() shouldBe listOf("default", "maxres")
        selected["default"].toList() shouldBe item.snippet.thumbnails.default.url.toList()
        selected["maxres"].toList() shouldBe item.snippet.thumbnails.maxres.url.toList()
    }

    @Test
    fun `aggregate by column`() {
        val res = typed.asGroupBy { items }.aggregate {
            this into "items"
            minBy { snippet.publishedAt }.snippet into "earliest"
        }

        res.columnsCount() shouldBe typed.columnsCount() + 1
        res.getColumnIndex("earliest") shouldBe typed.getColumnIndex("items") + 1

        val expected = typed.items.map { it.snippet.minBy { publishedAt } }.toList()
        res["earliest"].toList() shouldBe expected
    }
}

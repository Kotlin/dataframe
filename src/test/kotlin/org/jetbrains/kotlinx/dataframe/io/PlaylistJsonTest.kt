package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.getColumnGroupOrNull
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

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

    fun generateExtensionProperties(): List<String> {
        val types = listOf(
            DataFrameType1::class,
            DataFrameType2::class,
            DataFrameType3::class,
            DataFrameType4::class,
            DataFrameType5::class,
            DataFrameType6::class,
            DataFrameType7::class,
            DataFrameType8::class,
            DataFrameType9::class,
            DataFrameType10::class,
            DataRecord::class
        )
        val codeGen = ReplCodeGenerator.create()
        return types.map { codeGen.process(it) }
    }

    val path = "data/playlistItems.json"
    val df = DataFrame.read(path)
    val typed = df.cast<DataRecord>()
    val item = typed.items[0]

    @Test
    fun `deep update`() {
        val updated = item.convert { snippet.thumbnails.default.url }.with { IMG(it) }
        updated.snippet.thumbnails.default.url.type() shouldBe getType<IMG>()
    }

    @Test
    fun `deep update group`() {
        val updated = item.convert { snippet.thumbnails.default }.with { it.url }
        updated.snippet.thumbnails["default"].type() shouldBe getType<String>()
    }

    @Test
    fun `deep batch update`() {
        val updated = item.convert { snippet.thumbnails.default.url and snippet.thumbnails.high.url }.with { IMG(it) }
        updated.snippet.thumbnails.default.url.type() shouldBe getType<IMG>()
        updated.snippet.thumbnails.high.url.type() shouldBe getType<IMG>()
    }

    @Test
    fun `deep batch update all`() {
        val updated = item.convert { dfs { it.name == "url" } }.with { (it as? String)?.let { IMG(it) } }
        updated.snippet.thumbnails.default.url.type() shouldBe getType<IMG>()
        updated.snippet.thumbnails.maxres.url.type() shouldBe getType<IMG?>()
        updated.snippet.thumbnails.standard.url.type() shouldBe getType<IMG?>()
        updated.snippet.thumbnails.medium.url.type() shouldBe getType<IMG>()
        updated.snippet.thumbnails.high.url.type() shouldBe getType<IMG>()
    }

    @Test
    fun `select group`() {
        item.select { snippet.thumbnails.default }.ncol shouldBe 1
        item.select { snippet.thumbnails.default.all() }.ncol shouldBe 3
    }

    @Test
    fun `deep remove`() {
        val item2 = item.remove { snippet.thumbnails.default and snippet.thumbnails.maxres and snippet.channelId and etag }
        item2.ncol shouldBe item.ncol - 1
        item2.snippet.ncol shouldBe item.snippet.ncol - 1
        item2.snippet.thumbnails.ncol shouldBe item.snippet.thumbnails.ncol - 2
    }

    @Test
    fun `remove all from group`() {
        val item2 = item.remove { snippet.thumbnails.default and snippet.thumbnails.maxres and snippet.thumbnails.medium and snippet.thumbnails.high and snippet.thumbnails.standard }
        item2.snippet.ncol shouldBe item.snippet.ncol - 1
        item2.snippet.getColumnGroupOrNull("thumbnails") shouldBe null
    }

    @Test
    fun `deep move with rename`() {
        val moved = item.move { snippet.thumbnails.default }.into { snippet.path() + "movedDefault" }
        moved.snippet.thumbnails.columnNames() shouldBe item.snippet.thumbnails.remove { default }.columnNames()
        moved.snippet.ncol shouldBe item.snippet.ncol + 1
        (moved.snippet["movedDefault"] as ColumnGroup<*>).ncol shouldBe item.snippet.thumbnails.default.ncol
    }

    @Test
    fun `union`() {
        val merged = item.concat(item)
        merged.nrow shouldBe item.nrow * 2
        val group = merged.snippet
        group.nrow shouldBe item.snippet.nrow * 2
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

        res.ncol shouldBe typed.ncol + 1
        res.getColumnIndex("earliest") shouldBe typed.getColumnIndex("items") + 1

        val expected = typed.items.map { it.snippet.minBy { publishedAt } }.toList()
        res["earliest"].toList() shouldBe expected
    }
}

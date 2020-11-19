package org.jetbrains.dataframe.io

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.junit.Test

class PlaylistJsonTest {

    @DataFrameType(isOpen = false)
    interface DataFrameType4 {
        val url: String
        val width: Int
        val height: Int
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType5 {
        val url: String
        val width: Int
        val height: Int
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType6 {
        val url: String
        val width: Int
        val height: Int
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType7 {
        val url: String?
        val width: Int?
        val height: Int?
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType8 {
        val url: String?
        val width: Int?
        val height: Int?
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType3 {
        val default: TypedDataFrameRow<DataFrameType4>
        val medium: TypedDataFrameRow<DataFrameType5>
        val high: TypedDataFrameRow<DataFrameType6>
        val standard: TypedDataFrameRow<DataFrameType7>
        val maxres: TypedDataFrameRow<DataFrameType8>
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType9 {
        val kind: String
        val videoId: String
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType2 {
        val publishedAt: String
        val channelId: String
        val title: String
        val description: String
        val thumbnails: TypedDataFrameRow<DataFrameType3>
        val channelTitle: String
        val playlistId: String
        val position: Int
        val resourceId: TypedDataFrameRow<DataFrameType9>
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType1 {
        val kind: String
        val etag: String
        val id: String
        val snippet: TypedDataFrameRow<DataFrameType2>
    }

    @DataFrameType(isOpen = false)
    interface DataFrameType10 {
        val totalResults: Int
        val resultsPerPage: Int
    }

    @DataFrameType
    interface DataRecord {
        val kind: String
        val etag: String
        val nextPageToken: String
        val items: TypedDataFrame<DataFrameType1>
        val pageInfo: TypedDataFrameRow<DataFrameType10>
    }

    val DataFrameBase<DataFrameType1>.etag: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType1_etag") get() = this["etag"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType1>.etag: String @JvmName("DataFrameType1_etag") get() = this["etag"] as String
    val DataFrameBase<DataFrameType1>.id: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType1_id") get() = this["id"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType1>.id: String @JvmName("DataFrameType1_id") get() = this["id"] as String
    val DataFrameBase<DataFrameType1>.kind: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType1_kind") get() = this["kind"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType1>.kind: String @JvmName("DataFrameType1_kind") get() = this["kind"] as String
    val DataFrameBase<DataFrameType1>.snippet: org.jetbrains.dataframe.GroupedColumnBase<DataFrameType2> @JvmName("DataFrameType1_snippet") get() = this["snippet"] as org.jetbrains.dataframe.GroupedColumnBase<DataFrameType2>
    val DataFrameRowBase<DataFrameType1>.snippet: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType2> @JvmName("DataFrameType1_snippet") get() = this["snippet"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType2>
    val DataFrameBase<DataFrameType2>.channelId: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType2_channelId") get() = this["channelId"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType2>.channelId: String @JvmName("DataFrameType2_channelId") get() = this["channelId"] as String
    val DataFrameBase<DataFrameType2>.channelTitle: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType2_channelTitle") get() = this["channelTitle"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType2>.channelTitle: String @JvmName("DataFrameType2_channelTitle") get() = this["channelTitle"] as String
    val DataFrameBase<DataFrameType2>.description: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType2_description") get() = this["description"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType2>.description: String @JvmName("DataFrameType2_description") get() = this["description"] as String
    val DataFrameBase<DataFrameType2>.playlistId: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType2_playlistId") get() = this["playlistId"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType2>.playlistId: String @JvmName("DataFrameType2_playlistId") get() = this["playlistId"] as String
    val DataFrameBase<DataFrameType2>.position: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType2_position") get() = this["position"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType2>.position: Int @JvmName("DataFrameType2_position") get() = this["position"] as Int
    val DataFrameBase<DataFrameType2>.publishedAt: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType2_publishedAt") get() = this["publishedAt"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType2>.publishedAt: String @JvmName("DataFrameType2_publishedAt") get() = this["publishedAt"] as String
    val DataFrameBase<DataFrameType2>.resourceId: org.jetbrains.dataframe.GroupedColumnBase<DataFrameType9> @JvmName("DataFrameType2_resourceId") get() = this["resourceId"] as org.jetbrains.dataframe.GroupedColumnBase<DataFrameType9>
    val DataFrameRowBase<DataFrameType2>.resourceId: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType9> @JvmName("DataFrameType2_resourceId") get() = this["resourceId"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType9>
    val DataFrameBase<DataFrameType2>.thumbnails: org.jetbrains.dataframe.GroupedColumnBase<DataFrameType3> @JvmName("DataFrameType2_thumbnails") get() = this["thumbnails"] as org.jetbrains.dataframe.GroupedColumnBase<DataFrameType3>
    val DataFrameRowBase<DataFrameType2>.thumbnails: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType3> @JvmName("DataFrameType2_thumbnails") get() = this["thumbnails"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType3>
    val DataFrameBase<DataFrameType2>.title: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType2_title") get() = this["title"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType2>.title: String @JvmName("DataFrameType2_title") get() = this["title"] as String
    val DataFrameBase<DataFrameType3>.default: GroupedColumnBase<DataFrameType4> @JvmName("DataFrameType3_default") get() = this["default"] as GroupedColumnBase<DataFrameType4>
    val DataFrameRowBase<DataFrameType3>.default: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType4> @JvmName("DataFrameType3_default") get() = this["default"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType4>
    val DataFrameBase<DataFrameType3>.high: org.jetbrains.dataframe.GroupedColumnBase<DataFrameType6> @JvmName("DataFrameType3_high") get() = this["high"] as org.jetbrains.dataframe.GroupedColumnBase<DataFrameType6>
    val DataFrameRowBase<DataFrameType3>.high: TypedDataFrameRow<DataFrameType6> @JvmName("DataFrameType3_high") get() = this["high"] as TypedDataFrameRow<DataFrameType6>
    val DataFrameBase<DataFrameType3>.maxres: GroupedColumnBase<DataFrameType8> @JvmName("DataFrameType3_maxres") get() = this["maxres"] as GroupedColumnBase<DataFrameType8>
    val DataFrameRowBase<DataFrameType3>.maxres: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType8> @JvmName("DataFrameType3_maxres") get() = this["maxres"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType8>
    val DataFrameBase<DataFrameType3>.medium: org.jetbrains.dataframe.GroupedColumnBase<DataFrameType5> @JvmName("DataFrameType3_medium") get() = this["medium"] as org.jetbrains.dataframe.GroupedColumnBase<DataFrameType5>
    val DataFrameRowBase<DataFrameType3>.medium: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType5> @JvmName("DataFrameType3_medium") get() = this["medium"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType5>
    val DataFrameBase<DataFrameType3>.standard: org.jetbrains.dataframe.GroupedColumnBase<DataFrameType7> @JvmName("DataFrameType3_standard") get() = this["standard"] as org.jetbrains.dataframe.GroupedColumnBase<DataFrameType7>
    val DataFrameRowBase<DataFrameType3>.standard: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType7> @JvmName("DataFrameType3_standard") get() = this["standard"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType7>
    val DataFrameBase<DataFrameType4>.height: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType4_height") get() = this["height"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType4>.height: Int @JvmName("DataFrameType4_height") get() = this["height"] as Int
    val DataFrameBase<DataFrameType4>.url: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType4_url") get() = this["url"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType4>.url: String @JvmName("DataFrameType4_url") get() = this["url"] as String
    val DataFrameBase<DataFrameType4>.width: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType4_width") get() = this["width"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType4>.width: Int @JvmName("DataFrameType4_width") get() = this["width"] as Int
    val DataFrameBase<DataFrameType5>.height: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType5_height") get() = this["height"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType5>.height: Int @JvmName("DataFrameType5_height") get() = this["height"] as Int
    val DataFrameBase<DataFrameType5>.url: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType5_url") get() = this["url"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType5>.url: String @JvmName("DataFrameType5_url") get() = this["url"] as String
    val DataFrameBase<DataFrameType5>.width: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType5_width") get() = this["width"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType5>.width: Int @JvmName("DataFrameType5_width") get() = this["width"] as Int
    val DataFrameBase<DataFrameType6>.height: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType6_height") get() = this["height"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType6>.height: Int @JvmName("DataFrameType6_height") get() = this["height"] as Int
    val DataFrameBase<DataFrameType6>.url: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType6_url") get() = this["url"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType6>.url: String @JvmName("DataFrameType6_url") get() = this["url"] as String
    val DataFrameBase<DataFrameType6>.width: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType6_width") get() = this["width"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType6>.width: Int @JvmName("DataFrameType6_width") get() = this["width"] as Int
    val DataFrameBase<DataFrameType7>.height: org.jetbrains.dataframe.ColumnData<kotlin.Int?> @JvmName("DataFrameType7_height") get() = this["height"] as org.jetbrains.dataframe.ColumnData<kotlin.Int?>
    val DataFrameRowBase<DataFrameType7>.height: Int? @JvmName("DataFrameType7_height") get() = this["height"] as Int?
    val DataFrameBase<DataFrameType7>.url: org.jetbrains.dataframe.ColumnData<kotlin.String?> @JvmName("DataFrameType7_url") get() = this["url"] as org.jetbrains.dataframe.ColumnData<kotlin.String?>
    val DataFrameRowBase<DataFrameType7>.url: String? @JvmName("DataFrameType7_url") get() = this["url"] as String?
    val DataFrameBase<DataFrameType7>.width: org.jetbrains.dataframe.ColumnData<kotlin.Int?> @JvmName("DataFrameType7_width") get() = this["width"] as org.jetbrains.dataframe.ColumnData<kotlin.Int?>
    val DataFrameRowBase<DataFrameType7>.width: Int? @JvmName("DataFrameType7_width") get() = this["width"] as Int?
    val DataFrameBase<DataFrameType8>.height: org.jetbrains.dataframe.ColumnData<kotlin.Int?> @JvmName("DataFrameType8_height") get() = this["height"] as org.jetbrains.dataframe.ColumnData<kotlin.Int?>
    val DataFrameRowBase<DataFrameType8>.height: Int? @JvmName("DataFrameType8_height") get() = this["height"] as Int?
    val DataFrameBase<DataFrameType8>.url: org.jetbrains.dataframe.ColumnData<kotlin.String?> @JvmName("DataFrameType8_url") get() = this["url"] as org.jetbrains.dataframe.ColumnData<kotlin.String?>
    val DataFrameRowBase<DataFrameType8>.url: String? @JvmName("DataFrameType8_url") get() = this["url"] as String?
    val DataFrameBase<DataFrameType8>.width: org.jetbrains.dataframe.ColumnData<kotlin.Int?> @JvmName("DataFrameType8_width") get() = this["width"] as org.jetbrains.dataframe.ColumnData<kotlin.Int?>
    val DataFrameRowBase<DataFrameType8>.width: Int? @JvmName("DataFrameType8_width") get() = this["width"] as Int?
    val DataFrameBase<DataFrameType9>.kind: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType9_kind") get() = this["kind"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType9>.kind: String @JvmName("DataFrameType9_kind") get() = this["kind"] as String
    val DataFrameBase<DataFrameType9>.videoId: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataFrameType9_videoId") get() = this["videoId"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataFrameType9>.videoId: String @JvmName("DataFrameType9_videoId") get() = this["videoId"] as String
    val DataFrameBase<DataFrameType10>.resultsPerPage: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType10_resultsPerPage") get() = this["resultsPerPage"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType10>.resultsPerPage: Int @JvmName("DataFrameType10_resultsPerPage") get() = this["resultsPerPage"] as Int
    val DataFrameBase<DataFrameType10>.totalResults: org.jetbrains.dataframe.ColumnData<kotlin.Int> @JvmName("DataFrameType10_totalResults") get() = this["totalResults"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
    val DataFrameRowBase<DataFrameType10>.totalResults: Int @JvmName("DataFrameType10_totalResults") get() = this["totalResults"] as Int
    val DataFrameBase<DataRecord>.etag: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataRecord_etag") get() = this["etag"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataRecord>.etag: String @JvmName("DataRecord_etag") get() = this["etag"] as String
    val DataFrameBase<DataRecord>.items: org.jetbrains.dataframe.ColumnData<org.jetbrains.dataframe.TypedDataFrame<DataFrameType1>> @JvmName("DataRecord_items") get() = this["items"] as org.jetbrains.dataframe.ColumnData<org.jetbrains.dataframe.TypedDataFrame<DataFrameType1>>
    val DataFrameRowBase<DataRecord>.items: org.jetbrains.dataframe.TypedDataFrame<DataFrameType1> @JvmName("DataRecord_items") get() = this["items"] as org.jetbrains.dataframe.TypedDataFrame<DataFrameType1>
    val DataFrameBase<DataRecord>.kind: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataRecord_kind") get() = this["kind"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataRecord>.kind: String @JvmName("DataRecord_kind") get() = this["kind"] as String
    val DataFrameBase<DataRecord>.nextPageToken: org.jetbrains.dataframe.ColumnData<kotlin.String> @JvmName("DataRecord_nextPageToken") get() = this["nextPageToken"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
    val DataFrameRowBase<DataRecord>.nextPageToken: String @JvmName("DataRecord_nextPageToken") get() = this["nextPageToken"] as String
    val DataFrameBase<DataRecord>.pageInfo: org.jetbrains.dataframe.GroupedColumnBase<DataFrameType10> @JvmName("DataRecord_pageInfo") get() = this["pageInfo"] as org.jetbrains.dataframe.GroupedColumnBase<DataFrameType10>
    val DataFrameRowBase<DataRecord>.pageInfo: org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType10> @JvmName("DataRecord_pageInfo") get() = this["pageInfo"] as org.jetbrains.dataframe.TypedDataFrameRow<DataFrameType10>

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
                DataRecord::class)
        val codeGen = CodeGenerator()
        return types.flatMap { codeGen.generate(it) }
    }

    val path = "data/playlistItems.json"
    val df = TypedDataFrame.fromJson(path)
    val typed = df.typed<DataRecord>()
    val item = typed.items[0]

    @Test
    fun `deep update`() {

        val updated = item.update { snippet.thumbnails.default.url }.with {Image(it)}
        updated.snippet.thumbnails.default.url.type shouldBe getType<Image>()
    }

    @Test
    fun `deep update group`() {

        val updated = item.update { snippet.thumbnails.default }.with { it.url }
        updated.snippet.thumbnails["default"].type shouldBe getType<String>()
    }

    @Test
    fun `deep batch update`() {

        val updated = item.update { snippet.thumbnails.default.url and snippet.thumbnails.high.url }.with { Image(it) }
        updated.snippet.thumbnails.default.url.type shouldBe getType<Image>()
        updated.snippet.thumbnails.high.url.type shouldBe getType<Image>()
    }

    @Test
    fun `deep batch update all`() {

        val updated = item.update { colsDfs { it.name == "url" }  }.with { (it as? String)?.let{ Image(it) } }
        updated.snippet.thumbnails.default.url.type shouldBe getType<Image>()
        updated.snippet.thumbnails.maxres.url.type shouldBe getType<Image?>()
        updated.snippet.thumbnails.standard.url.type shouldBe getType<Image?>()
        updated.snippet.thumbnails.medium.url.type shouldBe getType<Image>()
        updated.snippet.thumbnails.high.url.type shouldBe getType<Image>()
    }

    @Test
    fun `select group`(){

        val selected = item.select { snippet.thumbnails.default }
        selected.ncol shouldBe 3
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
        item2.snippet.asDataFrame().tryGetColumnGroup("thumbnails") shouldBe null
    }

    @Test
    fun `deep move with rename`() {

        val moved = item.move { snippet.thumbnails.default }.into { snippet + "movedDefault" }
        moved.snippet.thumbnails.asDataFrame().columnNames() shouldBe item.snippet.thumbnails.asDataFrame().remove { default }.columnNames()
        moved.snippet.ncol shouldBe item.snippet.ncol + 1
        (moved.snippet["movedDefault"] as GroupedColumn<*>).ncol shouldBe item.snippet.thumbnails.default.ncol
    }

    @Test
    fun `union`(){
        val merged = item.union(item)
        merged.nrow shouldBe item.nrow * 2
        val group = merged.snippet.asDataFrame()
        group.nrow shouldBe item.snippet.asDataFrame().nrow * 2
        group.columnNames() shouldBe item.snippet.asDataFrame().columnNames()
    }

    @Test
    fun `select with rename`(){
        val selected = item.select { snippet.thumbnails.default.url.rename("default") and snippet.thumbnails.maxres.url.rename("maxres") }
        selected.columnNames() shouldBe listOf("default", "maxres")
        selected["default"].valuesList() shouldBe item.snippet.thumbnails.default.url.valuesList()
        selected["maxres"].valuesList() shouldBe item.snippet.thumbnails.maxres.url.valuesList()
    }
}
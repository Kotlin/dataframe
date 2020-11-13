package org.jetbrains.dataframe.io

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

    val DataFrameBase<DataRecord>.etag: ColumnData<String> get() = this["etag"] as ColumnData<String>
    val DataFrameRowBase<DataRecord>.etag: String get() = this["etag"] as String
    val DataFrameBase<DataRecord>.items: ColumnData<TypedDataFrame<DataFrameType1>> get() = this["items"] as ColumnData<TypedDataFrame<DataFrameType1>>
    val DataFrameRowBase<DataRecord>.items: TypedDataFrame<DataFrameType1> get() = this["items"] as TypedDataFrame<DataFrameType1>
    val DataFrameBase<DataRecord>.kind: ColumnData<String> get() = this["kind"] as ColumnData<String>
    val DataFrameRowBase<DataRecord>.kind: String get() = this["kind"] as String
    val DataFrameBase<DataRecord>.nextPageToken: ColumnData<String> get() = this["nextPageToken"] as ColumnData<String>
    val DataFrameRowBase<DataRecord>.nextPageToken: String get() = this["nextPageToken"] as String
    val DataFrameBase<DataRecord>.pageInfo: GroupedColumnBase<DataFrameType10> get() = this["pageInfo"] as GroupedColumnBase<DataFrameType10>
    val DataFrameRowBase<DataRecord>.pageInfo: TypedDataFrameRow<DataFrameType10> get() = this["pageInfo"] as TypedDataFrameRow<DataFrameType10>

    @Test
    fun readJson() {

        val path = "data/playlistItems.json"
        val df = TypedDataFrame.fromJson(path)
        df.typed<DataRecord>()
    }
}
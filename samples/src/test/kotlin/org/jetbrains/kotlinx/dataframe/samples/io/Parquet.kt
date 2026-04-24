@file:Suppress("UNUSED_VARIABLE", "unused", "RedundantVisibilityModifier")

package org.jetbrains.kotlinx.dataframe.samples.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.io.readParquet
import org.jetbrains.kotlinx.dataframe.testParquet
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

private const val ARROW_PARQUET_DEFAULT_BATCH_SIZE: Long = 1000

class Parquet {

    interface ReadParquetOverloads {
        // SampleStart
        // 1) URLs
        public fun DataFrame.Companion.readParquet(
            vararg urls: URL,
            nullability: NullabilityOptions = NullabilityOptions.Infer,
            batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
        ): AnyFrame

        // 2) Strings (interpreted as file paths or URLs, e.g., "data/file.parquet", "file://", or "http(s)://")
        public fun DataFrame.Companion.readParquet(
            vararg strUrls: String,
            nullability: NullabilityOptions = NullabilityOptions.Infer,
            batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
        ): AnyFrame

        // 3) Paths
        public fun DataFrame.Companion.readParquet(
            vararg paths: Path,
            nullability: NullabilityOptions = NullabilityOptions.Infer,
            batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
        ): AnyFrame

        // 4) Files
        public fun DataFrame.Companion.readParquet(
            vararg files: File,
            nullability: NullabilityOptions = NullabilityOptions.Infer,
            batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
        ): AnyFrame
        // SampleEnd
    }

    @Ignore
    @Test
    fun readParquet() {
        // SampleStart
        // Read from file paths (as strings)
        val df = DataFrame.readParquet("data/sales.parquet")
        // SampleEnd
    }

    @Test
    fun readParquetURL() {
        val url = testParquet("sales")

        // SampleStart
        // Read from URLs
        val df = DataFrame.readParquet(url)
        // SampleEnd
        df.rowsCount() shouldBe 300
        df.columnsCount() shouldBe 20
    }

    @Test
    fun readParquetFilePath() {
        val url = testParquet("sales")
        val path = Paths.get(url.toURI())
        // SampleStart
        val df = DataFrame.readParquet(path)
        // SampleEnd
        df.rowsCount() shouldBe 300
        df.columnsCount() shouldBe 20
    }

    @Test
    fun readParquetFile() {
        val url = testParquet("sales")
        val file = File(url.toURI())

        // SampleStart
        // Read from File objects
        val df = DataFrame.readParquet(file)
        // SampleEnd
        df.rowsCount() shouldBe 300
        df.columnsCount() shouldBe 20
    }

    @Test
    fun readParquetFileWithParameters() {
        val url = testParquet("sales")
        val file = File(url.toURI())

        // SampleStart
        val df = DataFrame.readParquet(
            file,
            nullability = NullabilityOptions.Infer,
            batchSize = 64L * 1024,
        )
        // SampleEnd
        df.rowsCount() shouldBe 300
        df.columnsCount() shouldBe 20
    }

    @Test
    fun readMultipleParquetFiles() {
        val url = testParquet("sales")
        val file = File(url.toURI())
        val file1 = File(url.toURI())
        val file2 = File(url.toURI())

        // SampleStart
        val df = DataFrame.readParquet(file, file1, file2)
        // SampleEnd
        df.rowsCount() shouldBe 900
        df.columnsCount() shouldBe 20
    }
}

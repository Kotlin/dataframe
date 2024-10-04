package org.jetbrains.kotlinx.dataframe.io

import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.ZipInputStream

/**
 * Compression algorithm to use when reading csv files.
 * We support GZIP and ZIP compression out of the box.
 *
 * Custom compression algorithms can be added by creating an instance of [Custom].
 */
public sealed class CsvCompression<I : InputStream>(public open val wrapStream: (InputStream) -> I) :
    (InputStream) -> I by wrapStream {

    public data object Gzip : CsvCompression<GZIPInputStream>(::GZIPInputStream)

    public data object Zip : CsvCompression<ZipInputStream>(::ZipInputStream)

    public data object None : CsvCompression<InputStream>({ it })

    public data class Custom<I : InputStream>(override val wrapStream: (InputStream) -> I) :
        CsvCompression<I>(wrapStream)
}

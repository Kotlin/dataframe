package org.jetbrains.kotlinx.dataframe.io

import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import java.util.zip.ZipInputStream

/**
 * Compression algorithm to use when reading csv files.
 * We support GZIP and ZIP compression out of the box.
 *
 * Custom decompression algorithms can be added by creating an instance of [Custom].
 *
 * @param wrapStream function that wraps any [InputStream] into a decompressing [InflaterInputStream] stream
 */
public sealed class Compression<I : InputStream>(public open val wrapStream: (InputStream) -> I) :
    (InputStream) -> I by wrapStream {

    /** Can be overridden to perform some actions before reading from the input stream. */
    public open fun doFirst(inputStream: I) {}

    /**
     * Can be overridden to perform some actions after reading from the input stream.
     * Remember to close the stream if you override this function.
     */
    public open fun doFinally(inputStream: I) {
        inputStream.close()
    }

    /**
     * For .gz / GZIP files.
     */
    public data object Gzip : Compression<GZIPInputStream>(::GZIPInputStream)

    /**
     * For .zip / ZIP files.
     */
    public data object Zip : Compression<ZipInputStream>(::ZipInputStream) {

        override fun doFirst(inputStream: ZipInputStream) {
            super.doFirst(inputStream)

            // Make sure to call nextEntry once to prepare the stream
            if (inputStream.nextEntry == null) error("No entries in zip file")
        }

        override fun doFinally(inputStream: ZipInputStream) {
            // Check we don't have more than one entry in the zip file
            if (inputStream.nextEntry != null) {
                inputStream.close()
                throw IllegalArgumentException("Zip file contains more than one entry")
            }
            inputStream.close()
        }
    }

    /**
     * No compression.
     */
    public data object None : Compression<InputStream>({ it })

    /**
     * Custom decompression algorithm.
     * @param wrapStream function that wraps any [InputStream] into a decompressing [InflaterInputStream] stream
     */
    public data class Custom<I : InflaterInputStream>(override val wrapStream: (InputStream) -> I) :
        Compression<I>(wrapStream)
}

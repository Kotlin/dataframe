package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.ipc.ArrowFileWriter
import org.apache.arrow.vector.ipc.ArrowStreamWriter
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.channels.Channels
import java.nio.channels.WritableByteChannel

public val ignoreMismatchMessage: (ConvertingMismatch) -> Unit = { message: ConvertingMismatch -> }
public val writeMismatchMessage: (ConvertingMismatch) -> Unit = { message: ConvertingMismatch ->
    System.err.println(message)
}

private val logger = LoggerFactory.getLogger(ArrowWriter::class.java)

public val logMismatchMessage: (ConvertingMismatch) -> Unit = { message: ConvertingMismatch ->
    logger.debug(message.toString())
}

/**
 * Save [dataFrame] content in Apache Arrow format (can be written to File, ByteArray, OutputStream or raw Channel) with [targetSchema].
 * If [dataFrame] content does not match with [targetSchema], behaviour is specified by [mode], mismatches would be sent to [mismatchSubscriber]
 */
public interface ArrowWriter : AutoCloseable {
    public val dataFrame: DataFrame<*>
    public val targetSchema: Schema
    public val mode: Mode
    public val mismatchSubscriber: (ConvertingMismatch) -> Unit

    public companion object {

        public fun create(
            dataFrame: AnyFrame,
            targetSchema: Schema,
            mode: Mode,
            mismatchSubscriber: (ConvertingMismatch) -> Unit = ignoreMismatchMessage
        ): ArrowWriter = ArrowWriterImpl(dataFrame, targetSchema, mode, mismatchSubscriber)

        /**
         * If [restrictWidening] is true, [dataFrame] columns not described in [targetSchema] would not be saved (otherwise, would be saved as is).
         * If [restrictNarrowing] is true, [targetSchema] fields that are not nullable and do not exist in [dataFrame] will produce exception (otherwise, would not be saved).
         * If [strictType] is true, [dataFrame] columns described in [targetSchema] with non-compatible type will produce exception (otherwise, would be saved as is).
         * If [strictNullable] is true, [targetSchema] fields that are not nullable and contain nulls in [dataFrame] will produce exception (otherwise, would be saved as is with nullable = true).
         */
        public data class Mode(
            public val restrictWidening: Boolean,
            public val restrictNarrowing: Boolean,
            public val strictType: Boolean,
            public val strictNullable: Boolean
        ) {
            public companion object {
                public val STRICT: Mode = Mode(true, true, true, true)
                public val LOYAL: Mode = Mode(false, false, false, false)
            }
        }
    }

    /**
     * Create Arrow [VectorSchemaRoot] with [dataFrame] content cast to [targetSchema] according to the [mode].
     */
    public fun allocateVectorSchemaRoot(): VectorSchemaRoot

    // IPC saving block

    /**
     * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to opened [channel].
     */
    public fun writeArrowIPC(channel: WritableByteChannel) {
        allocateVectorSchemaRoot().use { vectorSchemaRoot ->
            ArrowStreamWriter(vectorSchemaRoot, null, channel).use { writer ->
                writer.writeBatch()
            }
        }
    }

    /**
     * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to opened [stream].
     */
    public fun writeArrowIPC(stream: OutputStream) {
        writeArrowIPC(Channels.newChannel(stream))
    }

    /**
     * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to new or existing [file].
     * If file exists, it can be recreated or expanded.
     */
    public fun writeArrowIPC(file: File, append: Boolean = true) {
        writeArrowIPC(FileOutputStream(file, append))
    }

    /**
     * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to new [ByteArray]
     */
    public fun saveArrowIPCToByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        writeArrowIPC(stream)
        return stream.toByteArray()
    }

    // Feather saving block

    /**
     * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to opened [channel].
     */
    public fun writeArrowFeather(channel: WritableByteChannel) {
        allocateVectorSchemaRoot().use { vectorSchemaRoot ->
            ArrowFileWriter(vectorSchemaRoot, null, channel).use { writer ->
                writer.writeBatch()
            }
        }
    }

    /**
     * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to opened [stream].
     */
    public fun writeArrowFeather(stream: OutputStream) {
        writeArrowFeather(Channels.newChannel(stream))
    }

    /**
     * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to new or existing [file].
     * If file exists, it would be recreated.
     */
    public fun writeArrowFeather(file: File) {
        writeArrowFeather(FileOutputStream(file))
    }

    /**
     * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to new [ByteArray]
     */
    public fun saveArrowFeatherToByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        writeArrowFeather(stream)
        return stream.toByteArray()
    }
}

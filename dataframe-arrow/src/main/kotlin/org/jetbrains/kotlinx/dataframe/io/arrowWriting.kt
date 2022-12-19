package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyFrame
import java.io.File
import java.io.OutputStream
import java.nio.channels.WritableByteChannel

/**
 * Create [ArrowWriter] for [this] DataFrame with target schema matching actual data
 */
public fun AnyFrame.arrowWriter(): ArrowWriter = this.arrowWriter(this.columns().toArrowSchema())

/**
 * Create [ArrowWriter] for [this] DataFrame with explicit [targetSchema].
 * If DataFrame does not match with [targetSchema], behaviour is specified by [mode], mismatches would be sent to [mismatchSubscriber]
 */
public fun AnyFrame.arrowWriter(
    targetSchema: Schema,
    mode: ArrowWriter.Mode = ArrowWriter.Mode.STRICT,
    mismatchSubscriber: (ConvertingMismatch) -> Unit = ignoreMismatchMessage,
): ArrowWriter = ArrowWriter.create(this, targetSchema, mode, mismatchSubscriber)

// IPC saving block with default parameters

/**
 * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to opened [channel].
 */
public fun AnyFrame.writeArrowIPC(channel: WritableByteChannel) {
    this.arrowWriter().use { writer ->
        writer.writeArrowIPC(channel)
    }
}

/**
 * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to opened [stream].
 */
public fun AnyFrame.writeArrowIPC(stream: OutputStream) {
    this.arrowWriter().use { writer ->
        writer.writeArrowIPC(stream)
    }
}

/**
 * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to new or existing [file].
 * If file exists, it can be recreated or expanded.
 */
public fun AnyFrame.writeArrowIPC(file: File, append: Boolean = true) {
    this.arrowWriter().use { writer ->
        writer.writeArrowIPC(file, append)
    }
}

/**
 * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to new [ByteArray]
 */
public fun AnyFrame.saveArrowIPCToByteArray(): ByteArray {
    return this.arrowWriter().use { writer ->
        writer.saveArrowIPCToByteArray()
    }
}

// Feather saving block with default parameters

/**
 * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to opened [channel].
 */
public fun AnyFrame.writeArrowFeather(channel: WritableByteChannel) {
    this.arrowWriter().use { writer ->
        writer.writeArrowFeather(channel)
    }
}

/**
 * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to opened [stream].
 */
public fun AnyFrame.writeArrowFeather(stream: OutputStream) {
    this.arrowWriter().use { writer ->
        writer.writeArrowFeather(stream)
    }
}

/**
 * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to new or existing [file].
 * If file exists, it would be recreated.
 */
public fun AnyFrame.writeArrowFeather(file: File) {
    this.arrowWriter().use { writer ->
        writer.writeArrowFeather(file)
    }
}

/**
 * Save data to [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files), write to new [ByteArray]
 */
public fun AnyFrame.saveArrowFeatherToByteArray(): ByteArray {
    return this.arrowWriter().use { writer ->
        writer.saveArrowFeatherToByteArray()
    }
}

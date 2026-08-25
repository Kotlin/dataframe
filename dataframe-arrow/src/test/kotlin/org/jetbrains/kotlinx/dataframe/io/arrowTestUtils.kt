package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.ipc.ArrowFileWriter
import org.apache.arrow.vector.ipc.ArrowStreamWriter
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.Schema
import java.io.ByteArrayOutputStream
import java.nio.channels.Channels

/** Serializes an already-populated [VectorSchemaRoot] to the Arrow IPC (streaming) format. */
internal fun VectorSchemaRoot.toArrowStreamBytes(): ByteArray =
    ByteArrayOutputStream().also { out ->
        ArrowStreamWriter(this, null, Channels.newChannel(out)).use {
            it.start()
            it.writeBatch()
        }
    }.toByteArray()

/** Serializes an already-populated [VectorSchemaRoot] to the Arrow Feather (random-access file) format. */
internal fun VectorSchemaRoot.toArrowFeatherBytes(): ByteArray =
    ByteArrayOutputStream().also { out ->
        ArrowFileWriter(this, null, Channels.newChannel(out)).use {
            it.start()
            it.writeBatch()
        }
    }.toByteArray()

/**
 * Builds an Arrow [VectorSchemaRoot] for [fields], populates it with [fill], and returns the serialized bytes —
 * Arrow IPC (streaming) by default, or the Feather (random-access file) format when [feather] is `true`.
 *
 * Centralizes the `RootAllocator` / `VectorSchemaRoot` / writer ceremony so tests only describe their data.
 * Populating vectors directly (validity bits and child buffers set independently) is intentional: it is the only
 * way to craft layouts — e.g. a null struct parent over non-zero child values — that the higher-level writers
 * (the DataFrame Arrow writer, DuckDB) never emit.
 */
internal fun arrowBytes(vararg fields: Field, feather: Boolean = false, fill: (VectorSchemaRoot) -> Unit): ByteArray =
    RootAllocator().use { allocator ->
        VectorSchemaRoot.create(Schema(fields.toList()), allocator).use { root ->
            fill(root)
            if (feather) root.toArrowFeatherBytes() else root.toArrowStreamBytes()
        }
    }

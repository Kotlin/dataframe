package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinLocalTime
import org.apache.arrow.dataset.file.FileFormat
import org.apache.arrow.dataset.file.FileSystemDatasetFactory
import org.apache.arrow.dataset.jni.DirectReservationListener
import org.apache.arrow.dataset.jni.NativeMemoryPool
import org.apache.arrow.dataset.scanner.ScanOptions
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.BigIntVector
import org.apache.arrow.vector.BitVector
import org.apache.arrow.vector.DateDayVector
import org.apache.arrow.vector.DateMilliVector
import org.apache.arrow.vector.Decimal256Vector
import org.apache.arrow.vector.DecimalVector
import org.apache.arrow.vector.DurationVector
import org.apache.arrow.vector.FieldVector
import org.apache.arrow.vector.Float4Vector
import org.apache.arrow.vector.Float8Vector
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.LargeVarBinaryVector
import org.apache.arrow.vector.LargeVarCharVector
import org.apache.arrow.vector.NullVector
import org.apache.arrow.vector.SmallIntVector
import org.apache.arrow.vector.TimeMicroVector
import org.apache.arrow.vector.TimeMilliVector
import org.apache.arrow.vector.TimeNanoVector
import org.apache.arrow.vector.TimeSecVector
import org.apache.arrow.vector.TimeStampMicroTZVector
import org.apache.arrow.vector.TimeStampMicroVector
import org.apache.arrow.vector.TimeStampMilliTZVector
import org.apache.arrow.vector.TimeStampMilliVector
import org.apache.arrow.vector.TimeStampNanoTZVector
import org.apache.arrow.vector.TimeStampNanoVector
import org.apache.arrow.vector.TimeStampSecTZVector
import org.apache.arrow.vector.TimeStampSecVector
import org.apache.arrow.vector.TimeStampVector
import org.apache.arrow.vector.TinyIntVector
import org.apache.arrow.vector.UInt1Vector
import org.apache.arrow.vector.UInt2Vector
import org.apache.arrow.vector.UInt4Vector
import org.apache.arrow.vector.UInt8Vector
import org.apache.arrow.vector.VarBinaryVector
import org.apache.arrow.vector.VarCharVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.ViewVarBinaryVector
import org.apache.arrow.vector.ViewVarCharVector
import org.apache.arrow.vector.complex.LargeListVector
import org.apache.arrow.vector.complex.ListVector
import org.apache.arrow.vector.complex.StructVector
import org.apache.arrow.vector.ipc.ArrowFileReader
import org.apache.arrow.vector.ipc.ArrowReader
import org.apache.arrow.vector.ipc.ArrowStreamReader
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.util.DateUtility
import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.NullabilityException
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.applyNullability
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.asList
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.toKotlinDuration
import java.time.LocalTime as JavaLocalTime

/**
 * same as [Iterable<DataFrame<T>>.concat()] without internal type guessing (all batches should have the same schema)
 */
internal fun <T> Iterable<DataFrame<T>>.concatKeepingSchema(): DataFrame<T> {
    val dataFrames = asList()
    when (dataFrames.size) {
        0 -> return DataFrame.empty().cast()
        1 -> return dataFrames[0]
    }

    val columnPaths = dataFrames.first()
        .getColumnsWithPaths { colsAtAnyDepth().filter { !it.isColumnGroup() } }
        .map { it.path }

    val totalRows = dataFrames.sumOf { it.count() }
    val columns = columnPaths.map { path ->
        val values = dataFrames.flatMapTo(ArrayList(totalRows)) { it.getColumn(path).values() }
        val hasNulls = dataFrames.any { it.getColumn(path).hasNulls() }
        val col = dataFrames[0][path]
        if (col.isFrameColumn()) {
            path to DataColumn.createFrameColumn(path.name(), values as List<AnyFrame>, schema = col.schema)
        } else {
            path to DataColumn.createValueColumn(path.name(), values, col.type().withNullability(hasNulls))
        }
    }
    return columns.toDataFrameFromPairs()
}

private fun BitVector.values(range: IntRange): List<Boolean?> = range.map { getObject(it) }

private fun UInt1Vector.values(range: IntRange): List<Short?> = range.map { getObjectNoOverflow(it) }

private fun UInt2Vector.values(range: IntRange): List<Int?> = range.map { getObject(it)?.code }

private fun UInt4Vector.values(range: IntRange): List<Long?> = range.map { getObjectNoOverflow(it) }

private fun UInt8Vector.values(range: IntRange): List<BigInteger?> = range.map { getObjectNoOverflow(it) }

private fun TinyIntVector.values(range: IntRange): List<Byte?> = range.map { getObject(it) }

private fun SmallIntVector.values(range: IntRange): List<Short?> = range.map { getObject(it) }

private fun IntVector.values(range: IntRange): List<Int?> = range.map { getObject(it) }

private fun BigIntVector.values(range: IntRange): List<Long?> = range.map { getObject(it) }

private fun DecimalVector.values(range: IntRange): List<BigDecimal?> = range.map { getObject(it) }

private fun Decimal256Vector.values(range: IntRange): List<BigDecimal?> = range.map { getObject(it) }

private fun Float4Vector.values(range: IntRange): List<Float?> = range.map { getObject(it) }

private fun Float8Vector.values(range: IntRange): List<Double?> = range.map { getObject(it) }

// `getObject` returns `null` for an unset slot, so it must be navigated safely — every neighbouring helper
// guards against nulls, and omitting it here threw a NullPointerException on any nullable duration column.
private fun DurationVector.values(range: IntRange): List<Duration?> = range.map { getObject(it)?.toKotlinDuration() }

private fun DateDayVector.values(range: IntRange): List<LocalDate?> =
    range.map {
        if (getObject(it) == null) {
            null
        } else {
            DateUtility.getLocalDateTimeFromEpochMilli(getObject(it).toLong() * DateUtility.daysToStandardMillis)
                .toLocalDate()
                .toKotlinLocalDate()
        }
    }

private fun DateMilliVector.values(range: IntRange): List<LocalDateTime?> =
    range.map { getObject(it)?.toKotlinLocalDateTime() }

private fun TimeNanoVector.values(range: IntRange): List<LocalTime?> =
    range.mapIndexed { i, it ->
        if (isNull(i)) {
            null
        } else {
            JavaLocalTime.ofNanoOfDay(get(it)).toKotlinLocalTime()
        }
    }

private fun TimeMicroVector.values(range: IntRange): List<LocalTime?> =
    range.mapIndexed { i, it ->
        if (isNull(i)) {
            null
        } else {
            JavaLocalTime.ofNanoOfDay(getObject(it) * 1000).toKotlinLocalTime()
        }
    }

private fun TimeMilliVector.values(range: IntRange): List<LocalTime?> =
    range.mapIndexed { i, it ->
        if (isNull(i)) {
            null
        } else {
            JavaLocalTime.ofNanoOfDay(get(it).toLong() * 1000_000).toKotlinLocalTime()
        }
    }

private fun TimeSecVector.values(range: IntRange): List<LocalTime?> =
    range.map { getObject(it)?.let { JavaLocalTime.ofSecondOfDay(it.toLong()).toKotlinLocalTime() } }

private fun TimeStampNanoVector.values(range: IntRange): List<LocalDateTime?> =
    range.mapIndexed { i, it ->
        if (isNull(i)) {
            null
        } else {
            getObject(it).toKotlinLocalDateTime()
        }
    }

private fun TimeStampMicroVector.values(range: IntRange): List<LocalDateTime?> =
    range.mapIndexed { i, it ->
        if (isNull(i)) {
            null
        } else {
            getObject(it).toKotlinLocalDateTime()
        }
    }

private fun TimeStampMilliVector.values(range: IntRange): List<LocalDateTime?> =
    range.mapIndexed { i, it ->
        if (isNull(i)) {
            null
        } else {
            getObject(it).toKotlinLocalDateTime()
        }
    }

private fun TimeStampSecVector.values(range: IntRange): List<LocalDateTime?> =
    range.mapIndexed { i, it ->
        if (isNull(i)) {
            null
        } else {
            getObject(it).toKotlinLocalDateTime()
        }
    }

/**
 * Reads a timestamp vector that carries a time zone as [Instant]s.
 *
 * An Arrow timestamp *with* a time zone (`ArrowType.Timestamp(unit, tz)`, which is what Parquet's
 * `isAdjustedToUTC = true` becomes) stores the offset from the Unix epoch already normalized to UTC, so every
 * value identifies a single point on the time-line. The field's time zone is display metadata only — two files
 * describing the same instants, one tagged `UTC` and one `Europe/Brussels`, hold the same numbers and must read
 * back equal — so it is deliberately not applied here.
 *
 * Zone-less timestamps ([TimeStampNanoVector] and friends, `isAdjustedToUTC = false`) are calendar-and-clock
 * readings that identify no such point, and stay [LocalDateTime]. See
 * [Parquet logical types](https://github.com/apache/parquet-format/blob/master/LogicalTypes.md#timestamp).
 */
private fun TimeStampVector.instantValues(range: IntRange): List<Instant?> {
    val unitsPerSecond = (field.type as ArrowType.Timestamp).unit.perSecond
    return range.map { if (isNull(it)) null else epochToInstant(get(it), unitsPerSecond) }
}

/**
 * Converts an epoch offset expressed in `1 / [unitsPerSecond]` of a second into an [Instant].
 *
 * Divides with `floorDiv`/`mod` so that pre-1970 (negative) offsets keep a non-negative nanosecond adjustment.
 */
private fun epochToInstant(value: Long, unitsPerSecond: Long): Instant =
    Instant.fromEpochSeconds(
        epochSeconds = value.floorDiv(unitsPerSecond),
        nanosecondAdjustment = value.mod(unitsPerSecond) * (NANOS_PER_SECOND / unitsPerSecond),
    )

private fun NullVector.values(range: IntRange): List<Nothing?> =
    range.map {
        getObject(it) as Nothing?
    }

private fun VarCharVector.values(range: IntRange): List<String?> =
    range.map {
        if (isNull(it)) {
            null
        } else {
            String(get(it))
        }
    }

private fun LargeVarCharVector.values(range: IntRange): List<String?> =
    range.map {
        if (isNull(it)) {
            null
        } else {
            String(get(it))
        }
    }

private fun ViewVarCharVector.values(range: IntRange): List<String?> =
    range.map {
        if (isNull(it)) {
            null
        } else {
            String(get(it))
        }
    }

private fun VarBinaryVector.values(range: IntRange): List<ByteArray?> =
    range.map {
        if (isNull(it)) {
            null
        } else {
            get(it)
        }
    }

private fun LargeVarBinaryVector.values(range: IntRange): List<ByteArray?> =
    range.map {
        if (isNull(it)) {
            null
        } else {
            get(it)
        }
    }

private fun ViewVarBinaryVector.values(range: IntRange): List<ByteArray?> =
    range.map {
        if (isNull(it)) {
            null
        } else {
            get(it)
        }
    }

internal fun nothingType(nullable: Boolean): KType =
    if (nullable) {
        typeOf<List<Nothing?>>()
    } else {
        typeOf<List<Nothing>>()
    }.arguments.first().type!!

private inline fun <reified T> List<T?>.withTypeNullable(
    expectedNulls: Boolean,
    nullabilityOptions: NullabilityOptions,
): Pair<List<T?>, KType> {
    val nullable = nullabilityOptions.applyNullability(this, expectedNulls)
    val type = if (nullable) typeOf<T?>() else typeOf<T>()
    return this to type
}

@JvmName("withTypeNullableNothingList")
private fun List<Nothing?>.withTypeNullable(
    expectedNulls: Boolean,
    nullabilityOptions: NullabilityOptions,
): Pair<List<Nothing?>, KType> {
    val nullable = nullabilityOptions.applyNullability(this, expectedNulls)
    return this to nothingType(nullable)
}

/**
 * Propagates an Arrow struct's parent-level null into its child columns, returning a copy of this column with the
 * cells marked `true` in [isNull] set to `null`. A [ColumnGroup] has no per-row null mask ("a column group is never
 * null, instead, make the columns inside nullable"), so the parent-null has nowhere to live on the group itself and
 * must be pushed down onto the leaves.
 *
 * Two representation constraints shape the result:
 * - a [FrameColumn] cannot hold `null`, so a null row becomes an empty [DataFrame] carrying the column's original
 *   schema — the nested list genuinely does not exist for that row, yet its columns stay typed;
 * - the null is structurally mandated by the parent, so value cells are nulled without re-running [applyNullability],
 *   which would otherwise reject a structurally-required null under [NullabilityOptions.Checking].
 *
 * Callers invoke this only when [isNull] contains at least one `true`.
 */
private fun AnyBaseCol.injectNullsAt(isNull: BooleanArray): AnyBaseCol =
    when (kind()) {
        ColumnKind.Group ->
            DataColumn.createColumnGroup(
                name = name(),
                df = (this as ColumnGroup<*>).columns().map { it.injectNullsAt(isNull) }.toDataFrame(),
            )

        ColumnKind.Frame -> {
            val frameColumn = this as FrameColumn<*>
            val emptyFrame = DataFrame.empty(frameColumn.schema.value)
            DataColumn.createFrameColumn(
                name = name(),
                groups = frameColumn.toList().mapIndexed { i, frame -> if (isNull[i]) emptyFrame else frame },
                schema = frameColumn.schema,
            )
        }

        ColumnKind.Value ->
            DataColumn.createValueColumn(
                name = name(),
                values = toList().mapIndexed { i, value -> if (isNull[i]) null else value },
                type = type().withNullability(true),
                infer = Infer.None,
            )
    }

/**
 * The struct's own per-row null mask over [range] — Arrow keeps a struct validity buffer independent of its
 * child vectors — or `null` when this slice has no null-parent rows, so required groups are read as before.
 */
private fun StructVector.nullMaskOrNull(range: IntRange): BooleanArray? =
    if (nullCount > 0) {
        BooleanArray(range.count()) { isNull(range.first + it) }.takeIf { mask -> mask.any { it } }
    } else {
        null
    }

private fun readField(
    vector: FieldVector,
    field: Field,
    nullability: NullabilityOptions,
    range: IntRange = (0 until vector.valueCount),
): AnyBaseCol {
    try {
        if (vector is StructVector) {
            // An Arrow struct carries its own validity buffer, independent of the child vectors. Under a
            // null-parent slot the physical child values are unspecified (zeros, or leaked from other rows),
            // so reading them as-is produces phantom data; injectNullsAt pushes the parent-null down onto the
            // children instead. When the struct has null rows we also read its children with Widening: their
            // physically-null cells at those rows are about to be nulled anyway and must not trip
            // NullabilityOptions.Checking. Required groups (no struct-level nulls) are read exactly as before.
            val nullMask = vector.nullMaskOrNull(range)
            val childNullability = if (nullMask != null) NullabilityOptions.Widening else nullability
            val columns = field.children.map { childField ->
                val child = readField(vector.getChild(childField.name), childField, childNullability, range)
                if (nullMask != null) child.injectNullsAt(nullMask) else child
            }
            return DataColumn.createColumnGroup(field.name, columns.toDataFrame())
        }
        if (vector is LargeListVector) {
            return readListVector(vector.asAccessor(), field, range, nullability)
        }
        if (vector is ListVector) {
            return readListVector(vector.asAccessor(), field, range, nullability)
        }
        val (list, type) = when (vector) {
            is VarCharVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is LargeVarCharVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is ViewVarCharVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is VarBinaryVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is LargeVarBinaryVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is ViewVarBinaryVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is BitVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is SmallIntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TinyIntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is UInt1Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is UInt2Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is UInt4Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is UInt8Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is IntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is BigIntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is DecimalVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is Decimal256Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is Float8Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is Float4Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is DurationVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is DateDayVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is DateMilliVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeNanoVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeMicroVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeMilliVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeSecVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeStampNanoVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeStampMicroVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeStampMilliVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            is TimeStampSecVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            // Every zone-tagged unit shares one reader; see [instantValues] for why the zone is not applied.
            is TimeStampNanoTZVector, is TimeStampMicroTZVector, is TimeStampMilliTZVector, is TimeStampSecTZVector ->
                (vector as TimeStampVector).instantValues(range).withTypeNullable(field.isNullable, nullability)

            is NullVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)

            else -> {
                throw NotImplementedError("reading from ${vector.javaClass.canonicalName} is not implemented")
            }
        }
        return DataColumn.createValueColumn(field.name, list, type, Infer.None)
    } catch (unexpectedNull: NullabilityException) {
        throw IllegalArgumentException("Column `${field.name}` should be not nullable but has nulls")
    }
}

private fun readListVector(
    accessor: ListVectorAccessor,
    field: Field,
    range: IntRange,
    nullability: NullabilityOptions,
): AnyBaseCol {
    val dataVector = accessor.dataVector
    return if (dataVector is StructVector) {
        val structField = field.children.single()
        // A struct *element* can itself be null inside the list; like the top-level struct path we honor
        // its own validity buffer so null elements don't materialize phantom child values.
        val frames = range.map { i ->
            val start = accessor.getElementStartIndex(i)
            val end = accessor.getElementEndIndex(i)
            val columns = structField.children.map { childField ->
                readField(
                    dataVector.getChild(childField.name),
                    childField,
                    NullabilityOptions.Widening,
                    start until end,
                )
            }
            val nullMask = dataVector.nullMaskOrNull(start until end)
            val elementColumns = if (nullMask != null) columns.map { it.injectNullsAt(nullMask) } else columns
            elementColumns.toDataFrame()
        }
        DataColumn.createFrameColumn(field.name, frames)
    } else {
        val elementField = field.children.single()
        val fieldsData = range.map { i ->
            if (accessor.isNull(i)) {
                null
            } else {
                val start = accessor.getElementStartIndex(i)
                val end = accessor.getElementEndIndex(i)
                readField(dataVector, elementField, nullability, start until end)
            }
        }
        val sampleColumn = fieldsData.firstOrNull { it != null }
        val elementType = sampleColumn?.type() ?: nullableNothingType

        val listNullable = nullability.applyNullability(fieldsData, field.isNullable)

        val listType = List::class.createType(
            arguments = listOf(KTypeProjection.invariant(elementType)),
            nullable = listNullable,
        )

        DataColumn.createValueColumn(field.name, fieldsData.map { it?.values() }, listType)
    }
}

private interface ListVectorAccessor {
    val dataVector: FieldVector

    fun getElementStartIndex(index: Int): Int

    fun getElementEndIndex(index: Int): Int

    fun isNull(index: Int): Boolean
}

private fun ListVector.asAccessor() =
    object : ListVectorAccessor {
        override val dataVector: FieldVector get() = this@asAccessor.dataVector

        override fun getElementStartIndex(index: Int) = this@asAccessor.getElementStartIndex(index)

        override fun getElementEndIndex(index: Int) = this@asAccessor.getElementEndIndex(index)

        override fun isNull(index: Int) = this@asAccessor.isNull(index)
    }

// Arrow in Java doesn't support allocating 64-bit-indexed vectors itself
private fun LargeListVector.asAccessor() =
    object : ListVectorAccessor {
        override val dataVector: FieldVector get() = this@asAccessor.dataVector

        override fun getElementStartIndex(index: Int) = Math.toIntExact(this@asAccessor.getElementStartIndex(index))

        override fun getElementEndIndex(index: Int) = Math.toIntExact(this@asAccessor.getElementEndIndex(index))

        override fun isNull(index: Int) = this@asAccessor.isNull(index)
    }

internal val nullableNothingType: KType = typeOf<List<Nothing?>>().arguments.first().type!!

private fun readField(root: VectorSchemaRoot, field: Field, nullability: NullabilityOptions): AnyBaseCol =
    readField(root.getVector(field), field, nullability)

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format) data from existing [channel]
 */
internal fun DataFrame.Companion.readArrowIPCImpl(
    channel: ReadableByteChannel,
    allocator: RootAllocator = Allocator.ROOT,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowImpl(ArrowStreamReader(channel, allocator), nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files) data from existing [channel]
 */
internal fun DataFrame.Companion.readArrowFeatherImpl(
    channel: SeekableByteChannel,
    allocator: RootAllocator = Allocator.ROOT,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowImpl(ArrowFileReader(channel, allocator), nullability)

/**
 * Read [Arrow any format](https://arrow.apache.org/java/current/ipc.html#reading-writing-ipc-formats) data from existing [reader]
 */
internal fun DataFrame.Companion.readArrowImpl(
    reader: ArrowReader,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame {
    reader.use {
        val flattened = buildList {
            when (reader) {
                is ArrowFileReader -> {
                    reader.recordBlocks.forEach { block ->
                        reader.loadRecordBatch(block)
                        val root = reader.vectorSchemaRoot
                        val schema = root.schema
                        val df = schema.fields.map { f -> readField(root, f, nullability) }.toDataFrame()
                        add(df)
                    }
                }

                else -> {
                    val root = reader.vectorSchemaRoot
                    val schema = root.schema
                    while (reader.loadNextBatch()) {
                        val df = schema.fields.map { f -> readField(root, f, nullability) }.toDataFrame()
                        add(df)
                    }
                }
            }
        }
        return flattened.concatKeepingSchema()
    }
}

private fun resolveArrowDatasetUris(fileUris: Array<String>): Array<String> =
    fileUris.map {
        when {
            it.startsWith("http:", true) -> {
                val url = URI.create(it).toURL()
                val tempFile = File.createTempFile("kdf", ".parquet")
                tempFile.deleteOnExit()
                url.openStream().use { input ->
                    Files.copy(input, tempFile.toPath())
                    tempFile.toURI().toString()
                }
            }

            !it.startsWith("file:", true) && File(it).exists() -> {
                File(it).toURI().toString()
            }

            else -> it
        }
    }.toTypedArray()

/**
 * Read [Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html) from [fileUris]
 */
internal fun DataFrame.Companion.readArrowDatasetImpl(
    fileUris: Array<String>,
    fileFormat: FileFormat,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame {
    val scanOptions = ScanOptions(batchSize)
    RootAllocator().use { allocator ->
        FileSystemDatasetFactory(
            allocator,
            NativeMemoryPool.createListenable(DirectReservationListener.instance()),
            fileFormat,
            resolveArrowDatasetUris(fileUris),
        ).use { datasetFactory ->
            datasetFactory.finish().use { dataset ->
                dataset.newScan(scanOptions).use { scanner ->
                    scanner.scanBatches().use { reader ->
                        return readArrowImpl(reader, nullability)
                    }
                }
            }
        }
    }
}

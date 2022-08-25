package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.BaseFixedWidthVector
import org.apache.arrow.vector.BaseVariableWidthVector
import org.apache.arrow.vector.FieldVector
import org.apache.arrow.vector.FixedWidthVector
import org.apache.arrow.vector.LargeVarCharVector
import org.apache.arrow.vector.TinyIntVector
import org.apache.arrow.vector.SmallIntVector
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.BigIntVector
import org.apache.arrow.vector.BitVector
import org.apache.arrow.vector.DateDayVector
import org.apache.arrow.vector.DateMilliVector
import org.apache.arrow.vector.DecimalVector
import org.apache.arrow.vector.Decimal256Vector
import org.apache.arrow.vector.Float4Vector
import org.apache.arrow.vector.Float8Vector
import org.apache.arrow.vector.TimeMicroVector
import org.apache.arrow.vector.TimeMilliVector
import org.apache.arrow.vector.TimeNanoVector
import org.apache.arrow.vector.TimeSecVector
import org.apache.arrow.vector.VariableWidthVector
import org.apache.arrow.vector.VarCharVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.ipc.ArrowFileWriter
import org.apache.arrow.vector.ipc.ArrowStreamWriter
import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.convertToBoolean
import org.jetbrains.kotlinx.dataframe.api.convertToBigDecimal
import org.jetbrains.kotlinx.dataframe.api.convertToDouble
import org.jetbrains.kotlinx.dataframe.api.convertToFloat
import org.jetbrains.kotlinx.dataframe.api.convertToLong
import org.jetbrains.kotlinx.dataframe.api.convertToInt
import org.jetbrains.kotlinx.dataframe.api.convertToLocalDate
import org.jetbrains.kotlinx.dataframe.api.convertToLocalTime
import org.jetbrains.kotlinx.dataframe.api.convertToLocalDateTime
import org.jetbrains.kotlinx.dataframe.api.convertToString
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConversionException
import org.jetbrains.kotlinx.dataframe.typeClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.channels.Channels
import java.nio.channels.WritableByteChannel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

private val ignoreWarningMessage: (String) -> Unit = { message: String -> }
private val writeWarningMessage: (String) -> Unit = {message: String -> System.err.println(message)}

/**
 * Create Arrow [Schema] matching [this] actual data.
 * Columns with not supported types will be interpreted as String
 */
public fun List<AnyCol>.toArrowSchema(warningSubscriber: (String) -> Unit = ignoreWarningMessage): Schema {
    val fields = this.map { column ->
        val columnType = column.type()
        val nullable = columnType.isMarkedNullable
        when {
            columnType.isSubtypeOf(typeOf<String?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Utf8(), null), emptyList())

            columnType.isSubtypeOf(typeOf<Boolean?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Bool(), null), emptyList())

            columnType.isSubtypeOf(typeOf<Byte?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Int(8, true), null), emptyList())

            columnType.isSubtypeOf(typeOf<Short?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Int(16, true), null), emptyList())

            columnType.isSubtypeOf(typeOf<Int?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Int(32, true), null), emptyList())

            columnType.isSubtypeOf(typeOf<Long?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Int(64, true), null), emptyList())

            columnType.isSubtypeOf(typeOf<Float?>()) -> Field(column.name(), FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), null), emptyList())

            columnType.isSubtypeOf(typeOf<Double?>()) -> Field(column.name(), FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), null), emptyList())

            columnType.isSubtypeOf(typeOf<LocalDate?>()) || columnType.isSubtypeOf(typeOf<kotlinx.datetime.LocalDate?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Date(DateUnit.DAY), null), emptyList())

            columnType.isSubtypeOf(typeOf<LocalDateTime?>()) || columnType.isSubtypeOf(typeOf<kotlinx.datetime.LocalDateTime?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Date(DateUnit.MILLISECOND), null), emptyList())

            columnType.isSubtypeOf(typeOf<LocalTime?>()) -> Field(column.name(), FieldType(nullable, ArrowType.Time(TimeUnit.NANOSECOND, 64), null), emptyList())

            else -> {
                warningSubscriber("Column ${column.name()} has type ${column.typeClass.java.canonicalName}, will be saved as String")
                Field(column.name(), FieldType(true, ArrowType.Utf8(), null), emptyList())
            }
        }
    }
    return Schema(fields)
}

/**
 * Create [ArrowWriter] for [this] DataFrame with target schema matching actual data
 */
public fun DataFrame<*>.arrowWriter(): ArrowWriter = this.arrowWriter(this.columns().toArrowSchema())

/**
 * Create [ArrowWriter] for [this] DataFrame with explicit [targetSchema]
 */
public fun DataFrame<*>.arrowWriter(
    targetSchema: Schema,
    mode: ArrowWriter.Companion.Mode = ArrowWriter.Companion.Mode.STRICT,
    warningSubscriber: (String) -> Unit = ignoreWarningMessage
): ArrowWriter = ArrowWriter(this, targetSchema, mode, warningSubscriber)

/**
 * Save [dataFrame] content in Apache Arrow format (can be written to File, ByteArray, OutputStream or raw Channel) with [targetSchema].
 * If [dataFrame] content does not match with [targetSchema], behaviour is specified by [mode]
 */
public class ArrowWriter(
    private val dataFrame: DataFrame<*>,
    private val targetSchema: Schema,
    private val mode: Mode,
    private val warningSubscriber: (String) -> Unit = ignoreWarningMessage
): AutoCloseable {

    public companion object {
        /**
         * If [restrictWidening] is true, [dataFrame] columns not described in [targetSchema] would not be saved (otherwise, would be saved as is).
         * If [restrictNarrowing] is true, [targetSchema] fields that are not nullable and do not exist in [dataFrame] will produce exception (otherwise, would not be saved).
         * If [strictType] is true, [dataFrame] columns described in [targetSchema] with non-compatible type will produce exception (otherwise, would be saved as is).
         * If [strictNullable] is true, [targetSchema] fields that are not nullable and contain nulls in [dataFrame] will produce exception (otherwise, would be saved as is with nullable = true).
         */
        public class Mode(
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

    private val allocator = RootAllocator()

    private fun allocateVector(vector: FieldVector, size: Int) {
        when (vector) {
            is FixedWidthVector -> vector.allocateNew(size)
            is VariableWidthVector -> vector.allocateNew(size)
            else -> TODO("Not implemented for ${vector.javaClass.canonicalName}")
        }
    }

    private fun infillWithNulls(vector: FieldVector, size: Int) {
        when (vector) {
            is BaseFixedWidthVector -> for ( i in 0 until size) { vector.setNull(i) }
            is BaseVariableWidthVector -> for ( i in 0 until size) { vector.setNull(i) }
            else -> TODO("Not implemented for ${vector.javaClass.canonicalName}")
        }
        vector.valueCount = size
    }

    private fun convertColumnToTarget(column: AnyCol?, targetFieldType: ArrowType): AnyCol? {
        if (column == null) return null
        return when (targetFieldType) {
            ArrowType.Utf8() -> column.convertToString()
            ArrowType.LargeUtf8() -> column.convertToString()
            ArrowType.Binary(), ArrowType.LargeBinary() -> TODO("Saving var binary is currently not implemented")
            ArrowType.Bool() -> column.convertToBoolean()
            ArrowType.Int(8, true) -> column.convertTo<Byte>()
            ArrowType.Int(16, true) -> column.convertTo<Short>()
            ArrowType.Int(32, true) -> column.convertTo<Int>()
            ArrowType.Int(64, true) -> column.convertTo<Long>()
//            ArrowType.Int(8, false), ArrowType.Int(16, false), ArrowType.Int(32, false), ArrowType.Int(64, false) -> todo
            is ArrowType.Decimal -> column.convertToBigDecimal()
            ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE) -> column.convertToFloat()
            ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE) -> column.convertToDouble()
            ArrowType.Date(DateUnit.DAY) -> column.convertToLocalDate()
            ArrowType.Date(DateUnit.MILLISECOND) -> column.convertToLocalDateTime()
            is ArrowType.Time -> column.convertToLocalTime()
//            is ArrowType.Duration -> todo
//            is ArrowType.Struct -> todo
            else -> {
                TODO("Saving ${targetFieldType.javaClass.canonicalName} is not implemented")
            }
        }
    }

    private fun infillVector(vector: FieldVector, column: AnyCol) {
        when (vector) {
            is VarCharVector -> column.convertToString().forEachIndexed { i, value -> value?.let { vector.set(i, Text(value)); value} ?: vector.setNull(i) }
            is LargeVarCharVector -> column.convertToString().forEachIndexed { i, value -> value?.let { vector.set(i, Text(value)); value} ?: vector.setNull(i) }
//            is VarBinaryVector -> todo
//            is LargeVarBinaryVector -> todo
            is BitVector -> column.convertToBoolean().forEachIndexed { i, value -> value?.let { vector.set(i, value.compareTo(false)); value} ?: vector.setNull(i) }
            is TinyIntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is SmallIntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is IntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is BigIntVector -> column.convertToLong().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
//            is UInt1Vector -> todo
//            is UInt2Vector -> todo
//            is UInt4Vector -> todo
//            is UInt8Vector -> todo
            is DecimalVector -> column.convertToBigDecimal().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is Decimal256Vector -> column.convertToBigDecimal().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is Float8Vector -> column.convertToDouble().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is Float4Vector -> column.convertToFloat().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }

            is DateDayVector -> column.convertToLocalDate().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toJavaLocalDate().toEpochDay()).toInt()); value} ?: vector.setNull(i) }
            is DateMilliVector -> column.convertToLocalDateTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toInstant(TimeZone.UTC).toEpochMilliseconds()); value} ?: vector.setNull(i) }
//            is DurationVector -> todo
            is TimeNanoVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toNanoOfDay()); value} ?: vector.setNull(i) }
            is TimeMicroVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toNanoOfDay() / 1000); value} ?: vector.setNull(i) }
            is TimeMilliVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toNanoOfDay() / 1000 / 1000).toInt()); value} ?: vector.setNull(i) }
            is TimeSecVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toNanoOfDay() / 1000 / 1000 / 1000).toInt()); value} ?: vector.setNull(i) }
//            is StructVector -> todo
            else -> {
                TODO("Saving to ${vector.javaClass.canonicalName} is not implemented")
            }
        }

        vector.valueCount = dataFrame.rowsCount()

    }

    /**
     * Create Arrow FieldVector with [column] content cast to [field] type according to [strictType] and [strictNullable] settings.
     */
    private fun allocateVectorAndInfill(field: Field, column: AnyCol?, strictType: Boolean, strictNullable: Boolean): FieldVector {
        val containNulls = (column == null || column.hasNulls())
        // Convert the column to type specified in field. (If we already have target type, convertTo will do nothing)
        val (convertedColumn, actualField) =  try {
            convertColumnToTarget(column, field.type) to field
        } catch (e: TypeConversionException) {
            if (strictType) {
                // If conversion failed but strictType is enabled, throw the exception
                throw e
            } else {
                // If strictType is not enabled, use original data with its type. Target nullable is saved at this step.
                warningSubscriber(e.message)
                val actualType = listOf(column!!).toArrowSchema(warningSubscriber).fields.first().fieldType.type
                val actualField = Field(field.name, FieldType(field.isNullable, actualType, field.fieldType.dictionary), field.children)
                column to actualField
            }
        }
        val vector = if (!actualField.isNullable && containNulls) {
            if (strictNullable) {
                throw IllegalArgumentException("${actualField.name} column contains nulls but should be not nullable")
            } else {
                warningSubscriber("${actualField.name} column contains nulls but expected not nullable")
                Field(actualField.name, FieldType(true, actualField.fieldType.type, actualField.fieldType.dictionary), actualField.children).createVector(allocator)!!
            }
        } else {
            actualField.createVector(allocator)!!
        }

        allocateVector(vector, dataFrame.rowsCount())
        if (convertedColumn == null) {
            check(actualField.isNullable)
            infillWithNulls(vector, dataFrame.rowsCount())
        } else {
            infillVector(vector, convertedColumn)
        }
        return vector
    }

    private fun List<AnyCol>.toVectors(): List<FieldVector> = this.toArrowSchema(warningSubscriber).fields.mapIndexed { i, field ->
        allocateVectorAndInfill(field, this[i], true, true)
    }

    /**
     * Create Arrow VectorSchemaRoot with [dataFrame] content cast to [targetSchema] according to the [mode].
     */
    private fun allocateVectorSchemaRoot(): VectorSchemaRoot {
        val mainVectors = LinkedHashMap<String, FieldVector>()
        for (field in targetSchema.fields) {
            val column = dataFrame.getColumnOrNull(field.name)
            if (column == null && !field.isNullable) {
                if (mode.restrictNarrowing) {
                    throw IllegalArgumentException("${field.name} column is not presented")
                } else {
                    warningSubscriber("${field.name} column is not presented")
                    continue
                }
            }

            val vector = allocateVectorAndInfill(field, column, mode.strictType, mode.strictNullable)
            mainVectors[field.name] = vector
        }
        val vectors = ArrayList<FieldVector>()
        vectors.addAll(mainVectors.values)
        val otherVectors = dataFrame.columns().filter { column -> !mainVectors.containsKey(column.name()) }.toVectors()
        if (!mode.restrictWidening) {
            vectors.addAll(otherVectors)
        } else {
            otherVectors.forEach { warningSubscriber("${it.name} column is not described in target schema and was ignored") }
        }
        return VectorSchemaRoot(vectors)
    }

    // IPC saving block

    /**
     * Save data to [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format), write to opened [channel].
     */
    public fun writeArrowIPC(channel: WritableByteChannel) {
        allocateVectorSchemaRoot().use { vectorSchemaRoot ->
            ArrowStreamWriter(vectorSchemaRoot, null, channel).use { writer ->
                writer.writeBatch();
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
                writer.writeBatch();
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

    override fun close() {
        allocator.close()
    }
}

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

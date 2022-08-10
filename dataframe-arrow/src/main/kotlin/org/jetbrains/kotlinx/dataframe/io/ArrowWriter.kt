package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.*
import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.typeOf

public fun List<AnyCol>.toArrowSchema(): Schema {
    val fields = this.map { column ->
        when (column.type()) {
            typeOf<String?>() -> Field(column.name(), FieldType(true, ArrowType.Utf8(), null), emptyList())
            typeOf<String>() -> Field(column.name(), FieldType(false, ArrowType.Utf8(), null), emptyList())

            typeOf<Boolean?>() -> Field(column.name(), FieldType(true, ArrowType.Bool(), null), emptyList())
            typeOf<Boolean>() -> Field(column.name(), FieldType(false, ArrowType.Bool(), null), emptyList())

            typeOf<Byte?>() -> Field(column.name(), FieldType(true, ArrowType.Int(8, true), null), emptyList())
            typeOf<Byte>() -> Field(column.name(), FieldType(false, ArrowType.Int(8, true), null), emptyList())

            typeOf<Short?>() -> Field(column.name(), FieldType(true, ArrowType.Int(16, true), null), emptyList())
            typeOf<Short>() -> Field(column.name(), FieldType(false, ArrowType.Int(16, true), null), emptyList())

            typeOf<Int?>() -> Field(column.name(), FieldType(true, ArrowType.Int(32, true), null), emptyList())
            typeOf<Int>() -> Field(column.name(), FieldType(false, ArrowType.Int(32, true), null), emptyList())

            typeOf<Long?>() -> Field(column.name(), FieldType(true, ArrowType.Int(64, true), null), emptyList())
            typeOf<Long>() -> Field(column.name(), FieldType(false, ArrowType.Int(64, true), null), emptyList())

            typeOf<Float?>() -> Field(column.name(), FieldType(true, ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), null), emptyList())
            typeOf<Float>() -> Field(column.name(), FieldType(false, ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), null), emptyList())

            typeOf<Double?>() -> Field(column.name(), FieldType(true, ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), null), emptyList())
            typeOf<Double>() -> Field(column.name(), FieldType(false, ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), null), emptyList())

            typeOf<LocalDate?>(), typeOf<kotlinx.datetime.LocalDate?>() -> Field(column.name(), FieldType(true, ArrowType.Date(DateUnit.DAY), null), emptyList())
            typeOf<LocalDate>(),  typeOf<kotlinx.datetime.LocalDate>() -> Field(column.name(), FieldType(false, ArrowType.Date(DateUnit.DAY), null), emptyList())

            typeOf<LocalDateTime?>(), typeOf<kotlinx.datetime.LocalDateTime?>() -> Field(column.name(), FieldType(true, ArrowType.Date(DateUnit.MILLISECOND), null), emptyList())
            typeOf<LocalDateTime>(), typeOf<kotlinx.datetime.LocalDateTime>() -> Field(column.name(), FieldType(false, ArrowType.Date(DateUnit.MILLISECOND), null), emptyList())

            typeOf<LocalTime?>() -> Field(column.name(), FieldType(true, ArrowType.Time(TimeUnit.NANOSECOND, 64), null), emptyList())
            typeOf<LocalTime>() -> Field(column.name(), FieldType(false, ArrowType.Time(TimeUnit.NANOSECOND, 64), null), emptyList())

            else -> Field(column.name(), FieldType(true, ArrowType.Utf8(), null), emptyList())
        }
    }
    return Schema(fields)
}

/**
 * Save [dataFrame] content in Apache Arrow format (can be written to File, ByteArray or stream) with [targetSchema].
 *
 */
public class ArrowWriter(public val dataFrame: DataFrame<*>, public val targetSchema: Schema): AutoCloseable {
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

    private fun infillVector(vector: FieldVector, column: AnyCol) {
        when (vector) {
            is VarCharVector -> column.forEachIndexed { i, value -> value?.let { vector.set(i, Text(value.toString())); value} ?: vector.setNull(i) }
            is LargeVarCharVector -> column.forEachIndexed { i, value -> value?.let { vector.set(i, Text(value.toString())); value} ?: vector.setNull(i) }
//            is VarBinaryVector -> vector.values(range).withType()
//            is LargeVarBinaryVector -> vector.values(range).withType()
            is BitVector -> column.convertToBoolean().forEachIndexed { i, value -> value?.let { vector.set(i, value.compareTo(false)); value} ?: vector.setNull(i) }
            is SmallIntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is TinyIntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
//            is UInt1Vector -> vector.values(range).withType()
//            is UInt2Vector -> vector.values(range).withType()
//            is UInt4Vector -> vector.values(range).withType()
//            is UInt8Vector -> vector.values(range).withType()
            is IntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is BigIntVector -> column.convertToLong().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is DecimalVector -> column.convertToBigDecimal().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is Decimal256Vector -> column.convertToBigDecimal().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is Float8Vector -> column.convertToDouble().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }
            is Float4Vector -> column.convertToFloat().forEachIndexed { i, value -> value?.let { vector.set(i, value); value} ?: vector.setNull(i) }

            is DateDayVector -> column.convertToLocalDate().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toJavaLocalDate().toEpochDay()).toInt()); value} ?: vector.setNull(i) }
            is DateMilliVector -> column.convertToLocalDateTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toInstant(TimeZone.UTC).toEpochMilliseconds()); value} ?: vector.setNull(i) }
//            is DurationVector -> vector.values(range).withType()
            is TimeNanoVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toNanoOfDay()); value} ?: vector.setNull(i) }
            is TimeMicroVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toNanoOfDay() / 1000); value} ?: vector.setNull(i) }
            is TimeMilliVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toNanoOfDay() / 1000 / 1000).toInt()); value} ?: vector.setNull(i) }
            is TimeSecVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toNanoOfDay() / 1000 / 1000 / 1000).toInt()); value} ?: vector.setNull(i) }
//            is StructVector -> vector.values(range).withType()
            else -> {
                TODO("not fully implemented, ${vector.javaClass.canonicalName}")
            }
        }

        vector.valueCount = dataFrame.rowsCount()

    }

    private fun List<AnyCol>.toVectors(): List<FieldVector> {
        val actualSchema = this.toArrowSchema()
        val vectors = ArrayList<FieldVector>()
        for ((i, field) in actualSchema.fields.withIndex()) {
            val column = this[i]
            val vector = field.createVector(allocator)!!
            allocateVector(vector, dataFrame.rowsCount())
            infillVector(vector, column)
            vectors.add(vector)
        }
        return vectors
    }

        /**
     * Create Arrow VectorSchemaRoot with [dataFrame] content casted to [targetSchema].
     * If [restrictWidening] is true, [dataFrame] columns not described in [targetSchema] would not be saved (otherwise, would be saved as is).
     * If [restrictNarrowing] is true, [targetSchema] fields that are not nullable and do not exist in [dataFrame] will produce exception (otherwise, would not be saved).
     * If [strictType] is true, [dataFrame] columns described in [targetSchema] with non-compatible type will produce exception (otherwise, would be saved as is).
     * If [strictNullable] is true, [targetSchema] fields that are not nullable and contain nulls in [dataFrame] will produce exception (otherwise, would be saved as is with nullable = true).
     */
    private fun allocateVectorSchemaRoot(
        restrictWidening: Boolean = true,
        restrictNarrowing: Boolean = true,
        strictType: Boolean = true,
        strictNullable: Boolean = true
    ): VectorSchemaRoot {
        val mainVectors = LinkedHashMap<String, FieldVector>()
        for (field in targetSchema.fields) {
            val column = dataFrame.getColumnOrNull(field.name)
            val vector = field.createVector(allocator)!!
            if (column == null && !field.isNullable) {
                if (restrictNarrowing) {
                    throw Exception("${field.name} column is not presented")
                } else {
                    continue
                }
            }

            allocateVector(vector, dataFrame.rowsCount())
            if (column == null) {
                check(field.isNullable)
                infillWithNulls(vector, dataFrame.rowsCount())
            } else {
                infillVector(vector, column)
            }
            mainVectors[field.name] = vector
        }
        val vectors = ArrayList<FieldVector>()
        vectors.addAll(mainVectors.values)
        if (!restrictWidening) {
            val otherVectors = dataFrame.columns().filter { column -> !mainVectors.containsKey(column.name()) }.toVectors()
            vectors.addAll(otherVectors)
        }
        return VectorSchemaRoot(vectors)
    }

    override fun close() {
        allocator.close()
    }
}

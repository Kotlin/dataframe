package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.BaseFixedWidthVector
import org.apache.arrow.vector.BaseVariableWidthVector
import org.apache.arrow.vector.BigIntVector
import org.apache.arrow.vector.BitVector
import org.apache.arrow.vector.DateDayVector
import org.apache.arrow.vector.DateMilliVector
import org.apache.arrow.vector.Decimal256Vector
import org.apache.arrow.vector.DecimalVector
import org.apache.arrow.vector.FieldVector
import org.apache.arrow.vector.FixedWidthVector
import org.apache.arrow.vector.Float4Vector
import org.apache.arrow.vector.Float8Vector
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.LargeVarCharVector
import org.apache.arrow.vector.SmallIntVector
import org.apache.arrow.vector.TimeMicroVector
import org.apache.arrow.vector.TimeMilliVector
import org.apache.arrow.vector.TimeNanoVector
import org.apache.arrow.vector.TimeSecVector
import org.apache.arrow.vector.TinyIntVector
import org.apache.arrow.vector.VarCharVector
import org.apache.arrow.vector.VariableWidthVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.name
import org.jetbrains.kotlinx.dataframe.api.convertToBigDecimal
import org.jetbrains.kotlinx.dataframe.api.convertToBoolean
import org.jetbrains.kotlinx.dataframe.api.convertToByte
import org.jetbrains.kotlinx.dataframe.api.convertToDouble
import org.jetbrains.kotlinx.dataframe.api.convertToFloat
import org.jetbrains.kotlinx.dataframe.api.convertToInt
import org.jetbrains.kotlinx.dataframe.api.convertToLocalDate
import org.jetbrains.kotlinx.dataframe.api.convertToLocalDateTime
import org.jetbrains.kotlinx.dataframe.api.convertToLocalTime
import org.jetbrains.kotlinx.dataframe.api.convertToLong
import org.jetbrains.kotlinx.dataframe.api.convertToShort
import org.jetbrains.kotlinx.dataframe.api.convertToString
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.exceptions.CellConversionException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException

/**
 * Save [dataFrame] content in Apache Arrow format (can be written to File, ByteArray, OutputStream or raw Channel) with [targetSchema].
 * If [dataFrame] content does not match with [targetSchema], behaviour is specified by [mode], mismatches would be sent to [mismatchSubscriber]
 */
internal class ArrowWriterImpl(
    override val dataFrame: DataFrame<*>,
    override val targetSchema: Schema,
    override val mode: ArrowWriter.Companion.Mode,
    override val mismatchSubscriber: (ConvertingMismatch) -> Unit = ignoreMismatchMessage
) : ArrowWriter {

    private val allocator = RootAllocator()

    private fun allocateVector(vector: FieldVector, size: Int) {
        when (vector) {
            is FixedWidthVector -> vector.allocateNew(size)
            is VariableWidthVector -> vector.allocateNew(size)
            else -> throw IllegalArgumentException("Can not allocate ${vector.javaClass.canonicalName}")
        }
    }

    private fun infillWithNulls(vector: FieldVector, size: Int) {
        when (vector) {
            is BaseFixedWidthVector -> for (i in 0 until size) { vector.setNull(i) }
            is BaseVariableWidthVector -> for (i in 0 until size) { vector.setNull(i) }
            else -> throw IllegalArgumentException("Can not infill ${vector.javaClass.canonicalName}")
        }
        vector.valueCount = size
    }

    private fun convertColumnToTarget(column: AnyCol?, targetFieldType: ArrowType): AnyCol? {
        if (column == null) return null
        return when (targetFieldType) {
            ArrowType.Utf8() -> column.map { it?.toString() }
            ArrowType.LargeUtf8() -> column.map { it?.toString() }
            ArrowType.Binary(), ArrowType.LargeBinary() -> throw NotImplementedError("Saving var binary is currently not implemented")
            ArrowType.Bool() -> column.convertToBoolean()
            ArrowType.Int(8, true) -> column.convertToByte()
            ArrowType.Int(16, true) -> column.convertToShort()
            ArrowType.Int(32, true) -> column.convertToInt()
            ArrowType.Int(64, true) -> column.convertToLong()
//            ArrowType.Int(8, false), ArrowType.Int(16, false), ArrowType.Int(32, false), ArrowType.Int(64, false) -> todo
            is ArrowType.Decimal -> column.convertToBigDecimal()
            ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE) -> column.convertToDouble().convertToFloat() // Use [convertToDouble] as locale logic step
            ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE) -> column.convertToDouble()
            ArrowType.Date(DateUnit.DAY) -> column.convertToLocalDate()
            ArrowType.Date(DateUnit.MILLISECOND) -> column.convertToLocalDateTime()
            is ArrowType.Time -> column.convertToLocalTime()
//            is ArrowType.Duration -> todo
//            is ArrowType.Struct -> todo
            else -> {
                throw NotImplementedError("Saving ${targetFieldType.javaClass.canonicalName} is currently not implemented")
            }
        }
    }

    private fun infillVector(vector: FieldVector, column: AnyCol) {
        when (vector) {
            is VarCharVector -> column.convertToString().forEachIndexed { i, value -> value?.let { vector.set(i, Text(value)); value } ?: vector.setNull(i) }
            is LargeVarCharVector -> column.convertToString().forEachIndexed { i, value -> value?.let { vector.set(i, Text(value)); value } ?: vector.setNull(i) }
//            is VarBinaryVector -> todo
//            is LargeVarBinaryVector -> todo
            is BitVector -> column.convertToBoolean().forEachIndexed { i, value -> value?.let { vector.set(i, value.compareTo(false)); value } ?: vector.setNull(i) }
            is TinyIntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }
            is SmallIntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }
            is IntVector -> column.convertToInt().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }
            is BigIntVector -> column.convertToLong().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }
//            is UInt1Vector -> todo
//            is UInt2Vector -> todo
//            is UInt4Vector -> todo
//            is UInt8Vector -> todo
            is DecimalVector -> column.convertToBigDecimal().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }
            is Decimal256Vector -> column.convertToBigDecimal().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }
            is Float8Vector -> column.convertToDouble().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }
            is Float4Vector -> column.convertToFloat().forEachIndexed { i, value -> value?.let { vector.set(i, value); value } ?: vector.setNull(i) }

            is DateDayVector -> column.convertToLocalDate().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toJavaLocalDate().toEpochDay()).toInt()); value } ?: vector.setNull(i) }
            is DateMilliVector -> column.convertToLocalDateTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toInstant(
                TimeZone.UTC).toEpochMilliseconds()); value } ?: vector.setNull(i) }
//            is DurationVector -> todo
            is TimeNanoVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toNanoOfDay()); value } ?: vector.setNull(i) }
            is TimeMicroVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, value.toNanoOfDay() / 1000); value } ?: vector.setNull(i) }
            is TimeMilliVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toNanoOfDay() / 1000 / 1000).toInt()); value } ?: vector.setNull(i) }
            is TimeSecVector -> column.convertToLocalTime().forEachIndexed { i, value -> value?.let { vector.set(i, (value.toNanoOfDay() / 1000 / 1000 / 1000).toInt()); value } ?: vector.setNull(i) }
//            is StructVector -> todo
            else -> {
                throw NotImplementedError("Saving to ${vector.javaClass.canonicalName} is currently not implemented")
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

        val (convertedColumn, actualField) = try {
            convertColumnToTarget(column, field.type) to field
        } catch (e: CellConversionException) {
            if (strictType) {
                // If conversion failed but strictType is enabled, throw the exception
                val mismatch = ConvertingMismatch.TypeConversionFail.ConversionFailError(e.column, e.row, e)
                mismatchSubscriber(mismatch)
                throw ConvertingException(mismatch)
            } else {
                // If strictType is not enabled, use original data with its type. Target nullable is saved at this step.
                mismatchSubscriber(ConvertingMismatch.TypeConversionFail.ConversionFailIgnored(e.column, e.row, e))
                column to column!!.toArrowField(mismatchSubscriber)
            }
        } catch (e: TypeConverterNotFoundException) {
            if (strictType) {
                // If conversion failed but strictType is enabled, throw the exception
                val mismatch = ConvertingMismatch.TypeConversionNotFound.ConversionNotFoundError(field.name, e)
                mismatchSubscriber(mismatch)
                throw ConvertingException(mismatch)
            } else {
                // If strictType is not enabled, use original data with its type. Target nullable is saved at this step.
                mismatchSubscriber(ConvertingMismatch.TypeConversionNotFound.ConversionNotFoundIgnored(field.name, e))
                column to column!!.toArrowField(mismatchSubscriber)
            }
        }

        val vector = if (!actualField.isNullable && containNulls) {
            var firstNullValue: Int? = null
            for (i in 0 until (column?.size() ?: -1)) {
                if (column!![i] == null) {
                    firstNullValue = i
                    break
                }
            }
            if (strictNullable) {
                val mismatch = ConvertingMismatch.NullableMismatch.NullValueError(actualField.name, firstNullValue)
                mismatchSubscriber(mismatch)
                throw ConvertingException(mismatch)
            } else {
                mismatchSubscriber(ConvertingMismatch.NullableMismatch.NullValueIgnored(actualField.name, firstNullValue))
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

    private fun List<AnyCol>.toVectors(): List<FieldVector> = this.map {
        val field = it.toArrowField(mismatchSubscriber)
        allocateVectorAndInfill(field, it, true, true)
    }

    override fun allocateVectorSchemaRoot(): VectorSchemaRoot {
        val mainVectors = LinkedHashMap<String, FieldVector>()
        try {
            for (field in targetSchema.fields) {
                val column = dataFrame.getColumnOrNull(field.name)
                if (column == null && !field.isNullable) {
                    if (mode.restrictNarrowing) {
                        val mismatch = ConvertingMismatch.NarrowingMismatch.NotPresentedColumnError(field.name)
                        mismatchSubscriber(mismatch)
                        throw ConvertingException(mismatch)
                    } else {
                        mismatchSubscriber(ConvertingMismatch.NarrowingMismatch.NotPresentedColumnIgnored(field.name))
                        continue
                    }
                }

                val vector = allocateVectorAndInfill(field, column, mode.strictType, mode.strictNullable)
                mainVectors[field.name] = vector
            }
        } catch (e: Exception) {
            mainVectors.values.forEach { it.close() } // Clear buffers before throwing exception
            throw e
        }
        val vectors = ArrayList<FieldVector>()
        vectors.addAll(mainVectors.values)
        val otherColumns = dataFrame.columns().filter { column -> !mainVectors.containsKey(column.name()) }
        if (!mode.restrictWidening) {
            vectors.addAll(otherColumns.toVectors())
            otherColumns.forEach {
                mismatchSubscriber(ConvertingMismatch.WideningMismatch.AddedColumn(it.name))
            }
        } else {
            otherColumns.forEach {
                mismatchSubscriber(ConvertingMismatch.WideningMismatch.RejectedColumn(it.name))
            }
        }
        return VectorSchemaRoot(vectors)
    }

    override fun close() {
        allocator.close()
    }
}

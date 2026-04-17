package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime

/**
 * Create Arrow [Field] (note: this is part of [Schema], does not contain data itself) that has the same
 * name, type and nullable as [this]
 */
public fun AnyCol.toArrowField(mismatchSubscriber: (ConvertingMismatch) -> Unit = ignoreMismatchMessage): Field {
    val column = this
    val columnType = column.type()
    return when (column) {
        is ColumnGroup<*> -> {
            ColumnSchema.Group(schema(), type()).toArrowField(column.name(), mismatchSubscriber)
        }

        else -> columnType.toArrowField(column.name(), mismatchSubscriber)
    }
}

internal fun ColumnSchema.toArrowField(name: String, mismatchSubscriber: (ConvertingMismatch) -> Unit): Field =
    when (this) {
        is ColumnSchema.Value -> type.toArrowField(name, mismatchSubscriber)

        is ColumnSchema.Group -> {
            val childFields = schema.columns.map { (childName, childSchema) ->
                childSchema.toArrowField(childName, mismatchSubscriber)
            }
            Field(name, FieldType(nullable, ArrowType.Struct(), null), childFields)
        }

        is ColumnSchema.Frame -> {
            val childFields = schema.columns.map { (childName, childSchema) ->
                childSchema.toArrowField(childName, mismatchSubscriber)
            }
            val childStructField = Field("item", FieldType(true, ArrowType.Struct(), null), childFields)
            Field(name, FieldType(nullable, ArrowType.List(), null), listOf(childStructField))
        }
    }

internal fun KType.toArrowField(name: String, mismatchSubscriber: (ConvertingMismatch) -> Unit): Field {
    val nullable = isMarkedNullable
    return when {
        this == nullableNothingType -> Field(name, FieldType(true, ArrowType.Null(), null), emptyList())

        isSubtypeOf(typeOf<String?>()) ->
            Field(name, FieldType(nullable, ArrowType.Utf8(), null), emptyList())

        isSubtypeOf(typeOf<Boolean?>()) ->
            Field(name, FieldType(nullable, ArrowType.Bool(), null), emptyList())

        isSubtypeOf(typeOf<Byte?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(8, true), null), emptyList())

        isSubtypeOf(typeOf<Short?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(16, true), null), emptyList())

        isSubtypeOf(typeOf<Int?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(32, true), null), emptyList())

        isSubtypeOf(typeOf<Long?>()) ->
            Field(name, FieldType(nullable, ArrowType.Int(64, true), null), emptyList())

        isSubtypeOf(typeOf<Float?>()) ->
            Field(name, FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), null), emptyList())

        isSubtypeOf(typeOf<Double?>()) ->
            Field(name, FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), null), emptyList())

        isSubtypeOf(typeOf<JavaLocalDate?>()) || isSubtypeOf(typeOf<LocalDate?>()) ->
            Field(name, FieldType(nullable, ArrowType.Date(DateUnit.DAY), null), emptyList())

        isSubtypeOf(typeOf<JavaLocalDateTime?>()) || isSubtypeOf(typeOf<LocalDateTime?>()) ->
            Field(name, FieldType(nullable, ArrowType.Date(DateUnit.MILLISECOND), null), emptyList())

        isSubtypeOf(typeOf<JavaLocalTime?>()) || isSubtypeOf(typeOf<LocalTime?>()) ->
            Field(name, FieldType(nullable, ArrowType.Time(TimeUnit.NANOSECOND, 64), null), emptyList())

        else -> {
            val clazz = (classifier as? kotlin.reflect.KClass<*>)?.java ?: Any::class.java
            mismatchSubscriber(ConvertingMismatch.SavedAsString(name, clazz))
            Field(name, FieldType(true, ArrowType.Utf8(), null), emptyList())
        }
    }
}

/**
 * Create Arrow [Schema] matching [this] actual data.
 * Columns with not supported types will be interpreted as String
 */
public fun List<AnyCol>.toArrowSchema(
    mismatchSubscriber: (ConvertingMismatch) -> Unit = ignoreMismatchMessage,
): Schema {
    val fields = this.map { it.toArrowField(mismatchSubscriber) }
    return Schema(fields)
}

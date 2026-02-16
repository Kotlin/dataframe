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
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.typeClass
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
    val nullable = columnType.isMarkedNullable
    return when {
        column is ColumnGroup<*> -> {
            val childFields = column.columns().map { it.toArrowField(mismatchSubscriber) }
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Struct(), null),
                childFields,
            )
        }

        columnType.isSubtypeOf(typeOf<String?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Utf8(), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<Boolean?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Bool(), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<Byte?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(8, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<Short?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(16, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<Int?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(32, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<Long?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(64, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<Float?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<Double?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<JavaLocalDate?>()) ||
            columnType.isSubtypeOf(typeOf<LocalDate?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Date(DateUnit.DAY), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<JavaLocalDateTime?>()) ||
            columnType.isSubtypeOf(typeOf<LocalDateTime?>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Date(DateUnit.MILLISECOND), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOf<JavaLocalTime?>()) ||
            columnType.isSubtypeOf(typeOf<LocalTime>()) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Time(TimeUnit.NANOSECOND, 64), null),
                emptyList(),
            )

        else -> {
            mismatchSubscriber(ConvertingMismatch.SavedAsString(column.name(), column.typeClass.java))
            Field(column.name(), FieldType(true, ArrowType.Utf8(), null), emptyList())
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

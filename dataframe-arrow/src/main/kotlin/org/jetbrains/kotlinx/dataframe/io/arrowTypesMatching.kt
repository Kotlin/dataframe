package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.vector.types.DateUnit
import org.apache.arrow.vector.types.FloatingPointPrecision
import org.apache.arrow.vector.types.TimeUnit
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.typeClass
import org.jetbrains.kotlinx.dataframe.util.TypeOf
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
        columnType.isSubtypeOf(TypeOf.NULLABLE_STRING) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Utf8(), null),
                emptyList(),
            )

        columnType.isSubtypeOf(TypeOf.NULLABLE_BOOLEAN) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Bool(), null),
                emptyList(),
            )

        columnType.isSubtypeOf(TypeOf.NULLABLE_BYTE) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(8, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(TypeOf.NULLABLE_SHORT) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(16, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(TypeOf.NULLABLE_INT) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(32, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(TypeOf.NULLABLE_LONG) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Int(64, true), null),
                emptyList(),
            )

        columnType.isSubtypeOf(TypeOf.NULLABLE_FLOAT) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.SINGLE), null),
                emptyList(),
            )

        columnType.isSubtypeOf(TypeOf.NULLABLE_DOUBLE) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOfNullableJavaLocalDate) ||
            columnType.isSubtypeOf(TypeOf.NULLABLE_LOCAL_DATE) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Date(DateUnit.DAY), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOfNullableJavaLocalDateTime) ||
            columnType.isSubtypeOf(TypeOf.NULLABLE_LOCAL_DATE_TIME) ->
            Field(
                column.name(),
                FieldType(nullable, ArrowType.Date(DateUnit.MILLISECOND), null),
                emptyList(),
            )

        columnType.isSubtypeOf(typeOfNullableJavaLocalTime) ||
            columnType.isSubtypeOf(TypeOf.NULLABLE_LOCAL_TIME) ->
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

private val typeOfNullableJavaLocalDateTime = typeOf<JavaLocalDateTime?>()
private val typeOfNullableJavaLocalDate = typeOf<JavaLocalDate?>()
private val typeOfNullableJavaLocalTime = typeOf<JavaLocalTime?>()

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

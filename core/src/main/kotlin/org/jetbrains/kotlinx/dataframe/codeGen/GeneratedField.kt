package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.codeGen.needsQuoting
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema

public sealed interface FieldType {
    public class ValueFieldType(public val typeFqName: String) : FieldType
    public class FrameFieldType(public val markerName: String, public val nullable: Boolean) : FieldType
    public class GroupFieldType(public val markerName: String) : FieldType
}

/**
 * Returns whether the column type ends with `?` or not.
 * NOTE: for [FieldType.FrameFieldType], the `nullable` property indicates the nullability of the frame itself, not the type of the column.
 */
public fun FieldType.isNullable(): Boolean =
    when (this) {
        is FieldType.FrameFieldType -> markerName.endsWith("?")
        is FieldType.GroupFieldType -> markerName.endsWith("?")
        is FieldType.ValueFieldType -> typeFqName.endsWith("?")
    }

/**
 * Returns whether the column type doesn't end with `?` or whether it does.
 * NOTE: for [FieldType.FrameFieldType], the `nullable` property indicates the nullability of the frame itself, not the type of the column.
 */
public fun FieldType.isNotNullable(): Boolean = !isNullable()

private fun String.toNullable() = if (this.last() == '?') this else "$this?"

/**
 * Returns a new fieldType with the same type but with nullability in the column type.
 * NOTE: for [FieldType.FrameFieldType], the `nullable` property indicates the nullability of the frame itself, not the type of the column.
 */
public fun FieldType.toNullable(): FieldType =
    if (isNotNullable()) {
        when (this) {
            is FieldType.FrameFieldType -> FieldType.FrameFieldType(markerName.toNullable(), nullable)
            is FieldType.GroupFieldType -> FieldType.GroupFieldType(markerName.toNullable())
            is FieldType.ValueFieldType -> FieldType.ValueFieldType(typeFqName.toNullable())
        }
    } else this

/**
 * Returns a new fieldType with the same type but with nullability disabled in the column type.
 * NOTE: for [FieldType.FrameFieldType], the `nullable` property indicates the nullability of the frame itself, not the type of the column.
 */
public fun FieldType.toNotNullable(): FieldType =
    if (isNullable()) {
        when (this) {
            is FieldType.FrameFieldType -> FieldType.FrameFieldType(markerName.removeSuffix("?"), nullable)
            is FieldType.GroupFieldType -> FieldType.GroupFieldType(markerName.removeSuffix("?"))
            is FieldType.ValueFieldType -> FieldType.ValueFieldType(typeFqName.removeSuffix("?"))
        }
    } else this

public val FieldType.name: String
    get() = when (this) {
        is FieldType.FrameFieldType -> markerName
        is FieldType.GroupFieldType -> markerName
        is FieldType.ValueFieldType -> typeFqName
    }

public class ValidFieldName private constructor(private val identifier: String, public val needsQuote: Boolean) {
    public val unquoted: String get() = identifier
    public val quotedIfNeeded: String get() = if (needsQuote) "`$identifier`" else identifier

    public operator fun plus(other: ValidFieldName): ValidFieldName {
        return ValidFieldName(identifier = identifier + other.identifier, needsQuote = needsQuote || other.needsQuote)
    }

    public companion object {
        public fun of(name: String): ValidFieldName {
            val needsQuote = name.needsQuoting()
            var result = name
            if (needsQuote) {
                result = name.replace("<", "{")
                    .replace(">", "}")
                    .replace("::", " - ")
                    .replace(": ", " - ")
                    .replace(":", " - ")
                    .replace(".", " ")
                    .replace("/", "-")
                    .replace("[", "{")
                    .replace("]", "}")
                    .replace("`", "'")
                    .replace(";", " ")
                    .replace("\\", " ")
            }

            return ValidFieldName(result, needsQuote)
        }
    }
}

public interface BaseField {
    public val fieldName: ValidFieldName
    public val columnName: String
    public val fieldType: FieldType
}

public fun BaseField.toNullable(): BaseField =
    if (fieldType.isNullable()) this
    else object : BaseField {
        override val fieldName: ValidFieldName = this@toNullable.fieldName
        override val columnName: String = this@toNullable.columnName
        override val fieldType: FieldType = this@toNullable.fieldType.toNullable()
    }

public fun BaseField.toNotNullable(): BaseField =
    if (fieldType.isNotNullable()) this
    else object : BaseField {
        override val fieldName: ValidFieldName = this@toNotNullable.fieldName
        override val columnName: String = this@toNotNullable.columnName
        override val fieldType: FieldType = this@toNotNullable.fieldType.toNotNullable()
    }

public data class GeneratedField(
    override val fieldName: ValidFieldName,
    override val columnName: String,
    val overrides: Boolean,
    val columnSchema: ColumnSchema,
    override val fieldType: FieldType
) : BaseField {
    val columnKind: ColumnKind get() = columnSchema.kind
}

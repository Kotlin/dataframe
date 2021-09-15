package org.jetbrains.dataframe.internal.codeGen

import org.jetbrains.dataframe.ColumnKind
import org.jetbrains.dataframe.impl.codeGen.needsQuoting
import org.jetbrains.dataframe.internal.schema.ColumnSchema

public sealed interface ColumnInfo
public class ValueColumn(public val typeFqName: String) : ColumnInfo
public object FrameColumn : ColumnInfo
public object ColumnGroup : ColumnInfo

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
                    .replace("(", "{")
                    .replace(")", "}")
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
    public val markerName: String?
    public val nullable: Boolean
    public val columnInfo: ColumnInfo
}

public data class GeneratedField(
    override val fieldName: ValidFieldName,
    override val columnName: String,
    val overrides: Boolean,
    val columnSchema: ColumnSchema,
    override val markerName: String?
) : BaseField {
    val columnKind: ColumnKind get() = columnSchema.kind
    override val nullable: Boolean = columnSchema.nullable
    override val columnInfo: ColumnInfo = when (columnKind) {
        ColumnKind.Value -> ValueColumn((columnSchema as ColumnSchema.Value).type.toString())
        ColumnKind.Group -> ColumnGroup
        ColumnKind.Frame -> FrameColumn
    }
}

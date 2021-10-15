package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.codeGen.needsQuoting
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema

public sealed interface ColumnInfo {
    public class ValueColumnInfo(public val typeFqName: String) : ColumnInfo
    public object FrameColumnInfo : ColumnInfo
    public object ColumnGroupInfo : ColumnInfo
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
        ColumnKind.Value -> ColumnInfo.ValueColumnInfo((columnSchema as ColumnSchema.Value).type.toString())
        ColumnKind.Group -> ColumnInfo.ColumnGroupInfo
        ColumnKind.Frame -> ColumnInfo.FrameColumnInfo
    }
}

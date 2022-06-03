package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.codeGen.needsQuoting
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema

public sealed interface FieldType {
    public class ValueFieldType(public val typeFqName: String) : FieldType
    public class FrameFieldType(public val markerName: String, public val nullable: Boolean) : FieldType
    public class GroupFieldType(public val markerName: String) : FieldType
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

public data class GeneratedField(
    override val fieldName: ValidFieldName,
    override val columnName: String,
    val overrides: Boolean,
    val columnSchema: ColumnSchema,
    override val fieldType: FieldType
) : BaseField {
    val columnKind: ColumnKind get() = columnSchema.kind
}

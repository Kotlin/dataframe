package org.jetbrains.dataframe.internal.codeGen

import org.jetbrains.dataframe.ColumnKind
import org.jetbrains.dataframe.internal.schema.ColumnSchema

public sealed interface ColumnInfo
public class ValueColumn(public val typeFqName: String) : ColumnInfo
public object FrameColumn : ColumnInfo
public object ColumnGroup : ColumnInfo

public interface BaseField {
    public val fieldName: String
    public val columnName: String
    public val markerName: String?
    public val nullable: Boolean
    public val columnInfo: ColumnInfo
}

public data class GeneratedField(
    override val fieldName: String,
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

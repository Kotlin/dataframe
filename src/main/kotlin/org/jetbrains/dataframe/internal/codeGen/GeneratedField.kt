package org.jetbrains.dataframe.internal.codeGen

import org.jetbrains.dataframe.ColumnKind
import org.jetbrains.dataframe.internal.schema.ColumnSchema

public data class GeneratedField(
    val fieldName: String,
    val columnName: String,
    val overrides: Boolean,
    val columnSchema: ColumnSchema,
    val markerName: String?
) {
    val columnKind: ColumnKind get() = columnSchema.kind
}

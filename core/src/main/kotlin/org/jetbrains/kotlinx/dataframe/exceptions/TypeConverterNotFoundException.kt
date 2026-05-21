package org.jetbrains.kotlinx.dataframe.exceptions

import kotlin.reflect.KType
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath

public class TypeConverterNotFoundException(
    public val from: KType,
    public val to: KType,
    public val column: ColumnPath?,
) : IllegalArgumentException() {

    override val message: String
        get() =
            "Type converter from $from to $to is not found" +
                (column?.let { " for column '${it.joinToString()}'" } ?: "")
}

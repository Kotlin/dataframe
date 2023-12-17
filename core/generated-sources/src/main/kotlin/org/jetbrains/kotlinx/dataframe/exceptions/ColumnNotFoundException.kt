package org.jetbrains.kotlinx.dataframe.exceptions

public class ColumnNotFoundException(public val columnName: String, public override val message: String) : RuntimeException()

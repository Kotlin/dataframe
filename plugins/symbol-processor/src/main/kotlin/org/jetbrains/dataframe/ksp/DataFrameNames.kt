package org.jetbrains.dataframe.ksp

public object DataFrameNames {
    public const val DATAFRAME_PACKAGE: String = "org.jetbrains.kotlinx.dataframe"
    public const val DATA_SCHEMA: String = "org.jetbrains.kotlinx.dataframe.annotations.DataSchema"
    public const val SHORT_COLUMN_NAME: String = "ColumnName"
    public const val COLUMN_NAME: String = "org.jetbrains.kotlinx.dataframe.annotations.$SHORT_COLUMN_NAME"
    public const val DATA_FRAME: String = "$DATAFRAME_PACKAGE.DataFrame"
    public const val DATA_ROW: String = "$DATAFRAME_PACKAGE.DataRow"
}

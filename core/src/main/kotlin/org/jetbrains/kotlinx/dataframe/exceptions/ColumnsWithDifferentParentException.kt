package org.jetbrains.kotlinx.dataframe.exceptions

public class ColumnsWithDifferentParentException(message: String) :
    IllegalArgumentException(),
    DataFrameError {

    override val message: String = message
}

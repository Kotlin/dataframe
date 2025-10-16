package org.jetbrains.kotlinx.dataframe.exceptions

public class ColumnsWithDifferentParentException() :
    IllegalArgumentException(),
    DataFrameError {

    override val message: String
        get() = "Cannot move columns to an index remaining inside group if they have different parent"
}

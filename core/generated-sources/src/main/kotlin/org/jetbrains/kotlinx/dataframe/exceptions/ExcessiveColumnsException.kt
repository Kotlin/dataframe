package org.jetbrains.kotlinx.dataframe.exceptions

public class ExcessiveColumnsException(public val columns: List<String>) : RuntimeException() {

    override val message: String
        get() = "Excess columns in DataFrame: $columns"
}

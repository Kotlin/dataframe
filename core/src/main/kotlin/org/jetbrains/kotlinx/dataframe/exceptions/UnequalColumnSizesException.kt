package org.jetbrains.kotlinx.dataframe.exceptions

public class UnequalColumnSizesException(
    public val expectedRowsCount: Int,
    public val columnSizes: List<Pair<String, Int>>
) : IllegalArgumentException() {

    override val message: String
        get() = "Unequal column sizes. Expected rows count: $expectedRowsCount. Actual column sizes:\n${columnSizes.joinToString("\n") { it.first + ": " + it.second }}"
}

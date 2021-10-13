package org.jetbrains.kotlinx.dataframe.impl

public data class DataFrameSize(val ncol: Int, val nrow: Int) {
    override fun toString(): String = "$nrow x $ncol"
}

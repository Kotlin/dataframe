package org.jetbrains.dataframe

fun <T> DataFrame<T>.summary() =
        columns.toDataFrame {
            "column" { name() }
            "type" { type.fullName }
            "distinct values" { ndistinct }
            "nulls %" { values.count { it == null }.toDouble() * 100 / size.let { if (it == 0) 1 else it } }
            "most frequent value" { values.groupBy { it }.maxByOrNull { it.value.size }?.key }
        }
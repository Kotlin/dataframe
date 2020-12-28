package org.jetbrains.dataframe

fun <T,G> GroupedDataFrame<T, G>.forEach(body: (GroupedDataFrame.Entry<T, G>) -> Unit) = this@forEach.forEach { key, group -> body(GroupedDataFrame.Entry(key, group)) }
fun <T,G> GroupedDataFrame<T, G>.forEach(body: (key: DataRow<T>, group: DataFrame<G>) -> Unit) =
    keys.forEachIndexed { index, row ->
        val group = groups[index]
        body(row, group)
    }

fun <T> DataFrame<T>.forEach(action: (DataRow<T>) -> Unit) = rows.forEach(action)
fun <T> DataFrame<T>.forEachIndexed(action: (Int, DataRow<T>) -> Unit) = rows.forEachIndexed(action)
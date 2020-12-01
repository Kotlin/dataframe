package org.jetbrains.dataframe

fun <T> DataFrame<T>.nullToZero(cols: ColumnsSelector<T, Number?>) = nullToZero(getColumns(cols))
fun <T> DataFrame<T>.nullToZero(vararg cols: String) = nullToZero(getColumns(cols) as List<ColumnDef<Number?>>)
fun <T> DataFrame<T>.nullToZero(vararg cols: ColumnDef<Number?>) = nullToZero(cols.toList())
fun <T> DataFrame<T>.nullToZero(cols: Iterable<ColumnDef<Number?>>) = cols.fold(this) { df, col -> df.nullColumnToZero(df[col] as ColumnDef<Number?>) }
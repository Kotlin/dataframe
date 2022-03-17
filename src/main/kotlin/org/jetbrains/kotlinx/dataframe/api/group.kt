package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region DataFrame

public data class GroupClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

public fun <T, C> DataFrame<T>.group(columns: ColumnsSelector<T, C>): GroupClause<T, C> = GroupClause(this, columns)
public fun <T> DataFrame<T>.group(vararg columns: String): GroupClause<T, Any?> = group { columns.toColumns() }
public fun <T> DataFrame<T>.group(vararg columns: Column): GroupClause<T, Any?> = group { columns.toColumns() }
public fun <T> DataFrame<T>.group(vararg columns: KProperty<*>): GroupClause<T, Any?> = group { columns.toColumns() }

@JvmName("intoString")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
public infix fun <T, C> GroupClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> String): DataFrame<T> = df.move(columns).under { column(it).toColumnAccessor() }

@JvmName("intoColumn")
public infix fun <T, C> GroupClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> Column): DataFrame<T> = df.move(columns).under(column)
public infix fun <T, C> GroupClause<T, C>.into(column: String): DataFrame<T> = into(columnGroup().named(column))
public infix fun <T, C> GroupClause<T, C>.into(column: ColumnGroupAccessor): DataFrame<T> = df.move(columns).under(column)
public infix fun <T, C> GroupClause<T, C>.into(column: KProperty<*>): DataFrame<T> = into(column.columnName)

// endregion

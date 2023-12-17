package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.DELIMITED_STRING_REGEX
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.api.renameImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> =
    rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
        .into(*mappings.map { it.second }.toTypedArray())

public fun <T, C> DataFrame<T>.rename(columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)

public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumnSet() }

public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumnSet() }

public data class RenameClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

/**
 * ## Rename to camelCase
 *
 * This function renames all columns to `camelCase` by replacing all [delimiters][DELIMITERS_REGEX]
 * and converting the first char to lowercase.
 * Even [DataFrames][DataFrame] inside [FrameColumns][FrameColumn] are traversed recursively.
 */
public fun <T> DataFrame<T>.renameToCamelCase(): DataFrame<T> = this
    // recursively rename all columns written with delimiters or starting with a capital to camel case
    .rename {
        cols { it.name() matches DELIMITED_STRING_REGEX || it.name[0].isUpperCase() }.recursively()
    }.toCamelCase()

    // take all frame columns recursively and call renameToCamelCase() on all dataframes inside
    .update {
        colsOf<AnyFrame>().recursively()
    }.with { it.renameToCamelCase() }

public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())

public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> =
    renameImpl(newNames)

public fun <T, C> RenameClause<T, C>.into(vararg newNames: KProperty<*>): DataFrame<T> =
    into(*newNames.map { it.name }.toTypedArray())

public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> =
    renameImpl(transform)

/**
 * ## Rename to camelCase
 *
 * Renames the selected columns to `camelCase` by replacing all [delimiters][DELIMITERS_REGEX]
 * and converting the first char to lowercase.
 */
public fun <T, C> RenameClause<T, C>.toCamelCase(): DataFrame<T> = into {
    it.name()
        .toCamelCaseByDelimiters(DELIMITERS_REGEX)
        .replaceFirstChar { it.lowercaseChar() }
}

// endregion

// region DataColumn

public fun <T, C : ColumnReference<T>> C.rename(column: KProperty<T>): C = rename(column.columnName) as C

public fun <T, C : ColumnReference<T>> C.rename(column: ColumnAccessor<T>): C = rename(column.name()) as C

// endregion

// region named

public infix fun <T, C : ColumnReference<T>> C.named(name: String): C = rename(name) as C

public infix fun <T, C : ColumnReference<T>> C.named(name: KProperty<*>): C = rename(name)

public infix fun <T, C : ColumnReference<T>> C.named(name: ColumnAccessor<*>): C = rename(name)

// endregion

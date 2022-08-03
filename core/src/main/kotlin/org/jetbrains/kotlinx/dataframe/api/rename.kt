package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Schema
import org.jetbrains.kotlinx.dataframe.annotations.Value
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.DELIMITED_STRING_REGEX
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.columnsSelector
import org.jetbrains.kotlinx.dataframe.plugin.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.renameClause
import org.jetbrains.kotlinx.dataframe.plugin.varargString
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> =
    rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
        .into(*mappings.map { it.second }.toTypedArray())

public class Rename : AbstractInterpreter<RenameClauseApproximation>() {
    private val Arguments.receiver by dataFrame()
    private val Arguments.columns by columnsSelector()
    override fun Arguments.interpret(): RenameClauseApproximation {
        return RenameClauseApproximation(receiver, columns)
    }
}

@Interpretable(Rename::class)
public fun <T, C> @receiver:Schema DataFrame<T>.rename(@Value columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(cols: Iterable<ColumnReference<C>>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

@HasSchema(schemaArg = 0)
public data class RenameClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

public class RenameClauseApproximation(public val schema: PluginDataFrameSchema, public val columns: List<String>)

public fun <T> DataFrame<T>.renameToCamelCase(): DataFrame<T> {
    return rename {
        dfs { it.isColumnGroup() && it.name() matches DELIMITED_STRING_REGEX }
    }.toCamelCase()
        .rename {
            dfs { !it.isColumnGroup() && it.name() matches DELIMITED_STRING_REGEX }
        }.toCamelCase()
        .update {
            dfsOf<AnyFrame>()
        }.with { it.renameToCamelCase() }
}

public class Into : AbstractSchemaModificationInterpreter() {
    public val Arguments.receiver: RenameClauseApproximation by renameClause()
    public val Arguments.newNames: List<String> by varargString()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        receiver.schema

//        DataFrameImpl()
        TODO()
    }
}

public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())

@Interpretable(Into::class)
public fun <T, C> @receiver:Value RenameClause<T, C>.into(@Value vararg newNames: String): DataFrame<T> =
    df.move(columns).intoIndexed { col, index ->
        col.path.dropLast(1) + newNames[index]
    }
public fun <T, C> RenameClause<T, C>.into(vararg newNames: KProperty<*>): DataFrame<T> =
    into(*newNames.map { it.name }.toTypedArray())

public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> =
    df.move(columns).into {
        it.path.dropLast(1) + transform(it)
    }

public fun <T, C> RenameClause<T, C>.toCamelCase(): DataFrame<T> =
    into { it.name().toCamelCaseByDelimiters(DELIMITERS_REGEX) }

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

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.describeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KProperty
import kotlin.reflect.KType

// region describe

public fun <T> DataFrame<T>.describe(): DataFrame<ColumnDescription> = describe { allDfs() }
public fun <T> DataFrame<T>.describe(columns: ColumnsSelector<T, *>): DataFrame<ColumnDescription> = describeImpl(getColumnsWithPaths(columns))
public fun <T> DataFrame<T>.describe(vararg columns: String): DataFrame<ColumnDescription> = describe { columns.toColumns() }
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: ColumnReference<C>): DataFrame<ColumnDescription> = describe { columns.toColumns() }
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: KProperty<C>): DataFrame<ColumnDescription> = describe { columns.toColumns() }

public fun <T> DataColumn<T>.describe(): DataFrame<ColumnDescription> = describeImpl(listOf(this))

@DataSchema
public interface ColumnDescription {
    public val name: String
    public val path: ColumnPath
    public val type: KType
    public val count: Int
    public val unique: Int
    public val nulls: Int
    public val top: Any
    public val freq: Int
    public val mean: Double
    public val std: Double
    public val min: Any
    public val median: Any
    public val max: Any
}

// endregion

// schema

public fun AnyFrame.schema(): DataFrameSchema = extractSchema()

public fun GroupBy<*, *>.schema(): DataFrameSchema = toDataFrame().schema()

public fun AnyRow.schema(): DataFrameSchema = owner.schema()

// endregion

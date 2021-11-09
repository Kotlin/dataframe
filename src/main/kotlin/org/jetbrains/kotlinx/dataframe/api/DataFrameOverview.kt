package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.describeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KProperty
import kotlin.reflect.KType

// region describe

public fun <T> DataFrame<T>.describe(columns: ColumnsSelector<T, *> = { numberCols() }): DataFrame<ColumnDescriptionSchema> = describeImpl(this[columns])
public fun <T> DataFrame<T>.describe(vararg columns: String): DataFrame<ColumnDescriptionSchema> = describe { columns.toColumns() }
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: ColumnReference<C>): DataFrame<ColumnDescriptionSchema> = describe { columns.toColumns() }
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: KProperty<C>): DataFrame<ColumnDescriptionSchema> = describe { columns.toColumns() }

public fun <T> DataColumn<T>.describe(): DataFrame<ColumnDescriptionSchema> = describeImpl(listOf(this))

@DataSchema
public interface GeneralColumnDescriptionSchema {
    public val column: String
    public val count: Int
    public val nulls: Int
}

@DataSchema
public interface ColumnDescriptionSchema : GeneralColumnDescriptionSchema {
    public val unique: Int
    public val top: Any
    public val freq: Int
    public val type: KType
}

@DataSchema
public interface NumberColumnDescriptionSchema : GeneralColumnDescriptionSchema {
    public val mean: Double
    public val min: Any
    public val max: KType
}

// endregion

// schema

public fun AnyFrame.schema(): DataFrameSchema = extractSchema()

public fun AnyCol.schema(): ColumnSchema = extractSchema()

public fun AnyRow.schema(): DataFrameSchema = owner.schema()

// endregion

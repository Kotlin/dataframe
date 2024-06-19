package org.jetbrains.kotlinx.dataframe.plugin.impl

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.api.TypeApproximation
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.typeOf

/**
 * DataFrame wrapper of [PluginDataFrameSchema]:
 * - Value columns have one cell of type [TypeApproximation]
 * - Frame columns have one cell with a [PluginDataFrame]
 * - Names match the names in [PluginDataFrameSchema]
 */
class PluginDataFrame(
    df: AnyFrame,
) : DataFrame<Any?> by df {
    init {
        require(df.isPluginDataFrame()) { "Not a 'plugin' dataframe" }
    }
}

fun AnyFrame.asPluginDataFrame(): PluginDataFrame = PluginDataFrame(this)

/**
 * Column wrapper of [SimpleCol]:
 * - Value columns have one cell of type [TypeApproximation]
 * - Frame columns have one cell with a [PluginDataFrame]
 * - Names match the name of [SimpleCol]
 */
class PluginColumn(
    col: BaseColumn<*>,
) : BaseColumn<Any?> by col {
    init {
        require(col.isPluginColumn()) { "Not a 'plugin' column" }
    }
}

fun BaseColumn<*>.asPluginColumn(): PluginColumn = PluginColumn(this)

fun BaseColumn<*>.isPluginColumn(): Boolean =
    when (kind()) {
        Value -> (this as ValueColumn<*>).type == typeOf<TypeApproximation>() && size == 1
        Group -> (this as ColumnGroup<*>).columns().all { it.isPluginColumn() }
        Frame -> (this as FrameColumn<*>).single().columns().all { it.isPluginColumn() }
    }

fun AnyFrame.isPluginDataFrame(): Boolean = columns().all { it.isPluginColumn() }

fun PluginColumn.toSimpleCol(): SimpleCol =
    when (kind()) {
        Value ->
            SimpleDataColumn(
                name = name(),
                type = (this as ValueColumn<*>).cast<TypeApproximation>().first(),
            )

        Group ->
            SimpleColumnGroup(
                name = name(),
                columns = (this as ColumnGroup<*>).columns().map { it.asPluginColumn().toSimpleCol() },
            )

        Frame ->
            SimpleFrameColumn(
                name(),
                (this as FrameColumn<*>).first().columns().map { it.asPluginColumn().toSimpleCol() },
            )
    }

fun PluginDataFrame.toPluginDataFrameSchema(): PluginDataFrameSchema =
    PluginDataFrameSchema(columns().map { it.asPluginColumn().toSimpleCol() })

fun PluginDataFrameSchema.toPluginDataFrame(): PluginDataFrame =
    dataFrameOf(columns().map { it.toPluginColumn() })
        .asPluginDataFrame()

fun PluginDataFrameSchema.processAsPluginDataFrame(block: AnyFrame.() -> AnyFrame): PluginDataFrameSchema =
    toPluginDataFrame()
        .block()
        .asPluginDataFrame()
        .toPluginDataFrameSchema()

fun SimpleCol.toPluginColumn(): PluginColumn =
    when (this) {
        is SimpleDataColumn ->
            DataColumn.createValueColumn(
                name = name(),
                values = listOf(this.type),
                infer = Infer.None,
                type = typeOf<TypeApproximation>(),
            )

        is SimpleColumnGroup ->
            DataColumn.createColumnGroup(
                name = name(),
                df = columns().map { it.toPluginColumn() }.toDataFrame(),
            )

        is SimpleFrameColumn ->
            DataColumn.createFrameColumn(
                name = name(),
                groups = listOf(columns().map { it.toPluginColumn() }.toDataFrame()),
            )
    }.asPluginColumn()

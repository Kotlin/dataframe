package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.impl.DataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.createTypeWithArgument
import org.jetbrains.kotlinx.dataframe.impl.renderSchema
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KType

internal val anyRowType = createTypeWithArgument<AnyRow>()

internal open class ColumnGroupImpl<T>(override val df: DataFrame<T>, val name: String) :
    DataFrameImpl<T>(df.columns()),
    ColumnGroup<T>,
    DataColumnInternal<DataRow<T>>,
    DataColumnGroup<T> {

    override fun values() = df.rows()

    override fun countDistinct() = distinct.nrow

    override fun type() = anyRowType

    private val distinct by lazy { df.distinct() }

    private val set by lazy { distinct.rows().toSet() }

    override fun toSet() = set

    override fun size() = df.nrow

    override fun get(index: Int) = df[index]

    override fun get(firstIndex: Int, vararg otherIndices: Int): ColumnGroup<T> = DataColumn.createColumnGroup(name, df.get(firstIndex, *otherIndices))

    override fun rename(newName: String) = ColumnGroupImpl(df, newName)

    override fun defaultValue() = null

    override fun get(indices: Iterable<Int>) = ColumnGroupImpl(df[indices], name)

    override fun addParent(parent: ColumnGroup<*>): DataColumn<DataRow<T>> = ColumnGroupWithParent(parent, this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val g = other as? ColumnGroup<*> ?: return false
        return name == g.name() && df == other.df
    }

    private val hashCode by lazy { name.hashCode() * 31 + df.hashCode() }

    override fun hashCode() = hashCode

    override fun toString() = "$name: {${renderSchema(df)}}"

    override fun changeType(type: KType) = throw UnsupportedOperationException()

    override fun name() = name

    override fun distinct() = ColumnGroupImpl(distinct, name)

    override fun resolve(context: ColumnResolutionContext) = super<DataColumnInternal>.resolve(context)

    override fun resolveSingle(context: ColumnResolutionContext) = super<DataColumnInternal>.resolveSingle(context)

    override fun iterator() = df.iterator()

    override fun forceResolve() = ResolvingColumnGroup(this)

    override fun get(range: IntRange): ColumnGroupImpl<T> = ColumnGroupImpl(df[range], name)

    override fun get(columnName: String): AnyCol = getColumn(columnName)
}

internal class ResolvingColumnGroup<T>(
    override val source: ColumnGroupImpl<T>
) : DataColumnGroup<T> by source, ForceResolvedColumn<DataRow<T>> {

    override fun resolve(context: ColumnResolutionContext) = super<DataColumnGroup>.resolve(context)

    override fun resolveSingle(context: ColumnResolutionContext) = context.df.getColumn<DataRow<T>>(source.name(), context.unresolvedColumnsPolicy)?.addPath(context.df)

    override fun getValue(row: AnyRow) = super<DataColumnGroup>.getValue(row)

    override fun rename(newName: String) = ResolvingColumnGroup(source.rename(newName))
}

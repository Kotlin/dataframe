package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import kotlin.reflect.KProperty

public interface ColumnGroup<out T> : BaseColumn<DataRow<T>>, DataFrame<T> {

    override fun get(indices: Iterable<Int>): ColumnGroup<T>

    override fun get(columnName: String): AnyCol

    override fun kind(): ColumnKind = ColumnKind.Group

    override fun distinct(): ColumnGroup<T>

    override fun get(firstIndex: Int, vararg otherIndices: Int): ColumnGroup<T>

    override fun get(range: IntRange): ColumnGroup<T>

    override fun rename(newName: String): ColumnGroup<T>

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnGroup<T> = super.getValue(thisRef, property) as ColumnGroup<T>
}

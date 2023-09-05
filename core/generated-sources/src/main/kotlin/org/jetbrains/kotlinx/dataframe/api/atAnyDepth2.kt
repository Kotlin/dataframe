package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.atAnyDepthImpl
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class AtAnyDepthDslMarker

@AtAnyDepthDslMarker
@ColumnsSelectionDslMarker // TODO?
public class AtAnyDepthDsl<T>(@PublishedApi internal val context: ColumnsSelectionDsl<T>) : //ColumnsSelectionDsl<T> /* by context */,
    FirstColumnsSelectionDsl<T>,
    LastAtAnyDepthDsl<T>,
//    LastColumnsSelectionDsl,
//    SingleColumnsSelectionDsl,
    ColsOfColumnsSelectionDsl<T>,
    AndColumnsSelectionDsl<T> {

    override val scope: Scope
        get() = Scope.AT_ANY_DEPTH_DSL

//    override fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>> = context.get(columns)
//    override fun columns(): List<AnyCol> = context.columns()
//    override fun columnsCount(): Int = context.columnsCount()
//    override fun containsColumn(name: String): Boolean = context.containsColumn(name)
//    override fun containsColumn(path: ColumnPath): Boolean = context.containsColumn(path)
//    override fun getColumnIndex(name: String): Int = context.getColumnIndex(name)
//    override fun getColumnOrNull(name: String): AnyCol? = context.getColumnOrNull(name)
//    override fun getColumnOrNull(index: Int): AnyCol? = context.getColumnOrNull(index)
//    override fun <R> getColumnOrNull(column: ColumnReference<R>): DataColumn<R>? = context.getColumnOrNull(column)
//    override fun <R> getColumnOrNull(column: KProperty<R>): DataColumn<R>? = context.getColumnOrNull(column)
//    override fun getColumnOrNull(path: ColumnPath): AnyCol? = context.getColumnOrNull(path)
//    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>): DataColumn<R>? = context.getColumnOrNull(column)
}

public interface AtAnyDepth2ColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {

    public fun <R : ColumnsResolver<*>> ColumnsSelectionDsl<*>.atAnyDepth(
        selector: Selector<AtAnyDepthDsl<@UnsafeVariance T>, R>,
    ): R = AtAnyDepthDsl(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>).let { selector(it, it) }

    public fun <C, R : ColumnsResolver<*>> ColumnSet<C>.atAnyDepth(
        selector: ColumnFilter<C>,
    ): ColumnSet<C> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        this@atAnyDepth.cols(selector).atAnyDepthImpl()
    }

    public fun <C, S, R : ColumnsResolver<S>> SingleColumn<DataRow<C>>.atAnyDepth(
        selector: Selector<AtAnyDepthDsl<@UnsafeVariance C>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }

    public fun <C, S, R : ColumnsResolver<S>> KProperty<DataRow<C>>.atAnyDepth(
        selector: Selector<AtAnyDepthDsl<@UnsafeVariance C>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }

    public fun <S, R : ColumnsResolver<S>> String.atAnyDepth(
        selector: Selector<AtAnyDepthDsl<*>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }

    public fun <S, R : ColumnsResolver<S>> ColumnPath.atAnyDepth(
        selector: Selector<AtAnyDepthDsl<*>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }
}

// endregion

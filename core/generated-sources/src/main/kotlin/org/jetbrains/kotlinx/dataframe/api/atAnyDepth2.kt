package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.atAnyDepthImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformWithContext
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region ColumnsSelectionDsl

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class AtAnyDepthDslMarker

@AtAnyDepthDslMarker
@ColumnsSelectionDslMarker // TODO?
public open class AtAnyDepthDsl<T>(@PublishedApi internal val context: ColumnsSelectionDsl<T>) : //ColumnsSelectionDsl<T> /* by context */,
    FirstAtAnyDepthDsl<T>,
    LastAtAnyDepthDsl<T>,
//    LastColumnsSelectionDsl,
//    SingleColumnsSelectionDsl,
    ColsOfAtAnyDepthDsl<T>,
    AllAtAnyDepthDsl<T>,
    AndColumnsSelectionDsl<T> {

    override val scope: Scope
        get() = Scope.AT_ANY_DEPTH_DSL


    // region inline functions

    @AtAnyDepthDslMarker
    public inline fun <reified C> ColumnSet<*>.colsOf(
        noinline filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<C> = colsOfInternal(typeOf<C>(), scope, filter)

    @AtAnyDepthDslMarker
    public inline fun <reified C> SingleColumn<DataRow<*>>.colsOf(
        noinline filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<C> = ensureIsColumnGroup().colsOfInternal(typeOf<C>(), scope, filter)

    // TODO
    @AtAnyDepthDslMarker
    public inline fun <reified C> AtAnyDepthDsl<*>.colsOf(
        noinline filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<C> = context.asSingleColumn().colsOfInternal(typeOf<C>(), scope, filter)

    // endregion
}

public interface AtAnyDepth2ColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {

    @AtAnyDepthDslMarker
    public fun <R : ColumnsResolver<*>> ColumnsSelectionDsl<*>.atAnyDepth2(
        selector: Selector<AtAnyDepthDsl<@UnsafeVariance T>, R>,
    ): R = AtAnyDepthDsl(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>).let { selector(it, it) }

    @AtAnyDepthDslMarker
    public fun <C, R : ColumnsResolver<*>> ColumnSet<C>.atAnyDepth2(
        selector: Selector<AtAnyDepthDsl<*>, R>,
    ): ColumnSet<*> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        this@atAnyDepth2.transformWithContext {
            it.toColumnGroup("")
                .getColumnsWithPaths {
                    AtAnyDepthDsl(it).let { selector(it, it) }
                }
        }
    }

    @AtAnyDepthDslMarker
    public fun <C, S, R : ColumnsResolver<S>> SingleColumn<DataRow<C>>.atAnyDepth2(
        selector: Selector<AtAnyDepthDsl<@UnsafeVariance C>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }

    @AtAnyDepthDslMarker
    public fun <C, S, R : ColumnsResolver<S>> KProperty<C>.atAnyDepth2(
        selector: Selector<AtAnyDepthDsl<@UnsafeVariance C>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        (this@atAnyDepth2 as KProperty<DataRow<C>>).select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("atAnyDepth2KPropertyDataRow")
    @AtAnyDepthDslMarker
    public fun <C, S, R : ColumnsResolver<S>> KProperty<DataRow<C>>.atAnyDepth2(
        selector: Selector<AtAnyDepthDsl<@UnsafeVariance C>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }

    @AtAnyDepthDslMarker
    public fun <S, R : ColumnsResolver<S>> String.atAnyDepth2(
        selector: Selector<AtAnyDepthDsl<*>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }

    @AtAnyDepthDslMarker
    public fun <S, R : ColumnsResolver<S>> ColumnPath.atAnyDepth2(
        selector: Selector<AtAnyDepthDsl<*>, R>,
    ): ColumnSet<S> = with(this@AtAnyDepth2ColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
        select {
            AtAnyDepthDsl(this).let { selector(it, it) }
        }
    }
}

// endregion

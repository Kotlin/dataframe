package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.unfoldImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

public inline fun <reified T> DataColumn<T>.unfold(vararg roots: KCallable<*>, maxDepth: Int = 0): AnyCol =
    unfoldImpl(typeOf<T>()) { properties(roots = roots, maxDepth) }

@Refine
@Interpretable("DataFrameUnfold")
public fun <T> DataFrame<T>.unfold(
    vararg roots: KCallable<*>,
    maxDepth: Int = 0,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = replace(columns).with { it.unfoldImpl(it.type()) { properties(roots = roots, maxDepth) } }

public fun <T> DataFrame<T>.unfold(vararg columns: String): DataFrame<T> = unfold { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.unfold(vararg columns: AnyColumnReference): DataFrame<T> = unfold { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.unfold(vararg columns: KProperty<*>): DataFrame<T> = unfold { columns.toColumnSet() }

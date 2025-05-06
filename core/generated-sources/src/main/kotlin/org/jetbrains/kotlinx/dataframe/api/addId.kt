package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor

// region DataColumn

public fun AnyCol.addId(columnName: String = "id"): AnyFrame = toDataFrame().addId(columnName)

// endregion

// region DataFrame

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T> DataFrame<T>.addId(column: ColumnAccessor<Int>): DataFrame<T> = insert(column) { index() }.at(0)

@Refine
@Interpretable("AddId")
public fun <T> DataFrame<T>.addId(columnName: String = "id"): DataFrame<T> = insert(columnName) { index() }.at(0)

// endregion

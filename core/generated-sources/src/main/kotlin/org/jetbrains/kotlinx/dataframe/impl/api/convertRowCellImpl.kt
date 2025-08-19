package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.exceptions.ColumnTypeMismatchesColumnValuesException
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumn
import kotlin.reflect.KType

@PublishedApi
internal fun <T, C, R> Convert<T, C>.withRowCellImpl(
    type: KType,
    infer: Infer,
    rowConverter: RowValueExpression<T, C, R>,
): DataFrame<T> =
    asColumn { col ->
        try {
            df.newColumn(type, col.name, infer) { rowConverter(it, it[col]) }
        } catch (e: ClassCastException) {
            throw ColumnTypeMismatchesColumnValuesException(col, e)
        } catch (e: NullPointerException) {
            throw ColumnTypeMismatchesColumnValuesException(col, e)
        }
    }

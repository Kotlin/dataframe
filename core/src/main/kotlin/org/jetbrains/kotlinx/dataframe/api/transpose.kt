package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.convertTo
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.values
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region DataRow

public fun AnyRow.transpose(): DataFrame<NameValuePair<*>> {
    val valueColumn = DataColumn.createByInference(NameValuePair<*>::value.columnName, values)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<*>::name.name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

public inline fun <reified T> AnyRow.transposeTo(): DataFrame<NameValuePair<T>> = transposeTo(typeOf<T>())

@PublishedApi
internal fun <T> AnyRow.transposeTo(type: KType): DataFrame<NameValuePair<T>> {
    val convertedValues = values.map { it?.convertTo(type) as T? }
    val valueColumn = DataColumn.createByInference(NameValuePair<T>::value.columnName, convertedValues)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<T>::name.name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

// endregion

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.impl.api.convertTo
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.values
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region DataRow

public fun <T> DataRow<T>.transpose(): DataFrame<NameValuePair<*>> {
    val valueColumn = DataColumn.createWithTypeInference(NameValuePair<*>::value.columnName, values)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<*>::name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

public inline fun <reified T> AnyRow.transposeTo(): DataFrame<NameValuePair<T>> = transposeTo(typeOf<T>())

@PublishedApi
internal fun <T> AnyRow.transposeTo(type: KType): DataFrame<NameValuePair<T>> {
    val convertedValues = values.map { it?.convertTo(type) as T? }
    val valueColumn = DataColumn.createWithTypeInference(NameValuePair<T>::value.columnName, convertedValues)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<T>::name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

// endregion

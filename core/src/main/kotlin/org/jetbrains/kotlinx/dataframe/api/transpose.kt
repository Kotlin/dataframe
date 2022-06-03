package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.values

// region DataRow

@DataSchema
public interface NameValueSchema {
    public val name: String
    public val value: Any?
}

public val DataRow<NameValueSchema>.name: String get() = this["name"] as String
public val ColumnsContainer<NameValueSchema>.name: DataColumn<String> get() = this["name"] as DataColumn<String>
public val DataRow<NameValueSchema>.value: Any? get() = this["value"]
public val ColumnsContainer<NameValueSchema>.value: DataColumn<Any?> get() = this["value"]

public fun <T> DataRow<T>.transpose(): DataFrame<NameValueSchema> {
    val valueColumn = DataColumn.createWithTypeInference(NameValueSchema::value.columnName, values)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<T>::name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

/*

public inline fun <reified T> AnyRow.transposeTo(): DataFrame<NameValuePair<T>> = transposeTo(typeOf<T>())

@PublishedApi
internal fun <T> AnyRow.transposeTo(type: KType): DataFrame<NameValuePair<T>> {
    val convertedValues = values.map { it?.convertTo(type) as T? }
    val valueColumn = DataColumn.createWithTypeInference(NameValuePair<T>::value.columnName, convertedValues)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<T>::name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

 */

// endregion

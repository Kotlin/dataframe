package org.jetbrains.kotlinx.dataframe.examples.exposed

import org.jetbrains.exposed.v1.core.BiCompositeColumn
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.ExpressionAlias
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf

/**
 * Retrieves all columns of any [Iterable][Iterable]`<`[ResultRow][ResultRow]`>`, like [Query][Query],
 * from Exposed row by row and converts the resulting [Map] into a [DataFrame], cast to type [T].
 *
 * In notebooks, the untyped version works just as well due to runtime inference :)
 */
inline fun <reified T : Any> Iterable<ResultRow>.convertToDataFrame(): DataFrame<T> =
    convertToDataFrame().convertTo<T>()

/**
 * Retrieves all columns of an [Iterable][Iterable]`<`[ResultRow][ResultRow]`>` from Exposed, like [Query][Query],
 * row by row and converts the resulting [Map] of lists into a [DataFrame] by calling
 * [Map.toDataFrame].
 */
@JvmName("convertToAnyFrame")
fun Iterable<ResultRow>.convertToDataFrame(): AnyFrame {
    val map = mutableMapOf<String, MutableList<Any?>>()
    for (row in this) {
        for (expression in row.fieldIndex.keys) {
            map.getOrPut(expression.readableName) {
                mutableListOf()
            } += row[expression]
        }
    }
    return map.toDataFrame()
}

/**
 * Retrieves a simple column name from [this] [Expression].
 *
 * Might need to be expanded with multiple types of [Expression].
 */
val Expression<*>.readableName: String
    get() = when (this) {
        is Column<*> -> name
        is ExpressionAlias<*> -> alias
        is BiCompositeColumn<*, *, *> -> getRealColumns().joinToString("_") { it.readableName }
        else -> toString()
    }

/**
 * Creates a [DataFrameSchema] from the declared [Table] instance.
 *
 * This is not needed for conversion, but it can be useful to create a DataFrame [@DataSchema][DataSchema] instance.
 *
 * @param columnNameToAccessor Optional [MutableMap] which will be filled with entries mapping
 *   the SQL column name to the accessor name from the [Table].
 *   This can be used to define a [NameNormalizer] later.
 * @see toDataFrameSchemaWithNameNormalizer
 */
@Suppress("UNCHECKED_CAST")
fun Table.toDataFrameSchema(columnNameToAccessor: MutableMap<String, String> = mutableMapOf()): DataFrameSchema {
    // we use reflection to go over all `Column<*>` properties in the Table object
    val columns = this::class.memberProperties
        .filter { it.returnType.isSubtypeOf(typeOf<Column<*>>()) }
        .associate { prop ->
            prop as KProperty1<Table, Column<*>>

            // retrieve the SQL column name
            val columnName = prop.get(this).name
            // store the SQL column name together with the accessor name in the map
            columnNameToAccessor[columnName] = prop.name

            // get the column type from `val a: Column<Type>`
            val type = prop.returnType.arguments.first().type!!

            // and we add the name and column shema type to the `columns` map :)
            columnName to ColumnSchema.Value(type)
        }
    return DataFrameSchemaImpl(columns)
}

/**
 * Creates a [DataFrameSchema] from the declared [Table] instance with a [NameNormalizer] to
 * convert the SQL column names to the corresponding Kotlin property names.
 *
 * This is not needed for conversion, but it can be useful to create a DataFrame [@DataSchema][DataSchema] instance.
 *
 * @see toDataFrameSchema
 */
fun Table.toDataFrameSchemaWithNameNormalizer(): Pair<DataFrameSchema, NameNormalizer> {
    val columnNameToAccessor = mutableMapOf<String, String>()
    return Pair(toDataFrameSchema(), NameNormalizer { columnNameToAccessor[it] ?: it })
}

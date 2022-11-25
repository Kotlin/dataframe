package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.with
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

/**
 * Function to be used in [ConvertSchemaDsl] ([DataFrame.convertTo]) to help convert a DataFrame to adhere to an
 * OpenApi schema. Is used in generated OpenAPI code.
 */
public fun ConvertSchemaDsl<*>.convertDataRowsWithOpenApi() {
    // Convert DataRow to Any if the schema requires the types to be erased.
    convert<DataRow<*>>().with<_, Any?> { it }

    // Provide converter for (recursive) List<DataFrame<>>
    convertIf({ fromType, toSchema ->
        val (fromIsRecursiveListOfDataFrame, fromDepth) = fromType.isRecursiveListOfDataFrame()
        val (toIsRecursiveListOfDataFrame, toDepth) = toSchema.type.isRecursiveListOfDataFrame()

        fromIsRecursiveListOfDataFrame && toIsRecursiveListOfDataFrame && fromDepth == toDepth
    }) {
        try {
            it.convertRecursiveListOfDataFrame(toSchema.type) {
                convertDataRowsWithOpenApi()
            }
        } catch (_: Exception) {
            it
        }
    }
}

/**
 * @receiver [KType] to check if it is a recursive list of [DataFrame]s
 * @return [Pair] of result and the recursive depth.
 *   `true` if Receiver is a recursive list of [DataFrame]s, like [List]<[List]<[DataFrame]<*>>>
 */
private fun KType.isRecursiveListOfDataFrame(depth: Int = 0): Pair<Boolean, Int> = when (jvmErasure) {
    typeOf<List<*>>().jvmErasure -> arguments[0].type?.isRecursiveListOfDataFrame(depth + 1) ?: (false to depth)
    typeOf<DataFrame<*>>().jvmErasure -> true to depth
    typeOf<DataFrame<*>?>().jvmErasure -> true to depth
    else -> false to depth
}

/**
 * @receiver Recursive [List] of [DataFrame]s, like [List]<[List]<[DataFrame]<*>>>, for which to convert the [DataFrame]s.
 * @param type Type to which to convert the [DataFrame]s.
 * @param convertTo Optional [ConvertSchemaDsl] to use for the conversion.
 * @return Receiver with converted [DataFrame]s.
 */
private fun Any?.convertRecursiveListOfDataFrame(
    type: KType,
    convertTo: ConvertSchemaDsl<*>.() -> Unit = {},
): Any? = when (this) {
    is List<*> -> map { it?.convertRecursiveListOfDataFrame(type.arguments[0].type!!, convertTo) }
    is DataFrame<*> -> convertTo(schemaType = type.arguments[0].type!!, body = convertTo)
    null -> null
    else -> throw IllegalArgumentException("$this is not a List or DataFrame")
}

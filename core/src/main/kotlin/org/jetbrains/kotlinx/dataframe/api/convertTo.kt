package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.exceptions.*
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public enum class ExcessiveColumns { Remove, Keep, Fail }

public interface ConvertSchemaDsl<in T> {

    public fun <A, B> convert(from: KType, to: KType, converter: (A) -> B)
}

/**
 * Defines how to convert `String` values into given type [C].
 */
public inline fun <reified C> ConvertSchemaDsl<*>.parser(noinline parser: (String) -> C): Unit = convert<String>().with(parser)

/**
 * Defines how to convert values of given type [C]
 */
public inline fun <reified C> ConvertSchemaDsl<*>.convert(): ConvertType<C> = ConvertType(this, typeOf<C>())

/**
 * Defines how to convert values of type [C] into type [R]
 */
public inline fun <C, reified R> ConvertType<C>.with(noinline converter: (C) -> R): Unit = dsl.convert(from, typeOf<R>(), converter)

public class ConvertType<T>(
    @PublishedApi internal val dsl: ConvertSchemaDsl<*>,
    @PublishedApi internal val from: KType,
    internal val property: KProperty<T>? = null
)

// region DataFrame

/**
 * Converts values in [DataFrame] to match given column schema [T].
 *
 * Original columns are mapped to destination columns by column [path][DataColumn.path].
 *
 * Type converters for every column are selected automatically. See [convert] operation for details.
 *
 * To specify custom type converters for the particular types use [ConvertSchemaDsl].
 *
 * @param [T] class that defines target schema for conversion.
 * @param [excessiveColumnsBehavior] how to handle excessive columns in the original [DataFrame].
 * @throws [ColumnNotFoundException] if [DataFrame] doesn't contain columns that are required by destination schema.
 * @throws [ExcessiveColumnsException] if [DataFrame] contains columns that are not required by destination schema and [excessiveColumnsBehavior] is set to [ExcessiveColumns.Fail].
 * @throws [TypeConverterNotFoundException] if suitable type converter for some column was not found.
 * @throws [TypeConversionException] if type converter failed to convert column values.
 * @return converted [DataFrame].
 */
public inline fun <reified T : Any> AnyFrame.convertTo(
    excessiveColumnsBehavior: ExcessiveColumns = ExcessiveColumns.Keep,
    noinline body: ConvertSchemaDsl<T>.() -> Unit = {}
): DataFrame<T> = convertTo(typeOf<T>(), excessiveColumnsBehavior, body).cast()

public fun AnyFrame.convertTo(
    schemaType: KType,
    excessiveColumnsBehavior: ExcessiveColumns = ExcessiveColumns.Keep,
    body: ConvertSchemaDsl<Any>.() -> Unit = {}
): AnyFrame = convertToImpl(schemaType, true, excessiveColumnsBehavior, body)

// endregion

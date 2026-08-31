package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.exceptions.ColumnNotFoundException
import org.jetbrains.kotlinx.dataframe.exceptions.ExcessiveColumnsException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConversionException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.jetbrains.kotlinx.dataframe.impl.api.ConvertSchemaDslInternal
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Specifies how to handle columns in the original dataframe that were not matched to any column in destination dataframe schema.
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 */
public enum class ExcessiveColumns {
    /**
     * Remove excessive columns from resulting dataframe
     */
    Remove,

    /**
     * Keep excessive columns in resulting dataframe
     */
    Keep,

    /**
     * Throw [ExcessiveColumnsException] if any excessive columns were found in the original dataframe
     */
    Fail,
}

/**
 * Holds data context for [<code>fill</code>][fill] operation
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 */
public data class ConvertToFill<T, C>(internal val dsl: ConvertSchemaDsl<T>, val columns: ColumnsSelector<T, C>)

/**
 * Provides access to [<code>fromType</code>][fromType] and [<code>toSchema</code>][toSchema] in the flexible [<code>ConvertSchemaDsl.convertIf</code>][ConvertSchemaDsl.convertIf] method.
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 */
public class ConverterScope(public val fromType: KType, public val toSchema: ColumnSchema)

/**
 * Dsl to customize column conversion
 *
 * Example:
 * ```kotlin
 * df.convertTo<SomeSchema> {
 *     // defines how to convert Int? -> String
 *     convert<Int?>().with { it?.toString() ?: "No input given" }
 *     // defines how to convert String/Char -> SomeType
 *     parser { SomeType(it) }
 *     // fill missing column `sum` with expression `a+b`
 *     fill { sum }.with { a + b }
 * }
 * ```
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 */
public interface ConvertSchemaDsl<in T> {

    /**
     * Defines how to convert [<code>from</code>][from]: [<code>A</code>][A] to [<code>to</code>][to]: [<code>B</code>][B].
     *
     * Note: In most cases using `convert<Type>().with { }` is more convenient, however
     * if you only have [<code>KType</code>][KType], this method can be used.
     *
     * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
     */
    public fun <A, B> convert(from: KType, to: KType, converter: (A) -> B)

    /**
     * Advanced version of [<code>convert</code>][convert].
     * If you want to define a common conversion for multiple types (or any type), or
     * you need extra information about the target, such as its schema, use this method.
     *
     * The exact type conversion does have higher priority. After that, this flexible conversions will be checked
     * in order.
     *
     * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
     *
     * @param condition a function that should return `true` if the conversion should be applied from the given `fromType`
     *   to the given `toSchema`.
     * @param converter a function that performs the conversion with access to a [<code>ConverterScope</code>][ConverterScope].
     */
    public fun convertIf(
        condition: (fromType: KType, toSchema: ColumnSchema) -> Boolean,
        converter: ConverterScope.(Any?) -> Any?,
    )
}

/**
 * Defines how to fill specified columns in destination schema that were not found in the original dataframe.
 * All [<code>fill</code>][fill] operations for missing columns are executed after successful conversion of matched columns, so converted values of matched columns can be safely used in [<code>with</code>][with] expression.
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 *
 * @param columns target columns in the destination dataframe schema to be filled
 */
public inline fun <T, reified C> ConvertSchemaDsl<T>.fill(
    noinline columns: ColumnsSelector<T, C>,
): ConvertToFill<T, C> = ConvertToFill(this, columns)

public fun <T, C> ConvertToFill<T, C>.with(expr: RowExpression<T, C>) {
    (dsl as ConvertSchemaDslInternal<T>).fill(columns as ColumnsSelector<*, C>, expr as RowExpression<*, C>)
}

/**
 * Defines how to convert `String` values into given type [<code>C</code>][C].
 *
 * This method is a shortcut for `convert<String>().with { }`.
 *
 * If no converter is defined for `Char` values, this converter will be used for them as well.
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 */
public inline fun <reified C> ConvertSchemaDsl<*>.parser(noinline parser: (String) -> C): Unit =
    convert<String>().with(parser)

/**
 * Defines how to convert values of given type [<code>C</code>][C]
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 */
public inline fun <reified C> ConvertSchemaDsl<*>.convert(): ConvertType<C> = ConvertType(this, typeOf<C>())

/**
 * Defines how to convert values of type [<code>C</code>][C] into type [<code>R</code>][R]
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 */
public inline fun <C, reified R> ConvertType<C>.with(noinline converter: (C) -> R): Unit =
    dsl.convert(from, typeOf<R>(), converter)

public class ConvertType<T>(
    @PublishedApi internal val dsl: ConvertSchemaDsl<*>,
    @PublishedApi internal val from: KType,
    internal val property: KProperty<T>? = null,
)

// region DataFrame

/**
 * Converts values in [<code>DataFrame</code>][DataFrame] to match given column schema [<code>T</code>][T].
 *
 * Original columns are mapped to destination columns by column [<code>path</code>][DataColumn.path].
 *
 * Type converters for every column are selected automatically. See [<code>convert</code>][convert] operation for details.
 *
 * To specify custom type converters for the particular types use [<code>ConvertSchemaDsl</code>][ConvertSchemaDsl].
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 *
 * Example of Dsl:
 * ```kotlin
 * df.convertTo<SomeSchema> {
 *     // defines how to convert Int? -> String
 *     convert<Int?>().with { it?.toString() ?: "No input given" }
 *     // defines how to convert String -> SomeType
 *     parser { SomeType(it) }
 *     // fill missing column `sum` with expression `a + b`
 *     fill { sum }.with { a + b }
 * }
 * ```
 *
 * @param [T] class that defines target schema for conversion.
 * @param [excessiveColumnsBehavior] how to handle excessive columns in the original [<code>DataFrame</code>][DataFrame].
 * @param [body] optional dsl to define custom type converters.
 * @throws [ColumnNotFoundException] if [<code>DataFrame</code>][DataFrame] doesn't contain columns that are required by destination schema.
 * @throws [ExcessiveColumnsException] if [<code>DataFrame</code>][DataFrame] contains columns that are not required by destination schema and [<code>excessiveColumnsBehavior</code>][excessiveColumnsBehavior] is set to [<code>ExcessiveColumns.Fail</code>][ExcessiveColumns.Fail].
 * @throws [TypeConverterNotFoundException] if suitable type converter for some column was not found.
 * @throws [TypeConversionException] if type converter failed to convert column values.
 * @return converted [<code>DataFrame</code>][DataFrame].
 */
public inline fun <reified T : Any> DataFrame<*>.convertTo(
    excessiveColumnsBehavior: ExcessiveColumns = ExcessiveColumns.Keep,
    noinline body: ConvertSchemaDsl<T>.() -> Unit = {},
): DataFrame<T> = convertToImpl(typeOf<T>(), true, excessiveColumnsBehavior, body).cast()

/**
 * Converts values in [<code>DataFrame</code>][DataFrame] to match given column schema [<code>T</code>][T].
 *
 * Original columns are mapped to destination columns by column [<code>path</code>][DataColumn.path].
 *
 * Type converters for every column are selected automatically. See [<code>convert</code>][convert] operation for details.
 *
 * To specify custom type converters for the particular types use [<code>ConvertSchemaDsl</code>][ConvertSchemaDsl].
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 *
 * Example of Dsl:
 * ```kotlin
 * df.convertTo(schemaFrom = sample) {
 *     // defines how to convert Int? -> String
 *     convert<Int?>().with { it?.toString() ?: "No input given" }
 *     // defines how to convert String -> SomeType
 *     parser { SomeType(it) }
 *     // fill missing column `sum` with expression `a + b`
 *     fill { sum }.with { a + b }
 * }
 * ```
 *
 * @param [T] class that defines target schema for conversion.
 * @param [schemaFrom] dataframe which type [<code>T</code>][T] will be used.
 * @param [excessiveColumnsBehavior] how to handle excessive columns in the original [<code>DataFrame</code>][DataFrame].
 * @param [body] optional dsl to define custom type converters.
 * @throws [ColumnNotFoundException] if [<code>DataFrame</code>][DataFrame] doesn't contain columns that are required by destination schema.
 * @throws [ExcessiveColumnsException] if [<code>DataFrame</code>][DataFrame] contains columns that are not required by destination schema and [<code>excessiveColumnsBehavior</code>][excessiveColumnsBehavior] is set to [<code>ExcessiveColumns.Fail</code>][ExcessiveColumns.Fail].
 * @throws [TypeConverterNotFoundException] if suitable type converter for some column was not found.
 * @throws [TypeConversionException] if type converter failed to convert column values.
 * @return converted [<code>DataFrame</code>][DataFrame].
 */
public inline fun <reified T : Any> DataFrame<*>.convertTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: DataFrame<T>,
    excessiveColumnsBehavior: ExcessiveColumns = ExcessiveColumns.Keep,
    noinline body: ConvertSchemaDsl<T>.() -> Unit = {},
): DataFrame<T> = convertToImpl(typeOf<T>(), true, excessiveColumnsBehavior, body).cast()

/**
 * Converts values in [<code>DataFrame</code>][DataFrame] to match given column schema [<code>schemaType</code>][schemaType].
 *
 * Original columns are mapped to destination columns by column [<code>path</code>][DataColumn.path].
 *
 * Type converters for every column are selected automatically. See [<code>convert</code>][convert] operation for details.
 *
 * To specify custom type converters for the particular types use [<code>ConvertSchemaDsl</code>][ConvertSchemaDsl].
 *
 * For more information: [See `convertTo` on the documentation website.](https://kotlin.github.io/dataframe/convertto.html)
 *
 * Example of Dsl:
 * ```kotlin
 * df.convertTo<SomeSchema> {
 *     // defines how to convert Int? -> String
 *     convert<Int?>().with { it?.toString() ?: "No input given" }
 *     // defines how to convert String -> SomeType
 *     parser { SomeType(it) }
 *     // fill missing column `sum` with expression `a+b`
 *     fill { sum }.with { a + b }
 * }
 * ```
 *
 * @param [schemaType] defines target schema for conversion.
 * @param [excessiveColumnsBehavior] how to handle excessive columns in the original [<code>DataFrame</code>][DataFrame].
 * @param [body] optional dsl to define custom type converters.
 * @throws [ColumnNotFoundException] if [<code>DataFrame</code>][DataFrame] doesn't contain columns that are required by destination schema.
 * @throws [ExcessiveColumnsException] if [<code>DataFrame</code>][DataFrame] contains columns that are not required by destination schema and [<code>excessiveColumnsBehavior</code>][excessiveColumnsBehavior] is set to [<code>ExcessiveColumns.Fail</code>][ExcessiveColumns.Fail].
 * @throws [TypeConverterNotFoundException] if suitable type converter for some column was not found.
 * @throws [TypeConversionException] if type converter failed to convert column values.
 * @return converted [<code>DataFrame</code>][DataFrame].
 */
public fun DataFrame<*>.convertTo(
    schemaType: KType,
    excessiveColumnsBehavior: ExcessiveColumns = ExcessiveColumns.Keep,
    body: ConvertSchemaDsl<Any>.() -> Unit = {},
): DataFrame<*> = convertToImpl(schemaType, true, excessiveColumnsBehavior, body)

// endregion

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region inferType

/**
 * Changes the type of a column based on the runtime values stored in it.
 *
 * The new type is the nearest common supertype of the values:
 * for example, `1` and `2.0` together give the type [<code>Number</code>][Number], and `1` and `null` give `Int?`.
 * The inferred type never goes outside the current type of the column — the current type is an upper bound:
 * for example, `1` and `2.0` in a column of type [<code>Comparable</code>][Comparable]`<*>` keep the type [<code>Comparable</code>][Comparable]`<*>`,
 * because [<code>Number</code>][Number] is not [<code>Comparable</code>][Comparable].
 * The inferred type is nullable if the column contains `null` values, and non-nullable otherwise.
 *
 * The name, the position, and the values of the column stay the same; only its type can change.
 * A column group stays a column group, and the value columns inside it are inferred.
 * A frame column is returned as it is: the dataframes inside it keep their schema.
 * `inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
 * of strings — `"1"` does not become `1`.
 * To parse string values into typed values, use [<code>parse</code>][DataFrame.parse].
 * To convert values explicitly, use [<code>convert</code>][DataFrame.convert].
 *
 * Only the runtime types of the columns change: the static types known to the compiler
 * are not affected, so in a project with the compiler plugin the column accessors keep the old types.
 *
 * `inferType` exists for one practical case: a column can be created with a not precise type, for example
 * `Any?` — after reading data without a schema, after [<code>filter</code>][DataFrame.filter], or after a transformation
 * that disabled inference, such as [<code>convert</code>][DataFrame.convert] with [<code>Infer.None</code>][Infer.None]. `inferType` checks
 * whether such a type can be a more useful, specific one: in a [<code>DataFrame</code>][DataFrame] it is preferred to know what
 * is inside the columns.
 * More precise column types then help operations that depend on the schema, such as
 * [<code>colsOf</code>][ColumnsSelectionDsl.colsOf], [<code>convertTo</code>][DataFrame.convertTo],
 * [<code>generateDataClasses</code>][DataFrame.generateDataClasses], or schema checks.
 *
 * For more information: [See `inferType` on the documentation website.](https://kotlin.github.io/dataframe/infertype.html)
 *
 * See also:
 * - [<code>parse</code>][DataFrame.parse] — also changes the column type, by parsing string values into typed values.
 * - [<code>convert</code>][DataFrame.convert] — also changes the column type, by converting the values with a type converter.
 * - [<code>convertTo</code>][DataFrame.convertTo] — converts the values of the whole [<code>DataFrame</code>][DataFrame] to match a given schema.
 */
internal interface InferTypeDocs {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>inferType</code>][org.jetbrains.kotlinx.dataframe.api.inferType]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>inferType</code>][org.jetbrains.kotlinx.dataframe.api.inferType]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>inferType</code>][org.jetbrains.kotlinx.dataframe.api.inferType]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>inferType</code>][org.jetbrains.kotlinx.dataframe.api.inferType]`("length", "age")`
     *
     *
     *
     */
    typealias InferTypeSelectingOptions = Nothing
}

/**
 * Changes the type of a column based on the runtime values stored in it.
 *
 * The new type is the nearest common supertype of the values:
 * for example, `1` and `2.0` together give the type [<code>Number</code>][Number], and `1` and `null` give `Int?`.
 * The inferred type never goes outside the current type of the column — the current type is an upper bound:
 * for example, `1` and `2.0` in a column of type [<code>Comparable</code>][Comparable]`<*>` keep the type [<code>Comparable</code>][Comparable]`<*>`,
 * because [<code>Number</code>][Number] is not [<code>Comparable</code>][Comparable].
 * The inferred type is nullable if the column contains `null` values, and non-nullable otherwise.
 *
 * The name, the position, and the values of the column stay the same; only its type can change.
 * A column group stays a column group, and the value columns inside it are inferred.
 * A frame column is returned as it is: the dataframes inside it keep their schema.
 * `inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
 * of strings — `"1"` does not become `1`.
 * To parse string values into typed values, use [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse].
 * To convert values explicitly, use [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert].
 *
 * Only the runtime types of the columns change: the static types known to the compiler
 * are not affected, so in a project with the compiler plugin the column accessors keep the old types.
 *
 * `inferType` exists for one practical case: a column can be created with a not precise type, for example
 * `Any?` — after reading data without a schema, after [<code>filter</code>][org.jetbrains.kotlinx.dataframe.DataFrame.filter], or after a transformation
 * that disabled inference, such as [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] with [<code>Infer.None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None]. `inferType` checks
 * whether such a type can be a more useful, specific one: in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] it is preferred to know what
 * is inside the columns.
 * More precise column types then help operations that depend on the schema, such as
 * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo],
 * [<code>generateDataClasses</code>][org.jetbrains.kotlinx.dataframe.DataFrame.generateDataClasses], or schema checks.
 *
 * For more information: [See `inferType` on the documentation website.](https://kotlin.github.io/dataframe/infertype.html)
 *
 * See also:
 * - [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse] — also changes the column type, by parsing string values into typed values.
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] — also changes the column type, by converting the values with a type converter.
 * - [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo] — converts the values of the whole [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to match a given schema.
 *
 * ### Examples
 *
 * ```kotlin
 * val items = columnOf<Comparable<*>>("Alice", 1, 3.5)
 * val strings = items.filter { it is String }
 * val inferred = strings.inferType()
 * ```
 *
 * ```kotlin
 * val numbersAsText = DataColumn.createValueColumn("numbersAsText", listOf("1", "2", "3"), typeOf<Any?>())
 * val inferred = numbersAsText.inferType()
 * ```
 *
 * ```kotlin
 * val mixed = DataColumn.createValueColumn("mixed", listOf<Any?>(1, 2.0), typeOf<Any?>())
 * val inferred = mixed.inferType()
 * ```
 *
 * @return This column with its type inferred from the values stored in it; a column group stays a column
 * group with the value columns inside it inferred, and a frame column is returned as it is.
 */
public fun DataColumn<*>.inferType(): DataColumn<*> =
    when {
        // consistent with parse: a column group recurses into its nested value columns
        isColumnGroup() -> asColumnGroup().inferType().asColumnGroup(name).asDataColumn()

        // the dataframes inside a frame column keep their schema
        isFrameColumn() -> this

        else -> createColumnGuessingType(
            name = name,
            values = toList(),
            suggestedType = TypeSuggestion.InferWithUpperbound(type),
        )
    }

// endregion

// region DataFrame

/**
 * Changes the type of a column based on the runtime values stored in it.
 *
 * The new type is the nearest common supertype of the values:
 * for example, `1` and `2.0` together give the type [<code>Number</code>][Number], and `1` and `null` give `Int?`.
 * The inferred type never goes outside the current type of the column — the current type is an upper bound:
 * for example, `1` and `2.0` in a column of type [<code>Comparable</code>][Comparable]`<*>` keep the type [<code>Comparable</code>][Comparable]`<*>`,
 * because [<code>Number</code>][Number] is not [<code>Comparable</code>][Comparable].
 * The inferred type is nullable if the column contains `null` values, and non-nullable otherwise.
 *
 * The name, the position, and the values of the column stay the same; only its type can change.
 * A column group stays a column group, and the value columns inside it are inferred.
 * A frame column is returned as it is: the dataframes inside it keep their schema.
 * `inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
 * of strings — `"1"` does not become `1`.
 * To parse string values into typed values, use [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse].
 * To convert values explicitly, use [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert].
 *
 * Only the runtime types of the columns change: the static types known to the compiler
 * are not affected, so in a project with the compiler plugin the column accessors keep the old types.
 *
 * `inferType` exists for one practical case: a column can be created with a not precise type, for example
 * `Any?` — after reading data without a schema, after [<code>filter</code>][org.jetbrains.kotlinx.dataframe.DataFrame.filter], or after a transformation
 * that disabled inference, such as [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] with [<code>Infer.None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None]. `inferType` checks
 * whether such a type can be a more useful, specific one: in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] it is preferred to know what
 * is inside the columns.
 * More precise column types then help operations that depend on the schema, such as
 * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo],
 * [<code>generateDataClasses</code>][org.jetbrains.kotlinx.dataframe.DataFrame.generateDataClasses], or schema checks.
 *
 * For more information: [See `inferType` on the documentation website.](https://kotlin.github.io/dataframe/infertype.html)
 *
 * See also:
 * - [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse] — also changes the column type, by parsing string values into typed values.
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] — also changes the column type, by converting the values with a type converter.
 * - [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo] — converts the values of the whole [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to match a given schema.
 *
 * This overload infers the types of all value columns at any depth — the columns inside column groups too.
 * Column groups stay column groups, and frame columns stay frame columns;
 * the dataframes inside frame columns keep their schema.
 *
 * ### Examples
 *
 * ```kotlin
 * val inferred = df.inferType()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the inferred types of all value columns.
 */
public fun <T> DataFrame<T>.inferType(): DataFrame<T> =
    inferType {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

/**
 * Changes the type of a column based on the runtime values stored in it.
 *
 * The new type is the nearest common supertype of the values:
 * for example, `1` and `2.0` together give the type [<code>Number</code>][Number], and `1` and `null` give `Int?`.
 * The inferred type never goes outside the current type of the column — the current type is an upper bound:
 * for example, `1` and `2.0` in a column of type [<code>Comparable</code>][Comparable]`<*>` keep the type [<code>Comparable</code>][Comparable]`<*>`,
 * because [<code>Number</code>][Number] is not [<code>Comparable</code>][Comparable].
 * The inferred type is nullable if the column contains `null` values, and non-nullable otherwise.
 *
 * The name, the position, and the values of the column stay the same; only its type can change.
 * A column group stays a column group, and the value columns inside it are inferred.
 * A frame column is returned as it is: the dataframes inside it keep their schema.
 * `inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
 * of strings — `"1"` does not become `1`.
 * To parse string values into typed values, use [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse].
 * To convert values explicitly, use [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert].
 *
 * Only the runtime types of the columns change: the static types known to the compiler
 * are not affected, so in a project with the compiler plugin the column accessors keep the old types.
 *
 * `inferType` exists for one practical case: a column can be created with a not precise type, for example
 * `Any?` — after reading data without a schema, after [<code>filter</code>][org.jetbrains.kotlinx.dataframe.DataFrame.filter], or after a transformation
 * that disabled inference, such as [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] with [<code>Infer.None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None]. `inferType` checks
 * whether such a type can be a more useful, specific one: in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] it is preferred to know what
 * is inside the columns.
 * More precise column types then help operations that depend on the schema, such as
 * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo],
 * [<code>generateDataClasses</code>][org.jetbrains.kotlinx.dataframe.DataFrame.generateDataClasses], or schema checks.
 *
 * For more information: [See `inferType` on the documentation website.](https://kotlin.github.io/dataframe/infertype.html)
 *
 * See also:
 * - [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse] — also changes the column type, by parsing string values into typed values.
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] — also changes the column type, by converting the values with a type converter.
 * - [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo] — converts the values of the whole [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to match a given schema.
 *
 * Among the selected columns, value columns are inferred, including the value columns inside a selected
 * column group; a selected column group stays a column group, and a frame column is returned as it is.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][InferTypeDocs.InferTypeSelectingOptions].
 *
 * ### Examples
 *
 * ```kotlin
 * val inferred = df.inferType { value }
 * ```
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame]
 * to infer the type of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the inferred types of the selected columns.
 */
public fun <T> DataFrame<T>.inferType(columns: ColumnsSelector<T, *>): DataFrame<T> =
    replace(columns).with { it.inferType() }

/**
 * Changes the type of a column based on the runtime values stored in it.
 *
 * The new type is the nearest common supertype of the values:
 * for example, `1` and `2.0` together give the type [<code>Number</code>][Number], and `1` and `null` give `Int?`.
 * The inferred type never goes outside the current type of the column — the current type is an upper bound:
 * for example, `1` and `2.0` in a column of type [<code>Comparable</code>][Comparable]`<*>` keep the type [<code>Comparable</code>][Comparable]`<*>`,
 * because [<code>Number</code>][Number] is not [<code>Comparable</code>][Comparable].
 * The inferred type is nullable if the column contains `null` values, and non-nullable otherwise.
 *
 * The name, the position, and the values of the column stay the same; only its type can change.
 * A column group stays a column group, and the value columns inside it are inferred.
 * A frame column is returned as it is: the dataframes inside it keep their schema.
 * `inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
 * of strings — `"1"` does not become `1`.
 * To parse string values into typed values, use [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse].
 * To convert values explicitly, use [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert].
 *
 * Only the runtime types of the columns change: the static types known to the compiler
 * are not affected, so in a project with the compiler plugin the column accessors keep the old types.
 *
 * `inferType` exists for one practical case: a column can be created with a not precise type, for example
 * `Any?` — after reading data without a schema, after [<code>filter</code>][org.jetbrains.kotlinx.dataframe.DataFrame.filter], or after a transformation
 * that disabled inference, such as [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] with [<code>Infer.None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None]. `inferType` checks
 * whether such a type can be a more useful, specific one: in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] it is preferred to know what
 * is inside the columns.
 * More precise column types then help operations that depend on the schema, such as
 * [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf], [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo],
 * [<code>generateDataClasses</code>][org.jetbrains.kotlinx.dataframe.DataFrame.generateDataClasses], or schema checks.
 *
 * For more information: [See `inferType` on the documentation website.](https://kotlin.github.io/dataframe/infertype.html)
 *
 * See also:
 * - [<code>parse</code>][org.jetbrains.kotlinx.dataframe.DataFrame.parse] — also changes the column type, by parsing string values into typed values.
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] — also changes the column type, by converting the values with a type converter.
 * - [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convertTo] — converts the values of the whole [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to match a given schema.
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * Among the selected columns, value columns are inferred, including the value columns inside a selected
 * column group; a selected column group stays a column group, and a frame column is returned as it is.
 *
 * ### Examples
 *
 * ```kotlin
 * val inferred = df.inferType("value")
 * ```
 *
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame]
 * to infer the type of.
 * @return A new [<code>DataFrame</code>][DataFrame] with the inferred types of the selected columns.
 */
public fun <T> DataFrame<T>.inferType(vararg columns: String): DataFrame<T> = inferType { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.inferType(vararg columns: ColumnReference<*>): DataFrame<T> =
    inferType { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.inferType(vararg columns: KProperty<*>): DataFrame<T> = inferType { columns.toColumnSet() }

// endregion

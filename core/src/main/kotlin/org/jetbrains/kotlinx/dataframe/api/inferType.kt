package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
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
 * for example, `1` and `2.0` together give the type [Number], and `1` and `null` give `Int?`.
 * The inferred type never goes outside the current type of the column — the current type is an upper bound:
 * for example, `1` and `2.0` in a column of type [Comparable]`<*>` keep the type [Comparable]`<*>`,
 * because [Number] is not [Comparable].
 * The inferred type is nullable if the column contains `null` values, and non-nullable otherwise.
 *
 * The name, the position, and the values of the column stay the same; only its type can change.
 * A column group stays a column group, and the value columns inside it are inferred.
 * A frame column is returned as it is: the dataframes inside it keep their schema.
 * `inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
 * of strings — `"1"` does not become `1`.
 * To parse string values into typed values, use [parse][DataFrame.parse].
 * To convert values explicitly, use [convert][DataFrame.convert].
 *
 * Only the runtime types of the columns change: the static types known to the compiler
 * are not affected, so in a project with the compiler plugin the column accessors keep the old types.
 *
 * `inferType` exists for one practical case: a column can be created with a not precise type, for example
 * `Any?` — after reading data without a schema, after [filter][DataFrame.filter], or after a transformation
 * that disabled inference, such as [convert][DataFrame.convert] with [Infer.None]. `inferType` checks
 * whether such a type can be a more useful, specific one: in a [DataFrame] it is preferred to know what
 * is inside the columns.
 * More precise column types then help operations that depend on the schema, such as
 * [colsOf][ColumnsSelectionDsl.colsOf], [convertTo][DataFrame.convertTo],
 * [generateDataClasses][DataFrame.generateDataClasses], or schema checks.
 *
 * For more information: {@include [DocumentationUrls.InferType]}
 *
 * See also:
 * - [parse][DataFrame.parse] — also changes the column type, by parsing string values into typed values.
 * - [convert][DataFrame.convert] — also changes the column type, by converting the values with a type converter.
 * - [convertTo][DataFrame.convertTo] — converts the values of the whole [DataFrame] to match a given schema.
 */
internal interface InferTypeDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetInferTypeOperationArg]}
     */
    typealias InferTypeSelectingOptions = Nothing
}

/** {@set [SelectingColumns.OPERATION] [inferType][inferType]} */
@ExcludeFromSources
private typealias SetInferTypeOperationArg = Nothing

/**
 * @include [InferTypeDocs]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.InferTypeSamples.inferTypeNarrowsColumnAfterFilter]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.InferTypeSamples.inferTypeDoesNotParseStrings]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.InferTypeSamples.inferTypeMixedValues]
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
 * @include [InferTypeDocs]
 *
 * This overload infers the types of all value columns at any depth — the columns inside column groups too.
 * Column groups stay column groups, and frame columns stay frame columns;
 * the dataframes inside frame columns keep their schema.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.InferTypeSamples.inferTypeAllColumns]
 *
 * @return A new [DataFrame] with the inferred types of all value columns.
 */
public fun <T> DataFrame<T>.inferType(): DataFrame<T> =
    inferType {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

/**
 * @include [InferTypeDocs]
 *
 * Among the selected columns, value columns are inferred, including the value columns inside a selected
 * column group; a selected column group stays a column group, and a frame column is returned as it is.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See [Selecting Columns][InferTypeDocs.InferTypeSelectingOptions].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.InferTypeSamples.inferTypeSelectedColumns_properties]
 *
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame]
 * to infer the type of.
 * @return A new [DataFrame] with the inferred types of the selected columns.
 */
public fun <T> DataFrame<T>.inferType(columns: ColumnsSelector<T, *>): DataFrame<T> =
    replace(columns).with { it.inferType() }

/**
 * @include [InferTypeDocs]
 * @include [SelectingColumns.ColumnNamesApi] {@include [SetInferTypeOperationArg]}
 *
 * Among the selected columns, value columns are inferred, including the value columns inside a selected
 * column group; a selected column group stays a column group, and a frame column is returned as it is.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.InferTypeSamples.inferTypeSelectedColumns_strings]
 *
 * @param [columns\] The [Column Names][String] used to select the columns of this [DataFrame]
 * to infer the type of.
 * @return A new [DataFrame] with the inferred types of the selected columns.
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

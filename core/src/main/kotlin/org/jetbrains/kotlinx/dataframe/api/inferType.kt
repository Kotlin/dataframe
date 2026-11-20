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
 * Column groups and frame columns are not inference subjects: they stay as they are.
 * `inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
 * of strings — `"1"` does not become `1`.
 * To parse string values into typed values, use [parse][DataFrame.parse].
 * To convert values explicitly, use [convert][DataFrame.convert].
 *
 * Only the runtime types of the columns change: the static types known to the compiler
 * are not affected, so in a project with the compiler plugin the column accessors keep the old types.
 *
 * `inferType` is useful when the values are more specific than the column type:
 * - after [filter][DataFrame.filter], which keeps the original column type even when all remaining values
 *   have a narrower type;
 * - after a transformation that disabled inference, such as [convert][DataFrame.convert] with [Infer.None];
 * - after reading data without a schema, when columns get broad types like `Any?`.
 * More precise column types then help operations that depend on the schema, such as selecting columns
 * by type with [colsOf][ColumnsSelectionDsl.colsOf], [convertTo][DataFrame.convertTo], or schema checks.
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
 * @return This column with its type inferred from the values stored in it;
 * a column group or a frame column is returned as it is.
 */
public fun DataColumn<*>.inferType(): DataColumn<*> =
    // column groups and frame columns are not inference subjects: leave them as they are
    if (isValueColumn()) {
        createColumnGuessingType(
            name = name,
            values = toList(),
            suggestedType = TypeSuggestion.InferWithUpperbound(type),
        )
    } else {
        this
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
 * Among the selected columns, only value columns are inferred; column groups and frame columns are left
 * unchanged.
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
 * Among the selected columns, only value columns are inferred; column groups and frame columns are left
 * unchanged.
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

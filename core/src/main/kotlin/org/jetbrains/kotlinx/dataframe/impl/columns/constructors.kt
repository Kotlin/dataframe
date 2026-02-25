package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.api.AddDataRow
import org.jetbrains.kotlinx.dataframe.api.AddExpression
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.PivotColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.PivotDsl
import org.jetbrains.kotlinx.dataframe.api.SortColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.SortDsl
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.api.createConverter
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.jetbrains.kotlinx.dataframe.impl.isNothing
import org.jetbrains.kotlinx.dataframe.impl.replaceGenericTypeParametersWithUpperbound
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.CREATE_COLUMN
import org.jetbrains.kotlinx.dataframe.util.GUESS_COLUMN_TYPE
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region create DataColumn

internal class AddDataRowImpl<T>(index: Int, owner: DataFrame<T>, private val container: List<*>) :
    DataRowImpl<T>(index, owner),
    AddDataRow<T> {

    override fun <C> AnyRow.newValue() = container[index] as C
}

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.newColumn(
    type: KType,
    name: String = "",
    infer: Infer = Infer.Nulls,
    expression: AddExpression<T, R>,
): DataColumn<R> {
    val df = this as? DataFrame<T> ?: dataFrameOf(columns()).cast()
    val (nullable, values) = computeValues(df, expression)
    return when (infer) {
        Infer.Nulls -> DataColumn.createByType(
            name = name,
            values = values,
            type = type.withNullability(nullable).replaceGenericTypeParametersWithUpperbound(),
            infer = Infer.None,
        )

        Infer.Type -> DataColumn.createByInference(
            name = name,
            values = values,
            nullable = nullable,
        )

        Infer.None -> DataColumn.createByType(
            name = name,
            values = values,
            type = type.replaceGenericTypeParametersWithUpperbound(),
            infer = Infer.None,
        )
    }
}

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.newColumnWithActualType(
    name: String,
    expression: AddExpression<T, R>,
): DataColumn<R> {
    val (_, values) = computeValues(this as DataFrame<T>, expression)
    return createColumnGuessingType(name, values)
}

internal fun <T, R> computeValues(df: DataFrame<T>, expression: AddExpression<T, R>): Pair<Boolean, List<R>> {
    var nullable = false
    val list = ArrayList<R>(df.nrow)
    df.indices().forEach {
        val row = AddDataRowImpl(it, df, list)
        val value = expression(row, row)
        if (value == null) nullable = true
        list.add(value)
    }
    return nullable to list
}

// endregion

// region create Columns

internal inline fun <C> createColumnSet(
    crossinline resolver: (context: ColumnResolutionContext) -> List<ColumnWithPath<C>>,
): ColumnSet<C> =
    object : ColumnSet<C> {
        override fun resolve(context: ColumnResolutionContext) = resolver(context)
    }

internal fun <C> createTransformableColumnSet(
    resolver: (context: ColumnResolutionContext) -> List<ColumnWithPath<C>>,
    transformResolve: (
        context: ColumnResolutionContext,
        transformer: ColumnsResolverTransformer,
    ) -> List<ColumnWithPath<C>>,
): TransformableColumnSet<C> =
    object : TransformableColumnSet<C> {
        override fun resolve(context: ColumnResolutionContext) = resolver(context)

        override fun transformResolve(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): List<ColumnWithPath<C>> = transformResolve(context, transformer)
    }

// region toColumnSet

// region DSL

internal inline fun <TD, T : DataFrame<TD>, C> Selector<T, ColumnsResolver<C>>.toColumnSet(
    crossinline createReceiver: (ColumnResolutionContext) -> T,
): ColumnSet<C> =
    createColumnSet {
        val receiver = createReceiver(it)
        val columnSet = this(receiver, receiver)
        columnSet.resolve(receiver, it.unresolvedColumnsPolicy)
    }

@JvmName("toColumnSetForPivot")
internal fun <T, C> PivotColumnsSelector<T, C>.toColumnSet(): ColumnSet<C> =
    toColumnSet {
        object : DataFrameReceiver<T>(it.df.cast(), it.unresolvedColumnsPolicy), PivotDsl<T> {}
    }

@JvmName("toColumnSetForSort")
internal fun <T, C> SortColumnsSelector<T, C>.toColumnSet(): ColumnSet<C> =
    toColumnSet {
        object : DataFrameReceiver<T>(it.df.cast(), it.unresolvedColumnsPolicy), SortDsl<T> {}
    }

internal fun <T, C> ColumnsSelector<T, C>.toColumnSet(): ColumnSet<C> =
    toColumnSet {
        object : DataFrameReceiver<T>(it.df.cast(), it.unresolvedColumnsPolicy), ColumnsSelectionDsl<T> {}
    }

// endregion

// endregion

// region toComparableColumns

internal fun Array<out String>.toComparableColumns() = toColumnsSetOf<Comparable<Any?>>()

internal fun String.toComparableColumn() = toColumnOf<Comparable<Any?>>()

// endregion

internal fun Array<out String>.toNumberColumns() = toColumnsSetOf<Number>()

// endregion

/**
 * Creates a new column by doing type inference on the given values and
 * some conversions to unify the values if necessary.
 *
 * @param values values to create a column from
 * @param suggestedType optional suggested type for values. Default is [TypeSuggestion.Infer].
 *   See [TypeSuggestion] for more information.
 * @param defaultValue optional default value for the column used when a [ValueColumn] is created.
 * @param nullable optional hint for the column nullability, used when a [ValueColumn] is created.
 * @param listifyValues if `true`, then values and nulls will be wrapped in a list if they appear among other lists.
 *   For example: `[1, null, listOf(1, 2, 3)]` will become `[[1], [], [1, 2, 3]]`.
 *   Note: this parameter is ignored if another [Collection] is present in the values.
 * @param allColsMakesColGroup if `true`, then, if all values are non-null same-sized columns,
 *   a column group will be created instead of a [DataColumn][DataColumn]`<`[AnyCol][AnyCol]`>`.
 * @param unifyNumbers if `true`, then all numbers encountered in [values] will be converted to the smallest possible
 *   number-type that can hold all the values lossless. Unsigned numbers are not supported. See [UnifyingNumbers].
 *   For example, if the values are `[1, 2f, 3.0]`, then all values will be converted to [Double].
 */
@PublishedApi
internal fun <T> createColumnGuessingType(
    values: Iterable<T>,
    suggestedType: TypeSuggestion = TypeSuggestion.Infer,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
    unifyNumbers: Boolean = false,
): DataColumn<T> =
    createColumnGuessingType(
        name = "",
        values = values,
        suggestedType = suggestedType,
        defaultValue = defaultValue,
        nullable = nullable,
        listifyValues = listifyValues,
        allColsMakesColGroup = allColsMakesColGroup,
        unifyNumbers = unifyNumbers,
    )

/**
 * @include [createColumnGuessingType]
 * @param name name for the column
 */
@PublishedApi
internal fun <T> createColumnGuessingType(
    name: String,
    values: Iterable<T>,
    suggestedType: TypeSuggestion = TypeSuggestion.Infer,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
    unifyNumbers: Boolean = false,
): DataColumn<T> {
    val type = when (suggestedType) {
        is TypeSuggestion.Infer, is TypeSuggestion.InferWithUpperbound ->
            guessValueType(
                values = values.asSequence(),
                upperBound = (suggestedType as? TypeSuggestion.InferWithUpperbound)?.upperbound,
                listifyValues = listifyValues,
                allColsMakesRow = allColsMakesColGroup,
                unifyNumbers = unifyNumbers,
            )

        is TypeSuggestion.Use -> suggestedType.type
    }

    // only needs to be used when unifyNumbers == true
    @Suppress("UNCHECKED_CAST")
    fun getSafeNumberConverter(targetType: KType): (Any?) -> Any? {
        val converter = createConverter(
            from = typeOf<Number>(),
            to = targetType,
        ) as (Number) -> Number?

        return { value -> if (value != null && value is Number) converter(value) else value }
    }

    return when (type.classifier?.starProjectedType) {
        // guessValueType can only return DataRow if all values are `AnyRow?`
        // or allColsMakesColGroup == true, and all values are `AnyCol`
        typeOf<AnyRow>() ->
            if (allColsMakesColGroup && values.firstOrNull() is AnyCol) {
                val df = dataFrameOf(values as Iterable<AnyCol>)
                DataColumn.createColumnGroup(name, df)
            } else {
                val df = values.map {
                    (it as AnyRow?)?.toDataFrame() ?: DataFrame.empty(1)
                }.concat()
                DataColumn.createColumnGroup(name, df)
            }.asDataColumn().cast()

        typeOf<AnyFrame>() -> {
            val frames = values.map {
                when (it) {
                    null -> DataFrame.empty()
                    is AnyFrame -> it
                    is AnyRow -> it.toDataFrame()
                    is List<*> -> (it as List<AnyRow>).toDataFrame()
                    else -> throw IllegalStateException()
                }
            }
            DataColumn.createFrameColumn(name, frames).asDataColumn().cast()
        }

        typeOf<List<*>>() -> {
            val nullable = type.isMarkedNullable
            var isListOfRows: Boolean? = null
            val subType = type.arguments.first().type!! // List<T> -> T

            val needsNumberConversion = unifyNumbers &&
                subType.isSubtypeOf(typeOf<Number?>()) &&
                !subType.isNothing
            val numberConverter: (Any?) -> Any? by lazy { getSafeNumberConverter(subType) }

            val lists = values.map { value ->
                when (value) {
                    null -> if (nullable) null else emptyList()

                    is List<*> -> {
                        if (isListOfRows != false && value.isNotEmpty()) isListOfRows = value.all { it is AnyRow }

                        if (needsNumberConversion) value.map(numberConverter) else value
                    }

                    else -> { // if !detectType and suggestedType is a list, we wrap the values in lists
                        if (isListOfRows != false) isListOfRows = value is AnyRow

                        listOf(
                            if (needsNumberConversion) numberConverter(value) else value,
                        )
                    }
                }
            }
            if (isListOfRows == true) {
                val frames = lists.map {
                    if (it == null) {
                        DataFrame.empty()
                    } else {
                        (it as List<AnyRow>).concat()
                    }
                }
                DataColumn.createFrameColumn(name, frames).cast()
            } else {
                DataColumn.createValueColumn(
                    name = name,
                    values = lists,
                    type = type,
                    defaultValue = defaultValue,
                ).cast()
            }
        }

        else -> {
            val needsNumberConversion = unifyNumbers &&
                type.isSubtypeOf(typeOf<Number?>()) &&
                !type.isNothing
            val numberConverter by lazy { getSafeNumberConverter(type) }

            DataColumn.createValueColumn(
                name = name,
                values = if (needsNumberConversion) values.map(numberConverter) as List<T> else values.asList(),
                type = if (nullable != null) type.withNullability(nullable) else type,
                infer = when {
                    // even though an exact type is suggested,
                    // nullable is not given, so we still infer nullability
                    nullable == null && suggestedType is TypeSuggestion.Use -> Infer.Nulls

                    // nullability already handled; inferred by guessValueType or explicitly given
                    else -> Infer.None
                },
                defaultValue = defaultValue,
            )
        }
    }
}

// region deprecated

/** Just for binary compatibility, since it's @PublishedApi. */
@Deprecated(CREATE_COLUMN, level = DeprecationLevel.HIDDEN)
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> createColumn(values: Iterable<T>, suggestedType: KType, guessType: Boolean = false): DataColumn<T> =
    createColumnGuessingType(
        values = values,
        suggestedType = TypeSuggestion.create(suggestedType, guessType),
        allColsMakesColGroup = true,
        unifyNumbers = false,
    )

/** Just for binary compatibility, since it's @PublishedApi. */
@Deprecated(GUESS_COLUMN_TYPE, level = DeprecationLevel.HIDDEN)
@PublishedApi
internal fun <T> guessColumnType(
    name: String,
    values: List<T>,
    suggestedType: KType? = null,
    suggestedTypeIsUpperBound: Boolean = false,
    defaultValue: T? = null,
    nullable: Boolean? = null,
): DataColumn<T> =
    createColumnGuessingType(
        name = name,
        values = values,
        suggestedType = TypeSuggestion.create(suggestedType, suggestedTypeIsUpperBound),
        defaultValue = defaultValue,
        nullable = nullable,
        listifyValues = false,
        allColsMakesColGroup = false,
    )

/** Just for binary compatibility, since it's @PublishedApi. */
@Deprecated(GUESS_COLUMN_TYPE, level = DeprecationLevel.HIDDEN)
@PublishedApi
internal fun <T> createColumnGuessingType(
    values: Iterable<T>,
    suggestedType: TypeSuggestion = TypeSuggestion.Infer,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
): DataColumn<T> =
    createColumnGuessingType(
        values = values,
        suggestedType = suggestedType,
        defaultValue = defaultValue,
        nullable = nullable,
        listifyValues = listifyValues,
        allColsMakesColGroup = allColsMakesColGroup,
        unifyNumbers = false,
    )

/** Just for binary compatibility, since it's @PublishedApi. */
@Deprecated(GUESS_COLUMN_TYPE, level = DeprecationLevel.HIDDEN)
@PublishedApi
internal fun <T> createColumnGuessingType(
    name: String,
    values: Iterable<T>,
    suggestedType: TypeSuggestion = TypeSuggestion.Infer,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
): DataColumn<T> =
    createColumnGuessingType(
        name = name,
        values = values,
        suggestedType = suggestedType,
        defaultValue = defaultValue,
        nullable = nullable,
        listifyValues = listifyValues,
        allColsMakesColGroup = allColsMakesColGroup,
        unifyNumbers = false,
    )

// endregion

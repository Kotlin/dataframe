package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.api.chunkedImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.FrameColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ValueColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnKind
import org.jetbrains.kotlinx.dataframe.impl.getValuesType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.util.CHUNKED_IMPL_IMPORT
import org.jetbrains.kotlinx.dataframe.util.CREATE
import org.jetbrains.kotlinx.dataframe.util.CREATE_BY_INFERENCE_IMPORT
import org.jetbrains.kotlinx.dataframe.util.CREATE_BY_TYPE_IMPORT
import org.jetbrains.kotlinx.dataframe.util.CREATE_FRAME_COLUMN
import org.jetbrains.kotlinx.dataframe.util.CREATE_FRAME_COLUMN_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CREATE_INLINE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CREATE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CREATE_WITH_TYPE_INFERENCE
import org.jetbrains.kotlinx.dataframe.util.CREATE_WITH_TYPE_INFERENCE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.TYPE_SUGGESTION_IMPORT
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Column with [<code>name</code>][name] and [<code>values</code>][values] of specific [<code>type</code>][type].
 *
 * Base interface for [<code>ValueColumn</code>][ValueColumn] and [<code>FrameColumn</code>][FrameColumn], but not for [<code>ColumnGroup</code>][ColumnGroup]. However, implementations for all three [<code>column kinds</code>][ColumnKind] derive from DataColumn and can cast to it safely.
 * Column operations that have signature clash with [<code>DataFrame</code>][DataFrame] API ([<code>filter</code>][filter], [<code>take</code>][take], [<code>map</code>][map] etc.) are defined for [<code>DataColumn</code>][DataColumn] and not for [<code>BaseColumn</code>][BaseColumn].
 *
 * @param T type of values in the column.
 */
public interface DataColumn<out T> : BaseColumn<T> {

    public companion object {

        /**
         * Creates [<code>ValueColumn</code>][ValueColumn] using given [<code>name</code>][name], [<code>values</code>][values] and [<code>type</code>][type].
         *
         * Be careful; values are NOT checked to adhere to [<code>type</code>][type] for efficiency,
         * unless you specify [<code>infer</code>][infer].
         *
         * @param name name of the column
         * @param values list of column values
         * @param type type of the column
         * @param infer column type inference mode
         */
        public fun <T> createValueColumn(
            name: String,
            values: List<T>,
            type: KType,
            infer: Infer = Infer.None,
            defaultValue: T? = null,
        ): ValueColumn<T> =
            ValueColumnImpl(
                values = values,
                name = name,
                type = getValuesType(values, type, infer),
                defaultValue = defaultValue,
            )

        /**
         * Creates [<code>ValueColumn</code>][ValueColumn] using given [<code>name</code>][name], [<code>values</code>][values] and reified column [<code>type</code>][type].
         *
         * The column [<code>type</code>][type] will be defined at compile-time using [<code>T</code>][T] argument.
         * Be careful with casting; values are NOT checked to adhere to `reified` type [<code>T</code>][T] for efficiency,
         * unless you specify [<code>infer</code>][infer].
         *
         * @param T type of the column
         * @param name name of the column
         * @param values list of column values
         * @param infer column type inference mode
         */
        public inline fun <reified T> createValueColumn(
            name: String,
            values: List<T>,
            infer: Infer = Infer.None,
        ): ValueColumn<T> =
            createValueColumn(
                name = name,
                values = values,
                type = typeOf<T>(),
                infer = infer,
            )

        /**
         * Creates [<code>ColumnGroup</code>][ColumnGroup] using the given [<code>name</code>][name] and [<code>df</code>][df] representing the group of columns.
         *
         * @param name name of the column group
         * @param df the collection of columns representing the column group
         */
        public fun <T> createColumnGroup(name: String, df: DataFrame<T>): ColumnGroup<T> = ColumnGroupImpl(name, df)

        /**
         * Creates [<code>FrameColumn</code>][FrameColumn] using the given [<code>name</code>][name] and list of dataframes [<code>groups</code>][groups].
         *
         * [<code>groups</code>][groups] must be a non-null list of [<code>DataFrames</code>][DataFrame], as [<code>FrameColumn</code>][FrameColumn] does
         * not allow `null` values.
         * This is NOT checked at runtime for efficiency, nor is the validity of given [<code>schema</code>][schema].
         *
         * @param name name of the frame column
         * @param groups the dataframes to be put in the column
         * @param schema an optional (lazily calculated) [<code>DataFrameSchema</code>][DataFrameSchema] representing
         *   the intersecting schema of [<code>groups</code>][groups]
         */
        public fun <T> createFrameColumn(
            name: String,
            groups: List<DataFrame<T>>,
            schema: Lazy<DataFrameSchema>? = null,
        ): FrameColumn<T> = FrameColumnImpl(name, groups, schema)

        /**
         * Creates either a [<code>FrameColumn</code>][FrameColumn], [<code>ColumnGroup</code>][ColumnGroup], or [<code>ValueColumn</code>][ValueColumn] by analyzing each value in
         * [<code>values</code>][values].
         *
         * This is safer but slower than the other functions.
         *
         * Some conversions are done automatically to attempt to unify the values.
         *
         * For instance, when there are other [<code>DataFrames</code>][DataFrame] present in [<code>values</code>][values], we'll convert:
         * - `null` -> [<code>DataFrame.empty</code>][DataFrame.empty]`()`
         * - [<code>DataRow</code>][DataRow] -> single-row [<code>DataFrame</code>][DataFrame]
         * - [<code>List</code>][List]`<`[<code>DataRow</code>][DataRow]`<*>>` -> multi-row [<code>DataFrame</code>][DataFrame]
         *
         * to be able to create a [<code>FrameColumn</code>][FrameColumn].
         * There are more conversions for other types as well.
         *
         * @param name name of the column
         * @param values the values to represent each row in the column
         * @param suggestedType optional suggested type for values. Default is [<code>TypeSuggestion.Infer</code>][TypeSuggestion.Infer].
         *   See [<code>TypeSuggestion</code>][TypeSuggestion] for more information.
         * @param nullable optionally you can specify whether [<code>values</code>][values] contains nulls, if `null` it is inferred.
         */
        public fun <T> createByInference(
            name: String,
            values: List<T>,
            suggestedType: TypeSuggestion = TypeSuggestion.Infer,
            nullable: Boolean? = null,
        ): DataColumn<T> =
            createColumnGuessingType(
                name = name,
                values = values,
                suggestedType = suggestedType,
                nullable = nullable,
            )

        /**
         * Calls [<code>createColumnGroup</code>][createColumnGroup], [<code>createFrameColumn</code>][createFrameColumn], or [<code>createValueColumn</code>][createValueColumn] based on
         * [<code>type</code>][type].
         *
         * This may be unsafe but is more efficient than [<code>createByInference</code>][createByInference].
         *
         * Be careful; Values in [<code>values</code>][values] are NOT checked to adhere to the given [<code>type</code>][type], nor
         * do we check whether there are unexpected nulls among the values.
         *
         * It's recommended to use [<code>createValueColumn</code>][createValueColumn], [<code>createColumnGroup</code>][createColumnGroup], and [<code>createFrameColumn</code>][createFrameColumn] instead.
         *
         * @param name the name of the column
         * @param values the values to represent each row in the column
         * @param type the (unchecked) common type of [<code>values</code>][values]
         * @param infer in case a [<code>ValueColumn</code>][ValueColumn] is created, this controls how/whether types need to be inferred
         */
        public fun <T> createByType(
            name: String,
            values: List<T>,
            type: KType,
            infer: Infer = Infer.None,
        ): DataColumn<T> =
            when (type.toColumnKind()) { // AnyFrame -> Frame, AnyRow? -> Group, else -> Value
                ColumnKind.Value -> createValueColumn(name, values, type, infer)

                ColumnKind.Group -> createColumnGroup(name, (values as List<AnyRow?>).concat()).asDataColumn().cast()

                ColumnKind.Frame -> createFrameColumn(name, values as List<AnyFrame>).asDataColumn().cast()
            }

        /**
         * Calls [<code>createColumnGroup</code>][createColumnGroup], [<code>createFrameColumn</code>][createFrameColumn], or [<code>createValueColumn</code>][createValueColumn] based on
         * type [<code>T</code>][T].
         *
         * This is generally safe, as [<code>T</code>][T] can be inferred by the compiler,
         * and more efficient than [<code>createByInference</code>][createByInference].
         *
         * Be careful when casting occurs; Values in [<code>values</code>][values] are NOT checked to adhere to the given/inferred type [<code>T</code>][T],
         * nor do we check whether there are unexpected nulls among the values.
         *
         * It's recommended to use [<code>createValueColumn</code>][createValueColumn], [<code>createColumnGroup</code>][createColumnGroup], and [<code>createFrameColumn</code>][createFrameColumn] instead.
         *
         * @param T the (unchecked) common type of [<code>values</code>][values]
         * @param name the name of the column
         * @param values the values to represent each row in the column
         * @param infer in case a [<code>ValueColumn</code>][ValueColumn] is created, this controls how/whether types need to be inferred
         */
        public inline fun <reified T> createByType(
            name: String,
            values: List<T>,
            infer: Infer = Infer.None,
        ): DataColumn<T> = createByType(name, values, typeOf<T>(), infer)

        /**
         * Creates an empty [<code>DataColumn</code>][DataColumn] with given [<code>name</code>][name] of type [<code>Nothing</code>][Nothing].
         * If you want to specify another type, use [<code>`emptyOf<T>()`</code>][emptyOf].
         *
         * @see emptyOf
         */
        public fun empty(name: String = ""): DataColumn<Nothing> =
            createValueColumn(name, emptyList<Unit>(), nothingType).cast()

        /** Creates an empty [<code>DataColumn</code>][DataColumn] of type [<code>T</code>][T] with given [<code>name</code>][name]. */
        public inline fun <reified T> emptyOf(name: String = ""): DataColumn<T> =
            createValueColumn(name, emptyList<T>(), typeOf<T>()).cast()

        // region deprecated

        @Deprecated(
            message = CREATE_FRAME_COLUMN,
            replaceWith = ReplaceWith(CREATE_FRAME_COLUMN_REPLACE, CHUNKED_IMPL_IMPORT),
            level = DeprecationLevel.ERROR,
        )
        public fun <T> createFrameColumn(name: String, df: DataFrame<T>, startIndices: Iterable<Int>): FrameColumn<T> =
            df.chunkedImpl(startIndices = startIndices, name = name)

        @Deprecated(
            message = CREATE_WITH_TYPE_INFERENCE,
            replaceWith = ReplaceWith(
                CREATE_WITH_TYPE_INFERENCE_REPLACE,
                CREATE_BY_INFERENCE_IMPORT,
                TYPE_SUGGESTION_IMPORT,
            ),
            level = DeprecationLevel.ERROR,
        )
        public fun <T> createWithTypeInference(
            name: String,
            values: List<T>,
            nullable: Boolean? = null,
        ): DataColumn<T> =
            createByInference(
                name = name,
                values = values,
                suggestedType = TypeSuggestion.Infer,
                nullable = nullable,
            )

        @Deprecated(
            message = CREATE,
            replaceWith = ReplaceWith(CREATE_REPLACE, CREATE_BY_TYPE_IMPORT),
            level = DeprecationLevel.ERROR,
        )
        public fun <T> create(
            name: String,
            values: List<T>,
            type: KType,
            infer: Infer = Infer.None,
        ): DataColumn<T> =
            createByType(
                name = name,
                values = values,
                type = type,
                infer = infer,
            )

        @Deprecated(
            message = CREATE,
            replaceWith = ReplaceWith(CREATE_INLINE_REPLACE, CREATE_BY_TYPE_IMPORT),
            level = DeprecationLevel.ERROR,
        )
        public inline fun <reified T> create(name: String, values: List<T>, infer: Infer = Infer.None): DataColumn<T> =
            createByType(name = name, values = values, type = typeOf<T>(), infer = infer)

        // endregion
    }

    public fun hasNulls(): Boolean = type().isMarkedNullable

    override fun distinct(): DataColumn<T>

    override fun get(indices: Iterable<Int>): DataColumn<T>

    override fun rename(newName: String): DataColumn<T>

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? = this.addPath()

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): DataColumn<T> =
        super.getValue(thisRef, property) as DataColumn<T>

    public operator fun iterator(): Iterator<T> = values().iterator()

    public override operator fun get(range: IntRange): DataColumn<T>
}

public val AnyCol.name: String get() = name()
public val AnyCol.path: ColumnPath get() = path()

public val <T> DataColumn<T>.values: Iterable<T> get() = values()
public val AnyCol.hasNulls: Boolean get() = hasNulls()
public val AnyCol.size: Int get() = size()
public val AnyCol.indices: IntRange get() = indices()

public val AnyCol.type: KType get() = type()
public val AnyCol.kind: ColumnKind get() = kind()
public val AnyCol.typeClass: KClass<*>
    get() = type.classifier as? KClass<*>
        ?: error("Cannot cast ${type.classifier?.javaClass} to a ${KClass::class}. Column $name: $type")

public fun AnyBaseCol.indices(): IntRange = 0 until size()

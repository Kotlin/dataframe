package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.impl.DataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.createComputedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.forceResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.unbox
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_GROUP
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_GROUP_REPLACE
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.reflect.KProperty
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region create ColumnAccessor

// region column

public fun <T> column(): ColumnDelegate<T> = ColumnDelegate()

public fun <T> column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(name)

public fun <T> column(path: ColumnPath): ColumnAccessor<T> = ColumnAccessorImpl(path)

public fun <T> column(property: KProperty<T>): ColumnAccessor<T> = ColumnAccessorImpl(property.name)

public fun <T> ColumnGroupReference.column(): ColumnDelegate<T> = ColumnDelegate(this)

public fun <T> ColumnGroupReference.column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(path() + name)

public fun <T> ColumnGroupReference.column(path: ColumnPath): ColumnAccessor<T> = ColumnAccessorImpl(this.path() + path)

public fun <T> ColumnGroupReference.column(property: KProperty<T>): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + property.name)

public inline fun <reified T> column(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<Any?, T>,
): ColumnReference<T> = createComputedColumnReference(name, typeOf<T>(), infer, expression)

@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
public inline fun <T, reified C> column(
    df: DataFrame<T>,
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<T, C>,
): ColumnReference<C> = createComputedColumnReference(name, typeOf<C>(), infer, expression as RowExpression<Any?, C>)

// endregion

// region valueColumn

public fun valueColumn(): ColumnDelegate<Any?> = column()

@JvmName("valueColumnTyped")
public fun <T> valueColumn(): ColumnDelegate<T> = column()

public fun valueColumn(name: String): ColumnAccessor<Any?> = column(name)

@JvmName("valueColumnTyped")
public fun <T> valueColumn(name: String): ColumnAccessor<T> = column(name)

public fun valueColumn(path: ColumnPath): ColumnAccessor<Any?> = column(path)

@JvmName("valueColumnTyped")
public fun <T> valueColumn(path: ColumnPath): ColumnAccessor<T> = column(path)

public fun <T> valueColumn(property: KProperty<T>): ColumnAccessor<T> = column(property.name)

public fun ColumnGroupReference.valueColumn(): ColumnDelegate<Any?> = ColumnDelegate(this)

@JvmName("valueColumnTyped")
public fun <T> ColumnGroupReference.valueColumn(): ColumnDelegate<T> = ColumnDelegate(this)

public fun ColumnGroupReference.valueColumn(name: String): ColumnAccessor<Any?> = ColumnAccessorImpl(path() + name)

@JvmName("valueColumnTyped")
public fun <T> ColumnGroupReference.valueColumn(name: String): ColumnAccessor<T> =
    ColumnAccessorImpl(path() + name)

public fun ColumnGroupReference.valueColumn(path: ColumnPath): ColumnAccessor<Any?> =
    ColumnAccessorImpl(this.path() + path)

@JvmName("valueColumnTyped")
public fun <T> ColumnGroupReference.valueColumn(path: ColumnPath): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + path)

public fun <T> ColumnGroupReference.valueColumn(property: KProperty<T>): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + property.name)

// endregion

// region columnGroup

public fun columnGroup(): ColumnDelegate<AnyRow> = column()

@JvmName("columnGroupTyped")
public fun <T> columnGroup(): ColumnDelegate<DataRow<T>> = column()

public fun columnGroup(name: String): ColumnAccessor<AnyRow> = column(name)

@JvmName("columnGroupTyped")
public fun <T> columnGroup(name: String): ColumnAccessor<DataRow<T>> = column(name)

public fun columnGroup(path: ColumnPath): ColumnAccessor<AnyRow> = column(path)

@JvmName("columnGroupTyped")
public fun <T> columnGroup(path: ColumnPath): ColumnAccessor<DataRow<T>> = column(path)

@JvmName("columnGroupDataRowKProperty")
public fun <T> columnGroup(property: KProperty<DataRow<T>>): ColumnAccessor<DataRow<T>> = column(property)

public fun <T> columnGroup(property: KProperty<T>): ColumnAccessor<DataRow<T>> = column(property.name)

public fun ColumnGroupReference.columnGroup(): ColumnDelegate<AnyRow> = ColumnDelegate(this)

@JvmName("columnGroupTyped")
public fun <T> ColumnGroupReference.columnGroup(): ColumnDelegate<DataRow<T>> = ColumnDelegate(this)

public fun ColumnGroupReference.columnGroup(name: String): ColumnAccessor<AnyRow> = ColumnAccessorImpl(path() + name)

@JvmName("columnGroupTyped")
public fun <T> ColumnGroupReference.columnGroup(name: String): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(path() + name)

public fun ColumnGroupReference.columnGroup(path: ColumnPath): ColumnAccessor<AnyRow> =
    ColumnAccessorImpl(this.path() + path)

@JvmName("columnGroupTyped")
public fun <T> ColumnGroupReference.columnGroup(path: ColumnPath): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(this.path() + path)

@JvmName("columnGroupDataRowKProperty")
public fun <T> ColumnGroupReference.columnGroup(property: KProperty<DataRow<T>>): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(this.path() + property.name)

public fun <T> ColumnGroupReference.columnGroup(property: KProperty<T>): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(this.path() + property.name)

// endregion

// region frameColumn

public fun frameColumn(): ColumnDelegate<AnyFrame> = column()

@JvmName("frameColumnTyped")
public fun <T> frameColumn(): ColumnDelegate<DataFrame<T>> = column()

public fun frameColumn(name: String): ColumnAccessor<AnyFrame> = column(name)

@JvmName("frameColumnTyped")
public fun <T> frameColumn(name: String): ColumnAccessor<DataFrame<T>> = column(name)

public fun frameColumn(path: ColumnPath): ColumnAccessor<AnyFrame> = column(path)

@JvmName("frameColumnTyped")
public fun <T> frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<T>> = column(path)

@JvmName("frameColumnDataFrameKProperty")
public fun <T> frameColumn(property: KProperty<DataFrame<T>>): ColumnAccessor<DataFrame<T>> = column(property)

public fun <T> frameColumn(property: KProperty<List<T>>): ColumnAccessor<DataFrame<T>> = column(property.name)

public fun ColumnGroupReference.frameColumn(): ColumnDelegate<AnyFrame> = ColumnDelegate(this)

@JvmName("frameColumnTyped")
public fun <T> ColumnGroupReference.frameColumn(): ColumnDelegate<DataFrame<T>> = ColumnDelegate(this)

public fun ColumnGroupReference.frameColumn(name: String): ColumnAccessor<AnyFrame> = ColumnAccessorImpl(path() + name)

@JvmName("frameColumnTyped")
public fun <T> ColumnGroupReference.frameColumn(name: String): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(path() + name)

public fun ColumnGroupReference.frameColumn(path: ColumnPath): ColumnAccessor<AnyFrame> =
    ColumnAccessorImpl(this.path() + path)

@JvmName("frameColumnTyped")
public fun <T> ColumnGroupReference.frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(this.path() + path)

@JvmName("frameColumnDataFrameKProperty")
public fun <T> ColumnGroupReference.frameColumn(property: KProperty<DataFrame<T>>): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(this.path() + property.name)

public fun <T> ColumnGroupReference.frameColumn(property: KProperty<List<T>>): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(this.path() + property.name)

// endregion

// region asColumnGroup

/**
 * ## SingleColumn As ColumnGroup
 * Casts [this][this\] [SingleColumn][SingleColumn]`<`[C][C\]`>` to a [SingleColumn][SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 * This is especially useful when you want to use `ColumnGroup` functions in the [ColumnsSelectionDsl] but your column
 * type is not recognized as a `ColumnGroup`.
 * If you're not sure whether a column is recognized or not, you can always call [asColumnGroup][SingleColumn.asColumnGroup]
 * and it will return the same type if it is already a `ColumnGroup`.
 *
 * For example:
 *
 * `df.`[select][DataFrame.select]` { it`[`[`][ColumnsContainer.get]`"myColumn"`[`]`][ColumnsContainer.get]`.`[asColumnGroup][SingleColumn.asColumnGroup]`().`[first][ColumnsSelectionDsl.firstChild]`() }`
 *
 * @receiver The [SingleColumn] to cast to a [SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 * @param [C\] The type of the (group) column.
 * @return A [SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 */
private interface SingleColumnAsColumnGroupDocs

/** ## SingleColumn As ColumnGroup
 * Casts [this][this] [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[C][C]`>` to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * This is especially useful when you want to use `ColumnGroup` functions in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] but your column
 * type is not recognized as a `ColumnGroup`.
 * If you're not sure whether a column is recognized or not, you can always call [asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]
 * and it will return the same type if it is already a `ColumnGroup`.
 *
 * For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`"myColumn"`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`.`[asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]`().`[first][ColumnsSelectionDsl.firstChild]`() }`
 *
 * @receiver The [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] to cast to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * @param [C] The type of the (group) column.
 * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`. */
@Suppress("UNCHECKED_CAST")
public fun <C> SingleColumn<C>.asColumnGroup(): SingleColumn<DataRow<C>> = this as SingleColumn<DataRow<C>>

/** ## SingleColumn As ColumnGroup
 * Casts [this][this] [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[C][C]`>` to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * This is especially useful when you want to use `ColumnGroup` functions in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] but your column
 * type is not recognized as a `ColumnGroup`.
 * If you're not sure whether a column is recognized or not, you can always call [asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]
 * and it will return the same type if it is already a `ColumnGroup`.
 *
 * For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`"myColumn"`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`.`[asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]`().`[first][ColumnsSelectionDsl.firstChild]`() }`
 *
 * @receiver The [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] to cast to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * @param [C] The type of the (group) column.
 * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`. */
@JvmName("asColumnGroupDataRow")
public fun <C> SingleColumn<DataRow<C>>.asColumnGroup(): SingleColumn<DataRow<C>> = this

/**
 * ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][ColumnAccessor]`<`[DataRow][DataRow]`<`[C][C\]`>>` from [this][this\].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][columnGroup]`(`[this][this\]`)`
 *
 * @return A [ColumnAccessor]`<`[DataRow][DataRow]`>`.
 */
private interface AsColumnGroupDocs

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
public fun <C> KProperty<C>.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
@JvmName("asColumnGroupDataRowKProperty")
public fun <C> KProperty<DataRow<C>>.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
@JvmName("asColumnGroupTyped")
public fun <C> String.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
public fun String.asColumnGroup(): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
@JvmName("asColumnGroupTyped")
public fun <C> ColumnPath.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
public fun ColumnPath.asColumnGroup(): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(this)

// endregion

public class ColumnDelegate<T>(private val parent: ColumnGroupReference? = null) {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = named(property.columnName)

    public infix fun named(name: String): ColumnAccessor<T> =
        parent?.let { ColumnAccessorImpl(it.path() + name) } ?: ColumnAccessorImpl(name)
}

// endregion

// region create DataColumn

public inline fun <reified T> columnOf(vararg values: T): DataColumn<T> =
    createColumn(values.asIterable(), typeOf<T>(), true).forceResolve()

public fun columnOf(vararg values: AnyBaseCol): DataColumn<AnyRow> = columnOf(values.asIterable()).forceResolve()

public fun <T> columnOf(vararg frames: DataFrame<T>): FrameColumn<T> = columnOf(frames.asIterable()).forceResolve()

public fun columnOf(columns: Iterable<AnyBaseCol>): DataColumn<AnyRow> =
    DataColumn.createColumnGroup(
        name = "",
        df = dataFrameOf(columns)
    )
        .asDataColumn()
        .forceResolve()

public fun <T> columnOf(frames: Iterable<DataFrame<T>>): FrameColumn<T> = DataColumn.createFrameColumn(
    "",
    frames.toList()
).forceResolve()

public inline fun <reified T> column(values: Iterable<T>): DataColumn<T> =
    createColumn(values, typeOf<T>(), false).forceResolve()

// endregion

// region create DataFrame

/**
 * Creates new [DataFrame] with given [columns]
 *
 * All named columns must have unique names. For columns with empty names unique column names are generated: "untitled", "untitiled1", "untitled2" etc.
 *
 * All columns must have equal sizes.
 *
 * @throws [DuplicateColumnNamesException] if column names are not unique
 * @throws [UnequalColumnSizesException] if column size are not equal
 * @param columns columns for [DataFrame]
 */
public fun dataFrameOf(columns: Iterable<AnyBaseCol>): AnyFrame {
    val cols = columns.map { it.unbox() }
    val nrow = if (cols.isEmpty()) 0 else cols[0].size
    return DataFrameImpl<Unit>(cols, nrow)
}

public fun dataFrameOf(vararg header: ColumnReference<*>): DataFrameBuilder = DataFrameBuilder(header.map { it.name() })

public fun dataFrameOf(vararg columns: AnyBaseCol): AnyFrame = dataFrameOf(columns.asIterable())

public fun dataFrameOf(vararg header: String): DataFrameBuilder = dataFrameOf(header.toList())

public inline fun <reified C> dataFrameOf(vararg header: String, fill: (String) -> Iterable<C>): AnyFrame =
    dataFrameOf(header.asIterable(), fill)

public fun dataFrameOf(header: Iterable<String>): DataFrameBuilder = DataFrameBuilder(header.asList())

public fun dataFrameOf(vararg columns: Pair<String, List<Any?>>): AnyFrame =
    columns.map { it.second.toColumn(it.first, Infer.Type) }.toDataFrame()

public fun dataFrameOf(header: Iterable<String>, values: Iterable<Any?>): AnyFrame =
    dataFrameOf(header).withValues(values)

public inline fun <T, reified C> dataFrameOf(header: Iterable<T>, fill: (T) -> Iterable<C>): AnyFrame =
    header.map { value ->
        fill(value).asList().let {
            DataColumn.create(
                value.toString(),
                it
            )
        }
    }.toDataFrame()

public fun dataFrameOf(header: CharProgression): DataFrameBuilder = dataFrameOf(header.map { it.toString() })

public class DataFrameBuilder(private val header: List<String>) {

    public operator fun invoke(vararg columns: AnyCol): AnyFrame = invoke(columns.asIterable())

    public operator fun invoke(columns: Iterable<AnyCol>): AnyFrame {
        val cols = columns.asList()
        require(cols.size == header.size) { "Number of columns differs from number of column names" }
        return cols.mapIndexed { i, col ->
            col.rename(header[i])
        }.toDataFrame()
    }

    public operator fun invoke(vararg values: Any?): AnyFrame = withValues(values.asIterable())

    @JvmName("invoke1")
    internal fun withValues(values: Iterable<Any?>): AnyFrame {
        val list = values.asList()

        val ncol = header.size

        require(header.isNotEmpty() && list.size.rem(ncol) == 0) {
            "Number of values ${list.size} is not divisible by number of columns $ncol"
        }

        val nrow = list.size / ncol

        return (0 until ncol).map { col ->
            val colValues = (0 until nrow).map { row ->
                list[row * ncol + col]
            }
            DataColumn.createWithTypeInference(header[col], colValues)
        }.toDataFrame()
    }

    public operator fun invoke(args: Sequence<Any?>): AnyFrame = invoke(*args.toList().toTypedArray())

    public fun withColumns(columnBuilder: (String) -> AnyCol): AnyFrame = header.map(columnBuilder).toDataFrame()

    public inline operator fun <reified T> invoke(crossinline valuesBuilder: (String) -> Iterable<T>): AnyFrame =
        withColumns { name ->
            valuesBuilder(name).let {
                DataColumn.create(
                    name,
                    it.asList()
                )
            }
        }

    public inline fun <reified C> fill(nrow: Int, value: C): AnyFrame = withColumns { name ->
        DataColumn.createValueColumn(
            name,
            List(nrow) { value },
            typeOf<C>().withNullability(value == null)
        )
    }

    public inline fun <reified C> nulls(nrow: Int): AnyFrame = fill<C?>(nrow, null)

    public inline fun <reified C> fillIndexed(nrow: Int, crossinline init: (Int, String) -> C): AnyFrame =
        withColumns { name ->
            DataColumn.create(
                name,
                List(nrow) { init(it, name) }
            )
        }

    public inline fun <reified C> fill(nrow: Int, crossinline init: (Int) -> C): AnyFrame = withColumns { name ->
        DataColumn.create(
            name,
            List(nrow, init)
        )
    }

    private inline fun <reified C> fillNotNull(nrow: Int, crossinline init: (Int) -> C) = withColumns { name ->
        DataColumn.createValueColumn(
            name,
            List(nrow, init),
            typeOf<C>()
        )
    }

    public fun randomInt(nrow: Int): AnyFrame = fillNotNull(nrow) { Random.nextInt() }

    public fun randomInt(nrow: Int, range: IntRange): AnyFrame = fillNotNull(nrow) { Random.nextInt(range) }

    public fun randomDouble(nrow: Int): AnyFrame = fillNotNull(nrow) { Random.nextDouble() }

    public fun randomDouble(nrow: Int, range: ClosedRange<Double>): AnyFrame =
        fillNotNull(nrow) { Random.nextDouble(range.start, range.endInclusive) }

    public fun randomFloat(nrow: Int): AnyFrame = fillNotNull(nrow) { Random.nextFloat() }

    public fun randomLong(nrow: Int): AnyFrame = fillNotNull(nrow) { Random.nextLong() }

    public fun randomLong(nrow: Int, range: ClosedRange<Long>): AnyFrame =
        fillNotNull(nrow) { Random.nextLong(range.start, range.endInclusive) }

    public fun randomBoolean(nrow: Int): AnyFrame = fillNotNull(nrow) { Random.nextBoolean() }
}

/**
 * Returns [DataFrame] with no rows and no columns.
 *
 * To create [DataFrame] with empty columns or empty rows see [DataFrame.empty]
 *
 * @param T schema marker for [DataFrame]
 */
public fun <T> emptyDataFrame(): DataFrame<T> = DataFrame.empty().cast()

// endregion

// region create ColumnPath

public fun pathOf(vararg columnNames: String): ColumnPath = ColumnPath(columnNames.asList())

// endregion

// section ColumnsSelectionDsl

public interface ConstructorsColumnsSelectionDsl {

    // region valueCol

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[valueCol][valueCol]`({@includeArg [CommonValueColDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCol][ColumnReference.valueCol]`<SomeType>({@includeArg [CommonValueColDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup\]
     * @see [col\]
     * @see [frameCol\]
     */
    private interface CommonValueColDocs {

        /** Example argument */
        interface Arg
    }

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the value column.
     */
    public fun <C> valueCol(property: KProperty<C>): ColumnAccessor<C> = valueColumn(property)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun ColumnGroupReference.valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    public fun <C> ColumnGroupReference.valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun ColumnGroupReference.valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the value column.
     * @param [C] The type of the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    public fun <C> ColumnGroupReference.valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path)

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.valueCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[valueCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.valueCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    public fun <C> ColumnGroupReference.valueCol(property: KProperty<C>): ColumnAccessor<C> = valueColumn(property)

    // endregion

    // region colGroup

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`({@includeArg [CommonColGroupDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroup][ColumnReference.colGroup]`<SomeType>({@includeArg [CommonColGroupDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup\]
     * @see [col\]
     * @see [valueCol\]
     * @see [frameCol\]
     */
    private interface CommonColGroupDocs {

        /** Example argument */
        interface Arg
    }

    @Deprecated(COL_SELECT_DSL_GROUP, ReplaceWith(COL_SELECT_DSL_GROUP_REPLACE))
    public fun ColumnsContainer<*>.group(name: String): ColumnGroupReference = name.toColumnOf()

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("groupUnTyped")
    public fun colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUntyped")
    public fun colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> = columnGroup<C>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> = columnGroup(property)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the column group.
     */
    public fun <C> colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> = columnGroup(property)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUntyped")
    public fun ColumnGroupReference.colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnGroupReference.colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUntyped")
    public fun ColumnGroupReference.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        columnGroup<Any?>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnGroupReference.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        columnGroup<C>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> ColumnGroupReference.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(property)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [valueCol]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    public fun <C> ColumnGroupReference.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        columnGroup(property)

    // endregion

    // region frameCol

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup].
     *
     * #### For example:
     * `df.`[select][DataFrame.select]` { `[frameCol][frameCol]`({@includeArg [CommonFrameColDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[frameCol][ColumnReference.frameCol]`<SomeType>({@includeArg [CommonFrameColDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn\]
     * @see [colGroup\]
     * @see [valueCol\]
     * @see [col\]
     */
    private interface CommonFrameColDocs {

        /** Example argument */
        interface Arg
    }

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [name] The name of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [name] The name of the frame column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> = frameColumn<C>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [property] The [KProperty] pointing to the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColKPropertyDataFrame")
    public fun <C> frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> = frameColumn(property)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [property] The [KProperty] pointing to the frame column.
     */
    public fun <C> frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> = frameColumn(property)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [name] The name of the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun ColumnGroupReference.frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [name] The name of the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnGroupReference.frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun ColumnGroupReference.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
        frameColumn<Any?>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnGroupReference.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        frameColumn<C>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [property] The [KProperty] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColKPropertyDataFrame")
    public fun <C> ColumnGroupReference.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        frameColumn(property)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ConstructorsColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [valueCol]
     * @see [col] 
     * @param [property] The [KProperty] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    public fun <C> ColumnGroupReference.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        frameColumn(property)

    // endregion
}

// endregion

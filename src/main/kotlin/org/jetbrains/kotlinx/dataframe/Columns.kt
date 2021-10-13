package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.api.AddDataRowImpl
import org.jetbrains.kotlinx.dataframe.api.AddExpression
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.union
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.typed
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public enum class UnresolvedColumnsPolicy { Fail, Skip, Create }

public class ColumnResolutionContext(
    public val df: DataFrame<*>,
    public val unresolvedColumnsPolicy: UnresolvedColumnsPolicy
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy == UnresolvedColumnsPolicy.Skip
}

public fun <TD, T : DataFrame<TD>, C> Selector<T, Columns<C>>.toColumns(createReceiver: (ColumnResolutionContext) -> T): Columns<C> =
    createColumnSet {
        val receiver = createReceiver(it)
        val columnSet = this(receiver, receiver)
        columnSet.resolve(receiver, it.unresolvedColumnsPolicy)
    }

public fun <C> createColumnSet(resolver: (ColumnResolutionContext) -> List<ColumnWithPath<C>>): Columns<C> =
    object : Columns<C> {
        override fun resolve(context: ColumnResolutionContext) = resolver(context)
    }

public fun String.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

public fun <T> String.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

public fun <T> ColumnPath.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

public fun ColumnPath.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

public fun ColumnPath.toGroupColumnDef(): ColumnAccessor<AnyRow> = ColumnAccessorImpl(this)

internal fun KProperty<*>.getColumnName() = this.findAnnotation<ColumnName>()?.name ?: name

public fun <T> KProperty<T>.toColumnAccessor(): ColumnAccessor<T> = ColumnAccessorImpl<T>(name)

public fun <T> ColumnAccessor<DataRow<*>>.subcolumn(childName: String): ColumnAccessor<T> =
    ColumnAccessorImpl(path() + childName)

public inline fun <reified T> ColumnAccessor<T>.nullable(): ColumnAccessor<T?> = changeType<T?>()

public enum class ColumnKind {
    Value,
    Group,
    Frame
}

@OptIn(ExperimentalStdlibApi::class)
public inline fun <reified T> getType(): KType = typeOf<T>()

public fun KClass<*>.createStarProjectedType(nullable: Boolean): KType =
    this.starProjectedType.let { if (nullable) it.withNullability(true) else it }

public inline fun <reified T> ColumnReference<T>.withValues(vararg values: T): ValueColumn<T> = withValues(values.asIterable())

public inline fun <reified T> ColumnReference<T>.withValues(values: Iterable<T>): ValueColumn<T> =
    DataColumn.create(name(), values.asList(), getType<T>())

public fun AnyColumn.toDataFrame(): AnyFrame = dataFrameOf(listOf(this))

public fun <T, R> computeValues(df: DataFrame<T>, expression: AddExpression<T, R>): Pair<Boolean, List<R>> {
    var nullable = false
    val list = ArrayList<R>(df.nrow())
    df.indices().forEach {
        val row = AddDataRowImpl(it, df, list)
        val value = expression(row, row)
        if (value == null) nullable = true
        list.add(value)
    }
    return nullable to list
}

public inline fun <T, reified R> DataFrameBase<T>.newColumn(
    name: String = "",
    noinline expression: AddExpression<T, R>
): DataColumn<R> = newColumn(name, false, expression)

public inline fun <T, reified R> DataFrameBase<T>.newColumn(
    name: String = "",
    useActualType: Boolean,
    noinline expression: AddExpression<T, R>
): DataColumn<R> {
    if (useActualType) return newColumnWithActualType(name, expression)
    return newColumn(getType<R>(), name, expression)
}

@PublishedApi
internal fun <T, R> DataFrameBase<T>.newColumn(type: KType, name: String = "", expression: AddExpression<T, R>): DataColumn<R> {
    val (nullable, values) = computeValues(this as DataFrame<T>, expression)
    return when (type.classifier) {
        DataFrame::class -> DataColumn.frames(name, values as List<AnyFrame?>) as DataColumn<R>
        DataRow::class -> DataColumn.create(name, (values as List<AnyRow>).union()) as DataColumn<R>
        else -> DataColumn.create(name, values, type.withNullability(nullable))
    }
}

@PublishedApi
internal fun <T, R> DataFrameBase<T>.newColumnWithActualType(name: String, expression: AddExpression<T, R>): DataColumn<R> {
    val (_, values) = computeValues(this as DataFrame<T>, expression)
    return guessColumnType(name, values) as DataColumn<R>
}

public class ColumnDelegate<T>(private val parent: MapColumnReference? = null) {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = named(property.name)

    public infix fun named(name: String): ColumnAccessor<T> =
        parent?.let { ColumnAccessorImpl(it.path() + name) } ?: ColumnAccessorImpl(name)
}

public fun AnyColumn.asFrame(): AnyFrame = when (this) {
    is ColumnGroup<*> -> df
    is ColumnWithPath<*> -> data.asFrame()
    else -> error("Can not extract DataFrame from ${javaClass.kotlin}")
}

public fun AnyColumn.isGroup(): Boolean = kind() == ColumnKind.Group

public fun AnyColumn.isFrameColumn(): Boolean = kind() == ColumnKind.Frame

public fun <T> column(): ColumnDelegate<T> = ColumnDelegate()

public fun columnGroup(): ColumnDelegate<AnyRow> = column()

public fun <T> MapColumnReference.column(): ColumnDelegate<T> = ColumnDelegate<T>(this)

public fun columnGroup(parent: MapColumnReference): ColumnDelegate<AnyRow> = parent.column()

public fun frameColumn(): ColumnDelegate<AnyFrame> = column()

public fun <T> columnMany(): ColumnDelegate<Many<T>> = column<Many<T>>()

public fun <T> columnGroup(name: String): ColumnAccessor<DataRow<T>> = column(name)

public fun <T> frameColumn(name: String): ColumnAccessor<DataFrame<T>> = column(name)

public fun <T> columnMany(name: String): ColumnAccessor<Many<T>> = column<Many<T>>(name)

public fun <T> column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(name)

public fun <T> column(parent: MapColumnReference, name: String): ColumnAccessor<T> =
    ColumnAccessorImpl(parent.path() + name)

public inline fun <reified T> columnOf(vararg values: T): DataColumn<T> = createColumn(values.asIterable(), getType<T>(), true)

public fun columnOf(vararg values: AnyColumn): DataColumn<AnyRow> = columnOf(values.asIterable())

public fun <T> columnOf(vararg frames: DataFrame<T>?): FrameColumn<T> = columnOf(frames.asIterable())

public fun columnOf(columns: Iterable<AnyColumn>): DataColumn<AnyRow> = DataColumn.create("", dataFrameOf(columns)) as DataColumn<AnyRow>

public fun <T> columnOf(frames: Iterable<DataFrame<T>?>): FrameColumn<T> = DataColumn.create("", frames.toList())

public inline fun <reified T> column(values: Iterable<T>): DataColumn<T> = createColumn(values, getType<T>(), false)

@PublishedApi
internal fun <T> createColumn(values: Iterable<T>, suggestedType: KType, guessType: Boolean = false): DataColumn<T> = when {
    values.all { it is AnyCol } -> DataColumn.create("", (values as Iterable<AnyCol>).toDataFrame()) as DataColumn<T>
    values.all { it == null || it is AnyFrame } -> DataColumn.frames("", values.map { it as? AnyFrame }) as DataColumn<T>
    guessType -> guessColumnType("", values.asList(), suggestedType, suggestedTypeIsUpperBound = true).typed<T>()
    else -> DataColumn.create("", values.toList(), suggestedType)
}

public fun <T> Iterable<DataFrame<T>?>.toFrameColumn(name: String): FrameColumn<T> =
    DataColumn.create(name, asList())

public inline fun <reified T> Iterable<T>.toColumn(name: String = ""): ValueColumn<T> =
    asList().let { DataColumn.create(name, it, getType<T>().withNullability(it.any { it == null })) }

public fun Iterable<Any?>.toColumnGuessType(name: String = ""): AnyCol =
    guessColumnType(name, asList())

public inline fun <reified T> Iterable<T>.toColumn(ref: ColumnReference<T>): ValueColumn<T> =
    toColumn(ref.name())

public fun Iterable<AnyFrame?>.toColumn(): FrameColumn<Any?> = columnOf(this)

public fun Iterable<AnyFrame?>.toColumn(name: String): FrameColumn<Any?> = DataColumn.create(name, toList())

public inline fun <reified T> column(name: String, values: List<T>): DataColumn<T> = when {
    values.size > 0 && values.all { it is AnyCol } -> DataColumn.create(
        name,
        values.map { it as AnyCol }.toDataFrame()
    ) as DataColumn<T>
    else -> column(name, values, values.any { it == null })
}

public inline fun <reified T> column(name: String, values: List<T>, hasNulls: Boolean): DataColumn<T> =
    DataColumn.create(name, values, getType<T>().withNullability(hasNulls))

public fun <C> BaseColumn<C>.single(): C = values.single()

public fun <T> FrameColumn<T>.toDefinition(): ColumnAccessor<DataFrame<T>> = frameColumn<T>(name)
public fun <T> ColumnGroup<T>.toDefinition(): ColumnAccessor<DataRow<T>> = columnGroup<T>(name)
public fun <T> ValueColumn<T>.toDefinition(): ColumnAccessor<T> = column<T>(name)

public operator fun AnyColumn.plus(other: AnyColumn): AnyFrame = dataFrameOf(listOf(this, other))

public fun DataColumn<String?>.length(): DataColumn<Int?> = map { it?.length }
public fun DataColumn<String?>.lowercase(): DataColumn<String?> = map { it?.lowercase() }
public fun DataColumn<String?>.uppercase(): DataColumn<String?> = map { it?.uppercase() }

public infix fun <T, C : ColumnReference<T>> C.named(name: String): C = rename(name) as C

public infix fun <T> DataColumn<T>.eq(value: T): BooleanArray = isMatching { it == value }
public infix fun <T> DataColumn<T>.neq(value: T): BooleanArray = isMatching { it != value }

public infix fun DataColumn<Int>.gt(value: Int): BooleanArray = isMatching { it > value }
public infix fun DataColumn<Double>.gt(value: Double): BooleanArray = isMatching { it > value }
public infix fun DataColumn<Float>.gt(value: Float): BooleanArray = isMatching { it > value }
public infix fun DataColumn<String>.gt(value: String): BooleanArray = isMatching { it > value }

public infix fun DataColumn<Int>.lt(value: Int): BooleanArray = isMatching { it < value }
public infix fun DataColumn<Double>.lt(value: Double): BooleanArray = isMatching { it < value }
public infix fun DataColumn<Float>.lt(value: Float): BooleanArray = isMatching { it < value }
public infix fun DataColumn<String>.lt(value: String): BooleanArray = isMatching { it < value }

public infix fun <T> DataColumn<T>.isMatching(predicate: Predicate<T>): BooleanArray = BooleanArray(size) {
    predicate(this[it])
}

public infix fun <T> DataColumn<T>.isNotMatching(predicate: Predicate<T>): BooleanArray = BooleanArray(size) {
    !predicate(this[it])
}

public fun <T> BaseColumn<T>.first(): T = get(0)
public fun <T> BaseColumn<T>.firstOrNull(): T? = if (size > 0) first() else null
public fun <T> BaseColumn<T>.first(predicate: (T) -> Boolean): T = values.first(predicate)
public fun <T> BaseColumn<T>.firstOrNull(predicate: (T) -> Boolean): T? = values.firstOrNull(predicate)
public fun <T> BaseColumn<T>.last(): T = get(size - 1)
public fun <T> DataColumn<T>.lastOrNull(): T? = if (size > 0) last() else null

public fun <C> DataColumn<C>.allNulls(): Boolean = size == 0 || all { it == null }

internal fun KType.isSubtypeWithNullabilityOf(type: KType) = this.isSubtypeOf(type) && (!this.isMarkedNullable || type.isMarkedNullable)

public fun AnyCol.hasElementsOfType(type: KType): Boolean = typeOfElement().isSubtypeWithNullabilityOf(type)

public fun AnyCol.isSubtypeOf(type: KType): Boolean = this.type.isSubtypeOf(type) && (!this.type.isMarkedNullable || type.isMarkedNullable)

public inline fun <reified T> AnyCol.hasElementsOfType(): Boolean = hasElementsOfType(getType<T>())
public inline fun <reified T> AnyCol.isSubtypeOf(): Boolean = isSubtypeOf(getType<T>())
public inline fun <reified T> AnyCol.isType(): Boolean = type() == getType<T>()

@JvmName("asNumberAny?")
public fun DataColumn<Any?>.asNumbers(): ValueColumn<Number?> {
    require(isNumber())
    return this as ValueColumn<Number?>
}

@JvmName("asNumberAny")
public fun DataColumn<Any>.asNumbers(): ValueColumn<Number> {
    require(isNumber())
    return this as ValueColumn<Number>
}

@JvmName("asNumbersAny")
public fun Columns<Any>.asNumbers(): Columns<Number> = this as Columns<Number>

@JvmName("asNumbersAny?")
public fun Columns<Any?>.asNumbers(): Columns<Number?> = this as Columns<Number?>

public fun <T> DataColumn<T>.asComparable(): DataColumn<Comparable<T>> {
    require(isComparable())
    return this as DataColumn<Comparable<T>>
}

public fun <T> Columns<T>.asComparable(): Columns<Comparable<T>> = this as Columns<Comparable<T>>

public fun <T> DataColumn<T?>.asNotNullable(): DataColumn<T> {
    require(!hasNulls())
    return this as DataColumn<T>
}

public fun AnyCol.isNumber(): Boolean = hasElementsOfType<Number?>()

public fun AnyCol.isMany(): Boolean = typeClass == org.jetbrains.kotlinx.dataframe.Many::class
public fun AnyCol.typeOfElement(): KType =
    if (isMany()) type.arguments[0].type ?: getType<Any?>()
    else type

public fun AnyCol.elementTypeIsNullable(): Boolean = typeOfElement().isMarkedNullable

public fun AnyCol.isComparable(): Boolean = isSubtypeOf<Comparable<*>?>()

// TODO: remove by checking that type of column is always inferred
public fun AnyCol.guessType(): DataColumn<*> = DataColumn.create(name, toList())

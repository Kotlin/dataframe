package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.impl.anyNull
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.FrameColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ValueColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnKind
import org.jetbrains.kotlinx.dataframe.impl.splitByIndices
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Column with [name] and [values] of specific [type].
 *
 * Base interface for [ValueColumn] and [FrameColumn], but not for [ColumnGroup]. However, implementations for all three [column kinds][ColumnKind] derive from DataColumn and can cast to it safely.
 * Column operations that have signature clash with [DataFrame] API ([filter], [take], [map] etc.) are defined for [DataColumn] and not for [BaseColumn].
 *
 * @param T - type of values in the column.
 */
public interface DataColumn<out T> : BaseColumn<T> {

    public companion object {

        public fun <T> createValueColumn(
            name: String,
            values: List<T>,
            type: KType,
            checkForNulls: Boolean = false,
            defaultValue: T? = null
        ): ValueColumn<T> = ValueColumnImpl(values, name, type.let { if (checkForNulls) it.withNullability(values.anyNull()) else it }, defaultValue)

        public fun <T> createColumnGroup(name: String, df: DataFrame<T>): ColumnGroup<T> = ColumnGroupImpl(name, df)

        public fun <T> createFrameColumn(
            name: String,
            df: DataFrame<T>,
            startIndices: Iterable<Int>
        ): FrameColumn<T> =
            FrameColumnImpl(name, df.splitByIndices(startIndices.asSequence()).toList(), lazy { df.schema() })

        public fun <T> createFrameColumn(
            name: String,
            groups: List<DataFrame<T>>,
            schema: Lazy<DataFrameSchema>? = null
        ): FrameColumn<T> = FrameColumnImpl(name, groups, schema)

        public inline fun <reified T> createValueColumn(name: String, values: List<T>, checkForNulls: Boolean = false): ValueColumn<T> {
            val type = if (checkForNulls) typeOf<T>().withNullability(values.anyNull()) else typeOf<T>()
            return createValueColumn(name, values, type)
        }

        public fun <T> createWithTypeInference(name: String, values: List<T>, nullable: Boolean? = null): DataColumn<T> = guessColumnType(name, values, nullable = nullable)

        public fun <T> create(name: String, values: List<T>, type: KType, inferNulls: Boolean = false): DataColumn<T> {
            return when (type.toColumnKind()) {
                ColumnKind.Value -> createValueColumn(name, values, type, inferNulls)
                ColumnKind.Group -> createColumnGroup(name, (values as List<AnyRow?>).concat()).asDataColumn().cast()
                ColumnKind.Frame -> createFrameColumn(name, values as List<AnyFrame>).asDataColumn().cast()
            }
        }

        public inline fun <reified T> create(name: String, values: List<T>, checkForNulls: Boolean = false): DataColumn<T> = create(name, values, typeOf<T>(), checkForNulls)

        public fun empty(name: String = ""): AnyCol = createValueColumn(name, emptyList<Unit>(), typeOf<Unit>())
    }

    public fun hasNulls(): Boolean = type().isMarkedNullable

    override fun distinct(): DataColumn<T>

    override fun get(indices: Iterable<Int>): DataColumn<T>

    override fun rename(newName: String): DataColumn<T>

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? = this.addPath(context.df)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): DataColumn<T> = super.getValue(thisRef, property) as DataColumn<T>

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
public val AnyCol.typeClass: KClass<*> get() = type.classifier as KClass<*>

public fun AnyCol.indices(): IntRange = 0 until size

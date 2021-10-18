package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.anyNull
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.FrameColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ValueColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Column with type, name/path and values
 * Base interface for [ValueColumn] and [FrameColumn], but not for [ColumnGroup]
 * All extension functions that clash with [DataFrame] API (such as filter, forEach, map etc.) should be defined for this interface
 *
 * Although [ColumnGroup] doesn't implement this interface, but [ColumnGroupImpl] does, so you can cast any actual instance of [ColumnGroup] to [DataColumn]
 */
public interface DataColumn<out T> : BaseColumn<T> {

    public companion object {

        public fun <T> create(name: String, values: List<T>, type: KType, defaultValue: T? = null): ValueColumn<T> = ValueColumnImpl(values, name, type, defaultValue)

        public fun <T> create(name: String, df: DataFrame<T>): ColumnGroup<T> = ColumnGroupImpl(df, name)

        public fun <T> create(name: String, df: DataFrame<T>, startIndices: Sequence<Int>, emptyToNull: Boolean): FrameColumn<T> = FrameColumnImpl(name, df, startIndices, emptyToNull)

        public fun <T> create(name: String, df: DataFrame<T>, startIndices: Iterable<Int>, emptyToNull: Boolean): FrameColumn<T> =
            create(name, df, startIndices.asSequence(), emptyToNull)

        public fun <T> frames(name: String, groups: List<DataFrame<T>?>): FrameColumn<T> = create(name, groups, null)

        internal fun <T> create(
            name: String,
            groups: List<DataFrame<T>?>,
            hasNulls: Boolean? = null,
            schema: Lazy<DataFrameSchema>? = null
        ): FrameColumn<T> = FrameColumnImpl(name, groups, hasNulls, schema)

        public fun create(name: String, values: List<Any?>): DataColumn<*> = guessColumnType(name, values)

        public fun <T> createWithNullCheck(name: String, values: List<T>, type: KType): ValueColumn<T> = create(name, values, type.withNullability(values.anyNull()))

        public inline fun <reified T> createWithNullCheck(name: String, values: List<T>): ValueColumn<T> = createWithNullCheck(name, values, getType<T>())

        public fun empty(): AnyCol = create("", emptyList<Unit>(), getType<Unit>())
    }

    public fun hasNulls(): Boolean = type().isMarkedNullable

    override fun distinct(): DataColumn<T>

    override fun slice(range: IntRange): DataColumn<T>

    override fun slice(indices: Iterable<Int>): DataColumn<T>

    override fun slice(mask: BooleanArray): DataColumn<T>

    override fun rename(newName: String): DataColumn<T>

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? = this.addPath(context.df)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): DataColumn<T> = super.getValue(thisRef, property) as DataColumn<T>

    public operator fun iterator(): Iterator<T> = values().iterator()
}

internal val AnyCol.type get() = type()
internal val AnyCol.kind get() = kind()
internal val AnyCol.hasNulls get() = hasNulls()
internal val AnyCol.typeClass get() = type.classifier as KClass<*>

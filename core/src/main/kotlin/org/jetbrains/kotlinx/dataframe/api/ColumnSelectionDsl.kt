package org.jetbrains.kotlinx.dataframe.api

import kotlin.reflect.KProperty
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.getColumn
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API

/** [Column Selection DSL][ColumnSelectionDsl] */
internal typealias ColumnSelectionDslLink = Nothing

public interface ColumnSelectionDsl<out T> : ColumnsContainer<T> {

    /**
     * Retrieves the value of this [ColumnReference] or [-Accessor][ColumnAccessor] from the
     * [DataFrame].
     *
     * This is a shorthand for [get][ColumnsContainer.get]`(myColumn)`.
     *
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private typealias CommonColumnReferenceInvokeDocs = Nothing

    /**
     * @return The [DataColumn] this [Column Reference][ColumnReference] or
     *   [-Accessor][ColumnAccessor] points to.
     * @include [CommonColumnReferenceInvokeDocs]
     */
    public operator fun <C> ColumnReference<C>.invoke(): DataColumn<C> = get(this)

    /**
     * @return The [ColumnGroup] this [Column Reference][ColumnReference] or
     *   [-Accessor][ColumnAccessor] points to.
     * @include [CommonColumnReferenceInvokeDocs]
     */
    public operator fun <T> ColumnReference<DataRow<T>>.invoke(): ColumnGroup<T> = get(this)

    /**
     * @return The [FrameColumn] this [Column Reference][ColumnReference] or
     *   [-Accessor][ColumnAccessor] points to.
     * @include [CommonColumnReferenceInvokeDocs]
     */
    public operator fun <T> ColumnReference<DataFrame<T>>.invoke(): FrameColumn<T> = get(this)

    /**
     * Retrieves the value of this [ColumnPath] from the [DataFrame]. This is a shorthand for
     * [getColumn][ColumnsContainer.getColumn]`(myColumnPath)` and is most often used in combination
     * with `operator fun String.get(column: String)`, {@comment cannot point to the right
     * function.} for instance:
     * ```kotlin
     * "myColumn"["myNestedColumn"]<NestedColumnType>()
     * ```
     *
     * @return The [DataColumn] this [ColumnPath] points to.
     * @throws [IllegalArgumentException] if the column is not found.
     */
    @Interpretable("ColumnPathInvokeTyped")
    public operator fun <C> ColumnPath.invoke(): DataColumn<C> = getColumn(this).cast()

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame].
     *
     * This is a shorthand for [get][ColumnsContainer.get]`(MyType::myColumn)`.
     *
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private typealias CommonKPropertyInvokeDocs = Nothing

    /**
     * @return The [DataColumn] this [KProperty Accessor][KProperty] points to.
     * @include [CommonKPropertyInvokeDocs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T> KProperty<T>.invoke(): DataColumn<T> = this@ColumnSelectionDsl[this]

    /**
     * @return The [ColumnGroup] this [KProperty Accessor][KProperty] points to.
     * @include [CommonKPropertyInvokeDocs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T> KProperty<DataRow<T>>.invoke(): ColumnGroup<T> =
        this@ColumnSelectionDsl[this]

    /**
     * @return The [FrameColumn] this [KProperty Accessor][KProperty] points to.
     * @include [CommonKPropertyInvokeDocs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T> KProperty<DataFrame<T>>.invoke(): FrameColumn<T> =
        this@ColumnSelectionDsl[this]

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame].
     *
     * This is a shorthand for
     *
     * [get][ColumnsContainer.get]`(MyType::myColumnGroup).`[asColumnGroup][asColumnGroup]`().`[get][ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[`[`][KProperty.get]`MyOtherType::myOtherColumn`[`]`][KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private typealias CommonKPropertyGetDocs = Nothing

    /**
     * @return The [DataColumn] these [KProperty Accessors][KProperty] point to.
     * @include [CommonKPropertyGetDocs]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowGet")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<R>): DataColumn<R> =
        invoke()[column]

    /**
     * @return The [ColumnGroup] these [KProperty Accessors][KProperty] point to.
     * @include [CommonKPropertyGetDocs]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowGet")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<DataRow<T>>.get(
        column: KProperty<DataRow<R>>
    ): ColumnGroup<R> = invoke()[column]

    /**
     * @return The [FrameColumn] these [KProperty Accessors][KProperty] point to.
     * @include [CommonKPropertyGetDocs]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowGet")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<DataRow<T>>.get(
        column: KProperty<DataFrame<R>>
    ): FrameColumn<R> = invoke()[column]

    /**
     * @return The [DataColumn] these [KProperty Accessors][KProperty] point to.
     * @include [CommonKPropertyGetDocs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<T>.get(column: KProperty<R>): DataColumn<R> =
        invoke().asColumnGroup()[column]

    /**
     * @return The [ColumnGroup] these [KProperty Accessors][KProperty] point to.
     * @include [CommonKPropertyGetDocs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<T>.get(column: KProperty<DataRow<R>>): ColumnGroup<R> =
        invoke().asColumnGroup()[column]

    /**
     * @return The [FrameColumn] these [KProperty Accessors][KProperty] point to.
     * @include [CommonKPropertyGetDocs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<T>.get(column: KProperty<DataFrame<R>>): FrameColumn<R> =
        invoke().asColumnGroup()[column]

    /**
     * Retrieves the value of the column with this name from the [DataFrame]. This can be both typed
     * and untyped. This is a shorthand for [get][ColumnsContainer.get]`("myColumnName")` and can be
     * written as `"myColumnName"<MyColumnType>()` instead.
     *
     * @return The [DataColumn] with this name.
     * @throws [IllegalArgumentException] if there is no column with this name.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("stringInvokeTyped")
    @Interpretable("StringInvokeTyped")
    public operator fun <C> String.invoke(): DataColumn<C> = getColumn(this).cast()

    /**
     * Retrieves the value of the column with this name from the [DataFrame]. This can be both typed
     * and untyped. This is a shorthand for [get][ColumnsContainer.get]`("myColumnName")` and can be
     * written as `"myColumnName"()` instead.
     *
     * @return The [DataColumn] with this name.
     * @throws [IllegalArgumentException] if there is no column with this name.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("stringInvokeUntyped")
    @Interpretable("StringInvokeUntyped")
    public operator fun String.invoke(): DataColumn<*> = getColumn(this)

    /**
     * Creates a [ColumnPath] from the receiver and the given column name [column]. This is a
     * shorthand for [pathOf]`("myColumnName", "myNestedColumnName")` and is often used in
     * combination with [ColumnPath.invoke] to retrieve the value of a nested column. For instance:
     * ```kotlin
     * "myColumn"["myNestedColumn"]<NestedColumnType>()
     *
     * "myColumn"["myNestedColumn"]["myDoublyNestedColumn"]<NestedColumnType>()
     * ```
     */
    @Interpretable("StringGetColumn")
    public operator fun String.get(column: String): ColumnPath = pathOf(this, column)

    /**
     * As extension to `"myColumn"["myNestedColumn"]`, this function enables
     * `"myColumn"["myNestedColumn"]["myDoublyNestedColumn"]` as alternative to
     * [pathOf]`("myColumn", "myNestedColumn", "myDoublyNestedColumn")`
     */
    @Interpretable("ColumnPathGetColumn")
    public operator fun ColumnPath.get(column: String): ColumnPath = this + column
}

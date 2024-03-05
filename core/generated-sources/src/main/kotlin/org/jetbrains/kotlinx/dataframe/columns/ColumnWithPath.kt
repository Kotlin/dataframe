package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.addParentPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.depth
import org.jetbrains.kotlinx.dataframe.util.COLUMN_WITH_PATH_MESSAGE
import kotlin.reflect.KProperty

public interface ColumnWithPath<out T> : DataColumn<T> {
    public val data: DataColumn<T>

    public val path: ColumnPath

    public val name: String get() = name()

    public val parentName: String? get() = path.parentName

    public fun depth(): Int = path.depth()

    /**
     * Casts this column to a [ColumnGroup] and returns a column with the specified [accessor] or null if it
     * can't be found.
     */
    public fun <C> getCol(accessor: ColumnReference<C>): ColumnWithPath<C>? =
        asColumnGroup().getColumnOrNull(accessor)?.addPath(path + accessor.path())

    /**
     * Casts this column to a [ColumnGroup] and returns a column with the specified [name] or null if it
     * can't be found.
     */
    public fun getCol(name: String): ColumnWithPath<Any?>? =
        asColumnGroup().getColumnOrNull(name)?.addParentPath(path)

    /**
     * Casts this column to a [ColumnGroup] and returns a column with the specified [index] or null if it
     * can't be found.
     */
    public fun getCol(index: Int): ColumnWithPath<Any?>? =
        asColumnGroup().getColumnOrNull(index)?.addParentPath(path)

    /**
     * Casts this column to a [ColumnGroup] and returns a column with the specified [accessor] or null if it
     * can't be found.
     */
    public fun <C> getCol(accessor: KProperty<C>): ColumnWithPath<C>? =
        asColumnGroup().getColumnOrNull(accessor)?.addParentPath(path)

    /**
     * Returns all ("children") columns in this column if it's a group, else it returns an empty list.
     */
    public fun cols(): List<ColumnWithPath<Any?>> =
        if (isColumnGroup()) {
            data.asColumnGroup().columns().map { it.addParentPath(path) }
        } else {
            emptyList()
        }

    override fun path(): ColumnPath = path

    override fun rename(newName: String): ColumnWithPath<T>

    // region deprecated

    @Deprecated(
        message = COLUMN_WITH_PATH_MESSAGE,
        replaceWith = ReplaceWith("getCol(accessor)"),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> getChild(accessor: ColumnReference<C>): ColumnWithPath<C>? = getCol(accessor)

    @Deprecated(
        message = COLUMN_WITH_PATH_MESSAGE,
        replaceWith = ReplaceWith("getCol(name)"),
        level = DeprecationLevel.WARNING,
    )
    public fun getChild(name: String): ColumnWithPath<Any?>? = getCol(name)

    @Deprecated(
        message = COLUMN_WITH_PATH_MESSAGE,
        replaceWith = ReplaceWith("getCol(index)"),
        level = DeprecationLevel.WARNING,
    )
    public fun getChild(index: Int): ColumnWithPath<Any?>? = getCol(index)

    @Deprecated(
        message = COLUMN_WITH_PATH_MESSAGE,
        replaceWith = ReplaceWith("getCol(accessor)"),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> getChild(accessor: KProperty<C>): ColumnWithPath<C>? = getCol(accessor)

    @Deprecated(
        message = COLUMN_WITH_PATH_MESSAGE,
        replaceWith = ReplaceWith("cols()"),
        level = DeprecationLevel.WARNING,
    )
    public fun children(): List<ColumnWithPath<Any?>> = cols()

    // endregion
}

public val <T> ColumnWithPath<T>.depth: Int get() = path.depth()

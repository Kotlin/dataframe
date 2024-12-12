package org.jetbrains.kotlinx.dataframe

/**
 * Provides minimal API required for generated column properties:
 *
 * `val ColumnsScope<Schema marker>.column: DataColumn<String> get() = this["column"] as DataColumn<String>`
 *
 * @param T Schema marker. Used to resolve generated extension properties for typed column access.
 */
public interface ColumnsScope<out T> {
    public operator fun get(columnName: String): AnyCol
}

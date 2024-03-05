package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ValueWithDefault
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.KType

@Suppress("DataClassPrivateConstructor")
public data class NamedValue private constructor(
    val path: ColumnPath,
    val value: Any?,
    val type: KType?,
    var default: Any?,
    val guessType: Boolean = false
) {
    public companion object {
        internal fun create(path: ColumnPath, value: Any?, type: KType?, defaultValue: Any?, guessType: Boolean = false): NamedValue = when (value) {
            is ValueWithDefault<*> -> create(path, value.value, type, value.default, guessType)
            else -> NamedValue(path, value, type, defaultValue, guessType)
        }
        internal fun aggregator(builder: AggregateGroupedDsl<*>): NamedValue =
            NamedValue(emptyPath(), builder, null, null, false)
    }

    val name: String get() = path.last()
}

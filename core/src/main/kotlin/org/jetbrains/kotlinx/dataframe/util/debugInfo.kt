@file:Suppress("ktlint:standard:class-naming")

package org.jetbrains.kotlinx.dataframe.util

import org.jetbrains.annotations.Debug
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema

internal sealed interface DebugEntry {
    val key: String
    val value: Any?

    companion object {
        operator fun invoke(key: String): _D0 = _D0(key)

        operator fun invoke(key: String, value: Any?): _D1 = _D1(key, value)

        operator fun invoke(key: String, value: Array<out Any?>): _Dx = _Dx(key, value)
    }
}

@Debug.Renderer(
    text = "key",
    childrenArray = "new java.lang.Object[] {}",
    hasChildren = "false",
)
internal class _D0(override val key: String) : DebugEntry {
    override val value: Any? = null
}

@Debug.Renderer(
    text = "key",
    childrenArray = "new java.lang.Object[] { this.value }",
    hasChildren = "true",
)
internal class _D1(override val key: String, override val value: Any?) : DebugEntry

@Debug.Renderer(
    text = "key",
    childrenArray = "this.value",
    hasChildren = "true",
)
internal class _Dx(override val key: String, override val value: Array<out Any?>) : DebugEntry

internal fun renderColumnNameAndType(column: AnyCol): String =
    renderColumnNameAndType(column.name(), column.extractSchema())

internal fun renderColumnNameAndType(name: String, schema: ColumnSchema): String =
    when (schema) {
        is ColumnSchema.Value -> "$name: ${renderType(schema.type)}"
        is ColumnSchema.Frame -> "$name: frame column <${renderType(schema.contentType)}>"
        is ColumnSchema.Group -> "$name: column group <${renderType(schema.contentType)}>"
        else -> error("")
    }

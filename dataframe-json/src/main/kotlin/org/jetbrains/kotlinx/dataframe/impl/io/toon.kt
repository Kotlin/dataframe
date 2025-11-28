package org.jetbrains.kotlinx.dataframe.impl.io

public enum class ToonDelimiter(internal val value: Char) {
    COMMA(','),
    TAB('\t'),
    PIPE('|'),
}

internal sealed interface ToonElement

internal fun ToonElement.render(
    delimiter: ToonDelimiter = ToonDelimiter.COMMA,
    indentStart: Int = 0,
    indentStep: Int = 2,
): String =
    when (this) {
        is ToonPrimitive<*> -> render(delimiter)
        is ToonArray -> render(delimiter, indentStart, indentStep)
        is ToonObject -> render(delimiter, indentStart, indentStep)
    }

internal fun ToonElement.asPrimitive() = this as ToonPrimitive<*>

internal fun ToonElement.asObject() = this as ToonObject

internal fun ToonElement.asArray() = this as ToonArray

// region Primitives

internal sealed interface ToonPrimitive<T : Any?> : ToonElement {

    val value: T

    fun render(delimiter: ToonDelimiter): String

    companion object {
        val NULL = ToonNull

        operator fun invoke(value: Nothing?): ToonNull = ToonNull

        operator fun invoke(value: Boolean): ToonBoolean = ToonBoolean(value)

        operator fun invoke(value: Number): ToonNumber = ToonNumber(value)

        operator fun invoke(value: String): ToonString = ToonString(value)

        operator fun invoke(value: Any?): ToonPrimitive<*> =
            when (value) {
                null -> ToonNull
                is Number -> ToonNumber(value)
                is Boolean -> ToonBoolean(value)
                is String -> ToonString(value)
                else -> ToonString(value.toString()) // default to string
            }
    }
}

internal data object ToonNull : ToonPrimitive<Nothing?> {
    override val value: Nothing? = null

    override fun render(delimiter: ToonDelimiter): String = "null"
}

@JvmInline
internal value class ToonString(override val value: String) : ToonPrimitive<String> {

    private companion object {
        private val disallowedToonChars = setOf('\n', '\r', '\t', '"', '{', '}', '[', ']', ':', '\\')

        private fun String.looksLikeNumber() =
            matches("""/^-?\d+(?:.\d+)?(?:e[+-]?\d+)?$/i""".toRegex()) ||
                matches("""/^0\d+$/""".toRegex())

        private fun String.needsQuotes(activeDelimiter: ToonDelimiter): Boolean =
            isEmpty() ||
                trim().length != length ||
                this == "true" ||
                this == "false" ||
                this == "null" ||
                looksLikeNumber() ||
                any { it in disallowedToonChars } ||
                contains(activeDelimiter.value) ||
                startsWith('-')
    }

    override fun render(delimiter: ToonDelimiter): String =
        when {
            value.needsQuotes(delimiter) -> "\"$value\""
            else -> value
        }
}

@JvmInline
internal value class ToonNumber(override val value: Number) : ToonPrimitive<Number> {
    override fun render(delimiter: ToonDelimiter): String = value.toString()
}

@JvmInline
internal value class ToonBoolean(override val value: Boolean) : ToonPrimitive<Boolean> {
    override fun render(delimiter: ToonDelimiter): String = value.toString()
}

// endregion

// region Object

/**
 * id: 123
 * name: Ada
 * active[2]: true,false
 *
 * or
 *
 * user:
 *   id: 123
 *   name: Ada
 */
internal class ToonObject(val map: Map<String, ToonElement>) : ToonElement {
    // can be made tabular if flat
    val isFlat: Boolean = map.values.all { it is ToonPrimitive<*> }

    fun String.needsQuotes() = !matches("""^[A-Za-z_][A-Za-z0-9_.]*$""".toRegex())

    // TODO? key folding
    fun render(delimiter: ToonDelimiter, indentStart: Int, indentStep: Int): String =
        buildString {
            var isFirst = true
            for ((key, value) in map) {
                // ignore indents for the first key-value pair
                if (isFirst) {
                    isFirst = false
                } else {
                    append(indent(indentStart))
                }
                append(
                    when {
                        key.needsQuotes() -> "\"$key\""
                        else -> key
                    },
                )
                when (value) {
                    // id: 123
                    is ToonPrimitive<*> -> appendLine(": ${value.render(delimiter)}")

                    // active[2]: true,false
                    //
                    // items[2]{sku,qty,price}:
                    //   A1,2,9.99
                    //   B2,1,14.5
                    //
                    // items[3]:
                    //   - 1
                    //   - a: 1
                    //     b: 3
                    //   - text
                    is ToonArray -> append(
                        value.render(
                            delimiter = delimiter,
                            indentStart = indentStart,
                            indentStep = indentStep,
                        ),
                    )

                    // user:
                    //   id: 123
                    //   name: Ada
                    is ToonObject -> append(
                        ":\n${indent(indentStart + indentStep)}${
                            value.render(
                                delimiter = delimiter,
                                indentStart = indentStart + indentStep,
                                indentStep = indentStep,
                            )
                        }",
                    )
                }
            }
        }
}

// endregion

// region Arrays

internal sealed interface ToonArray : ToonElement {
    val size: Int

    fun render(delimiter: ToonDelimiter, indentStart: Int, indentStep: Int): String

    companion object {
        @JvmName("ofPrimitives")
        operator fun invoke(primitives: List<ToonPrimitive<*>>): ToonArray = ToonInlineArray(values = primitives)

        @JvmName("ofObjectArray")
        operator fun invoke(header: List<String>, objectArray: List<List<ToonPrimitive<*>>>): ToonArray {
            require(objectArray.all { it.size == header.size }) {
                "All object rows must have the same number of columns as the header"
            }
            return ToonTabularArray(header = header, objectArray = objectArray)
        }

        @JvmName("ofObjects")
        operator fun invoke(objects: List<ToonObject>): ToonArray =
            objects.toTabularOrNull()
                ?: ToonMixedArray(values = objects)

        @JvmName("ofElements")
        @Suppress("UNCHECKED_CAST")
        operator fun invoke(values: List<ToonElement>): ToonArray =
            when {
                values.all { it is ToonPrimitive<*> } ->
                    invoke(primitives = values as List<ToonPrimitive<*>>)

                values.all { it is ToonObject } -> invoke(objects = values as List<ToonObject>)

                else -> ToonMixedArray(values = values)
            }

        private fun List<ToonObject>.toTabularOrNull(): ToonTabularArray? {
            if (isEmpty()) return ToonTabularArray(emptyList(), emptyList())

            val header = first().map.keys
            if (!all { it.map.keys == header }) return null
            if (any { !it.isFlat }) return null

            val objectArray = map { it.map.values.toList() as List<ToonPrimitive<*>> }
            return ToonTabularArray(header = header.toList(), objectArray = objectArray)
        }
    }
}

/**
 * tags[3]: admin,ops,dev
 */
private class ToonInlineArray(val values: List<ToonPrimitive<*>>) : ToonArray {
    override val size: Int = values.size

    override fun render(delimiter: ToonDelimiter, indentStart: Int, indentStep: Int): String =
        buildString {
            append("[$size")
            if (delimiter != ToonDelimiter.COMMA) append(delimiter.value)
            append("]: ")
            appendLine(
                values.joinToString(delimiter.value.toString()) { it.render(delimiter) },
            )
        }
}

/**
 * items[2]{sku,qty,price}:
 *   A1,2,9.99
 *   B2,1,14.5
 */
private class ToonTabularArray(val header: List<String>, val objectArray: List<List<ToonPrimitive<*>>>) : ToonArray {
    override val size: Int = objectArray.size

    override fun render(delimiter: ToonDelimiter, indentStart: Int, indentStep: Int): String =
        buildString {
            append("[$size")
            if (delimiter != ToonDelimiter.COMMA) append(delimiter.value)
            append("]{")
            append(header.joinToString(delimiter.value.toString()))
            appendLine("}:")

            val valueIndent = indentStart + indentStep
            for (row in objectArray) {
                append(indent(valueIndent))
                appendLine(
                    row.joinToString(delimiter.value.toString()) { it.render(delimiter) },
                )
            }
        }
}

/**
 * items[3]:
 *   - 1
 *   - a: 1
 *   - text
 */
private class ToonMixedArray(val values: List<ToonElement>) : ToonArray {
    override val size: Int = values.size

    override fun render(delimiter: ToonDelimiter, indentStart: Int, indentStep: Int): String =
        buildString {
            append("[$size")
            if (delimiter != ToonDelimiter.COMMA) append(delimiter.value)
            appendLine("]:")

            val valueIndent = indentStart + indentStep
            for (value in values) {
                append(indent(valueIndent))
                append("- ")
                append(
                    value.render(
                        delimiter = delimiter,
                        indentStart = valueIndent + indentStep,
                        indentStep = indentStep,
                    ),
                )
                if (value is ToonPrimitive<*>) appendLine()
            }
        }
}

// endregion

private fun indent(size: Int) = " ".repeat(size)

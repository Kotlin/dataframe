package org.jetbrains.kotlinx.dataframe.impl.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.isValueColumn
import org.jetbrains.kotlinx.dataframe.api.rows
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

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

private fun List<ToonObject>.toTabularOrNull(): ToonTabularArray? {
    if (isEmpty()) return ToonTabularArray(emptyList(), emptyList())

    val header = first().map.keys
    if (!all { it.map.keys == header }) return null
    if (any { !it.isFlat }) return null

    val objectArray = map { it.map.values.toList() as List<ToonPrimitive<*>> }
    return ToonTabularArray(header = header.toList(), objectArray = objectArray)
}

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

private fun indent(size: Int) = " ".repeat(size)

private fun Any?.toToonPrimitive(type: KType): ToonPrimitive<*> {
    val type = type.withNullability(false)
    return when {
        this == null -> ToonPrimitive.NULL
        type == typeOf<Boolean>() -> ToonPrimitive(this as Boolean)
        type.isSubtypeOf(typeOf<Number>()) -> ToonPrimitive(this as Number)
        type == typeOf<String>() -> ToonPrimitive(this as String)
        else -> ToonPrimitive(this)
    }
}

internal fun encodeToToonImpl(df: AnyFrame): ToonArray {
    val isFlat = df.columns().all { it.isValueColumn() }
    if (isFlat) {
        val header = df.columnNames()
        val types = df.columnTypes()

        val objectArray = df.rows().map { row ->
            header.indices.map { i ->
                row[i].toToonPrimitive(types[i])
            }
        }

        return ToonArray(
            header = header,
            objectArray = objectArray,
        )
    }

    val objects = df.rows().map { encodeToToonImpl(it) }
    return ToonArray(objects)
}

internal fun encodeToToonImpl(row: AnyRow): ToonObject {
    val values = row.df().columns().associate { col ->
        col.name() to when {
            col.isColumnGroup() -> encodeToToonImpl(col[row.index()])

            col.isFrameColumn() -> encodeToToonImpl(col[row.index()])

            col.isList() -> col.cast<List<*>?>()[row.index()]?.let {
                ToonArray(it.map { ToonPrimitive(it) })
            } ?: ToonPrimitive.NULL

            else -> col[row.index()].toToonPrimitive(col.type())
        }
    }
    return ToonObject(values)
}

/**
 * ```json
 * {
 *   "context": {
 *     "task": "Our favorite hikes together",
 *     "location": "Boulder",
 *     "season": "spring_2025"
 *   },
 *   "friends": ["ana", "luis", "sam"],
 *   "friends2": ["ana", "luis", ["sam"], {"a": 1, "b": 3}, [{
 *   "id": 1,
 *   "name": "Blue Lake Trail",
 *   "distanceKm": 7.5,
 *   "elevationGain": 320,
 *   "companion": "ana",
 *   "wasSunny": true
 * },
 * {
 *   "id": 2,
 *   "name": "Ridge Overlook",
 *   "distanceKm": 9.2,
 *   "elevationGain": 540,
 *   "companion": "luis",
 *   "wasSunny": false
 * },
 * {
 *   "id": 3,
 *   "name": "Wildflower Loop",
 *   "distanceKm": 5.1,
 *   "elevationGain": 180,
 *   "companion": "sam",
 *   "wasSunny": true
 * }]],
 *   "hikes": [
 *     {
 *       "id": 1,
 *       "name": "Blue Lake Trail",
 *       "distanceKm": 7.5,
 *       "elevationGain": 320,
 *       "companion": "ana",
 *       "wasSunny": true
 *     },
 *     {
 *       "id": 2,
 *       "name": "Ridge Overlook",
 *       "distanceKm": 9.2,
 *       "elevationGain": 540,
 *       "companion": "luis",
 *       "wasSunny": false
 *     },
 *     {
 *       "id": 3,
 *       "name": "Wildflower Loop",
 *       "distanceKm": 5.1,
 *       "elevationGain": 180,
 *       "companion": "sam",
 *       "wasSunny": true
 *     }
 *   ]
 * }
 * ```
 */
public fun main() {
    @Suppress("ktlint:standard:argument-list-wrapping")
    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
        "Alice", "Cooper", 15, "London", 54, true,
        "Bob", "Dylan", 45, "Dubai", 87, true,
        "Charlie", "Daniels", 20, "Moscow", null, false,
        "Charlie", "Chaplin", 40, "Milan", null, true,
        "Bob", "Marley", 30, "Tokyo", 68, true,
        "Alice", "Wolf", 20, null, 55, false,
        "Charlie", "Byrd", 30, "Moscow", 90, true,
    ).group("firstName", "lastName").into("name")

    println(
        encodeToToonImpl(df).render(),
    )

    return

    // [
    //  1,
    //  [1],
    //  {"a": 1},
    //  [{"b": 2}]
    // ]
    ToonArray(
        listOf(
            ToonPrimitive(1),
            ToonArray(listOf(ToonPrimitive(1))),
            ToonObject(mapOf("a" to ToonPrimitive(1))),
            ToonArray(listOf(ToonObject(mapOf("b" to ToonPrimitive(2))))),
        ),
    ).let {
        println(it.render())
    }

    val obj = ToonObject(
        mapOf(
            "context" to ToonObject(
                mapOf(
                    "task" to ToonPrimitive("Our favorite hikes together"),
                    "location" to ToonPrimitive("Boulder"),
                    "season" to ToonPrimitive("spring_2025"),
                ),
            ),
            "friends" to ToonArray(listOf(ToonPrimitive("ana"), ToonPrimitive("luis"), ToonPrimitive("sam"))),
            "friends2" to ToonArray(
                listOf(
                    ToonPrimitive("ana"),
                    ToonPrimitive("luis"),
                    ToonArray(listOf(ToonPrimitive("sam"))),
                    ToonObject(mapOf("a" to ToonPrimitive(1), "b" to ToonPrimitive(3))),
                    ToonArray(
                        listOf(
                            ToonObject(
                                mapOf(
                                    "test" to ToonPrimitive(1),
                                    "nested" to ToonPrimitive(3),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
            "hikes" to ToonArray(
                listOf(
                    ToonObject(
                        mapOf(
                            "id" to ToonPrimitive(1),
                            "name" to ToonPrimitive("Blue Lake Trail"),
                            "distanceKm" to ToonPrimitive(7.5),
                            "elevationGain" to ToonPrimitive(320),
                            "companion" to ToonPrimitive("ana"),
                            "wasSunny" to ToonPrimitive(true),
                        ),
                    ),
                    ToonObject(
                        mapOf(
                            "id" to ToonPrimitive(2),
                            "name" to ToonPrimitive("Ridge Overlook"),
                            "distanceKm" to ToonPrimitive(9.2),
                            "elevationGain" to ToonPrimitive(540),
                            "companion" to ToonPrimitive("luis"),
                            "wasSunny" to ToonPrimitive(false),
                        ),
                    ),
                    ToonObject(
                        mapOf(
                            "id" to ToonPrimitive(3),
                            "name" to ToonPrimitive("Wildflower Loop"),
                            "distanceKm" to ToonPrimitive(5.1),
                            "elevationGain" to ToonPrimitive(180),
                            "companion" to ToonPrimitive("sam"),
                            "wasSunny" to ToonPrimitive(true),
                        ),
                    ),
                ),
            ),
        ),
    )

    println(
        obj.render(),
    )
}

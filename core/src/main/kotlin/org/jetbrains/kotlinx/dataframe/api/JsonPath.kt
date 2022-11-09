package org.jetbrains.kotlinx.dataframe.api

import org.intellij.lang.annotations.Language
import java.io.Serializable

/**
 * Simplistic JSON path implementation.
 * Supports just keys (in bracket notation), double quotes, arrays and wildcards.
 *
 * Examples:
 * `$["store"]["book"][*]["author"]`
 *
 * `$[1]` will match `$[*]`
 */
@JvmInline
public value class JsonPath(@Language("jsonpath") public val path: String = "$") : Serializable {

    public fun append(name: String): JsonPath = JsonPath("$path[\"$name\"]")

    public fun appendWildcard(): JsonPath = JsonPath("$path[*]")

    public fun appendArrayWithIndex(index: Int): JsonPath = JsonPath("$path[$index]")

    public fun appendArrayWithWildcard(): JsonPath = JsonPath("$path[*]")

    public fun replaceLastWildcardWithIndex(index: Int): JsonPath = JsonPath(
        path.toCharArray().let { chars ->
            val lastStarIndex = chars.lastIndexOf('*')
            chars.flatMapIndexed { i, c ->
                if (i == lastStarIndex) index.toString().toCharArray().toList()
                else listOf(c)
            }.joinToString("")
        }
    )

    public fun prepend(name: String): JsonPath = JsonPath(
        "\$[\"$name\"]" + path.removePrefix("$")
    )

    public fun prependWildcard(): JsonPath = JsonPath(
        "\$[*]" + path.removePrefix("$")
    )

    public fun prependArrayWithIndex(index: Int): JsonPath = JsonPath(
        "\$[$index]" + path.removePrefix("$")
    )

    public fun prependArrayWithWildcard(): JsonPath = JsonPath(
        "\$[*]" + path.removePrefix("$")
    )

    private fun erasedIndices(): JsonPath = JsonPath(
        path.replace("""\[[0-9]+]""".toRegex(), "[*]")
    )

    private fun toDotNotation(): JsonPath = JsonPath(
        path.replace("""\[(('([^']+)')|("([^"]+)"))]""".toRegex()) {
            val value = it.groupValues[3].takeIf { it.isNotBlank() } // single quotes
                ?: it.groupValues[5].takeIf { it.isNotBlank() } // double quotes
                ?: error("Invalid path")
            ".$value"
        }
    )

    private fun splitPath() = path.split("[", "]").filter { it.isNotBlank() }

    public fun matches(other: JsonPath): Boolean =
        path == other.path ||
            run {
                val path = splitPath()
                val otherPath = other.splitPath()

                if (path.size != otherPath.size) false
                else path.zip(otherPath).all { (p, o) ->
                    p == o || p == "*" || o == "*"
                }
            }
}

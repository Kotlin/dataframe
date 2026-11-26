package org.jetbrains.kotlinx.dataframe.samples.guides

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.abs

/**
 * The Kotlin half of a `pandas-*-cases.json` file. The Pandas half is `pandas-validation/validate_pandas.py`.
 *
 * A case has an `id`, a Pandas expression, the value it must give (`expect`) and, when Kotlin DataFrame gives another
 * value on the same data, `expectKotlin`. Expected values are taken from real runs of each side, never from one
 * side compared with the other.
 */
internal object PandasCases {

    /**
     * Runs [kotlinSide] (one lambda per case id) against the cases in [resource] and returns what went wrong:
     * an empty list means every case holds.
     */
    fun check(resource: String, kotlinSide: Map<String, () -> Any?>): List<String> {
        val text = PandasCases::class.java.getResource(resource)!!.readText()
        val cases = Json.parseToJsonElement(text).jsonArray.map { it.jsonObject }
        val ids = cases.map { it.getValue("id").jsonPrimitive.content }

        // No case without a Kotlin counterpart, and no counterpart without a case.
        val onlyInFile = ids - kotlinSide.keys
        val onlyInCode = kotlinSide.keys - ids.toSet()
        if (onlyInFile.isNotEmpty() || onlyInCode.isNotEmpty()) {
            return listOf("case ids differ: only in $resource: $onlyInFile, only in the Kotlin code: $onlyInCode")
        }

        return cases.mapNotNull { case ->
            val id = case.getValue("id").jsonPrimitive.content
            val expected = (case["expectKotlin"] ?: case.getValue("expect")).plain()
            val actual = kotlinSide.getValue(id)()
            if (same(expected, actual)) null else "$id: expected $expected but was $actual"
        }
    }

    private fun JsonElement.plain(): Any? =
        when (this) {
            is JsonArray -> map { it.plain() }
            is JsonNull -> null
            is JsonPrimitive -> if (isString) content else content.toDouble()
            else -> error("objects are not used in the cases files")
        }

    /** Numbers compare as doubles, lists element by element. */
    private fun same(expected: Any?, actual: Any?): Boolean =
        when {
            expected is List<*> && actual is List<*> ->
                expected.size == actual.size && expected.indices.all { same(expected[it], actual[it]) }

            expected is Double && actual is Number -> abs(expected - actual.toDouble()) < 1e-9

            else -> expected == actual
        }
}

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

    /** One case as the Kotlin side sees it: its [id] and the value Kotlin DataFrame must give. */
    class Case(val id: String, val expected: Any?)

    fun load(resource: String): List<Case> {
        val text = PandasCases::class.java.getResource(resource)!!.readText()
        return Json.parseToJsonElement(text).jsonArray.map { it.jsonObject }.map { case ->
            Case(
                id = case.getValue("id").jsonPrimitive.content,
                expected = (case["expectKotlin"] ?: case.getValue("expect")).plain(),
            )
        }
    }

    /** Runs [kotlinSide] and returns `null` when [case] holds, otherwise a sentence that says what went wrong. */
    fun failure(case: Case, kotlinSide: (() -> Any?)?): String? {
        if (kotlinSide == null) return "${case.id}: there is no Kotlin counterpart for this case"
        val actual = try {
            kotlinSide()
        } catch (e: Exception) {
            return "${case.id}: the Kotlin side threw $e"
        }
        return if (same(case.expected, actual)) null else "${case.id}: expected ${case.expected} but was $actual"
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

package org.jetbrains.kotlinx.dataframe.lint

import com.lemonappdev.konsist.api.Konsist
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import org.junit.Test

class PivotApiLintTest {

    private val pivotReceivers = setOf(
        "Pivot",
        "ReducedPivot",
        "PivotGroupBy",
        "ReducedPivotGroupBy",
    )

    private val pivotGroupByReceivers = setOf(
        "PivotGroupBy",
        "ReducedPivotGroupBy",
    )

    private val pivotSpecialNames = setOf(
        "pivotMatches",
        "pivotCounts",
    )

    private fun isDeprecatedAccessApi(
        function: com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration,
    ): Boolean =
        function.hasAnnotation { it.name == "Deprecated" } ||
            function.hasAnnotation { it.name == "AccessApiOverload" }

    @Test
    fun `all public pivot terminal operations must return wildcard DataFrame or DataRow`() {
        val corePublicFunctions = Konsist.scopeFromDirectories(listOf("core"))
            .functions()
            .filter { !it.path.contains("generated") }
            .filter { it.hasPublicModifier }

        val pivotFunctions = corePublicFunctions.filter { function ->
            val receiverName = function.receiverType?.name?.substringBefore("<")
            val isPivotReceiver = receiverName in pivotReceivers
            val isPivotSpecial = function.name in pivotSpecialNames
            isPivotReceiver || isPivotSpecial
        }

        // Sanity check: Ensure we actually found all pivot operations in core
        pivotFunctions.size shouldBeGreaterThan 300

        val invalidPivotFunctions = pivotFunctions.filter { function ->
            val returnTypeName = function.returnType?.name ?: ""
            val returnTypeBase = returnTypeName.substringBefore("<")
            if (returnTypeBase in setOf("DataFrame", "DataRow")) {
                val isWildcard =
                    returnTypeName == "DataFrame<*>" ||
                        returnTypeName == "DataRow<*>" ||
                        returnTypeName.contains("<*>")
                !isWildcard
            } else {
                false
            }
        }.map { function ->
            val file = function.containingFile.name
            val name = function.name
            val receiver = function.receiverType?.name
            val ret = function.returnType?.name
            "$file: $name with receiver $receiver returns $ret"
        }

        invalidPivotFunctions.shouldBeEmpty()
    }

    @Test
    fun `all public PivotGroupBy functions returning DataFrame must have Refine annotation`() {
        val corePublicFunctions = Konsist.scopeFromDirectories(listOf("core"))
            .functions()
            .filter { !it.path.contains("generated") }
            .filter { it.hasPublicModifier }
            .filter { !isDeprecatedAccessApi(it) }

        val targetFunctions = corePublicFunctions.filter { function ->
            val receiverName = function.receiverType?.name?.substringBefore("<")
            val isPivotGroupByReceiver = receiverName in pivotGroupByReceivers
            val isPivotSpecial = function.name in pivotSpecialNames &&
                (receiverName == "DataFrame" || receiverName == "GroupBy")
            val returnsDataFrame = function.returnType?.name?.substringBefore("<") == "DataFrame"

            (isPivotGroupByReceiver || isPivotSpecial) && returnsDataFrame
        }

        targetFunctions.size shouldBeGreaterThan 20

        val missingRefine = targetFunctions.filter { function ->
            !function.hasAnnotation { it.name == "Refine" }
        }.map { function ->
            "${function.containingFile.name}: ${function.name} with receiver ${function.receiverType?.name}"
        }

        missingRefine.shouldBeEmpty()
    }

    @Test
    fun `all public pivot chain operations must have Interpretable or StringApiInterpretable annotation`() {
        val corePublicFunctions = Konsist.scopeFromDirectories(listOf("core"))
            .functions()
            .filter { !it.path.contains("generated") }
            .filter { it.hasPublicModifier }
            .filter { !isDeprecatedAccessApi(it) }

        val targetFunctions = corePublicFunctions.filter { function ->
            val receiverName = function.receiverType?.name?.substringBefore("<")
            val isPivotGroupByReceiver = receiverName in pivotGroupByReceivers
            val isPivotSpecial = function.name in pivotSpecialNames &&
                (receiverName == "DataFrame" || receiverName == "GroupBy")
            val isEntryPivot = (receiverName == "DataFrame" || receiverName == "GroupBy") &&
                function.name == "pivot"
            val isPivotGrouping = receiverName == "Pivot" &&
                (function.name == "groupBy" || function.name == "groupByOther")

            val isSingleStringColumnShortcut = function.parameters.any {
                it.name == "column" && it.type.name == "String"
            }

            if (isSingleStringColumnShortcut) {
                false
            } else {
                isPivotSpecial || isEntryPivot || isPivotGrouping || (
                    isPivotGroupByReceiver && (
                        function.returnType?.name?.substringBefore("<") in setOf("DataFrame", "ReducedPivotGroupBy")
                    )
                )
            }
        }

        targetFunctions.size shouldBeGreaterThan 30

        val missingInterpretable = targetFunctions.filter { function ->
            val hasInterpretable = function.hasAnnotation { it.name == "Interpretable" }
            val hasStringApi = function.hasAnnotation { it.name == "StringApiInterpretable" }
            !hasInterpretable && !hasStringApi
        }.map { function ->
            val file = function.containingFile.name
            val name = function.name
            val params = function.parameters.joinToString { p -> "${p.name}: ${p.type.name}" }
            val receiver = function.receiverType?.name
            "$file: $name($params) with receiver $receiver"
        }

        missingInterpretable.shouldBeEmpty()
    }
}

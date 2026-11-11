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

    private val pivotSpecialNames = setOf(
        "pivotMatches",
        "pivotCounts",
    )

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
                    returnTypeName == "DataFrame<*>" || returnTypeName == "DataRow<*>" || returnTypeName.contains("<*>")
                !isWildcard
            } else {
                false
            }
        }.map { function ->
            "${function.containingFile.name}: ${function.name} with receiver ${function.receiverType?.name} returns ${function.returnType?.name}"
        }

        invalidPivotFunctions.shouldBeEmpty()
    }
}

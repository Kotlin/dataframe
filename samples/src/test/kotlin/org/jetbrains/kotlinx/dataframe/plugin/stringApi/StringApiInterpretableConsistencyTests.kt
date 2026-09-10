package org.jetbrains.kotlinx.dataframe.plugin.stringApi

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.inward
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.util.DataFrameApi
import org.jetbrains.kotlinx.dataframe.util.annotationArguments
import org.jetbrains.kotlinx.dataframe.util.annotations
import org.jetbrains.kotlinx.dataframe.util.dataFrameApi
import org.jetbrains.kotlinx.dataframe.util.interpretableFunctions
import org.jetbrains.kotlinx.dataframe.util.interpreter
import org.jetbrains.kotlinx.dataframe.util.name
import org.jetbrains.kotlinx.dataframe.util.parameters
import org.jetbrains.kotlinx.dataframe.util.stringApiFunctions
import org.junit.Ignore
import org.junit.Test

@Ignore
class StringApiInterpretableConsistencyTests {
    @Test
    fun `check all StringApiInterpretable map string API overload parameter to valid CS DSL parameter`() {
        val dataFrameApi = dataFrameApi()
        val csDslInterpretable = dataFrameApi.interpretableFunctions()
            .filter {
                parameters.any {
                    it.type.name.contains("ColumnsSelector") ||
                        it.type.name.contains("ColumnsForAggregateSelector") ||
                        it.type.name.contains("ColumnSelector")
                }
            }

        val stringOverloads = dataFrameApi
            .select { annotations and name and parameters }
            .cast<DataFrameApi>(verify = false)
            .stringApiFunctions()
            .split { annotationArguments }.inward("delegateInterpreter", "stringArgument", "targetArgument")
            .rename { annotationArguments }.to("adapter")

        // we try to make sure not to miss String APIs that need @StringApiInterpretable
        // so, if there's @Interpretable, does it have String API overload?
        // if yes, maybe annotation there should be added.
        val doNotNeedStringApiInterpretableAdapter = setOf(
            "Parse", // own interpreter
            "Convert0", // own interpreter
            "Select0", // own interpreter
            "Under0", // own interpreter Under4
            "DataFrameXs", // no String overload at all
            "GroupByXs", // no String overload at all
            "NestedSelect", // no String overload
            "StringSelect", // no String overload
            "ColumnPathSelect", // no String overload
            "CSDslAllExceptSelector", // own interpreter CSDslAllExceptStrings
            "ColumnGroupAllColsExceptSelector", // own interpreter ColumnGroupAllColsExceptStrings
            "ColumnGroupExceptSelector", // own interpreter ColumnGroupExceptStrings
            "StringExceptSelector", // own interpreter StringExceptStrings
            "ColumnPathExceptSelector", // own interpreter ColumnPathExceptStrings
            "StringAllColsExceptSelector", // own interpreter StringAllColsExceptStrings
            "ColumnPathAllColsExceptSelector", // own interpreter ColumnPathAllColsExceptStrings
            "GroupByCountDistinct0", // no String overload
            "AllAfter1", // need own interpreter
            "AllFrom1", // need own interpreter
            "AllBefore1", // need own interpreter
            "AllUpTo1", // need own interpreter
            "MoveAfter0", // need investigate and fix
            "MoveBefore0", // need investigate and fix
            "InsertAfter0", // need investigate and fix
            "InsertBefore0", // need investigate and fix
            "AsGroupBy", // need investigate and fix
            "Require0", // no String overload
        )

        val stringApiGroup = stringOverloads.group { all() }.into("stringOverload")
        val remainingInconsistentApis = csDslInterpretable
            .leftJoin(stringApiGroup) { interpreter.match(right.stringOverload.adapter.delegateInterpreter) }
            // any invalid mapping?
            .filter {
                val parametersOfCslDslOverload = parameters.map { it.name }
                stringOverload.adapter.targetArgument !in parametersOfCslDslOverload
            }
            .filter { interpreter !in doNotNeedStringApiInterpretableAdapter }

        remainingInconsistentApis.asClue {
            remainingInconsistentApis.rowsCount() shouldBe 0
        }
    }
}

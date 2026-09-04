package org.jetbrains.kotlinx.dataframe.plugin.stringApi

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoParameterDeclaration
import com.lemonappdev.konsist.api.declaration.KoTypeParameterDeclaration
import com.lemonappdev.konsist.api.declaration.type.KoTypeDeclaration
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.inward
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with
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

        val ignore = setOf(
            "Parse", // own interpreter
            "Convert0", // own interpreter
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
            .filter { interpreter !in ignore }

        remainingInconsistentApis.asClue {
            remainingInconsistentApis.rowsCount() shouldBe 0
        }
    }

    private fun dataFrameApi(): DataFrame<DataFrameApi> {
        val scope = Konsist.scopeFromDirectories(listOf("core"))
            .functions()
            .filter { !it.path.contains("generated") }

        return scope.toDataFrame()
            .filter { it.hasPublicModifier }
            .filter { !annotations.any { it.name in setOf("Deprecated", "AccessApiOverload") } }
            .select {
                cols(
                    receiverType,
                    name,
                    parameters,
                    returnType,
                    annotations,
                    projectPath,
                    isTopLevel,
                    typeParameters,
                )
            }
            .convert { projectPath }.with { it.removePrefix("projectPath: ") }
            .cast<DataFrameApi>()
    }

    @DataSchema
    data class DataFrameApi(
        val receiverType: KoTypeDeclaration?,
        val name: String,
        val parameters: List<KoParameterDeclaration>,
        val returnType: KoTypeDeclaration?,
        val annotations: List<KoAnnotationDeclaration>,
        val projectPath: String,
        val isTopLevel: Boolean,
        val typeParameters: List<KoTypeParameterDeclaration>,
    )

    private fun DataFrame<DataFrameApi>.interpretableFunctions(): DataFrame<InterpretableFunctions> =
        select {
            annotations and receiverType and name and parameters and returnType
        }
            .filter { annotations.any { it.name == "Interpretable" } }
            .insert("interpreter") {
                annotations
                    .single { it.name == "Interpretable" }
                    .arguments.single().value
            }.at(0)
            .cast<InterpretableFunctions>()

    @DataSchema
    data class InterpretableFunctions(
        val interpreter: String?,
        val annotations: List<KoAnnotationDeclaration>,
        val receiverType: KoTypeDeclaration?,
        val name: String,
        val parameters: List<KoParameterDeclaration>,
        val returnType: KoTypeDeclaration?,
    )

    private fun DataFrame<DataFrameApi>.stringApiFunctions(): DataFrame<StringApiFunctions> =
        insert("annotationArguments") {
            annotations.singleOrNull { it.name == "StringApiInterpretable" }
                ?.arguments?.map { it.value }
        }.at(0)
            .dropNulls { annotationArguments }
            .cast<StringApiFunctions>()

    @DataSchema
    data class StringApiFunctions(
        val annotationArguments: List<String?>,
        val receiverType: KoTypeDeclaration?,
        val name: String,
        val parameters: List<KoParameterDeclaration>,
        val returnType: KoTypeDeclaration?,
        val annotations: List<KoAnnotationDeclaration>,
        val projectPath: String,
        val isTopLevel: Boolean,
        val typeParameters: List<KoTypeParameterDeclaration>,
    )
}

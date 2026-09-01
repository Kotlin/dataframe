package org.jetbrains.kotlinx.dataframe

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoParameterDeclaration
import com.lemonappdev.konsist.api.declaration.KoTypeParameterDeclaration
import com.lemonappdev.konsist.api.declaration.type.KoTypeDeclaration
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.inward
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByCount
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.valuesInto
import org.jetbrains.kotlinx.dataframe.api.with
import org.junit.Ignore
import org.junit.Test

@Ignore
class TestStringApiInterpretableConsistency {
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

    @Test
    fun `check all StringApiInterpretable match string API overload parameter to valid CS DSL parameter`() {
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
            .rename { annotationArguments }.into("adapter")

        val ignore = setOf(
            "Parse", // own interpreter
            "Convert0", // own interpreter
            "DataFrameXs", // no String overload at all
            "GroupByXs", // no String overload at all
            "NestedSelect", // no String overload
            "StringSelect", // no String overload
            "ColumnPathSelect", // no String overload
        )

        val stringApiGroup = stringOverloads.group { all() }.into("stringOverload")
        val remainingInconsistentApis = csDslInterpretable
            .leftJoin(stringApiGroup) { interpreter.match(right.stringOverload.adapter.delegateInterpreter) }
            // Default value
            .fillNulls { stringOverload.adapter.targetArgument }.with { stringOverload.adapter.stringArgument }
            .also { println(it.size()) }
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

    @Test
    fun `print Interpretable function headers with String API overloads`() {
        val dataFrameApi = dataFrameApi()
        val csDslHeaders = dataFrameApi.interpretableFunctions()
            .add("csDslHeader") {
                buildString {
                    receiverType?.name?.let { append("$it.") }
                    append(name)
                    append(parameters.joinToString(prefix = "(", postfix = ")") { "${it.name}: ${it.type.name}" })
                    returnType?.name?.let { append(": $it") }
                }
            }
            .select { interpreter and csDslHeader }

        val stringApiHeaders = dataFrameApi.stringApiFunctions()
            .add("interpreter") {
                annotations
                    .single { it.name == "StringApiInterpretable" }
                    .arguments
                    .first()
                    .value
            }
            .add("stringApiHeader") {
                buildString {
                    receiverType?.name?.let { append("$it.") }
                    append(name)
                    append(parameters.joinToString(prefix = "(", postfix = ")") { "${it.name}: ${it.type.name}" })
                    returnType?.name?.let { append(": $it") }
                }
            }
            .select { interpreter and stringApiHeader }

        csDslHeaders
            .innerJoin(stringApiHeaders) { interpreter }
            .sortBy { interpreter }
            .gather { all() }
            .valuesInto("Interpretable ID / CS DSL header / String API header")
            .print(rowsLimit = 1000, valueLimit = 1000, rowIndex = false, columnTypes = false, alignLeft = true)
    }

    @Test
    fun `print StringApiInterpretable functions without Refine when CS DSL has it`() {
        val dataFrameApi = dataFrameApi()
        val csDslInterpretersWithRefine = dataFrameApi.interpretableFunctions()
            .filter { annotations.any { it.name == "Refine" } }
            .select { interpreter }
            .distinct()

        dataFrameApi.stringApiFunctions()
            .filter { annotations.none { it.name == "Refine" } }
            .add("interpreter") {
                annotations
                    .single { it.name == "StringApiInterpretable" }
                    .arguments
                    .first()
                    .value
            }
            .innerJoin(csDslInterpretersWithRefine) { interpreter }
            .add("stringApiHeader") {
                buildString {
                    receiverType?.name?.let { append("$it.") }
                    append(name)
                    append(parameters.joinToString(prefix = "(", postfix = ")") { "${it.name}: ${it.type.name}" })
                    returnType?.name?.let { append(": $it") }
                }
            }
            .select { interpreter and stringApiHeader }
            .sortBy { interpreter }
            .print(rowsLimit = 1000, valueLimit = 1000, rowIndex = false, columnTypes = false, alignLeft = true)
    }

    @Test
    fun print() {
        val dataFrameApi = dataFrameApi()
        val trueInterpretable = dataFrameApi.interpretableFunctions()
        trueInterpretable
            .convert { parameters }.with { it.map { it.type.name } }
            .explode { parameters into "parameterName" }
            .groupBy { parameterName }.sortByCount().count()
            .sortBy {
                expr {
                    parameterName.contains("ColumnsSelector") ||
                        parameterName.contains("ColumnsForAggregateSelector")
                }.desc()
            }
            .print(rowsLimit = 1000)
    }
}

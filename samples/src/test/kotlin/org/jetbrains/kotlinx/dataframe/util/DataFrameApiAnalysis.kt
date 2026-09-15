package org.jetbrains.kotlinx.dataframe.util

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoParameterDeclaration
import com.lemonappdev.konsist.api.declaration.KoTypeParameterDeclaration
import com.lemonappdev.konsist.api.declaration.type.KoTypeDeclaration
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with

/**
 * Make sure to add `import org.jetbrains.kotlinx.dataframe.util.*` for to use generated properties, if needed.
 * https://youtrack.jetbrains.com/issue/KTIJ-35709/Compiler-plugin-generated-properties-are-not-recommended-for-import-in-other-packages
 */
fun dataFrameApi(): DataFrame<DataFrameApi> {
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

fun DataFrame<DataFrameApi>.interpretableFunctions(): DataFrame<InterpretableFunctions> =
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

fun DataFrame<DataFrameApi>.stringApiFunctions(): DataFrame<StringApiFunctions> =
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

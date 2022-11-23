package org.jetbrains.kotlinx.dataframe.io

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import java.io.InputStream
import java.net.URL
import kotlin.reflect.typeOf

/**
 * Used to add `readJson` and `convertToMyMarker` functions to the generated interfaces.
 * Makes sure [convertDataRowsWithOpenApi] is always used in conversions.
 */
internal object DefaultReadOpenApiMethod : AbstractDefaultReadMethod(
    path = null,
    arguments = MethodArguments.EMPTY,
    methodName = "",
) {

    override val additionalImports: List<String> = listOf(
        "import org.jetbrains.kotlinx.dataframe.io.readJson",
        "import org.jetbrains.kotlinx.dataframe.io.readJsonStr",
        "import org.jetbrains.kotlinx.dataframe.api.convertTo",
        "import org.jetbrains.kotlinx.dataframe.api.first",
        "import org.jetbrains.kotlinx.dataframe.api.${JsonPath::class.simpleName}",
        "import org.jetbrains.kotlinx.dataframe.api.${DataSchemaEnum::class.simpleName}",
        "import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.*",
        "import org.jetbrains.kotlinx.dataframe.io.${ConvertSchemaDsl<*>::convertDataRowsWithOpenApi.name}",
    )

    override fun toDeclaration(marker: Marker, visibility: String): String {
        val returnType = DataFrame::class.asClassName().parameterizedBy(ClassName("", listOf(marker.shortName)))

        // convertTo: ConvertSchemaDsl<MyMarker>.() -> Unit = {}
        val convertToParameter = ParameterSpec.builder(
            name = "convertTo",
            type = LambdaTypeName.get(
                receiver = ConvertSchemaDsl::class
                    .asClassName()
                    .parameterizedBy(ClassName("", listOf(marker.shortName))),
                parameters = emptyList(),
                returnType = UNIT,
            ),
        )
            .defaultValue("{}")
            .build()

        fun getConvertMethod(): String = """
            return convertTo<${marker.shortName}> { 
                ${ConvertSchemaDsl<*>::convertDataRowsWithOpenApi.name}() 
                convertTo()
            }
        """.trimIndent()

        fun getReadAndConvertMethod(
            readMethod: String,
        ): String = """
            return ${DataFrame::class.asClassName()}
                .$readMethod${if (marker is OpenApiMarker.AdditionalPropertyInterface) "[\"$valueColumnName\"].first().let { it as DataFrame<*> }" else ""}
                .convertTo${marker.shortName}()
        """.trimIndent()

        val typeSpec = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("convertTo${marker.shortName}")
                    .receiver(DataFrame::class.asClassName().parameterizedBy(STAR))
                    .addParameter(convertToParameter)
                    .addCode(getConvertMethod())
                    .returns(returnType)
                    .build()
            )
            .addProperty(
                PropertySpec.Companion.builder(name = "keyValuePaths", type = typeOf<List<JsonPath>>().asTypeName())
                    .getter(
                        FunSpec.getterBuilder()
                            .addCode(
                                run {
                                    val additionalPropertyPaths = (marker as OpenApiMarker)
                                        .additionalPropertyPaths
                                        .distinct()

                                    "return listOf(${additionalPropertyPaths.joinToString { "JsonPath(\"\"\"${it.path}\"\"\")" }})"
                                }
                            )
                            .build()
                    )
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("url", URL::class)
                    .addCode(getReadAndConvertMethod("readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("path", String::class)
                    .addCode(getReadAndConvertMethod("readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("stream", InputStream::class)
                    .addCode(getReadAndConvertMethod("readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJsonStr")
                    .returns(returnType)
                    .addParameter("text", String::class)
                    .addCode(getReadAndConvertMethod("readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
                    .build()
            )
            .build()

        return typeSpec.toString()
    }
}

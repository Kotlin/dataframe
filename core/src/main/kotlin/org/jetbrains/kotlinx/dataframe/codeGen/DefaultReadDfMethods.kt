package org.jetbrains.kotlinx.dataframe.codeGen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.typeNameOf
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.MethodArguments

public interface DefaultReadDfMethod {
    public fun toDeclaration(marker: Marker, visibility: String): String

    public val additionalImports: List<String>
}

// Used APIs
private const val cast = "cast"
private const val verify = "verify" // cast(true) is obscure, i think it's better to use named argument here
private const val readCSV = "readCSV"
private const val readTSV = "readTSV"
private const val readJson = "readJson"

public abstract class AbstractDefaultReadMethod(
    private val path: String?,
    private val arguments: MethodArguments,
    private val methodName: String,
) : DefaultReadDfMethod {
    override fun toDeclaration(marker: Marker, visibility: String): String {
        val parameters = arguments.defaultValues.map {
            ParameterSpec.builder(it.name, it.property.type)
                .defaultValue("%N", it.property)
                .build()
        }

        val defaultPath = path?.let {
            PropertySpec.builder("defaultPath", typeNameOf<String>(), KModifier.CONST)
                .initializer("%S", path)
                .build()
        }

        val type = DataFrame::class.asClassName().parameterizedBy(ClassName("", listOf(marker.shortName)))

        val arguments = parameters.joinToString(", ") { "${it.name} = ${it.name}" }

        val typeSpec = TypeSpec.companionObjectBuilder()
            .apply {
                if (defaultPath != null) {
                    addProperty(defaultPath)
                }
            }
            .addProperties(this.arguments.defaultValues.map { it.property })
            .addFunction(
                FunSpec.builder(methodName)
                    .returns(type)
                    .addParameter(
                        ParameterSpec.builder("path", typeNameOf<String>())
                            .apply {
                                if (defaultPath != null) {
                                    defaultValue("%N", defaultPath)
                                }
                            }
                            .build()

                    )
                    .addParameters(parameters)
                    .addParameter(
                        ParameterSpec.builder("verify", typeNameOf<Boolean?>())
                            .defaultValue("null")
                            .build()
                    )
                    .addCode(
                        """
                        val df = DataFrame.$methodName(path, $arguments)
                        return if ($verify != null) df.$cast($verify = $verify) else df.$cast()
                        """.trimIndent()
                    )
                    .build()
            )
            .build()

        return typeSpec.toString()
    }

    override val additionalImports: List<String> = listOf("import org.jetbrains.kotlinx.dataframe.io.$methodName")
}

internal class DefaultReadJsonMethod(path: String?, arguments: MethodArguments) : AbstractDefaultReadMethod(
    path = path,
    arguments = arguments,
    methodName = readJson,
)

internal class DefaultReadCsvMethod(
    path: String?,
    arguments: MethodArguments,
) : AbstractDefaultReadMethod(path, arguments, readCSV)

internal class DefaultReadTsvMethod(path: String?) : AbstractDefaultReadMethod(path, MethodArguments.EMPTY, readTSV)

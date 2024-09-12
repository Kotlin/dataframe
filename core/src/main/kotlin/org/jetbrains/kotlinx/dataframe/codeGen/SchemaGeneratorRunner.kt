package org.jetbrains.kotlinx.dataframe.codeGen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.impl.codeGen.toStandaloneSnippet
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.jvm.kotlinFunction

public interface Transformer<I, O> {
    public fun transform(value: I): DataFrame<O>
}

public fun <I, O> transform(f: (I) -> DataFrame<O>): Transformer<I, O> {
    return object : Transformer<I, O> {
        override fun transform(value: I): DataFrame<O> = f(value)
    }
}

internal object SchemaGeneratorRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        // SchemaGeneratorRunner.class.classLoader.loadClass("SchemasKt")
        val src = System.getenv("DATAFRAME_GENERATED_SRC")
        args.forEach { classFile ->
            val cl = this::class.java.classLoader.loadClass(classFile.replace(".class", ""))
            val generators = cl.declaredMethods.filter {
                it.parameterCount == 0 && Modifier.isStatic(it.modifiers)
            }
            val generator = CodeGenerator.create(useFqNames = true)
            generators.forEach { function ->
                when (function.returnType) {
                    DataFrameSchema::class.java -> {
                        val result = try {
                            function.invoke(null) as? DataFrameSchema ?: error("$cl $function")
                        } catch (e: InvocationTargetException) {
                            throw e.targetException
                        }

                        val code = generator.generate(result, "MySchema", true, false, false)
                        val get = File(src, "Schema.kt")
                        get.writeText(code.toStandaloneSnippet("", emptyList()))
                        println(get)
                    }

                    DataFrame::class.java -> {
                        val result = try {
                            function.invoke(null) as DataFrame<*>
                        } catch (e: InvocationTargetException) {
                            throw e.targetException
                        }

                        val factory = object : DefaultReadDfMethod {
                            override val additionalImports: List<String> = listOf(function.name)

                            override fun toDeclaration(marker: Marker, visibility: String): String {
                                val type = DataFrame::class.asClassName()
                                    .parameterizedBy(ClassName(packageName = "", listOf(marker.shortName)))

                                val format =
                                    """
                                    val df = ${function.name}()
                                    return df.cast()
                                    """.trimIndent()
                                val typeSpec = TypeSpec.companionObjectBuilder()
                                    .addFunction(
                                        FunSpec.builder("sample")
                                            .returns(type)
                                            .addCode(format)
                                            .build()
                                    ).build()
                                return typeSpec.toString()
                            }
                        }
                        val className = function.name.replaceFirstChar { it.uppercase() }
                        val code = generator
                            .generate(result.schema(), className, true, false, false, readDfMethod = factory)
                            .toStandaloneSnippet("dataframe", listOf("import ${function.name}"))
                        File(src, "$className.kt").writeText(code)

                        println("Result from ${function.name}: $result")
                    }

                    DataRow::class.java -> {
                        val result = try {
                            function.invoke(null) as DataRow<*>
                        } catch (e: InvocationTargetException) {
                            throw e.targetException
                        }

                        val factory = object : DefaultReadDfMethod {
                            override val additionalImports: List<String> = listOf(function.name)

                            override fun toDeclaration(marker: Marker, visibility: String): String {
                                val type = DataRow::class.asClassName()
                                    .parameterizedBy(ClassName(packageName = "", listOf(marker.shortName)))

                                val format =
                                    """
                                    val df = ${function.name}()
                                    return df.cast()
                                    """.trimIndent()
                                val typeSpec = TypeSpec.companionObjectBuilder()
                                    .addFunction(
                                        FunSpec.builder("sample")
                                            .returns(type)
                                            .addCode(format)
                                            .build()
                                    ).build()
                                return typeSpec.toString()
                            }
                        }
                        val className = function.name.replaceFirstChar { it.uppercase() }
                        val code = generator
                            .generate(result.schema(), className, true, false, false, readDfMethod = factory)
                            .toStandaloneSnippet("dataframe", listOf("import ${function.name}"))
                        File(src, "$className.kt").writeText(code)

                        println("Result from ${function.name}: $result")
                    }

                    Transformer::class.java -> {
                        val classifier =
                            (function.kotlinFunction!!.returnType.arguments[1].type?.classifier as? KClass<*>)!!

                        val classifier0 =
                            (function.kotlinFunction!!.returnType.arguments[0].type?.classifier as? KClass<*>)!!

                        val factory = object : DefaultReadDfMethod {
                            override val additionalImports: List<String> = listOf(function.name)

                            override fun toDeclaration(marker: Marker, visibility: String): String {
                                val type = DataFrame::class.asClassName()
                                    .parameterizedBy(ClassName(packageName = "", listOf(marker.shortName)))
                                val name = classFile.replace(".class", "")
                                val format =
                                    """
                                    val factory = this::class.java.classLoader.loadClass("$name").getDeclaredMethod("${function.name}")
                                    factory.isAccessible = true
                                    return ((factory.invoke(null) as Transformer<${classifier0.simpleName}, *>).transform(v) as DataFrame<*>).cast()
                                    """.trimIndent()
                                val typeSpec = TypeSpec.companionObjectBuilder()
                                    .addFunction(
                                        FunSpec.builder("convert")
                                            .addParameter(
                                                ParameterSpec.builder("v", classifier0).build()
                                            )
                                            .returns(type)
                                            .addCode(format)
                                            .build()
                                    ).build()
                                return typeSpec.toString()
                            }
                        }
                        val marker = MarkersExtractor.get(classifier)
                        val className = function.name.replaceFirstChar { it.uppercase() }
                        val code = generator
                            .generate(marker.schema, className, true, false, false, readDfMethod = factory)
                            .toStandaloneSnippet(
                                "dataframe",
                                listOf("import org.jetbrains.kotlinx.dataframe.codeGen.Transformer")
                            )
                        File(src, "$className.kt").writeText(code)
                        println("Result from ${function.name}: ${marker.schema}")
                    }
                }
            }
        }
    }
}

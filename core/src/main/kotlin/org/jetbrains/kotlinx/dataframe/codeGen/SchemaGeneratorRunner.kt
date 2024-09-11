package org.jetbrains.kotlinx.dataframe.codeGen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
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

internal object SchemaGeneratorRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        // SchemaGeneratorRunner.class.classLoader.loadClass("SchemasKt")
        val src = System.getenv("DATAFRAME_GENERATED_SRC")
        args.forEach {
            val cl = this::class.java.classLoader.loadClass(it.replace(".class", ""))
            val generators = cl.methods.filter {
                it.parameterCount == 0 /*&& it.returnType == String::class.java*/ && Modifier.isStatic(it.modifiers)
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

                            override fun toDeclaration(
                                marker: Marker,
                                visibility: String
                            ): String {
                                val type =
                                    DataFrame::class.asClassName().parameterizedBy(ClassName(packageName = "", listOf(marker.shortName)))
                                val format = """
                            val df = ${function.name}()
                            return df.cast()
                            """.trimIndent()
                                val typeSpec = TypeSpec.companionObjectBuilder()
                                    .addFunction(
                                        FunSpec.builder("sample")
                                            .returns(type)
                                            .addCode(format)
                                            .build(),
                                    ).build()
                                return typeSpec.toString()
                            }
                        }
                        val className = function.name.replaceFirstChar { it.uppercase() }
                        val code = generator.generate(result.schema(), className, true, false, false, readDfMethod = factory)
                        File(src, "$className.kt").writeText(code.toStandaloneSnippet("dataframe", listOf("import ${function.name}")))

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

                            override fun toDeclaration(
                                marker: Marker,
                                visibility: String
                            ): String {
                                val type =
                                    DataRow::class.asClassName().parameterizedBy(ClassName(packageName = "", listOf(marker.shortName)))
                                val format = """
                            val df = ${function.name}()
                            return df.cast()
                            """.trimIndent()
                                val typeSpec = TypeSpec.companionObjectBuilder()
                                    .addFunction(
                                        FunSpec.builder("sample")
                                            .returns(type)
                                            .addCode(format)
                                            .build(),
                                    ).build()
                                return typeSpec.toString()
                            }
                        }
                        val className = function.name.replaceFirstChar { it.uppercase() }
                        val code = generator.generate(result.schema(), className, true, false, false, readDfMethod = factory)
                        File(src, "$className.kt").writeText(code.toStandaloneSnippet("dataframe", listOf("import ${function.name}")))

                        println("Result from ${function.name}: $result")
                    }
                }
            }
        }
    }
}

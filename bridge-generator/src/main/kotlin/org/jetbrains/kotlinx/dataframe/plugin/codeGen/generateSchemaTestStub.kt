package org.jetbrains.kotlinx.dataframe.plugin.codeGen

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.bridges
import org.jetbrains.kotlinx.dataframe.plugin.generateSchemaDeclaration
import org.jetbrains.kotlinx.dataframe.plugin.generateTestCode
import org.jetbrains.kotlinx.dataframe.plugin.model.name
import org.jetbrains.kotlinx.dataframe.plugin.model.type
import org.jetbrains.kotlinx.dataframe.plugin.pluginSchema
import java.util.*

fun KotlinTypeFacade.generateSchemaTestStub(name: String, expression: () -> DataFrame<*>): PluginDataFrameSchema {
        val approximation by column<String>()
        fun writeInterpreter(s: String) {
            // val root = File("/home/nikitak/IdeaProjects/dataframe/core/src/main")
//            val schemaRender = File(root, "kotlin/org/jetbrains/kotlinx/dataframe/plugin/testing/schemaRender").also { it.mkdirs() }
//            File(schemaRender, "$name.kt").writeText(s)
            println(s)
        }

        fun writeTestStub(s: String) {
            // val root = File("/home/nikitak/Downloads/kotlin/plugins/kotlin-dataframe")
//            val schemaRender = File(root, "testData/diagnostics/schemaRender").also { it.mkdirs() }
//            File(schemaRender, "$name.kt")
            println(s)
        }

        val capitalizedName = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

//        val repl = object : JupyterReplTestCase() {
//        }
        val df = expression()
//        val df = repl.execRaw("val df = $expression; df") as DataFrame<*>
        val declaration = df.generateSchemaDeclaration(capitalizedName)
        val schemaTestCode = df.generateTestCode()
        //repl.exec(schemaTestCode)

        val pluginSchema = df.pluginSchema()
        //val jsonString = pluginSchema.toJson()


        bridges.first { it.type.name == "DataFrame<T>" }.run {
            writeInterpreter("""
                        package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender
                        
                        import kotlinx.serialization.decodeFromString
                        import org.jetbrains.kotlinx.dataframe.DataFrame
                        import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
                        import org.jetbrains.kotlinx.dataframe.annotations.Arguments
                        import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
                        import org.jetbrains.kotlinx.dataframe.plugin.*
                        
                        @Interpretable(${capitalizedName}::class)
                        public fun $name(): DataFrame<*> {
                            return TODO("won't run")
                        }
                        
                        public class ${capitalizedName} : AbstractInterpreter<$approximation>() {
                            override fun Arguments.interpret(): $approximation {
                                return SchemaData.$name()
                            }
                        }
                """.trimIndent())
        }
        val schemaDeclaration = declaration.lineSequence().joinToString("\n|")
        writeTestStub("""
                |import org.jetbrains.kotlinx.dataframe.*
                |import org.jetbrains.kotlinx.dataframe.api.*
                |import org.jetbrains.kotlinx.dataframe.annotations.*
                |import org.jetbrains.kotlinx.dataframe.plugin.testing.*
                |import org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender.*
                |
                |/*
                |$schemaDeclaration
                |*/
                |
                |internal fun schemaTest() {
                |    val df = $name()
                |    ${schemaTestCode}
                |}
            """.trimMargin())
        
        return pluginSchema
    }

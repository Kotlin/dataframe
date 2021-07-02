package org.jetbrains.dataframe.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.internal.schema.extractSchema
import org.jetbrains.dataframe.io.read
import java.io.File
import java.net.URL
import java.nio.file.Paths

abstract class GenerateDataSchemaTask : DefaultTask() {

    @get:Input
    abstract val data: Property<Any>

    @get:Input
    abstract val src: Property<File>

    @get:Input
    abstract val interfaceName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputFile
    val dataSchema = packageName.zip(interfaceName) { packageName, interfaceName ->
        val packagePath = packageName.replace('.', File.separatorChar)
        Paths.get(src.get().absolutePath, packagePath, "$interfaceName.kt").toFile()
    }

    @TaskAction
    fun generate() {
        val codeGenerator = CodeGenerator.create()
        val dataSchema = dataSchema.orNull ?: return
        val df = try {
            when (val data = data.orNull) {
                is File -> DataFrame.read(data)
                is URL -> DataFrame.read(data)
                else -> throw NotImplementedError()
            }
        } catch (e: Exception) {
            println(e)
            throw e
        }
        val codeGenResult = codeGenerator.generate(
            schema = df.extractSchema(),
            name = interfaceName.get(),
            fields = true,
            extensionProperties = true,
            isOpen = false
        )
        dataSchema.writeText("""
            // GENERATED. DO NOT EDIT MANUALLY
            package ${packageName.get()}
            
            ${codeGenResult.code.declarations}
        """.trimIndent())
    }
}

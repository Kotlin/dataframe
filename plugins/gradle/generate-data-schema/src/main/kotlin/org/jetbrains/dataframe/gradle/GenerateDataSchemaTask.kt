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
import java.io.IOException
import java.net.URL
import java.nio.file.Paths
import com.beust.klaxon.KlaxonException
import org.jetbrains.dataframe.AnyFrame
import org.jetbrains.dataframe.impl.codeGen.CodeGenResult
import java.io.FileNotFoundException

abstract class GenerateDataSchemaTask : DefaultTask() {

    @get:Input
    abstract val data: Property<Any>

    @get:Input
    abstract val src: Property<File>

    @get:Input
    abstract val interfaceName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val generateExtensionProperties: Property<Boolean>

    @get:OutputFile
    val dataSchema = packageName.zip(interfaceName) { packageName, interfaceName ->
        val packagePath = packageName.replace('.', File.separatorChar)
        Paths.get(src.get().absolutePath, packagePath, "Generated$interfaceName.kt").toFile()
    }

    @TaskAction
    fun generate() {
        val df = readDataFrame(data.get())
        val codeGenerator = CodeGenerator.create()
        val codeGenResult = codeGenerator.generate(
            schema = df.extractSchema(),
            name = interfaceName.get(),
            fields = true,
            extensionProperties = generateExtensionProperties.get(),
            isOpen = false
        )
        val escapedPackageName = escapePackageName(packageName.get())

        val dataSchema = dataSchema.get()
        dataSchema.writeText(buildSourceFileContent(escapedPackageName, codeGenResult))
    }

    private fun escapePackageName(packageName: String): String {
        // See RegexExpectationsTest
        return if (packageName.isNotEmpty()) {
            packageName.split(NameChecker.PACKAGE_IDENTIFIER_DELIMITER)
                .joinToString(".") { part -> "`$part`" }
        } else {
            packageName
        }
    }

    private fun readDataFrame(data: Any): AnyFrame {
        return try {
            when (data) {
                is File -> DataFrame.read(data)
                is URL -> DataFrame.read(data)
                is String -> DataFrame.read(data)
                else -> throw IllegalArgumentException("data for schema \"${interfaceName.get()}\" must be File, URL or String")
            }
        } catch (e: Exception) {
            when (e) {
                is KlaxonException, is IndexOutOfBoundsException, is IOException -> throw InvalidDataException(e)
                is FileNotFoundException, is IllegalArgumentException -> throw MissingDataException(e)
                else -> throw e
            }
        }
    }

    private fun buildSourceFileContent(escapedPackageName: String, codeGenResult: CodeGenResult): String {
        return buildString {
            appendLine("""
                @file:Suppress(
                    "RemoveRedundantBackticks", 
                    "RemoveRedundantQualifierName", 
                    "unused", "ObjectPropertyName", 
                    "UNCHECKED_CAST", "PropertyName",
                    "ClassName"
                )
                """.trimIndent()
            )

            if (escapedPackageName.isNotEmpty()) {
                appendLine("package $escapedPackageName")
                appendLine()
            }
            appendLine("import org.jetbrains.dataframe.annotations.*")
            appendLine()
            appendLine("// GENERATED. DO NOT EDIT MANUALLY")
            appendLine(codeGenResult.code.declarations)
        }
    }
}

class MissingDataException(cause: Exception) : Exception(cause)
class InvalidDataException(cause: Exception) : Exception(cause)

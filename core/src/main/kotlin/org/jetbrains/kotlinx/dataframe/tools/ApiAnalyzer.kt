package org.jetbrains.kotlinx.dataframe.tools

import com.intellij.openapi.util.Disposer
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

/**
 * Data class representing analysis results for a single function
 */
public data class FunctionInfo(
    public val name: String,
    public val receiver: String?,
    public val hasKDoc: Boolean,
    public val hasCoreTests: Boolean,
    public val hasDocumentation: Boolean,
    public val hasCompilerSupport: Boolean?,
)

/**
 * Data class representing a group of related functions
 */
public data class FunctionGroup(
    public val fileName: String,
    public val name: String,
    public val receiverAndName: String,
    public val functions: List<FunctionInfo>,
    public val fullyQualifiedPath: String,
)

/**
 * Main class for analyzing public API functions
 */
public class ApiAnalyzer {
    private val projectRoot = Paths.get(System.getProperty("user.dir")).parent.also {
        System.err.println("[DEBUG_LOG] Project root: ${it.toAbsolutePath()}")
    }
    private val coreSourceDir = projectRoot.resolve("core/src/main/kotlin").also {
        System.err.println("[DEBUG_LOG] Core source directory: ${it.toAbsolutePath()}")
    }
    private val coreTestDir = projectRoot.resolve("core/src/test").also {
        System.err.println("[DEBUG_LOG] Core test directory: ${it.toAbsolutePath()}")
    }
    private val docsDir = projectRoot.resolve("docs").also {
        System.err.println("[DEBUG_LOG] Docs directory: ${it.toAbsolutePath()}")
        System.err.println("[DEBUG_LOG] Docs directory exists: ${Files.exists(it)}")
        if (Files.exists(it)) {
            System.err.println("[DEBUG_LOG] Docs directory content: ${Files.list(it).count()} files")
            Files.list(it).forEach { file ->
                System.err.println("[DEBUG_LOG] Found doc file: ${file.fileName}")
            }
        }
    }
    private val outputDir = projectRoot.resolve(".junie")

    private val environment: KotlinCoreEnvironment by lazy {
        val configuration = CompilerConfiguration().apply {
            put(
                CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
                PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false),
            )
        }
        KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES,
        )
    }

    private fun scanKotlinFiles(): List<Path> {
        val files = Files.walk(coreSourceDir)
            .filter { it.isRegularFile() && it.extension == "kt" }
            .collect(Collectors.toList())

        println("[DEBUG_LOG] Found ${files.size} Kotlin files to analyze")
        System.out.flush()

        return files
    }

    private fun parseKotlinFile(file: Path): KtFile {
        try {
            val content = file.readText()
            val psiFactory = KtPsiFactory(environment.project)
            return psiFactory.createFile(
                file.fileName.toString(),
                content,
            )
        } catch (e: Exception) {
            System.err.println("[DEBUG_LOG] Error parsing file ${file.fileName}: ${e.message}")
            throw IllegalStateException("Failed to parse Kotlin file: ${file.fileName}", e)
        }
    }

    private fun KtModifierListOwner.isPublic(): Boolean {
        val modifierList = modifierList

        // If no modifier list, it's public by default in Kotlin
        if (modifierList == null) {
            return true
        }

        // Check if explicitly public or implicitly public (no visibility modifier)
        val isExplicitlyPublic = modifierList.hasModifier(KtTokens.PUBLIC_KEYWORD)
        val isImplicitlyPublic = !modifierList.hasModifier(KtTokens.PRIVATE_KEYWORD) &&
            !modifierList.hasModifier(KtTokens.PROTECTED_KEYWORD) &&
            !modifierList.hasModifier(KtTokens.INTERNAL_KEYWORD)

        return isExplicitlyPublic || isImplicitlyPublic
    }

    /**
     * Scans all Kotlin files in the core module and analyzes public API functions
     */
    public fun analyzePublicApi(): List<FunctionGroup> {
        // Check if source directory exists
        if (!Files.exists(coreSourceDir)) {
            throw IllegalStateException("Core source directory does not exist: ${coreSourceDir.toAbsolutePath()}")
        }

        // Create output directory if it doesn't exist
        Files.createDirectories(outputDir)

        val functionGroups = mutableMapOf<Pair<String, String>, MutableList<FunctionInfo>>()

        // Start analysis
        println("[DEBUG_LOG] Starting API analysis...")

        // Scan and analyze all Kotlin files
        val kotlinFiles = scanKotlinFiles()
        println("[DEBUG_LOG] Found ${kotlinFiles.size} Kotlin files")
        for (file in kotlinFiles) {
            println("[DEBUG_LOG] Processing file: ${file.fileName}")
            println("[DEBUG_LOG] File content length: ${file.readText().length} bytes")
            val ktFile = parseKotlinFile(file)
            val fileName = file.fileName.toString()
            println("[DEBUG_LOG] Parsed KtFile: $fileName")

            // Extract public functions using visitor pattern
            ktFile.accept(object : KtVisitorVoid() {
                override fun visitKtFile(file: KtFile) {
                    file.declarations.forEach { it.accept(this) }
                }

                override fun visitClass(klass: KtClass) {
                    klass.declarations.forEach { it.accept(this) }
                }

                override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
                    declaration.declarations.forEach { it.accept(this) }
                }

                override fun visitNamedFunction(function: KtNamedFunction) {
                    if (function.isPublic()) {
                        val functionName = function.name ?: return
                        val receiverType = function.receiverTypeReference?.text
                        val key = Pair(fileName, "${receiverType?.plus(".") ?: ""}$functionName")

                        val functionInfo = FunctionInfo(
                            name = functionName,
                            receiver = receiverType,
                            hasKDoc = checkKDoc(function),
                            hasCoreTests = checkTests(function),
                            hasDocumentation = checkDocumentation(function),
                            hasCompilerSupport = checkCompilerSupport(function),
                        )

                        val group = functionGroups.getOrPut(key) { mutableListOf() }
                        group.add(functionInfo)
                    }
                    super.visitNamedFunction(function)
                }
            })
        }

        // Convert map to list of FunctionGroup
        val result = functionGroups.map { (key, functions) ->
            val fileName = key.first
            val groupName = fileName.substringBeforeLast(".kt")
            // Find the original file path from kotlinFiles list
            val originalFile = kotlinFiles.find { it.fileName.toString() == fileName }
                ?: throw IllegalStateException("Could not find original file for $fileName")
            val packageName = coreSourceDir.relativize(originalFile).toString()
                .replace(File.separator, ".")
                .substringBeforeLast(".kt")
            val fullyQualifiedPath = "org.jetbrains.kotlinx.dataframe.$packageName"

            FunctionGroup(
                fileName = fileName,
                name = groupName,
                receiverAndName = key.second,
                functions = functions,
                fullyQualifiedPath = fullyQualifiedPath,
            )
        }

        // Print summary
        println("[DEBUG_LOG] Analysis complete:")
        println(
            "[DEBUG_LOG] Found ${result.size} function groups with ${result.sumOf {
                it.functions.size
            }} total functions",
        )
        System.out.flush()

        return result
    }

    /**
     * Checks if a function has KDoc documentation
     */
    private fun checkKDoc(function: KtNamedFunction): Boolean = function.docComment != null

    /**
     * Checks if a function has tests in core:test
     */
    private fun checkTests(function: KtNamedFunction): Boolean {
        val functionName = function.name ?: return false

        try {
            if (!Files.exists(coreTestDir)) {
                return false
            }

            val testFiles = Files.walk(coreTestDir)
                .filter { it.isRegularFile() && it.extension == "kt" }
                .collect(Collectors.toList())

            return testFiles.any { testFile ->
                try {
                    testFile.readText().contains(functionName)
                } catch (e: Exception) {
                    false
                }
            }
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Checks if a function is documented in the writerside website
     */
    private fun checkDocumentation(function: KtNamedFunction): Boolean {
        val functionName = function.name ?: return false
        System.err.println("[DEBUG_LOG] Checking documentation for function: $functionName")

        try {
            if (!Files.exists(docsDir)) {
                println("[DEBUG_LOG] Documentation directory does not exist: $docsDir")
                return false
            }

            val docFiles = Files.walk(docsDir)
                .filter { it.isRegularFile() && (it.extension == "md" || it.extension == "html") }
                .collect(Collectors.toList())

            System.err.println("[DEBUG_LOG] Found ${docFiles.size} documentation files")
            System.err.println("[DEBUG_LOG] Documentation files:")
            docFiles.forEach { file ->
                System.err.println("[DEBUG_LOG]   - ${file.fileName}")
            }

            return docFiles.any { docFile ->
                try {
                    val hasDoc = docFile.readText().contains(functionName)
                    if (hasDoc) {
                        println("[DEBUG_LOG] Documentation found for $functionName in ${docFile.fileName}")
                    }
                    hasDoc
                } catch (e: Exception) {
                    println("[DEBUG_LOG] Error reading documentation file ${docFile.fileName}: ${e.message}")
                    false
                }
            }
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Checks if a function has compiler plugin support
     */
    private fun checkCompilerSupport(function: KtNamedFunction): Boolean? {
        val functionName = function.name ?: return false
        System.err.println("[DEBUG_LOG] Checking compiler support for function: $functionName")
        val annotations = function.annotationEntries
        System.err.println("[DEBUG_LOG] Found ${annotations.size} annotations")

        if (annotations.any { it.shortName?.asString() == "AccessApiOverload" }) {
            println("[DEBUG_LOG] Found AccessApiOverload annotation for $functionName")
            return null
        }

        val hasSupport = annotations.any {
            val name = it.shortName?.asString()
            val isSupported = name == "Refine" || name == "Interpretable"
            if (isSupported) {
                println("[DEBUG_LOG] Found compiler support annotation '$name' for $functionName")
            }
            isSupported
        }
        println("[DEBUG_LOG] Compiler support for $functionName: $hasSupport")
        return hasSupport
    }

    /**
     * Generates an XLSX report with the analysis results
     */
    public fun generateReport(groups: List<FunctionGroup>) {
        println("[DEBUG_LOG] Generating Excel report...")

        try {
            // Create output directory if it doesn't exist
            Files.createDirectories(outputDir)

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("API Analysis")

            // Create header row
            val headerRow = sheet.createRow(0)
            val headers = listOf(
                "File",
                "Group Name",
                "Fully Qualified Path",
                "Receiver+Name",
                "Has KDoc",
                "Has Tests",
                "Has Documentation",
                "Has Compiler Support",
            )
            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            // Fill data rows
            var rowNum = 1
            groups.forEach { group ->
                val row = sheet.createRow(rowNum++)
                row.createCell(0).setCellValue(group.fileName)
                row.createCell(1).setCellValue(group.name)
                row.createCell(2).setCellValue(group.fullyQualifiedPath)
                row.createCell(3).setCellValue(group.receiverAndName)

                // Calculate aggregated values for the group
                // KDoc must be present on all overloads to be considered documented
                val hasKDoc = group.functions.all { it.hasKDoc }
                // Tests, documentation, and compiler support are considered present if any overload has them
                val hasTests = group.functions.any { it.hasCoreTests }
                val hasDocs = group.functions.any { it.hasDocumentation }
                val hasCompilerSupport = group.functions.any { it.hasCompilerSupport != false }

                row.createCell(4).setCellValue(if (hasKDoc) "Yes" else "No")
                row.createCell(5).setCellValue(if (hasTests) "Yes" else "No")
                row.createCell(6).setCellValue(if (hasDocs) "Yes" else "No")
                row.createCell(7).setCellValue(if (hasCompilerSupport) "Yes" else "No")
            }

            // Auto-size columns
            (0..7).forEach { sheet.autoSizeColumn(it) }

            // Write the workbook to a file
            val outputFile = outputDir.resolve("api_analysis.xlsx")
            println("[DEBUG_LOG] Writing Excel file to: ${outputFile.toAbsolutePath()}")

            FileOutputStream(outputFile.toFile()).use {
                workbook.write(it)
            }
            workbook.close()

            println("[DEBUG_LOG] Excel report generated successfully")
            System.out.flush()
        } catch (e: Exception) {
            System.err.println("[DEBUG_LOG] Error generating Excel report: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Main entry point for running the analysis
     */
    public fun run() {
        val groups = analyzePublicApi()
        generateReport(groups)
    }
}

public fun main() {
    try {
        println("Starting API analysis...")
        val analyzer = ApiAnalyzer()
        analyzer.run()
        println("Analysis completed. Results saved to .junie/api_analysis.xlsx")
    } catch (e: Exception) {
        System.err.println("Error during analysis: ${e.message}")
        e.printStackTrace()
        throw e
    }
}

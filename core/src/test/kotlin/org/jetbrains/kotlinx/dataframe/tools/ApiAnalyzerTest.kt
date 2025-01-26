package org.jetbrains.kotlinx.dataframe.tools

import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class ApiAnalyzerTest {
    @Test
    fun testApiAnalysis() {
        // Run the analysis
        val analyzer = ApiAnalyzer()
        val groups = analyzer.analyzePublicApi()
        analyzer.generateReport(groups)

        // Verify that we found functions
        assertTrue("Should find at least some function groups", groups.isNotEmpty())

        // Analyze results
        val totalFunctions = groups.sumOf { it.functions.size }
        val filesWithFunctions = groups.map { it.fileName }.distinct()
        val functionsWithKDoc = groups.flatMap { it.functions }.count { it.hasKDoc }
        val functionsWithTests = groups.flatMap { it.functions }.count { it.hasCoreTests }
        val functionsWithDocs = groups.flatMap { it.functions }.count { it.hasDocumentation }
        val functionsWithCompilerSupport = groups.flatMap { it.functions }.count { it.hasCompilerSupport != false }

        // Print detailed statistics
        println("\n[DEBUG_LOG] Detailed Analysis Results:")
        println("[DEBUG_LOG] Total function groups: ${groups.size}")
        println("[DEBUG_LOG] Total unique functions: $totalFunctions")
        println("[DEBUG_LOG] Files with functions: ${filesWithFunctions.size}")
        println(
            "[DEBUG_LOG] Functions with KDoc: $functionsWithKDoc (${"%.1f".format(
                functionsWithKDoc * 100.0 / totalFunctions,
            )}%)",
        )
        println(
            "[DEBUG_LOG] Functions with tests: $functionsWithTests (${"%.1f".format(
                functionsWithTests * 100.0 / totalFunctions,
            )}%)",
        )
        println(
            "[DEBUG_LOG] Functions with documentation: $functionsWithDocs (${"%.1f".format(
                functionsWithDocs * 100.0 / totalFunctions,
            )}%)",
        )
        println(
            "[DEBUG_LOG] Functions with compiler support: $functionsWithCompilerSupport (${
                "%.1f".format(
                    functionsWithCompilerSupport * 100.0 / totalFunctions,
                )
            }%)",
        )
        System.out.flush()

        println("\n[DEBUG_LOG] Files with most functions:")
        groups.groupBy { it.fileName }
            .mapValues { it.value.sumOf { group -> group.functions.size } }
            .entries.sortedByDescending { it.value }
            .take(5)
            .forEach { (file, count) ->
                println("[DEBUG_LOG]   $file: $count functions")
            }
        System.out.flush()

        // Verify Excel file
        val outputFile = Paths.get(System.getProperty("user.dir")).parent.resolve(".junie/api_analysis.xlsx")
        assertTrue("Output Excel file should be created", Files.exists(outputFile))
        assertTrue("Output Excel file should not be empty", Files.size(outputFile) > 0)

        // Additional assertions
        assertTrue("Should find at least 10 functions", totalFunctions >= 10)
        assertTrue("Should find functions in at least 3 files", filesWithFunctions.size >= 3)
    }
}

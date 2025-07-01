@file:OptIn(ExperimentalCompilerApi::class)

package org.jetbrains.dataframe.ksp.runner

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspProcessorOptions
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.tschuchort.compiletesting.useKsp2
import org.jetbrains.dataframe.ksp.DataFrameSymbolProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths

@Suppress("unused")
internal class KotlinCompileTestingCompilationResult(
    val delegate: JvmCompilationResult,
    val successfulCompilation: Boolean,
    val kspGeneratedFiles: List<File>,
    val outputSourceDirs: List<File>,
    private val rawOutput: String,
)

internal data class TestCompilationParameters(
    val sources: List<SourceFile> = emptyList(),
    val classpath: List<File> = emptyList(),
    val options: Map<String, String> = emptyMap(),
)

internal object KspCompilationTestRunner {

    val compilationDir: File = Paths.get("build/test-compile").toAbsolutePath().toFile()

    fun compile(params: TestCompilationParameters): KotlinCompileTestingCompilationResult {
        // looks like this requires a kotlin source file
        // see: https://github.com/tschuchortdev/kotlin-compile-testing/issues/57
        val sources = params.sources + SourceFile.kotlin("placeholder.kt", "")
        val combinedOutputStream = ByteArrayOutputStream()
        val kspCompilation = KotlinCompilationUtil.prepareCompilation(
            sources = sources,
            outputStream = combinedOutputStream,
            classpaths = params.classpath,
            tempDir = compilationDir,
        )
        kspCompilation.kspProcessorOptions.putAll(params.options)

        // We don't support KSP2, but because we target Kotlin 2.2 the tests only work if I set them up like this.
        kspCompilation.useKsp2()
        kspCompilation.kspProcessorOptions["ksp.useKSP2"] = "false"
        kspCompilation.kspProcessorOptions["useKSP2"] = "false"

        kspCompilation.symbolProcessorProviders = mutableListOf(DataFrameSymbolProcessorProvider())
        kspCompilation.compile().also {
            println(it.messages)
            if (it.exitCode == KotlinCompilation.ExitCode.COMPILATION_ERROR) {
                return KotlinCompileTestingCompilationResult(
                    delegate = it,
                    successfulCompilation = false,
                    kspGeneratedFiles = emptyList(),
                    outputSourceDirs = emptyList(),
                    rawOutput = combinedOutputStream.toString(Charsets.UTF_8),
                )
            }
        }
        // ignore KSP result for now because KSP stops compilation, which might create false
        // negatives when java code accesses kotlin code.
        // TODO:  fix once https://github.com/tschuchortdev/kotlin-compile-testing/issues/72 is
        //  fixed

        // after ksp, compile without ksp with KSP's output as input
        val finalCompilation = KotlinCompilationUtil.prepareCompilation(
            sources = sources,
            outputStream = combinedOutputStream,
            classpaths = params.classpath,
            tempDir = compilationDir,
        )
        // build source files from generated code
        finalCompilation.sources += kspCompilation.kspJavaSourceDir.collectSourceFiles() +
            kspCompilation.kspKotlinSourceDir.collectSourceFiles()

        val result = finalCompilation.compile()
        println(result.messages)
        return KotlinCompileTestingCompilationResult(
            delegate = result,
            successfulCompilation = result.exitCode == KotlinCompilation.ExitCode.OK,
            outputSourceDirs = listOf(
                kspCompilation.kspJavaSourceDir,
                kspCompilation.kspKotlinSourceDir,
            ),
            kspGeneratedFiles = kspCompilation.kspJavaSourceDir.collectFiles() +
                kspCompilation.kspKotlinSourceDir.collectFiles(),
            rawOutput = combinedOutputStream.toString(Charsets.UTF_8),
        )
    }

    // TODO get rid of these once kotlin compile testing supports two step compilation for KSP.
    //  https://github.com/tschuchortdev/kotlin-compile-testing/issues/72
    private val KotlinCompilation.kspJavaSourceDir: File
        get() = kspSourcesDir.resolve("java")

    private val KotlinCompilation.kspKotlinSourceDir: File
        get() = kspSourcesDir.resolve("kotlin")

    private fun File.collectSourceFiles(): List<SourceFile> =
        walkTopDown()
            .filter { it.isFile }
            .map { file -> SourceFile.fromPath(file) }
            .toList()

    private fun File.collectFiles(): List<File> =
        walkTopDown()
            .filter { it.isFile }
            .toList()
}

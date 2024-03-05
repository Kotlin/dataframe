package org.jetbrains.dataframe.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import java.io.File
import java.nio.file.Files

fun runGradleBuild(
    task: String,
    @Language("kts") settingsGradle: (File) -> String = { "" },
    @Language("kts") build: (File) -> String,
): Build {
    val buildDir = Files.createTempDirectory("test").toFile()
    val buildFile = File(buildDir, "build.gradle.kts")
    buildFile.writeText(build(buildDir))
    val settingsFile = File(buildDir, "settings.gradle.kts")
    settingsFile.writeText(settingsGradle(buildDir))
    return Build(buildDir, gradleRunner(buildDir, task).build())
}

fun gradleRunner(buildDir: File, task: String): GradleRunner = GradleRunner.create()
    .withProjectDir(buildDir)
    .withPluginClasspath()
    .withArguments(task, "--stacktrace", "--info")
    .withDebug(true)

data class Build(val buildDir: File, val buildResult: BuildResult)

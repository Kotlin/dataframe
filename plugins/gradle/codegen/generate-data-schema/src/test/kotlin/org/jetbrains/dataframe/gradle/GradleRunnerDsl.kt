package org.jetbrains.dataframe.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files

internal fun runGradleBuild(task: String, build: (File) -> String): Build {
    val buildDir = Files.createTempDirectory("test").toFile()
    val buildFile = File(buildDir, "build.gradle.kts")
    buildFile.writeText(build(buildDir))
    return Build(buildDir, gradleRunner(buildDir, task).build())
}

internal fun gradleRunner(buildDir: File, task: String) = GradleRunner.create()
    .withProjectDir(buildDir)
    .withGradleVersion("7.0")
    .withPluginClasspath()
    .withArguments(task)
    .withDebug(true)

data class Build(val buildDir: File, val buildResult: BuildResult)

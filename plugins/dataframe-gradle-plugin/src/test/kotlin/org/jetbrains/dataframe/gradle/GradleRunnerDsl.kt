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
    val propertiesFile = File(buildDir, "gradle.properties")
    propertiesFile.writeText("ksp.useKSP2=false")
    return Build(buildDir, gradleRunner(buildDir, task).build())
}

fun gradleRunner(buildDir: File, task: String): GradleRunner =
    GradleRunner.create()
        .withProjectDir(buildDir)
        // if we use api from the newest Gradle release, a user project will fail with NoSuchMethod
        // testing compatibility with an older Gradle version ensures our plugin can run on as many versions as possible
        .withGradleVersion("8.5")
        .withPluginClasspath()
        .withArguments(task, "--stacktrace", "--info")
        .withDebug(true)

data class Build(val buildDir: File, val buildResult: BuildResult)

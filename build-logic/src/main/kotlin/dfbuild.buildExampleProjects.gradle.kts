import dfbuild.buildExampleProjects.BuildSystem
import dfbuild.buildExampleProjects.detectBuildSystem
import dfbuild.buildExampleProjects.setupGradleSyncVersionsTask
import dfbuild.buildExampleProjects.setupMavenSyncVersionsTask
import dfbuild.toCamelCaseByDelimiters
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(convention.plugins.kotlinJvmCommon)
}

val versionsToSync =
    listOf(
        "kotlin",
        "dataframe",
        "kandy",
        "ktlint-gradle",
        "ktlint",
    )

val syncExampleFolders by tasks.registering {
    group = "build"
    description = "Sync the versions in the nested Gradle build in /examples/projects"
}

tasks.named("assemble") {
    dependsOn(syncExampleFolders)
}

val promoteExamples by tasks.registering {
    group = "publishing"
    description = "Promotes the /examples/projects/dev example projects to /examples/projects"

    val projectsFolder = file("examples/projects")

    doLast {
        // Deletes existing projects before promotion
        projectsFolder.listFiles()?.forEach {
            if (!it.isDirectory || it.name == "dev") return@forEach
            it.deleteRecursively()
        }
        logger.lifecycle("Removed example projects from /examples/projects")
        projectsFolder.resolve("dev").listFiles()?.forEach {
            if (!it.isDirectory) return@forEach
            it.copyRecursively(projectsFolder.resolve(it.name))
        }
        logger.lifecycle("Copied example projects from /examples/projects/dev to examples/projects")
    }
    finalizedBy(syncExampleFolders)
}

val testBuildingExamples: SourceSet by sourceSets.creating {
    kotlin.srcDir(file("build-logic/src/testBuildingExamples/kotlin"))
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

tasks.named<KotlinCompile>("compile${testBuildingExamples.name.capitalized()}Kotlin") {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        freeCompilerArgs.add("-Xjdk-release=21")
    }
}

tasks.named<JavaCompile>("compile${testBuildingExamples.name.capitalized()}Java") {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
    options.release.set(21)
}

val testBuildingExamplesImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val testBuildingExamplesRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

dependencies {
    testBuildingExamplesImplementation(gradleTestKit())
    testBuildingExamplesImplementation(libs.junit.jupiter)
    testBuildingExamplesImplementation(libs.junit)
    testBuildingExamplesImplementation(libs.maven.invoker)
    testBuildingExamplesImplementation(project(":build-logic"))
    testBuildingExamplesRuntimeOnly(libs.junit.platform.launcher)
}

private fun Test.commonSetup() {
    group = "verification"
    dependsOn(syncExampleFolders)

    testClassesDirs = testBuildingExamples.output.classesDirs
    classpath = testBuildingExamples.runtimeClasspath
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    useJUnitPlatform()
    testLogging { events("passed", "skipped", "failed") }

    // pass all project properties down to the tests prepending them with 'gradle.properties.'
    project.properties.forEach { (key, value) ->
        systemProperty("gradle.properties.$key", value?.toString())
    }
}

val buildMavenExampleFolders by tasks.registering(Test::class) {
    commonSetup()
    description = "Builds the nested Maven builds in /examples/projects to verify they compile correctly."

    // Because we're including a Maven project, we need to publish to maven local to test it.
    dependsOn(":publishToMavenLocal")
    useJUnitPlatform {
        includeTags("maven")
    }
}

val buildGradleExampleFolders by tasks.registering(Test::class) {
    commonSetup()
    description = "Builds the nested Gradle builds in /examples/projects to verify they compile correctly."
    useJUnitPlatform {
        includeTags("gradle")
    }
}

val buildExampleFolders by tasks.registering(Test::class) {
    group = "verification"
    description = "Builds all the nested builds in /examples/projects to verify they compile correctly."
    dependsOn(buildMavenExampleFolders, buildGradleExampleFolders)
}

tasks.named("test") {
    // only builds the examples on ':test' when debug mode is enabled
    if (project.properties["kotlin.dataframe.debug"].toString() == "true") {
        dependsOn(buildExampleFolders)
    }
}

/**
 * Sets up example folders sync tasks
 */
private fun setupExampleProjectFolder(folder: File, isDev: Boolean) {
    val name = folder.name.toCamelCaseByDelimiters().replaceFirstChar { it.uppercase() } +
        (if (isDev) "Dev" else "")

    val buildSystem = folder.detectBuildSystem()
        ?: error(
            "Could not detect build system in example project folder '$folder'. We only support ${BuildSystem.entries.toList()}.",
        )

    val libs = versionCatalogs.named("libs")

    val syncTask =
        when (buildSystem) {
            BuildSystem.GRADLE ->
                setupGradleSyncVersionsTask(
                    name = name,
                    folder = folder,
                    isDev = isDev,
                    versionCatalog = libs,
                    versionsToSync = versionsToSync,
                )

            BuildSystem.MAVEN ->
                setupMavenSyncVersionsTask(
                    name = name,
                    folder = folder,
                    isDev = isDev,
                    versionCatalog = libs,
                    versionsToSync = versionsToSync,
                )
        }
    syncExampleFolders {
        dependsOn(syncTask)
    }
}

file("examples/projects").listFiles()?.forEach {
    if (!it.isDirectory || it.name == "dev") return@forEach
    setupExampleProjectFolder(folder = it, isDev = false)
}
file("examples/projects/dev").listFiles()?.forEach {
    if (!it.isDirectory) return@forEach
    setupExampleProjectFolder(folder = it, isDev = true)
}

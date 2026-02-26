import dfbuild.buildExampleProjects.BuildSystem
import dfbuild.buildExampleProjects.detectBuildSystem
import dfbuild.buildExampleProjects.generateTestCase
import dfbuild.buildExampleProjects.isAndroid
import dfbuild.buildExampleProjects.setupGradleSyncVersionsTask
import dfbuild.buildExampleProjects.setupMavenSyncVersionsTask
import dfbuild.toCamelCaseByDelimiters
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(convention.plugins.kotlinJvmCommon)
}

val buildExampleProjectsGroup = "build example projects"

// region syncing

val versionsToSync =
    listOf(
        "kotlin",
        "dataframe",
        "kandy",
        "ktlint-gradle",
        "ktlint",
        "maven-wrapper",
        "maven",
    )

val syncAllExampleFolders by tasks.registering {
    group = buildExampleProjectsGroup
    description = "Sync the versions in the nested Gradle build in /examples/projects"
}
tasks.named("assemble") {
    dependsOn(syncAllExampleFolders)
}

/**
 * Sets up example folders sync tasks
 */
private fun setupExampleProjectFolderSyncTask(folder: File, isDev: Boolean) {
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
    syncTask { group = buildExampleProjectsGroup }
    syncAllExampleFolders {
        dependsOn(syncTask)
    }
}

// endregion

// region promoting

val promoteExamples by tasks.registering {
    group = buildExampleProjectsGroup
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
    finalizedBy(syncAllExampleFolders)
}

// endregion

// region testing/building examples

val generateAllExampleFoldersTests by tasks.registering {
    group = buildExampleProjectsGroup
    description = "Generates test classes for each example in /examples/projects"

    doFirst {
        layout.buildDirectory
            .dir("generated/testBuildingExamples")
            .get()
            .asFile
            .takeIf { it.exists() }
            ?.deleteRecursively()
    }
}

val runBuildAllExampleFolders by tasks.registering(Test::class) {
    group = buildExampleProjectsGroup
    description = "Builds all the nested builds in /examples/projects to verify they compile correctly."
}
tasks.named("test") {
    // builds the examples on ':test' when debug mode is enabled
    if (project.properties["kotlin.dataframe.debug"].toString() == "true") {
        dependsOn(runBuildAllExampleFolders)
    }
}

val runBuildReleaseExampleFolders by tasks.registering(Test::class) {
    group = buildExampleProjectsGroup
    description = "Builds the nested release builds in /examples/projects to verify they compile correctly."
}
val runBuildDevExampleFolders by tasks.registering(Test::class) {
    group = buildExampleProjectsGroup
    description = "Builds the nested dev builds in /examples/projects to verify they compile correctly."
}
val runBuildMavenExampleFolders by tasks.registering(Test::class) {
    group = buildExampleProjectsGroup
    description = "Builds the nested Maven builds in /examples/projects to verify they compile correctly."
}
val runBuildGradleExampleFolders by tasks.registering(Test::class) {
    group = buildExampleProjectsGroup
    description = "Builds the nested Gradle builds in /examples/projects to verify they compile correctly."
}
val runBuildAndroidExampleFolders by tasks.registering(Test::class) {
    group = buildExampleProjectsGroup
    description = "Builds the nested Android builds in /examples/projects to verify they compile correctly."
}
val runBuildNonAndroidExampleFolders by tasks.registering(Test::class) {
    group = buildExampleProjectsGroup
    description = "Builds the nested non-Android builds in /examples/projects to verify they compile correctly."
}

private fun setupGenerateAndRunTestTasks(folder: File, isDev: Boolean) {
    val testClassName = folder.name
        .toCamelCaseByDelimiters()
        .replaceFirstChar { it.uppercase() }
        .plus(if (isDev) "Dev" else "")
        .plus("Test")

    val isAndroid = folder.isAndroid()
    val buildSystem = folder.detectBuildSystem() ?: error(
        "Could not detect build system in example project folder '$folder'. We only support ${BuildSystem.entries.toList()}.",
    )

    val generateTask = tasks.register("generate$testClassName") {
        group = buildExampleProjectsGroup

        val tags = buildList {
            if (isAndroid) add("android")
            add(buildSystem.name.lowercase())
            add(if (isDev) "dev" else "release")
        }

        val targetFile = layout.buildDirectory
            .dir("generated/testBuildingExamples/src/test/kotlin").get()
            .file("$testClassName.kt")
            .asFile
        doLast {
            val text = generateTestCase(testClassName = testClassName, folder = folder, isDev = isDev, tags = tags)

            if (!targetFile.parentFile.exists()) {
                targetFile.parentFile.mkdirs()
            }
            targetFile.writeText(text)
        }
    }
    generateAllExampleFoldersTests {
        finalizedBy(generateTask)
    }

    val runTestBuildTask = tasks.register<Test>("runBuild$testClassName") {
        group = buildExampleProjectsGroup
        dependsOn(syncAllExampleFolders, generateAllExampleFoldersTests)

        if (buildSystem == BuildSystem.MAVEN && isDev) {
            // Because we're including a dev Maven project, we need to publish to /build/maven to test it.
            dependsOn(":publishLocal")
        }

        maxHeapSize = "3g"
        testClassesDirs = testBuildingExamples.output.classesDirs
        classpath = testBuildingExamples.runtimeClasspath
        useJUnitPlatform()
        filter { includeTestsMatching(testClassName) }
        testLogging { events("passed", "skipped", "failed") }

        // pass down project parameters -> JUnit configuration parameters
        val props = listOf(
            "android.sdk.dir",
        )
        for (prop in props) {
            val value = project.properties[prop]?.toString() ?: continue
            systemProperty("gradle.properties.$prop", value)
        }
        systemProperty(
            "gradle.properties.maven.repo.local",
            layout.buildDirectory.dir("maven").get().asFile.absolutePath,
        )
    }
    runBuildAllExampleFolders { dependsOn(runTestBuildTask) }
    when (isDev) {
        true -> runBuildDevExampleFolders { dependsOn(runTestBuildTask) }
        false -> runBuildReleaseExampleFolders { dependsOn(runTestBuildTask) }
    }
    when (buildSystem) {
        BuildSystem.MAVEN -> runBuildMavenExampleFolders { dependsOn(runTestBuildTask) }
        BuildSystem.GRADLE -> runBuildGradleExampleFolders { dependsOn(runTestBuildTask) }
    }
    when (isAndroid) {
        true -> runBuildAndroidExampleFolders { dependsOn(runTestBuildTask) }
        false -> runBuildNonAndroidExampleFolders { dependsOn(runTestBuildTask) }
    }
}

val testBuildingExamples: SourceSet by sourceSets.creating {
    kotlin.setSrcDirs(
        listOf(
            // base class
            file("build-logic/src/testBuildingExamples/kotlin"),
            // generated test classes
            layout.buildDirectory.dir("generated/testBuildingExamples/src/test/kotlin").get(),
        ),
    )

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

// endregion

// region folder-scan and task registration

file("examples/projects").listFiles()?.forEach {
    if (!it.isDirectory || it.name == "dev") return@forEach
    setupExampleProjectFolderSyncTask(folder = it, isDev = false)
    setupGenerateAndRunTestTasks(folder = it, isDev = false)
}
file("examples/projects/dev").listFiles()?.forEach {
    if (!it.isDirectory) return@forEach
    setupExampleProjectFolderSyncTask(folder = it, isDev = true)
    setupGenerateAndRunTestTasks(folder = it, isDev = true)
}

// endregion

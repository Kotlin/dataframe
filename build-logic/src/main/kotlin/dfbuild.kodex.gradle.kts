import dfbuild.findRootDir
import dfbuild.kodex.KODEX_COMPONENT_NAME
import dfbuild.kodex.addKodexVariants
import dfbuild.kodex.createKodexComponent
import dfbuild.kodex.isKodexComponentUsed
import dfbuild.kodex.useKodexJarForApiValidation
import nl.jolanrensen.kodex.gradle.KodexIsolationMode
import nl.jolanrensen.kodex.gradle.RunKodexTask
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    alias(conventions.plugins.dfbuild.kotlinJvmCommon)

    alias(libs.plugins.kodex)
    idea
}

/** The name of the folder the KoDEx-processed sources are written to. */
val generatedSourcesFolderName = "generated-sources"

/**
 * If the `skipKodex` property is present, the normal, unprocessed, jar files are published.
 * This is useful when publishing locally during development, as KoDEx takes a while to run.
 */
val skipKodex = project.hasProperty("skipKodex")

val processKDocsMainName = "processKDocsMain"

// The name of the new sourceSet and compilation created by KoDEx for the processed sources.
val kodexSourceSetName = "mainKodex"
val ktlintFormatKodexSourceSet = "runKtlintFormatOverMainKodexSourceSet"
val kodexJarTaskName = "kodex${kodexSourceSetName.uppercaseFirstChar()}Jar"
val kodexSourcesJarTaskName = "kodex${kodexSourceSetName.uppercaseFirstChar()}SourcesJar"

/**
 * The KoDEx-processed source roots of the `main` sourceSet (inside the `generated-sources` folder),
 * carrying the task dependencies needed to produce them.
 * Calculated lazily, as the sources of [processKDocsMainName] may only be known after evaluation.
 */
val kodexProcessedSourceRoots: ConfigurableFileCollection = files(
    provider {
        tasks.named<RunKodexTask>(processKDocsMainName).get().calculateTargets()
    },
).builtBy(processKDocsMainName)

/*
 * Attaches KoDEx to the main sourceSet. This:
 *  - creates the `processKDocsMain` task, which writes the processed sources to the `generated-sources` folder
 *  - creates the `mainKodex` sourceSet and compilation, which compiles the processed sources
 *    (with the same dependencies and compiler settings as `main`).
 *    This makes sure `@ExcludeFromSources` is respected in the compiled classes too.
 *  - creates the `kodexMainKodexJar` and `kodexMainKodexSourcesJar` tasks, which create a jar and a sources jar
 *    from the KoDEx processed sources
 */
kodex {
    preprocess(kotlin.sourceSets.main) {
        taskName = processKDocsMainName
        newSourceSetName = kodexSourceSetName

        target = file(generatedSourcesFolderName)

        // false, so `runKtlintFormatOverMainKodexSourceSet` can format the output
        outputReadOnly = false

        exportAsHtml {
            dir = findRootDir().absoluteFile.resolve("docs/StardustDocs/resources/snippets/kdocs")
        }

        workerIsolation {
            mode = KodexIsolationMode.PROCESS
            maxHeapSize = "512m"
        }

        // use `project()` dependencies for connecting contextual KoDEx sources and -caches
        // Any api/implementation/compileOnly project dependency is added as contextual source set.
        contextualSourceSets(
            provider {
                sequenceOf(
                    configurations.api,
                    configurations.implementation,
                    configurations.compileOnly,
                ).flatMap { it.get().dependencies }
                    .filterIsInstance<ProjectDependency>()
                    .map { it.path }
                    .distinct()
                    .onEach { logger.info("$processKDocsMainName in $path uses $it as contextual source set.") }
                    .toList()
            },
        )
        // Additional sources processed by KoDEx, but not included in the jar files,
        // useful for `@sample` or `@includeFile`. Adding the `:samples` module manually.
        contextualSourceSet(":samples", "test")

        // Yes, we would like KoDEx to register Jar-creation tasks
        generateJar = true
        generateSourcesJar = true
        // We need to be able to skip the jar and sources jar generation with skipKodex
        addJarsToAssemble = false
        // Ordinarily, this replaces the jars in the `java` component with the KoDEx jar and sources jar.
        // We will do this manually to keep the logic in the DataFrame project. It's a delicate API.
        replaceArtifactsInJavaComponent = false
    }
}

// Alias for processKDocsMain
tasks.register("kodex") {
    group = "KoDEx"
    dependsOn(processKDocsMainName)
}

/*
 * Configure KoDEx + maven publisher
 *
 * When `libs.publisher` is applied too (in any order), and the `skipKodex` property is not present,
 * this creates the `kodexJava` component: a copy of the `java` component, but with the KoDEx jar and sources jar.
 * The publication of the module must select it explicitly with:
 * ```kotlin
 * kotlinPublications {
 *     publication {
 *         composeOf { from(kodexAwareComponentToPublish()) }
 *     }
 * }
 * ```
 * Publishing fails if this is forgotten, so we never accidentally publish the unprocessed jars.
 */
pluginManager.withPlugin(libs.plugins.publisher.get().pluginId) {
    if (skipKodex) return@withPlugin

    val kodexComponent = createKodexComponent()

    // Run as late as possible, as the `java` component's variants (like `sourcesElements`)
    // may only be created in `afterEvaluate {}` and the KoDEx jar tasks are registered in `afterEvaluate {}` too.
    gradle.projectsEvaluated {
        addKodexVariants(
            component = kodexComponent,
            jar = tasks.named<Jar>(kodexJarTaskName),
            sourcesJar = tasks.named<Jar>(kodexSourcesJarTaskName),
        )
    }

    val projectPath = path
    gradle.taskGraph.whenReady {
        val publishesThisProject = allTasks.any {
            it.project.path == projectPath && it is AbstractPublishToMaven
        }
        check(!publishesThisProject || isKodexComponentUsed) {
            "$projectPath applies both `dfbuild.kodex` and `libs.publisher`, but its publication does not use " +
                "the '$KODEX_COMPONENT_NAME' component with the KoDEx-processed jars. " +
                "Add `composeOf { from(kodexAwareComponentToPublish()) }` to its " +
                "`kotlinPublications { publication {} }`, " +
                "or use `-PskipKodex` to publish the unprocessed jars."
        }
    }
}

/*
 * Configure KoDEx + Binary Compatibility Validator
 *
 * When `libs.plugins.binary.compatibility.validator` is applied too (in any order),
 * the `apiBuild` task (used by `apiCheck` and `apiDump`)
 * reads the public API from the KoDEx-processed jar instead of the `main` classes.
 * This way, the binary compatibility is validated against the jar we actually publish.
 *
 * This will also be skipped with `skipKodex`, since `apiCheck` would fail if run without KoDEx.
 */
pluginManager.withPlugin(libs.plugins.binary.compatibility.validator.get().pluginId) {
    if (skipKodex) {
        tasks
            .named { it in setOf("apiBuild", "apiCheck", "apiDump") }
            .configureEach { enabled = false }
    } else {
        useKodexJarForApiValidation(kodexJarTaskName = kodexJarTaskName)
    }
}

/*
 * Configure KoDEx + KtLint
 *
 * Make sure KtLint runs on the processed sources, only when they are regenerated.
 */
pluginManager.withPlugin(libs.plugins.ktlint.gradle.get().pluginId) {
    tasks.named(processKDocsMainName) {
        finalizedBy(ktlintFormatKodexSourceSet)
    }
    // The processed sources are only compiled and packaged after they have been formatted
    tasks.named {
        it in listOf(
            "compile${kodexSourceSetName.uppercaseFirstChar()}Kotlin",
            "compile${kodexSourceSetName.uppercaseFirstChar()}Java",
            "kodex${kodexSourceSetName.uppercaseFirstChar()}SourcesJar",
        )
    }.configureEach {
        dependsOn(ktlintFormatKodexSourceSet)
    }
    // Anything else reading the processed sources (like Dokka) should also wait for the formatting
    kodexProcessedSourceRoots.builtBy(ktlintFormatKodexSourceSet)

    // Skips `mainKodex` KtLint check on "normal" KtLint runs.
    // The formatting runs automatically after `processKDocsMain`
    tasks.named {
        it == "ktlint${kodexSourceSetName.uppercaseFirstChar()}SourceSetCheck" ||
            it == "runKtlintCheckOver${kodexSourceSetName.uppercaseFirstChar()}SourceSet"
    }.configureEach {
        onlyIf { false }
    }
}

/*
 * Configure KoDEx + Dokka
 *
 * When `libs.plugins.dokka` is applied too (in any order), and the `skipKodex` property is not present,
 * Dokka documents the KoDEx-processed sources instead of the original `main` sources.
 * This way, the generated documentation contains the fully expanded KDocs and respects `@ExcludeFromSources`.
 *
 * Dokka automatically creates a Dokka sourceSet for each Kotlin sourceSet, so also for `mainKodex`
 * (with the processed sources, classpath and platform of the KoDEx compilation).
 * We simply document that one instead of `main`:
 *  - `main` is suppressed, so the same declarations are not documented twice
 *  - `mainKodex` is enabled (Dokka suppresses non-`main` sourceSets by default),
 *    gets the same display name as `main` (like "jvm") so the rendered docs look the same,
 *    and waits for the processed sources to be generated (and formatted).
 */
pluginManager.withPlugin(libs.plugins.dokka.get().pluginId) {
    if (skipKodex) return@withPlugin

    extensions.configure<DokkaExtension> {
        dokkaSourceSets.configureEach {
            when (name) {
                SourceSet.MAIN_SOURCE_SET_NAME -> {
                    // Disable the default main sourceSet being processed by Dokka
                    suppress = true
                }

                kodexSourceSetName -> {
                    // Enable the KoDEx main sourceSet being processed by Dokka
                    suppress = false
                    displayName = dokkaSourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME).flatMap { it.displayName }
                    // the source dirs of the `mainKodex` Kotlin sourceSet don't carry the KoDEx task dependencies
                    sourceRoots.builtBy(kodexProcessedSourceRoots)
                }
            }
        }
    }
}

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(generatedSourcesFolderName))
    }
}

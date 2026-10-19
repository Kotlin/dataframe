import dfbuild.findRootDir
import dfbuild.kodex.KODEX_COMPONENT_NAME
import dfbuild.kodex.addKodexVariants
import dfbuild.kodex.createKodexComponent
import dfbuild.kodex.isKodexComponentUsed
import dfbuild.kodex.useKodexJarForApiValidation
import nl.jolanrensen.kodex.gradle.KodexIsolationMode
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

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
 * This cannot be skipped with `skipKodex`.
 */
pluginManager.withPlugin(libs.plugins.binary.compatibility.validator.get().pluginId) {
    useKodexJarForApiValidation(jarTaskName = kodexJarTaskName)
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

    // Skips `mainKodex` KtLint check on "normal" KtLint runs.
    // The formatting runs automatically after `processKDocsMain`
    tasks.named {
        it == "ktlint${kodexSourceSetName.uppercaseFirstChar()}SourceSetCheck" ||
            it == "runKtlintCheckOver${kodexSourceSetName.uppercaseFirstChar()}SourceSet"
    }.configureEach {
        onlyIf { false }
    }
}

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(generatedSourcesFolderName))
    }
}

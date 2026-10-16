import dfbuild.kodex.KODEX_COMPONENT_NAME
import dfbuild.kodex.addKodexVariants
import dfbuild.kodex.createKodexComponent
import dfbuild.findRootDir
import dfbuild.kodex.isKodexComponentUsed
import nl.jolanrensen.kodex.gradle.KodexIsolationMode
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    alias(conventions.plugins.dfbuild.kotlinJvmCommon)

    alias(libs.plugins.kodex)
    idea
}

/**
 * These settings can be modified using `kodexConvention {}`.
 */
interface KodexConventionExtension {

    /**
     * Resolved Kotlin main SourceSet to be processed by KoDEx and
     * form the eventual jar files.
     *
     * By default, this contains all source directories of `kotlin.sourceSets.main`.
     */
    val kotlinMainSourcesDirectories: SetProperty<File>

    /**
     * Any additional (resolved) Kotlin SourceSets to be processed by KoDEx,
     * but that will not be included in the eventual jar files.
     *
     * This can be useful if you want to use `@sample` or `@includeFile`.
     *
     * By default, this contains the `:samples` test sources.
     *
     * NOTE: All `api`/`implementation`/`compileOnly` project dependencies of this project are
     * automatically added as contextual sources by KoDEx, including their output caches.
     * These projects must also apply `dfbuild.kodex`.
     */
    val contextualSourcesDirectories: SetProperty<List<File>>

    /**
     * The name of the generated sources folder. "generated-sources" by default.
     */
    val generatedSourcesFolderName: Property<String>

    /**
     * Any additional output caches of KoDEx tasks to use, see [contextualSourcesDirectories].
     * Empty by default.
     */
    val inputCacheFiles: ConfigurableFileCollection
}

val extension = project.extensions.create<KodexConventionExtension>("kodexConvention")

extension.generatedSourcesFolderName.convention("generated-sources")
extension.kotlinMainSourcesDirectories.convention(
    // read lazily, so any modifications to the main sourceSet in build files are present
    provider {
        kotlin.sourceSets.main.get()
            .kotlin
            .sourceDirectories
            // important! This clones the collection
            .toSet()
    },
)
extension.contextualSourcesDirectories.convention(
    // Adding the `:samples` module manually
    listOf(listOf(findRootDir().resolve("samples/src/test/kotlin"))),
)
extension.inputCacheFiles.convention(emptyList<Any>())

/**
 * If the `skipKodex` property is present, the normal, unprocessed, jar files are published.
 * This is useful when publishing locally during development, as KoDEx takes a while to run.
 */
val skipKodex = project.hasProperty("skipKodex")

/**
 * The ID of the `libs.publisher` plugin.
 * If it's applied to this project, the `kodexJava` component is created, see below.
 */
val publisherPluginId = libs.plugins.publisher.get().pluginId

val processKDocsMainName = "processKDocsMain"

// The name of the new sourceSet and compilation created by KoDEx for the processed sources.
val kodexSourceSetName = "mainKodex"
val ktlintFormatKodexSourceSet = "runKtlintFormatOverMainKodexSourceSet"

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

        target = extension.generatedSourcesFolderName.map { file(it) }

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

        // only built when needed, like when publishing
        generateJar = true
        generateSourcesJar = true
        addJarsToAssemble = false

        task {
            sources = extension.kotlinMainSourcesDirectories
            contextualSources.addAll(extension.contextualSourcesDirectories)
            inputCacheFiles.from(extension.inputCacheFiles)
        }
    }
}

/*
 * When `libs.publisher` is applied too (in any order), and the `skipKodex` property is not present,
 * this creates the `kodexJava` component: a copy of the `java` component, but with the KoDEx jar and sources jar.
 * The publication of the module must select it explicitly with:
 * ```kotlin
 * kotlinPublications {
 *     publication {
 *         composeOf { from(componentToPublish()) }
 *     }
 * }
 * ```
 * Publishing fails if this is forgotten, so we never accidentally publish the unprocessed jars.
 */
pluginManager.withPlugin(publisherPluginId) {
    if (skipKodex) return@withPlugin

    val kodexComponent = createKodexComponent()

    // Run as late as possible, as the `java` component's variants (like `sourcesElements`)
    // may only be created in `afterEvaluate {}` and the KoDEx jar tasks are registered in `afterEvaluate {}` too.
    gradle.projectsEvaluated {
        addKodexVariants(
            component = kodexComponent,
            jar = tasks.named<Jar>("kodex${kodexSourceSetName.uppercaseFirstChar()}Jar"),
            sourcesJar = tasks.named<Jar>("kodex${kodexSourceSetName.uppercaseFirstChar()}SourcesJar"),
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
                "Add `composeOf { from(componentToPublish()) }` to its `kotlinPublications { publication {} }`, " +
                "or use `-PskipKodex` to publish the unprocessed jars."
        }
    }
}

tasks.named(processKDocsMainName) {
    finalizedBy(ktlintFormatKodexSourceSet)
}

// Alias for processKDocsMain
tasks.register("kodex") {
    group = "KoDEx"
    dependsOn(processKDocsMainName)
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

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(extension.generatedSourcesFolderName.get()))
    }
}

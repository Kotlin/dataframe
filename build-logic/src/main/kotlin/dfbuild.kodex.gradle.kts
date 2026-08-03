import dfbuild.findRootDir
import nl.jolanrensen.kodex.gradle.KodexIsolationMode
import nl.jolanrensen.kodex.gradle.RunKodexTask

plugins {
    alias(conventions.plugins.dfbuild.kotlinJvmCommon)

    alias(libs.plugins.kodex)
    idea
}

// TODO migrate to kodex {} extension syntax. #985

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
     * By default, this contains all `api`/`implementation`/`compileOnly` project source sets this project depends on.
     *
     * [inputCacheFiles] will be used to stop KoDEx from processing contextual sources multiple times.
     */
    val contextualSourcesDirectories: SetProperty<List<File>>

    /**
     * The name of the generated sources folder. "generated-sources" by default.
     */
    val generatedSourcesFolderName: Property<String>

    /**
     * Used in conjunction with [contextualSourcesDirectories].
     * By default, it contains the combined `outputCacheFile`s of the contextual sources.
     */
    val inputCacheFiles: ConfigurableFileCollection
}

val extension = project.extensions.create<KodexConventionExtension>("kodexConvention")

extension.generatedSourcesFolderName.convention("generated-sources")

// this is set in afterEvaluate to any modifications to the main/test sourceSets are present
afterEvaluate {
    extension.kotlinMainSourcesDirectories.convention(
        kotlin.sourceSets.main.get()
            .kotlin
            .sourceDirectories
            // important! This clones the collection
            .toSet(),
    )

    // use `project()` dependencies for connecting contextual KoDEx sources and -caches
    val contextualProjects =
        sequenceOf(
            configurations.api,
            configurations.implementation,
            configurations.compileOnly,
        ).flatMap { it.get().dependencies }
            .filterIsInstance<ProjectDependency>()
            .distinctBy { it.path }
            .map { project(it.path) }

    val processKDocsMain = tasks["processKDocsMain"]

    // connect contextual outputCaches -> inputCache and dependsOn relations
    extension.inputCacheFiles.convention(emptyList<Any>())
    contextualProjects.forEach {
        it.tasks.withType<RunKodexTask>().configureEach {
            logger.lifecycle("${processKDocsMain.path} now dependsOn ${this.path}, contextual cache is transferred.")
            extension.inputCacheFiles.from(this.outputCacheFile)
        }
    }

    // collect contextual source dirs
    val contextualProjectsSources = contextualProjects.map {
        it.extensions
            .findByName("sourceSets")?.let { it as SourceSetContainer }
            ?.findByName("main")
            ?.extensions
            ?.findByName("kotlin")?.let { it as SourceDirectorySet }
            ?.sourceDirectories
            ?.toList()
            ?: emptyList()
    }.toSet()

    extension.contextualSourcesDirectories.convention(contextualProjectsSources)
}

fun pathOf(vararg parts: String) = parts.joinToString(File.separator)

// main sourceset of the generated sources as a result of `processKDocsMain`, this will create linter tasks
// This also makes sure the contextual sources are not in the final jar
val generatedMainSources = kotlin.sourceSets.create("generatedMainSources") {
    kotlin {
        afterEvaluate {
            this@kotlin.setSrcDirs(
                extension.kotlinMainSourcesDirectories.get().mapTo(mutableSetOf()) {
                    // follows the same logic as KoDEx
                    val relativePath = projectDir.toPath().relativize(it.toPath())
                    File(extension.generatedSourcesFolderName.get(), relativePath.toString())
                },
            )
        }
    }
}

// Task to generate the processed documentation
val processKDocsMain = tasks.register<RunKodexTask>("processKDocsMain") {
    sources = extension.kotlinMainSourcesDirectories.get()
        .also {
            logger.info("$name: Preprocessing sources with KoDEx: ${it.toList()}")
        }
    contextualSources = extension.contextualSourcesDirectories
    inputCacheFiles = extension.inputCacheFiles

    group = "KoDEx"
    target = file(extension.generatedSourcesFolderName)

    workerIsolation {
        mode = KodexIsolationMode.PROCESS
        maxHeapSize = "512m"
    }

    // false, so `ktlintGeneratedMainSourcesSourceSetFormat` can format the output
    outputReadOnly = false

    exportAsHtml {
        dir = findRootDir().absoluteFile.resolve("docs/StardustDocs/resources/snippets/kdocs")
    }
    finalizedBy(
        tasks.findByName("runKtlintFormatOverGeneratedMainSourcesSourceSet")
            ?: error("dfbuild.kodex could not find task :runKtlintFormatOverGeneratedMainSourcesSourceSet"),
    )
}

// Alias for processKDocsMain
val kodex = tasks.register("kodex") {
    group = "KoDEx"
    dependsOn(processKDocsMain)
}

// Skips generatedMainSources KtLint check on "normal" KtLint runs.
// The checks run automatically after `processKDocsMain`
tasks.named("ktlintGeneratedMainSourcesSourceSetCheck") {
    onlyIf { false }
}
tasks.named("runKtlintCheckOverGeneratedMainSourcesSourceSet") {
    onlyIf { false }
}

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(extension.generatedSourcesFolderName.get()))
    }
}

// If `changeJarTask` is run, modify all Jar tasks such that before running the Kotlin sources are set to
// the target of `processKdocMain`, and they are returned to normal afterward.
// This is usually only done when publishing
val changeJarTask = tasks.register("changeJarTask") {
    outputs.upToDateWhen { project.hasProperty("skipKodex") }
    doFirst {
        tasks.withType<Jar> {

            // Making sure additional source files are allowed to be overwritten by the KoDEx version,
            // such as BuildConfig
            duplicatesStrategy = DuplicatesStrategy.WARN

            doFirst {
                require(generatedMainSources.kotlin.srcDirs.toList().isNotEmpty()) {
                    logger.error("`processKDocsMain`'s outputs are empty, did `processKDocsMain` run before this task?")
                }
                kotlin.sourceSets.main {
                    kotlin.setSrcDirs(generatedMainSources.kotlin.srcDirs)
                }
                logger.lifecycle(
                    "$this is run with KoDEx modified sources: \"${extension.generatedSourcesFolderName.get()}\"",
                )
                logger.info(
                    "KoDEx modified sourceDirs: ${kotlin.sourceSets.main.get().kotlin.srcDirs.toList()}",
                )
            }

            doLast {
                kotlin.sourceSets.main {
                    kotlin.setSrcDirs(extension.kotlinMainSourcesDirectories.get())
                }
                logger.info(
                    "$this: KoDEx restored sourceDirs: ${kotlin.sourceSets.main.get().kotlin.srcDirs.toList()}",
                )
            }
        }
    }
}

// if `processKDocsMain` runs, the Jar tasks must run after it so the generated-sources are there
tasks.withType<Jar> {
    mustRunAfter(changeJarTask, processKDocsMain)
}

// modify all publishing tasks to depend on `changeJarTask` so the sources are swapped out with generated sources
tasks.configureEach {
    if (!project.hasProperty("skipKodex") && name.startsWith("publish")) {
        dependsOn(processKDocsMain, changeJarTask)
    }
}

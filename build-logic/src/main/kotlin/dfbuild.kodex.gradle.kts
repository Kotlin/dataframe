import dfbuild.findRootDir
import nl.jolanrensen.kodex.gradle.RunKodexTask

plugins {
    alias(convention.plugins.kotlinJvmCommon)

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
     * By default, this contains all source directories of `kotlin.sourceSets.test`.
     */
    val contextualSourcesDirectories: SetProperty<File>

    val generatedSourcesFolderName: Property<String>
}

val extension = project.extensions.create<KodexConventionExtension>("kodexConvention")
    .apply {
        generatedSourcesFolderName.convention("generated-sources")

        // this is set in afterEvaluate to any modifications to the main/test sourceSets are present
        afterEvaluate {
            kotlinMainSourcesDirectories.convention(
                kotlin.sourceSets.main.get()
                    .kotlin
                    .sourceDirectories
                    // important! This clones the collection
                    .toSet(),
            )

            contextualSourcesDirectories.convention(
                kotlin.sourceSets.test.get()
                    .kotlin
                    .sourceDirectories
                    // important! This clones the collection
                    .toSet(),
            )
        }
    }

fun pathOf(vararg parts: String) = parts.joinToString(File.separator)

// main sourceset of the generated sources as a result of `processKDocsMain`, this will create linter tasks
// This also makes sure the contextual sources are not in the final jar
val generatedMainSources by kotlin.sourceSets.creating {
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
val processKDocsMain by tasks.registering(RunKodexTask::class) {
    // Include both test and main sources for cross-referencing, plus contextualSourcesDirectories
    sources = buildSet<File> {
        this += extension.kotlinMainSourcesDirectories.get()
        this += extension.contextualSourcesDirectories.get()
    }.also {
        logger.info("$name: Preprocessing sources with KoDEx: ${it.toList()}")
    }
    group = "KDocs"
    target = file(extension.generatedSourcesFolderName.get())

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
val kodex by tasks.registering {
    group = "KDocs"
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
val changeJarTask by tasks.registering {
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

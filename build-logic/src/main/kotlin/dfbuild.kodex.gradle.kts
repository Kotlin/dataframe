import dfbuild.findRootDir
import nl.jolanrensen.kodex.gradle.RunKodexTask

plugins {
    alias(convention.plugins.kotlinJvmCommon)

    alias(libs.plugins.kodex)
    idea
}

// TODO migrate to kodex {} extension syntax. #985

interface KodexConventionExtension {

    /**
     * KoDEx pick up the Kotlin `main` and `test` SourceSets.
     * Any modifications or additions need to be specified here.
     */
    val extraSourcesForKodex: ListProperty<File>

    /**
     * If extra sources need to be added to the jar but not processed by KoDEx,
     * specify them here.
     */
    val extraSourcesForJar: ListProperty<File>
}

val extension = project.extensions.create<KodexConventionExtension>("kodexConvention")
    .also {
        it.extraSourcesForKodex.convention(emptyList())
        it.extraSourcesForJar.convention(emptyList())
    }

val generatedSourcesFolderName = "generated-sources"

// Backup the kotlin source files location
val kotlinMainSources = kotlin.sourceSets.main
    .get()
    .kotlin.sourceDirectories
    .toList()
val kotlinTestSources = kotlin.sourceSets.test
    .get()
    .kotlin.sourceDirectories
    .toList()

fun pathOf(vararg parts: String) = parts.joinToString(File.separator)

// sourceset of the generated sources as a result of `processKDocsMain`, this will create linter tasks
val generatedSources by kotlin.sourceSets.creating {
    kotlin {
        setSrcDirs(
            listOf(
                "$generatedSourcesFolderName/src/main/kotlin",
                "$generatedSourcesFolderName/src/main/java",
            ),
        )
        srcDirs(extension.extraSourcesForJar)
    }
}

// Task to generate the processed documentation
val processKDocsMain by tasks.registering(RunKodexTask::class) {

    // Include both test and main sources for cross-referencing, plus extraSourcesForKodex
    sources = buildSet {
        this += kotlinMainSources
        this += kotlinTestSources
        this += extension.extraSourcesForKodex.get()
    }
    group = "KDocs"
    target = file(generatedSourcesFolderName)

    // false, so `ktlintGeneratedSourcesSourceSetFormat` can format the output
    outputReadOnly = false

    exportAsHtml {
        dir = findRootDir().absoluteFile.resolve("docs/StardustDocs/resources/snippets/kdocs")
    }
    finalizedBy(
        tasks.findByName("runKtlintFormatOverGeneratedSourcesSourceSet")
            ?: error("dfbuild.kodex could not find task :runKtlintFormatOverGeneratedSourcesSourceSet"),
    )
}

// Alias for processKDocsMain
val kodex by tasks.registering { dependsOn(processKDocsMain) }

// Skips generatedSources KtLint check on "normal" KtLint runs.
// The checks run automatically after `processKDocsMain`
tasks.named("ktlintGeneratedSourcesSourceSetCheck") {
    onlyIf { false }
}
tasks.named("runKtlintCheckOverGeneratedSourcesSourceSet") {
    onlyIf { false }
}

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(generatedSourcesFolderName))
    }
}

// If `changeJarTask` is run, modify all Jar tasks such that before running the Kotlin sources are set to
// the target of `processKdocMain`, and they are returned to normal afterward.
// This is usually only done when publishing
val changeJarTask by tasks.registering {
    outputs.upToDateWhen { project.hasProperty("skipKodex") }
    doFirst {
        tasks.withType<Jar> {
            doFirst {
                require(generatedSources.kotlin.srcDirs.toList().isNotEmpty()) {
                    logger.error("`processKDocsMain`'s outputs are empty, did `processKDocsMain` run before this task?")
                }
                kotlin.sourceSets.main {
                    kotlin.setSrcDirs(generatedSources.kotlin.srcDirs)
                }
                logger.lifecycle("$this is run with modified sources: \"$generatedSourcesFolderName\"")
            }

            doLast {
                kotlin.sourceSets.main {
                    kotlin.setSrcDirs(kotlinMainSources)
                }
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

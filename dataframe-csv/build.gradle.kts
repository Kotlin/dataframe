import nl.jolanrensen.kodex.gradle.creatingRunKodexTask
import org.gradle.jvm.tasks.Jar

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization)
        alias(kover)
        alias(ktlint)
        alias(kodex)
        alias(binary.compatibility.validator)
        alias(kotlinx.benchmark)
    }
    idea
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(projects.core)

    // for reading/writing JSON <-> DataFrame/DataRow in CSV/TSV/Delim
    // can safely be excluded when working without JSON and only writing flat dataframes
    api(projects.dataframeJson)

    // for csv reading
    api(libs.deephavenCsv)
    // for csv writing
    api(libs.commonsCsv)
    implementation(libs.commonsIo)
    implementation(libs.sl4j)
    implementation(libs.kotlinLogging)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.kotlinx.benchmark.runtime)
    testImplementation(libs.junit)
    testImplementation(libs.sl4jsimple)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

benchmark {
    targets {
        register("test")
    }
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

// Include both test and main sources for cross-referencing, Exclude generated sources
val processKDocsMainSources = (kotlinMainSources + kotlinTestSources)
    .filterNot { pathOf("build", "generated") in it.path }

// sourceset of the generated sources as a result of `processKDocsMain`, this will create linter tasks
val generatedSources by kotlin.sourceSets.creating {
    kotlin {
        setSrcDirs(
            listOf(
                "$generatedSourcesFolderName/src/main/kotlin",
                "$generatedSourcesFolderName/src/main/java",
            ),
        )
    }
}

// Task to generate the processed documentation
val processKDocsMain by creatingRunKodexTask(processKDocsMainSources) {
    group = "KDocs"
    target = file(generatedSourcesFolderName)

    // false, so `runKtlintFormatOverGeneratedSourcesSourceSet` can format the output
    outputReadOnly = false

    exportAsHtml {
        dir = file("../docs/StardustDocs/resources/snippets/kdocs")
    }
    finalizedBy("runKtlintFormatOverGeneratedSourcesSourceSet")
}

tasks.named("ktlintGeneratedSourcesSourceSetCheck") {
    onlyIf { false }
}
tasks.named("runKtlintCheckOverGeneratedSourcesSourceSet") {
    onlyIf { false }
}

// If `changeJarTask` is run, modify all Jar tasks such that before running the Kotlin sources are set to
// the target of `processKdocMain`, and they are returned to normal afterward.
// This is usually only done when publishing
val changeJarTask by tasks.registering {
    outputs.upToDateWhen { false }
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
    if (name.startsWith("publish")) {
        dependsOn(processKDocsMain, changeJarTask)
    }
}

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(generatedSourcesFolderName))
    }
}

kotlinPublications {
    publication {
        publicationName = "dataframeCsv"
        artifactId = project.name
        description = "CSV support for Kotlin Dataframe"
        packageName = artifactId
    }
}

kotlin {
    explicitApi()
}

val instrumentedJars: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add("instrumentedJars", tasks.jar.get().archiveFile) {
        builtBy(tasks.jar)
    }
}

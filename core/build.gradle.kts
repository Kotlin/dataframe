import com.google.devtools.ksp.gradle.KspTask
import com.google.devtools.ksp.gradle.KspTaskJvm
import io.github.devcrocod.korro.KorroTask
import nl.jolanrensen.kodex.gradle.creatingRunKodexTask
import org.gradle.jvm.tasks.Jar
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization)
        alias(korro)
//        alias(kover)
        alias(ktlint)
        alias(kodex)
        alias(buildconfig)
        alias(binary.compatibility.validator)

        // generates keywords using the :generator module
        alias(keywordGenerator)

        // dependence on our own plugin
        alias(dataframe)

        // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
        alias(ksp)
    }
    idea
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin.sourceSets {
    main {
        kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
    test {
        kotlin.srcDir("build/generated/ksp/test/kotlin/")
    }
}

sourceSets {
    // Gradle creates configurations and compilation task for each source set
    create("samples") {
        kotlin.srcDir("src/test/kotlin")
    }
}

dependencies {
    val kotlinCompilerPluginClasspathSamples by configurations.getting

    api(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    kotlinCompilerPluginClasspathSamples(projects.plugins.expressionsConverter)

    api(libs.commonsCsv)

    implementation(libs.commonsIo)
    implementation(libs.fastDoubleParser)

    api(libs.kotlin.datetimeJvm)
    implementation(libs.kotlinpoet)
    implementation(libs.sl4j)
    implementation(libs.kotlinLogging)

    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kotlin.scriptingJvm)
    testImplementation(libs.jsoup)
    testImplementation(libs.sl4jsimple)
    testImplementation(projects.dataframeJson)
    testImplementation(libs.serialization.core)
    testImplementation(libs.serialization.json)

    // for checking results
    testImplementation(libs.commonsStatisticsDescriptive)

    // for samples.api
    testImplementation(projects.dataframeCsv)
}

val samplesImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val compileSamplesKotlin = tasks.named<KotlinCompile>("compileSamplesKotlin") {
    tasks.named<KotlinCompile>("compileTestKotlin").get().let {
        friendPaths.from(it.friendPaths)
        libraries.from(it.libraries)
    }
    source(sourceSets["test"].kotlin)
    destinationDirectory = layout.buildDirectory.dir("classes/testWithOutputs/kotlin")
}

tasks.withType<KspTask> {
    // "test" classpath is re-used, so repeated generation should be disabled
    if (name == "kspSamplesKotlin") {
        dependsOn("kspTestKotlin")
        enabled = false
    }
}

val clearTestResults by tasks.registering(Delete::class, fun Delete.() {
    delete(layout.buildDirectory.dir("dataframes"))
    delete(layout.buildDirectory.dir("korroOutputLines"))
})

val samplesTest = tasks.register<Test>("samplesTest") {
    group = "Verification"
    description = "Runs all samples that are used in the documentation, but modified to save their outputs to a file."

    dependsOn(compileSamplesKotlin)
    dependsOn(clearTestResults)
    outputs.upToDateWhen { false }

    environment("DATAFRAME_SAVE_OUTPUTS", "")

    filter {
        includeTestsMatching("org.jetbrains.kotlinx.dataframe.samples.api.*")
    }

    ignoreFailures = true

    testClassesDirs = fileTree("${layout.buildDirectory.get().asFile.path}/classes/testWithOutputs/kotlin")
    classpath =
        files("${layout.buildDirectory.get().asFile.path}/classes/testWithOutputs/kotlin") +
        configurations["samplesRuntimeClasspath"] +
        sourceSets["main"].runtimeClasspath
}

val clearSamplesOutputs by tasks.registering {
    group = "documentation"

    doFirst {
        delete {
            val generatedSnippets = fileTree(file("../docs/StardustDocs/resources/snippets"))
                .exclude("**/manual/**", "**/kdocs/**")
            delete(generatedSnippets)
        }
    }
}

val copySamplesOutputs = tasks.register<JavaExec>("copySamplesOutputs") {
    group = "documentation"
    mainClass = "org.jetbrains.kotlinx.dataframe.explainer.SampleAggregatorKt"

    dependsOn(clearSamplesOutputs)
    dependsOn(samplesTest)
    classpath = sourceSets.test.get().runtimeClasspath
}

tasks.withType<KorroTask> {
    dependsOn(copySamplesOutputs)
}

// region docPreprocessor

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
                "build/generated/ksp/main/kotlin/",
                "core/build/generatedSrc",
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

// generateLibrariesJson makes sure a META-INF/kotlin-jupyter-libraries/libraries.json file is generated
// This file allows loading dataframe-jupyter when dataframe-core is present on its own in a Kotlin Notebook.
val generatedJupyterResourcesDir = layout.buildDirectory.dir("generated/jupyter")
val generateLibrariesJson by tasks.registering {
    val outDir = generatedJupyterResourcesDir.get().asFile.resolve("META-INF/kotlin-jupyter-libraries")
    val outFile = outDir.resolve("libraries.json")
    outputs.file(outFile)
    inputs.property("version", project.version)

    doLast {
        outDir.mkdirs()
        @Language("json")
        val content =
            """
            {
              "descriptors": [
                {
                  "init": [
                    "USE { dependencies(\"org.jetbrains.kotlinx:dataframe-jupyter:${project.version}\") }"
                  ]
                }
              ]
            }
            """.trimIndent()

        outFile.delete()
        outFile.writeText(content)
        logger.lifecycle("generated META-INF/kotlin-jupyter-libraries/libraries.json for :core")
    }
}

// If `includeCoreLibrariesJson` is set, modify the processResources task such that it includes
// a META-INF libraries.json file.
// This file allows loading dataframe-jupyter when dataframe-core is present on its own in a Kotlin Notebook.
// This is usually only done when publishing.
tasks.processResources {
    if (project.hasProperty("includeCoreLibrariesJson")) {
        dependsOn(generateLibrariesJson)
        from(generatedJupyterResourcesDir) {
            into("") // keep META-INF/... structure as generated
        }
        doLast {
            logger.lifecycle("$this includes generated META-INF/kotlin-jupyter-libraries/libraries.json")
        }
    }
}

// if `processKDocsMain` runs, the Jar tasks must run after it so the generated-sources are there
tasks.withType<Jar> {
    mustRunAfter(changeJarTask, tasks.generateKeywordsSrc, processKDocsMain)
}

// modify all publishing tasks to depend on `changeJarTask` so the sources are swapped out with generated sources
tasks.configureEach {
    if (!project.hasProperty("skipKodex") && name.startsWith("publish")) {
        dependsOn(processKDocsMain, changeJarTask)
    }
}

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(generatedSourcesFolderName))
    }
}

// If we want to use Dokka, make sure to use the preprocessed sources
tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask> {
    dependsOn(processKDocsMain)
    dokkaSourceSets {
        all {
            sourceRoot(processKDocsMain.target.get())
        }
    }
}

// endregion

korro {
    docs = fileTree(rootProject.rootDir) {
        include("docs/StardustDocs/topics/*.md")
    }

    samples = fileTree(project.projectDir) {
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt")
    }

    outputs = fileTree(project.layout.buildDirectory) {
        include("korroOutputLines/*")
    }

    groupSamples {

        beforeSample = "<tab title=\"NAME\">\n"
        afterSample = "\n</tab>"

        funSuffix("_properties") {
            replaceText("NAME", "Properties")
        }
        funSuffix("_strings") {
            replaceText("NAME", "Strings")
        }
        beforeGroup = "<tabs>\n"
        afterGroup = "</tabs>"
    }
}

tasks.withType<KspTaskJvm> {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.runKtlintFormatOverMainSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspKotlin")
}

tasks.runKtlintFormatOverTestSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspTestKotlin")
}

tasks.named("runKtlintFormatOverGeneratedSourcesSourceSet") {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspKotlin")
}

tasks.runKtlintCheckOverMainSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspKotlin")
}

tasks.runKtlintCheckOverTestSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspTestKotlin")
}

tasks.named("runKtlintCheckOverGeneratedSourcesSourceSet") {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspKotlin")
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        optIn.addAll("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll("-Xinline-classes")
        freeCompilerArgs.addAll("-Xjvm-default=all")
    }
}

tasks.test {
    maxHeapSize = "2048m"
//    kover {
//        currentProject {
//            instrumentation { disabledForTestTasks.addAll("samplesTest") }
//        }
//        reports {
//            total {
//                filters {
//                    excludes {
//                        classes("org.jetbrains.kotlinx.dataframe.jupyter.*")
//                        classes("org.jetbrains.kotlinx.dataframe.jupyter.SampleNotebooksTests")
//                    }
//                }
//            }
//        }
//    }
}

kotlinPublications {
    publication {
        publicationName = "core"
        artifactId = "dataframe-core"
        description = "Dataframe core API"
        packageName = artifactId
    }
}

// Disable and enable if updating plugin breaks the build
dataframes {
    schema {
        sourceSet = "test"
        visibility = org.jetbrains.dataframe.gradle.DataSchemaVisibility.IMPLICIT_PUBLIC
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        name = "org.jetbrains.kotlinx.dataframe.samples.api.Repository"
    }
}

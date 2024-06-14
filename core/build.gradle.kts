import com.google.devtools.ksp.gradle.KspTask
import com.google.devtools.ksp.gradle.KspTaskJvm
import io.github.devcrocod.korro.KorroTask
import nl.jolanrensen.docProcessor.defaultProcessors.ARG_DOC_PROCESSOR_LOG_NOT_FOUND
import nl.jolanrensen.docProcessor.gradle.creatingProcessDocTask
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.LintTask
import xyz.ronella.gradle.plugin.simple.git.task.GitTask

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization)
        alias(jupyter.api)
        alias(korro)
        alias(keywordGenerator)
        alias(kover)
        alias(kotlinter)
        alias(docProcessor)
        alias(simpleGit)

        // dependence on our own plugin
        alias(dataframe)

        // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
        alias(ksp)
    }
    idea
}

group = "org.jetbrains.kotlinx"

val jupyterApiTCRepo: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(jupyterApiTCRepo)
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
    kotlinCompilerPluginClasspathSamples(project(":plugins:expressions-converter"))
    implementation(libs.kotlin.stdlib.jdk8)

    api(libs.commonsCsv)
    implementation(libs.serialization.core)
    implementation(libs.serialization.json)

    implementation(libs.fuel)

    api(libs.kotlin.datetimeJvm)
    implementation(libs.kotlinpoet)

    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kotlin.scriptingJvm)
    testImplementation(libs.jsoup)
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
    destinationDirectory.set(layout.buildDirectory.dir("classes/testWithOutputs/kotlin"))
}

tasks.withType<KspTask> {
    // "test" classpath is re-used, so repeated generation should be disabled
    if (name == "kspSamplesKotlin") {
        dependsOn("kspTestKotlin")
        enabled = false
    }
}

tasks.named("lintKotlinSamples") {
    onlyIf { false }
}

val clearTestResults by tasks.creating(Delete::class) {
    delete(layout.buildDirectory.dir("dataframes"))
    delete(layout.buildDirectory.dir("korroOutputLines"))
}

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

val clearSamplesOutputs by tasks.creating {
    group = "documentation"

    doFirst {
        delete {
            val generatedSnippets = fileTree(file("../docs/StardustDocs/snippets")).exclude("**/manual/**")
            delete(generatedSnippets)
        }
    }
}

val addSamplesToGit by tasks.creating(GitTask::class) {
    directory.set(file("."))
    command.set("add")
    args.set(listOf("-A", "../docs/StardustDocs/snippets"))
}

val copySamplesOutputs = tasks.register<JavaExec>("copySamplesOutputs") {
    group = "documentation"
    mainClass.set("org.jetbrains.kotlinx.dataframe.explainer.SampleAggregatorKt")

    dependsOn(clearSamplesOutputs)
    dependsOn(samplesTest)
    classpath = sourceSets.test.get().runtimeClasspath

    doLast {
        addSamplesToGit.executeCommand()
    }
}

tasks.withType<KorroTask> {
    dependsOn(copySamplesOutputs)
}

// region docPreprocessor

val generatedSourcesFolderName = "generated-sources"

// Backup the kotlin source files location
val kotlinMainSources: FileCollection = kotlin.sourceSets.main.get().kotlin.sourceDirectories
val kotlinTestSources: FileCollection = kotlin.sourceSets.test.get().kotlin.sourceDirectories

fun pathOf(vararg parts: String) = parts.joinToString(File.separator)

// Include both test and main sources for cross-referencing, Exclude generated sources
val processKDocsMainSources = (kotlinMainSources + kotlinTestSources)
    .filterNot { pathOf("build", "generated") in it.path }

// Task to generate the processed documentation
val processKDocsMain by creatingProcessDocTask(processKDocsMainSources) {
    target = file(generatedSourcesFolderName)
    arguments += ARG_DOC_PROCESSOR_LOG_NOT_FOUND to false
    exportAsHtml {
        dir = file("../docs/StardustDocs/snippets")
    }
    task {
        group = "KDocs"
    }
}

// Exclude the generated/processed sources from the IDE
idea {
    module {
        excludeDirs.add(file(generatedSourcesFolderName))
    }
}

// Modify all Jar tasks such that before running the Kotlin sources are set to
// the target of processKdocMain and they are returned back to normal afterwards.
tasks.withType<Jar> {
    dependsOn(processKDocsMain)
    mustRunAfter(tasks.generateKeywordsSrc)
    outputs.upToDateWhen { false }

    doFirst {
        kotlin.sourceSets.main {
            kotlin.setSrcDirs(
                processKDocsMain.targets
                    .filterNot {
                        pathOf("src", "test", "kotlin") in it.path ||
                            pathOf("src", "test", "java") in it.path
                    } // filter out test sources again
                    .plus(
                        kotlinMainSources.filter {
                            pathOf("build", "generated") in it.path
                        },
                    ), // Include generated sources (which were excluded above)
            )
        }
    }

    doLast {
        kotlin.sourceSets.main {
            kotlin.setSrcDirs(kotlinMainSources)
        }
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

        beforeSample.set("<tab title=\"NAME\">\n")
        afterSample.set("\n</tab>")

        funSuffix("_properties") {
            replaceText("NAME", "Properties")
        }
        funSuffix("_accessors") {
            replaceText("NAME", "Accessors")
        }
        funSuffix("_strings") {
            replaceText("NAME", "Strings")
        }
        beforeGroup.set("<tabs>\n")
        afterGroup.set("</tabs>")
    }
}

tasks.withType<KspTaskJvm> {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.formatKotlinMain {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspKotlin")
}

tasks.formatKotlinTest {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspTestKotlin")
}

tasks.lintKotlinMain {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspKotlin")
}

tasks.lintKotlinTest {
    dependsOn(tasks.generateKeywordsSrc)
    dependsOn("kspTestKotlin")
}

tasks.withType<LintTask> {
    exclude("**/*keywords*/**")
    exclude {
        it.name.endsWith(".Generated.kt")
    }
    exclude {
        it.name.endsWith("\$Extensions.kt")
    }
    enabled = true
}

kotlin {
    explicitApi()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks.test {
    maxHeapSize = "2048m"
    extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
        excludes.set(
            listOf(
                "org.jetbrains.kotlinx.dataframe.jupyter.*",
                "org.jetbrains.kotlinx.dataframe.jupyter.SampleNotebooksTests",
            )
        )
    }
}

tasks.processJupyterApiResources {
    libraryProducers = listOf("org.jetbrains.kotlinx.dataframe.jupyter.Integration")
}

kotlinPublications {
    publication {
        publicationName.set("core")
        artifactId.set("dataframe-core")
        description.set("Dataframe core API")
        packageName.set(artifactId)
    }
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

// Disable and enable if updating plugin breaks the build
dataframes {
    schema {
        sourceSet = "test"
        visibility = org.jetbrains.dataframe.gradle.DataSchemaVisibility.IMPLICIT_PUBLIC
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        name = "org.jetbrains.kotlinx.dataframe.samples.api.Repository"
    }
}

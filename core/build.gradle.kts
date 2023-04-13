import com.google.devtools.ksp.gradle.KspTaskJvm
import com.google.devtools.ksp.gradle.KspTask
import nl.jolanrensen.docProcessor.defaultProcessors.*
import nl.jolanrensen.docProcessor.gradle.creatingProcessDocTask
import org.gradle.jvm.tasks.Jar
import org.jmailen.gradle.kotlinter.tasks.LintTask
import xyz.ronella.gradle.plugin.simple.git.task.GitTask

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
    kotlin("plugin.serialization")
    kotlin("jupyter.api")

    id("io.github.devcrocod.korro") version libs.versions.korro
    id("org.jetbrains.dataframe.generator")
    id("org.jetbrains.kotlinx.kover")
    id("org.jmailen.kotlinter")
    id("org.jetbrains.kotlinx.dataframe")
    id("nl.jolanrensen.docProcessor")
    id("xyz.ronella.simple-git")
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
    create("myTest") {
        kotlin.srcDir("src/test/kotlin")
    }
}

dependencies {
    val kotlinCompilerPluginClasspathMyTest by configurations.getting

    api(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    kotlinCompilerPluginClasspathMyTest(project(":plugins:expressions-converter"))
    implementation(libs.kotlin.stdlib.jdk8)

    api(libs.commonsCsv)
    implementation(libs.klaxon)
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

val myTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val myKotlinCompileTask = tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileMyTestKotlin") {
    friendPaths.from(sourceSets["main"].output.classesDirs)
    source(sourceSets["test"].kotlin)
    destinationDirectory.set(file("$buildDir/classes/testWithOutputs/kotlin"))
}

tasks.withType<KspTask> {
    // "test" classpath is re-used, so repeated generation should be disabled
    if (name == "kspMyTestKotlin") {
        dependsOn("kspTestKotlin")
        enabled = false
    }
}

tasks.named("lintKotlinMyTest") {
    onlyIf { false }
}

val customTest = tasks.register<Test>("customTest") {
    group = "Verification"
    description = "Runs the custom tests."

    dependsOn(myKotlinCompileTask)

    doFirst {
        delete {
            delete(fileTree(File(buildDir, "dataframes")))
        }
    }

    environment("DATAFRAME_SAVE_OUTPUTS", "")

    filter {
        includeTestsMatching("org.jetbrains.kotlinx.dataframe.samples.api.*")
    }

    ignoreFailures = true

    testClassesDirs = fileTree("$buildDir/classes/testWithOutputs/kotlin")
    classpath = files("$buildDir/classes/testWithOutputs/kotlin") + configurations["myTestRuntimeClasspath"] + sourceSets["main"].runtimeClasspath
}

val copySamplesOutputs = tasks.register<JavaExec>("copySamplesOutputs") {
    group = "documentation"
    mainClass.set("org.jetbrains.kotlinx.dataframe.explainer.SampleAggregatorKt")

    doFirst {
        delete {
            delete(fileTree(File(projectDir, "../docs/StardustDocs/snippets")))
        }
    }

    dependsOn(customTest)
    classpath = sourceSets.test.get().runtimeClasspath
}

val generatedSourcesFolderName = "generated-sources"
val addGeneratedSourcesToGit by tasks.creating(GitTask::class) {
    directory.set(file("."))
    command.set("add")
    args.set(listOf("-A", generatedSourcesFolderName))
}

// Backup the kotlin source files location
val kotlinMainSources = kotlin.sourceSets.main.get().kotlin.sourceDirectories
val kotlinTestSources = kotlin.sourceSets.test.get().kotlin.sourceDirectories

fun pathOf(vararg parts: String) = parts.joinToString(File.separator)

// Task to generate the processed documentation
val processKDocsMain by creatingProcessDocTask(
    sources = (kotlinMainSources + kotlinTestSources) // Include both test and main sources for cross-referencing
        .filterNot { pathOf("build", "generated") in it.path }, // Exclude generated sources
) {
    target = file(generatedSourcesFolderName)
    processors = listOf(
        INCLUDE_DOC_PROCESSOR,
        INCLUDE_FILE_DOC_PROCESSOR,
        INCLUDE_ARG_DOC_PROCESSOR,
        COMMENT_DOC_PROCESSOR,
        SAMPLE_DOC_PROCESSOR,
    )

    task {
        doLast {
            // ensure generated sources are added to git
            addGeneratedSourcesToGit.executeCommand()
        }
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
    outputs.upToDateWhen { false }

    doFirst {
        kotlin.sourceSets.main {
            kotlin.setSrcDirs(
                processKDocsMain.targets
                    .filterNot {
                        pathOf("src", "test", "kotlin") in it.path ||
                            pathOf("src", "test", "java") in it.path
                    } // filter out test sources again
                    .plus(kotlinMainSources.filter {
                        pathOf("build", "generated") in it.path
                    }) // Include generated sources (which were excluded above)
            )
        }
    }

    doLast {
        kotlin.sourceSets.main {
            kotlin.setSrcDirs(kotlinMainSources)
        }
    }
}

korro {
    docs = fileTree(rootProject.rootDir) {
        include("docs/StardustDocs/topics/*.md")
    }

    samples = fileTree(project.projectDir) {
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt")
    }

    outputs = fileTree(project.projectDir) {
        include("outputs/")
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

kotlinter {
    ignoreFailures = false
    reporters = arrayOf("checkstyle", "plain")
    experimentalRules = true
    disabledRules = arrayOf(
        "no-wildcard-imports",
        "experimental:spacing-between-declarations-with-annotations",
        "experimental:enum-entry-name-case",
        "experimental:argument-list-wrapping",
        "experimental:annotation",
        "max-line-length",
        "filename",
        "comment-spacing",
        "curly-spacing",
        "experimental:annotation-spacing"
    )
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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
                "org.jetbrains.kotlinx.dataframe.jupyter.SampleNotebooksTests"
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

// If we want to use Dokka, make sure to use the preprocessed sources
tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask> {
    dependsOn(processKDocsMain)
    dokkaSourceSets {
        all {
            sourceRoot(processKDocsMain.target.get())
        }
    }
}

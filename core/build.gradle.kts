import io.github.devcrocod.korro.KorroTask
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(convention.plugins) {
        alias(kotlinJvm8)
        alias(buildConfig)
        alias(kodex)
    }
    with(libs.plugins) {
        alias(publisher)
        alias(serialization)
        alias(korro)
        alias(binary.compatibility.validator)
        alias(kotlinx.benchmark)

        // generates keywords using the :generator module
        alias(keywordGenerator)
    }
    idea
}

group = "org.jetbrains.kotlinx"

kotlin.sourceSets {
    main {
        kotlin.srcDir("src/generated-dataschema-accessors/main/kotlin/")
    }
    test {
        kotlin.srcDir("src/generated-dataschema-accessors/test/kotlin/")
    }
}

kodexConvention {
    // we modified kotlin.sourceSets.(main|test); letting `dfbuild.kodex` know
    // these will all be processed by KoDEx ánd end up in the final jar.
    extraSourcesForKodex =
        kotlin.sourceSets
            .run { listOf(main, test) }
            .flatMap {
                it.get().kotlin.sourceDirectories.toList()
            }
    // making sure the generated keywords are part of the generatedSources SourceSet
    // as that's the SourceSet that ends up in the final jars. These will not be preprocessed by KoDEx.
    extraSourcesForJar = listOf(file("core/build/generatedSrc"))
}

sourceSets {
    // Gradle creates configurations and compilation task for each source set
    create("samples") {
        kotlin.srcDir("src/test/kotlin")
    }
}

// Separate source set for Java 16+ language-specific tests (e.g., Java Records)
val testJava16 by sourceSets.creating {
    java.srcDir("src/testJava16/java")
    kotlin.srcDir("src/testJava16/kotlin")
    compileClasspath += sourceSets.main.get().output + configurations.testCompileClasspath.get()
    runtimeClasspath += output + compileClasspath + configurations.testRuntimeClasspath.get()
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
    // kotest shouldBe forcefully escapes newlines when content is identical except additional newlines
    // which makes diff very hard to perceive. CodeGenerationTests.kt suffers from it a lot. using assertEquals there for working diff.
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.benchmark.runtime)
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

// Configure testJava16 dependencies to extend from test
configurations {
    val testJava16Implementation by getting {
        extendsFrom(configurations.testImplementation.get())
    }
    val testJava16RuntimeOnly by getting {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
}

// Configure testJava16 sources to use Java 16+
tasks.named<JavaCompile>("compileTestJava16Java") {
    sourceCompatibility = JavaVersion.VERSION_16.toString()
    targetCompatibility = JavaVersion.VERSION_16.toString()
    options.release.set(16)
}

tasks.named<KotlinCompile>("compileTestJava16Kotlin") {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_16
        freeCompilerArgs.add("-Xjdk-release=16")
    }
}

benchmark {
    targets {
        register("test")
    }
    configurations {
        register("sort") {
            include("SortingBenchmark")
        }
    }
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

korro {
    docs = fileTree(rootProject.rootDir) {
        include("docs/StardustDocs/topics/*.md")
        include("docs/StardustDocs/topics/concepts/*.md")
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

tasks.withType<Jar> {
    mustRunAfter(tasks.generateKeywordsSrc)
}

tasks.runKtlintFormatOverMainSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.runKtlintFormatOverTestSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.named("runKtlintFormatOverGeneratedSourcesSourceSet") {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.runKtlintCheckOverMainSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.runKtlintCheckOverTestSourceSet {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.named("runKtlintCheckOverGeneratedSourcesSourceSet") {
    dependsOn(tasks.generateKeywordsSrc)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        optIn.addAll("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll("-Xinline-classes")
        freeCompilerArgs.addAll("-jvm-default=no-compatibility")
    }
}

tasks.test {
    maxHeapSize = "1g"
}

// Test task for Java 16+ language-specific tests
val testJava16Task = tasks.register<Test>("testJava16") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    testClassesDirs = testJava16.output.classesDirs
    classpath = testJava16.runtimeClasspath

    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(17)
    }

    maxHeapSize = "2048m"
}

tasks.check {
    dependsOn(testJava16Task)
}

kotlinPublications {
    publication {
        publicationName = "core"
        artifactId = "dataframe-core"
        description = "Dataframe core API"
        packageName = artifactId
    }
}

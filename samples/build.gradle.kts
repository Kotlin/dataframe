import dfbuild.localDataFrameModuleDependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(korro)
        alias(ktlint)
        alias(dataframe.compiler.plugin)
//        alias(kover)
        alias(ksp)
    }
    id("dfbuild.local-dataframe-compiler-plugin")
}

repositories {
    mavenCentral()
    mavenLocal() // for local development
}

tasks.withType<KotlinCompile>().configureEach {
    friendPaths.from(project(projects.core.path).projectDir)
}

localDataFrameModuleDependencies(
    with(projects) {
        listOf(
            dataframeArrow,
            dataframeExcel,
            dataframeJdbc,
            dataframeCsv,
            dataframeJson,
        )
    }.map { project(it.path) },
)

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kandy) {
        exclude("org.jetbrains.kotlinx", "dataframe")
    }
    testImplementation(libs.kandy.samples.utils) {
        exclude("org.jetbrains.kotlinx", "dataframe")
    }
    testImplementation(libs.kotlin.datetimeJvm)
    testImplementation(libs.poi)
    testImplementation(libs.arrow.vector)
}

kotlin.sourceSets {
    main {
        kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
    test {
        kotlin.srcDir("build/generated/ksp/test/kotlin/")
    }
}

korro {
    docs = fileTree(rootProject.rootDir) {
        include("docs/StardustDocs/topics/DataSchema-Data-Classes-Generation.md")
        include("docs/StardustDocs/topics/read.md")
        include("docs/StardustDocs/topics/write.md")
        include("docs/StardustDocs/topics/rename.md")
        include("docs/StardustDocs/topics/format.md")
        include("docs/StardustDocs/topics/toHTML.md")
        include("docs/StardustDocs/topics/guides/*.md")
        include("docs/StardustDocs/topics/operations/utils/*.md")
        include("docs/StardustDocs/topics/operations/multiple/*.md")
        include("docs/StardustDocs/topics/operations/column/*.md")
        include("docs/StardustDocs/topics/collectionsInterop/*.md")
        include("docs/StardustDocs/topics/dataSources/sql/*.md")
        include("docs/StardustDocs/topics/info/*.md")
    }

    samples = fileTree(project.projectDir) {
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/utils/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/multiple/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/render/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/collectionsInterop/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/column/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/info/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/guides/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/io/*.kt")
    }

    groupSamples {

        beforeSample = "<tab title=\"NAME\">\n"
        afterSample = "\n</tab>"

        funSuffix("_properties") {
            replaceText("NAME", "Properties")
        }
        funSuffix("_accessors") {
            replaceText("NAME", "Accessors")
        }
        funSuffix("_strings") {
            replaceText("NAME", "Strings")
        }
        beforeGroup = "<tabs>\n"
        afterGroup = "</tabs>"
    }
}

tasks.runKtlintFormatOverMainSourceSet {
    dependsOn("kspKotlin")
}

tasks.runKtlintFormatOverTestSourceSet {
    dependsOn("kspTestKotlin")
}

tasks.runKtlintCheckOverMainSourceSet {
    dependsOn("kspKotlin")
}

tasks.runKtlintCheckOverTestSourceSet {
    dependsOn("kspTestKotlin")
}

tasks.test {
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}

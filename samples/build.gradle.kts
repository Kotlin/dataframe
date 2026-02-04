import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.korro
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.libs
import org.gradle.kotlin.dsl.main
import org.gradle.kotlin.dsl.projects
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.runKtlintCheckOverMainSourceSet
import org.gradle.kotlin.dsl.runKtlintCheckOverTestSourceSet
import org.gradle.kotlin.dsl.runKtlintFormatOverMainSourceSet
import org.gradle.kotlin.dsl.runKtlintFormatOverTestSourceSet
import org.gradle.kotlin.dsl.sourceSets
import org.gradle.kotlin.dsl.test
import org.gradle.kotlin.dsl.testImplementation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(convention.plugins) {
        alias(kotlinJvm11)
        alias(ktlint)
    }
    with(libs.plugins) {
        alias(korro)
        alias(dataframe.compiler.plugin)
    }
}

val dependentProjects = with(projects) {
    listOf(
        core,
        dataframeArrow,
        dataframeExcel,
        dataframeJdbc,
        dataframeCsv,
        dataframeJson,
    )
}.map { project(it.path) }

tasks.withType<KotlinCompile> {
    dependentProjects.forEach {
        dependsOn("${it.path}:jar")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    friendPaths.from(project(projects.core.path).projectDir)
}

// get the output of the instrumentedJars configuration, aka the jar-files of the compiled modules
// all modules with jar-task have this artifact in the DataFrame project
val dependentProjectJarPaths = dependentProjects.map {
    it.configurations
        .getByName("instrumentedJars")
        .artifacts.single()
        .file.absolutePath
        .replace(File.separatorChar, '/')
}

dependencies {
    runtimeOnly(projects.dataframe) // Must depend on jars for the compiler plugin to work!
    implementation(files(dependentProjectJarPaths))

    // include api() dependencies from dependent projects, as they are not included in the jars
    dependentProjects.forEach {
        it.configurations.getByName("api").dependencies.forEach { dep ->
            if (dep is ExternalModuleDependency) {
                implementation("${dep.group}:${dep.name}:${dep.version ?: "+"}")
            }
        }
    }

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

tasks.test {
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}

import io.github.devcrocod.korro.KorroGenerateTask
import org.gradle.kotlin.dsl.libs
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
        dataframeOpenapi,
        dataframeOpenapiGenerator,
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
    testImplementation(libs.hikaricp)
}

// Running tests creates korro output lines!
tasks.withType<KorroGenerateTask>().configureEach {
    dependsOn(tasks.test)
}

korro {

    // TODO(#898)
    // Should work without "missing" errors
    // after migration all test sample to :samples module
    behavior {
        ignoreMissing = true
    }

    docs {
        from(
            fileTree(rootProject.file("docs/StardustDocs/topics")) {
                include("DataSchema-Data-Classes-Generation.md")
                include("read.md")
                include("readSqlFromCustomDatabase.md")
                include("write.md")
                include("rename.md")
                include("format.md")
                include("parse.md")
                include("toHTML.md")
                include("guides/*.md")
                include("concepts/*.md")
                include("schemas/*.md")
                include("operations/utils/*.md")
                include("operations/multiple/*.md")
                include("operations/column/*.md")
                include("collectionsInterop/*.md")
                include("dataSources/*.md")
                include("dataSources/sql/*.md")
                include("readSqlDatabases.md")
                include("info/*.md")
                include("columnArithmetics.md")
                include("groupBy.md")
                include("pivot.md")
            },
        )
        baseDir = rootProject.file("docs/StardustDocs/topics")
    }

    samples {
        from(
            fileTree(project.projectDir) {
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/utils/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/multiple/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/render/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/collectionsInterop/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/column/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/info/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/guides/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/concepts/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/io/*.kt")
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/schemas/*.kt")
            },
        )

        outputs.from(
            fileTree(project.layout.buildDirectory) {
                include("korroOutputLines/*")
            },
        )
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
        funSuffix("_kotlin") {
            replaceText("NAME", "Kotlin")
        }
        funSuffix("_java") {
            replaceText("NAME", "Java")
        }

        beforeGroup = "<tabs>\n"
        afterGroup = "</tabs>"
    }
}

tasks.test {
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}

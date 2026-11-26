import io.github.devcrocod.korro.KorroGenerateTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.libs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files
import kotlin.io.path.div
import kotlin.io.path.name

plugins {
    with(conventions.plugins.dfbuild) {
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
    testImplementation(libs.konsist)
    testImplementation(libs.kotlin.datetimeJvm)
    testImplementation(libs.poi)
    testImplementation(libs.arrow.vector)
    testImplementation(libs.hikaricp)
    testImplementation(libs.postgresql) // PandasPostgresCasesTest, see `pandasPostgresTest`
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
                include("require.md")
                include("format.md")
                include("parse.md")
                include("toHTML.md")
                include("guides/*.md")
                include("concepts/*.md")
                include("io/*.md")
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
                include("convert.md")
                include("convertTo.md")
                include("ColumnSelectors.md")
                include("append.md")
                include("appendNulls.md")
                include("create.md")
                include("createColumn.md")
                include("createDataFrame.md")
                include("concat.md")
                include("cast.md")
                include("concatDf.md")
                include("groupBy.md")
                include("inferType.md")
                include("map.md")
                include("merge.md")
                include("iterate.md")
                include("pivot.md")
                include("countDistinct.md")
                include("fill.md")
                include("drop.md")
                include("duplicate.md")
                include("filter.md")
                include("getColumn.md")
                include("getRow.md")
                include("toList.md")
                include("count.md")
                include("valueCounts.md")
                include("sliceRows.md")
                include("head.md")
                include("sum.md")
                include("mean.md")
                include("std.md")
                include("cumSum.md")
                include("median.md")
                include("percentile.md")
                include("split.md")
                include("MigrationTo_1_0.md")
                include("single.md")
                include("indexing.md")
                include("unfold.md")
                include("replace.md")
                include("reorder.md")
                include("values.md")
                include("operations.md")
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
                include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/io/jdbc/*.kt")
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

// Tests that need a running PostgreSQL: they have their own task, `pandasPostgresTest`, so that `test` needs no database.
val postgresTests = "*PandasPostgresCasesTest*"

tasks.test {
    maxHeapSize = "4g"
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
    filter {
        excludeTestsMatching(postgresTests)
    }

    // PandasReferenceGuardTest reads this page. Without declaring it, an edit of the page alone leaves `test`
    // UP-TO-DATE and the guard does not run.
    inputs.file(rootProject.file("docs/StardustDocs/topics/guides/pandasToKotlinDataFrameReference.md"))
}

// The PostgreSQL cases of the guide for Pandas users (`pandas-validation/README.md`). Needs a throwaway PostgreSQL on
// this machine and its address in PANDAS_POSTGRES_URL, for example postgresql://user:password@127.0.0.1:5432/postgres.
// Not part of `test`/`check`/`build`. The task FAILS without the variable: a database test that did not run is not green.
tasks.register<Test>("pandasPostgresTest") {
    group = "verification"
    description = "Runs the PostgreSQL cases of the guide for Pandas users. " +
        "Needs PANDAS_POSTGRES_URL; not part of test/check/build."

    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    maxHeapSize = "4g"
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
    filter {
        includeTestsMatching(postgresTests)
    }
    // A cached result of an earlier run must not stand for a run against today's database.
    outputs.upToDateWhen { false }

    doFirst {
        if (System.getenv("PANDAS_POSTGRES_URL").isNullOrBlank()) {
            throw GradleException(
                "PANDAS_POSTGRES_URL is not set. Point it at a throwaway local PostgreSQL, for example " +
                    "postgresql://user:password@127.0.0.1:5432/postgres. See samples/pandas-validation/README.md.",
            )
        }
    }
}

val resourcesDir = rootProject.file("docs/StardustDocs/resources")
val mdFile = rootProject.file("docs/StardustDocs/topics/_shadow_resources.md")

tasks.register("updateShadowResources") {
    group = "documentation"
    description = "Updates _shadow_resources.md with resource tags for all files in resources directory"

    val ignore = listOf(".DS_Store")
    doLast {
        val resourceFiles = Files.walk(resourcesDir.toPath())
            .filter { Files.isRegularFile(it) && it.name !in ignore }
            .sorted()
            .toList()

        val content = resourceFiles.joinToString("\n") { file ->
            """<resource src="${file.name}"></resource>"""
        }

        mdFile.writeText(content)
    }
}

// The Pandas half of the "Coming from Pandas" guide: see `pandas-validation/README.md`.
//
// Deliberately NOT a dependency of `test`, `check` or `build`: it needs Python with pandas, openpyxl and pyarrow,
// which a Kotlin CI agent usually does not have. Run it by hand, or in a CI job that has Python:
//     ./gradlew :samples:runPandasValidation [-PpandasPython=/path/to/python]
// The interpreter is `-PpandasPython`, else the PANDAS_PYTHON environment variable, else `python3` (`python` on Windows).
// A missing interpreter or module FAILS the task: a validation that silently did not run must not look green.
tasks.register<Exec>("runPandasValidation") {
    group = "documentation"
    description = "Runs pandas-validation/validate_pandas.py, the Pandas half of the 'Coming from Pandas' guide. " +
        "Needs Python with pandas, openpyxl and pyarrow; not part of test/check/build."

    val python = providers.gradleProperty("pandasPython")
        .orElse(providers.environmentVariable("PANDAS_PYTHON"))
        .orElse(if (System.getProperty("os.name").startsWith("Windows")) "python" else "python3")
    val script = layout.projectDirectory.file("pandas-validation/validate_pandas.py").asFile

    commandLine(python.get(), script.path)

    doFirst {
        val ready = runCatching {
            val probe = ProcessBuilder(python.get(), "-c", "import pandas, openpyxl, pyarrow")
                .redirectErrorStream(true)
                .start()
            probe.inputStream.readBytes()
            probe.waitFor() == 0
        }.getOrDefault(false)
        if (!ready) {
            throw GradleException(
                "`${python.get()}` is not available or lacks pandas, openpyxl or pyarrow. " +
                    "Install them (pip install -r samples/pandas-validation/requirements.txt) or point " +
                    "-PpandasPython / PANDAS_PYTHON at an interpreter that has them. " +
                    "See samples/pandas-validation/README.md.",
            )
        }
    }
}

// datasets from the root `data/` folder that are used in the docs
val docsDatasets = listOf(
    "movies.csv",
    "jetbrains_repositories.csv",
    "titanic.csv",
)

tasks.register<Copy>("syncDocsDatasets") {
    group = "documentation"
    description = "Copies datasets used in the docs from data/ to docs/StardustDocs/resources/datasets"

    from(rootProject.file("data")) {
        include(docsDatasets)
    }
    into(resourcesDir.toPath() / "datasets")
}

// region fail fast source dependency on :core
val coreProjectPath = projects.core.path

val compilerPluginClasspaths = configurations.matching {
    it.name in setOf("compileClasspath", "testCompileClasspath")
}

val configurationsThatAffectCompilerPluginResolve = setOf("api", "implementation", "compileOnly", "compileOnlyApi")

fun Configuration.coreDependencyPath(visited: MutableSet<Configuration> = mutableSetOf()): List<String>? {
    if (!visited.add(this)) return null

    dependencies.filterIsInstance<ProjectDependency>().forEach { dependency ->
        if (dependency.path == coreProjectPath) return listOf(coreProjectPath)

        rootProject.project(dependency.path).configurations
            .filter { it.name in configurationsThatAffectCompilerPluginResolve }
            .forEach { configuration ->
                configuration.coreDependencyPath(visited)?.let {
                    return listOf("${dependency.path}:${configuration.name}") + it
                }
            }
    }

    extendsFrom.forEach { configuration ->
        configuration.coreDependencyPath(visited)?.let {
            return listOf("${project.path}:${configuration.name}") + it
        }
    }

    return null
}

gradle.projectsEvaluated {
    compilerPluginClasspaths.forEach {
        val dependencyPath = it.coreDependencyPath()
        require(dependencyPath == null) {
            val fullDependencyPath = listOf("${project.path}:${it.name}") + dependencyPath.orEmpty()
            "Module ${project.path} with DataFrame compiler plugin cannot use source dependency $coreProjectPath " +
                "on $it. Dependency path: ${fullDependencyPath.joinToString(" -> ")}. " +
                "Depend on the prebuilt instrumentedJars artifact instead."
        }
    }
}

// endregion

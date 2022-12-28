import com.igormaznitsa.jcp.gradle.JcpTask
import kotlinx.kover.api.KoverTaskExtension
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dataframe.gradle.DataSchemaVisibility

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
    id("com.igormaznitsa.jcp")
}

group = "org.jetbrains.kotlinx"

val jupyterApiTCRepo: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(jupyterApiTCRepo)
}

dependencies {
    api(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
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

kotlin.sourceSets {
    main {
        kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
    test {
        kotlin.srcDir("build/generated/ksp/test/kotlin/")
    }
}

tasks.lintKotlinMain {
    exclude("**/*keywords*/**")
    exclude {
        it.name.endsWith(".Generated.kt")
    }
    exclude {
        it.name.endsWith("\$Extensions.kt")
    }
}

tasks.lintKotlinTest {
    exclude {
        it.name.endsWith(".Generated.kt")
    }
    exclude {
        it.name.endsWith("\$Extensions.kt")
    }
    enabled = true
}

korro {
    docs = fileTree(rootProject.rootDir) {
        include("docs/StardustDocs/topics/*.md")
    }

    samples = fileTree(project.projectDir) {
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/*.kt")
        include("src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/*.kt")
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
    )
}

kotlin {
    explicitApi()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

// Setup preprocessing with JCP for main sources

val kotlinMainSources = kotlin.sourceSets.main.get().kotlin.sourceDirectories

val preprocessMain by tasks.creating(JcpTask::class) {
    sources.set(kotlinMainSources.filter { "ksp" !in it.path })
    clearTarget.set(true)
    fileExtensions.set(listOf("kt"))
    vars.set(
        mapOf()
    )
    outputs.upToDateWhen { target.get().exists() }
}

tasks.compileKotlin {
    dependsOn(tasks.lintKotlin)
    kotlinOptions {
        freeCompilerArgs += listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }

    dependsOn(preprocessMain)
    outputs.upToDateWhen {
        preprocessMain.outcomingFiles.files.isEmpty()
    }

    doFirst {
        kotlin {
            sourceSets {
                main {
                    kotlin.setSrcDirs(
                        kotlinMainSources.filter { "ksp" in it.path } + preprocessMain.target.get()
                    )
                }
            }
        }
    }

    doLast {
        kotlin {
            sourceSets {
                main {
                    kotlin.setSrcDirs(kotlinMainSources)
                }
            }
        }
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    maxHeapSize = "2048m"
    extensions.configure(KoverTaskExtension::class) {
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
        visibility = DataSchemaVisibility.IMPLICIT_PUBLIC
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        name = "org.jetbrains.kotlinx.dataframe.samples.api.Repository"
    }
}

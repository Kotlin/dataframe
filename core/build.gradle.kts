
@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
    kotlin("plugin.serialization")
    kotlin("jupyter.api")
    id("org.jetbrains.dataframe.generator")

    id("org.jmailen.kotlinter")
}

group = "org.jetbrains.kotlinx"

val jupyterApiTCRepo: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(jupyterApiTCRepo)
}

val introspect by sourceSets.creating {
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
        kotlin.srcDir("src/main/kotlin")
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val introspectImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val introspectCompileOnly by configurations.getting {
    extendsFrom(configurations.compileOnly.get())
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.reflect)

    api(libs.commonsCsv)
    implementation(libs.klaxon)
    implementation(libs.fuel)

    implementation(libs.kotlin.datetimeJvm)
    implementation("com.squareup:kotlinpoet:1.11.0")

    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kotlin.scriptingJvm)
    testImplementation(libs.jsoup)

    val kotlinCompilerPluginClasspathIntrospect by configurations.getting
    kotlinCompilerPluginClasspathIntrospect(project(":plugins:dataframe-introspection"))
}

tasks.lintKotlinMain {
    exclude("**/*keywords*/**")
}

tasks.lintKotlinTest {
    enabled = true
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
        "filename"
    )
}

kotlin {
    explicitApi()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(tasks.lintKotlin)
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks.test {
    maxHeapSize = "2048m"
//    filter {
//        exclude
//    }
}

tasks.create<Test>("testApi") {
    
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

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    `maven-publish`
    with(convention.plugins) {
        alias(kotlinJvm11)
    }
    with(libs.plugins) {
        alias(buildconfig)
        alias(plugin.publish)
        alias(ktlint)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://jitpack.io")
    google()
}

group = "org.jetbrains.kotlinx.dataframe"

buildscript {
    dependencies {
        classpath(embeddedKotlin("gradle-plugin"))
    }
}

dependencies {
    api(libs.kotlin.reflect)
    implementation(projects.dataframe)
    // experimental
    implementation(projects.dataframeOpenapiGenerator)

    compileOnly(embeddedKotlin("gradle-plugin"))
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.serialization.core)
    implementation(libs.serialization.json)
    implementation(libs.ksp.gradle)
    implementation(libs.ksp.api)

    testImplementation(gradleTestKit())
    testImplementation(embeddedKotlin("test"))
    testImplementation(embeddedKotlin("test-junit"))
    testImplementation(libs.kotestAssertions)
    testImplementation(libs.android.gradle.api)
    testImplementation(libs.android.gradle)
    testImplementation(embeddedKotlin("gradle-plugin"))
    testImplementation(libs.ktor.server.netty)
    testImplementation(libs.h2db)
}

tasks.withType<ProcessResources> {
    filesMatching("**/plugin.properties") {
        filter {
            it.replace("%PREPROCESSOR_VERSION%", "$version")
        }
    }
}

tasks.withType<ProcessResources> {
    filesMatching("**/df.properties") {
        filter {
            it.replace(
                "%DATAFRAME_JAR%",
                listOf(":core", ":dataframe-csv", ":dataframe-json").joinToString("\", \"") {
                    project(it).configurations
                        .getByName("instrumentedJars")
                        .artifacts.single()
                        .file.absolutePath
                        .replace(File.separatorChar, '/')
                },
            )
        }
    }
}

gradlePlugin {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/Kotlin/dataframe"
    vcsUrl = "https://github.com/Kotlin/dataframe"

    plugins {
        create("schemaGeneratorPlugin") {
            id = "org.jetbrains.kotlinx.dataframe"
            implementationClass = "org.jetbrains.dataframe.gradle.ConvenienceSchemaGeneratorPlugin"
            displayName = "Kotlin DataFrame gradle plugin"
            description = "Gradle plugin providing task for inferring data schemas from your CSV or JSON data"
            tags = listOf("dataframe", "kotlin")
        }
    }
}

sourceSets {
    val main by getting
    val test by getting
    val testRuntimeClasspath by configurations
    create("integrationTest") {
        kotlin.srcDir("src/integrationTest/kotlin")
        compileClasspath += main.output + test.output + testRuntimeClasspath
        runtimeClasspath += output + compileClasspath + test.runtimeClasspath
    }
}

val integrationTestConfiguration by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
}

tasks.pluginUnderTestMetadata {
    pluginClasspath.from(integrationTestConfiguration)
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    dependsOn(":plugins:symbol-processor:publishToMavenLocal")
    dependsOn(":dataframe-arrow:publishToMavenLocal")
    dependsOn(":dataframe-excel:publishToMavenLocal")
    dependsOn(":dataframe-csv:publishToMavenLocal")
    dependsOn(":dataframe-jdbc:publishToMavenLocal")
    dependsOn(":dataframe-json:publishToMavenLocal")
    dependsOn(":dataframe-openapi-generator:publishToMavenLocal")
    dependsOn(":dataframe-openapi:publishToMavenLocal")
    dependsOn(":publishApiPublicationToMavenLocal")
    dependsOn(":dataframe-arrow:publishDataframeArrowPublicationToMavenLocal")
    dependsOn(":dataframe-excel:publishDataframeExcelPublicationToMavenLocal")
    dependsOn(":dataframe-csv:publishDataframeCsvPublicationToMavenLocal")
    dependsOn(":dataframe-jdbc:publishDataframeJDBCPublicationToMavenLocal")
    dependsOn(":dataframe-openapi-generator:publishDataframeOpenApiPublicationToMavenLocal")
    dependsOn(":plugins:symbol-processor:publishMavenPublicationToMavenLocal")
    dependsOn(":core:publishCorePublicationToMavenLocal")
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.check { dependsOn(integrationTestTask) }

// fixing linter + buildConfig
kotlin.sourceSets.create("buildConfigSources") {
    kotlin.srcDir("build/generated/sources/buildConfig/main")
}
tasks.generateBuildConfig {
    finalizedBy("runKtlintFormatOverBuildConfigSourcesSourceSet")
}
tasks.named("runKtlintCheckOverBuildConfigSourcesSourceSet") {
    dependsOn(tasks.generateBuildConfig, "runKtlintFormatOverBuildConfigSourcesSourceSet")
}

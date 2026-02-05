import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    application
    kotlin("jvm")

    // uses the 'old' Gradle plugin instead of the compiler plugin for now
    id("org.jetbrains.kotlinx.dataframe")

    // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
    id("com.google.devtools.ksp")
}

repositories {
    mavenLocal() // in case of local dataframe development
    mavenCentral()
}

dependencies {
    // implementation("org.jetbrains.kotlinx:dataframe:X.Y.Z")
    implementation(project(":"))

    // (kotlin) spark support
    implementation(libs.kotlin.spark)
    compileOnly(libs.spark)
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)
}

/**
 * Runs the kotlinSpark/typedDataset example with java 11.
 */
val runKotlinSparkTypedDataset by tasks.registering(JavaExec::class) {
    classpath = sourceSets["main"].runtimeClasspath
    javaLauncher = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(11) }
    mainClass = "org.jetbrains.kotlinx.dataframe.examples.kotlinSpark.TypedDatasetKt"
}

/**
 * Runs the kotlinSpark/untypedDataset example with java 11.
 */
val runKotlinSparkUntypedDataset by tasks.registering(JavaExec::class) {
    classpath = sourceSets["main"].runtimeClasspath
    javaLauncher = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(11) }
    mainClass = "org.jetbrains.kotlinx.dataframe.examples.kotlinSpark.UntypedDatasetKt"
}

/**
 * Runs the spark/typedDataset example with java 11.
 */
val runSparkTypedDataset by tasks.registering(JavaExec::class) {
    classpath = sourceSets["main"].runtimeClasspath
    javaLauncher = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(11) }
    mainClass = "org.jetbrains.kotlinx.dataframe.examples.spark.TypedDatasetKt"
}

/**
 * Runs the spark/untypedDataset example with java 11.
 */
val runSparkUntypedDataset by tasks.registering(JavaExec::class) {
    classpath = sourceSets["main"].runtimeClasspath
    javaLauncher = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(11) }
    mainClass = "org.jetbrains.kotlinx.dataframe.examples.spark.UntypedDatasetKt"
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.release.set(11)
}

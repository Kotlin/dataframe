import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dataframe)
    alias(libs.plugins.ktlint.gradle)

    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.dataframe)

    // (Kotlin) Spark SQL (Spark 3.3.2)
    implementation(libs.kotlin.spark)
    compileOnly(libs.spark3.sql)

    // Logging to keep Spark quiet
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
    jvmToolchain(11)
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

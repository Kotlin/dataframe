import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")

    id("org.jetbrains.kotlinx.dataframe")

    // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
    mavenLocal() // in case of local dataframe development
}

application.mainClass = "org.jetbrains.kotlinx.dataframe.examples.spark.parquet.SparkParquetDataframeKt"

dependencies {
    implementation(project(":"))

    // Spark SQL + MLlib (Spark 4.0.0)
    implementation("org.apache.spark:spark-sql_2.13:4.0.0")
    implementation("org.apache.spark:spark-mllib_2.13:4.0.0")

    // Logging to keep Spark quiet
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.release.set(11)
}

// Configure KSP tasks to use the same JVM target
tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}




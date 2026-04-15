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

application.mainClass = "org.jetbrains.kotlinx.dataframe.examples.spark.parquet.SparkParquetDataframeKt"

dependencies {
    implementation(libs.dataframe)
    implementation(libs.kandy)

    // Spark SQL + MLlib (Spark 4.0.0)
    implementation(libs.spark.sql)
    implementation(libs.spark.mllib)

    // Logging to keep Spark quiet
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)
}

// for Java 17+, and Arrow/Parquet support
application {
    applicationDefaultJvmArgs = listOf(
        "--add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED",
    )
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

ktlint {
    version = libs.versions.ktlint.asProvider()
    // rules are set up through .editorconfig
}

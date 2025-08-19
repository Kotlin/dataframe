plugins {
    application
    kotlin("jvm")

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

    // exposed + sqlite database support
    implementation(libs.sqlite)
    implementation(libs.exposed.core)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.json)
    implementation(libs.exposed.money)

    // Hibernate + H2 + HikariCP (for Hibernate example)
    implementation(libs.hibernate.core)
    implementation(libs.hibernate.hikaricp)
    implementation(libs.hikaricp)

    implementation(libs.h2db)
    implementation(libs.sl4jsimple)

    // (kotlin) spark support
    implementation(libs.kotlin.spark)
    compileOnly(libs.spark)
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)

    // multik support
    implementation(libs.multik.core)
    implementation(libs.multik.default)
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

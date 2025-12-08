plugins {
    application
    kotlin("jvm")

    // uses the 'old' Gradle plugin instead of the compiler plugin for now
    id("org.jetbrains.kotlinx.dataframe")

    // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
    id("com.google.devtools.ksp")
}

/*
 * NOTE: This example requires running at least Java 11:
 * ```
 * tasks.withType<KotlinCompile> {
 *     compilerOptions {
 *         jvmTarget = JvmTarget.JVM_11
 *         freeCompilerArgs.add("-Xjdk-release=11")
 *     }
 * }
 * tasks.withType<JavaCompile> {
 *     sourceCompatibility = JavaVersion.VERSION_11.toString()
 *     targetCompatibility = JavaVersion.VERSION_11.toString()
 *     options.release.set(11)
 * }
 * ```
 * In the dataframe project, though, we handle this in the main build.gradle.kts file.
 */

repositories {
    mavenLocal() // in case of local dataframe development
    mavenCentral()
}

dependencies {
    // implementation("org.jetbrains.kotlinx:dataframe:X.Y.Z")
    implementation(project(":"))

    // Hibernate + H2 + HikariCP (for Hibernate example)
    implementation(libs.hibernate.core)
    implementation(libs.hibernate.hikaricp)
    implementation(libs.hikaricp)

    implementation(libs.h2db)
    implementation(libs.sl4jsimple)
}

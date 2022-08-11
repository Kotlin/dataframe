import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlinx.dataframe")
}

repositories {
    mavenCentral()
}

kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

application.mainClass.set("org.jetbrains.kotlinx.dataframe.examples.youtube.YoutubeKt")

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlin.datetimeJvm)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


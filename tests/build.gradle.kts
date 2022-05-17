plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.dataframe")
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kotlin.datetimeJvm)
}

kotlin.sourceSets {
    main {
        kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
    test {
        kotlin.srcDir("build/generated/ksp/test/kotlin/")
    }
}

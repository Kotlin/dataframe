import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(ktlint)
    }
}

group = "org.jetbrains.kotlinx"

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    api(projects.core)
    api(projects.dataframeJson)
    api(projects.dataframeArrow)
    api(projects.dataframeCsv)
    api(projects.dataframeJdbc)

    // Spring dependencies
    implementation("org.springframework:spring-context:6.2.7")
    implementation("org.springframework:spring-beans:6.2.7")
    implementation(libs.kotlin.reflect)
    
    // Test dependencies
    testImplementation("org.springframework:spring-test:6.2.7")
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotestAssertions)
}

// Define examples source set
val examples by sourceSets.creating {
    kotlin.srcDir("examples/src")
    resources.srcDir("examples/resources")
}

// Configure examples classpath
configurations {
    named("examplesImplementation") {
        extendsFrom(configurations.implementation.get())
    }
    named("examplesRuntimeOnly") {
        extendsFrom(configurations.runtimeOnly.get())
    }
}

// Add dependencies for examples
dependencies {
    "examplesImplementation"(project(":dataframe-spring"))
    "examplesImplementation"("org.springframework:spring-context:6.2.7")
    "examplesImplementation"("org.springframework:spring-beans:6.2.7")
}

// Task for running examples
tasks.register<JavaExec>("runExamples") {
    group = "Examples"
    description = "Runs the DataFrame Spring examples"
    classpath = examples.runtimeClasspath
    mainClass.set("org.jetbrains.kotlinx.dataframe.spring.examples.ExampleRunnerKt")
}

tasks.test {
    useJUnitPlatform()
}

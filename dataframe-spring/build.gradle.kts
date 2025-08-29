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

tasks.test {
    useJUnitPlatform()
}

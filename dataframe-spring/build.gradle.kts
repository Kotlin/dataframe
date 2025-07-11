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
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
    }
}

dependencies {
    api(projects.core)
    api(projects.dataframeCsv)
    
    // Spring dependencies
    implementation("org.springframework:spring-context:6.0.0")
    implementation("org.springframework:spring-beans:6.0.0")
    implementation(libs.kotlin.reflect)
    
    // Test dependencies
    testImplementation("org.springframework:spring-test:6.0.0")
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotestAssertions)
}

tasks.test {
    useJUnitPlatform()
}
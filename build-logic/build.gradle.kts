plugins {
    // The Kotlin DSL plugin provides a convenient way to develop convention plugins.
    // Convention plugins are located in `src/main/kotlin`, with the file extension `.gradle.kts`,
    // and are applied in the project's `build.gradle.kts` files as required.
    `kotlin-dsl`
    alias(libs.plugins.ktlint.gradle)
}

kotlin {
    jvmToolchain(libs.versions.gradle.jdk.get().toInt())
}

// rules are set up through .editorconfig
ktlint {
    version = libs.versions.ktlint.asProvider()
}

dependencies {
    // So we can create pretty tables in gradle task outputs, parse json, and rename to camelCase
    implementation(libs.dataframe.core)
    implementation(libs.dataframe.json)
}

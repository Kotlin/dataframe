import dev.panuszewski.gradle.pluginMarker
import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion

plugins {
    // The Kotlin DSL plugin provides a convenient way to develop convention plugins.
    // Convention plugins are located in `src/main/kotlin`, with the file extension `.gradle.kts`,
    // and are applied in the project's `build.gradle.kts` files as required.
    `kotlin-dsl`
    alias(libs.plugins.ktlint.gradle)
}

kotlin {
    jvmToolchain(21)
}

// rules are set up through .editorconfig
ktlint {
    version = libs.versions.ktlint.asProvider()
}

dependencies {
    // Add a dependency on the Kotlin Gradle plugin so that convention plugins can apply it.
    implementation(libs.kotlin.gradle.plugin)
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-plugins:$expectedKotlinDslPluginsVersion")

    // We need to declare a dependency for each plugin used in convention plugins below
    implementation(pluginMarker(libs.plugins.ktlint.gradle))
    implementation(pluginMarker(libs.plugins.buildconfig))
}

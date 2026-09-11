import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Internal
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Package
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Protected
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Public

plugins {
    alias(conventions.plugins.dfbuild.base)
    // enables the linter for every Kotlin module in the project
    alias(conventions.plugins.dfbuild.ktlint)

    alias(libs.plugins.kotlin.jvm)

    alias(libs.plugins.dokka)
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.gradle.jdk.get().toInt())
}

// Adds the instrumentedJars configuration/artifact to all Kotlin sub-projects.
// This allows other modules to depend on the output of this task, aka the compiled jar of that module
// Used in :plugins:dataframe-gradle-plugin integration tests and in :samples for compiler plugin support
val instrumentedJars: Configuration = configurations.create("instrumentedJars") {
    isCanBeConsumed = true
    isCanBeResolved = false
}
artifacts {
    add("instrumentedJars", tasks.jar.get().archiveFile) {
        builtBy(tasks.jar)
    }
}

dependencies {
    testImplementation(project(":common-test-utils"))
}

dokka {
    dokkaSourceSets.configureEach {
        documentedVisibilities(Public, Protected, Internal, Package)
    }
}

import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(conventions.plugins.dfbuild.base)
    // enables the linter for every Kotlin module in the project
    alias(conventions.plugins.dfbuild.ktlint)

    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.gradle.jdk.get().toInt())

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()
}

// Aliases akin to the old binary compatibility validator plugin
val apiCheck by tasks.registering {
    group = "verification"
    dependsOn("checkKotlinAbi")
}
val apiDump by tasks.registering {
    group = "verification"
    dependsOn("updateKotlinAbi")
}

// Adds the instrumentedJars configuration/artifact to all Kotlin sub-projects.
// This allows other modules to depend on the output of this task, aka the compiled jar of that module
// Used in :plugins:dataframe-gradle-plugin integration tests and in :samples for compiler plugin support
val instrumentedJars: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}
artifacts {
    add("instrumentedJars", tasks.jar.get().archiveFile) {
        builtBy(tasks.jar)
    }
}

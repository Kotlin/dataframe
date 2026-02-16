plugins {
    alias(convention.plugins.base)
    // enables the linter for every Kotlin module in the project
    alias(convention.plugins.ktlint)

    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    explicitApi()
    jvmToolchain(21)
    compilerOptions {
        // can be removed once kotlin.uuid.ExperimentalUuidApi is marked "stable".
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
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

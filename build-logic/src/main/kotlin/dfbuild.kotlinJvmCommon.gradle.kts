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
        // enables support for kotlin.time.Instant as kotlinx.datetime.Instant was deprecated; Issue #1350
        // Can be removed once kotlin.time.Instant is marked "stable".
        optIn.add("kotlin.time.ExperimentalTime")
        // can be removed once kotlin.uuid.ExperimentalUuidApi is marked "stable".
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

rootProject.name = "build-settings-logic"

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// versions should be kept in sync with `gradle/libs.versions.toml`
plugins {
    // cannot be applied here, it's an early-evaluated included build
    id("dev.panuszewski.typesafe-conventions") version "0.10.0" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.gradle.develocity") version "3.19"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

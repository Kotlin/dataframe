package dfbuild.buildExampleProjects

import java.io.File

enum class BuildSystem {
    GRADLE,
    MAVEN,
    KOTLIN_TOOLCHAIN,
}

fun File.detectBuildSystem(): BuildSystem? {
    require(this.isDirectory)
    val files = this.list()
    return when {
        "pom.xml" in files -> BuildSystem.MAVEN

        "settings.gradle.kts" in files ||
            "build.gradle.kts" in files ||
            "settings.gradle" in files ||
            "build.gradle" in files -> BuildSystem.GRADLE

        "project.yaml" in files ||
            "module.yaml" in files -> BuildSystem.KOTLIN_TOOLCHAIN

        else -> null
    }
}

fun File.isAndroid(): Boolean = "android" in name.lowercase()

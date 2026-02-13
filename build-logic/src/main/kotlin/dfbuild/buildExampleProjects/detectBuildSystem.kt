package dfbuild.buildExampleProjects

import java.io.File

enum class BuildSystem {
    GRADLE,
    MAVEN,
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

        else -> null
    }
}

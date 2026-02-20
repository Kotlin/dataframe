package dfbuild

import org.gradle.api.Project
import java.io.File

/**
 * Returns the root directory of the whole project
 * by finding the `gradlew` file in the parent directories.
 */
fun Project.findRootDir(): File = projectDir.findRootDir()

fun File.findRootDir(): File {
    var rootDir = this
    while (!rootDir.resolve("gradlew").exists()) {
        rootDir = rootDir.parentFile
            ?: error("Could not find parent of '${rootDir.absolutePath}'")
    }
    return rootDir
}

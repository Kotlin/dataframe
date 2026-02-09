package dfbuild

import org.gradle.api.Project
import java.io.File

/**
 * Returns the root directory of the whole project
 * by finding the `gradlew` file in the parent directories.
 */
fun Project.findRootDir(): File {
    var rootDir = projectDir
    while (!rootDir.resolve("gradlew").exists()) {
        rootDir = rootDir.parentFile
    }
    return rootDir!!
}

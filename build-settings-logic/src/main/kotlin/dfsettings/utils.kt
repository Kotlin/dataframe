package dfsettings

import org.gradle.api.initialization.Settings
import java.io.File

/**
 * Returns the root directory of the whole project
 * by finding the `gradlew` file in the parent directories.
 */
fun Settings.findRootDir(): File {
    var rootDir = settingsDir
    while (!rootDir.resolve("gradlew").exists()) {
        rootDir = rootDir.parentFile
    }
    return rootDir!!
}

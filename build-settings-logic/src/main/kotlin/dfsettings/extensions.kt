package dfsettings

import org.gradle.api.initialization.Settings
import java.io.File

fun Settings.getDfRootDir(): File {
    var dfRootDir = settingsDir
    while (!dfRootDir.resolve("gradlew").exists()) {
        dfRootDir = dfRootDir.parentFile
    }
    return dfRootDir!!
}

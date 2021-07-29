package org.jetbrains.dataframe.gradle

import java.io.File

internal fun File.isMiddlePackage(): Boolean {
    return isDirectory && (listFiles()?.singleOrNull()?.isDirectory ?: false)
}

internal fun File.findDeepestCommonSubdirectory(): File {
    if (!exists()) return this
    return walkTopDown().filterNot { it.isMiddlePackage() }.first()
}

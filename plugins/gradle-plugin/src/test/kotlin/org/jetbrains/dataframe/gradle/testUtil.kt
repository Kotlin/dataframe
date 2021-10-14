package org.jetbrains.dataframe.gradle

import io.kotest.matchers.should
import java.io.File
import java.nio.file.Paths

val File.unixPath: String get() = absolutePath.replace(File.separatorChar, '/')

fun File.shouldEndWith(first: String, vararg path: String) = this should { it.endsWith(Paths.get(first, *path).toFile()) }

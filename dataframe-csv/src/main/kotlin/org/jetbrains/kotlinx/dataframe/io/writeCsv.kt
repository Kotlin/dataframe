package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.impl.io.writeCsvImpl
import java.io.File
import java.io.FileWriter

public fun AnyFrame.writeCsv(file: File): Unit = writeCsvImpl(this, FileWriter(file))

public fun AnyFrame.writeCsv(path: String): Unit = writeCsvImpl(this, FileWriter(path))

public fun AnyFrame.toCsvStr(): String = writeCsvImpl(this, StringBuilder()).toString()

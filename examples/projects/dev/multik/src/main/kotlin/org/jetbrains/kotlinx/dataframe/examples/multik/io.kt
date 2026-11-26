package org.jetbrains.kotlinx.dataframe.examples.multik

import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.multik.api.io.readNPY
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import java.io.File

/**
 * Multik can read/write data from NPY/NPZ files.
 * We can use this from DataFrame too!
 *
 * We use compatibilityLayer.kt for the conversions, check it out for the implementation details of the conversion!
 */
fun main() {
    val npyFilename = "a1d.npy"
    val npyFile = File(object {}.javaClass.classLoader.getResource(npyFilename)!!.toURI())

    val mk1 = mk.readNPY<Long, D1>(npyFile)
    val df1 = mk1.convertToDataFrame()

    df1.print(borders = true, columnTypes = true)
}

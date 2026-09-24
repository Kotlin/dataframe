package org.jetbrains.kotlinx.dataframe.impl.io

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

internal fun ByteArray.encodeGzip(): ByteArray {
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).use { it.write(this) }

    return bos.toByteArray()
}

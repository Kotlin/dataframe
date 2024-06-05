package org.jetbrains.kotlinx.dataframe.impl.io

import java.util.Base64

internal fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)

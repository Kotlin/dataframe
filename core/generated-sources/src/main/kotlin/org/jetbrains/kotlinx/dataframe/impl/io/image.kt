package org.jetbrains.kotlinx.dataframe.impl.io

import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

internal fun BufferedImage.resizeKeepingAspectRatio(
    maxSize: Int,
    resultImageType: Int = BufferedImage.TYPE_INT_ARGB,
    interpolation: Any = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
    renderingQuality: Any = RenderingHints.VALUE_RENDER_QUALITY,
    antialiasing: Any = RenderingHints.VALUE_ANTIALIAS_ON,
    observer: ImageObserver? = null
): BufferedImage {
    val aspectRatio = width.toDouble() / height.toDouble()
    val size = min(maxSize, max(width, height))

    val (nWidth, nHeight) = if (width > height) {
        Pair(size, (size / aspectRatio).toInt())
    } else {
        Pair((size * aspectRatio).toInt(), size)
    }

    return resize(nWidth, nHeight, resultImageType, interpolation, renderingQuality, antialiasing, observer)
}

internal fun BufferedImage.resize(
    width: Int,
    height: Int,
    resultImageType: Int = BufferedImage.TYPE_INT_ARGB,
    interpolation: Any = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
    renderingQuality: Any = RenderingHints.VALUE_RENDER_QUALITY,
    antialiasing: Any = RenderingHints.VALUE_ANTIALIAS_ON,
    observer: ImageObserver? = null
): BufferedImage {
    val resized = BufferedImage(width, height, resultImageType)
    val g: Graphics2D = resized.createGraphics()

    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, renderingQuality)
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasing)

    g.drawImage(this, 0, 0, width, height, observer)
    g.dispose()

    return resized
}

internal const val DEFAULT_IMG_FORMAT = "png"

internal fun BufferedImage.toByteArray(format: String = DEFAULT_IMG_FORMAT): ByteArray =
    ByteArrayOutputStream().use { bos ->
        ImageIO.write(this, format, bos)
        bos.toByteArray()
    }

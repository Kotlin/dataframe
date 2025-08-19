package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.io.BufferedImageEncoder
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.KOTLIN_DATAFRAME
import org.jetbrains.kotlinx.dataframe.impl.io.resizeKeepingAspectRatio
import org.jetbrains.kotlinx.dataframe.io.Base64ImageEncodingOptions.Companion.ALL_OFF
import org.jetbrains.kotlinx.dataframe.io.Base64ImageEncodingOptions.Companion.GZIP_ON
import org.jetbrains.kotlinx.dataframe.io.Base64ImageEncodingOptions.Companion.LIMIT_SIZE_ON
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO
import kotlin.math.abs

class ImageSerializationTests {
    @ParameterizedTest
    @MethodSource("imageEncodingOptionsToTest")
    fun `serialize images as base64`(encodingOptions: Base64ImageEncodingOptions?) {
        val images = readImagesFromResources()
        val json = encodeImagesAsJson(images, encodingOptions)

        if (encodingOptions == DISABLED) {
            checkImagesEncodedAsToString(json, images.size)
            return
        }

        val decodedImages = decodeImagesFromJson(json, images.size, encodingOptions)

        for ((decodedImage, original) in decodedImages.zip(images)) {
            val expectedImage = resizeIfNeeded(original, encodingOptions)
            isImagesIdentical(decodedImage, expectedImage, 2) shouldBe true
        }
    }

    private fun readImagesFromResources(): List<BufferedImage> {
        val dir = File(testResource("imgs").path)

        return dir.listFiles()?.map { file ->
            try {
                ImageIO.read(file)
            } catch (ex: Exception) {
                throw IllegalArgumentException("Error reading ${file.name}: ${ex.message}")
            }
        } ?: emptyList()
    }

    private fun encodeImagesAsJson(
        images: List<BufferedImage>,
        encodingOptions: Base64ImageEncodingOptions?,
    ): JsonObject {
        val df = dataFrameOf("imgs" to images)
        val jsonStr = df.toJsonWithMetadata(
            20,
            nestedRowLimit = 20,
            customEncoders = listOfNotNull(encodingOptions?.let { BufferedImageEncoder(encodingOptions) }),
        )

        return parseJsonStr(jsonStr)
    }

    private fun checkImagesEncodedAsToString(json: JsonObject, numImgs: Int) {
        for (i in 0..<numImgs) {
            val row = json[KOTLIN_DATAFRAME]!!.jsonArray[i].jsonObject
            val img = row["imgs"]?.jsonPrimitive?.content

            img shouldContain "BufferedImage"
        }
    }

    private fun decodeImagesFromJson(
        json: JsonObject,
        imgsNum: Int,
        encodingOptions: Base64ImageEncodingOptions,
    ): List<BufferedImage> {
        val result = mutableListOf<BufferedImage>()
        for (i in 0..<imgsNum) {
            val row = json[KOTLIN_DATAFRAME]!!.jsonArray[i].jsonObject
            val imgString = row["imgs"]!!.jsonPrimitive.content

            val bytes = decodeBase64Image(imgString, encodingOptions)
            val decodedImage = createImageFromBytes(bytes)

            result.add(decodedImage)
        }

        return result
    }

    private fun decodeBase64Image(imgString: String, encodingOptions: Base64ImageEncodingOptions): ByteArray =
        when {
            encodingOptions.isGzipOn -> decompressGzip(Base64.getDecoder().decode(imgString))
            else -> Base64.getDecoder().decode(imgString)
        }

    private fun decompressGzip(input: ByteArray): ByteArray =
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            GZIPInputStream(input.inputStream()).use { inputStream ->
                inputStream.copyTo(byteArrayOutputStream)
            }
            byteArrayOutputStream.toByteArray()
        }

    private fun resizeIfNeeded(image: BufferedImage, encodingOptions: Base64ImageEncodingOptions): BufferedImage =
        when {
            !encodingOptions.isLimitSizeOn -> image
            else -> image.resizeKeepingAspectRatio(encodingOptions.imageSizeLimit)
        }

    private fun createImageFromBytes(bytes: ByteArray): BufferedImage {
        val bais = ByteArrayInputStream(bytes)
        return ImageIO.read(bais)
    }

    private fun isImagesIdentical(img1: BufferedImage, img2: BufferedImage, allowedDelta: Int): Boolean {
        // First check dimensions
        if (img1.width != img2.width || img1.height != img2.height) {
            return false
        }

        // Then check each pixel
        for (y in 0 until img1.height) {
            for (x in 0 until img1.width) {
                val rgb1 = img1.getRGB(x, y)
                val rgb2 = img2.getRGB(x, y)

                val r1 = (rgb1 shr 16) and 0xFF
                val g1 = (rgb1 shr 8) and 0xFF
                val b1 = rgb1 and 0xFF

                val r2 = (rgb2 shr 16) and 0xFF
                val g2 = (rgb2 shr 8) and 0xFF
                val b2 = rgb2 and 0xFF

                val diff = abs(r1 - r2) + abs(g1 - g2) + abs(b1 - b2)

                // If the difference in color components exceed our allowance return false
                if (diff > allowedDelta) {
                    return false
                }
            }
        }

        // If no exceeding difference was found, the images are identical within our allowedDelta
        return true
    }

    companion object {
        private val DEFAULT = Base64ImageEncodingOptions()
        private val GZIP_ON_RESIZE_OFF = Base64ImageEncodingOptions(options = GZIP_ON)
        private val GZIP_OFF_RESIZE_OFF = Base64ImageEncodingOptions(options = ALL_OFF)
        private val GZIP_ON_RESIZE_TO_700 =
            Base64ImageEncodingOptions(imageSizeLimit = 700, options = GZIP_ON or LIMIT_SIZE_ON)
        private val DISABLED = null

        @JvmStatic
        fun imageEncodingOptionsToTest(): List<Base64ImageEncodingOptions?> =
            listOf(
                DEFAULT,
                GZIP_ON_RESIZE_OFF,
                GZIP_OFF_RESIZE_OFF,
                GZIP_ON_RESIZE_TO_700,
                null,
            )
    }
}

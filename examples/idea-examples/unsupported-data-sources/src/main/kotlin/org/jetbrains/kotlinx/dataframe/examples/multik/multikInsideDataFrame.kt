package org.jetbrains.kotlinx.dataframe.examples.multik

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.set
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@DataSchema
data class Transformation(
    val type: TransformationType,
    val parameters: Map<String, Double>,
    val note: String,
    val matrix: D2Array<Double>,
)

enum class TransformationType {
    IDENTITY,
    TRANSLATION,
    SCALING,
    ROTATION,
    SHEARING,
    REFLECTION_ABOUT_ORIGIN,
    REFLECTION_ABOUT_X_AXIS,
    REFLECTION_ABOUT_Y_AXIS,
}

/**
 * IDK yet about this one... TODO
 */
fun main() {
    // DataFrames can store anything inside, including Multik nd arrays.
    // This can be useful for storing matrices for easier access later,
    // such as affine transformations when making 2D graphics!
    // (https://en.wikipedia.org/wiki/Affine_transformation)

    // let's make a transformation sequence that rotates and scales an image in place.
    // It's currently 100x50, positioned with its left bottom corner at (x=10, y=0)
    val transformations = listOf(
        Transformation(
            type = TransformationType.TRANSLATION,
            parameters = mapOf("x" to -10.0, "y" to 0.0),
            note = "Translate so left-bottom touches origin",
            matrix = translationMatrixOf(x = -10.0, y = 0.0),
        ),
        Transformation(
            type = TransformationType.SCALING,
            parameters = mapOf("w" to 2.0, "h" to 2.0),
            note = "Scale by x2",
            matrix = scaleMatrixOf(w = 2.0, h = 2.0),
        ),
        Transformation(
            type = TransformationType.TRANSLATION,
            parameters = mapOf("x" to -100.0, "y" to -50.0),
            note = "Translate so the new image center is at the origin",
            matrix = translationMatrixOf(x = -100.0, y = -50.0),
        ),
        Transformation(
            type = TransformationType.ROTATION,
            parameters = mapOf("angle" to 45.0),
            note = "Rotate by 45 degrees",
            matrix = rotationMatrixOf(angle = 45.0),
        ),
        Transformation(
            type = TransformationType.TRANSLATION,
            parameters = mapOf("x" to 10.0 + 50.0, "y" to 0.0 + 25.0),
            note = "Translate back so the center is at the same original position",
            matrix = translationMatrixOf(x = 10.0 + 50.0, y = 0.0 + 25.0),
        ),
    ).toDataFrame()

    transformations.print(borders = true)
    transformations.toStandaloneHtml().openInBrowser()
}

fun identityMatrix(): D2Array<Double> = mk.identity(3)

/** Returns a 3x3 affine transformation matrix that translates by (x, y) */
fun translationMatrixOf(x: Double = 0.0, y: Double = 0.0): D2Array<Double> =
    identityMatrix().apply {
        this[0, 2] = x
        this[1, 2] = y
    }

/** Returns a 3x3 affine transformation matrix that scales by (w, h) about the origin */
fun scaleMatrixOf(w: Double = 1.0, h: Double = 1.0): D2Array<Double> =
    identityMatrix().apply {
        this[0, 0] = w
        this[1, 1] = h
    }

/** Returns a 3x3 affine transformation matrix that rotates by [angle] degrees about the origin */
fun rotationMatrixOf(angle: Double): D2Array<Double> {
    val cos = cos(angle)
    val sin = sin(angle)
    return identityMatrix().apply {
        this[0, 0] = cos
        this[0, 1] = -sin
        this[1, 0] = sin
        this[1, 1] = cos
    }
}

/** Returns a 3x3 affine transformation matrix that shears by [x] and [y] */
fun shearingMatrixOf(x: Double = 0.0, y: Double = 0.0): D2Array<Double> =
    identityMatrix().apply {
        this[0, 1] = tan(x)
        this[1, 0] = tan(y)
    }

/** Returns a 3x3 affine transformation matrix that reflects about the origin */
fun reflectionAboutOriginMatrix(): D2Array<Double> =
    identityMatrix().apply {
        this[0, 0] = -1.0
        this[1, 1] = -1.0
    }

/** Returns a 3x3 affine transformation matrix that reflects about the x-axis */
fun reflectionAboutXAxisMatrix(): D2Array<Double> =
    identityMatrix().apply {
        this[1, 1] = -1.0
    }

/** Returns a 3x3 affine transformation matrix that reflects about the y-axis */
fun reflectionAboutYAxisMatrix(): D2Array<Double> =
    identityMatrix().apply {
        this[0, 0] = -1.0
    }

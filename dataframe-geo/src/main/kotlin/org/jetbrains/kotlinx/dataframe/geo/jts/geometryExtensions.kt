package org.jetbrains.kotlinx.dataframe.geo.jts

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.util.AffineTransformation

/**
 * Scales the geometry around its center using the same scaling factor for both axes.
 *
 * @param factor The scaling factor for both the X and Y axes.
 * @return A new geometry scaled around its center.
 */
public fun Geometry.scaleAroundCenter(factor: Double): Geometry = scaleAroundCenter(factor, factor)

/**
 * Scales the geometry around its center using different scaling factors for the X and Y axes.
 *
 * @param xFactor The scaling factor for the X axis.
 * @param yFactor The scaling factor for the Y axis.
 * @return A new geometry scaled around its center.
 */
public fun Geometry.scaleAroundCenter(xFactor: Double, yFactor: Double): Geometry {
    val centroid = centroid.coordinate

    val moveToOrigin = AffineTransformation
        .translationInstance(-centroid.x, -centroid.y)
    val scale = AffineTransformation.scaleInstance(xFactor, yFactor)
    val moveBack = AffineTransformation.translationInstance(centroid.x, centroid.y)
    val transformation = moveToOrigin.compose(scale).compose(moveBack)

    return transformation.transform(this)
}

/**
 * Translates (moves) the geometry by the specified distances along the X and Y axes.
 *
 * @param valueX The translation distance along the X axis.
 * @param valueY The translation distance along the Y axis.
 * @return A new geometry translated by the specified distances.
 */
public fun Geometry.translate(valueX: Double, valueY: Double): Geometry =
    AffineTransformation().translate(valueX, valueY).transform(this)

/**
 * Rotates the geometry around its center by the specified angle in radians.
 *
 * @param angleRadians The rotation angle in radians.
 * @return A new geometry rotated around its center.
 */
public fun Geometry.rotate(angleRadians: Double): Geometry {
    val centroid = centroid.coordinate

    val moveToOrigin = AffineTransformation.translationInstance(-centroid.x, -centroid.y)
    val rotate = AffineTransformation.rotationInstance(angleRadians)
    val moveBack = AffineTransformation.translationInstance(centroid.x, centroid.y)

    val transformation = moveToOrigin.compose(rotate).compose(moveBack)
    return transformation.transform(this)
}

/**
 * Reflects the geometry across the X axis, inverting its horizontal position.
 *
 * @return A new geometry reflected across the X axis.
 */
public fun Geometry.reflectX(): Geometry = scaleAroundCenter(-1.0, 1.0)

/**
 * Reflects the geometry across the Y axis, inverting its vertical position.
 *
 * @return A new geometry reflected across the Y axis.
 */
public fun Geometry.reflectY(): Geometry = scaleAroundCenter(1.0, -1.0)

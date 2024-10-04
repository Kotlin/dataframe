package org.jetbrains.kotlinx.dataframe.geo.jts

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.util.AffineTransformation

fun Geometry.scale(value: Double): Geometry {

    val centroid = centroid.coordinate

    val moveToOrigin = AffineTransformation
        .translationInstance(-centroid.x, -centroid.y)

    val scale = AffineTransformation.scaleInstance(value, value)

    val moveBack = AffineTransformation.translationInstance(centroid.x, centroid.y)

    val transformation = moveToOrigin.compose(scale).compose(moveBack)

    return transformation.transform(this)
}

fun Geometry.translate(valueX: Double, valueY: Double): Geometry {
    return AffineTransformation().translate(valueX, valueY).transform(this)
}

package org.jetbrains.kotlinx.dataframe.geo.jts

import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

/**
 * Converts a [Polygon] to a [MultiPolygon] by wrapping it in a MultiPolygon.
 *
 * @return A MultiPolygon containing the original Polygon.
 * @receiver Polygon to be converted.
 */
public fun Polygon.toMultiPolygon(): MultiPolygon {
    val geometryFactory = this.factory
    return geometryFactory.createMultiPolygon(arrayOf(this))
}

/**
 * Converts a [Point] to a [MultiPoint] by wrapping it in a MultiPoint.
 *
 * @return A MultiPoint containing the original Point.
 * @receiver Point to be converted.
 */
public fun Point.toMultiPoint(): MultiPoint {
    val geometryFactory = this.factory
    return geometryFactory.createMultiPoint(arrayOf(this))
}

/**
 * Converts a [LineString] to a [MultiLineString] by wrapping it in a MultiLineString.
 *
 * @return A MultiLineString containing the original LineString.
 * @receiver LineString to be converted.
 */
public fun LineString.toMultiLineString(): MultiLineString {
    val geometryFactory = this.factory
    return geometryFactory.createMultiLineString(arrayOf(this))
}

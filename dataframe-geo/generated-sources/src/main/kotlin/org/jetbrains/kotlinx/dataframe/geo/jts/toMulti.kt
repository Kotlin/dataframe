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
 * @receiver Polygon to be converted.
 * @return A MultiPolygon containing the original Polygon.
 */
public fun Polygon.toMultiPolygon(): MultiPolygon {
    val geometryFactory = this.factory
    return geometryFactory.createMultiPolygon(arrayOf(this))
}

/**
 * Converts a [Point] to a [MultiPoint] by wrapping it in a MultiPoint.
 *
 * @receiver Point to be converted.
 * @return A MultiPoint containing the original Point.
 */
public fun Point.toMultiPoint(): MultiPoint {
    val geometryFactory = this.factory
    return geometryFactory.createMultiPoint(arrayOf(this))
}

/**
 * Converts a [LineString] to a [MultiLineString] by wrapping it in a MultiLineString.
 *
 * @receiver LineString to be converted.
 * @return A MultiLineString containing the original LineString.
 */
public fun LineString.toMultiLineString(): MultiLineString {
    val geometryFactory = this.factory
    return geometryFactory.createMultiLineString(arrayOf(this))
}

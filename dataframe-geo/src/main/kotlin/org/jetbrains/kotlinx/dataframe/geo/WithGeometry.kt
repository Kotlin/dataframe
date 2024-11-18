package org.jetbrains.kotlinx.dataframe.geo

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

@DataSchema
interface WithGeometry {
    val geometry: Geometry
}

@DataSchema
interface WithPolygonGeometry : WithGeometry {
    override val geometry: Polygon
}

@DataSchema
interface WithMultiPolygonGeometry : WithGeometry {
    override val geometry: MultiPolygon
}

@DataSchema
interface WithPointGeometry : WithGeometry {
    override val geometry: Point
}

@DataSchema
interface WithMultiPointGeometry : WithGeometry {
    override val geometry: MultiPoint
}

@DataSchema
interface WithLineStringGeometry : WithGeometry {
    override val geometry: LineString
}

@DataSchema
interface WithMultiLineStringGeometry : WithGeometry {
    override val geometry: MultiLineString
}

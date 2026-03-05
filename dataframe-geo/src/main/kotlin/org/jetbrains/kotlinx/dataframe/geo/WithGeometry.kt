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
public interface WithGeometry {
    public val geometry: Geometry
}

@DataSchema
public interface WithPolygonGeometry : WithGeometry {
    override val geometry: Polygon
}

@DataSchema
public interface WithMultiPolygonGeometry : WithGeometry {
    override val geometry: MultiPolygon
}

@DataSchema
public interface WithPointGeometry : WithGeometry {
    override val geometry: Point
}

@DataSchema
public interface WithMultiPointGeometry : WithGeometry {
    override val geometry: MultiPoint
}

@DataSchema
public interface WithLineStringGeometry : WithGeometry {
    override val geometry: LineString
}

@DataSchema
public interface WithMultiLineStringGeometry : WithGeometry {
    override val geometry: MultiLineString
}

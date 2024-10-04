package org.jetbrains.kotlinx.dataframe.geo

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon

@DataSchema
interface GeoFrame {
    val geometry: Geometry
}

@DataSchema
interface PolygonGeoFrame : GeoFrame {
    override val geometry: Polygon
}

@DataSchema
interface MultiPolygonGeoFrame : GeoFrame {
    override val geometry: MultiPolygon
}

@get:JvmName("geometry")
val <T : GeoFrame> ColumnsContainer<T>.geometry: DataColumn<Geometry>
    get() = get("geometry") as DataColumn<Geometry>

@get:JvmName("geometryPolygon")
val <T : PolygonGeoFrame> ColumnsContainer<T>.geometry: DataColumn<Polygon>
    get() = get("geometry") as DataColumn<Polygon>

@get:JvmName("geometryMultiPolygon")
val <T : MultiPolygonGeoFrame> ColumnsContainer<T>.geometry: DataColumn<MultiPolygon>
    get() = get("geometry") as DataColumn<MultiPolygon>

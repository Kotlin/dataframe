package org.jetbrains.kotlinx.dataframe.geo

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon

@DataSchema
interface WithGeometry {
    val geometry: Geometry
}

@DataSchema
interface WithPolygon : WithGeometry {
    override val geometry: Polygon
}

@DataSchema
interface WithMultiPolygon : WithGeometry {
    override val geometry: MultiPolygon
}

@Suppress("UNCHECKED_CAST")
@get:JvmName("geometry")
val ColumnsContainer<WithGeometry>.geometry: DataColumn<Geometry>
    get() = get("geometry") as DataColumn<Geometry>

@Suppress("UNCHECKED_CAST")
@get:JvmName("geometryPolygon")
val ColumnsContainer<WithPolygon>.geometry: DataColumn<Polygon>
    get() = get("geometry") as DataColumn<Polygon>

@Suppress("UNCHECKED_CAST")
@get:JvmName("geometryMultiPolygon")
val ColumnsContainer<WithMultiPolygon>.geometry: DataColumn<MultiPolygon>
    get() = get("geometry") as DataColumn<MultiPolygon>

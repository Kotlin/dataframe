@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.geo
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

public val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithPolygonGeometry>.geometry: DataColumn<org.locationtech.jts.geom.Polygon> @JvmName("WithPolygonGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.Polygon>
public val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithPolygonGeometry>.geometry: org.locationtech.jts.geom.Polygon @JvmName("WithPolygonGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.Polygon
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithPolygonGeometry?>.geometry: DataColumn<org.locationtech.jts.geom.Polygon?> @JvmName("NullableWithPolygonGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.Polygon?>
public val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithPolygonGeometry?>.geometry: org.locationtech.jts.geom.Polygon? @JvmName("NullableWithPolygonGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.Polygon?

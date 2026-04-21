@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.geo
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

public val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithMultiPolygonGeometry>.geometry: DataColumn<org.locationtech.jts.geom.MultiPolygon> @JvmName("WithMultiPolygonGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.MultiPolygon>
public val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithMultiPolygonGeometry>.geometry: org.locationtech.jts.geom.MultiPolygon @JvmName("WithMultiPolygonGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.MultiPolygon
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithMultiPolygonGeometry?>.geometry: DataColumn<org.locationtech.jts.geom.MultiPolygon?> @JvmName("NullableWithMultiPolygonGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.MultiPolygon?>
public val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithMultiPolygonGeometry?>.geometry: org.locationtech.jts.geom.MultiPolygon? @JvmName("NullableWithMultiPolygonGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.MultiPolygon?

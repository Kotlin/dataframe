@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.geo
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithGeometry>.geometry: DataColumn<org.locationtech.jts.geom.Geometry> @JvmName("WithGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.Geometry>
val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithGeometry>.geometry: org.locationtech.jts.geom.Geometry @JvmName("WithGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.Geometry
val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithGeometry?>.geometry: DataColumn<org.locationtech.jts.geom.Geometry?> @JvmName("NullableWithGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.Geometry?>
val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithGeometry?>.geometry: org.locationtech.jts.geom.Geometry? @JvmName("NullableWithGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.Geometry?

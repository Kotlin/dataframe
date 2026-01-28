@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.geo
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithLineStringGeometry>.geometry: DataColumn<org.locationtech.jts.geom.LineString> @JvmName("WithLineStringGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.LineString>
val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithLineStringGeometry>.geometry: org.locationtech.jts.geom.LineString @JvmName("WithLineStringGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.LineString
val ColumnsScope<org.jetbrains.kotlinx.dataframe.geo.WithLineStringGeometry?>.geometry: DataColumn<org.locationtech.jts.geom.LineString?> @JvmName("NullableWithLineStringGeometry_geometry") get() = this["geometry"] as DataColumn<org.locationtech.jts.geom.LineString?>
val DataRow<org.jetbrains.kotlinx.dataframe.geo.WithLineStringGeometry?>.geometry: org.locationtech.jts.geom.LineString? @JvmName("NullableWithLineStringGeometry_geometry") get() = this["geometry"] as org.locationtech.jts.geom.LineString?

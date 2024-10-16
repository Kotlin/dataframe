package org.jetbrains.kotlinx.dataframe.geo

import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame

fun AnyFrame.toGeo(crs: CoordinateReferenceSystem? = null): GeoDataFrame<*> = GeoDataFrame(
    this as DataFrame<GeoFrame>, crs
)

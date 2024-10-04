package org.jetbrains.kotlinx.dataframe.geo

import org.geotools.geometry.jts.ReferencedEnvelope
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.geo.jts.computeBounds

fun GeoDataFrame<*>.bounds(): ReferencedEnvelope {
    return ReferencedEnvelope(df.geometry.asIterable().computeBounds(), crs)
}

package org.jetbrains.kotlinx.dataframe.geo

import org.geotools.geometry.jts.ReferencedEnvelope
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.geo.jts.computeBounds

/**
 * Computes the bounding envelope for all geometries in a `GeoDataFrame`, considering the specified
 * coordinate reference system (CRS).
 *
 * @return The bounding envelope that includes all geometries, associated with the CRS of the
 *   `GeoDataFrame`.
 * @receiver The `GeoDataFrame` containing the geometries for which to compute bounds.
 */
public fun GeoDataFrame<*>.bounds(): ReferencedEnvelope =
    ReferencedEnvelope(df.geometry.asIterable().computeBounds(), crs)

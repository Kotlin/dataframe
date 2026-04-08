package org.jetbrains.kotlinx.dataframe.geo

import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * Transforms an `AnyFrame` (a general-purpose DataFrame) into a `GeoDataFrame`
 * by interpreting it as a `DataFrame` containing geometry data. Optionally, a
 * Coordinate Reference System (CRS) can be specified.
 *
 * @receiver The input DataFrame to be converted into a `GeoDataFrame`.
 * @param crs The coordinate reference system to associate with the `GeoDataFrame`.
 *            If null, no specific CRS is applied.
 * @return The resulting `GeoDataFrame` with geometry and, if provided, an associated CRS.
 *
 * Note: The `AnyFrame` must contain a `geometry` column to be converted successfully.
 */
@Suppress("UNCHECKED_CAST")
public fun AnyFrame.toGeo(crs: CoordinateReferenceSystem? = null): GeoDataFrame<*> =
    GeoDataFrame(
        this as DataFrame<WithGeometry>,
        crs,
    )

package org.jetbrains.kotlinx.dataframe.geo

import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with

/**
 * A data structure representing a geographical DataFrame, combining spatial data with
 * an optional Coordinate Reference System (CRS).
 *
 * @param T The type parameter extending `WithGeometry`, indicating the presence of a geometry column.
 * @property df The underlying `DataFrame` containing geometries.
 * @property crs The coordinate reference system associated with the data, if any.
 */
class GeoDataFrame<T : WithGeometry>(val df: DataFrame<T>, val crs: CoordinateReferenceSystem?) {
    /**
     * Creates a new `GeoDataFrame` using a specified transformation block on the underlying DataFrame.
     *
     * @param updateBlock The block defining the transformations to be applied to the DataFrame.
     * @return A new `GeoDataFrame` instance with updated data and the same CRS.
     */
    fun update(updateBlock: DataFrame<T>.() -> DataFrame<T>): GeoDataFrame<T> = GeoDataFrame(df.updateBlock(), crs)

    /**
     * Transforms the geometries to a specified Coordinate Reference System (CRS).
     *
     * This function reprojects the geometry data from the current CRS to a target CRS.
     * If no target CRS is specified and the `GeoDataFrame` has no CRS, WGS 84 is used by default.
     *
     * @param targetCrs The target CRS for transformation.
     * @return A new `GeoDataFrame` with reprojected geometries and the specified CRS.
     */
    fun applyCrs(targetCrs: CoordinateReferenceSystem): GeoDataFrame<T> {
        if (crs == null) {
            return GeoDataFrame(df, targetCrs)
        }
        if (targetCrs == this.crs) return this
        // Use WGS 84 by default TODO
        val sourceCRS: CoordinateReferenceSystem = this.crs
        val transform = CRS.findMathTransform(sourceCRS, targetCrs, true)
        return GeoDataFrame(
            df.update { geometry }.with { JTS.transform(it, transform) },
            targetCrs,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GeoDataFrame<*>) return false

        return df == other.df &&
            when {
                crs == null && other.crs == null -> true
                crs == null || other.crs == null -> false
                else -> CRS.equalsIgnoreMetadata(crs, other.crs)
            }
    }

    override fun toString(): String = super.toString()

    override fun hashCode(): Int = super.hashCode()

    companion object {
        val DEFAULT_CRS = CRS.decode("EPSG:4326", true)
    }
}

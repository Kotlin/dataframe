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
public class GeoDataFrame<T : WithGeometry>(public val df: DataFrame<T>, public val crs: CoordinateReferenceSystem?) {

    /**
     * Creates a new [GeoDataFrame] by applying transformations to the underlying [DataFrame].
     *
     * This function opens a modification scope where the current [DataFrame] can be transformed using
     * [Kotlin DataFrame operations](https://kotlin.github.io/dataframe/operations.html). The transformation block
     * receives the original [DataFrame] both as a receiver and as an explicit argument, allowing flexible modifications.
     *
     * The Coordinate Reference System (CRS) remains unchanged.
     *
     * @param block A lambda defining the transformations to apply to the DataFrame.
     * @return A new [GeoDataFrame] instance with the modified DataFrame while preserving the original CRS.
     */
    public inline fun modify(block: DataFrame<T>.(DataFrame<T>) -> DataFrame<T>): GeoDataFrame<T> =
        GeoDataFrame(df.block(df), crs)

    /**
     * Transforms the geometries to a specified Coordinate Reference System (CRS).
     *
     * This function reprojects the geometry data from the current CRS to a target CRS.
     * If no target CRS is specified and the `GeoDataFrame` has no CRS, WGS 84 is used by default.
     *
     * @param targetCrs The target CRS for transformation.
     * @return A new `GeoDataFrame` with reprojected geometries and the specified CRS.
     */
    public fun applyCrs(targetCrs: CoordinateReferenceSystem): GeoDataFrame<T> {
        if (targetCrs == this.crs) return this
        // Use WGS 84 by default TODO
        val sourceCRS: CoordinateReferenceSystem = this.crs ?: DEFAULT_CRS
        val transform = CRS.findMathTransform(sourceCRS, targetCrs, true)
        return GeoDataFrame(
            df.update { geometry }.with { JTS.transform(it, transform) },
            targetCrs,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeoDataFrame<*>

        if (df != other.df) return false

        return when {
            crs == null && other.crs == null -> true
            crs == null || other.crs == null -> false
            else -> CRS.equalsIgnoreMetadata(crs, other.crs)
        }
    }

    override fun hashCode(): Int {
        var result = df.hashCode()
        result = 31 * result + (crs?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "GeoDataFrame(df=$df, crs=$crs)"

    public companion object {
        public val DEFAULT_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:4326", true)
    }
}

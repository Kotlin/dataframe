package org.jetbrains.kotlinx.dataframe.geo

import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with

class GeoDataFrame<T : GeoFrame>(val df: DataFrame<T>, val crs: CoordinateReferenceSystem?) {
    fun update(updateBlock: DataFrame<T>.() -> DataFrame<T>): GeoDataFrame<T> {
        return GeoDataFrame(df.updateBlock(), crs)
    }

    fun applyCRS(targetCRS: CoordinateReferenceSystem? = null): GeoDataFrame<T> {
        if (targetCRS == this.crs) return this
        // Use WGS 84 by default TODO
        val sourceCRS: CoordinateReferenceSystem = this.crs ?: DEFAULT_CRS
        val transform = CRS.findMathTransform(sourceCRS, targetCRS, true)
        return GeoDataFrame(
            df.update { geometry }.with { JTS.transform(it, transform) },
            targetCRS
        )
    }

    companion object {
        val DEFAULT_CRS = CRS.decode("EPSG:4326", true)
    }
}

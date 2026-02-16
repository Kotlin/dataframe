package org.jetbrains.kotlinx.dataframe.geo.geotools

import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.data.collection.ListFeatureCollection
import org.geotools.data.simple.SimpleFeatureCollection
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.locationtech.jts.geom.Geometry

/**
 * Converts the `GeoDataFrame` to a `SimpleFeatureCollection`.
 *
 * @param name Optional name for the `SimpleFeatureCollection`. Defaults to "geodata" if not specified.
 * @param singleGeometryType Whether to enforce a single geometry type within the collection. Defaults to false.
 * @return A `SimpleFeatureCollection` representing the `GeoDataFrame`.
 */
public fun GeoDataFrame<*>.toSimpleFeatureCollection(
    name: String? = null,
    singleGeometryType: Boolean = false,
): SimpleFeatureCollection {
    val typeBuilder = SimpleFeatureTypeBuilder()
    typeBuilder.name = name ?: "geodata"
    typeBuilder.setCRS(crs)
    val geometryClass = if (singleGeometryType) {
        // todo singleOrNull() ?: error()
        df["geometry"].map { it!!::class.java }.distinct().single()
    } else {
        Geometry::class.java
    }
    typeBuilder.add("the_geom", geometryClass)
    df.columnNames().filter { it != "geometry" }.forEach { colName ->
        typeBuilder.add(colName, String::class.java)
    }
    val featureType = typeBuilder.buildFeatureType()

    val featureCollection = ListFeatureCollection(featureType)

    val featureBuilder = SimpleFeatureBuilder(featureType)
    // if ID is present, SortedMap in DefaultFeatureCollection sorts rows by ID lexicographically
    // I couldn't disable writing it, so let's generate lexicographically sorted IDs
    val format = "f%0${df.rowsCount().toString().length}d"
    df.forEach { row ->
        val geometry = row["geometry"]
        featureBuilder.add(geometry)
        df.columnNames().filter { it != "geometry" }.forEach { colName ->
            featureBuilder.add(row[colName])
        }
        val feature: SimpleFeature = featureBuilder.buildFeature(String.format(format, index()))
        featureCollection.add(feature)
    }

    return featureCollection
}

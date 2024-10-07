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

fun GeoDataFrame<*>.toSimpleFeatureCollection(
    name: String? = null,
    singleGeometryType: Boolean = false
): SimpleFeatureCollection {
    val typeBuilder = SimpleFeatureTypeBuilder()
    typeBuilder.name = name ?: "geodata"
    typeBuilder.setCRS(crs)
    val geometryClass = if (singleGeometryType) {
        // todo singleOrNull() ?: error()
        df["geometry"].map { it!!::class.java }.distinct().single()
    } else Geometry::class.java
    typeBuilder.add("the_geom", geometryClass)
    df.columnNames().filter { it != "geometry" }.forEach { colName ->
        typeBuilder.add(colName, String::class.java)
    }
    val featureType = typeBuilder.buildFeatureType()

    val featureCollection = ListFeatureCollection(featureType)

    val featureBuilder = SimpleFeatureBuilder(featureType)

    df.forEach { row ->
        val geometry = row["geometry"]
        featureBuilder.add(geometry)
        df.columnNames().filter { it != "geometry" }.forEach { colName ->
            featureBuilder.add(row[colName])
        }
        val feature: SimpleFeature = featureBuilder.buildFeature(null)
        featureCollection.add(feature)
    }

    return featureCollection
}

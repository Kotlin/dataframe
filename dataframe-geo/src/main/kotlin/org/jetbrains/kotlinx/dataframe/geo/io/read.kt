package org.jetbrains.kotlinx.dataframe.geo.io

import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.data.simple.SimpleFeatureCollection
import org.geotools.geojson.feature.FeatureJSON
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.geotools.toGeoDataFrame
import org.jetbrains.kotlinx.dataframe.io.asUrl
import java.io.File
import java.net.URL

public fun GeoDataFrame.Companion.readGeoJson(path: String): GeoDataFrame<*> = readGeoJson(asUrl(path))

public fun GeoDataFrame.Companion.readGeoJson(url: URL): GeoDataFrame<*> =
    url.openStream().use { inputStream ->
        val featureCollection = FeatureJSON().readFeatureCollection(inputStream) as SimpleFeatureCollection
        featureCollection.toGeoDataFrame()
    }

public fun DataFrame.Companion.readGeoJson(path: String): GeoDataFrame<*> = GeoDataFrame.readGeoJson(path)

public fun DataFrame.Companion.readGeoJson(url: URL): GeoDataFrame<*> = GeoDataFrame.readGeoJson(url)

/**
 * Examples:
 * ```
 * GeoDataFrame.readShapefile("simple_points")
 * GeoDataFrame.readShapefile("simple_points/simple_points.shp")
 * ```
 *
 * @param path path to *.shp or *.shp.gz file, or to a directory containing such a file
 */
public fun GeoDataFrame.Companion.readShapefile(path: String): GeoDataFrame<*> {
    val url = resolveShapefileUrl(path)
    return readShapeFileImpl(url)
}

public fun GeoDataFrame.Companion.readShapefile(url: URL): GeoDataFrame<*> {
    val resolvedUrl = if (url.protocol == "file") {
        resolveShapefileUrl(url.path)
    } else {
        url
    }
    return readShapeFileImpl(resolvedUrl)
}

private fun readShapeFileImpl(url: URL): GeoDataFrame<*> {
    val dataStore = ShapefileDataStoreFactory().createDataStore(url)
    try {
        return dataStore.featureSource.features.toGeoDataFrame()
    } finally {
        dataStore.dispose()
    }
}

private fun resolveShapefileUrl(path: String): URL {
    val file = File(path)
    val shpFile = when {
        file.isDirectory -> findShapefileInDirectory(file)
        else -> file
    }
    return shpFile.toURI().toURL()
}

private fun findShapefileInDirectory(dir: File): File =
    File(dir, "${dir.name}.shp").takeIf { it.exists() }
        ?: File(dir, "${dir.name}.shp.gz").takeIf { it.exists() }
        ?: throw IllegalArgumentException("No shapefile found in directory: ${dir.absolutePath}")

public fun DataFrame.Companion.readShapefile(path: String): GeoDataFrame<*> = GeoDataFrame.readShapefile(path)

public fun DataFrame.Companion.readShapefile(url: URL): GeoDataFrame<*> = GeoDataFrame.readShapefile(url)

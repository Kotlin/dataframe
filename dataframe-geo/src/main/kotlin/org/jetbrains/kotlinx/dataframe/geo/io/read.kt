@file:Suppress("INVISIBLE_REFERENCE")

package org.jetbrains.kotlinx.dataframe.geo.io

import org.geotools.data.shapefile.ShapefileDataStoreFactory
import org.geotools.data.simple.SimpleFeatureCollection
import org.geotools.geojson.feature.FeatureJSON
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.geotools.toGeoDataFrame
import org.jetbrains.kotlinx.dataframe.io.asUrl
import java.io.File
import java.net.URL

/**
 * {@comment `readGeoJson` KDoc-snippet}
 *
 * Reads a [GeoJSON](https://geojson.org/) file into a [GeoDataFrame].
 *
 * Each row in the resulting [GeoDataFrame] corresponds to a single
 * [`Feature`](https://datatracker.ietf.org/doc/html/rfc7946#section-3.2)
 * from the input file :
 * * `"geometry"` value is stored in the `geometry` column.
 * * `"properties"` are flattened into individual columns using their keys as column names.
 * * `"crs"` is read as `null` if not specified (interpreted as WGS 84 by default).
 *
 * > Uses the [GeoTools](https://geotools.org/) GeoJSON parser.
 * > Nested properties are not supported and parsed as [`LinkedHashMap<*, *>`][java.util.LinkedHashMap].
 * > According to the specification, the CRS in GeoJSON should be
 * > [WGS 84](https://datatracker.ietf.org/doc/html/rfc7946#section-4);
 * > deprecated "crs" field is used for now;
 * > if it is not present, it is read as `null`.
 */
@ExcludeFromSources
internal typealias ReadGeoJsonSnippet = Nothing

/**
 * @include [ReadGeoJsonSnippet]
 *
 * @param [path] a [String] path to GeoJSON file
 * @return a new [GeoDataFrame] with the data from the GeoJSON file
 */
public fun GeoDataFrame.Companion.readGeoJson(path: String): GeoDataFrame<*> = readGeoJson(asUrl(path))

/**
 * @include [ReadGeoJsonSnippet]
 *
 * @param [url] a [URL] of GeoJSON file
 * @return a new [GeoDataFrame] with the data from the GeoJSON file
 */
public fun GeoDataFrame.Companion.readGeoJson(url: URL): GeoDataFrame<*> =
    url.openStream().use { inputStream ->
        val featureCollection = FeatureJSON().readFeatureCollection(inputStream) as SimpleFeatureCollection
        featureCollection.toGeoDataFrame()
    }

/**
 * @include [ReadGeoJsonSnippet]
 *
 * @param [path] a [String] path to GeoJSON file
 * @return a new [GeoDataFrame] with the data from the GeoJSON file
 */
public fun DataFrame.Companion.readGeoJson(path: String): GeoDataFrame<*> = GeoDataFrame.readGeoJson(path)

/**
 * @include [ReadGeoJsonSnippet]
 *
 * @param [url] a [URL] of GeoJSON file
 * @return a new [GeoDataFrame] with the data from the GeoJSON file
 */
public fun DataFrame.Companion.readGeoJson(url: URL): GeoDataFrame<*> = GeoDataFrame.readGeoJson(url)

/**
 * {@comment `readShapefile` KDoc-snippet}
 *
 * Reads a [Shapefile](https://doc.arcgis.com/en/arcgis-online/reference/shapefiles.htm) file
 * into a [GeoDataFrame].
 *
 * Each row in the resulting [GeoDataFrame] corresponds to a single feature
 * from the shapefile:
 * * `Geometry` is stored in the `geometry` column.
 * * Attribute data from the `.dbf` file is stored in separate columns.
 * * CRS is read from the `.prj` file if present, otherwise set to `null`.
 *
 * Supported input formats:
 * * Path to a `.shp` file.
 * * Path to a `.shp.gz` file.
 * * Path to a directory containing a shapefile with the same name as a directory (expects `<dir>/<dir>.shp`).
 *
 * Requires `.shx` and `.dbf` files with the same name to be present in the same directory as the `.shp` file.
 */
@ExcludeFromSources
internal typealias ReadShapefileSnippet = Nothing

/**
 * @include [ReadShapefileSnippet]
 *
 * ### Examples:
 * ```
 * GeoDataFrame.readShapefile("simple_points")
 * // Do the same
 * GeoDataFrame.readShapefile("simple_points/simple_points.shp")
 * ```
 *
 * @param path path to a Shapefile (*.shp or *.shp.gz file), or to a directory containing such a file
 * @return a new [GeoDataFrame] with the data from the Shapefile
 */
public fun GeoDataFrame.Companion.readShapefile(path: String): GeoDataFrame<*> {
    val url = resolveShapefileUrl(path)
    return readShapeFileImpl(url)
}

/**
 * @include [ReadShapefileSnippet]
 *
 * @param url URL of a Shapefile (*.shp or *.shp.gz file) or of a directory containing such a file
 * @return a new [GeoDataFrame] with the data from the Shapefile
 */
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

/**
 * @include [ReadShapefileSnippet]
 *
 * ### Examples:
 * ```
 * DataFrame.readShapefile("simple_points")
 * // Do the same
 * DataFrame.readShapefile("simple_points/simple_points.shp")
 * ```
 *
 * @param path path to a Shapefile (*.shp or *.shp.gz file), or to a directory containing such a file
 * @return a new [GeoDataFrame] with the data from the Shapefile
 */
public fun DataFrame.Companion.readShapefile(path: String): GeoDataFrame<*> = GeoDataFrame.readShapefile(path)

/**
 * @include [ReadShapefileSnippet]
 *
 * @param url URL of a Shapefile (*.shp or *.shp.gz file) or of a directory containing such a file
 * @return a new [GeoDataFrame] with the data from the Shapefile
 */
public fun DataFrame.Companion.readShapefile(url: URL): GeoDataFrame<*> = GeoDataFrame.readShapefile(url)

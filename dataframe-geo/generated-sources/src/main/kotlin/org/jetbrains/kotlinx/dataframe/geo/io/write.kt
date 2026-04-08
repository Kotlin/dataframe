@file:Suppress("INVISIBLE_REFERENCE")

package org.jetbrains.kotlinx.dataframe.geo.io

import org.geotools.api.data.FileDataStoreFinder
import org.geotools.api.data.SimpleFeatureStore
import org.geotools.api.data.Transaction
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.geojson.feature.FeatureJSON
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.geotools.toSimpleFeatureCollection
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

/**
 *
 *
 * Writes this [GeoDataFrame] into a [GeoJSON](https://geojson.org/) file.
 *
 * Each [`Feature`](https://datatracker.ietf.org/doc/html/rfc7946#section-3.2)
 * in the resulting GeoJSON file corresponds to a single
 * row in this [GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are stored as "properties" of the corresponding names and with the
 * corresponding structure.
 *
 * > According to the specification, the CRS in GeoJSON should be
 * > [WGS 84](https://datatracker.ietf.org/doc/html/rfc7946#section-4);
 * > deprecated "crs" field [is written for now](https://github.com/Kotlin/dataframe/issues/1187);
 */
@ExcludeFromSources
internal typealias WriteGeoJsonSnippet = Nothing

/**
 *
 *
 * Writes this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame] into a [GeoJSON](https://geojson.org/) file.
 *
 * Each [`Feature`](https://datatracker.ietf.org/doc/html/rfc7946#section-3.2)
 * in the resulting GeoJSON file corresponds to a single
 * row in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are stored as "properties" of the corresponding names and with the
 * corresponding structure.
 *
 * > According to the specification, the CRS in GeoJSON should be
 * > [WGS 84](https://datatracker.ietf.org/doc/html/rfc7946#section-4);
 * > deprecated "crs" field [is written for now](https://github.com/Kotlin/dataframe/issues/1187);
 *
 * @param [path] a [String] path to GeoJSON file
 */
public fun GeoDataFrame<*>.writeGeoJson(path: String): Unit = writeGeoJson(File(path))

/**
 *
 *
 * Writes this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame] into a [GeoJSON](https://geojson.org/) file.
 *
 * Each [`Feature`](https://datatracker.ietf.org/doc/html/rfc7946#section-3.2)
 * in the resulting GeoJSON file corresponds to a single
 * row in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are stored as "properties" of the corresponding names and with the
 * corresponding structure.
 *
 * > According to the specification, the CRS in GeoJSON should be
 * > [WGS 84](https://datatracker.ietf.org/doc/html/rfc7946#section-4);
 * > deprecated "crs" field [is written for now](https://github.com/Kotlin/dataframe/issues/1187);
 *
 * @param [path] a [Path] to GeoJSON file
 */
public fun GeoDataFrame<*>.writeGeoJson(path: Path) {
    val featureJSON = FeatureJSON()
    path.outputStream().use { outputStream ->
        featureJSON.writeFeatureCollection(toSimpleFeatureCollection(), outputStream)
    }
}

/**
 *
 *
 * Writes this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame] into a [GeoJSON](https://geojson.org/) file.
 *
 * Each [`Feature`](https://datatracker.ietf.org/doc/html/rfc7946#section-3.2)
 * in the resulting GeoJSON file corresponds to a single
 * row in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are stored as "properties" of the corresponding names and with the
 * corresponding structure.
 *
 * > According to the specification, the CRS in GeoJSON should be
 * > [WGS 84](https://datatracker.ietf.org/doc/html/rfc7946#section-4);
 * > deprecated "crs" field [is written for now](https://github.com/Kotlin/dataframe/issues/1187);
 *
 * @param [file] a GeoJSON file
 */
public fun GeoDataFrame<*>.writeGeoJson(file: File) {
    writeGeoJson(file.toPath())
}

/**
 * Writes this [GeoDataFrame] into a
 * [Shapefile](https://doc.arcgis.com/en/arcgis-online/reference/shapefiles.htm).
 *
 * The shapefile is written into the specified directory. The resulting files use the directory name
 * as the base file name. For example, writing to `roads/` creates `roads.shp`, `roads.shx`,
 * `roads.dbf`, and related files inside that directory.
 *
 * Each feature in the resulting shapefile corresponds to a single row in this [GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are written as feature attributes.
 *
 * The CRS is written to the schema if it is defined in this [GeoDataFrame]. Otherwise,
 * [default CRS][GeoDataFrame.DEFAULT_CRS] is used.
 *
 * If the target directory does not exist, it is created automatically.
 */
@ExcludeFromSources
internal typealias WriteShapefileSnippet = Nothing

/**
 * Writes this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame] into a
 * [Shapefile](https://doc.arcgis.com/en/arcgis-online/reference/shapefiles.htm).
 *
 * The shapefile is written into the specified directory. The resulting files use the directory name
 * as the base file name. For example, writing to `roads/` creates `roads.shp`, `roads.shx`,
 * `roads.dbf`, and related files inside that directory.
 *
 * Each feature in the resulting shapefile corresponds to a single row in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are written as feature attributes.
 *
 * The CRS is written to the schema if it is defined in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]. Otherwise,
 * [default CRS][GeoDataFrame.DEFAULT_CRS] is used.
 *
 * If the target directory does not exist, it is created automatically.
 *
 * @param [directoryPath] a [String] path to a Shapefile directory
 */
public fun GeoDataFrame<*>.writeShapefile(directoryPath: String): Unit = writeShapefile(File(directoryPath))

/**
 * Writes this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame] into a
 * [Shapefile](https://doc.arcgis.com/en/arcgis-online/reference/shapefiles.htm).
 *
 * The shapefile is written into the specified directory. The resulting files use the directory name
 * as the base file name. For example, writing to `roads/` creates `roads.shp`, `roads.shx`,
 * `roads.dbf`, and related files inside that directory.
 *
 * Each feature in the resulting shapefile corresponds to a single row in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are written as feature attributes.
 *
 * The CRS is written to the schema if it is defined in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]. Otherwise,
 * [default CRS][GeoDataFrame.DEFAULT_CRS] is used.
 *
 * If the target directory does not exist, it is created automatically.
 *
 * @param [directory] a [Path] to a Shapefile directory
 */
public fun GeoDataFrame<*>.writeShapefile(directory: Path) {
    if (directory.notExists()) {
        directory.createDirectories()
    }
    val fileName = directory.fileName.toString()
    val shp = directory.resolve("$fileName.shp")

    val creationParams = mutableMapOf<String, java.io.Serializable>()
    creationParams["url"] = shp.toUri().toURL()

    val factory = FileDataStoreFinder.getDataStoreFactory("shp")
    val dataStore = factory.createNewDataStore(creationParams)

    val featureCollection = toSimpleFeatureCollection(fileName, true)

    val schema = featureCollection.schema
    val schemaWithCrs = SimpleFeatureTypeBuilder.retype(schema, crs ?: GeoDataFrame.DEFAULT_CRS)

    dataStore.createSchema(schemaWithCrs)

    val featureSource = dataStore.getFeatureSource(fileName) as SimpleFeatureStore
    val transaction = Transaction.AUTO_COMMIT

    try {
        featureSource.addFeatures(featureCollection)
        transaction.commit()
    } catch (e: Exception) {
        e.printStackTrace()
        transaction.rollback()
    } finally {
        dataStore.dispose()
        transaction.close()
    }
}

/**
 * Writes this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame] into a
 * [Shapefile](https://doc.arcgis.com/en/arcgis-online/reference/shapefiles.htm).
 *
 * The shapefile is written into the specified directory. The resulting files use the directory name
 * as the base file name. For example, writing to `roads/` creates `roads.shp`, `roads.shx`,
 * `roads.dbf`, and related files inside that directory.
 *
 * Each feature in the resulting shapefile corresponds to a single row in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]:
 * * `"geometry"` value is taken from the `geometry` column.
 * * other column values are written as feature attributes.
 *
 * The CRS is written to the schema if it is defined in this [GeoDataFrame][org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame]. Otherwise,
 * [default CRS][GeoDataFrame.DEFAULT_CRS] is used.
 *
 * If the target directory does not exist, it is created automatically.
 *
 * @param [directory] a Shapefile directory
 */
public fun GeoDataFrame<*>.writeShapefile(directory: File) {
    writeShapefile(directory.toPath())
}

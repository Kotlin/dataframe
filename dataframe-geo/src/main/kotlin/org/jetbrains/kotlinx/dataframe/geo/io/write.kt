package org.jetbrains.kotlinx.dataframe.geo.io

import org.geotools.api.data.FileDataStoreFinder
import org.geotools.api.data.SimpleFeatureStore
import org.geotools.api.data.Transaction
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.geojson.feature.FeatureJSON
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.geotools.toSimpleFeatureCollection
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun GeoDataFrame<*>.writeGeoJson(path: String): Unit = writeGeoJson(File(path))

/** Path overload for writing GeoJSON */
fun GeoDataFrame<*>.writeGeoJson(path: Path) {
    val featureJSON = FeatureJSON()
    Files.newOutputStream(path).use { outputStream ->
        featureJSON.writeFeatureCollection(toSimpleFeatureCollection(), outputStream)
    }
}

fun GeoDataFrame<*>.writeGeoJson(file: File) {
    // TODO: adds ids that breaks order of reading
    val featureJSON = FeatureJSON()
    file.outputStream().use { outputStream ->
        featureJSON.writeFeatureCollection(toSimpleFeatureCollection(), outputStream)
    }
}

fun GeoDataFrame<*>.writeShapefile(directoryPath: String): Unit = writeShapefile(File(directoryPath))

/** Path overload for writing Shapefile to a directory */
fun GeoDataFrame<*>.writeShapefile(directory: Path) {
    writeShapefile(directory.toFile())
}

fun GeoDataFrame<*>.writeShapefile(directory: File) {
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val fileName = directory.name

    val file = File(directory, "$fileName.shp")

    val creationParams = mutableMapOf<String, java.io.Serializable>()
    creationParams["url"] = file.toURI().toURL()

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

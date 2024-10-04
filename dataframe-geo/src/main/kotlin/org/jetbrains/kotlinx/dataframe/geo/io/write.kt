package org.jetbrains.kotlinx.dataframe.geo.io

import org.geotools.api.data.FileDataStoreFinder
import org.geotools.api.data.SimpleFeatureStore
import org.geotools.api.data.Transaction
import org.geotools.geojson.feature.FeatureJSON
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.geotools.toSimpleFeatureCollection
import java.io.File


fun GeoDataFrame<*>.writeGeoJson(path: String): Unit = writeGeoJson(File(path))

fun GeoDataFrame<*>.writeGeoJson(file: File) {
    val featureJSON = FeatureJSON()
    file.outputStream().use { outputStream ->
        featureJSON.writeFeatureCollection(toSimpleFeatureCollection(), outputStream)
    }
}

fun GeoDataFrame<*>.writeShapefile(directoryPath: String): Unit = writeShapefile(File(directoryPath))

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

    dataStore.createSchema(schema)

    val featureSource = dataStore.getFeatureSource(fileName) as SimpleFeatureStore
    val transaction = Transaction.AUTO_COMMIT

    try {
        featureSource.addFeatures(featureCollection)
        transaction.commit()
    } catch (e: Exception) {
        e.printStackTrace()
        transaction.rollback()
    } finally {
        transaction.close()
    }

}

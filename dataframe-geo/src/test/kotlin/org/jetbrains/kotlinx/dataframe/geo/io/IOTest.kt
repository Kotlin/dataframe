package org.jetbrains.kotlinx.dataframe.geo.io

import org.geotools.referencing.CRS
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.toGeo
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class IOTest {

    private val simplePointsDf = run {
        val geometryFactory = GeometryFactory()
        val point1 = geometryFactory.createPoint(Coordinate(30.5, 50.5))
        val point2 = geometryFactory.createPoint(Coordinate(31.5, 51.5))
        dataFrameOf("name", "geometry")(
            "Point 1",
            point1,
            "Point 2",
            point2,
        )
    }
    private val simplePointsGeoDf = simplePointsDf.toGeo(GeoDataFrame.DEFAULT_CRS)
    private val classLoader = (this::class as Any).javaClass.classLoader

    @Test
    fun readGeoJson() {
        val jsonURL = classLoader.getResource("./simple_points.geojson")!!
        val geodf = GeoDataFrame.readGeoJson(jsonURL)

        assertEquals(simplePointsDf, geodf.df)
        assert(geodf.crs == null)
    }

    @Test
    fun writeGeoJson() {
        val tempFile = Files.createTempFile("simple_points", ".json").toFile()
        simplePointsGeoDf.writeGeoJson(tempFile)

        val loadedGeoDataFrame = GeoDataFrame.readGeoJson(tempFile.toURI().toURL())
        assertEquals(simplePointsGeoDf.df, loadedGeoDataFrame.df)
        // TODO: Doesn't work because of how equality between CRS is checked by geotools
        // assertEquals(simplePointsGeoDf, loadedGeoDataFrame)

        tempFile.deleteOnExit()
    }

    @Test
    fun readShapefile() {
        val shapefileURL = classLoader.getResource("./simple_points/simple_points.shp")!!
        val geodf = GeoDataFrame.readShapefile(shapefileURL)

        assertEquals(simplePointsDf, geodf.df)
        assert(geodf.crs == null)
    }

    @Test
    fun writeShapefile() {
        val tempDir = Files.createTempDirectory("shapefiles").toFile()
        val tempShapefileDir = File(tempDir, "simple_points").also { it.mkdir() }
        simplePointsGeoDf.writeShapefile(tempShapefileDir)
        val shapefile = File("${tempShapefileDir.path}/simple_points.shp")
        assertEquals(simplePointsGeoDf, GeoDataFrame.readShapefile(shapefile.toURI().toURL()))
        tempDir.deleteOnExit()
    }
}

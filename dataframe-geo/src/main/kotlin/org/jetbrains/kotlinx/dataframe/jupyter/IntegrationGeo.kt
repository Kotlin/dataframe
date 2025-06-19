@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.WithGeometry
import org.jetbrains.kotlinx.dataframe.geo.WithLineStringGeometry
import org.jetbrains.kotlinx.dataframe.geo.WithMultiLineStringGeometry
import org.jetbrains.kotlinx.dataframe.geo.WithMultiPointGeometry
import org.jetbrains.kotlinx.dataframe.geo.WithMultiPolygonGeometry
import org.jetbrains.kotlinx.dataframe.geo.WithPointGeometry
import org.jetbrains.kotlinx.dataframe.geo.WithPolygonGeometry
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl
import org.jetbrains.kotlinx.jupyter.api.FieldHandler
import org.jetbrains.kotlinx.jupyter.api.FieldHandlerExecution
import org.jetbrains.kotlinx.jupyter.api.libraries.FieldHandlerFactory
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

/**
 * DataFrame Jupyter integration for geo module.
 *
 * Adds all necessary imports.
 * Adds type converter for inner dataframe in `GeoDataFrame`.
 */
internal class IntegrationGeo : JupyterIntegration() {

    override fun Builder.onLoaded() {
        import("org.jetbrains.kotlinx.dataframe.geo.*")
        import("org.jetbrains.kotlinx.dataframe.geo.io.*")
        import("org.jetbrains.kotlinx.dataframe.geo.jts.*")
        import("org.jetbrains.kotlinx.dataframe.geo.geotools.*")
        import("org.jetbrains.kotlinx.dataframe.geo.geocode.*")
        import("org.geotools.referencing.CRS")

        import("org.locationtech.jts.geom.Geometry")
        import("org.locationtech.jts.geom.Point")
        import("org.locationtech.jts.geom.MultiPoint")
        import("org.locationtech.jts.geom.LineString")
        import("org.locationtech.jts.geom.MultiLineString")
        import("org.locationtech.jts.geom.Polygon")
        import("org.locationtech.jts.geom.MultiPolygon")
        import("org.locationtech.jts.geom.Envelope")

        onLoaded {
            useSchema<WithGeometry>()
            useSchema<WithPolygonGeometry>()
            useSchema<WithMultiPolygonGeometry>()
            useSchema<WithPointGeometry>()
            useSchema<WithMultiPointGeometry>()
            useSchema<WithLineStringGeometry>()
            useSchema<WithMultiLineStringGeometry>()
        }

        val replCodeGeneratorImpl = ReplCodeGeneratorImpl()
        replCodeGeneratorImpl.process(WithGeometry::class)
        replCodeGeneratorImpl.process(WithPolygonGeometry::class)
        replCodeGeneratorImpl.process(WithMultiPolygonGeometry::class)
        replCodeGeneratorImpl.process(WithPointGeometry::class)
        replCodeGeneratorImpl.process(WithMultiPointGeometry::class)
        replCodeGeneratorImpl.process(WithLineStringGeometry::class)
        replCodeGeneratorImpl.process(WithMultiLineStringGeometry::class)
        val execution = FieldHandlerFactory.createUpdateExecution<GeoDataFrame<*>> { geo, kProperty ->
            // TODO rewrite better
            val generatedDf = execute(
                codeWithTypeCastGenerator = replCodeGeneratorImpl.process(geo.df, kProperty),
                expression = "(${kProperty.name}.df as DataFrame<*>)",
            )
            val name = execute("GeoDataFrame($generatedDf, ${kProperty.name}.crs)").name
            name
        }

        addTypeConverter(object : FieldHandler {
            override val execution: FieldHandlerExecution<*> = execution

            override fun accepts(value: Any?, property: KProperty<*>): Boolean =
                property.returnType.isSubtypeOf(typeOf<GeoDataFrame<*>>())
        })
    }
}

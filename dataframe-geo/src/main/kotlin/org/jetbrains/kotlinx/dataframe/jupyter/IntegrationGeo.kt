@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.jetbrains.kotlinx.dataframe.jupyter


import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.WithGeometry
import org.jetbrains.kotlinx.dataframe.geo.WithMultiPolygon
import org.jetbrains.kotlinx.dataframe.geo.WithPolygon
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
        onLoaded {
            useSchema<WithGeometry>()
            useSchema<WithPolygon>()
            useSchema<WithMultiPolygon>()
        }
        val replCodeGeneratorImpl = ReplCodeGeneratorImpl()
        replCodeGeneratorImpl.process(WithGeometry::class)
        replCodeGeneratorImpl.process(WithPolygon::class)
        replCodeGeneratorImpl.process(WithMultiPolygon::class)
        val execution = FieldHandlerFactory.createUpdateExecution<GeoDataFrame<*>> { geo, kProperty ->
            // TODO rewrite
            val generatedDf = execute(
                codeWithConverter = replCodeGeneratorImpl.process(geo.df, kProperty),
                "(${kProperty.name}.df as DataFrame<*>)"
            )
            val name = execute("GeoDataFrame($generatedDf, ${kProperty.name}.crs)").name
            name
        }


        addTypeConverter(object : FieldHandler {
            override val execution: FieldHandlerExecution<*> = execution

            override fun accepts(value: Any?, property: KProperty<*>): Boolean {
                return property.returnType.isSubtypeOf(typeOf<GeoDataFrame<*>>())
            }
        })
    }
}

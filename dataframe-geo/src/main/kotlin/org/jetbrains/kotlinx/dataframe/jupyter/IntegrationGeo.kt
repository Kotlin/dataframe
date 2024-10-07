@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.jetbrains.kotlinx.dataframe.jupyter


import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.GeoFrame
import org.jetbrains.kotlinx.dataframe.geo.MultiPolygonGeoFrame
import org.jetbrains.kotlinx.dataframe.geo.PolygonGeoFrame
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl
import org.jetbrains.kotlinx.jupyter.api.FieldHandler
import org.jetbrains.kotlinx.jupyter.api.FieldHandlerExecution
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.VariableName
import org.jetbrains.kotlinx.jupyter.api.libraries.FieldHandlerFactory
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

internal class IntegrationGeo : JupyterIntegration() {

    // TODO make internal in core and use here
    private fun KotlinKernelHost.execute(codeWithConverter: CodeWithConverter, argument: String): VariableName? {
        val code = codeWithConverter.with(argument)
        return if (code.isNotBlank()) {
            val result = execute(code)
            if (codeWithConverter.hasConverter) {
                result.name
            } else {
                null
            }
        } else {
            null
        }
    }

    // TODO make internal in core and use here
    private fun KotlinKernelHost.execute(
        codeWithConverter: CodeWithConverter,
        property: KProperty<*>,
        type: KType,
    ): VariableName? {
        val variableName = "(${property.name}${if (property.returnType.isMarkedNullable) "!!" else ""} as $type)"
        return execute(codeWithConverter, variableName)
    }

    override fun Builder.onLoaded() {
        import("org.jetbrains.kotlinx.dataframe.geo.*")
        import("org.jetbrains.kotlinx.dataframe.geo.io.*")
        import("org.jetbrains.kotlinx.dataframe.geo.jts.*")
        import("org.jetbrains.kotlinx.dataframe.geo.geotools.*")
        import("org.jetbrains.kotlinx.dataframe.geo.geocode.*")
        onLoaded {
            useSchema<GeoFrame>()
            useSchema<PolygonGeoFrame>()
            useSchema<MultiPolygonGeoFrame>()
        }
        val replCodeGeneratorImpl = ReplCodeGeneratorImpl()
        replCodeGeneratorImpl.process(GeoFrame::class)
        replCodeGeneratorImpl.process(PolygonGeoFrame::class)
        replCodeGeneratorImpl.process(MultiPolygonGeoFrame::class)
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

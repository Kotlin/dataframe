package org.jetbrains.dataframe.internal.codeGen

import org.jetbrains.dataframe.impl.codeGen.SchemaProcessorImpl
import org.jetbrains.dataframe.internal.schema.DataFrameSchema

internal interface SchemaProcessor {

    val generatedMarkers: List<Marker>

    val namePrefix: String

    fun process(
        schema: DataFrameSchema,
        isOpen: Boolean,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC
    ): Marker

    companion object {
        fun create(namePrefix: String, existingMarkers: Iterable<Marker> = emptyList()) = SchemaProcessorImpl(existingMarkers, namePrefix)
    }
}

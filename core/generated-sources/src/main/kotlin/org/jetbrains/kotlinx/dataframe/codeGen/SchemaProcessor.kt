package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.impl.codeGen.SchemaProcessorImpl
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

internal interface SchemaProcessor {

    val generatedMarkers: List<Marker>

    val namePrefix: String

    fun process(
        schema: DataFrameSchema,
        isOpen: Boolean,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC
    ): Marker

    companion object {
        fun create(
            namePrefix: String,
            existingMarkers: Iterable<Marker> = emptyList(),
            fieldNameNormalizer: (String) -> String = { it }
        ): SchemaProcessorImpl {
            return SchemaProcessorImpl(existingMarkers, namePrefix, fieldNameNormalizer)
        }
    }
}

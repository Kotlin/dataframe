package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.impl.DELIMITED_STRING_REGEX
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.codeGen.SchemaProcessorImpl
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
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
            normalizeFieldNames: Boolean = false
        ): SchemaProcessorImpl {
            return if (normalizeFieldNames) {
                SchemaProcessorImpl(existingMarkers, namePrefix, fieldNameNormalizer)
            } else {
                SchemaProcessorImpl(existingMarkers, namePrefix)
            }
        }

        private val fieldNameNormalizer: (String) -> String = {
            if (it matches DELIMITED_STRING_REGEX) {
                it.lowercase().toCamelCaseByDelimiters(DELIMITERS_REGEX)
            } else {
                it
            }
        }
    }
}

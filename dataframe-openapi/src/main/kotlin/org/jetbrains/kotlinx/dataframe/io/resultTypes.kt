package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.codeGen.name

/** Represents a query to find a [Marker] with certain name. Produces a [MarkerResult]. */
internal fun interface GetRefMarker {

    /** Produces a [MarkerResult] (either [MarkerResult.CannotFindRefMarker] or [MarkerResult.OpenApiMarker]) for the
     * given [refName] representing a query to find a marker with that given name. */
    operator fun invoke(refName: String): MarkerResult
}

/** Either [MarkerResult.CannotFindRefMarker] or [MarkerResult.OpenApiMarker] containing an [org.jetbrains.kotlinx.dataframe.io.OpenApiMarker]. */
internal sealed interface MarkerResult {

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : MarkerResult

    /** Successfully found or created [marker]. */
    data class OpenApiMarker(val marker: org.jetbrains.kotlinx.dataframe.io.OpenApiMarker) : MarkerResult

    companion object {
        fun fromNullable(schema: org.jetbrains.kotlinx.dataframe.io.OpenApiMarker?): MarkerResult =
            if (schema == null) CannotFindRefMarker else OpenApiMarker(schema)
    }
}

/** Either [FieldTypeResult.CannotFindRefMarker] or [FieldTypeResult.FieldType]. */
internal sealed interface FieldTypeResult {

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : FieldTypeResult

    /** ColumnSchema [fieldType] created successfully. */
    data class FieldType(
        val fieldType: org.jetbrains.kotlinx.dataframe.codeGen.FieldType,
        val additionalPropertyPaths: List<JsonPath> = emptyList(),
    ) : FieldTypeResult
}

/**
 * Represents a call to produce an additional [Marker] from inside a schema component.
 * Not all objects or enums are named, so this is used to create and produce a name for them.
 */
internal fun interface ProduceAdditionalMarker {

    /**
     * Produces an additional Marker with the given [validName].
     *
     * @param isTopLevelObject only used in `allOf` cases. If true, the additionally produced marker is a top-level object
     *  that is to be merged with another object.
     * @param marker the marker to produce.
     * @param validName the name of the marker.
     * @return the name of the produced marker. This name is guaranteed to be unique and might not be the same as the
     *   provided [validName].
     */
    operator fun invoke(
        validName: ValidFieldName,
        marker: OpenApiMarker,
        isTopLevelObject: Boolean,
    ): String

    companion object {
        /** No-op implementation. Passes through `validName`. */
        val NOOP = ProduceAdditionalMarker { validName, _, _ -> validName.quotedIfNeeded }
    }
}

/**
 * Represents a call to [toMarker] that can be repeated until it returns a [MarkerResult.OpenApiMarker].
 */
internal fun interface RetrievableMarker {

    /**
     * Represents a call to [toMarker] that can be repeated until it returns a [MarkerResult.OpenApiMarker].
     *
     * @param getRefMarker              A function that returns a [Marker] for a given reference name if successful.
     * @param produceAdditionalMarker   A function that produces an additional [Marker] for a given name.
     *                                  This is used for `object` types not present in the root of `components/schemas`.
     *
     * @return A [MarkerResult.OpenApiMarker] if successful, otherwise [MarkerResult.CannotFindRefMarker].
     */
    operator fun invoke(
        getRefMarker: GetRefMarker,
        produceAdditionalMarker: ProduceAdditionalMarker,
    ): MarkerResult
}

/** Either a [OpenApiTypeResult.UsingRef], [OpenApiTypeResult.CannotFindRefMarker], [OpenApiTypeResult.OpenApiType],
 * or [OpenApiTypeResult.Enum]. */
internal sealed interface OpenApiTypeResult {

    /** Property is a reference with name [name] and Marker [marker]. Ref cannot be nullable by OpenAPI spec. */
    class UsingRef(val marker: OpenApiMarker) : OpenApiTypeResult

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : OpenApiTypeResult

    /** Property is a schema with OpenApiType [openApiType]. */
    data class OpenApiType(
        val openApiType: org.jetbrains.kotlinx.dataframe.io.OpenApiType,
        val nullable: Boolean,
    ) : OpenApiTypeResult

    /** Property is an enum with values [values]. */
    data class Enum(val values: List<String>, val nullable: Boolean) : OpenApiTypeResult
}


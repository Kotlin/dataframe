package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.GeneratedField
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.codeGen.name

/**
 * Represents the type of Markers that we can use for code generation.
 * This includes [OpenApiMarker.Enum], [OpenApiMarker.Interface] (and [OpenApiMarker.AdditionalPropertyInterface]),
 * [OpenApiMarker.TypeAlias], and [OpenApiMarker.MarkerAlias].
 * It's a bit more flexible than [Marker] and insures the right arguments are given for the right type of [Marker].
 */
internal sealed class OpenApiMarker private constructor(
    val nullable: Boolean, // in openApi, just like an enum, nullability can be saved in the object
    protected val topInterfaceName: ValidFieldName,
    name: String,
    visibility: MarkerVisibility,
    fields: List<GeneratedField>,
    superMarkers: List<Marker>,
    prependTopInterfaceName: Boolean = true,
) : IsObject,
    Marker(
        name = if (prependTopInterfaceName) name.withTopInterfaceName(topInterfaceName) else name,
        isOpen = false,
        fields = fields,
        superMarkers = superMarkers,
        visibility = visibility,
        typeParameters = emptyList(),
        typeArguments = emptyList(),
    ) {

    abstract val additionalPropertyPaths: List<JsonPath>
    abstract fun withName(name: String, prependTopInterfaceName: Boolean = true): OpenApiMarker
    abstract fun withVisibility(visibility: MarkerVisibility): OpenApiMarker

    abstract fun toFieldType(): FieldType

    override fun toString(): String =
        "MyMarker(markerType = ${this::class}, name = $name, isOpen = $isOpen, fields = $fields, superMarkers = $superMarkers, visibility = $visibility, typeParameters = $typeParameters, typeArguments = $typeArguments)"

    /**
     * A [Marker] that will be used to generate an enum.
     *
     * @param nullable whether the enum can be null. Needs to be checked when referring to this [Marker].
     * @param fields the fields of the enum, can be created using [generatedEnumFieldOf].
     * @param name the name of the enum.
     * @param visibility the visibility of the enum.
     */
    class Enum(
        nullable: Boolean,
        fields: List<GeneratedField>,
        name: String,
        topInterfaceName: ValidFieldName,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
        prependTopInterfaceName: Boolean = true,
    ) : OpenApiMarker(
        nullable = nullable,
        name = name,
        topInterfaceName = topInterfaceName,
        visibility = visibility,
        fields = fields,
        superMarkers = emptyList(),
        prependTopInterfaceName = prependTopInterfaceName,
    ) {

        // enums become List<Something>, not Dataframe<*>
        override val isObject = false

        override val additionalPropertyPaths: List<JsonPath> = emptyList()

        override fun toFieldType(): FieldType =
            FieldType.ValueFieldType(
                // nullable or not, an enum must contain null to be nullable
                // https://github.com/OAI/OpenAPI-Specification/blob/main/proposals/2019-10-31-Clarify-Nullable.md#if-a-schema-specifies-nullable-true-and-enum-1-2-3-does-that-schema-allow-null-values-see-1900
                // if not required, it can still be omitted, resulting in null in Kotlin
                typeFqName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String, prependTopInterfaceName: Boolean): Enum =
            Enum(
                nullable = nullable,
                fields = fields,
                name = name,
                topInterfaceName = topInterfaceName,
                visibility = visibility,
                prependTopInterfaceName = prependTopInterfaceName,
            )

        override fun withVisibility(visibility: MarkerVisibility): Enum =
            Enum(
                nullable = nullable,
                fields = fields,
                name = name,
                topInterfaceName = topInterfaceName,
                visibility = visibility
            )
    }

    /**
     * A [Marker] that will be used to generate an interface.
     *
     * @param nullable whether the object can be null. Needs to be checked when referring to this [Marker].
     * @param fields the fields of the enum, can be created using [generatedFieldOf].
     * @param name the name of the interface.
     * @param visibility the visibility of the interface.
     */
    open class Interface(
        nullable: Boolean,
        fields: List<GeneratedField>,
        superMarkers: List<Marker>,
        name: String,
        topInterfaceName: ValidFieldName,
        override val additionalPropertyPaths: List<JsonPath>,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
        prependTopInterfaceName: Boolean = true,
    ) : OpenApiMarker(
        nullable = nullable,
        name = name,
        topInterfaceName = topInterfaceName,
        visibility = visibility,
        fields = fields,
        superMarkers = superMarkers,
        prependTopInterfaceName = prependTopInterfaceName,
    ) {

        // Will be a DataFrame<*>
        override val isObject = true

        override fun toFieldType(): FieldType =
            FieldType.GroupFieldType(
                markerName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String, prependTopInterfaceName: Boolean): Interface =
            Interface(
                nullable = nullable,
                fields = fields,
                superMarkers = superMarkers.values.toList(),
                name = name,
                topInterfaceName = topInterfaceName,
                additionalPropertyPaths = additionalPropertyPaths,
                visibility = visibility,
                prependTopInterfaceName = prependTopInterfaceName,
            )

        override fun withVisibility(visibility: MarkerVisibility): Interface =
            Interface(
                nullable = nullable,
                fields = fields,
                superMarkers = superMarkers.values.toList(),
                name = name,
                topInterfaceName = topInterfaceName,
                additionalPropertyPaths = additionalPropertyPaths,
                visibility = visibility
            )
    }

    /**
     * Special type of [Interface] that inherits [AdditionalProperty]. Also generates different read-methods in
     * [DefaultReadOpenApiMethod] including automatic conversion to [AdditionalProperty].
     *
     * @param nullable whether the object can be null. Needs to be checked when referring to this [Marker].
     * @param valueType the type of the value of the [AdditionalProperty].
     * @param name the name of the interface.
     * @param visibility the visibility of the interface.
     */
    class AdditionalPropertyInterface(
        nullable: Boolean,
        val valueType: FieldType,
        name: String,
        topInterfaceName: ValidFieldName,
        additionalPropertyPaths: List<JsonPath>,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
        prependTopInterfaceName: Boolean = true,
    ) : Interface(
        nullable = nullable,
        name = name,
        topInterfaceName = topInterfaceName,
        visibility = visibility,
        fields = listOf(
            generatedFieldOf(
                overrides = true,
                fieldName = ValidFieldName.of(AdditionalProperty<*>::`value`.name),
                columnName = "value",
                fieldType = valueType,
            ),
            generatedFieldOf(
                overrides = true,
                fieldName = ValidFieldName.of(AdditionalProperty<*>::key.name),
                columnName = AdditionalProperty<*>::key.name,
                fieldType = FieldType.ValueFieldType(String::class.qualifiedName!!),
            ),
        ),
        additionalPropertyPaths = (additionalPropertyPaths + JsonPath()).distinct(),
        superMarkers = listOf(AdditionalProperty.getMarker(listOf(valueType.name))),
        prependTopInterfaceName = prependTopInterfaceName,
    ) {

        // Will be a DataFrame<out AdditionalProperty>
        override val isObject = true

        override fun toFieldType(): FieldType =
            FieldType.FrameFieldType(
                markerName = name + if (nullable) "?" else "",
                nullable = false,
            )

        override fun withName(name: String, prependTopInterfaceName: Boolean): AdditionalPropertyInterface =
            AdditionalPropertyInterface(
                nullable = nullable,
                valueType = valueType,
                name = name,
                topInterfaceName = topInterfaceName,
                additionalPropertyPaths = additionalPropertyPaths,
                visibility = visibility,
                prependTopInterfaceName = prependTopInterfaceName,
            )

        override fun withVisibility(visibility: MarkerVisibility): AdditionalPropertyInterface =
            AdditionalPropertyInterface(
                nullable = nullable,
                valueType = valueType,
                name = name,
                topInterfaceName = topInterfaceName,
                additionalPropertyPaths = additionalPropertyPaths,
                visibility = visibility
            )
    }

    /**
     * A [Marker] that will be used to generate a type alias that points at a primitive.
     *
     * @param nullable whether the object can be null. Needs to be checked when referring to this [Marker].
     * @param name the name of the type alias.
     * @param superMarkerName the name of the type that the type alias points at.
     * @param visibility the visibility of the type alias.
     */
    class TypeAlias(
        nullable: Boolean,
        name: String,
        topInterfaceName: ValidFieldName,
        val superMarkerName: String,
        override val additionalPropertyPaths: List<JsonPath>,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
        prependTopInterfaceName: Boolean = false,
    ) : OpenApiMarker(
        nullable = nullable,
        name = name,
        topInterfaceName = topInterfaceName,
        visibility = visibility,
        fields = emptyList(),
        superMarkers = listOf(
            Marker(
                name = superMarkerName,

                // all below is unused
                isOpen = false,
                fields = emptyList(),
                superMarkers = emptyList(),
                visibility = MarkerVisibility.IMPLICIT_PUBLIC,
                typeParameters = emptyList(),
                typeArguments = emptyList(),
            )
        ),
        prependTopInterfaceName = prependTopInterfaceName,
    ) {

        override val isObject = false

        override fun toFieldType(): FieldType =
            FieldType.ValueFieldType(
                typeFqName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String, prependTopInterfaceName: Boolean): TypeAlias =
            TypeAlias(
                nullable = nullable,
                name = name,
                topInterfaceName = topInterfaceName,
                superMarkerName = superMarkerName,
                additionalPropertyPaths = additionalPropertyPaths,
                visibility = visibility,
                prependTopInterfaceName = prependTopInterfaceName,
            )

        override fun withVisibility(visibility: MarkerVisibility): TypeAlias =
            TypeAlias(
                nullable = nullable,
                name = name,
                topInterfaceName = topInterfaceName,
                superMarkerName = superMarkerName,
                additionalPropertyPaths = additionalPropertyPaths,
                visibility = visibility,
            )
    }

    /**
     * A [Marker] that will be used to generate a type alias that points at another [Marker].
     *
     * @param superMarker the type that the type alias points at.
     * @param nullable whether the typealias points at a nullable type.
     * @param name the name of the type alias.
     * @param visibility the visibility of the type alias.
     */
    class MarkerAlias(
        private val superMarker: OpenApiMarker,
        topInterfaceName: ValidFieldName,
        nullable: Boolean,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
        prependTopInterfaceName: Boolean = false,
    ) : OpenApiMarker(
        nullable = nullable || superMarker.nullable,
        name = name,
        topInterfaceName = topInterfaceName,
        visibility = visibility,
        fields = emptyList(),
        superMarkers = listOf(superMarker),
        prependTopInterfaceName = prependTopInterfaceName,
    ) {

        // depends on the marker it points to whether it's primitive or not
        override val isObject = superMarker.isObject

        override val additionalPropertyPaths: List<JsonPath> = superMarker.additionalPropertyPaths

        override fun toFieldType(): FieldType =
            FieldType.GroupFieldType(
                markerName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String, prependTopInterfaceName: Boolean): MarkerAlias =
            MarkerAlias(
                superMarker = superMarker,
                topInterfaceName = topInterfaceName,
                nullable = nullable,
                name = name,
                visibility = visibility,
                prependTopInterfaceName = prependTopInterfaceName,
            )

        override fun withVisibility(visibility: MarkerVisibility): MarkerAlias =
            MarkerAlias(
                superMarker = superMarker,
                topInterfaceName = topInterfaceName,
                nullable = nullable,
                name = name,
                visibility = visibility,
            )
    }
}

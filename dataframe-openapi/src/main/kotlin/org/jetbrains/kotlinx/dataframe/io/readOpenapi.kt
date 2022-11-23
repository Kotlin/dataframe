package org.jetbrains.kotlinx.dataframe.io // ktlint-disable filename

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.core.models.AuthorizationValue
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.GeneratedField
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.codeGen.isNullable
import org.jetbrains.kotlinx.dataframe.codeGen.name
import org.jetbrains.kotlinx.dataframe.codeGen.toNotNullable
import org.jetbrains.kotlinx.dataframe.codeGen.toNullable
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Any.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.AnyObject.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Array.getTypeAsFrame
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Array.getTypeAsFrameList
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Array.getTypeAsList
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Boolean.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Integer.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Number.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Object.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.String.getType
import org.jetbrains.kotlinx.jupyter.api.Code
import kotlin.reflect.typeOf

/** Parse and read OpenApi specification to [DataSchema] interfaces. */
public fun readOpenApi(
    uri: String,
    name: String,
    auth: List<AuthorizationValue>? = null,
    options: ParseOptions? = null,
    extensionProperties: Boolean,
    generateHelperCompanionObject: Boolean,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): Code {
    require(isOpenApi(uri)) { "Not an OpenApi specification with type schemas: $uri" }

    return readOpenApi(
        swaggerParseResult = OpenAPIParser().readLocation(uri, auth, options),
        name = name,
        extensionProperties = extensionProperties,
        visibility = visibility,
        generateHelperCompanionObject = generateHelperCompanionObject,
    )
}

/** Parse and read OpenApi specification to [DataSchema] interfaces. */
public fun readOpenApiAsString(
    openApiAsString: String,
    name: String,
    auth: List<AuthorizationValue>? = null,
    options: ParseOptions? = null,
    extensionProperties: Boolean,
    generateHelperCompanionObject: Boolean,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): Code {
    require(isOpenApiStr(openApiAsString)) { "Not an OpenApi specification with type schemas: $openApiAsString" }

    return readOpenApi(
        swaggerParseResult = OpenAPIParser().readContents(openApiAsString, auth, options),
        name = name,
        extensionProperties = extensionProperties,
        visibility = visibility,
        generateHelperCompanionObject = generateHelperCompanionObject,
    )
}

/**
 * Converts a parsed OpenAPI specification into [Code] consisting of [DataSchema] interfaces.
 *
 * @param swaggerParseResult the result of parsing an OpenAPI specification, created using [readOpenApi] or [readOpenApiAsString].
 * @param extensionProperties whether to add extension properties to the generated interfaces. This is usually not
 *   necessary, since both the KSP- and the Gradle plugin, will add extension properties to the generated code.
 * @param visibility the visibility of the generated marker classes.
 *
 * @return a [Code] object, representing the generated code.
 */
private fun readOpenApi(
    swaggerParseResult: SwaggerParseResult,
    name: String,
    extensionProperties: Boolean,
    generateHelperCompanionObject: Boolean,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): Code {
    val openApi = swaggerParseResult.openAPI
        ?: error("Failed to parse OpenAPI, ${swaggerParseResult.messages.toList()}")

    val topInterfaceName = ValidFieldName.of(name)

    // take the components.schemas from the openApi spec and convert them to a list of Markers, representing the
    // interfaces, enums, and typeAliases that need to be generated.
    val result = openApi.components?.schemas
        ?.toMap()
        ?.toMarkers(topInterfaceName)
        ?.toList()
        ?: emptyList()

    // generate the code for the markers in result
    val codeGenerator = CodeGenerator.create(useFqNames = true)

    fun toCode(marker: OpenApiMarker): Code =
        codeGenerator.generate(
            marker = marker
                .withVisibility(visibility)
                .withName(
                    name = marker.name.withoutTopInterfaceName(topInterfaceName),
                    prependTopInterfaceName = false,
                ),
            interfaceMode = when (marker) {
                is OpenApiMarker.Enum -> InterfaceGenerationMode.Enum
                is OpenApiMarker.Interface -> InterfaceGenerationMode.WithFields
                is OpenApiMarker.TypeAlias, is OpenApiMarker.MarkerAlias -> InterfaceGenerationMode.TypeAlias
            },
            extensionProperties = false,
            readDfMethod = if (marker is OpenApiMarker.Interface) DefaultReadOpenApiMethod else null,
        ).declarations

    fun Code.merge(other: Code): Code = "$this\n$other"

    fun toExtensionProperties(marker: OpenApiMarker): Code =
        if (marker !is OpenApiMarker.Interface) ""
        else codeGenerator.generate(
            marker = marker.withVisibility(visibility),
            interfaceMode = InterfaceGenerationMode.None,
            extensionProperties = true,
            readDfMethod = null,
        ).declarations

    val (typeAliases, markers) = result
        .partition { it is OpenApiMarker.TypeAlias || it is OpenApiMarker.MarkerAlias }

    val generatedMarkers = markers
        .map(::toCode)
        .reduceOrNull(Code::merge)
        ?: ""

    val generatedTypeAliases = typeAliases
        .map(::toCode)
        .reduceOrNull(Code::merge)
        ?: ""

    val generatedExtensionProperties =
        if (!extensionProperties) ""
        else result
            .map(::toExtensionProperties)
            .reduceOrNull(Code::merge)
            ?: ""

    val helperCompanionObject =
        if (!generateHelperCompanionObject) ""
        else {
            val accessors = markers
                .filterIsInstance<OpenApiMarker.Interface>()
                .joinToString("\n|        ") {
                    "val ${it.name.withoutTopInterfaceName(topInterfaceName)} = ${it.name}.Companion"
                }

            """
            |    companion object {
            |        $accessors
            |    }
            """
        }

    return """
             |interface ${topInterfaceName.quotedIfNeeded} {
             |    $helperCompanionObject
             |    ${generatedMarkers.replace("\n", "\n|    ")}
             |}
             |${generatedTypeAliases.replace("\n", "\n|")}
             |${generatedExtensionProperties.replace("\n", "\n|")}
         """.trimMargin()
}

/**
 * Converts named OpenApi schemas to a list of [OpenApiMarker]s.
 * Will cause an exception for circular references, however they shouldn't occur in OpenApi specs.
 *
 * Some explanation:
 * OpenApi provides schemas for all the types used. For each type, we want to generate a [Marker]
 * (Which can be an interface, enum or typealias). However, the OpenApi schema is not ordered per se,
 * so when we are reading the schema it might be that we have a reference to a (super)type
 * (which are queried using `getRefMarker`) for which we have not yet created a [Marker].
 * In that case, we "pause" that one (by returning `CannotFindRefMarker`) and try to read another type schema first.
 * Circular references cannot exist since it's encoded in JSON, so we never get stuck in an infinite loop.
 * When all markers are "retrieved" (so turned from a [RetrievableMarker] to a [MarkerResult.OpenApiMarker]),
 * we're done and have converted everything!
 * As for `produceAdditionalMarker`: In OpenAPI not all enums/objects have to be defined as a separate schema.
 * Although recommended, you can still define an object anonymously directly as a type. For this, we have
 * `produceAdditionalMarker` since during the conversion of a schema -> [Marker] we get an additional new [Marker].
 */
private fun Map<String, Schema<*>>.toMarkers(topInterfaceName: ValidFieldName): List<OpenApiMarker> {
    // Convert the schemas to toMarker calls that can be repeated to resolve references.
    val retrievableMarkers = mapValues { (typeName, value) ->
        RetrievableMarker { getRefMarker, produceAdditionalMarker ->
            value.toMarker(
                typeName = typeName,
                getRefMarker = getRefMarker,
                produceAdditionalMarker = produceAdditionalMarker,
                topInterfaceName = topInterfaceName,
            )
        }
    }.toMutableMap()

    // Retrieved Markers will be collected here
    val markers = mutableMapOf<String, OpenApiMarker>()

    // Function to get a marker from [markers] by name, see explanation above.
    val getRefMarker = GetRefMarker { MarkerResult.fromNullable(markers[it]) }

    // convert all the retrievable markers to actual markers, resolving references as we go and if possible
    while (retrievableMarkers.isNotEmpty()) try {
        retrievableMarkers.entries.first { (name, retrieveMarker) ->
            // To avoid producing additional markers twice due to a CannotFindRefMarker, save them here first
            val additionalMarkers = mutableMapOf<String, OpenApiMarker>()

            // Function to produce additional markers during conversion, see explanation above.
            val produceAdditionalMarker = ProduceAdditionalMarker { validName, marker, _ ->
                var result = ValidFieldName.of(validName.unquoted)
                val baseName = result
                var attempt = 1
                while (result.quotedIfNeeded in markers || result.quotedIfNeeded in additionalMarkers) {
                    result = ValidFieldName.of(
                        baseName.unquoted + (if (result.needsQuote) " ($attempt)" else "$attempt")
                    )
                    attempt++
                }

                additionalMarkers[result.quotedIfNeeded] = marker.withName(result.quotedIfNeeded)
                result.quotedIfNeeded
            }

            val res = retrieveMarker(
                getRefMarker = getRefMarker,
                produceAdditionalMarker = produceAdditionalMarker,
            )

            when (res) {
                is MarkerResult.OpenApiMarker -> {
                    markers[name] = res.marker
                    markers += additionalMarkers
                    retrievableMarkers -= name
                    true // Marker is retrieved completely, remove it from the map
                }

                is MarkerResult.CannotFindRefMarker ->
                    false // Cannot find a referenced Marker for this one, so we'll try again later
            }
        }
    } catch (e: NoSuchElementException) {
        throw IllegalStateException(
            "Exception while converting OpenApi schemas to markers. ${retrievableMarkers.keys.toList()} cannot find a ref marker.",
            e,
        )
    }

    return markers.values.toList()
}

/**
 * Converts a single OpenApi object type schema to an [OpenApiMarker] if successful.
 *
 * Can handle the following cases:
 * - `allOf:` combining multiple objects into one with inheritance.
 * - `enum:` creating an enum of any type.
 * - `type: object`
 *     - `properties:` (`additionalProperties` are ignored) creating an [OpenApiMarker.Interface] using the fields in the properties.
 *     - `additionalProperties:` (if `properties` is not present) creating an [OpenApiMarker.AdditionalPropertiesInterface] using the additionalProperties schema as type of `value`.
 * - `type:` if type is something else, generating a type alias for it. This can be a [OpenApiMarker.TypeAlias] or a [OpenApiMarker.MarkerAlias].
 *
 * @param typeName The name of the schema / type to convert.
 * @param getRefMarker Function to retrieve a [Marker] for a given reference name.
 * @param produceAdditionalMarker Function to produce an additional [Marker] on the fly, such as for
 *   inline enums/classes in arrays.
 * @param required Optional list of required properties for this schema.
 *
 * @return A [MarkerResult.OpenApiMarker] if successful, otherwise [MarkerResult.CannotFindRefMarker].
 */
private fun Schema<*>.toMarker(
    typeName: String,
    getRefMarker: GetRefMarker,
    produceAdditionalMarker: ProduceAdditionalMarker,
    topInterfaceName: ValidFieldName,
    required: List<String> = emptyList(),
): MarkerResult {
    @Suppress("NAME_SHADOWING")
    val required = (this.required ?: emptyList()) + required
    val nullable = nullable ?: false
    return when {
        // If allOf is defined, multiple objects are to be composed together. This is done using inheritance.
        // https://swagger.io/docs/specification/data-models/oneof-anyof-allof-not/#allof
        allOf != null -> {
            val allOfSchemas = allOf!!.associateWith {
                it.toOpenApiType(getRefMarker = getRefMarker)
            }

            // An un-required super field might be required from a child schema.
            val requiredFields =
                (allOfSchemas.keys.flatMap { it.required ?: emptyList() } + required).distinct()

            // combine all schemas into a single schema by combining their supertypes and fields
            val superMarkers = mutableListOf<Marker>()
            val fields = mutableListOf<GeneratedField>()
            val additionalPropertyPaths = mutableListOf<JsonPath>()
            for ((schema, openApiTypeResult) in allOfSchemas)
                when (openApiTypeResult) {
                    is OpenApiTypeResult.CannotFindRefMarker ->
                        return MarkerResult.CannotFindRefMarker

                    is OpenApiTypeResult.UsingRef -> {
                        val superMarker = openApiTypeResult.marker
                        superMarkers += superMarker

                        additionalPropertyPaths += superMarker.additionalPropertyPaths

                        // make sure required fields are overridden to be non-null
                        val allSuperFields =
                            (superMarker.fields + superMarker.allSuperMarkers.values.flatMap { it.fields })
                                .distinctBy { it.fieldName.unquoted }

                        fields += allSuperFields
                            .filter {
                                it.fieldName.unquoted in requiredFields && it.fieldType.isNullable()
                            }.map {
                                generatedFieldOf(
                                    fieldName = it.fieldName,
                                    columnName = it.columnName,
                                    fieldType = it.fieldType.toNotNullable(),
                                    overrides = true,
                                )
                            }
                    }

                    is OpenApiTypeResult.Enum -> error("allOf cannot contain enum types")

                    is OpenApiTypeResult.OpenApiType -> {
                        val (openApiType, nullable) = openApiTypeResult

                        // must be an object
                        openApiType as OpenApiType.Object

                        // create a temp marker so its fields can be merged in the allOf
                        var tempMarker: OpenApiMarker? = null

                        val fieldTypeResult = openApiType.toFieldType(
                            schema = schema,
                            schemaName = typeName,
                            nullable = nullable,
                            getRefMarker = getRefMarker,
                            produceAdditionalMarker = { name, marker, isTopLevelObject ->
                                // the top-level object must not be produced as additional marker.
                                // instead, we just need it to be the tempMarker for which we gather just the fields.
                                if (isTopLevelObject) {
                                    tempMarker = marker
                                    name.quotedIfNeeded
                                } else {
                                    produceAdditionalMarker(name, marker, false)
                                }
                            },
                            required = required,
                            topInterfaceName = topInterfaceName,
                        )

                        when (fieldTypeResult) {
                            is FieldTypeResult.CannotFindRefMarker -> {
                                return MarkerResult.CannotFindRefMarker
                            }

                            // extract the fields from tempMarker
                            is FieldTypeResult.FieldType -> {
                                fields += tempMarker!!.fields
                                additionalPropertyPaths += fieldTypeResult.additionalPropertyPaths
                            }
                        }
                    }
                }

            MarkerResult.OpenApiMarker(
                OpenApiMarker.Interface(
                    nullable = nullable,
                    name = typeName,
                    fields = fields,
                    superMarkers = superMarkers,
                    additionalPropertyPaths = additionalPropertyPaths,
                    topInterfaceName = topInterfaceName,
                )
            )
        }

        // If enum is defined, create an enum class.
        // https://swagger.io/docs/specification/data-models/enums/
        enum != null -> {
            val openApiTypeResult = toOpenApiType(
                getRefMarker = getRefMarker,
            ) as OpenApiTypeResult.Enum // must be an enum

            val enumMarker = produceNewEnum(
                name = typeName,
                topInterfaceName = topInterfaceName,
                values = openApiTypeResult.values,
                nullable = openApiTypeResult.nullable,
                produceAdditionalMarker = ProduceAdditionalMarker.NOOP, // we need it here, not as additional marker
            )

            MarkerResult.OpenApiMarker(enumMarker)
        }

        // If type == object, create a new Marker to become an interface.
        // https://swagger.io/docs/specification/data-models/data-types/#object
        type == "object" -> when {
            // Gather the given properties as fields
            properties != null -> {
                if (additionalProperties != null && additionalProperties != false) {
                    println("OpenAPI warning: type $name has both properties and additionalProperties defined, but only properties will be generated in the data schema.")
                }

                val keyValuePaths = mutableListOf<JsonPath>()

                // build a list of fields from properties
                val fields = buildList {
                    for ((name, property) in (properties ?: emptyMap())) {
                        val isRequired = name in required

                        // find the OpenApiType of the property (or ref or enum)
                        val openApiTypeResult = property.toOpenApiType(
                            getRefMarker = getRefMarker,
                        )

                        when (openApiTypeResult) {
                            is OpenApiTypeResult.CannotFindRefMarker ->
                                return MarkerResult.CannotFindRefMarker

                            is OpenApiTypeResult.UsingRef -> {
                                keyValuePaths += openApiTypeResult.marker
                                    .additionalPropertyPaths
                                    .map { it.prepend(name) }

                                val validName = ValidFieldName.of(name.snakeToLowerCamelCase())

                                // find the field type of the marker reference
                                val fieldType = openApiTypeResult.marker.toFieldType()
                                    .let { if (!isRequired) it.toNullable() else it }

                                this += generatedFieldOf(
                                    overrides = false,
                                    fieldName = validName,
                                    columnName = name,
                                    fieldType = fieldType,
                                )
                            }

                            is OpenApiTypeResult.Enum -> {
                                // inner enum, so produce it as additional
                                val enumMarker = produceNewEnum(
                                    name = name,
                                    topInterfaceName = topInterfaceName,
                                    values = openApiTypeResult.values,
                                    produceAdditionalMarker = produceAdditionalMarker,
                                    nullable = openApiTypeResult.nullable,
                                )

                                this += generatedFieldOf(
                                    overrides = false,
                                    fieldName = ValidFieldName.of(name.snakeToLowerCamelCase()),
                                    columnName = name,
                                    fieldType = FieldType.ValueFieldType(
                                        typeFqName = enumMarker.name +
                                            if (enumMarker.nullable || !isRequired) "?" else "",
                                    ),
                                )
                            }

                            is OpenApiTypeResult.OpenApiType -> {
                                val (openApiType, nullable) = openApiTypeResult

                                val fieldTypeResult = openApiType.toFieldType(
                                    schema = property,
                                    schemaName = name,
                                    nullable = nullable,
                                    getRefMarker = getRefMarker,
                                    produceAdditionalMarker = produceAdditionalMarker,
                                    required = required,
                                    topInterfaceName = topInterfaceName,
                                )

                                when (fieldTypeResult) {
                                    is FieldTypeResult.CannotFindRefMarker ->
                                        return MarkerResult.CannotFindRefMarker

                                    is FieldTypeResult.FieldType -> {
                                        val validName = ValidFieldName.of(name.snakeToLowerCamelCase())

                                        keyValuePaths += fieldTypeResult
                                            .additionalPropertyPaths
                                            .map { it.prepend(name) }

                                        this += generatedFieldOf(
                                            overrides = false,
                                            fieldName = validName,
                                            columnName = name,
                                            fieldType = fieldTypeResult.fieldType.let {
                                                if (!isRequired) it.toNullable() else it
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                MarkerResult.OpenApiMarker(
                    OpenApiMarker.Interface(
                        nullable = nullable,
                        name = typeName,
                        fields = fields,
                        superMarkers = emptyList(),
                        additionalPropertyPaths = keyValuePaths,
                        topInterfaceName = topInterfaceName,
                    )
                )
            }

            // Create this object as a map-like type
            properties == null && additionalProperties != null && additionalProperties != false -> {
                val openApiTypeResult = (additionalProperties as? Schema<*>)
                    ?.toOpenApiType(getRefMarker = getRefMarker)

                val additionalPropertyPaths = mutableListOf<JsonPath>()
                val valueType = when (openApiTypeResult) {
                    is OpenApiTypeResult.CannotFindRefMarker ->
                        return MarkerResult.CannotFindRefMarker

                    is OpenApiTypeResult.UsingRef -> {
                        val marker = openApiTypeResult.marker
                        additionalPropertyPaths += marker.additionalPropertyPaths.map {
                            it.prependWildcard()
                        }
                        marker.toFieldType()
                    }

                    is OpenApiTypeResult.OpenApiType -> {
                        val fieldTypeResult = openApiTypeResult
                            .openApiType
                            .toFieldType(
                                schema = this,
                                schemaName = typeName,
                                nullable = openApiTypeResult.nullable,
                                getRefMarker = getRefMarker,
                                produceAdditionalMarker = produceAdditionalMarker,
                                required = required,
                                topInterfaceName = topInterfaceName,
                            )

                        when (fieldTypeResult) {
                            FieldTypeResult.CannotFindRefMarker ->
                                return MarkerResult.CannotFindRefMarker

                            is FieldTypeResult.FieldType -> {
                                additionalPropertyPaths += fieldTypeResult.additionalPropertyPaths.map {
                                    it.prependWildcard()
                                }

                                fieldTypeResult.fieldType
                            }
                        }
                    }

                    is OpenApiTypeResult.Enum -> {
                        // inner enum, so produce it as additional
                        val enumMarker = produceNewEnum(
                            name = name,
                            topInterfaceName = topInterfaceName,
                            values = openApiTypeResult.values,
                            produceAdditionalMarker = produceAdditionalMarker,
                            nullable = openApiTypeResult.nullable,
                        )

                        FieldType.ValueFieldType(
                            typeFqName = enumMarker.name + if (enumMarker.nullable) "?" else "",
                        )
                    }

                    null -> FieldType.ValueFieldType(
                        typeFqName = typeOf<Any?>().toString(),
                    )
                }

                MarkerResult.OpenApiMarker(
                    OpenApiMarker.AdditionalPropertyInterface(
                        nullable = nullable,
                        valueType = valueType,
                        name = ValidFieldName.of(typeName).quotedIfNeeded,
                        additionalPropertyPaths = additionalPropertyPaths,
                        topInterfaceName = topInterfaceName,
                    )
                )
            }

            else -> MarkerResult.OpenApiMarker(
                OpenApiMarker.Interface(
                    nullable = nullable,
                    name = typeName,
                    fields = emptyList(),
                    superMarkers = emptyList(),
                    additionalPropertyPaths = emptyList(),
                    topInterfaceName = topInterfaceName,
                )
            )
        }

        // If type is something else, produce it as type alias. Can be a reference to another OpenApi type or something else.
        else -> {
            val openApiTypeResult = toOpenApiType(
                getRefMarker = getRefMarker,
            )

            val typeAliasMarker = when (openApiTypeResult) {
                is OpenApiTypeResult.CannotFindRefMarker ->
                    return MarkerResult.CannotFindRefMarker

                is OpenApiTypeResult.UsingRef -> OpenApiMarker.MarkerAlias(
                    name = ValidFieldName.of(typeName).quotedIfNeeded,
                    superMarker = openApiTypeResult.marker,
                    topInterfaceName = topInterfaceName,
                    nullable = nullable,
                )

                is OpenApiTypeResult.OpenApiType -> {
                    val typeResult = openApiTypeResult
                        .openApiType
                        .toFieldType(
                            schema = this,
                            schemaName = typeName,
                            nullable = false,
                            getRefMarker = getRefMarker,
                            produceAdditionalMarker = produceAdditionalMarker,
                            required = required,
                            topInterfaceName = topInterfaceName,
                        )

                    val superMarkerName = when (typeResult) {
                        is FieldTypeResult.CannotFindRefMarker ->
                            return MarkerResult.CannotFindRefMarker

                        is FieldTypeResult.FieldType ->
                            when (typeResult.fieldType) {
                                is FieldType.ValueFieldType, is FieldType.GroupFieldType ->
                                    typeResult.fieldType.name

                                is FieldType.FrameFieldType ->
                                    "${DataFrame::class.qualifiedName!!}<${typeResult.fieldType.name}>"
                            }
                    }

                    OpenApiMarker.TypeAlias(
                        nullable = nullable,
                        name = ValidFieldName.of(typeName).quotedIfNeeded,
                        superMarkerName = superMarkerName,
                        additionalPropertyPaths = typeResult.additionalPropertyPaths,
                        topInterfaceName = topInterfaceName,
                    )
                }

                is OpenApiTypeResult.Enum -> error("cannot happen, since enum != null is checked earlier")
            }

            MarkerResult.OpenApiMarker(typeAliasMarker)
        }
    }
}

/**
 * Converts a single property of an OpenApi type schema to [OpenApiTypeResult] representing a single type for DataFrame.
 * It must either have `$ref`, `type`, `enum`, `oneOf`, `anyOf`, or `not` defined.
 * It can become an [OpenApiType], [OpenApiMarker] reference or unresolved reference (if `$ref:` is set), enum (if `enum:` is set).
 * `anyOf` and `oneOf` types are merged.
 *
 * These results still have to be converted to [FieldType]s to be able to generate [OpenApiMarker]s from it
 * (unless it's a [OpenApiTypeResult.UsingRef] of course).
 *
 * @receiver Single property of an OpenApi type schema to convert.
 * @param getRefMarker function to attempt to resolve a reference.
 * @return [OpenApiTypeResult]
 */
private fun Schema<*>.toOpenApiType(
    getRefMarker: GetRefMarker,
): OpenApiTypeResult {
    val nullable = nullable ?: false

    // if it's a reference, resolve it or try again later
    if (`$ref` != null) {
        val typeName = `$ref`.takeLastWhile { it != '/' }
        return when (val it = getRefMarker(typeName)) {
            is MarkerResult.CannotFindRefMarker ->
                OpenApiTypeResult.CannotFindRefMarker

            is MarkerResult.OpenApiMarker ->
                OpenApiTypeResult.UsingRef(it.marker)
        }
    }

    // if it's an enum, return the enum
    if (enum != null) {
        // nullability of an enum is given only by the enum itself
        // https://github.com/OAI/OpenAPI-Specification/blob/main/proposals/2019-10-31-Clarify-Nullable.md#if-a-schema-specifies-nullable-true-and-enum-1-2-3-does-that-schema-allow-null-values-see-1900
        @Suppress("NAME_SHADOWING")
        val nullable = enum.any { it == null }

        return OpenApiTypeResult.Enum(
            values = enum.filterNotNull().map { it.toString() },
            nullable = nullable, // enum can still become null in Kotlin if not required
        )
    }

    var openApiType = OpenApiType.fromStringOrNull(type)

    // check for anyOf/oneOf/not, https://swagger.io/docs/specification/data-models/oneof-anyof-allof-not/
    if (openApiType == null || openApiType is OpenApiType.Any) {
        val anyOf = ((anyOf ?: emptyList()) + (oneOf ?: emptyList()))

        // gather all references if there are any, try again later if unresolved
        val anyOfRefs = anyOf.mapNotNull { it.`$ref` }.map { ref ->
            val typeName = ref.takeLastWhile { it != '/' }
            when (val it = getRefMarker(typeName)) {
                is MarkerResult.CannotFindRefMarker ->
                    return OpenApiTypeResult.CannotFindRefMarker

                is MarkerResult.OpenApiMarker -> it.marker
            }
        }

        val anyOfTypes = anyOf.mapNotNull { it.type }
            .mapNotNull(OpenApiType.Companion::fromStringOrNull)
            .distinct()

        val allTypes = anyOfTypes + anyOfRefs

        openApiType = when {
            // only one type
            anyOfTypes.size == 1 && anyOfRefs.isEmpty() -> anyOfTypes.first()

            // just Number-like types
            anyOfTypes.size == 2 && anyOfRefs.isEmpty() && anyOfTypes.containsAll(
                listOf(OpenApiType.Number, OpenApiType.Integer)
            ) -> OpenApiType.Number

            !anyOfTypes.any { it.isObject } && anyOfRefs.isEmpty() -> OpenApiType.Any

            // only one ref
            anyOfTypes.isEmpty() && anyOfRefs.size == 1 ->
                return OpenApiTypeResult.UsingRef(anyOfRefs.first())

            // only refs
            anyOfTypes.isEmpty() && anyOfRefs.isNotEmpty() -> {
                val commonSuperMarker = anyOfRefs.map { it.allSuperMarkers.values.toSet() }
                    .reduce(Set<Marker>::intersect)
                    .firstOrNull() as? OpenApiMarker?

                if (commonSuperMarker != null) {
                    return OpenApiTypeResult.UsingRef(commonSuperMarker)
                } else {
                    OpenApiType.AnyObject
                }
            }

            // more than one ref or types
            allTypes.isNotEmpty() && allTypes.all { it.isObject } -> OpenApiType.AnyObject

            // cannot assume anything about a type when there are multiple types except one
            not != null -> OpenApiType.Any

            else -> OpenApiType.Any
        }
    }

    return OpenApiTypeResult.OpenApiType(openApiType, nullable)
}

/**
 * Converts an [OpenApiType] with [schema] to a [FieldType] if successful.
 *
 * @receiver OpenApiType to convert.
 * @param schema Schema of the property that the [OpenApiType] belongs to.
 *   Used to get extra information if needed (for arrays / objects / format etc.).
 * @param schemaName Name of the schema that the property belongs to. Used in the name generation of the
 *   additionally produced [Marker]s.
 * @param nullable Whether the [FieldType] is supposed to be nullable.
 * @param getRefMarker Function to attempt to resolve a reference.
 * @param produceAdditionalMarker Function to produce additional [Marker]s if needed.
 * @param required List of required properties. Passed down into child objects.
 * @return [FieldTypeResult]
 */
private fun OpenApiType.toFieldType(
    schema: Schema<*>,
    schemaName: String,
    nullable: Boolean,
    getRefMarker: GetRefMarker,
    produceAdditionalMarker: ProduceAdditionalMarker,
    required: List<String>,
    topInterfaceName: ValidFieldName,
): FieldTypeResult {
    return when (this) {
        is OpenApiType.Any -> FieldTypeResult.FieldType(getType(nullable))

        is OpenApiType.Boolean -> FieldTypeResult.FieldType(getType(nullable))

        is OpenApiType.Integer -> FieldTypeResult.FieldType(
            getType(
                nullable = nullable,
                format = OpenApiIntegerFormat.fromStringOrNull(schema.format),
            )
        )

        is OpenApiType.Number -> FieldTypeResult.FieldType(
            getType(
                nullable = nullable,
                format = OpenApiNumberFormat.fromStringOrNull(schema.format),
            )
        )

        is OpenApiType.String -> FieldTypeResult.FieldType(
            getType(
                nullable = nullable,
                format = OpenApiStringFormat.fromStringOrNull(schema.format),
            )
        )

        // Becomes a DataRow<Any> or DataRow<Any?> since we don't know the type, but we do know it's an object
        is OpenApiType.AnyObject -> FieldTypeResult.FieldType(
            getType(
                nullable = nullable,
            )
        )

        is OpenApiType.Array -> {
            schema as ArraySchema

            if (schema.items == null) {
                // should in theory not occur, but make List<Any?> just in case
                FieldTypeResult.FieldType(
                    getTypeAsList(
                        nullableArray = nullable,
                        typeFqName = OpenApiType.Any.getType(nullable = true).typeFqName
                    )
                )
            } else {
                // resolve the type of the contents of the array
                val arrayTypeResult = schema
                    .items!!
                    .toOpenApiType(getRefMarker = getRefMarker)

                // convert the type to a FieldType
                when (arrayTypeResult) {
                    is OpenApiTypeResult.CannotFindRefMarker ->
                        FieldTypeResult.CannotFindRefMarker

                    is OpenApiTypeResult.UsingRef ->
                        when {
                            // accessed like List<DataFrame<MyMarker>>
                            arrayTypeResult.marker is OpenApiMarker.AdditionalPropertyInterface ->
                                FieldTypeResult.FieldType(
                                    fieldType = getTypeAsFrameList(
                                        nullable = arrayTypeResult.marker.nullable,
                                        nullableArray = nullable,
                                        markerName = arrayTypeResult.marker.name,
                                    ),
                                    additionalPropertyPaths = arrayTypeResult.marker.additionalPropertyPaths.map {
                                        it.prependArrayWithWildcard()
                                    },
                                )

                            // accessed like DataFrame<MyMarker>
                            arrayTypeResult.marker.isObject ->
                                FieldTypeResult.FieldType(
                                    fieldType = getTypeAsFrame(
                                        nullable = nullable || arrayTypeResult.marker.nullable,
                                        markerName = arrayTypeResult.marker.name,
                                    ),
                                    additionalPropertyPaths = arrayTypeResult.marker.additionalPropertyPaths.map {
                                        it.prependArrayWithWildcard()
                                    },
                                )

                            // accessed like List<Primitive>
                            else ->
                                FieldTypeResult.FieldType(
                                    fieldType = getTypeAsList(
                                        nullableArray = nullable || arrayTypeResult.marker.nullable,
                                        typeFqName = arrayTypeResult.marker.name,
                                    ),
                                    additionalPropertyPaths = arrayTypeResult.marker.additionalPropertyPaths.map {
                                        it.prependArrayWithWildcard()
                                    },
                                )
                        }

                    is OpenApiTypeResult.OpenApiType -> {
                        // Convert openApiType of array contents to FieldType.
                        // Will produce additional markers if needed.
                        val arrayTypeSchemaResult = arrayTypeResult
                            .openApiType
                            .toFieldType(
                                schema = schema.items!!,
                                schemaName = schemaName + "Content", // type name objects in the array will get
                                nullable = arrayTypeResult.nullable,
                                getRefMarker = getRefMarker,
                                produceAdditionalMarker = produceAdditionalMarker,
                                required = emptyList(),
                                topInterfaceName = topInterfaceName,
                            )

                        when (arrayTypeSchemaResult) {
                            is FieldTypeResult.CannotFindRefMarker ->
                                FieldTypeResult.CannotFindRefMarker

                            is FieldTypeResult.FieldType -> {
                                val fieldType = arrayTypeSchemaResult.fieldType
                                val additionalPropertyPaths = arrayTypeSchemaResult
                                    .additionalPropertyPaths
                                    .map { it.prependArrayWithWildcard() }

                                FieldTypeResult.FieldType(
                                    fieldType = when {
                                        // array of OpenApiType.AnyObject -> DataFrame<Any>
                                        fieldType is FieldType.GroupFieldType &&
                                            fieldType.name == typeOf<DataRow<Any>>().toString() ->
                                            getTypeAsFrame(
                                                nullable = nullable,
                                                markerName = typeOf<Any>().toString(),
                                            )

                                        // array of OpenApiType.AnyObject -> DataFrame<Any?>
                                        fieldType is FieldType.GroupFieldType &&
                                            fieldType.name == typeOf<DataRow<Any?>>().toString() ->
                                            getTypeAsFrame(
                                                nullable = nullable,
                                                markerName = typeOf<Any?>().toString(),
                                            )

                                        // array of Marker -> DataFrame<Marker>
                                        fieldType is FieldType.GroupFieldType ->
                                            getTypeAsFrame(
                                                nullable = nullable,
                                                markerName = fieldType.name,
                                            )

                                        // array of DataFrames -> List<DataFrame<Marker>>
                                        fieldType is FieldType.FrameFieldType ->
                                            getTypeAsList(
                                                nullableArray = nullable,
                                                typeFqName = "${DataFrame::class.qualifiedName}<${fieldType.name}>",
                                            )

                                        // array of primitives -> List<T>
                                        fieldType is FieldType.ValueFieldType ->
                                            getTypeAsList(
                                                nullableArray = nullable,
                                                typeFqName = fieldType.name,
                                            )

                                        else -> error("Error reading array type")
                                    },
                                    additionalPropertyPaths = additionalPropertyPaths,
                                )
                            }
                        }
                    }

                    is OpenApiTypeResult.Enum -> {
                        // enum needs to be produced as additional marker
                        val enumMarker = produceNewEnum(
                            name = schemaName,
                            topInterfaceName = topInterfaceName,
                            values = arrayTypeResult.values,
                            produceAdditionalMarker = produceAdditionalMarker,
                            nullable = arrayTypeResult.nullable,
                        )

                        FieldTypeResult.FieldType(
                            getTypeAsList(
                                nullableArray = nullable,
                                typeFqName = enumMarker.name + if (enumMarker.nullable) "?" else "",
                            )
                        )
                    }
                }
            }
        }

        is OpenApiType.Object -> {
            // read the schema to an OpenApiMarker
            val dataFrameSchemaResult = schema.toMarker(
                typeName = schemaName.snakeToUpperCamelCase(),
                getRefMarker = getRefMarker,
                produceAdditionalMarker = { validName, marker, _ ->
                    // ensure isTopLevelObject == false, since we go a layer deeper
                    produceAdditionalMarker(validName, marker, isTopLevelObject = false)
                },
                required = required,
                topInterfaceName = topInterfaceName,
            )

            when (dataFrameSchemaResult) {
                is MarkerResult.CannotFindRefMarker ->
                    FieldTypeResult.CannotFindRefMarker

                is MarkerResult.OpenApiMarker -> {
                    // Produce the marker as additional marker
                    val newName = produceAdditionalMarker(
                        validName = ValidFieldName.of(schemaName.snakeToUpperCamelCase()),
                        marker = dataFrameSchemaResult.marker,
                        isTopLevelObject = true, // only relevant in `allOf` cases
                    )

                    when (val marker = dataFrameSchemaResult.marker.withName(newName)) {
                        // needs to be accessed like DataFrame<MyMarker>
                        is OpenApiMarker.AdditionalPropertyInterface ->
                            FieldTypeResult.FieldType(
                                fieldType = OpenApiType.Array.getTypeAsFrame(
                                    nullable = nullable,
                                    markerName = marker.name,
                                ),
                                additionalPropertyPaths = marker.additionalPropertyPaths,
                            )

                        // accessed like Marker (or DataRow<Marker>)
                        else -> FieldTypeResult.FieldType(
                            fieldType = getType(
                                nullable = nullable,
                                marker = marker,
                            ),
                            additionalPropertyPaths = marker.additionalPropertyPaths,
                        )
                    }
                }
            }
        }
    }
}

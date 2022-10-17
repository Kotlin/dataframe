package org.jetbrains.kotlinx.dataframe.io // ktlint-disable filename

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.core.models.AuthorizationValue
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.intellij.lang.annotations.Language
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toMap
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.GeneratedField
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.codeGen.isNullable
import org.jetbrains.kotlinx.dataframe.codeGen.name
import org.jetbrains.kotlinx.dataframe.codeGen.plus
import org.jetbrains.kotlinx.dataframe.codeGen.toNotNullable
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Any.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Array.getTypeAsFrame
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Array.getTypeAsList
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Boolean.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Integer.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Number.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Object.getType
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.String.getType
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

public class OpenApi : SupportedCodeGenerationFormat {

    public fun readCodeForGeneration(text: String, extensionProperties: Boolean = false): CodeWithConverter =
        readOpenApiAsString(text, extensionProperties = extensionProperties)

    override fun readCodeForGeneration(stream: InputStream): CodeWithConverter =
        readOpenApiAsString(stream.bufferedReader().readText(), extensionProperties = false)

    public fun readCodeForGeneration(stream: InputStream, extensionProperties: Boolean): CodeWithConverter =
        readOpenApiAsString(stream.bufferedReader().readText(), extensionProperties = extensionProperties)

    override fun readCodeForGeneration(file: File): CodeWithConverter =
        readOpenApiAsString(file.readText(), extensionProperties = false)

    public fun readCodeForGeneration(file: File, extensionProperties: Boolean): CodeWithConverter =
        readOpenApiAsString(file.readText(), extensionProperties = extensionProperties)

    override fun acceptsExtension(ext: String): Boolean = ext in listOf("yaml", "yml", "json")

    override val testOrder: Int = 60000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod = DefaultReadOpenApiMethod
}

/**
 * Attempt to undo the creation of `value` and `array` columns by the json -> DF decoder.
 */
private fun DataRow<*>.unwrapJsonColumn(): Any? {
    // for clashes like array and array1, take the highest number (this may break, but not much I can do about it)
    val valueName = columnNames().filter { it.contains(valueColumnName) }.maxOfOrNull { it }
    val arrayName = columnNames().filter { it.contains(arrayColumnName) }.maxOfOrNull { it }

    return when {
        getVisibleValues().isEmpty() -> null

        // Can't unwrap anything
        valueName == null && arrayName == null -> this

        else -> valueName?.let(::getOrNull) ?: arrayName?.let(::getOrNull)
    }
}

/**
 * Attempt to undo the creation of `value` and `array` columns by the json -> DF decoder for Maps.
 */
@Suppress("UNCHECKED_CAST")
private fun Map<String, *>.unwrapJsonColumn(): Map<String, Any?> =
    mapValues { (_, value) ->
        when {
            value is DataRow<*>? -> value?.unwrapJsonColumn()

            value is Map<*, *> && value.keys.all { it is String } ->
                (value as Map<String, *>).unwrapJsonColumn()

            else -> value
        }
    }

/**
 * Function to be used in [ConvertSchemaDsl] ([AnyFrame.convertTo]) to help convert a DataFrame to adhere to an
 * OpenApi schema.
 */
@Suppress("RemoveExplicitTypeArguments")
public fun ConvertSchemaDsl<*>.convertDataRowsWithOpenApi() {
    // undo Json wrapping of values that should be Any?
    convert<DataRow<*>>().with<_, Any?>(DataRow<*>::unwrapJsonColumn)

    // undo Json wrapping of maps with values that should be Any?
    convert(
        from = { it == typeOf<DataRow<*>>() },
        to = { // any type of Map<String, Any? / *> or Map<String, Any? / *>?
            (it.isSubtypeOf(typeOf<Map<*, *>>()) || it.isSubtypeOf(typeOf<Map<*, *>?>())) &&
                it.arguments.getOrNull(0)?.type == typeOf<String>() &&
                it.arguments.getOrNull(1)?.type.let { it == typeOf<Any?>() || it == null }
        }
    ) { (it as DataRow<*>).toMap().unwrapJsonColumn() }

    // convert DataRows to Maps if required by the schema
    convert(
        from = { it == typeOf<DataRow<*>>() },
        to = { // any type of Map<String, _> or Map<String, _>?
            (it.isSubtypeOf(typeOf<Map<*, *>>()) || it.isSubtypeOf(typeOf<Map<*, *>?>())) &&
                it.arguments.getOrNull(0)?.type == typeOf<String>()
        }
    ) { (it as DataRow<*>).toMap() }

    // convert DataFrame to DataFrame<Any?> if required by the schema
    convert(
        from = { it == typeOf<DataFrame<*>>() },
        to = { it.isSubtypeOf(typeOf<DataFrame<Any>>()) || it.isSubtypeOf(typeOf<DataFrame<Any?>>()) }
    ) {
        (it as DataFrame<*>).map {
            it.unwrapJsonColumn()
        }.toDataFrame()
    }
}

/** Used to add readJson functions to the generated interfaces. */
private object DefaultReadOpenApiMethod : AbstractDefaultReadMethod(
    path = null,
    arguments = MethodArguments.EMPTY,
    methodName = "",
) {

    override val additionalImports: List<String> = listOf(
        "import org.jetbrains.kotlinx.dataframe.io.readJson",
        "import org.jetbrains.kotlinx.dataframe.io.readJsonStr",
        "import org.jetbrains.kotlinx.dataframe.api.convertTo",
        "import org.jetbrains.kotlinx.dataframe.api.${DataSchemaEnum::class.simpleName}",
        "import org.jetbrains.kotlinx.dataframe.io.${ConvertSchemaDsl<*>::convertDataRowsWithOpenApi.name}",
    )

    override fun toDeclaration(markerName: String, visibility: String): String {
        val returnType = DataFrame::class.asClassName().parameterizedBy(ClassName("", listOf(markerName)))

        @Language("kt")
        fun getConvertMethod(readMethod: String): String =
            """return DataFrame.$readMethod.convertTo<$markerName> { ${ConvertSchemaDsl<*>::convertDataRowsWithOpenApi.name}() }
            """.trimIndent()

        val typeSpec = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("url", URL::class)
                    .addCode(getConvertMethod("readJson(url)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("path", String::class)
                    .addCode(getConvertMethod("readJson(path)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("stream", InputStream::class)
                    .addCode(getConvertMethod("readJson(stream)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJsonStr")
                    .returns(returnType)
                    .addParameter("text", String::class)
                    .addCode(getConvertMethod("readJsonStr(text)"))
                    .build()
            )
            .build()

        return typeSpec.toString()
    }
}

internal fun isOpenApi(path: String): Boolean = isOpenApi(asURL(path))

internal fun isOpenApi(url: URL): Boolean {
    if (url.path.endsWith(".yml") || url.path.endsWith("yaml")) {
        return true
    }
    if (!url.path.endsWith("json")) {
        return false
    }

    return url.openStream().use {
        val parsed = Parser.default().parse(it) as? JsonObject ?: return false
        parsed["openapi"] != null
    }
}

internal fun isOpenApi(file: File): Boolean {
    if (file.extension.lowercase() in listOf("yml", "yaml")) {
        return true
    }

    if (file.extension.lowercase() != "json") {
        return false
    }

    val parsed = Parser.default().parse(file.inputStream()) as? JsonObject ?: return false

    return parsed["openapi"] != null
}

public fun readOpenApi(
    uri: String,
    auth: List<AuthorizationValue>? = null,
    options: ParseOptions? = null,
    extensionProperties: Boolean,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeWithConverter = readOpenApi(
    swaggerParseResult = OpenAPIParser().readLocation(uri, auth, options),
    extensionProperties = extensionProperties,
    visibility = visibility,
)

public fun readOpenApiAsString(
    openApiAsString: String,
    auth: List<AuthorizationValue>? = null,
    options: ParseOptions? = null,
    extensionProperties: Boolean,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeWithConverter = readOpenApi(
    swaggerParseResult = OpenAPIParser().readContents(openApiAsString, auth, options),
    extensionProperties = extensionProperties,
    visibility = visibility,
)

/**
 * Converts a parsed OpenAPI specification into a list of [CodeWithConverter] objects.
 *
 * @param swaggerParseResult the result of parsing an OpenAPI specification, created using [readJson] or [readOpenApiAsString].
 * @param visibility the visibility of the generated marker classes.
 *
 * @return a [CodeWithConverter] object, representing the generated code.
 */
private fun readOpenApi(
    swaggerParseResult: SwaggerParseResult,
    extensionProperties: Boolean,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeWithConverter {
    val openApi = swaggerParseResult.openAPI
        ?: error("Failed to parse OpenAPI, ${swaggerParseResult.messages.toList()}")

    // take the components.schemas from the openApi spec and convert them to a list of Markers, representing the
    // interfaces, enums, and typeAliases that need to be generated.
    val result = openApi.components?.schemas
        ?.toMap()
        ?.toMarkers()
        ?.toList()
        ?: emptyList()

    val codeGenerator = CodeGenerator.create(useFqNames = true)

    return result.map { marker ->
        codeGenerator.generate(
            marker = marker.withVisibility(visibility),
            interfaceMode = when (marker) {
                is OpenApiMarker.Enum -> InterfaceGenerationMode.Enum
                is OpenApiMarker.Interface -> InterfaceGenerationMode.WithFields
                is OpenApiMarker.TypeAlias, is OpenApiMarker.MarkerAlias -> InterfaceGenerationMode.TypeAlias
            },
            extensionProperties = extensionProperties,
            readDfMethod = if (marker is OpenApiMarker.Interface) DefaultReadOpenApiMethod else null,
        )
    }.reduce { a, b -> a + b }
}

private interface PrimitiveOrNot {
    val isPrimitive: Boolean
}

/** Represents the type of markers that we can generate. */
private sealed class OpenApiMarker private constructor(
    name: String,
    visibility: MarkerVisibility,
    fields: List<GeneratedField>,
    superMarkers: List<Marker>,
) : PrimitiveOrNot,
    Marker(
        name = name,
        isOpen = false,
        fields = fields,
        superMarkers = superMarkers,
        visibility = visibility,
        typeParameters = emptyList(),
        typeArguments = emptyList(),
    ) {

    abstract fun withName(name: String): OpenApiMarker
    abstract fun withVisibility(visibility: MarkerVisibility): OpenApiMarker

    override fun toString(): String =
        "MyMarker(markerType = ${this::class}, name = $name, isOpen = $isOpen, fields = $fields, superMarkers = $superMarkers, visibility = $visibility, typeParameters = $typeParameters, typeArguments = $typeArguments)"

    class Enum(
        val nullable: Boolean,
        fields: List<GeneratedField>,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        name = name,
        visibility = visibility,
        fields = fields,
        superMarkers = emptyList(),
    ) {

        // enums become List<Something>, not Dataframe<*>
        override val isPrimitive: Boolean = true

        override fun withName(name: String): Enum =
            Enum(nullable, fields, name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): Enum =
            Enum(nullable, fields, name, visibility)
    }

    class Interface(
        fields: List<GeneratedField>,
        superMarkers: List<Marker>,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        name = name,
        visibility = visibility,
        fields = fields,
        superMarkers = superMarkers,
    ) {

        override val isPrimitive = false

        override fun withName(name: String): Interface =
            Interface(fields, superMarkers.values.toList(), name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): Interface =
            Interface(fields, superMarkers.values.toList(), name, visibility)
    }

    /** Type alias that points at something other than a Marker. */
    class TypeAlias(
        name: String,
        val superMarkerName: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        name = name,
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
    ) {

        override val isPrimitive = true

        override fun withName(name: String): TypeAlias = TypeAlias(name, superMarkerName, visibility)

        override fun withVisibility(visibility: MarkerVisibility): TypeAlias =
            TypeAlias(name, superMarkerName, visibility)
    }

    /** Type alias that points at another Marker. */
    class MarkerAlias(
        val superMarker: OpenApiMarker,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        name = name,
        visibility = visibility,
        fields = emptyList(),
        superMarkers = listOf(superMarker),
    ) {

        // depends on the marker it points to whether it's primitive or not
        override val isPrimitive = superMarker.isPrimitive

        override fun withName(name: String): MarkerAlias = MarkerAlias(superMarker, name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): MarkerAlias =
            MarkerAlias(superMarker, name, visibility)
    }
}

/**
 * Converts named OpenApi schemas to a list of [OpenApiMarker]s.
 * Will cause an exception for circular references, however they shouldn't occur in OpenApi specs.
 *
 * Some explanation:
 * OpenApi provides schemas for all the types used. For each type, we want to generate a [Marker]
 * (Which can be an interface, enum or typealias). However, the OpenApi schema is not ordered per se,
 * so when we are reading the schema it might be that we have a reference to a (super)type
 * (which are queried using [getRefMarker]) for which we have not yet created a [Marker].
 * In that case, we "pause" that one (by returning [CannotFindRefMarker]) and try to read another type schema first.
 * Circular references cannot exist since it's encoded in JSON, so we never get stuck in an infinite loop.
 * When all markers are "retrieved" (so turned from a [RetrievableMarker] to a [MarkerResult.Success]),
 * we're done and have converted everything!
 * As for [produceAdditionalMarker]: In OpenAPI not all enums/objects have to be defined as a separate schema.
 * Although recommended, you can still define an object anonymously directly as a type. For this, we have
 * [produceAdditionalMarker] since during the conversion of a schema -> [Marker] we get an additional new [Marker].
 */
private fun Map<String, Schema<*>>.toMarkers(): List<OpenApiMarker> {
    // Convert the schemas to toMarker calls that can be repeated to resolve references.
    val retrievableMarkers = mapValues { (typeName, value) ->
        RetrievableMarker { getRefMarker, produceAdditionalMarker ->
            value.toMarker(
                typeName = typeName,
                getRefMarker = getRefMarker,
                produceAdditionalMarker = produceAdditionalMarker,
            )
        }
    }.toMutableMap()

    val markers = mutableMapOf<String, OpenApiMarker>()
    val produceAdditionalMarker = ProduceAdditionalMarker { validName, marker, _ ->
        var result = ValidFieldName.of(validName.unquoted)
        val baseName = result
        var attempt = 1
        while (result.quotedIfNeeded in markers) {
            result = ValidFieldName.of(
                baseName.unquoted + (if (result.needsQuote) " ($attempt)" else "$attempt")
            )
            attempt++
        }

        markers[result.quotedIfNeeded] = marker.withName(result.quotedIfNeeded)
        result.quotedIfNeeded
    }

    val getRefMarker = GetRefMarker {
        MarkerResult.fromNullable(markers[it])
    }

    // convert all the retrievable markers to actual markers, resolving references as we go and if possible
    while (retrievableMarkers.isNotEmpty()) try {
        retrievableMarkers.entries.first { (name, retrieveMarker) ->
            val res = retrieveMarker(
                getRefMarker = getRefMarker,
                produceAdditionalMarker = produceAdditionalMarker,
            )

            when (res) {
                is MarkerResult.Success -> {
                    markers[name] = res.marker
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

private sealed interface MarkerResult {

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : MarkerResult

    /** Successfully found or created [marker]. */
    data class Success(val marker: OpenApiMarker) : MarkerResult

    companion object {
        fun fromNullable(schema: OpenApiMarker?): MarkerResult =
            if (schema == null) CannotFindRefMarker else Success(schema)
    }
}

/** Represents a query to find a marker with certain name. Produces a [MarkerResult]. */
private fun interface GetRefMarker {

    /** Produces a [MarkerResult] (either [MarkerResult.CannotFindRefMarker] or [MarkerResult.Success]) for the
     * given [refName] representing a query to find a marker with that given name. */
    operator fun invoke(refName: String): MarkerResult
}

/**
 * Represents a call to produce an additional Marker from inside a schema component.
 * Not all objects or enums are named, so this is used to create and produce a name for them.
 */
private fun interface ProduceAdditionalMarker {

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
        val NULL = ProduceAdditionalMarker { validName, _, _ -> validName.quotedIfNeeded }
    }
}

/**
 * Represents a call to [toMarker] that can be repeated until it returns a [MarkerResult.Success].
 */
private fun interface RetrievableMarker {

    /**
     * Represents a call to [toMarker] that can be repeated until it returns a [MarkerResult.Success].
     *
     * @param getRefMarker              A function that returns a [Marker] for a given reference name if successful.
     * @param produceAdditionalMarker   A function that produces an additional [Marker] for a given name.
     *                                  This is used for `object` types not present in the root of `components/schemas`.
     *
     * @return A [MarkerResult.Success] if successful, otherwise [MarkerResult.CannotFindRefMarker].
     */
    operator fun invoke(
        getRefMarker: GetRefMarker,
        produceAdditionalMarker: ProduceAdditionalMarker
    ): MarkerResult
}

private fun generatedFieldOf(
    fieldName: ValidFieldName,
    columnName: String,
    overrides: Boolean,
    fieldType: FieldType,
): GeneratedField = GeneratedField(
    fieldName = fieldName,
    columnName = columnName,
    overrides = overrides,
    columnSchema = ColumnSchema.Value(typeOf<Any?>()), // unused
    fieldType = fieldType,
)

private fun generatedEnumFieldOf(
    fieldName: ValidFieldName,
    columnName: String,
): GeneratedField = generatedFieldOf(
    fieldName = fieldName,
    columnName = columnName,
    overrides = false,
    fieldType = FieldType.ValueFieldType(typeOf<String>().toString()), // all enums will be of type String
)

/**
 * Converts a single OpenApi object type schema to a [Marker] if successful.
 *
 * @param typeName The name of the schema / type to convert.
 * @param getRefMarker Function to retrieve a [Marker] for a given reference name.
 * @param produceAdditionalMarker Function to produce an additional [Marker] on the fly, such as for
 *   inline enums/classes in arrays.
 * @param required Optional list of required properties for this schema.
 *
 * @return A [MarkerResult.Success] if successful, otherwise [MarkerResult.CannotFindRefMarker].
 */
private fun Schema<*>.toMarker(
    typeName: String,
    getRefMarker: GetRefMarker,
    produceAdditionalMarker: ProduceAdditionalMarker,
    required: List<String> = emptyList(),
): MarkerResult {
    @Suppress("NAME_SHADOWING")
    val required = (this.required ?: emptyList()) + required
    return when {
        // If allOf is defined, multiple objects are to be composed together. This is done using inheritance.
        // https://swagger.io/docs/specification/data-models/oneof-anyof-allof-not/#allof
        allOf != null -> {
            val allOfSchemas = allOf!!.associateWith {
                it.toOpenApiType(isRequired = true, getRefMarker = getRefMarker)
            }

            // An un-required super field might be required from a child schema.
            val requiredFields =
                (allOfSchemas.keys.flatMap { it.required ?: emptyList() } + required).distinct()

            val superMarkers = mutableListOf<Marker>()
            val fields = mutableListOf<GeneratedField>()

            for ((schema, openApiTypeResult) in allOfSchemas)
                when (openApiTypeResult) {
                    is OpenApiTypeResult.CannotFindRefMarker ->
                        return MarkerResult.CannotFindRefMarker

                    is OpenApiTypeResult.UsingRef -> {
                        val superMarker = openApiTypeResult.marker
                        superMarkers += superMarker

                        // make sure required fields are overridden to be non-null
                        val allSuperFields = (
                            superMarker.fields +
                                superMarker.allSuperMarkers.values.flatMap { it.fields }
                            )
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

                    is OpenApiTypeResult.SuccessAsEnum -> error("allOf cannot contain enum types")

                    is OpenApiTypeResult.Success -> {
                        val (openApiType, nullable) = openApiTypeResult

                        // must be an object
                        openApiType as OpenApiType.Object

                        // create temp marker for top-level object so its fields can be merged in the allOf
                        var tempMarker: OpenApiMarker? = null

                        val fieldTypeResult = openApiType.toFieldType(
                            schema = schema,
                            schemaName = typeName,
                            nullable = nullable,
                            getRefMarker = getRefMarker,
                            produceAdditionalMarker = { name, marker, isTopLevelObject ->
                                if (isTopLevelObject) {
                                    tempMarker = marker
                                    name.quotedIfNeeded
                                } else {
                                    produceAdditionalMarker(name, marker, false)
                                }
                            },
                            required = required,
                        )

                        when (fieldTypeResult) {
                            is FieldTypeResult.CannotFindRefMarker ->
                                return MarkerResult.CannotFindRefMarker

                            is FieldTypeResult.Success ->
                                fields += tempMarker!!.fields
                        }
                    }
                }

            MarkerResult.Success(
                OpenApiMarker.Interface(
                    name = typeName,
                    fields = fields,
                    superMarkers = superMarkers,
                )
            )
        }

        // If enum is defined, create an enum class.
        // https://swagger.io/docs/specification/data-models/enums/
        enum != null -> {
            val openApiTypeResult = toOpenApiType(
                isRequired = name in required,
                getRefMarker = getRefMarker,
            ) as OpenApiTypeResult.SuccessAsEnum // must be an enum

            val enumMarker = produceNewEnum(
                name = typeName,
                values = openApiTypeResult.values,
                nullable = openApiTypeResult.nullable,
            )

            MarkerResult.Success(enumMarker)
        }

        // If type == object, create a new Marker to become an interface.
        // https://swagger.io/docs/specification/data-models/data-types/#object
        type == "object" -> {
            // todo remove
            println(
                "Debug: Type: ${this::class}, properties: ${properties != null} , additionalProperties: ${additionalProperties != null}"
            )

            if (nullable == true) {
//                println(
//                    "Warning: type $name is marked nullable, but ColumnGroups cannot be null, so instead all properties will be made nullable."
//                )
            }

            when {
                // Gather the given properties as fields
                properties != null -> {
                    if (additionalProperties != null) {
                        println("OpenAPI warning: type $name has both properties and additionalProperties defined, but only properties will be generated in the data schema.")
                    }

                    val fields = buildList {
                        for ((name, property) in (properties ?: emptyMap())) {
                            val isRequired = name in required
                            val isRequiredAndNotNullable = isRequired || property.nullable == false
                            val openApiTypeResult = property.toOpenApiType(
                                isRequired = isRequiredAndNotNullable,
                                getRefMarker = getRefMarker,
                            )

                            when (openApiTypeResult) {
                                is OpenApiTypeResult.CannotFindRefMarker ->
                                    return MarkerResult.CannotFindRefMarker

                                is OpenApiTypeResult.UsingRef -> {
                                    val validName = ValidFieldName.of(name.snakeToLowerCamelCase())

                                    this += generatedFieldOf(
                                        overrides = false,
                                        fieldName = validName,
                                        columnName = name,
                                        fieldType = when (val marker = openApiTypeResult.marker) {
                                            is OpenApiMarker.TypeAlias ->
                                                FieldType.ValueFieldType(
                                                    typeFqName = marker.name + if (isRequiredAndNotNullable) "" else "?",
                                                )

                                            is OpenApiMarker.Enum ->
                                                FieldType.ValueFieldType(
                                                    // nullable or not, an enum must contain null to be nullable
                                                    // https://github.com/OAI/OpenAPI-Specification/blob/main/proposals/2019-10-31-Clarify-Nullable.md#if-a-schema-specifies-nullable-true-and-enum-1-2-3-does-that-schema-allow-null-values-see-1900
                                                    // if not required, it can still be omitted, resulting in null in Kotlin
                                                    typeFqName = marker.name + if (!isRequired || marker.nullable) "?" else "",
                                                )

                                            is OpenApiMarker.MarkerAlias, is OpenApiMarker.Interface ->
                                                FieldType.GroupFieldType(
                                                    markerName = marker.name + if (isRequiredAndNotNullable) "" else "?",
                                                )
                                        },
                                    )
                                }

                                is OpenApiTypeResult.SuccessAsEnum -> {
                                    // inner enum, so produce it as additional
                                    val enumMarker = produceNewEnum(
                                        name = name,
                                        values = openApiTypeResult.values,
                                        produceAdditionalMarker = produceAdditionalMarker,
                                        nullable = openApiTypeResult.nullable,
                                    )

                                    this += generatedFieldOf(
                                        overrides = false,
                                        fieldName = ValidFieldName.of(name.snakeToLowerCamelCase()),
                                        columnName = name,
                                        fieldType = FieldType.ValueFieldType(
                                            typeFqName = enumMarker.name + if (!isRequiredAndNotNullable || enumMarker.nullable) "?" else "",
                                        ),
                                    )
                                }

                                is OpenApiTypeResult.Success -> {
                                    val (openApiType, nullable) = openApiTypeResult

                                    val fieldTypeResult = openApiType.toFieldType(
                                        schema = property,
                                        schemaName = name,
                                        nullable = nullable,
                                        getRefMarker = getRefMarker,
                                        produceAdditionalMarker = produceAdditionalMarker,
                                        required = required,
                                    )

                                    when (fieldTypeResult) {
                                        is FieldTypeResult.CannotFindRefMarker ->
                                            return MarkerResult.CannotFindRefMarker

                                        is FieldTypeResult.Success -> {
                                            val validName = ValidFieldName.of(name.snakeToLowerCamelCase())

                                            this += generatedFieldOf(
                                                overrides = false,
                                                fieldName = validName,
                                                columnName = name,
                                                fieldType = fieldTypeResult.fieldType,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    MarkerResult.Success(
                        OpenApiMarker.Interface(
                            name = typeName,
                            fields = fields,
                            superMarkers = emptyList(),
                        )
                    )
                }

                properties == null && additionalProperties != null -> {
                    val valueTypeResult = (additionalProperties as? Schema<*>)
                        ?.toOpenApiType(isRequired = false, getRefMarker = getRefMarker)

                    val mapType = "${Map::class.qualifiedName!!}<${String::class.qualifiedName!!}, " +
                        when (valueTypeResult) {
                            is OpenApiTypeResult.CannotFindRefMarker ->
                                return MarkerResult.CannotFindRefMarker

                            is OpenApiTypeResult.UsingRef ->
                                if (valueTypeResult.marker.isPrimitive) {
                                    valueTypeResult.marker.name
                                } else {
                                    "${DataRow::class.qualifiedName}<${valueTypeResult.marker.name}>"
                                }

                            is OpenApiTypeResult.Success -> {
                                if (!valueTypeResult.nullable) {
                                    println("OpenAPI warning: $typeName is marked to have additionalProperties that are not nullable, however in DataFrame is may still have null values based off keys from other instances of $typeName.")
                                }
                                val fieldTypeRes = valueTypeResult
                                    .openApiType
                                    .toFieldType(
                                        schema = this,
                                        schemaName = typeName,
                                        nullable = true,
                                        getRefMarker = getRefMarker,
                                        produceAdditionalMarker = produceAdditionalMarker,
                                        required = required,
                                    )

                                when (fieldTypeRes) {
                                    FieldTypeResult.CannotFindRefMarker ->
                                        return MarkerResult.CannotFindRefMarker

                                    is FieldTypeResult.Success ->
                                        when (fieldTypeRes.fieldType) {
                                            is FieldType.ValueFieldType, is FieldType.GroupFieldType ->
                                                fieldTypeRes.fieldType.name

                                            is FieldType.FrameFieldType ->
                                                "${DataFrame::class.qualifiedName!!}<${fieldTypeRes.fieldType.name}>"
                                        }
                                }
                            }

                            is OpenApiTypeResult.SuccessAsEnum -> {
                                // inner enum, so produce it as additional
                                val enumMarker = produceNewEnum(
                                    name = name,
                                    values = valueTypeResult.values,
                                    produceAdditionalMarker = produceAdditionalMarker,
                                    nullable = valueTypeResult.nullable,
                                )

                                enumMarker.name + if (enumMarker.nullable) "?" else ""
                            }

                            null -> "Any?"
                        } + ">"

                    MarkerResult.Success(
                        OpenApiMarker.TypeAlias(
                            name = ValidFieldName.of(typeName).quotedIfNeeded,
                            superMarkerName = mapType,
                        )
                    )
                }

                else -> MarkerResult.Success(
                    OpenApiMarker.Interface(
                        name = typeName,
                        fields = emptyList(),
                        superMarkers = emptyList(),
                    )
                )
            }
        }

        // If type is something else, produce it as type alias. Can be a reference to another OpenApi type or something else.
        else -> {
            val openApiTypeResult = toOpenApiType(
                isRequired = true,
                getRefMarker = getRefMarker,
            )

            val typeAliasMarker = when (openApiTypeResult) {
                is OpenApiTypeResult.CannotFindRefMarker ->
                    return MarkerResult.CannotFindRefMarker

                is OpenApiTypeResult.UsingRef -> OpenApiMarker.MarkerAlias(
                    name = ValidFieldName.of(typeName).quotedIfNeeded,
                    superMarker = openApiTypeResult.marker,
                )

                is OpenApiTypeResult.Success -> {
                    val type = openApiTypeResult
                        .openApiType
                        .toFieldType(
                            schema = this,
                            schemaName = typeName,
                            nullable = false,
                            getRefMarker = getRefMarker,
                            produceAdditionalMarker = produceAdditionalMarker,
                            required = required,
                        ).let {
                            when (it) {
                                FieldTypeResult.CannotFindRefMarker ->
                                    return MarkerResult.CannotFindRefMarker

                                is FieldTypeResult.Success ->
                                    when (it.fieldType) {
                                        is FieldType.ValueFieldType, is FieldType.GroupFieldType -> it.fieldType.name
                                        is FieldType.FrameFieldType ->
                                            "${DataFrame::class.qualifiedName!!}<${it.fieldType.name}>"
                                    }
                            }
                        }

                    OpenApiMarker.TypeAlias(
                        name = ValidFieldName.of(typeName).quotedIfNeeded,
                        superMarkerName = type,
                    )
                }

                is OpenApiTypeResult.SuccessAsEnum -> error("cannot happen, since enum != null is checked earlier")
            }

            MarkerResult.Success(typeAliasMarker)
        }
    }
}

/** Small helper function to produce a new enum Marker. */
private fun produceNewEnum(
    name: String,
    values: List<String>,
    nullable: Boolean,
    produceAdditionalMarker: ProduceAdditionalMarker = ProduceAdditionalMarker.NULL,
): OpenApiMarker.Enum {
    val enumName = ValidFieldName.of(name.snakeToUpperCamelCase())
    val enumMarker = OpenApiMarker.Enum(
        name = enumName.quotedIfNeeded,
        fields = values.map {
            generatedEnumFieldOf(
                fieldName = ValidFieldName.of(it),
                columnName = it,
            )
        },
        nullable = nullable,
    )
    val newName = produceAdditionalMarker(enumName, enumMarker, isTopLevelObject = false)

    return enumMarker.withName(newName)
}

private enum class OpenApiIntegerFormat(val value: String) {
    INT32("int32"),
    INT64("int64");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiIntegerFormat? = values().firstOrNull { it.value == value }
    }
}

private enum class OpenApiNumberFormat(val value: String) {
    FLOAT("float"),
    DOUBLE("double");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiNumberFormat? = values().firstOrNull { it.value == value }
    }
}

private enum class OpenApiStringFormat(val value: String) {
    DATE("date"),
    DATE_TIME("date-time"),
    PASSWORD("password"),
    BYTE("byte"),
    BINARY("binary");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiStringFormat? = values().firstOrNull { it.value == value }
    }
}

private fun String.toNullable() = if (this.last() == '?') this else "$this?"

/**
 * Represents all types supported by OpenApi with functions to create a [FieldType] from each.
 */
private sealed class OpenApiType(val name: kotlin.String?, override val isPrimitive: kotlin.Boolean) : PrimitiveOrNot {

    object String : OpenApiType("string", true) {

        fun getType(nullable: kotlin.Boolean, format: OpenApiStringFormat?): FieldType = FieldType.ValueFieldType(
            typeFqName = when (format) {
                OpenApiStringFormat.DATE -> if (nullable) typeOf<LocalDate?>() else typeOf<LocalDate>()
                OpenApiStringFormat.DATE_TIME -> if (nullable) typeOf<LocalDateTime?>() else typeOf<LocalDateTime>()
                OpenApiStringFormat.PASSWORD -> if (nullable) typeOf<kotlin.String?>() else typeOf<kotlin.String>()
                OpenApiStringFormat.BYTE -> if (nullable) typeOf<Byte?>() else typeOf<Byte>()
                OpenApiStringFormat.BINARY -> if (nullable) typeOf<ByteArray?>() else typeOf<ByteArray>()
                null -> if (nullable) typeOf<kotlin.String?>() else typeOf<kotlin.String>()
            }.toString(),
        )
    }

    object Integer : OpenApiType("integer", true) {

        fun getType(nullable: kotlin.Boolean, format: OpenApiIntegerFormat?): FieldType = FieldType.ValueFieldType(
            typeFqName = when (format) {
                null, OpenApiIntegerFormat.INT32 -> if (nullable) typeOf<Int?>() else typeOf<Int>()
                OpenApiIntegerFormat.INT64 -> if (nullable) typeOf<Long?>() else typeOf<Long>()
            }.toString(),
        )
    }

    object Number : OpenApiType("number", true) {

        fun getType(nullable: kotlin.Boolean, format: OpenApiNumberFormat?): FieldType = FieldType.ValueFieldType(
            typeFqName = when (format) {
                null, OpenApiNumberFormat.FLOAT -> if (nullable) typeOf<Float?>() else typeOf<Float>()
                OpenApiNumberFormat.DOUBLE -> if (nullable) typeOf<Double?>() else typeOf<Double>()
            }.toString(),
        )
    }

    object Boolean : OpenApiType("boolean", true) {

        fun getType(nullable: kotlin.Boolean): FieldType = FieldType.ValueFieldType(
            typeFqName = if (nullable) {
                typeOf<kotlin.Boolean?>()
            } else {
                typeOf<kotlin.Boolean>()
            }.toString(),
        )
    }

    object Object : OpenApiType("object", false) {

        fun getType(nullable: kotlin.Boolean, marker: OpenApiMarker): FieldType =
            FieldType.GroupFieldType(
                markerName = marker.name.let {
                    if (nullable) it.toNullable() else it
                },
            )

        fun getType(nullable: kotlin.Boolean, type: KType): FieldType =
            FieldType.GroupFieldType(
                markerName = type.toString().let {
                    if (nullable) it.toNullable() else it
                },
            )
    }

    /** Represents a merged object which will turn into DataRow<Any?> */
    object AnyObject : OpenApiType(null, false) {

        fun getType(nullable: kotlin.Boolean): FieldType =
            FieldType.GroupFieldType(
                markerName = (if (nullable) typeOf<DataRow<kotlin.Any?>>() else typeOf<DataRow<kotlin.Any>>()).toString(),
            )
    }

    object Array : OpenApiType("array", true) {

        // used for list of primitives
        fun getTypeAsList(nullableArray: kotlin.Boolean, typeFqName: kotlin.String): FieldType =
            FieldType.ValueFieldType(
                typeFqName = "${List::class.qualifiedName!!}<$typeFqName>${if (nullableArray) "?" else ""}",
            )

        // used for list of objects
        fun getTypeAsFrame(nullableArray: kotlin.Boolean, markerName: kotlin.String): FieldType =
            FieldType.FrameFieldType(
                markerName = markerName.let { if (nullableArray) it.toNullable() else it },
                nullable = false, // preferring DataFrame<Something?> over DataFrame<Something>?
            )
    }

    object Any : OpenApiType(null, true) {
        fun getType(nullable: kotlin.Boolean): FieldType = FieldType.ValueFieldType(
            typeFqName = if (nullable) {
                typeOf<kotlin.Any?>()
            } else {
                typeOf<kotlin.Any>()
            }.toString(),
        )
    }

    override fun toString(): kotlin.String = name.toString()

    companion object {

        val all: List<OpenApiType> = listOf(String, Integer, Number, Boolean, Object, Array, Any)

        val primives: List<OpenApiType> = all.filter { it.isPrimitive }

        fun fromString(type: kotlin.String?): OpenApiType =
            fromStringOrNull(type) ?: throw IllegalArgumentException("Unknown type: $type")

        fun fromStringOrNull(type: kotlin.String?): OpenApiType? = when (type) {
            "string" -> String
            "integer" -> Integer
            "number" -> Number
            "boolean" -> Boolean
            "object" -> Object
            "array" -> Array
            null -> Any
            else -> null
        }
    }
}

private sealed interface OpenApiTypeResult {

    /** Property is a reference with name [name] and Marker [marker]. */
    class UsingRef(val marker: OpenApiMarker) : OpenApiTypeResult

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : OpenApiTypeResult

    /** Property is a schema with OpenApiType [openApiType]. */
    data class Success(val openApiType: OpenApiType, val nullable: Boolean) : OpenApiTypeResult

    /** Property is an enum with values [values]. */
    data class SuccessAsEnum(val values: List<String>, val nullable: Boolean) : OpenApiTypeResult
}

/**
 * Converts a single property of an OpenApi type schema to [OpenApiTypeResult].
 * It can become an [OpenApiType], [OpenApiMarker] reference, enum, or unresolved reference.
 */
private fun Schema<*>.toOpenApiType(
    isRequired: Boolean,
    getRefMarker: GetRefMarker,
): OpenApiTypeResult {
    if (`$ref` != null) {
        val typeName = `$ref`.takeLastWhile { it != '/' }
        return when (val it = getRefMarker(typeName)) {
            is MarkerResult.CannotFindRefMarker ->
                OpenApiTypeResult.CannotFindRefMarker

            is MarkerResult.Success ->
                OpenApiTypeResult.UsingRef(it.marker)
        }
    }

    if (enum != null) {
        val nullable = enum.any { it == null }

        // Note: type doesn't matter, all is interpreted as (quoted) string

        return OpenApiTypeResult.SuccessAsEnum(
            values = enum.filterNotNull().map { it.toString() },
            nullable = nullable,
        )
    }

    var openApiType = OpenApiType.fromStringOrNull(type)

    if (openApiType == null || openApiType is OpenApiType.Any) { // check for anyOf/oneOf/not, https://swagger.io/docs/specification/data-models/oneof-anyof-allof-not/
        val anyOf = ((anyOf ?: emptyList()) + (oneOf ?: emptyList()))
        val anyOfRefs = anyOf.mapNotNull { it.`$ref` }.map { ref ->
            val typeName = ref.takeLastWhile { it != '/' }
            when (val it = getRefMarker(typeName)) {
                is MarkerResult.CannotFindRefMarker ->
                    return OpenApiTypeResult.CannotFindRefMarker

                is MarkerResult.Success -> it.marker
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

            anyOfTypes.all { it.isPrimitive } && anyOfRefs.isEmpty() -> OpenApiType.Any

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
            allTypes.isNotEmpty() && !allTypes.any { it.isPrimitive } -> OpenApiType.AnyObject

            // cannot assume anything about a type when there are multiple types except one
            not != null -> OpenApiType.Any

            else -> OpenApiType.Any
        }
    }

    val nullable = nullable ?: !isRequired

    if (nullable && openApiType == OpenApiType.Object) {
//        println(
//            "Warning: type $name is marked nullable, but ColumnGroups cannot be null, so instead all properties will be made nullable."
//        )
    }

    return OpenApiTypeResult.Success(openApiType, nullable)
}

private sealed interface FieldTypeResult {

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : FieldTypeResult

    /** ColumnSchema [fieldType] created successfully. */
    data class Success(val fieldType: FieldType) : FieldTypeResult
}

/** Converts an OpenApiType with schema to a [FieldType] if successful. */
private fun OpenApiType.toFieldType(
    schema: Schema<*>,
    schemaName: String,
    nullable: Boolean,
    getRefMarker: GetRefMarker,
    produceAdditionalMarker: ProduceAdditionalMarker,
    required: List<String>,
): FieldTypeResult {
    return FieldTypeResult.Success(
        fieldType = when (this) {
            is OpenApiType.Any -> getType(nullable)

            is OpenApiType.Array -> {
                schema as ArraySchema
                if (schema.items == null) { // should in theory not occur, but make List<Any?> just in case
                    getTypeAsList(
                        nullableArray = nullable,
                        typeFqName = (OpenApiType.Any.getType(nullable = true) as FieldType.ValueFieldType).typeFqName
                    )
                } else { // Try to get the array type TODO decide whether to make nullable or not
                    val arrayTypeResult = schema.items!!
                        .toOpenApiType(
                            isRequired = true,
                            getRefMarker = getRefMarker,
                        )

                    when (arrayTypeResult) {
                        is OpenApiTypeResult.CannotFindRefMarker ->
                            return FieldTypeResult.CannotFindRefMarker

                        is OpenApiTypeResult.UsingRef ->
                            if (arrayTypeResult.marker.isPrimitive) {
                                getTypeAsList(
                                    nullableArray = nullable,
                                    typeFqName = arrayTypeResult.marker.name,
                                )
                            } else {
                                getTypeAsFrame(
                                    nullableArray = nullable,
                                    markerName = arrayTypeResult.marker.name,
                                )
                            }

                        is OpenApiTypeResult.Success -> {
                            val arrayTypeSchemaResult = arrayTypeResult.openApiType
                                .toFieldType(
                                    schema = schema.items!!,
                                    schemaName = schemaName + "Content", // type name objects in the array will get
                                    nullable = arrayTypeResult.nullable,
                                    getRefMarker = getRefMarker,
                                    produceAdditionalMarker = produceAdditionalMarker,
                                    required = emptyList(),
                                )

                            when (arrayTypeSchemaResult) {
                                is FieldTypeResult.CannotFindRefMarker ->
                                    return FieldTypeResult.CannotFindRefMarker

                                is FieldTypeResult.Success -> {
                                    val it = arrayTypeSchemaResult.fieldType
                                    when {
                                        it is FieldType.GroupFieldType && it.name == typeOf<DataRow<Any>>().toString() ->
                                            getTypeAsFrame(
                                                nullableArray = nullable,
                                                markerName = typeOf<Any>().toString(),
                                            )

                                        it is FieldType.GroupFieldType && it.name == typeOf<DataRow<Any?>>().toString() ->
                                            getTypeAsFrame(
                                                nullableArray = nullable,
                                                markerName = typeOf<Any?>().toString(),
                                            )

                                        it is FieldType.GroupFieldType ->
                                            getTypeAsFrame(
                                                nullableArray = nullable, // GroupFieldType (DataFrame<*>) cannot be null
                                                markerName = it.name,
                                            )

                                        it is FieldType.FrameFieldType ->
                                            getTypeAsFrame(
                                                nullableArray = nullable,
                                                markerName = it.name,
                                            )

                                        it is FieldType.ValueFieldType ->
                                            getTypeAsList(
                                                nullableArray = nullable,
                                                typeFqName = it.name,
                                            )

                                        else -> error("")
                                    }
                                }
                            }
                        }

                        is OpenApiTypeResult.SuccessAsEnum -> {
                            val enumMarker = produceNewEnum(
                                name = schemaName,
                                values = arrayTypeResult.values,
                                produceAdditionalMarker = produceAdditionalMarker,
                                nullable = arrayTypeResult.nullable,
                            )

                            getTypeAsList(
                                nullableArray = nullable,
                                typeFqName = enumMarker.name + if (enumMarker.nullable) "?" else "",
                            )
                        }
                    }
                }
            }

            is OpenApiType.Boolean -> getType(nullable)

            is OpenApiType.Integer -> getType(
                nullable = nullable,
                format = OpenApiIntegerFormat.fromStringOrNull(schema.format)
            )

            is OpenApiType.Number -> getType(
                nullable = nullable,
                format = OpenApiNumberFormat.fromStringOrNull(schema.format)
            )

            is OpenApiType.AnyObject -> getType(
                nullable = nullable,
            )

            is OpenApiType.Object -> {
                val dataFrameSchemaResult = schema.toMarker(
                    typeName = schemaName.snakeToUpperCamelCase(),
                    getRefMarker = getRefMarker,
                    produceAdditionalMarker = { validName, marker, _ ->
                        produceAdditionalMarker(validName, marker, isTopLevelObject = false)
                    },
                    required = required,
                )

                when (dataFrameSchemaResult) {
                    is MarkerResult.CannotFindRefMarker ->
                        return FieldTypeResult.CannotFindRefMarker

                    is MarkerResult.Success -> {
                        val newName = produceAdditionalMarker(
                            validName = ValidFieldName.of(schemaName.snakeToUpperCamelCase()),
                            marker = dataFrameSchemaResult.marker,
                            isTopLevelObject = true,
                        )

                        getType(
                            nullable = nullable,
                            marker = dataFrameSchemaResult.marker.withName(newName),
                        )
                    }
                }
            }

            is OpenApiType.String -> getType(
                nullable = nullable,
                format = OpenApiStringFormat.fromStringOrNull(schema.format),
            )
        },
    )
}

internal fun String.snakeToLowerCamelCase(): String =
    toCamelCaseByDelimiters(DELIMITERS_REGEX)

internal fun String.snakeToUpperCamelCase(): String =
    snakeToLowerCamelCase()
        .replaceFirstChar { it.uppercaseChar() }

package org.jetbrains.kotlinx.dataframe.io // ktlint-disable filename

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.core.models.AuthorizationValue
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.intellij.lang.annotations.Language
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.GeneratedField
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.codeGen.plus
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.reflect.typeOf

public fun main() {
    val result = readOpenApi(
        "/mnt/data/Projects/dataframe/core/src/test/resources/openapi_advanced_example.yaml"
    )

//        println("$name:")
//        println(marker)
//        println()
//        println("generated code:")
    println(result.declarations)
}

public class OpenApi : SupportedCodeGenerationFormat {

    public fun readCodeForGeneration(text: String): CodeWithConverter =
        readOpenApiAsString(text)

    override fun readCodeForGeneration(stream: InputStream): CodeWithConverter =
        readOpenApiAsString(stream.bufferedReader().readText())

    override fun readCodeForGeneration(file: File): CodeWithConverter =
        readOpenApiAsString(file.readText())

    override fun acceptsExtension(ext: String): Boolean = ext in listOf("yaml", "yml", "json")

    override val testOrder: Int = 60000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod = DefaultReadOpenApiMethod
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
        "import org.jetbrains.kotlinx.dataframe.api.convert",
        "import org.jetbrains.kotlinx.dataframe.api.with",
        "import org.jetbrains.kotlinx.dataframe.io.getVisibleValues",
        "import org.jetbrains.kotlinx.dataframe.io.valueColumnName",
        "import org.jetbrains.kotlinx.dataframe.io.arrayColumnName",
    )

    override fun toDeclaration(markerName: String, visibility: String): String {
        val returnType = DataFrame::class.asClassName().parameterizedBy(ClassName("", listOf(markerName)))

        @Language("kt")
        fun getConvertMethod(readMethod: String): String =
            """|return DataFrame.$readMethod.convertTo<$markerName> {
               |    convert<DataRow<*>>().with<DataRow<*>, Any?> {
               |        if (it.getVisibleValues().isEmpty()) null
               |        else it[valueColumnName] ?: it[arrayColumnName]
               |    }
               |}
            """.trimMargin()

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
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeWithConverter = readOpenApi(OpenAPIParser().readLocation(uri, auth, options), visibility)

public fun readOpenApiAsString(
    openApiAsString: String,
    auth: List<AuthorizationValue>? = null,
    options: ParseOptions? = null,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeWithConverter = readOpenApi(OpenAPIParser().readContents(openApiAsString, auth, options), visibility)

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
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeWithConverter {
    val openApi = swaggerParseResult.openAPI
        ?: error("Failed to parse OpenAPI, ${swaggerParseResult.messages.toList()}")

    // take the components.schemas from the openApi spec and convert them to a list of Markers, representing the
    // interfaces, enums, and typealiases that need to be generated.
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
                is MyMarker.Enum -> InterfaceGenerationMode.Enum
                is MyMarker.Interface -> InterfaceGenerationMode.WithFields
                is MyMarker.TypeAlias, is MyMarker.MarkerAlias -> InterfaceGenerationMode.TypeAlias
            },
            extensionProperties = false,
            readDfMethod = if (marker is MyMarker.Interface) DefaultReadOpenApiMethod else null,
        )
    }.reduce { a, b -> a + b }
}

/** Represents the type of markers that we can generate. */
private sealed class MyMarker private constructor(
    name: String,
    visibility: MarkerVisibility,
    fields: List<GeneratedField>,
    superMarkers: List<Marker>,
) : Marker(
    name = name,
    isOpen = false,
    fields = fields,
    superMarkers = superMarkers,
    visibility = visibility,
    typeParameters = emptyList(),
    typeArguments = emptyList(),
) {

    abstract fun withName(name: String): MyMarker
    abstract fun withVisibility(visibility: MarkerVisibility): MyMarker

    override fun toString(): String =
        "MyMarker(markerType = ${this::class}, name = $name, isOpen = $isOpen, fields = $fields, superMarkers = $superMarkers, visibility = $visibility, typeParameters = $typeParameters, typeArguments = $typeArguments)"

    class Enum(
        val nullable: Boolean,
        fields: List<GeneratedField>,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : MyMarker(name = name, visibility = visibility, fields = fields, superMarkers = emptyList()) {
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
    ) : MyMarker(name = name, visibility = visibility, fields = fields, superMarkers = superMarkers) {
        override fun withName(name: String): Interface =
            Interface(fields, superMarkers.values.toList(), name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): Interface =
            Interface(fields, superMarkers.values.toList(), name, visibility)
    }

    /** Type alias that points at something other than a Marker. */
    class TypeAlias(
        val superMarker: Marker,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : MyMarker(name = name, visibility = visibility, fields = emptyList(), superMarkers = listOf(superMarker)) {
        override fun withName(name: String): TypeAlias = TypeAlias(superMarker, name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): TypeAlias =
            TypeAlias(superMarker, name, visibility)
    }

    /** Type alias that points at another Marker. */
    class MarkerAlias(
        val superMarker: Marker,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : MyMarker(name = name, visibility = visibility, fields = emptyList(), superMarkers = listOf(superMarker)) {
        override fun withName(name: String): MarkerAlias = MarkerAlias(superMarker, name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): MarkerAlias =
            MarkerAlias(superMarker, name, visibility)
    }
}

/**
 * Converts named OpenApi schemas to a list of [MyMarker]s.
 * Will cause an exception for circular references, however they shouldn't occur in OpenApi specs.
 */
private fun Map<String, Schema<*>>.toMarkers(): List<MyMarker> {
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

    val markers = mutableMapOf<String, MyMarker>()

    // convert all the retrievable markers to actual markers, resolving references as we go.
    while (retrievableMarkers.isNotEmpty()) {
        retrievableMarkers.entries.first { (name, retrieveMarker) ->
            val res = retrieveMarker(
                getRefMarker = { MarkerResult.fromNullable(markers[it]) },
                produceAdditionalMarker = { validName, marker, _ ->
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
            )

            when (res) {
                is MarkerResult.Success -> {
                    markers[name] = res.marker
                    retrievableMarkers -= name
                    true // Marker is retrieved completely, remove it from the map.
                }

                is MarkerResult.CannotFindRefMarker ->
                    false // Cannot find a referenced Marker for this one, so we'll try again later.
            }
        }
    }

    return markers.values.toList()
}

private sealed interface MarkerResult {

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : MarkerResult

    /** Successfully found or created [marker]. */
    data class Success(val marker: MyMarker) : MarkerResult

    companion object {
        fun fromNullable(schema: MyMarker?): MarkerResult =
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
        marker: MyMarker,
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

private fun FieldType.isNullable(): Boolean = when (this) {
    is FieldType.FrameFieldType -> nullable
    is FieldType.GroupFieldType -> false
    is FieldType.ValueFieldType -> typeFqName.endsWith("?")
}

private fun FieldType.toNotNullable(): FieldType = if (isNullable()) {
    when (this) {
        is FieldType.FrameFieldType -> FieldType.FrameFieldType(markerName, false)
        is FieldType.GroupFieldType -> this
        is FieldType.ValueFieldType -> FieldType.ValueFieldType(typeFqName = typeFqName.removeSuffix("?"))
    }
} else this

private val FieldType.name
    get() = when (this) {
        is FieldType.FrameFieldType -> markerName
        is FieldType.GroupFieldType -> markerName
        is FieldType.ValueFieldType -> typeFqName
    }

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
                        superMarkers += openApiTypeResult.marker

                        // make sure required fields are overridden to be non-null
                        fields += openApiTypeResult.marker.fields
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
                        var tempMarker: MyMarker? = null

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
                MyMarker.Interface(
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
            // Gather the given properties as fields
            val fields = buildList {
                for ((name, property) in (properties ?: emptyMap())) {
                    val isRequired = name in required
                    val openApiTypeResult = property.toOpenApiType(
                        isRequired = isRequired,
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
                                    is MyMarker.TypeAlias ->
                                        FieldType.ValueFieldType(
                                            typeFqName = marker.name + if (isRequired) "" else "?",
                                        )

                                    is MyMarker.Enum ->
                                        FieldType.ValueFieldType(
                                            typeFqName = marker.name + if (!isRequired || marker.nullable) "?" else "",
                                        )

                                    is MyMarker.MarkerAlias, is MyMarker.Interface ->
                                        FieldType.GroupFieldType(
                                            markerName = marker.name, // Group cannot be nullable in DF
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
                                    typeFqName = enumMarker.name + (if (!isRequired || enumMarker.nullable) "?" else ""),
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
                MyMarker.Interface(
                    name = typeName,
                    fields = fields,
                    superMarkers = emptyList(),
                )
            )
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

                is OpenApiTypeResult.UsingRef -> MyMarker.MarkerAlias(
                    name = ValidFieldName.of(typeName).quotedIfNeeded,
                    superMarker = openApiTypeResult.marker,
                )

                is OpenApiTypeResult.Success -> {
                    var type = openApiTypeResult.openApiType.toFieldType(
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
                                it.fieldType.name
                        }
                    }

                    if (openApiTypeResult.openApiType is OpenApiType.Array) {
                        type = "List<$type>"
                    }

                    MyMarker.TypeAlias(
                        name = ValidFieldName.of(typeName).quotedIfNeeded,
                        superMarker = Marker(
                            name = type,

                            // all below is unused
                            isOpen = false,
                            fields = emptyList(),
                            superMarkers = emptyList(),
                            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
                            typeParameters = emptyList(),
                            typeArguments = emptyList(),
                        ),
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
): MyMarker.Enum {
    val enumName = ValidFieldName.of(name.snakeToUpperCamelCase())
    val enumMarker = MyMarker.Enum(
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

/**
 * Represents all types supported by OpenApi with functions to create a [FieldType] from each.
 */
private sealed class OpenApiType(val name: kotlin.String?) {

    object String : OpenApiType("string") {

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

    object Integer : OpenApiType("integer") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiIntegerFormat?): FieldType = FieldType.ValueFieldType(
            typeFqName = when (format) {
                null, OpenApiIntegerFormat.INT32 -> if (nullable) typeOf<Int?>() else typeOf<Int>()
                OpenApiIntegerFormat.INT64 -> if (nullable) typeOf<Long?>() else typeOf<Long>()
            }.toString(),
        )
    }

    object Number : OpenApiType("number") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiNumberFormat?): FieldType = FieldType.ValueFieldType(
            typeFqName = when (format) {
                null, OpenApiNumberFormat.FLOAT -> if (nullable) typeOf<Float?>() else typeOf<Float>()
                OpenApiNumberFormat.DOUBLE -> if (nullable) typeOf<Double?>() else typeOf<Double>()
            }.toString(),
        )
    }

    object Boolean : OpenApiType("boolean") {

        fun getType(nullable: kotlin.Boolean): FieldType = FieldType.ValueFieldType(
            typeFqName = if (nullable) {
                typeOf<kotlin.Boolean?>()
            } else {
                typeOf<kotlin.Boolean>()
            }.toString(),
        )
    }

    object Object : OpenApiType("object") {

        fun getType(marker: MyMarker): FieldType = FieldType.GroupFieldType(markerName = marker.name)
    }

    object Any : OpenApiType(null) {

        fun getType(nullable: kotlin.Boolean): FieldType = FieldType.ValueFieldType(
            typeFqName = if (nullable) {
                typeOf<kotlin.Any?>()
            } else {
                typeOf<kotlin.Any>()
            }.toString(),
        )
    }

    object Array : OpenApiType("array") {

        // used for lists of primitives
        fun getTypeAsList(nullable: kotlin.Boolean, typeFqName: kotlin.String): FieldType = FieldType.ValueFieldType(
            typeFqName = "${List::class.qualifiedName!!}<$typeFqName>${if (nullable) "?" else ""}",
        )

        fun getTypeAsFrame(nullable: kotlin.Boolean, markerName: kotlin.String): FieldType = FieldType.FrameFieldType(
            markerName = markerName,
            nullable = nullable,
        )
    }

    override fun toString(): kotlin.String = name.toString()

    companion object {

        val all: List<OpenApiType> = listOf(String, Integer, Number, Boolean, Object, Any)

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
    class UsingRef(val marker: MyMarker) : OpenApiTypeResult

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : OpenApiTypeResult

    /** Property is a schema with OpenApiType [openApiType]. */
    data class Success(val openApiType: OpenApiType, val nullable: Boolean) : OpenApiTypeResult

    /** Property is an enum with values [values]. */
    data class SuccessAsEnum(val values: List<String>, val nullable: Boolean) : OpenApiTypeResult
}

/**
 * Converts a single property of an OpenApi type schema to [OpenApiTypeResult].
 * It can become an [OpenApiType], [MyMarker] reference, enum, or unresolved reference.
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
        val anyOfRefs = anyOf.mapNotNull { it.`$ref` }
        val anyOfTypes = anyOf.mapNotNull { it.type }
            .mapNotNull(OpenApiType.Companion::fromStringOrNull)
            .distinct()

        openApiType = when {
            // only one type
            anyOfTypes.size == 1 && anyOfRefs.isEmpty() -> anyOfTypes.first()

            // just Number-like types
            anyOfTypes.size == 2 && anyOfRefs.isEmpty() && anyOfTypes.containsAll(
                listOf(OpenApiType.Number, OpenApiType.Integer)
            ) -> OpenApiType.Number

            // only one ref
            anyOfTypes.isEmpty() && anyOfRefs.size == 1 -> {
                val typeName = anyOfRefs.first().takeLastWhile { it != '/' }
                return when (val it = getRefMarker(typeName)) {
                    is MarkerResult.CannotFindRefMarker ->
                        OpenApiTypeResult.CannotFindRefMarker

                    is MarkerResult.Success ->
                        OpenApiTypeResult.UsingRef(it.marker)
                }
            }

            // more than one ref
            anyOfTypes.isEmpty() && anyOfRefs.isNotEmpty() -> {
                // TODO merge oneOf refs, can be enums
                println("TODO merge oneOf refs, can be enums")

                OpenApiType.Any
            }

            // cannot assume anything about a type when there are multiple types except one
            not != null -> OpenApiType.Any

            else -> OpenApiType.Any
        }
    }

    val nullable = nullable ?: !isRequired

    if (nullable && openApiType == OpenApiType.Object) {
        println(
            "Warning: type $name is marked nullable, but ColumnGroups cannot be null, so it will be interpreted as non-nullable."
        )
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

            is OpenApiType.Array ->
                if (schema.items == null) { // should in theory not occur, but make List<Any?> just in case
                    getTypeAsList(
                        nullable = nullable,
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
                            when (arrayTypeResult.marker) {
                                is MyMarker.Enum, is MyMarker.TypeAlias ->
                                    getTypeAsList(
                                        nullable = nullable,
                                        typeFqName = arrayTypeResult.marker.name,
                                    )

                                is MyMarker.Interface, is MyMarker.MarkerAlias ->
                                    getTypeAsFrame(
                                        nullable = nullable,
                                        markerName = arrayTypeResult.marker.name,
                                    )
                            }

                        is OpenApiTypeResult.Success -> {
                            val arrayTypeSchemaResult = arrayTypeResult.openApiType.toFieldType(
                                schema = schema.items!!,
                                schemaName = schemaName,
                                nullable = arrayTypeResult.nullable,
                                getRefMarker = getRefMarker,
                                produceAdditionalMarker = produceAdditionalMarker,
                                required = emptyList(),
                            )

                            when (arrayTypeSchemaResult) {
                                is FieldTypeResult.CannotFindRefMarker ->
                                    return FieldTypeResult.CannotFindRefMarker

                                is FieldTypeResult.Success ->
                                    getTypeAsList(
                                        nullable = nullable,
                                        typeFqName = when (val it = arrayTypeSchemaResult.fieldType) {
                                            is FieldType.FrameFieldType -> it.markerName
                                            is FieldType.GroupFieldType -> it.markerName
                                            is FieldType.ValueFieldType -> it.typeFqName
                                        }
                                    )
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
                                nullable = nullable,
                                typeFqName = enumMarker.name + if (enumMarker.nullable) "?" else "",
                            )
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

                        getType(dataFrameSchemaResult.marker.withName(newName))
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

internal val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
internal val snakeRegex = "_[a-zA-Z]".toRegex()

// String extensions
internal fun String.camelToSnakeCase(): String = camelRegex.replace(this) { "_${it.value}" }.lowercase()
internal fun String.snakeToLowerCamelCase(): String =
    snakeRegex.replace(this) { it.value.replace("_", "").uppercase() }

internal fun String.snakeToUpperCamelCase(): String = snakeToLowerCamelCase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

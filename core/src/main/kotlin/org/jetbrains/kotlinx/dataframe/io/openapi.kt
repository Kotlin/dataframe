package org.jetbrains.kotlinx.dataframe.io

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.codeGen.*
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.typeOf

public fun main() {
    val openAPI: OpenAPI = OpenAPIParser()
        .readContents(
            File("/data/Projects/dataframe 2/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/io/petstore.yaml").readText(),
            null,
            null
        )
        .also {
            if (it.messages != null) println("messages: ${it.messages.toList()}")
        }
        .openAPI ?: error("Failed to parse OpenAPI")

    val result = openAPI.components?.schemas?.toMap()
        ?.filter { it.value.type == "object" || it.value.allOf != null || it.value.enum != null }
        ?.toMarkers()
        ?.toList()
        ?: emptyList()

    val codeGenerator = CodeGenerator.create(useFqNames = false)

    for ((name, marker) in result) {
        println("$name:")
        println(marker.describe())
        println()
        println("generated code:")
        println(
            codeGenerator.generate(
                marker = marker,
                interfaceMode = InterfaceGenerationMode.WithFields,
                extensionProperties = true,
            ).declarations
        )
    }
}

private fun Marker.describe() =
    "Marker(name = $name, isOpen = $isOpen, fields = $fields, superMarkers = $superMarkers, visibility = $visibility, typeParameters = $typeParameters, typeArguments = $typeArguments)"

/**
 * Converts named OpenApi schemas to Markers with name.
 */
private fun Map<String, Schema<*>>.toMarkers(): Map<String, Marker> {
    val retrievableMarkers = mapValues { (typeName, value) ->
        RetrievableMarker { getRefMarker, produceAdditionalMarker ->
            value.toMarker(
                typeName = typeName,
                getRefMarker = getRefMarker,
                produceAdditionalMarker = produceAdditionalMarker,
            )
        }
    }.toMutableMap()

    val markers = mutableMapOf<String, Marker>()

    while (retrievableMarkers.isNotEmpty()) {
        val retrievedMarker = retrievableMarkers.entries.first { (name, retrieveMarker) ->
            val res = retrieveMarker(
                getRefMarker = { MarkerResult.fromNullable(markers[it]) },
                produceAdditionalMarker = markers::put,
            )

            if (res is MarkerResult.Success) {
                markers[name] = res.marker
                true
            } else false
        }.key
        retrievableMarkers -= retrievedMarker
    }

    return markers
}

private sealed interface MarkerResult {

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : MarkerResult

    /** Successfully found or created [marker]. */
    data class Success(val marker: Marker) : MarkerResult

    companion object {
        fun fromNullable(schema: Marker?): MarkerResult =
            if (schema == null) CannotFindRefMarker else Success(schema)
    }
}

private typealias GetRefMarker = (String) -> MarkerResult

private typealias ProduceAdditionalMarker = (String, Marker) -> Unit

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
        produceAdditionalMarker: ProduceAdditionalMarker,
    ): MarkerResult
}

private class BaseFieldImpl(
    override val fieldName: ValidFieldName,
    override val columnName: String,
    override val fieldType: FieldType
) : BaseField

/** Converts a single OpenApi object type schema to a [Marker] if successful. */
private fun Schema<*>.toMarker(
    typeName: String,
    getRefMarker: GetRefMarker,
    produceAdditionalMarker: ProduceAdditionalMarker,
): MarkerResult {
    require(type == "object" || allOf != null || enum != null) { "Only object-, allOf, or enum types can be converted to a DataFrameSchema" }

    return when {
        allOf != null -> TODO()
        enum != null -> TODO()
        type == "object" -> {
            val fields = buildList<BaseField> {
                for ((name, property) in (properties ?: emptyMap())) {
                    val openApiTypeResult = property.toOpenApiType(
                        isRequired = required?.contains(name) == true,
                        getRefMarker = getRefMarker,
                    )

                    val (openApiType, nullable) = when (openApiTypeResult) {
                        is OpenApiTypeResult.CannotFindRefMarker ->
                            return MarkerResult.CannotFindRefMarker

                        is OpenApiTypeResult.UsingRef -> {
                            val validName = ValidFieldName.of(name)

                            this += BaseFieldImpl(
                                fieldName = validName,
                                columnName = validName.unquoted,
                                fieldType = FieldType.GroupFieldType(
                                    markerName = openApiTypeResult.marker.name,
                                ),
                            )
                            continue
                        }

                        is OpenApiTypeResult.SuccessAsEnum -> TODO()

                        is OpenApiTypeResult.Success ->
                            openApiTypeResult
                    }
                    val fieldTypeResult = openApiType
                        .toFieldType(
                            property = property,
                            propertyName = name,
                            nullable = nullable,
                            getRefMarker = getRefMarker,
                            produceAdditionalMarker = produceAdditionalMarker,
                        )
                }
            }

            MarkerResult.Success(
                Marker(
                    name = typeName,
                    isOpen = false,
                    fields = fields,
                    superMarkers = emptyList(),
                    visibility = MarkerVisibility.EXPLICIT_PUBLIC,
                    typeParameters = emptyList(),
                    typeArguments = emptyList(),
                )
            )
        }

        else -> {
            TODO("other types like array can also occur, typealias??")
        }
    }
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
 * Represents all types supported by OpenApi with functions to create a [ColumnSchema] from each.
 */
private sealed class OpenApiType(val name: kotlin.String?) {

    object String : OpenApiType("string") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiStringFormat?): FieldType =
            FieldType.ValueFieldType(
                when (format) {
                    OpenApiStringFormat.DATE -> if (nullable) typeOf<LocalDate?>() else typeOf<LocalDate>()
                    OpenApiStringFormat.DATE_TIME -> if (nullable) typeOf<LocalDateTime?>() else typeOf<LocalDateTime>()
                    OpenApiStringFormat.PASSWORD -> if (nullable) typeOf<kotlin.String?>() else typeOf<kotlin.String>()
                    OpenApiStringFormat.BYTE -> if (nullable) typeOf<Byte?>() else typeOf<Byte>()
                    OpenApiStringFormat.BINARY -> if (nullable) typeOf<ByteArray?>() else typeOf<ByteArray>()
                    null -> if (nullable) typeOf<kotlin.String?>() else typeOf<kotlin.String>()
                }.toString()
            )
    }

    object Integer : OpenApiType("integer") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiIntegerFormat?): FieldType =
            FieldType.ValueFieldType(
                when (format) {
                    null, OpenApiIntegerFormat.INT32 -> if (nullable) typeOf<Int?>() else typeOf<Int>()
                    OpenApiIntegerFormat.INT64 -> if (nullable) typeOf<Long?>() else typeOf<Long>()
                }.toString()
            )
    }

    object Number : OpenApiType("number") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiNumberFormat?): FieldType =
            FieldType.ValueFieldType(
                when (format) {
                    null, OpenApiNumberFormat.FLOAT -> if (nullable) typeOf<Float?>() else typeOf<Float>()
                    OpenApiNumberFormat.DOUBLE -> if (nullable) typeOf<Double?>() else typeOf<Double>()
                }.toString()
            )
    }

    object Boolean : OpenApiType("boolean") {

        fun getType(nullable: kotlin.Boolean): FieldType =
            FieldType.ValueFieldType(
                (if (nullable) typeOf<kotlin.Boolean?>() else typeOf<kotlin.Boolean>()).toString()
            )
    }

    object Object : OpenApiType("object") {

        fun getType(marker: Marker): FieldType =
            FieldType.GroupFieldType(
                markerName = marker.name,
            )
    }

    object Any : OpenApiType(null) {

        fun getType(nullable: kotlin.Boolean): FieldType =
            FieldType.ValueFieldType(
                (if (nullable) typeOf<kotlin.Any?>() else typeOf<kotlin.Any>()).toString()
            )
    }

    object Array : OpenApiType("array") {

        // used for lists of primitives
        fun getTypeAsList(nullable: kotlin.Boolean, typeFqName: kotlin.String): FieldType =
            FieldType.ValueFieldType(
                "${List::class.qualifiedName!!}<${typeFqName.removeSuffix("?")}${if (nullable) "?" else "" + ">"}",
            )

        fun getTypeAsFrame(nullable: kotlin.Boolean, markerName: kotlin.String): FieldType =
            FieldType.FrameFieldType(
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

    /** Property is a reference with name [name] and ColumnSchema [columnSchema]. */
    class UsingRef(val marker: Marker) : OpenApiTypeResult

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : OpenApiTypeResult

    /** Property is a schema with OpenApiType [openApiType]. */
    data class Success(val openApiType: OpenApiType, val nullable: Boolean) : OpenApiTypeResult

    /** Property is an enum with OpenApiType [openApiType]. */
    class SuccessAsEnum() : OpenApiTypeResult
}

/**  Converts a single property of an OpenApi type schema to [OpenApiType] */
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
        val openApiType = OpenApiType.fromStringOrNull(type)!!

        return TODO()
    }

    var openApiType = OpenApiType.fromStringOrNull(type)

    if (openApiType == null) { // check for anyOf/oneOf/not
        val anyOf = ((anyOf ?: emptyList()) + (oneOf ?: emptyList()))
        val anyOfRefs = anyOf.mapNotNull { it.`$ref` }
        val anyOfTypes = anyOf.mapNotNull { it.type }
            .mapNotNull(OpenApiType.Companion::fromStringOrNull)
            .distinct()

        openApiType = when {
            anyOfTypes.size == 1 && anyOfRefs.isEmpty() -> anyOfTypes.first()

            anyOfTypes.size == 2 && anyOfRefs.isEmpty() && anyOfTypes.containsAll(
                listOf(OpenApiType.Number, OpenApiType.Integer)
            ) -> OpenApiType.Number

            anyOfTypes.isEmpty() && anyOfRefs.size == 1 -> {
                val typeName = anyOfRefs.first().takeLastWhile { it != '/' }
                return when (val it = getRefMarker(typeName)) {
                    is MarkerResult.CannotFindRefMarker ->
                        OpenApiTypeResult.CannotFindRefMarker

                    is MarkerResult.Success ->
                        OpenApiTypeResult.UsingRef(it.marker)
                }
            }
            // cannot assume anything about a type when there are multiple types except one
            not != null -> OpenApiType.Any

            else -> OpenApiType.Any
        }
    }

    val nullable = nullable ?: !isRequired

    if (nullable && openApiType == OpenApiType.Object) {
        println("Warning: type $name is marked nullable, but ColumnGroups cannot be null.")
    }

    return OpenApiTypeResult.Success(openApiType, nullable)
}

private sealed interface FieldTypeResult {

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : FieldTypeResult

    /** ColumnSchema [fieldType] created successfully. */
    data class Success(val fieldType: FieldType) : FieldTypeResult
}

/** Converts an OpenApi type schema to a [FieldType] if successful. */
private fun OpenApiType.toFieldType(
    property: Schema<*>,
    propertyName: String,
    nullable: Boolean,
    getRefMarker: GetRefMarker,
    produceAdditionalMarker: ProduceAdditionalMarker,
): FieldTypeResult {
    return FieldTypeResult.Success(
        when (this) {
            is OpenApiType.Any -> getType(nullable)

            is OpenApiType.Array ->
                if (property.items == null) { // should in theory not occur, but make List<Any?> just in case
                    getTypeAsList(
                        nullable = nullable,
                        typeFqName = (OpenApiType.Any.getType(nullable = true) as FieldType.ValueFieldType).typeFqName,
                    )
                } else { // Try to get the array type
                    val arrayTypeResult = property.items!!.toOpenApiType(
                        isRequired = true,
                        getRefMarker = getRefMarker,
                    )

                    when (arrayTypeResult) {
                        is OpenApiTypeResult.CannotFindRefMarker ->
                            return FieldTypeResult.CannotFindRefMarker

                        is OpenApiTypeResult.UsingRef ->
                            getTypeAsFrame(
                                nullable = nullable,
                                markerName = arrayTypeResult.marker.name,
                            )

                        is OpenApiTypeResult.Success -> {
                            val arrayTypeSchemaResult = arrayTypeResult.openApiType.toFieldType(
                                property = property.items!!,
                                propertyName = propertyName,
                                nullable = arrayTypeResult.nullable,
                                getRefMarker = getRefMarker,
                                produceAdditionalMarker = produceAdditionalMarker,
                            )

                            when (arrayTypeSchemaResult) {
                                is FieldTypeResult.CannotFindRefMarker ->
                                    return FieldTypeResult.CannotFindRefMarker

                                is FieldTypeResult.Success ->
                                    getTypeAsList(
                                        nullable = nullable,
                                        typeFqName = when (val it = arrayTypeSchemaResult.fieldType) {
                                            is FieldType.FrameFieldType -> it.markerName // TODO fq name
                                            is FieldType.GroupFieldType -> it.markerName // TODO fq name
                                            is FieldType.ValueFieldType -> it.typeFqName
                                        },
                                    )
                            }
                        }

                        is OpenApiTypeResult.SuccessAsEnum -> TODO()
                    }
                }

            is OpenApiType.Boolean -> getType(nullable)

            is OpenApiType.Integer -> getType(
                nullable = nullable,
                format = OpenApiIntegerFormat.fromStringOrNull(property.format),
            )

            is OpenApiType.Number -> getType(
                nullable = nullable,
                format = OpenApiNumberFormat.fromStringOrNull(property.format),
            )

            is OpenApiType.Object -> {
                val dataFrameSchemaResult = property.toMarker(
                    typeName = propertyName.snakeToUpperCamelCase(),
                    getRefMarker = getRefMarker,
                    produceAdditionalMarker = produceAdditionalMarker,
                )

                when (dataFrameSchemaResult) {
                    is MarkerResult.CannotFindRefMarker ->
                        return FieldTypeResult.CannotFindRefMarker

                    is MarkerResult.Success -> {
                        produceAdditionalMarker(
                            propertyName.snakeToUpperCamelCase(),
                            dataFrameSchemaResult.marker,
                        )

                        getType(dataFrameSchemaResult.marker)
                    }
                }
            }

            is OpenApiType.String -> getType(
                nullable = nullable,
                format = OpenApiStringFormat.fromStringOrNull(property.format),
            )
        }
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

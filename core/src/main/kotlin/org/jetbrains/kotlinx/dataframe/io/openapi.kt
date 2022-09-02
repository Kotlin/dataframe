package org.jetbrains.kotlinx.dataframe.io // ktlint-disable filename

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.GeneratedField
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.typeOf

public fun main() {
    val openAPI: OpenAPI = OpenAPIParser()
        .readContents(
            File(
                "/data/Projects/dataframe 2/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/io/petstore-expanded.yaml"
            ).readText(),
            null,
            null
        )
        .also {
            if (it.messages != null) println("messages: ${it.messages.toList()}")
        }
        .openAPI ?: error("Failed to parse OpenAPI")

    val result = openAPI.components?.schemas?.toMap()
//        ?.filter { it.value.type == "object" || it.value.allOf != null || it.value.enum != null }
        ?.toMarkers()
        ?.toList()
        ?: emptyList()

    val codeGenerator = CodeGenerator.create(useFqNames = false)

    for ((name, marker) in result) {
//        println("$name:")
//        println(marker)
//        println()
//        println("generated code:")
        println(
            codeGenerator.generate(
                marker = marker,
                interfaceMode = when (marker.markerType) {
                    MarkerType.ENUM -> InterfaceGenerationMode.Enum
                    MarkerType.INTERFACE -> InterfaceGenerationMode.WithFields
                    MarkerType.TYPE_ALIAS -> InterfaceGenerationMode.TypeAlias
                },
                extensionProperties = false,
            ).declarations
        )
    }
}

private enum class MarkerType {
    ENUM, INTERFACE, TYPE_ALIAS
}

/** Local marker helper class. */
private class MyMarker(
    val markerType: MarkerType,
    name: String,
    fields: List<GeneratedField>,
    superMarkers: List<Marker>,
    visibility: MarkerVisibility,
) : Marker(
    name = name,
    isOpen = false,
    fields = fields,
    superMarkers = superMarkers,
    visibility = visibility,
    typeParameters = emptyList(),
    typeArguments = emptyList(),
) {
    fun withName(name: String) = MyMarker(markerType, name, fields, superMarkers.values.toList(), visibility)

    override fun toString(): String =
        "MyMarker(markerType = $markerType, name = $name, isOpen = $isOpen, fields = $fields, superMarkers = $superMarkers, visibility = $visibility, typeParameters = $typeParameters, typeArguments = $typeArguments)"
}

/**
 * Converts named OpenApi schemas to Markers with name.
 */
private fun Map<String, Schema<*>>.toMarkers(): Map<String, MyMarker> {
    val retrievableMarkers = mapValues { (typeName, value) ->
        RetrievableMarker { getRefMarker, produceAdditionalMarker ->
            value.toMarker(
                typeName = typeName,
                getRefMarker = getRefMarker,
                produceAdditionalMarker = produceAdditionalMarker
            )
        }
    }.toMutableMap()

    val markers = mutableMapOf<String, MyMarker>()

    while (retrievableMarkers.isNotEmpty()) {
        val retrievedMarker = retrievableMarkers.entries.first { (name, retrieveMarker) ->
            val res = retrieveMarker(
                getRefMarker = { MarkerResult.fromNullable(markers[it]) },
                produceAdditionalMarker = { validName, marker ->
                    var result = ValidFieldName.of(validName.unquoted)
                    val baseName = result
                    var attempt = 1
                    while (result.quotedIfNeeded in markers) {
                        result = if (result.needsQuote) baseName + ValidFieldName.of(" ($attempt)") else baseName + ValidFieldName.of("$attempt")
                        attempt++
                    }

                    markers[result.quotedIfNeeded] = marker.withName(result.quotedIfNeeded)
                    result.quotedIfNeeded
                }
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
    data class Success(val marker: MyMarker) : MarkerResult

    companion object {
        fun fromNullable(schema: MyMarker?): MarkerResult =
            if (schema == null) CannotFindRefMarker else Success(schema)
    }
}

private typealias GetRefMarker = (String) -> MarkerResult

private typealias ProduceAdditionalMarker = (ValidFieldName, MyMarker) -> String

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
    fieldType: FieldType
) = GeneratedField(
    fieldName = fieldName,
    columnName = columnName,
    overrides = overrides,
    columnSchema = ColumnSchema.Value(typeOf<Any?>()), // unused
    fieldType = fieldType,
)

private fun generatedEnumFieldOf(
    fieldName: ValidFieldName,
    columnName: String,
) = generatedFieldOf(
    fieldName = fieldName,
    columnName = columnName,
    overrides = false,
    fieldType = FieldType.ValueFieldType(typeOf<String>().toString()),
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

/** Converts a single OpenApi object type schema to a [Marker] if successful. */
private fun Schema<*>.toMarker(
    typeName: String,
    getRefMarker: GetRefMarker,
    produceAdditionalMarker: ProduceAdditionalMarker
): MarkerResult {
//    require(type == "object" || allOf != null || enum != null) {
//        "Only object-, allOf, or enum types can be converted to a DataFrameSchema"
//    }

    return when {
        allOf != null -> {
            val allOfSchemas = allOf!!.associateWith {
                it.toOpenApiType(isRequired = true, getRefMarker = getRefMarker)
            }

            val requiredFields = allOfSchemas.keys.flatMap { it.required ?: emptyList() }.distinct()

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

                        var tempMarker: MyMarker? = null

                        val fieldTypeResult = openApiType.toFieldType(
                            property = schema,
                            propertyName = typeName,
                            nullable = nullable,
                            getRefMarker = getRefMarker,
                            produceAdditionalMarker = { name, marker ->
                                tempMarker = marker
                                name.quotedIfNeeded
                            },
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
                MyMarker(
                    markerType = MarkerType.INTERFACE,
                    name = typeName,
                    fields = fields,
                    superMarkers = superMarkers,
                    visibility = MarkerVisibility.EXPLICIT_PUBLIC,
                )
            )
        }

        enum != null -> {
            val openApiTypeResult = this.toOpenApiType(
                isRequired = required?.contains(name) == true,
                getRefMarker = getRefMarker,
            ) as OpenApiTypeResult.SuccessAsEnum // must be an enum

            val enumMarker = produceNewEnum(
                name = typeName,
                values = openApiTypeResult.values,
                produceAdditionalMarker = produceAdditionalMarker,
            )

            MarkerResult.Success(enumMarker)
        }

        type == "object" -> {
            val fields = buildList {
                for ((name, property) in (properties ?: emptyMap())) {
                    val isRequired = required?.contains(name) == true
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
                                fieldType = if (openApiTypeResult.marker.markerType != MarkerType.INTERFACE) {
                                    FieldType.ValueFieldType(
                                        typeFqName = openApiTypeResult.marker.name + if (isRequired) "" else "?",
                                    )
                                } else {
                                    FieldType.GroupFieldType(
                                        markerName = openApiTypeResult.marker.name
                                    )
                                }
                            )
                        }

                        is OpenApiTypeResult.SuccessAsEnum -> {
                            val enumMarker = produceNewEnum(
                                name = name,
                                values = openApiTypeResult.values,
                                produceAdditionalMarker = produceAdditionalMarker,
                            )

                            this += generatedFieldOf(
                                overrides = false,
                                fieldName = ValidFieldName.of(name.snakeToLowerCamelCase()),
                                columnName = name,
                                fieldType = FieldType.ValueFieldType(
                                    typeFqName = enumMarker.name,
                                ),
                            )
                        }

                        is OpenApiTypeResult.Success -> {
                            val (openApiType, nullable) = openApiTypeResult

                            val fieldTypeResult = openApiType
                                .toFieldType(
                                    property = property,
                                    propertyName = name,
                                    nullable = nullable,
                                    getRefMarker = getRefMarker,
                                    produceAdditionalMarker = produceAdditionalMarker
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
                                        fieldType = fieldTypeResult.fieldType
                                    )
                                }
                            }
                        }
                    }
                }
            }

            MarkerResult.Success(
                MyMarker(
                    markerType = MarkerType.INTERFACE,
                    name = typeName,
                    fields = fields,
                    superMarkers = emptyList(),
                    visibility = MarkerVisibility.EXPLICIT_PUBLIC,
                )
            )
        }

        else -> {
            val openApiTypeResult = this.toOpenApiType(
                isRequired = true,
                getRefMarker = getRefMarker,
            )

            val typeAliasMarker = when (openApiTypeResult) {
                is OpenApiTypeResult.CannotFindRefMarker ->
                    return MarkerResult.CannotFindRefMarker

                is OpenApiTypeResult.UsingRef -> MyMarker(
                    markerType = MarkerType.TYPE_ALIAS,
                    name = ValidFieldName.of(typeName).quotedIfNeeded,
                    fields = emptyList(),
                    superMarkers = listOf(openApiTypeResult.marker),
                    visibility = MarkerVisibility.EXPLICIT_PUBLIC,
                )

                is OpenApiTypeResult.Success -> MyMarker(
                    markerType = MarkerType.TYPE_ALIAS,
                    name = ValidFieldName.of(typeName).quotedIfNeeded,
                    fields = emptyList(),
                    superMarkers = listOf(
                        Marker(
                            name = openApiTypeResult.openApiType.toFieldType(
                                property = this,
                                propertyName = typeName,
                                nullable = false,
                                getRefMarker = getRefMarker,
                                produceAdditionalMarker = produceAdditionalMarker,
                            ).let {
                                when (it) {
                                    FieldTypeResult.CannotFindRefMarker ->
                                        return MarkerResult.CannotFindRefMarker

                                    is FieldTypeResult.Success ->
                                        it.fieldType.name
                                }
                            },

                            // all below is unused
                            isOpen = false,
                            fields = emptyList(),
                            superMarkers = emptyList(),
                            visibility = MarkerVisibility.EXPLICIT_PUBLIC,
                            typeParameters = emptyList(),
                            typeArguments = emptyList(),
                        )
                    ),
                    visibility = MarkerVisibility.EXPLICIT_PUBLIC,
                )

                is OpenApiTypeResult.SuccessAsEnum -> error("cannot happen, since enum != null is checked earlier")
            }

            MarkerResult.Success(typeAliasMarker)
        }
    }
}

private fun produceNewEnum(
    name: String,
    values: List<String>,
    produceAdditionalMarker: ProduceAdditionalMarker,
): MyMarker {
    val enumName = ValidFieldName.of(name.snakeToUpperCamelCase())
    val enumMarker = MyMarker(
        markerType = MarkerType.ENUM,
        name = enumName.quotedIfNeeded,
        fields = values.map {
            generatedEnumFieldOf(
                fieldName = ValidFieldName.of(it),
                columnName = it,
            )
        },
        superMarkers = emptyList(),
        visibility = MarkerVisibility.EXPLICIT_PUBLIC,
    )
    val newName = produceAdditionalMarker(enumName, enumMarker)

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

        fun getType(marker: MyMarker): FieldType =
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
                "${List::class.qualifiedName!!}<$typeFqName>${if (nullable) "?" else ""}"
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
    class UsingRef(val marker: MyMarker) : OpenApiTypeResult

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : OpenApiTypeResult

    /** Property is a schema with OpenApiType [openApiType]. */
    data class Success(val openApiType: OpenApiType, val nullable: Boolean) : OpenApiTypeResult

    /** Property is an enum with OpenApiType [openApiType]. */
    data class SuccessAsEnum(val values: List<String>, val nullable: Boolean) : OpenApiTypeResult
}

/**  Converts a single property of an OpenApi type schema to [OpenApiType] */
private fun Schema<*>.toOpenApiType(
    isRequired: Boolean,
    getRefMarker: GetRefMarker
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

        return OpenApiTypeResult.SuccessAsEnum(enum.filterNotNull().map { it.toString() }, nullable)
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

            anyOfTypes.isEmpty() && anyOfRefs.isNotEmpty() -> {
                // TODO
                println("TODO merge oneOf refs")

                OpenApiType.Any
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
    produceAdditionalMarker: ProduceAdditionalMarker
): FieldTypeResult {
    return FieldTypeResult.Success(
        when (this) {
            is OpenApiType.Any -> getType(nullable)

            is OpenApiType.Array ->
                if (property.items == null) { // should in theory not occur, but make List<Any?> just in case
                    getTypeAsList(
                        nullable = nullable,
                        typeFqName = (OpenApiType.Any.getType(nullable = true) as FieldType.ValueFieldType).typeFqName
                    )
                } else { // Try to get the array type TODO decide whether to make nullable or not
                    val arrayTypeResult = property.items!!
                        .toOpenApiType(
                            isRequired = true,
                            getRefMarker = getRefMarker,
                        )

                    when (arrayTypeResult) {
                        is OpenApiTypeResult.CannotFindRefMarker ->
                            return FieldTypeResult.CannotFindRefMarker

                        is OpenApiTypeResult.UsingRef ->
                            if (arrayTypeResult.marker.markerType != MarkerType.INTERFACE) {
                                getTypeAsList(
                                    nullable = nullable,
                                    typeFqName = arrayTypeResult.marker.name,
                                )
                            } else {
                                getTypeAsFrame(
                                    nullable = nullable,
                                    markerName = arrayTypeResult.marker.name,
                                )
                            }

                        is OpenApiTypeResult.Success -> {
                            val arrayTypeSchemaResult = arrayTypeResult.openApiType.toFieldType(
                                property = property.items!!,
                                propertyName = propertyName,
                                nullable = arrayTypeResult.nullable,
                                getRefMarker = getRefMarker,
                                produceAdditionalMarker = produceAdditionalMarker
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
                                name = propertyName,
                                values = arrayTypeResult.values,
                                produceAdditionalMarker = produceAdditionalMarker,
                            )

                            getTypeAsList(
                                nullable = nullable,
                                typeFqName = enumMarker.name,
                            )
                        }
                    }
                }

            is OpenApiType.Boolean -> getType(nullable)

            is OpenApiType.Integer -> getType(
                nullable = nullable,
                format = OpenApiIntegerFormat.fromStringOrNull(property.format)
            )

            is OpenApiType.Number -> getType(
                nullable = nullable,
                format = OpenApiNumberFormat.fromStringOrNull(property.format)
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
                        val newName = produceAdditionalMarker(
                            ValidFieldName.of(propertyName.snakeToUpperCamelCase()),
                            dataFrameSchemaResult.marker,
                        )

                        getType(dataFrameSchemaResult.marker.withName(newName))
                    }
                }
            }

            is OpenApiType.String -> getType(
                nullable = nullable,
                format = OpenApiStringFormat.fromStringOrNull(property.format)
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

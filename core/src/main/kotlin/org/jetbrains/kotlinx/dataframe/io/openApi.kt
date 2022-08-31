package org.jetbrains.kotlinx.dataframe.io

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.id
import org.jetbrains.kotlinx.dataframe.impl.createTypeWithArgument
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Any.createSchema
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Array.createSchemaAsFrame
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Array.createSchemaAsList
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Boolean.createSchema
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Integer.createSchema
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Number.createSchema
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.Object.createSchema
import org.jetbrains.kotlinx.dataframe.io.OpenApiType.String.createSchema
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public fun main() {
    val openAPI: OpenAPI = OpenAPIParser()
        .readContents(
            File("/data/Projects/dataframe/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/io/petstore-expanded.yaml").readText(),
            null,
            null
        )
        .also {
            if (it.messages != null) println("messages: ${it.messages.toList()}")
        }
        .openAPI ?: error("Failed to parse OpenAPI")

    val result = openAPI.components?.schemas?.toMap()
        ?.filter { it.value.type == "object" || it.value.allOf != null || it.value.enum != null }
        ?.toDataFrameSchemas()
        ?.toList()
        ?: emptyList()

    val codeGenerator = CodeGenerator.create(useFqNames = false)
    val knownMarkers = mutableListOf<Marker>()

    for ((name, dataFrameSchema) in result) {
//        println("$name:")
//        println(dataFrameSchema)
//        println()
//        println("generated code:")
        println(
            codeGenerator.generate(
                schema = dataFrameSchema,
                name = name,
                fields = true,
                extensionProperties = true,
                isOpen = false,
                knownMarkers = knownMarkers,
                readDfMethod = null,
                fieldNameNormalizer = NameNormalizer.id(),
            ).also {
                knownMarkers += it.newMarkers
            }.code.declarations
        )
    }
}

/**
 * Converts named OpenApi schemas to DataFrameSchemas with name.
 */
private fun Map<String, Schema<*>>.toDataFrameSchemas(): Map<String, DataFrameSchema> {
    val retrievableColumnSchemas = mapValues { (typeName, value) ->
        RetrievableDataFrameSchema { getRefSchema, produceAdditionalSchema ->
            value.toDataFrameSchema(
                typeName = typeName,
                getRefSchema = getRefSchema,
                produceAdditionalSchema = produceAdditionalSchema
            )
        }
    }.toMutableMap()

    val dataFrames = mutableMapOf<String, DataFrameSchema>()

    while (retrievableColumnSchemas.isNotEmpty()) {
        val retrievedColumn = retrievableColumnSchemas.entries.first { (name, columnSchemas) ->
            val res = columnSchemas(
                getRefSchema = { DataFrameSchemaResult.fromNullable(dataFrames[it]) },
                produceAdditionalSchema = dataFrames::put,
            )

            if (res is DataFrameSchemaResult.Success) {
                dataFrames[name] = res.dataFrameSchema
                true
            } else false
        }.key
        retrievableColumnSchemas -= retrievedColumn
    }

    return dataFrames
}

private sealed interface DataFrameSchemaResult {

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefSchema : DataFrameSchemaResult

    /** Successfully found or created [dataFrameSchema]. */
    data class Success(val dataFrameSchema: DataFrameSchema) : DataFrameSchemaResult

    companion object {
        fun fromNullable(schema: DataFrameSchema?): DataFrameSchemaResult =
            if (schema == null) CannotFindRefSchema else Success(schema)
    }
}

/** A function that returns a [DataFrameSchema] for a given reference name if successful. */
private typealias GetRefSchema = (String) -> DataFrameSchemaResult

/** A function that produces an additional [DataFrameSchema] for a given name.
 * This is used for `object` types not present in the root of `components/schemas`.
 */
private typealias ProduceAdditionalSchema = (String, DataFrameSchema) -> Unit

/**
 * Represents a call to [toDataFrameSchema] that can be repeated until it returns a [DataFrameSchemaResult.Success].
 */
private fun interface RetrievableDataFrameSchema {

    /**
     * Represents a call to [toDataFrameSchema] that can be repeated until it returns a [DataFrameSchemaResult.Success].
     *
     * @param getRefSchema              A function that returns a [DataFrameSchema] for a given reference name if successful.
     * @param produceAdditionalSchema   A function that produces an additional [DataFrameSchema] for a given name.
     *                                  This is used for `object` types not present in the root of `components/schemas`.
     *
     * @return A [DataFrameSchemaResult.Success] if successful, otherwise [DataFrameSchemaResult.CannotFindRefSchema].
     */
    operator fun invoke(
        getRefSchema: GetRefSchema,
        produceAdditionalSchema: ProduceAdditionalSchema,
    ): DataFrameSchemaResult
}

/**
 * Converts a single OpenApi object type schema to a [DataFrameSchema] if successful.
 *
 * @receiver The object type schema to convert.
 * @param typeName                  The name of the object type schema. Only used in allOf schemas.
 * @param getRefSchema              A function that returns a [DataFrameSchema] for a given reference name if successful.
 * @param produceAdditionalSchema   A function that produces an additional [DataFrameSchema] for a given name.
 *                                  This is used for `object` types not present in the root of `components/schemas`.
 *
 * @return A [DataFrameSchemaResult.Success] if successful, otherwise [DataFrameSchemaResult.CannotFindRefSchema].
 */
private fun Schema<*>.toDataFrameSchema(
    typeName: String,
    getRefSchema: GetRefSchema,
    produceAdditionalSchema: ProduceAdditionalSchema,
): DataFrameSchemaResult {
    require(type == "object" || allOf != null || enum != null) { "Only object-, allOf, or enum types can be converted to a DataFrameSchema" }

//    if (additionalProperties != false) {
//        println("An object does not have `additionalProperties == false` and thus might have extra fields that will not be encoded. additionalProperties: $additionalProperties")
//    }

    val columns: Map<String, ColumnSchema> = buildMap {
        when {
            allOf != null -> {
                val allOfSchemas = allOf!!.map {
                    it to it.toOpenApiType(true, getRefSchema)
                }

                for ((schema, openApiTypeResult) in allOfSchemas) {
                    val (openApiType, nullable) = when (openApiTypeResult) {
                        is OpenApiTypeResult.CannotFindRefSchema ->
                            return DataFrameSchemaResult.CannotFindRefSchema

                        is OpenApiTypeResult.UsingRef -> {
                            this += (openApiTypeResult.columnSchema as ColumnSchema.Group)
                                .schema
                                .columns

                            continue
                        }

                        is OpenApiTypeResult.Success ->
                            openApiTypeResult

                        is OpenApiTypeResult.SuccessAsEnum -> TODO()
                    }

                    // must be an object
                    openApiType as OpenApiType.Object

                    val columnSchemaResult = openApiType.toColumnSchema(
                        property = schema,
                        propertyName = typeName,
                        nullable = nullable,
                        getRefSchema = getRefSchema,
                        produceAdditionalSchema = produceAdditionalSchema,
                    )

                    when (columnSchemaResult) {
                        is ColumnSchemaResult.CannotFindRefSchema ->
                            return DataFrameSchemaResult.CannotFindRefSchema

                        is ColumnSchemaResult.Success ->
                            this += (columnSchemaResult.columnSchema as ColumnSchema.Group)
                                .schema
                                .columns
                    }
                }
            }

//            enum != null -> {
//                when(val res = toOpenApiType(true, getRefSchema)) {
//                    is OpenApiTypeResult.SuccessAsEnum -> {
//                        TODO()
//                    }
//                    else -> error("Malformed enum type: $typeName")
//                }
//            }

            else -> {
                for ((name, property) in (properties ?: emptyMap())) {
                    val openApiTypeResult = property.toOpenApiType(
                        isRequired = required?.contains(name) == true,
                        getRefSchema = getRefSchema,
                    )

                    val (openApiType, nullable) = when (openApiTypeResult) {
                        is OpenApiTypeResult.CannotFindRefSchema ->
                            return DataFrameSchemaResult.CannotFindRefSchema

                        is OpenApiTypeResult.UsingRef -> {
                            this[name] = openApiTypeResult.columnSchema
                            continue
                        }

                        is OpenApiTypeResult.Success ->
                            openApiTypeResult

                        is OpenApiTypeResult.SuccessAsEnum -> TODO()
                    }

                    val columnSchemaResult = openApiType.toColumnSchema(
                        property = property,
                        propertyName = name,
                        nullable = nullable,
                        getRefSchema = getRefSchema,
                        produceAdditionalSchema = produceAdditionalSchema,
                    )

                    when (columnSchemaResult) {
                        is ColumnSchemaResult.CannotFindRefSchema ->
                            return DataFrameSchemaResult.CannotFindRefSchema

                        is ColumnSchemaResult.Success ->
                            this[name] = columnSchemaResult.columnSchema
                    }
                }
            }
        }
    }

    return DataFrameSchemaResult.Success(
        DataFrameSchemaImpl(columns)
    )
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

        fun getType(nullable: kotlin.Boolean, format: OpenApiStringFormat?): KType =
            when (format) {
                OpenApiStringFormat.DATE -> if (nullable) typeOf<LocalDate?>() else typeOf<LocalDate>()
                OpenApiStringFormat.DATE_TIME -> if (nullable) typeOf<LocalDateTime?>() else typeOf<LocalDateTime>()
                OpenApiStringFormat.PASSWORD -> if (nullable) typeOf<kotlin.String?>() else typeOf<kotlin.String>()
                OpenApiStringFormat.BYTE -> if (nullable) typeOf<Byte?>() else typeOf<Byte>()
                OpenApiStringFormat.BINARY -> if (nullable) typeOf<ByteArray?>() else typeOf<ByteArray>()
                null -> if (nullable) typeOf<kotlin.String?>() else typeOf<kotlin.String>()
            }

        fun createSchema(nullable: kotlin.Boolean, format: OpenApiStringFormat?): ColumnSchema =
            ColumnSchema.Value(
                type = getType(nullable, format),
            )
    }

    object Integer : OpenApiType("integer") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiIntegerFormat?): KType =
            when (format) {
                null, OpenApiIntegerFormat.INT32 -> if (nullable) typeOf<Int?>() else typeOf<Int>()
                OpenApiIntegerFormat.INT64 -> if (nullable) typeOf<Long?>() else typeOf<Long>()
            }

        fun createSchema(nullable: kotlin.Boolean, format: OpenApiIntegerFormat?): ColumnSchema =
            ColumnSchema.Value(
                type = getType(nullable, format),
            )
    }

    object Number : OpenApiType("number") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiNumberFormat?): KType =
            when (format) {
                null, OpenApiNumberFormat.FLOAT -> if (nullable) typeOf<Float?>() else typeOf<Float>()
                OpenApiNumberFormat.DOUBLE -> if (nullable) typeOf<Double?>() else typeOf<Double>()
            }

        fun createSchema(nullable: kotlin.Boolean, format: OpenApiNumberFormat?): ColumnSchema =
            ColumnSchema.Value(
                type = getType(nullable, format),
            )
    }

    object Boolean : OpenApiType("boolean") {

        fun getType(nullable: kotlin.Boolean): KType =
            if (nullable) typeOf<kotlin.Boolean?>() else typeOf<kotlin.Boolean>()

        fun createSchema(nullable: kotlin.Boolean): ColumnSchema = ColumnSchema.Value(
            type = getType(nullable),
        )
    }

    object Object : OpenApiType("object") {
        fun createSchema(schema: DataFrameSchema): ColumnSchema =
            ColumnSchema.Group(
                schema = schema,
            )
    }

    object Any : OpenApiType(null) {

        fun getType(nullable: kotlin.Boolean): KType =
            if (nullable) typeOf<kotlin.Any?>() else typeOf<kotlin.Any>()

        fun createSchema(nullable: kotlin.Boolean): ColumnSchema = ColumnSchema.Value(
            type = getType(nullable),
        )
    }

    object Array : OpenApiType("array") {

        fun getType(nullable: kotlin.Boolean, type: KType): KType =
            List::class.createTypeWithArgument(type, nullable)

        // used for lists of primitives
        fun createSchemaAsList(nullable: kotlin.Boolean, arraySchema: ColumnSchema): ColumnSchema =
            ColumnSchema.Value(
                type = getType(nullable, arraySchema.type),
            )

        fun createSchemaAsFrame(nullable: kotlin.Boolean, arraySchema: DataFrameSchema): ColumnSchema =
            ColumnSchema.Frame(
                schema = arraySchema,
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
    data class UsingRef(val name: String, val columnSchema: ColumnSchema) : OpenApiTypeResult

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefSchema : OpenApiTypeResult

    /** Property is a schema with OpenApiType [openApiType]. */
    data class Success(val openApiType: OpenApiType, val nullable: Boolean) : OpenApiTypeResult

    /** Property is an enum with OpenApiType [openApiType]. */
    data class SuccessAsEnum(val openApiType: OpenApiType, val nullable: Boolean, val values: List<Any?>) : OpenApiTypeResult
}

/**
 * Converts a single property of an OpenApi type schema to [OpenApiType] or [ColumnSchema] (from another type reference)
 * if successful. A property can be a reference to another schema, or an OpenApi type.
 *
 * @receiver The property of an OpenApi type schema to convert.
 * @param isRequired    True if the property appears in the `required` field of the type schema.
 * @param getRefSchema  A function that returns a [DataFrameSchema] for a given reference name if successful.
 *
 * @return If the property is a reference, [OpenApiTypeResult.UsingRef], if the property is an OpenApi type,
 *   [OpenApiTypeResult.Success]. If unsuccessful, [OpenApiTypeResult.CannotFindRefSchema].
 */
private fun Schema<*>.toOpenApiType(
    isRequired: Boolean,
    getRefSchema: GetRefSchema,
): OpenApiTypeResult {
    if (`$ref` != null) {
        val typeName = `$ref`.takeLastWhile { it != '/' }

        return OpenApiTypeResult.UsingRef(
            name = typeName,
            columnSchema = ColumnSchema.Group(
                when (val it = getRefSchema(typeName)) {
                    is DataFrameSchemaResult.CannotFindRefSchema -> return OpenApiTypeResult.CannotFindRefSchema
                    is DataFrameSchemaResult.Success -> it.dataFrameSchema
                },
            ),
        )
    }

    if (enum != null) {
        val nullable = enum.any { it == null }
        val openApiType = OpenApiType.fromStringOrNull(type)!!

//        return OpenApiTypeResult.SuccessAsEnum(
//            openApiType = openApiType,
//            nullable = nullable,
//            values = enum.toList(),
//        )
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

                return OpenApiTypeResult.UsingRef(
                    name = typeName,
                    columnSchema = ColumnSchema.Group(
                        when (val it = getRefSchema(typeName)) {
                            is DataFrameSchemaResult.CannotFindRefSchema -> return OpenApiTypeResult.CannotFindRefSchema
                            is DataFrameSchemaResult.Success -> it.dataFrameSchema
                        },
                    ),
                )
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

private sealed interface ColumnSchemaResult {

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefSchema : ColumnSchemaResult

    /** ColumnSchema [columnSchema] created successfully. */
    data class Success(val columnSchema: ColumnSchema) : ColumnSchemaResult
}

/**
 * Converts an OpenApi type schema to a [ColumnSchema] if successful.
 *
 * @receiver The OpenApi type to convert.
 * @param property                  The original property of the OpenApi type for if extra data is needed from it.
 * @param propertyName              The name of the property with type [OpenApiType].
 *   Usually only used if [this] is an (inline) [OpenApiType.Object] or [OpenApiType.Array] with .
 * @param nullable                  Whether the property is nullable.
 * @param getRefSchema              A function that returns a [DataFrameSchema] for a given reference name if successful.
 * @param produceAdditionalSchema   A function that produces an additional [DataFrameSchema] for a given name.
 *                                  This is used for `object` types not present in the root of `components/schemas`.
 *
 * @return A [ColumnSchemaResult.Success] if successful, [ColumnSchemaResult.CannotFindRefSchema] if unsuccessful.
 */
private fun OpenApiType.toColumnSchema(
    property: Schema<*>,
    propertyName: String,
    nullable: Boolean,
    getRefSchema: GetRefSchema,
    produceAdditionalSchema: ProduceAdditionalSchema,
): ColumnSchemaResult {
    return ColumnSchemaResult.Success(
        when (this) {
            is OpenApiType.Any -> createSchema(nullable)

            is OpenApiType.Array ->
                if (property.items == null) { // should in theory not occur, but make List<Any?> just in case
                    createSchemaAsList(
                        nullable = nullable,
                        arraySchema = OpenApiType.Any.createSchema(nullable = true),
                    )
                } else { // Try to get the array type
                    val arrayTypeResult = property.items!!.toOpenApiType(
                        isRequired = true,
                        getRefSchema = getRefSchema,
                    )

                    when (arrayTypeResult) {
                        is OpenApiTypeResult.CannotFindRefSchema ->
                            return ColumnSchemaResult.CannotFindRefSchema

                        is OpenApiTypeResult.UsingRef ->
                            createSchemaAsFrame(
                                nullable = nullable,
                                arraySchema = (arrayTypeResult.columnSchema as ColumnSchema.Group).schema,
                            )

                        is OpenApiTypeResult.Success -> {
                            val arrayTypeSchemaResult = arrayTypeResult.openApiType.toColumnSchema(
                                property = property.items!!,
                                propertyName = propertyName,
                                nullable = arrayTypeResult.nullable,
                                getRefSchema = getRefSchema,
                                produceAdditionalSchema = produceAdditionalSchema,
                            )

                            when (arrayTypeSchemaResult) {
                                is ColumnSchemaResult.CannotFindRefSchema ->
                                    return ColumnSchemaResult.CannotFindRefSchema

                                is ColumnSchemaResult.Success ->
                                    createSchemaAsList(
                                        nullable = nullable,
                                        arraySchema = arrayTypeSchemaResult.columnSchema,
                                    )
                            }
                        }

                        is OpenApiTypeResult.SuccessAsEnum -> TODO()
                    }
                }

            is OpenApiType.Boolean -> createSchema(nullable)

            is OpenApiType.Integer -> createSchema(
                nullable = nullable,
                format = OpenApiIntegerFormat.fromStringOrNull(property.format),
            )

            is OpenApiType.Number -> createSchema(
                nullable = nullable,
                format = OpenApiNumberFormat.fromStringOrNull(property.format),
            )

            is OpenApiType.Object -> {
                val dataFrameSchemaResult = property.toDataFrameSchema(
                    typeName = propertyName.snakeToUpperCamelCase(),
                    getRefSchema = getRefSchema,
                    produceAdditionalSchema = produceAdditionalSchema,
                )

                when (dataFrameSchemaResult) {
                    is DataFrameSchemaResult.CannotFindRefSchema ->
                        return ColumnSchemaResult.CannotFindRefSchema

                    is DataFrameSchemaResult.Success -> {
                        produceAdditionalSchema(
                            propertyName.snakeToUpperCamelCase(),
                            dataFrameSchemaResult.dataFrameSchema,
                        )

                        createSchema(dataFrameSchemaResult.dataFrameSchema)
                    }
                }
            }

            is OpenApiType.String -> createSchema(
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

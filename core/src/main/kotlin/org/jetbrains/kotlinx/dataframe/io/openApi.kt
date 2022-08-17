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
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public fun main() {
    val openAPI: OpenAPI = OpenAPIParser()
        .readLocation(
            "https://petstore3.swagger.io/api/v3/openapi.json",
            null,
            null
        )
        .also {
            if (it.messages != null) println("messages: ${it.messages.toList()}")
        }
        .openAPI ?: error("Failed to parse OpenAPI")

    val result = openAPI.components?.schemas?.toMap()
        ?.filter { it.value.type == "object" }
        ?.toDataFrameSchema()
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

        // TODO use arraySchema.
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

private fun interface GetDataFrameSchema {
    operator fun invoke(
        getRefSchema: (String) -> DataFrameSchemaResult,
        produceAdditionalSchema: (String, DataFrameSchema) -> Unit,
    ): DataFrameSchemaResult
}

private fun Map<String, Schema<*>>.toDataFrameSchema(): List<Pair<String, DataFrameSchema>> {
    val retrievableColumnSchemas = mapValues { (_, value) ->
        GetDataFrameSchema(value::toDataFrameSchema)
    }.toMutableMap()

    val dataFrames = mutableMapOf<String, DataFrameSchema>()

    while (retrievableColumnSchemas.isNotEmpty()) {
        val (res, _) = retrievableColumnSchemas.entries.first { (name, columnSchemas) ->
            val res = columnSchemas(
                getRefSchema = { DataFrameSchemaResult.fromNullable(dataFrames[it]) },
                produceAdditionalSchema = dataFrames::put,
            )

            if (res is DataFrameSchemaResult.Success) {
                dataFrames[name] = res.dataFrameSchema
                true
            } else false
        }
        retrievableColumnSchemas -= res
    }

    return dataFrames.toList()
}

private sealed interface OpenApiTypeResult {

    data class UsingRef(val name: String, val columnSchema: ColumnSchema) : OpenApiTypeResult

    object CannotFindRefSchema : OpenApiTypeResult

    data class Success(val openApiType: OpenApiType, val nullable: Boolean) : OpenApiTypeResult
}

private fun Schema<*>.toOpenApiType(
    getRefSchema: (String) -> DataFrameSchemaResult,
    required: List<String>,
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
        // TODO
        println("TODO enum not yet implemented")
    }

    if (not != null) {
        // TODO
        println("TODO not not yet implemented")
    }

    if (allOf != null) {
        // TODO https://swagger.io/docs/specification/data-models/oneof-anyof-allof-not/
        println("TODO allOf not yet implemented")
    }

    var openApiType = OpenApiType.fromStringOrNull(type)

    if (openApiType == null) { // check for anyOf/oneOf
        val anyOf = ((anyOf ?: emptyList()) + (oneOf ?: emptyList()))

        val refs = anyOf.mapNotNull { it.`$ref` }

        val types = anyOf.mapNotNull { it.type }
            .mapNotNull(OpenApiType.Companion::fromStringOrNull)
            .distinct()

        openApiType = when {
            types.size == 1 && refs.isEmpty() -> types.first()

            types.size == 2 && refs.isEmpty() && types.containsAll(
                listOf(OpenApiType.Number, OpenApiType.Integer)
            ) -> OpenApiType.Number

            types.isEmpty() && refs.size == 1 -> {
                val typeName = refs.first().takeLastWhile { it != '/' }

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

            else -> OpenApiType.Any
        }
    }

    val nullable = nullable ?: (name !in required)

    if (nullable && openApiType == OpenApiType.Object) {
        println("Warning: type $name is marked nullable, but ColumnGroups cannot be null.")
    }

    return OpenApiTypeResult.Success(openApiType, nullable)
}

private sealed interface DataFrameSchemaResult {

    object CannotFindRefSchema : DataFrameSchemaResult

    data class Success(val dataFrameSchema: DataFrameSchema) : DataFrameSchemaResult

    companion object {
        fun fromNullable(schema: DataFrameSchema?): DataFrameSchemaResult =
            if (schema == null) CannotFindRefSchema else Success(schema)
    }
}

private sealed interface ColumnSchemaResult {

    object CannotFindRefSchema : ColumnSchemaResult

    data class Success(val schema: ColumnSchema) : ColumnSchemaResult
}

private fun OpenApiType.toColumnSchema(
    name: String,
    nullable: Boolean,
    prop: Schema<*>,
    getRefSchema: (String) -> DataFrameSchemaResult,
    produceAdditionalSchema: (String, DataFrameSchema) -> Unit,
): ColumnSchemaResult {
    return ColumnSchemaResult.Success(
        when (this) {
            is OpenApiType.Any -> createSchema(nullable)

            is OpenApiType.Array -> {
                if (prop.items == null) {
                    this.createSchemaAsList(
                        nullable = nullable,
                        arraySchema = OpenApiType.Any.createSchema(nullable = true),
                    ) // make List<Any?>
                } else {
                    when (
                        val it = prop.items.toOpenApiType(
                            getRefSchema = getRefSchema,
                            required = listOf(prop.name),
                        )
                    ) {
                        is OpenApiTypeResult.CannotFindRefSchema ->
                            return ColumnSchemaResult.CannotFindRefSchema

                        is OpenApiTypeResult.UsingRef ->
                            createSchemaAsFrame(
                                nullable = nullable,
                                arraySchema = (it.columnSchema as ColumnSchema.Group).schema,
                            )

                        is OpenApiTypeResult.Success -> {
                            val arrayTypeSchemaResult = it.openApiType.toColumnSchema(
                                name = name, // TODO not sure what this should be
                                nullable = it.nullable,
                                prop = prop.items,
                                getRefSchema = getRefSchema,
                                produceAdditionalSchema = produceAdditionalSchema,
                            )

                            when (arrayTypeSchemaResult) {
                                is ColumnSchemaResult.CannotFindRefSchema ->
                                    return ColumnSchemaResult.CannotFindRefSchema

                                is ColumnSchemaResult.Success ->
                                    createSchemaAsList(
                                        nullable = nullable,
                                        arraySchema = arrayTypeSchemaResult.schema,
                                    )
                            }
                        }
                    }
                }
            }

            is OpenApiType.Boolean -> createSchema(nullable)

            is OpenApiType.Integer -> createSchema(
                nullable = nullable,
                format = OpenApiIntegerFormat.fromStringOrNull(prop.format),
            )

            is OpenApiType.Number -> createSchema(
                nullable = nullable,
                format = OpenApiNumberFormat.fromStringOrNull(prop.format),
            )

            is OpenApiType.Object -> createSchema(
                when (val it = prop.toDataFrameSchema(getRefSchema, produceAdditionalSchema)) {
                    is DataFrameSchemaResult.CannotFindRefSchema -> return ColumnSchemaResult.CannotFindRefSchema
                    is DataFrameSchemaResult.Success -> {
                        produceAdditionalSchema(name, it.dataFrameSchema)
                        it.dataFrameSchema
                    }
                },
            )

            is OpenApiType.String -> createSchema(
                nullable = nullable,
                format = OpenApiStringFormat.fromStringOrNull(prop.format),
            )
        }
    )
}

private fun Schema<*>.toDataFrameSchema(
    getRefSchema: (String) -> DataFrameSchemaResult,
    produceAdditionalSchema: (String, DataFrameSchema) -> Unit,
): DataFrameSchemaResult {
    val columns = buildMap {
        for ((name, prop) in properties) {
            val openApiTypeResult = prop.toOpenApiType(getRefSchema, required ?: emptyList())

            val (openApiType, nullable) = when (openApiTypeResult) {
                is OpenApiTypeResult.CannotFindRefSchema ->
                    return DataFrameSchemaResult.CannotFindRefSchema

                is OpenApiTypeResult.UsingRef -> {
                    this[name] = openApiTypeResult.columnSchema
                    continue
                }

                is OpenApiTypeResult.Success ->
                    openApiTypeResult
            }

            val columnSchemaResult = openApiType.toColumnSchema(
                name = name,
                prop = prop,
                nullable = nullable,
                getRefSchema = getRefSchema,
                produceAdditionalSchema = produceAdditionalSchema,
            )

            when (columnSchemaResult) {
                is ColumnSchemaResult.CannotFindRefSchema ->
                    return DataFrameSchemaResult.CannotFindRefSchema

                is ColumnSchemaResult.Success ->
                    this[name] = columnSchemaResult.schema
            }
        }
    }

    return DataFrameSchemaResult.Success(
        DataFrameSchemaImpl(columns)
    )
}

package org.jetbrains.kotlinx.dataframe.io

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.time.LocalDate
import java.time.LocalDateTime
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

    for ((name, dataFrameSchema) in result) {
        println("$name:")
        println(dataFrameSchema)
        println()
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

private sealed class OpenApiType(
    val name: kotlin.String?,
) {

    object String : OpenApiType("string") {
        fun createSchema(nullable: kotlin.Boolean, format: OpenApiStringFormat?): ColumnSchema = ColumnSchema.Value(
            type = when (format) {
                OpenApiStringFormat.DATE -> if (nullable) typeOf<LocalDate?>() else typeOf<LocalDate>()
                OpenApiStringFormat.DATE_TIME -> if (nullable) typeOf<LocalDateTime?>() else typeOf<LocalDateTime>()
                OpenApiStringFormat.PASSWORD -> if (nullable) typeOf<String?>() else typeOf<kotlin.String>()
                OpenApiStringFormat.BYTE -> if (nullable) typeOf<Byte?>() else typeOf<Byte>()
                OpenApiStringFormat.BINARY -> if (nullable) typeOf<ByteArray?>() else typeOf<ByteArray>()
                null -> if (nullable) typeOf<String?>() else typeOf<kotlin.String>()
            },
        )
    }

    object Integer : OpenApiType("integer") {
        fun createSchema(nullable: kotlin.Boolean, format: OpenApiIntegerFormat?): ColumnSchema = ColumnSchema.Value(
            type = when (format) {
                null, OpenApiIntegerFormat.INT32 -> if (nullable) typeOf<Int?>() else typeOf<Int>()
                OpenApiIntegerFormat.INT64 -> if (nullable) typeOf<Long?>() else typeOf<Long>()
            },
        )
    }

    object Number : OpenApiType("number") {
        fun createSchema(nullable: kotlin.Boolean, format: OpenApiNumberFormat?): ColumnSchema = ColumnSchema.Value(
            type = when (format) {
                null, OpenApiNumberFormat.FLOAT -> if (nullable) typeOf<Float?>() else typeOf<Float>()
                OpenApiNumberFormat.DOUBLE -> if (nullable) typeOf<Double?>() else typeOf<Double>()
            },
        )
    }

    object Boolean : OpenApiType("boolean") {
        fun createSchema(nullable: kotlin.Boolean): ColumnSchema = ColumnSchema.Value(
            type = if (nullable) typeOf<Boolean?>() else typeOf<Boolean>(),
        )
    }

    object Object : OpenApiType("object") {
        fun createSchema(schema: org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema): ColumnSchema =
            ColumnSchema.Group(
                schema = schema,
            )
    }

    object Any : OpenApiType(null) {
        fun createSchema(nullable: kotlin.Boolean): ColumnSchema = ColumnSchema.Value(
            type = if (nullable) typeOf<Any?>() else typeOf<Any>(),
        )
    }

    object Array : OpenApiType("array") {
        // TODO
        fun createSchema(nullable: kotlin.Boolean): ColumnSchema =
            ColumnSchema.Value(
                type = if (nullable) typeOf<List<*>?>() else typeOf<List<*>>(),
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

private fun Map<String, Schema<*>>.toDataFrameSchema(): List<Pair<String, DataFrameSchema>> {
    val retrievableColumnSchemas =
        mapValues { (_, value) -> value.toDataFrameSchema() }
            .toMutableMap()

    val dataFrames = mutableMapOf<String, DataFrameSchema>()

    while (retrievableColumnSchemas.isNotEmpty()) {
        val (name, columnSchemas) = retrievableColumnSchemas.entries.first()
        val res = columnSchemas { dataFrames[it] }

        if (res != null) {
            retrievableColumnSchemas.remove(name)
            dataFrames[name] = res
        }
    }

    return dataFrames.toList()
}

private fun interface GetDataFrameSchema {
    operator fun invoke(getOtherScheme: (String) -> DataFrameSchema?): DataFrameSchema?
}

private fun Schema<*>.toDataFrameSchema(): GetDataFrameSchema = GetDataFrameSchema { getOtherScheme ->
    val columns = buildMap {
        for ((name, prop) in properties) {
            if (prop.`$ref` != null) {
                val typeName = prop.`$ref`.takeLastWhile { it != '/' }
                this[name] = ColumnSchema.Group(
                    getOtherScheme(typeName) ?: return@GetDataFrameSchema null,
                )
                continue
            }

            if (prop.enum != null) {
                // TODO
                println("TODO enum not yet implemented")
            }

            var openApiType = OpenApiType.fromStringOrNull(prop.type)

            if (openApiType == null) { // check for anyOf/oneOf
                val anyOf = ((prop.anyOf ?: emptyList()) + (prop.oneOf ?: emptyList()))
                    .mapNotNull { it.type }
                    .mapNotNull(OpenApiType.Companion::fromStringOrNull)
                    .distinct()

                openApiType = when {
                    anyOf.size == 2 && anyOf.containsAll(
                        listOf(OpenApiType.Number, OpenApiType.Integer)
                    ) -> OpenApiType.Number

                    else -> OpenApiType.Any
                }
            }

            val nullable = prop.nullable ?: false

            if (nullable && openApiType == OpenApiType.Object) {
                println("Warning: type $name is marked nullable, but ColumnGroups cannot be null.")
            }

            val columnSchema = when (openApiType) {
                is OpenApiType.Any -> openApiType.createSchema(nullable)
                is OpenApiType.Array -> openApiType.createSchema(nullable)
                is OpenApiType.Boolean -> openApiType.createSchema(nullable)
                is OpenApiType.Integer -> openApiType.createSchema(
                    nullable,
                    OpenApiIntegerFormat.fromStringOrNull(prop.format),
                )

                is OpenApiType.Number -> openApiType.createSchema(
                    nullable,
                    OpenApiNumberFormat.fromStringOrNull(prop.format),
                )

                is OpenApiType.Object -> openApiType.createSchema(
                    prop.toDataFrameSchema()(getOtherScheme) ?: return@GetDataFrameSchema null,
                )

                is OpenApiType.String -> openApiType.createSchema(
                    nullable,
                    OpenApiStringFormat.fromStringOrNull(prop.format),
                )
            }

            this[name] = columnSchema
        }
    }

    return@GetDataFrameSchema DataFrameSchemaImpl(columns)
}

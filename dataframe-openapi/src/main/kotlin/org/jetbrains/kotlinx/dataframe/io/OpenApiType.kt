package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import kotlin.reflect.typeOf

/**
 * Represents all types supported by OpenApi with functions to create a [FieldType] from each.
 */
internal sealed class OpenApiType(val name: kotlin.String?) : IsObject {

    // Used in generation to decide whether something is an object or not.
    override val isObject: kotlin.Boolean
        get() = this is Object || this is AnyObject

    object String : OpenApiType("string") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiStringFormat?): FieldType.ValueFieldType =
            FieldType.ValueFieldType(
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

        fun getType(nullable: kotlin.Boolean, format: OpenApiIntegerFormat?): FieldType.ValueFieldType =
            FieldType.ValueFieldType(
                typeFqName = when (format) {
                    null, OpenApiIntegerFormat.INT32 -> if (nullable) typeOf<Int?>() else typeOf<Int>()
                    OpenApiIntegerFormat.INT64 -> if (nullable) typeOf<Long?>() else typeOf<Long>()
                }.toString(),
            )
    }

    object Number : OpenApiType("number") {

        fun getType(nullable: kotlin.Boolean, format: OpenApiNumberFormat?): FieldType.ValueFieldType =
            FieldType.ValueFieldType(
                typeFqName = when (format) {
                    null, OpenApiNumberFormat.FLOAT -> if (nullable) typeOf<Float?>() else typeOf<Float>()
                    OpenApiNumberFormat.DOUBLE -> if (nullable) typeOf<Double?>() else typeOf<Double>()
                }.toString(),
            )
    }

    object Boolean : OpenApiType("boolean") {

        fun getType(nullable: kotlin.Boolean): FieldType.ValueFieldType =
            FieldType.ValueFieldType(
                typeFqName = (if (nullable) typeOf<kotlin.Boolean?>() else typeOf<kotlin.Boolean>()).toString(),
            )
    }

    object Object : OpenApiType("object") {

        fun getType(nullable: kotlin.Boolean, marker: OpenApiMarker): FieldType =
            FieldType.GroupFieldType(
                markerName = marker.name.let {
                    if (nullable) it.toNullable() else it
                },
            )
    }

    /** Represents a merged object which will turn into DataRow<Any?> */
    object AnyObject : OpenApiType(null) {

        fun getType(nullable: kotlin.Boolean): FieldType =
            FieldType.GroupFieldType(
                markerName = (if (nullable) typeOf<DataRow<kotlin.Any?>>() else typeOf<DataRow<kotlin.Any>>()).toString(),
            )
    }

    object Array : OpenApiType("array") {

        /** used for list of primitives (read as List<MyPrimitive>) */
        fun getTypeAsList(nullableArray: kotlin.Boolean, typeFqName: kotlin.String): FieldType.ValueFieldType =
            FieldType.ValueFieldType(
                typeFqName = "${List::class.qualifiedName!!}<$typeFqName>${if (nullableArray) "?" else ""}",
            )

        /** used for list of objects (read as DataFrame<MyMarker>) */
        fun getTypeAsFrame(nullable: kotlin.Boolean, markerName: kotlin.String): FieldType.FrameFieldType =
            FieldType.FrameFieldType(
                markerName = markerName.let { if (nullable) it.toNullable() else it },
                nullable = false, // preferring DataFrame<Something?> over DataFrame<Something>?
            )

        /** used for list of AdditionalProperty objects (read as List<DataFrame<MyMarker>>) */
        fun getTypeAsFrameList(
            nullable: kotlin.Boolean,
            nullableArray: kotlin.Boolean,
            markerName: kotlin.String,
        ): FieldType.ValueFieldType =
            FieldType.ValueFieldType(
                typeFqName = "${List::class.qualifiedName!!}<${DataFrame::class.qualifiedName!!}<${markerName.let { if (nullable) it.toNullable() else it }}>>${if (nullableArray) "?" else ""}",
            )
    }

    object Any : OpenApiType(null) {
        fun getType(nullable: kotlin.Boolean): FieldType.ValueFieldType =
            FieldType.ValueFieldType(
                typeFqName = (if (nullable) typeOf<kotlin.Any?>() else typeOf<kotlin.Any>()).toString(),
            )
    }

    override fun toString(): kotlin.String = name.toString()

    companion object {

        val all: List<OpenApiType> = listOf(String, Integer, Number, Boolean, Object, Array, Any)

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

/** https://swagger.io/docs/specification/data-models/data-types/#numbers */
internal enum class OpenApiIntegerFormat(val value: String) {
    INT32("int32"),
    INT64("int64");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiIntegerFormat? = values().firstOrNull { it.value == value }
    }
}

/** https://swagger.io/docs/specification/data-models/data-types/#numbers */
internal enum class OpenApiNumberFormat(val value: String) {
    FLOAT("float"),
    DOUBLE("double");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiNumberFormat? = values().firstOrNull { it.value == value }
    }
}

/** https://swagger.io/docs/specification/data-models/data-types/#string */
internal enum class OpenApiStringFormat(val value: String) {
    DATE("date"),
    DATE_TIME("date-time"),
    PASSWORD("password"),
    BYTE("byte"),
    BINARY("binary");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiStringFormat? = values().firstOrNull { it.value == value }
    }
}

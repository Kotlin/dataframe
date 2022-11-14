package org.jetbrains.kotlinx.dataframe.io // ktlint-disable filename

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.core.models.AuthorizationValue
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.KeyValueProperty
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.replace
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
import org.jetbrains.kotlinx.dataframe.codeGen.toNullable
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.io.AdditionalProperty.Companion.convertToAdditionalProperties
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
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.String
import kotlin.reflect.KType
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

/**
 * Allows for OpenApi type schemas to be converted to [DataSchema] interfaces.
 */
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
 * Function to be used in [ConvertSchemaDsl] ([AnyFrame.convertTo]) to help convert a DataFrame to adhere to an
 * OpenApi schema.
 */
public fun ConvertSchemaDsl<*>.convertDataRowsWithOpenApi() {
    // Convert DataRow to Any if the schema requires the types to be erased.
    convert<DataRow<*>>().with<_, Any?> { it }

    // Provide converter for (recursive) List<DataFrame<>>
    convertIf({ fromType, toSchema ->
        val (fromIsRecursiveListOfDataFrame, fromDepth) = fromType.isRecursiveListOfDataFrame()
        val (toIsRecursiveListOfDataFrame, toDepth) = toSchema.type.isRecursiveListOfDataFrame()

        fromIsRecursiveListOfDataFrame && toIsRecursiveListOfDataFrame && fromDepth == toDepth
    }) {
        try {
            it.convertRecursiveListOfDataFrame(toSchema.type) {
                convertDataRowsWithOpenApi()
            }
        } catch (_: Exception) {
            it
        }
    }
}

/**
 * @receiver [KType] to check if it is a recursive list of [DataFrame]s
 * @return [Pair] of result and the recursive depth.
 *   `true` if Receiver is a recursive list of [DataFrame]s, like [List]<[List]<[DataFrame]<*>>>
 */
private fun KType.isRecursiveListOfDataFrame(depth: Int = 0): Pair<Boolean, Int> = when (jvmErasure) {
    typeOf<List<*>>().jvmErasure -> arguments[0].type?.isRecursiveListOfDataFrame(depth + 1) ?: (false to depth)
    typeOf<DataFrame<*>>().jvmErasure -> true to depth
    typeOf<DataFrame<*>?>().jvmErasure -> true to depth
    else -> false to depth
}

/**
 * @receiver Recursive [List] of [DataFrame]s, like [List]<[List]<[DataFrame]<*>>>, for which to convert the [DataFrame]s.
 * @param type Type to which to convert the [DataFrame]s.
 * @param convertTo Optional [ConvertSchemaDsl] to use for the conversion.
 * @return Receiver with converted [DataFrame]s.
 */
private fun Any?.convertRecursiveListOfDataFrame(
    type: KType,
    convertTo: ConvertSchemaDsl<*>.() -> Unit = {},
): Any? = when (this) {
    is List<*> -> map { it?.convertRecursiveListOfDataFrame(type.arguments[0].type!!, convertTo) }
    is DataFrame<*> -> convertTo(schemaType = type.arguments[0].type!!, body = convertTo)
    null -> null
    else -> throw IllegalArgumentException("$this is not a List or DataFrame")
}

/**
 * Used to add `readJson` and `convertToMyMarker` functions to the generated interfaces.
 * Makes sure [convertDataRowsWithOpenApi] is always used in conversions.
 */
private object DefaultReadOpenApiMethod : AbstractDefaultReadMethod(
    path = null,
    arguments = MethodArguments.EMPTY,
    methodName = "",
) {

    override val additionalImports: List<String> = listOf(
        "import org.jetbrains.kotlinx.dataframe.io.readJson",
        "import org.jetbrains.kotlinx.dataframe.io.readJsonStr",
        "import org.jetbrains.kotlinx.dataframe.api.convertTo",
        "import org.jetbrains.kotlinx.dataframe.api.first",
        "import org.jetbrains.kotlinx.dataframe.api.${JsonPath::class.simpleName}",
        "import org.jetbrains.kotlinx.dataframe.api.${DataSchemaEnum::class.simpleName}",
        "import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.*",
        "import org.jetbrains.kotlinx.dataframe.io.${ConvertSchemaDsl<*>::convertDataRowsWithOpenApi.name}",
    )

    override fun toDeclaration(marker: Marker, visibility: String): String {
        val returnType = DataFrame::class.asClassName().parameterizedBy(ClassName("", listOf(marker.shortName)))

        // convertTo: ConvertSchemaDsl<MyMarker>.() -> Unit = {}
        val convertToParameter = ParameterSpec
            .builder(
                name = "convertTo",
                type = LambdaTypeName.get(
                    receiver = ConvertSchemaDsl::class
                        .asClassName()
                        .parameterizedBy(ClassName("", listOf(marker.shortName))),
                    parameters = emptyList(),
                    returnType = UNIT,
                ),
            )
            .defaultValue("{}")
            .build()

        fun getConvertMethod(): String = """
            return convertTo<${marker.shortName}> { 
                ${ConvertSchemaDsl<*>::convertDataRowsWithOpenApi.name}() 
                convertTo()
            }
        """.trimIndent()

        fun getReadAndConvertMethod(
            readMethod: String,
        ): String = """
            return ${DataFrame::class.asClassName()}
                .$readMethod${if (marker is OpenApiMarker.AdditionalPropertyInterface) "[\"$valueColumnName\"].first().let { it as DataFrame<*> }" else ""}
                .convertTo${marker.shortName}()
        """.trimIndent()

        val typeSpec = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("convertTo${marker.shortName}")
                    .receiver(DataFrame::class.asClassName().parameterizedBy(STAR))
                    .addParameter(convertToParameter)
                    .addCode(getConvertMethod())
                    .returns(returnType)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(name = "keyValuePaths", type = typeOf<List<JsonPath>>().asTypeName())
                    .getter(
                        FunSpec.getterBuilder()
                            .addCode(
                                run {
                                    val additionalPropertyPaths = (marker as OpenApiMarker)
                                        .additionalPropertyPaths
                                        .distinct()

                                    "return listOf(${additionalPropertyPaths.joinToString { "JsonPath(\"\"\"${it.path}\"\"\")" }})"
                                }
                            )
                            .build()
                    )
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("url", URL::class)
                    .addCode(getReadAndConvertMethod("readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("path", String::class)
                    .addCode(getReadAndConvertMethod("readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJson")
                    .returns(returnType)
                    .addParameter("stream", InputStream::class)
                    .addCode(getReadAndConvertMethod("readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("readJsonStr")
                    .returns(returnType)
                    .addParameter("text", String::class)
                    .addCode(getReadAndConvertMethod("readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)"))
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

/** Parse and read OpenApi specification to [DataSchema] interfaces. */
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

/** Parse and read OpenApi specification to [DataSchema] interfaces. */
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
 * Converts a parsed OpenAPI specification into a [CodeWithConverter] consisting of [DataSchema] interfaces.
 *
 * @param swaggerParseResult the result of parsing an OpenAPI specification, created using [readOpenApi] or [readOpenApiAsString].
 * @param extensionProperties whether to add extension properties to the generated interfaces. This is usually not
 *   necessary, since both the KSP- and the Gradle plugin, will add extension properties to the generated code.
 * @param visibility the visibility of the generated marker classes.
 *
 * @return a [CodeWithConverter] object, representing the generated code.
 *  Use converter to change name of the wrapping object.
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

    // generate the code for the markers in result
    val codeGenerator = CodeGenerator.create(useFqNames = true)

    fun toCode(marker: OpenApiMarker) = codeGenerator.generate(
        marker = marker.withVisibility(visibility),
        interfaceMode = when (marker) {
            is OpenApiMarker.Enum -> InterfaceGenerationMode.Enum
            is OpenApiMarker.Interface -> InterfaceGenerationMode.WithFields
            is OpenApiMarker.TypeAlias, is OpenApiMarker.MarkerAlias -> InterfaceGenerationMode.TypeAlias
        },
        extensionProperties = extensionProperties,
        readDfMethod = if (marker is OpenApiMarker.Interface) DefaultReadOpenApiMethod else null,
    )

    val (typeAliases, markers) = result.partition { it is OpenApiMarker.TypeAlias || it is OpenApiMarker.MarkerAlias }
    val generatedMarkers = markers.map(::toCode).reduceOrNull(CodeWithConverter::plus)
    val generatedTypeAliases = typeAliases.map(::toCode).reduceOrNull(CodeWithConverter::plus)

    fun getCode(singletonObjectName: ValidFieldName): String =
        """
         |object ${ValidFieldName.of(objectName).quotedIfNeeded} {
         |    ${generatedMarkers?.declarations?.replace("\n", "\n|    ") ?: ""}
         |}
         |${generatedTypeAliases?.declarations?.replace("\n", "\n|") ?: ""}
     """.trimMargin()

    return CodeWithConverter(
        declarations = getCode(openApi.info?.title ?: "OpenApiDataSchemas"),
        converter = ::getCode,
    )
}

private interface IsObjectOrList {
    val isObject: Boolean
    val isList: Boolean
}

/**
 * A [DataSchema] interface can implement this if it represents a map-like data schema (so key: value).
 * Used in OpenAPI to represent objects with 'just' additionalProperties of a certain type.
 */
public interface AdditionalProperty<T> : KeyValueProperty<T> {

    /** Key of the property. */
    override val key: String

    /** Value of the property. */
    override val value: T

    public companion object {
        internal fun getMarker(typeArguments: List<String>) = Marker(
            name = AdditionalProperty::class.qualifiedName!!,
            isOpen = false,
            fields = listOf(
                generatedFieldOf(
                    fieldName = ValidFieldName.of(AdditionalProperty<*>::key.name),
                    columnName = AdditionalProperty<*>::key.name,
                    overrides = false,
                    fieldType = FieldType.ValueFieldType(String::class.qualifiedName!!),
                ),
                generatedFieldOf(
                    fieldName = ValidFieldName.of(AdditionalProperty<*>::`value`.name),
                    columnName = AdditionalProperty<*>::`value`.name,
                    overrides = false,
                    fieldType = FieldType.ValueFieldType(Any::class.qualifiedName!! + "?"),
                ),
            ),
            superMarkers = emptyList(),
            visibility = MarkerVisibility.EXPLICIT_PUBLIC,
            typeParameters = emptyList(),
            typeArguments = typeArguments,
        )

        /**
         * Used to convert a [DataFrame] to an [AdditionalProperty] [DataFrame] by [DataFrame.gather]ing all column names
         * into a column named `key` and the values into a column named `value` of type `Any?`.
         *
         * We first convert the columns (and recursively down the frame-tree) before gathering this
         * DataFrame. This is to prevent `gather { all() }` from creating a lot of `null` values due to
         * merging of ColumnGroups.
         *
         *
         * @receiver the DataFrame to convert to an [AdditionalProperty] [DataFrame].
         * @param convertTo optional [ConvertSchemaDsl] to specify extra conversions that need to occur.
         * @return a [DataFrame] of [AdditionalProperty]s.
         */
        @Suppress("UNCHECKED_CAST")
        public fun AnyFrame.convertToAdditionalProperties(
            schemaType: KType,
            convertTo: ConvertSchemaDsl<Any>.() -> Unit = {},
        ): DataFrame<AdditionalProperty<*>> {
            require(
                schemaType.isSubtypeOf(typeOf<AdditionalProperty<*>>()) ||
                    schemaType.isSubtypeOf(typeOf<AdditionalProperty<*>?>())
            ) { "schemaType must be an AdditionalProperty" }

            // grab the type of the `value` argument of the schemaType
            val valueType = schemaType.jvmErasure
                .supertypes
                .first { it.isSubtypeOf(typeOf<AdditionalProperty<*>>()) }
                .arguments
                .firstOrNull()
                ?.type
                ?: typeOf<Any?>()

            // Before gathering, first convert all columns (which will become the values) to the correct type.
            val dfWithConvertedValues = this.replace { all() }.with {
                when (it) {
                    is DataFrame<*> ->
                        it.convertTo(schemaType = valueType, body = convertTo)
                            .asColumnGroup(name = it.name())

                    else -> it
                }
            }

            // gather all columns with their names into a column "key" and the values into a column "value"
            val dfAsAdditionalProperty = dfWithConvertedValues
                .gather { all() }
                .into(AdditionalProperty<*>::key, AdditionalProperty<*>::`value`)

            val valueTypeIsDataFrameLike = with(valueType) {
                isSubtypeOf(typeOf<AnyFrame>()) ||
                    isSubtypeOf(typeOf<AnyFrame?>()) ||
                    hasAnnotation<DataSchema>() ||
                    jvmErasure.hasAnnotation<DataSchema>() ||
                    isSubtypeOf(typeOf<DataRow<*>>()) ||
                    isSubtypeOf(typeOf<DataRow<*>?>())
            }

            // filter null values if valueType is not nullable (if appeared in readJson)
            val dfNotNull = when {
                !valueType.isMarkedNullable && !valueTypeIsDataFrameLike ->
                    dfAsAdditionalProperty.filter { it[AdditionalProperty<*>::`value`] != null }

                else ->
                    dfAsAdditionalProperty
            }

            // convert to the correct type
            return dfNotNull.convertTo(
                schemaType = schemaType,
                body = convertTo
            ) as DataFrame<AdditionalProperty<*>>
        }

        /**
         * Used to convert a [DataFrame] to an [AdditionalProperty] [DataFrame] by [DataFrame.gather]ing all column names
         * into a column named `key` and the values into a column named `value` of type [T].
         *
         * @param T the type of the `value` column. Must implement [AdditionalProperty]
         * @receiver the DataFrame to convert to an [AdditionalProperty] [DataFrame].
         * @param convertTo optional [ConvertSchemaDsl] to specify extra conversions that need to occur.
         * @return a [DataFrame] of [AdditionalProperty]s.
         * @see convertToAdditionalProperties
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : AdditionalProperty<*>> DataFrame<*>.convertToAdditionalProperties(
            noinline convertTo: ConvertSchemaDsl<T>.() -> Unit = {},
        ): DataFrame<T> {
            require(T::class.hasAnnotation<DataSchema>()) {
                "The type ${T::class.simpleName} must be annotated with @${DataSchema::class.simpleName} to be converted to a MapLikeDataSchema"
            }
            return convertToAdditionalProperties(
                schemaType = typeOf<T>(),
                convertTo = convertTo,
            ) as DataFrame<T>
        }
    }
}

/**
 * Represents the type of Markers that we can use for code generation.
 * This includes [OpenApiMarker.Enum], [OpenApiMarker.Interface] (and [OpenApiMarker.AdditionalPropertyInterface]),
 * [OpenApiMarker.TypeAlias], and [OpenApiMarker.MarkerAlias].
 * It's a bit more flexible than [Marker] and insures the right arguments are given for the right type of [Marker].
 */
private sealed class OpenApiMarker private constructor(
    val nullable: Boolean, // in openApi, just like an enum, nullability can be saved in the object
    name: String,
    visibility: MarkerVisibility,
    fields: List<GeneratedField>,
    superMarkers: List<Marker>,
) : IsObjectOrList,
    Marker(
        name = name,
        isOpen = false,
        fields = fields,
        superMarkers = superMarkers,
        visibility = visibility,
        typeParameters = emptyList(),
        typeArguments = emptyList(),
    ) {

    abstract val additionalPropertyPaths: List<JsonPath>
    abstract fun withName(name: String): OpenApiMarker
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
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        nullable = nullable,
        name = name,
        visibility = visibility,
        fields = fields,
        superMarkers = emptyList(),
    ) {

        // enums become List<Something>, not Dataframe<*>
        override val isObject = false

        override val isList = false

        override val additionalPropertyPaths: List<JsonPath> = emptyList()

        override fun toFieldType(): FieldType =
            FieldType.ValueFieldType(
                // nullable or not, an enum must contain null to be nullable
                // https://github.com/OAI/OpenAPI-Specification/blob/main/proposals/2019-10-31-Clarify-Nullable.md#if-a-schema-specifies-nullable-true-and-enum-1-2-3-does-that-schema-allow-null-values-see-1900
                // if not required, it can still be omitted, resulting in null in Kotlin
                typeFqName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String): Enum =
            Enum(nullable, fields, name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): Enum =
            Enum(nullable, fields, name, visibility)
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
        override val additionalPropertyPaths: List<JsonPath>,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        nullable = nullable,
        name = name,
        visibility = visibility,
        fields = fields,
        superMarkers = superMarkers,
    ) {

        // Will be a DataFrame<*>
        override val isObject = true
        override val isList = false

        override fun toFieldType(): FieldType =
            FieldType.GroupFieldType(
                markerName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String): Interface =
            Interface(nullable, fields, superMarkers.values.toList(), name, additionalPropertyPaths, visibility)

        override fun withVisibility(visibility: MarkerVisibility): Interface =
            Interface(nullable, fields, superMarkers.values.toList(), name, additionalPropertyPaths, visibility)
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
        override val isList: Boolean,
        name: String,
        additionalPropertyPaths: List<JsonPath>,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : Interface(
        nullable = nullable,
        name = name,
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
    ) {

        // Will be a DataFrame<out AdditionalProperty>
        override val isObject = true

        override fun toFieldType(): FieldType =
            FieldType.FrameFieldType(
                markerName = name + if (nullable) "?" else "",
                nullable = false,
            )

        override fun withName(name: String): AdditionalPropertyInterface =
            AdditionalPropertyInterface(nullable, valueType, isList, name, additionalPropertyPaths, visibility)

        override fun withVisibility(visibility: MarkerVisibility): AdditionalPropertyInterface =
            AdditionalPropertyInterface(nullable, valueType, isList, name, additionalPropertyPaths, visibility)
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
        override val isList: Boolean,
        name: String,
        val superMarkerName: String,
        override val additionalPropertyPaths: List<JsonPath>,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        nullable = nullable,
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

        override val isObject = false

        override fun toFieldType(): FieldType =
            FieldType.ValueFieldType(
                typeFqName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String): TypeAlias =
            TypeAlias(nullable, isList, name, superMarkerName, additionalPropertyPaths, visibility)

        override fun withVisibility(visibility: MarkerVisibility): TypeAlias =
            TypeAlias(nullable, isList, name, superMarkerName, additionalPropertyPaths, visibility)
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
        val superMarker: OpenApiMarker,
        nullable: Boolean,
        name: String,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : OpenApiMarker(
        nullable = nullable || superMarker.nullable,
        name = name,
        visibility = visibility,
        fields = emptyList(),
        superMarkers = listOf(superMarker),
    ) {

        // depends on the marker it points to whether it's primitive or not
        override val isObject = superMarker.isObject
        override val isList = superMarker.isList

        override val additionalPropertyPaths: List<JsonPath> = superMarker.additionalPropertyPaths

        override fun toFieldType(): FieldType =
            FieldType.GroupFieldType(
                markerName = name + if (nullable) "?" else "",
            )

        override fun withName(name: String): MarkerAlias = MarkerAlias(superMarker, nullable, name, visibility)

        override fun withVisibility(visibility: MarkerVisibility): MarkerAlias =
            MarkerAlias(superMarker, nullable, name, visibility)

        fun withSuperMarker(superMarker: OpenApiMarker): MarkerAlias =
            MarkerAlias(superMarker, nullable, name, visibility)
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
 * (which are queried using `getRefMarker`) for which we have not yet created a [Marker].
 * In that case, we "pause" that one (by returning `CannotFindRefMarker`) and try to read another type schema first.
 * Circular references cannot exist since it's encoded in JSON, so we never get stuck in an infinite loop.
 * When all markers are "retrieved" (so turned from a [RetrievableMarker] to a [MarkerResult.OpenApiMarker]),
 * we're done and have converted everything!
 * As for `produceAdditionalMarker`: In OpenAPI not all enums/objects have to be defined as a separate schema.
 * Although recommended, you can still define an object anonymously directly as a type. For this, we have
 * `produceAdditionalMarker` since during the conversion of a schema -> [Marker] we get an additional new [Marker].
 */
private fun Map<String, Schema<*>>.toMarkers(singletonObjectName: ValidFieldName): List<OpenApiMarker> {
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

/** Either [MarkerResult.CannotFindRefMarker] or [MarkerResult.OpenApiMarker] containing an [org.jetbrains.kotlinx.dataframe.io.OpenApiMarker]. */
private sealed interface MarkerResult {

    /** A schema reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : MarkerResult

    /** Successfully found or created [marker]. */
    data class OpenApiMarker(val marker: org.jetbrains.kotlinx.dataframe.io.OpenApiMarker) : MarkerResult

    companion object {
        fun fromNullable(schema: org.jetbrains.kotlinx.dataframe.io.OpenApiMarker?): MarkerResult =
            if (schema == null) CannotFindRefMarker else OpenApiMarker(schema)
    }
}

/** Represents a query to find a [Marker] with certain name. Produces a [MarkerResult]. */
private fun interface GetRefMarker {

    /** Produces a [MarkerResult] (either [MarkerResult.CannotFindRefMarker] or [MarkerResult.OpenApiMarker]) for the
     * given [refName] representing a query to find a marker with that given name. */
    operator fun invoke(refName: String): MarkerResult
}

/**
 * Represents a call to produce an additional [Marker] from inside a schema component.
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
        /** No-op implementation. Passes through `validName`. */
        val NOOP = ProduceAdditionalMarker { validName, _, _ -> validName.quotedIfNeeded }
    }
}

/**
 * Represents a call to [toMarker] that can be repeated until it returns a [MarkerResult.OpenApiMarker].
 */
private fun interface RetrievableMarker {

    /**
     * Represents a call to [toMarker] that can be repeated until it returns a [MarkerResult.OpenApiMarker].
     *
     * @param getRefMarker              A function that returns a [Marker] for a given reference name if successful.
     * @param produceAdditionalMarker   A function that produces an additional [Marker] for a given name.
     *                                  This is used for `object` types not present in the root of `components/schemas`.
     *
     * @return A [MarkerResult.OpenApiMarker] if successful, otherwise [MarkerResult.CannotFindRefMarker].
     */
    operator fun invoke(
        getRefMarker: GetRefMarker,
        produceAdditionalMarker: ProduceAdditionalMarker,
    ): MarkerResult
}

/** Helper function to create a [GeneratedField] without [GeneratedField.columnSchema]. */
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

/** Helper function to create a [GeneratedField] for enums. */
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
    singletonObjectName: ValidFieldName,
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
                    )
                )
            }

            // Create this object as a map-like type
            properties == null && additionalProperties != null && additionalProperties != false -> {
                val openApiTypeResult = (additionalProperties as? Schema<*>)
                    ?.toOpenApiType(getRefMarker = getRefMarker)

                var isList = false
                val additionalPropertyPaths = mutableListOf<JsonPath>()
                val valueType = when (openApiTypeResult) {
                    is OpenApiTypeResult.CannotFindRefMarker ->
                        return MarkerResult.CannotFindRefMarker

                    is OpenApiTypeResult.UsingRef -> {
                        val marker = openApiTypeResult.marker
                        additionalPropertyPaths += marker.additionalPropertyPaths.map {
                            it.prependWildcard()
                        }
                        isList = marker.isList
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
                            )

                        isList = openApiTypeResult.openApiType.isList

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
                        isList = isList,
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
                        isList = openApiTypeResult.openApiType.isList,
                    )
                }

                is OpenApiTypeResult.Enum -> error("cannot happen, since enum != null is checked earlier")
            }

            MarkerResult.OpenApiMarker(typeAliasMarker)
        }
    }
}

/** Small helper function to produce a new enum Marker. */
private fun produceNewEnum(
    name: String,
    values: List<String>,
    nullable: Boolean,
    produceAdditionalMarker: ProduceAdditionalMarker,
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

/** https://swagger.io/docs/specification/data-models/data-types/#numbers */
private enum class OpenApiIntegerFormat(val value: String) {
    INT32("int32"),
    INT64("int64");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiIntegerFormat? = values().firstOrNull { it.value == value }
    }
}

/** https://swagger.io/docs/specification/data-models/data-types/#numbers */
private enum class OpenApiNumberFormat(val value: String) {
    FLOAT("float"),
    DOUBLE("double");

    companion object {
        fun fromStringOrNull(value: String?): OpenApiNumberFormat? = values().firstOrNull { it.value == value }
    }
}

/** https://swagger.io/docs/specification/data-models/data-types/#string */
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
private sealed class OpenApiType(val name: kotlin.String?) : IsObjectOrList {

    // Used in generation to decide whether something is an object or not.
    override val isObject: kotlin.Boolean
        get() = this is Object || this is AnyObject

    override val isList: kotlin.Boolean
        get() = this is Array

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

/** Either a [OpenApiTypeResult.UsingRef], [OpenApiTypeResult.CannotFindRefMarker], [OpenApiTypeResult.OpenApiType],
 * or [OpenApiTypeResult.Enum]. */
private sealed interface OpenApiTypeResult {

    /** Property is a reference with name [name] and Marker [marker]. Ref cannot be nullable by OpenAPI spec. */
    class UsingRef(val marker: OpenApiMarker) : OpenApiTypeResult

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : OpenApiTypeResult

    /** Property is a schema with OpenApiType [openApiType]. */
    data class OpenApiType(
        val openApiType: org.jetbrains.kotlinx.dataframe.io.OpenApiType,
        val nullable: Boolean,
    ) : OpenApiTypeResult

    /** Property is an enum with values [values]. */
    data class Enum(val values: List<String>, val nullable: Boolean) : OpenApiTypeResult
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
 * @param isRequired whether the property is required by parent schemas.
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

//            // Dataframe turns [1, null, [1,2,3]] into [[1], [], [1,2,3]], so we need to take that into account
//            anyOfTypes.any { it.isList } -> OpenApiType.Array

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

/** Either [FieldTypeResult.CannotFindRefMarker] or [FieldTypeResult.FieldType]. */
private sealed interface FieldTypeResult {

    /** A marker reference cannot be found at this time, try again later. */
    object CannotFindRefMarker : FieldTypeResult

    /** ColumnSchema [fieldType] created successfully. */
    data class FieldType(
        val fieldType: org.jetbrains.kotlinx.dataframe.codeGen.FieldType,
        val additionalPropertyPaths: List<JsonPath> = emptyList(),
    ) : FieldTypeResult
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
    singletonObjectName: ValidFieldName,
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

internal fun String.snakeToLowerCamelCase(): String =
    toCamelCaseByDelimiters(DELIMITERS_REGEX)

internal fun String.snakeToUpperCamelCase(): String =
    snakeToLowerCamelCase()
        .replaceFirstChar { it.uppercaseChar() }

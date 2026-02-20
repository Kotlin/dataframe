package org.jetbrains.kotlinx.dataframe.impl.codeGen

import com.squareup.kotlinpoet.buildCodeBlock
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.codeGen.BaseField
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenResult
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithTypeCastGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.ExtensionsCodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.codeGen.InterfaceGenerationMode.Enum
import org.jetbrains.kotlinx.dataframe.codeGen.InterfaceGenerationMode.NoFields
import org.jetbrains.kotlinx.dataframe.codeGen.InterfaceGenerationMode.None
import org.jetbrains.kotlinx.dataframe.codeGen.InterfaceGenerationMode.TypeAlias
import org.jetbrains.kotlinx.dataframe.codeGen.InterfaceGenerationMode.WithFields
import org.jetbrains.kotlinx.dataframe.codeGen.IsolatedMarker
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerNameProvider
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.codeGen.SchemaProcessor
import org.jetbrains.kotlinx.dataframe.codeGen.TypeCastGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.codeGen.toNullable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.toSnakeCase
import org.jetbrains.kotlinx.dataframe.keywords.HardKeywords
import org.jetbrains.kotlinx.dataframe.keywords.ModifierKeywords
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass

private fun renderNullability(nullable: Boolean) = if (nullable) "?" else ""

internal fun Iterable<Marker>.filterRequiredForSchema(schema: DataFrameSchema) =
    filter { it.isOpen && it.schema.compare(schema, ComparisonMode.STRICT_FOR_NESTED_SCHEMAS).isSuperOrMatches() }

internal val charsToQuote = """[ `(){}\[\].<>'"/|\\!?@:;%^&*#$-]""".toRegex()

/**
 * Simple utility function that creates a [CodeWithTypeCastGenerator] using [code] and [TypeCastGenerator.DataFrameApi],
 * meaning it uses the [cast] functions of DataFrame.
 */
internal fun createCodeWithTypeCastGenerator(code: String, vararg targetTypeNames: String) =
    CodeWithTypeCastGenerator(code, TypeCastGenerator.DataFrameApi(*targetTypeNames))

private val letterCategories = setOf(
    CharCategory.UPPERCASE_LETTER,
    CharCategory.TITLECASE_LETTER,
    CharCategory.MODIFIER_LETTER,
    CharCategory.LOWERCASE_LETTER,
    CharCategory.DECIMAL_DIGIT_NUMBER,
)

internal fun String.needsQuoting(): Boolean =
    if (isQuoted()) {
        false
    } else {
        isBlank() ||
            first().isDigit() ||
            contains(charsToQuote) ||
            HardKeywords.VALUES.contains(this) ||
            ModifierKeywords.VALUES.contains(this) ||
            all { it == '_' } ||
            any { it != '_' && it.category !in letterCategories }
    }

public fun String.isQuoted(): Boolean = startsWith("`") && endsWith("`")

public fun String.quoteIfNeeded(): String = if (needsQuoting()) "`$this`" else this

public fun KClass<*>.quotedQualifiedNameOrNull(): String? {
    fun String.partNeedsQuoting(): Boolean =
        if (isQuoted()) {
            false
        } else {
            isBlank() ||
                contains(charsToQuote) ||
                all { it == '_' } ||
                any { it != '_' && it.category !in letterCategories }
        }

    fun String.quotePartIfNeeded(): String = if (partNeedsQuoting()) "`$this`" else this

    return qualifiedName
        ?.split('.')
        ?.joinToString(".") { it.quotePartIfNeeded() }
}

internal fun List<Code>.join() = joinToString("\n")

/** Strategy to render types. Instances include [FullyQualifiedNames] and [ShortNames]. */
internal interface TypeRenderingStrategy {

    /**
     * How to render a row type. Used as receiver type for column accessors for DSLs.
     * E.g.: `DataRow<Marker>`
     */
    fun renderRowTypeName(markerName: String): String

    /**
     * How to render a columns-container (base type for [DataFrame] and [ColumnSelectionDsl]).
     * Used as receiver type for column accessors.
     * E.g.: `ColumnsContainer<Marker>`
     */
    fun renderColumnsContainerTypeName(markerName: String): String

    /**
     * How to render a column type from a [BaseField]. Used as return type for column accessors.
     * Result will be a [DataColumn] (or [ColumnGroup], which can be seen as a [DataColumn]).
     * E.g.: `DataColumn<DataFrame<Marker>>`
     */
    fun BaseField.renderColumnType(): Code

    /**
     * How to render the field type of [BaseField]. Used as return type for column accessors for DSLs.
     * Result will be either the value type name, a [DataRow] or [DataFrame].
     */
    fun BaseField.renderAccessorFieldType(): Code

    /**
     * How to render the field type of [BaseField]. Used as property type in generated interfaces.
     * Result will be either the value type name, the group field type name or [DataFrame].
     */
    fun BaseField.renderFieldType(): Code
}

internal object FullyQualifiedNames : TypeRenderingStrategy {

    private val dataRow = DataRow::class.qualifiedName!!
    private val columnsContainer = ColumnsContainer::class.qualifiedName!!
    private val dataFrame = DataFrame::class.qualifiedName!!
    private val dataColumn = DataColumn::class.qualifiedName!!
    private val columnGroup = ColumnGroup::class.qualifiedName!!

    override fun renderRowTypeName(markerName: String) = "$dataRow<$markerName>"

    override fun renderColumnsContainerTypeName(markerName: String) = "$columnsContainer<$markerName>"

    override fun BaseField.renderColumnType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                "$dataColumn<${fieldType.typeFqName}>"

            is FieldType.GroupFieldType ->
                "$columnGroup<${fieldType.markerName}>"

            is FieldType.FrameFieldType ->
                "$dataColumn<$dataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}>"
        }

    override fun BaseField.renderAccessorFieldType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                fieldType.typeFqName

            is FieldType.GroupFieldType ->
                "$dataRow<${fieldType.markerName}>"

            is FieldType.FrameFieldType ->
                "$dataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
        }

    override fun BaseField.renderFieldType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                fieldType.typeFqName

            is FieldType.GroupFieldType -> if (fieldType.renderAsObject) {
                fieldType.markerName
            } else {
                renderAccessorFieldType()
            }

            is FieldType.FrameFieldType -> if (fieldType.renderAsList) {
                "List<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
            } else {
                renderAccessorFieldType()
            }
        }
}

internal object ShortNames : TypeRenderingStrategy {

    private val dataRow = DataRow::class.simpleName!!
    private val columnsContainer = ColumnsScope::class.simpleName!!
    private val dataFrame = DataFrame::class.simpleName!!
    private val dataColumn = DataColumn::class.simpleName!!
    private val columnGroup = ColumnGroup::class.simpleName!!

    override fun renderRowTypeName(markerName: String): String = "$dataRow<${markerName.shorten()}>"

    override fun renderColumnsContainerTypeName(markerName: String): String =
        "$columnsContainer<${markerName.shorten()}>"

    override fun BaseField.renderColumnType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                "$dataColumn<${fieldType.typeFqName.shorten()}>"

            is FieldType.GroupFieldType ->
                "$columnGroup<${fieldType.markerName}>"

            is FieldType.FrameFieldType ->
                "$dataColumn<$dataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}>"
        }

    override fun BaseField.renderAccessorFieldType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                fieldType.typeFqName.shorten()

            is FieldType.GroupFieldType ->
                "$dataRow<${fieldType.markerName}>"

            is FieldType.FrameFieldType ->
                "$dataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
        }

    override fun BaseField.renderFieldType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                fieldType.typeFqName.shorten()

            is FieldType.GroupFieldType -> if (fieldType.renderAsObject) {
                fieldType.markerName
            } else {
                renderAccessorFieldType()
            }

            is FieldType.FrameFieldType -> if (fieldType.renderAsList) {
                "List<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
            } else {
                renderAccessorFieldType()
            }
        }

    private fun String.shorten() = removeRedundantQualifier(this)

    private fun removeRedundantQualifier(markerName: String): String {
        val parts = markerName.split('.')
        return if (parts.size == 2 && parts[0] == "kotlin") {
            parts[1]
        } else {
            markerName
        }
    }
}

internal open class ExtensionsCodeGeneratorImpl(private val typeRendering: TypeRenderingStrategy) :
    ExtensionsCodeGenerator,
    TypeRenderingStrategy by typeRendering {

    fun renderStringLiteral(name: String) =
        name
            .replace("\\", "\\\\")
            .replace("$", "\\\$")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

    private fun String.removeQuotes() = this.removeSurrounding("`")

    private fun generatePropertyCode(
        marker: IsolatedMarker,
        shortMarkerName: String,
        typeName: String,
        name: String,
        propertyType: String,
        getter: String,
        visibility: String,
    ): String {
        // jvm name is required to prevent signature clash like this:
        // val DataRow<Type>.name: String
        // val DataRow<Repo>.name: String
        val jvmName = "${shortMarkerName}_${name.removeQuotes()}"
        val typeParameters = marker.typeParameters.let {
            if (it.isNotEmpty() && !it.startsWith(" ")) {
                " $it"
            } else {
                it
            }
        }
        return "${visibility}val$typeParameters $typeName.$name: $propertyType @JvmName(\"${
            renderStringLiteral(jvmName)
        }\") get() = $getter as $propertyType"
    }

    /**
     * nullable properties can be needed when *DECLARED* schema is referenced with nullability:
     * ```
     * @DataSchema
     * data class Schema(val i: Int)
     *
     * @DataSchema
     * data class A(
     *  val prop: Schema?
     * )
     * ```
     * When converted `listOf<A>().toDataFrame(maxDepth=2)` actual schema is
     * ```
     * prop:
     *     i: Int?
     * ```
     * So this sudden `i: Int?` must be somehow handled.
     * However, REPL code generator will not create such a situation. Nullable properties are not needed then
     */
    protected fun generateExtensionProperties(marker: IsolatedMarker, withNullable: Boolean = true): Code {
        val markerName = marker.name
        val markerType = "$markerName${marker.typeArguments}"
        val visibility = renderTopLevelDeclarationVisibility(marker)
        val shortMarkerName = markerName.substring(markerName.lastIndexOf('.') + 1).removeQuotes()
        val nullableShortMarkerName = "Nullable$shortMarkerName"

        fun String.toNullable() = if (this.last() == '?' || this == "*") this else "$this?"

        val declarations = mutableListOf<String>()
        val dfTypename = renderColumnsContainerTypeName(markerType)
        val nullableDfTypename = renderColumnsContainerTypeName(markerType.toNullable())
        val rowTypename = renderRowTypeName(markerType)
        val nullableRowTypename = renderRowTypeName(markerType.toNullable())

        val nullableFields = marker.fields.map {
            it.toNullable()
        }.associateBy { it.columnName }

        marker.fields.sortedBy { it.fieldName.quotedIfNeeded }.forEach {
            val getter = "this[\"${renderStringLiteral(it.columnName)}\"]"
            val name = it.fieldName
            val fieldType = it.renderAccessorFieldType()
            val nullableFieldType = nullableFields[it.columnName]!!.renderAccessorFieldType()
            val columnType = it.renderColumnType()
            val nullableColumnType = nullableFields[it.columnName]!!.renderColumnType()

            declarations.addAll(
                listOf(
                    generatePropertyCode(
                        marker = marker,
                        shortMarkerName = shortMarkerName,
                        typeName = dfTypename,
                        name = name.quotedIfNeeded,
                        propertyType = columnType,
                        getter = getter,
                        visibility = visibility,
                    ),
                    generatePropertyCode(
                        marker = marker,
                        shortMarkerName = shortMarkerName,
                        typeName = rowTypename,
                        name = name.quotedIfNeeded,
                        propertyType = fieldType,
                        getter = getter,
                        visibility = visibility,
                    ),
                ),
            )
            if (withNullable) {
                declarations.addAll(
                    listOf(
                        generatePropertyCode(
                            marker = marker,
                            shortMarkerName = nullableShortMarkerName,
                            typeName = nullableDfTypename,
                            name = name.quotedIfNeeded,
                            propertyType = nullableColumnType,
                            getter = getter,
                            visibility = visibility,
                        ),
                        generatePropertyCode(
                            marker = marker,
                            shortMarkerName = nullableShortMarkerName,
                            typeName = nullableRowTypename,
                            name = name.quotedIfNeeded,
                            propertyType = nullableFieldType,
                            getter = getter,
                            visibility = visibility,
                        ),
                    ),
                )
            }
        }
        return declarations.joinToString("\n")
    }

    override fun generate(marker: IsolatedMarker): CodeWithTypeCastGenerator {
        val code = generateExtensionProperties(marker)
        return createCodeWithTypeCastGenerator(code, marker.name)
    }

    protected fun renderTopLevelDeclarationVisibility(marker: IsolatedMarker) =
        when (marker.visibility) {
            MarkerVisibility.INTERNAL -> "internal "
            MarkerVisibility.IMPLICIT_PUBLIC -> ""
            MarkerVisibility.EXPLICIT_PUBLIC -> "public "
        }

    protected fun renderInternalDeclarationVisibility(marker: IsolatedMarker) =
        when (marker.visibility) {
            MarkerVisibility.INTERNAL -> ""
            MarkerVisibility.IMPLICIT_PUBLIC -> ""
            MarkerVisibility.EXPLICIT_PUBLIC -> "public "
        }
}

internal class CodeGeneratorImpl(typeRendering: TypeRenderingStrategy = FullyQualifiedNames) :
    ExtensionsCodeGeneratorImpl(typeRendering),
    CodeGenerator {
    override fun generate(
        marker: Marker,
        interfaceMode: InterfaceGenerationMode,
        extensionProperties: Boolean,
        readDfMethod: DefaultReadDfMethod?,
    ): CodeWithTypeCastGenerator {
        val code = when (interfaceMode) {
            NoFields, WithFields ->
                generateInterface(
                    marker = marker,
                    fields = interfaceMode == WithFields,
                    readDfMethod = readDfMethod,
                ) + if (extensionProperties) "\n" + generateExtensionProperties(marker) else ""

            Enum -> generateEnum(marker)

            TypeAlias -> generateTypeAlias(marker)

            None -> if (extensionProperties) generateExtensionProperties(marker) else ""
        }

        return createCodeWithTypeCastGenerator(code, marker.name)
    }

    private fun generateTypeAlias(marker: Marker): Code {
        val visibility = renderTopLevelDeclarationVisibility(marker)

        return "${visibility}typealias ${marker.name} = ${marker.superMarkers.keys.single()}"
    }

    private fun generateEnum(marker: Marker): Code {
        val visibility = renderTopLevelDeclarationVisibility(marker)

        val header =
            "${visibility}enum class ${marker.name}(override val value: ${String::class.qualifiedName}) : ${DataSchemaEnum::class.qualifiedName}"

        val fieldNames = mutableSetOf<String>()
        val fieldsDeclaration = marker.fields.mapIndexed { i, it ->
            val originalFieldName = it.fieldName.unquoted.toSnakeCase().uppercase().ifEmpty { "EMPTY_STRING" }
            var fieldName = originalFieldName
            var j = 1
            while (fieldName in fieldNames) {
                fieldName = "${originalFieldName}_${j++}"
            }
            fieldNames += fieldName

            val valueName = it.fieldName.unquoted
            val isLast = i == marker.fields.size - 1

            "    ${ValidFieldName.of(fieldName).quotedIfNeeded}(\"$valueName\")${if (isLast) ";" else ","}"
        }.join()

        val body = if (fieldsDeclaration.isNotBlank()) {
            buildString {
                append(" {\n")
                append(fieldsDeclaration)
                append("\n}")
            }
        } else {
            ""
        }

        return listOf(header + body).join()
    }

    override fun generate(
        schema: DataFrameSchema,
        name: String,
        fields: Boolean,
        extensionProperties: Boolean,
        isOpen: Boolean,
        visibility: MarkerVisibility,
        knownMarkers: Iterable<Marker>,
        readDfMethod: DefaultReadDfMethod?,
        fieldNameNormalizer: NameNormalizer,
        asDataClass: Boolean,
        nestedMarkerNameProvider: MarkerNameProvider,
    ): CodeGenResult {
        val context = SchemaProcessor.create(
            name,
            if (asDataClass) emptyList() else knownMarkers,
            fieldNameNormalizer,
            nestedMarkerNameProvider,
        )
        val marker = context.process(schema, isOpen, visibility)
        val declarations = mutableListOf<Code>()
        context.generatedMarkers.forEach { itMarker ->
            val declaration = if (asDataClass) {
                generateClasses(itMarker)
            } else {
                generateInterface(itMarker, fields, readDfMethod.takeIf { marker == itMarker })
            }
            declarations.add(declaration)
            if (extensionProperties) {
                declarations.add(generateExtensionProperties(itMarker, withNullable = false))
            }
        }
        val code = createCodeWithTypeCastGenerator(declarations.joinToString("\n\n"), marker.name)
        return CodeGenResult(code, context.generatedMarkers)
    }

    private fun generateInterface(marker: Marker, fields: Boolean, readDfMethod: DefaultReadDfMethod? = null): Code {
        val annotationName = DataSchema::class.simpleName

        val visibility = renderTopLevelDeclarationVisibility(marker)
        val propertyVisibility = renderInternalDeclarationVisibility(marker)

        val header =
            "@$annotationName${if (marker.isOpen) "" else "(isOpen = false)"}\n${visibility}interface ${marker.name}"
        val baseInterfacesDeclaration =
            if (marker.superMarkers.isNotEmpty()) {
                " : " + marker.superMarkers
                    .map { it.value.name + it.value.typeArguments }
                    .joinToString()
            } else {
                ""
            }
        val resultDeclarations = mutableListOf<String>()

        val fieldsDeclaration = if (fields) renderFields(marker, propertyVisibility).join() else ""

        val readDfMethodDeclaration = readDfMethod?.toDeclaration(marker, propertyVisibility)

        val body = if (fieldsDeclaration.isNotBlank() || readDfMethodDeclaration?.isNotBlank() == true) {
            buildString {
                append(" {\n")
                append(fieldsDeclaration)
                if (readDfMethodDeclaration != null) {
                    append("\n")
                    val companionObject = buildCodeBlock {
                        add("    ")
                        indent()
                        indent()
                        add(readDfMethodDeclaration)
                    }
                    append(companionObject.toString())
                }
                append("\n}")
            }
        } else {
            " { }"
        }
        resultDeclarations.add(header + baseInterfacesDeclaration + body)
        return resultDeclarations.join()
    }

    private fun generateClasses(marker: Marker): Code {
        val annotationName = DataSchema::class.simpleName

        val visibility = renderTopLevelDeclarationVisibility(marker)
        val propertyVisibility = renderInternalDeclarationVisibility(marker)
        val header =
            "@$annotationName\n${visibility}data class ${marker.name}("

        val fieldsDeclaration = renderFields(marker, propertyVisibility).joinToString(",\n")
        return buildString {
            appendLine(header)
            appendLine(fieldsDeclaration)
            append(")")
        }
    }

    private fun renderFields(marker: Marker, propertyVisibility: String): List<String> =
        marker.fields.map {
            val override = if (it.overrides) "override " else ""
            val columnNameAnnotation = if (it.columnName != it.fieldName.quotedIfNeeded) {
                "    @ColumnName(\"${renderStringLiteral(it.columnName)}\")\n"
            } else {
                ""
            }

            val fieldType = it.renderFieldType()
            "$columnNameAnnotation    ${propertyVisibility}${override}val ${it.fieldName.quotedIfNeeded}: $fieldType"
        }
}

public fun CodeWithTypeCastGenerator.toStandaloneSnippet(packageName: String, additionalImports: List<String>): String =
    declarations.toStandaloneSnippet(packageName, additionalImports)

public fun Code.toStandaloneSnippet(packageName: String, additionalImports: List<String>): String =
    buildString {
        if (packageName.isNotEmpty()) {
            appendLine("package $packageName")
            appendLine()
        }
        appendLine("import org.jetbrains.kotlinx.dataframe.ColumnsScope")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataColumn")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataFrame")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataRow")
        appendLine("import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup")
        appendLine("import org.jetbrains.kotlinx.dataframe.annotations.ColumnName")
        appendLine("import org.jetbrains.kotlinx.dataframe.annotations.DataSchema")
        appendLine("import org.jetbrains.kotlinx.dataframe.api.cast")
        additionalImports.forEach {
            appendLine(it)
        }
        appendLine()
        appendLine(this@toStandaloneSnippet)
    }

public fun CodeGenResult.toStandaloneSnippet(packageName: String, additionalImports: List<String>): String =
    code.toStandaloneSnippet(packageName, additionalImports)

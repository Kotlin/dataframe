package org.jetbrains.kotlinx.dataframe.impl.codeGen

import com.squareup.kotlinpoet.buildCodeBlock
import org.jetbrains.dataframe.impl.codeGen.CodeGenResult
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode.Enum
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode.NoFields
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode.None
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode.TypeAlias
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode.WithFields
import org.jetbrains.dataframe.keywords.HardKeywords
import org.jetbrains.dataframe.keywords.ModifierKeywords
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.codeGen.BaseField
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.ExtensionsCodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.IsolatedMarker
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.codeGen.SchemaProcessor
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.codeGen.toNullable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.toSnakeCase
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.jupyter.api.Code

private fun renderNullability(nullable: Boolean) = if (nullable) "?" else ""

internal fun getRequiredMarkers(schema: DataFrameSchema, markers: Iterable<Marker>) = markers
    .filter { it.isOpen && it.schema.compare(schema).isSuperOrEqual() }

internal val charsToQuote = """[ `(){}\[\].<>'"/|\\!?@:;%^&*#$-]""".toRegex()

internal fun createCodeWithConverter(code: String, markerName: String) =
    CodeWithConverter(code) { "$it.cast<$markerName>()" }

private val letterCategories = setOf(
    CharCategory.UPPERCASE_LETTER,
    CharCategory.TITLECASE_LETTER,
    CharCategory.MODIFIER_LETTER,
    CharCategory.LOWERCASE_LETTER,
    CharCategory.DECIMAL_DIGIT_NUMBER
)

internal fun String.needsQuoting(): Boolean {
    return isBlank() ||
        first().isDigit() ||
        contains(charsToQuote) ||
        HardKeywords.VALUES.contains(this) ||
        ModifierKeywords.VALUES.contains(this) ||
        all { it == '_' } ||
        any { it != '_' && it.category !in letterCategories }
}

internal fun String.quoteIfNeeded() = if (needsQuoting()) "`$this`" else this

internal fun List<Code>.join() = joinToString("\n")

internal interface TypeRenderingStrategy {
    fun renderRowTypename(markerName: String): String
    fun renderDfTypename(markerName: String): String
    fun BaseField.renderColumnType(): Code
    fun BaseField.renderFieldType(asAccessor: Boolean): Code
}

internal object FqNames : TypeRenderingStrategy {

    private val DataRow = DataRow::class.qualifiedName!!
    private val ColumnsContainer = ColumnsContainer::class.qualifiedName!!
    private val DataFrame = DataFrame::class.qualifiedName!!
    private val DataColumn = DataColumn::class.qualifiedName!!
    private val ColumnGroup = ColumnGroup::class.qualifiedName!!

    override fun renderRowTypename(markerName: String) = "$DataRow<$markerName>"

    override fun renderDfTypename(markerName: String) = "$ColumnsContainer<$markerName>"

    override fun BaseField.renderColumnType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                "$DataColumn<${fieldType.typeFqName}>"

            is FieldType.GroupFieldType ->
                "$ColumnGroup<${fieldType.markerName}>"

            is FieldType.FrameFieldType ->
                "$DataColumn<$DataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}>"
        }

    override fun BaseField.renderFieldType(asAccessor: Boolean): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                fieldType.typeFqName

            is FieldType.GroupFieldType ->
                if (asAccessor) "$DataRow<${fieldType.markerName}>"
                else fieldType.markerName

            is FieldType.FrameFieldType ->
                "$DataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
        }
}

internal object ShortNames : TypeRenderingStrategy {

    private val DataRow = DataRow::class.simpleName!!
    private val ColumnsContainer = ColumnsContainer::class.simpleName!!
    private val DataFrame = DataFrame::class.simpleName!!
    private val DataColumn = DataColumn::class.simpleName!!
    private val ColumnGroup = ColumnGroup::class.simpleName!!

    override fun renderRowTypename(markerName: String): String = "$DataRow<${markerName.shorten()}>"

    override fun renderDfTypename(markerName: String): String = "$ColumnsContainer<${markerName.shorten()}>"

    override fun BaseField.renderColumnType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                "$DataColumn<${fieldType.typeFqName.shorten()}>"

            is FieldType.GroupFieldType ->
                "$ColumnGroup<${fieldType.markerName}>"

            is FieldType.FrameFieldType ->
                "$DataColumn<$DataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}>"
        }

    override fun BaseField.renderFieldType(asAccessor: Boolean): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType ->
                fieldType.typeFqName.shorten()

            is FieldType.GroupFieldType ->
                if (asAccessor) "$DataRow<${fieldType.markerName}>"
                else fieldType.markerName

            is FieldType.FrameFieldType ->
                "$DataFrame<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
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

internal open class ExtensionsCodeGeneratorImpl(
    private val typeRendering: TypeRenderingStrategy,
) : ExtensionsCodeGenerator, TypeRenderingStrategy by typeRendering {

    fun renderStringLiteral(name: String) = name
        .replace("\\", "\\\\")
        .replace("$", "\\\$")
        .replace("\"", "\\\"")

    private fun String.removeQuotes() = this.removeSurrounding("`")

    private fun generateExtensionProperties(markers: List<Marker>) = markers.map { generateExtensionProperties(it) }

    private fun generatePropertyCode(
        marker: IsolatedMarker,
        shortMarkerName: String,
        typeName: String,
        name: String,
        propertyType: String,
        getter: String,
        visibility: String,
    ): String {
        val jvmName = "${shortMarkerName}_${name.removeQuotes()}"
        val typeParameters = marker.typeParameters.let {
            if (it.isNotEmpty() && !it.startsWith(" ")) {
                " $it"
            } else {
                it
            }
        }
        return "${visibility}val$typeParameters $typeName.$name: $propertyType @JvmName(\"${renderStringLiteral(jvmName)}\") get() = $getter as $propertyType"
    }

    protected fun generateExtensionProperties(marker: IsolatedMarker): Code {
        val markerName = marker.name
        val markerType = "$markerName${marker.typeArguments}"
        val visibility = renderTopLevelDeclarationVisibility(marker)
        val shortMarkerName = markerName.substring(markerName.lastIndexOf('.') + 1)
        val nullableShortMarkerName = "Nullable$shortMarkerName"

        fun String.toNullable() = if (this.last() == '?') this else "$this?"

        val declarations = mutableListOf<String>()
        val dfTypename = renderDfTypename(markerType)
        val nullableDfTypename = renderDfTypename(markerType.toNullable())
        val rowTypename = renderRowTypename(markerType)
        val nullableRowTypename = renderRowTypename(markerType.toNullable())

        val nullableFields = marker.fields.map {
            it.toNullable()
        }.associateBy { it.columnName }

        marker.fields.sortedBy { it.fieldName.quotedIfNeeded }.forEach {
            val getter = "this[\"${renderStringLiteral(it.columnName)}\"]"
            val name = it.fieldName
            val fieldType = it.renderFieldType(asAccessor = true)
            val nullableFieldType = nullableFields[it.columnName]!!.renderFieldType(asAccessor = true)
            val columnType = it.renderColumnType()
            val nullableColumnType = nullableFields[it.columnName]!!.renderColumnType()

            declarations.addAll(
                listOf(
                    // non nullable
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

                    // nullable
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
                )
            )
        }
        return declarations.joinToString("\n")
    }

    override fun generate(marker: IsolatedMarker): CodeWithConverter {
        val code = generateExtensionProperties(marker)
        return createCodeWithConverter(code, marker.name)
    }

    protected fun renderTopLevelDeclarationVisibility(marker: IsolatedMarker) = when (marker.visibility) {
        MarkerVisibility.INTERNAL -> "internal "
        MarkerVisibility.IMPLICIT_PUBLIC -> ""
        MarkerVisibility.EXPLICIT_PUBLIC -> "public "
    }

    protected fun renderInternalDeclarationVisibility(marker: IsolatedMarker) = when (marker.visibility) {
        MarkerVisibility.INTERNAL -> ""
        MarkerVisibility.IMPLICIT_PUBLIC -> ""
        MarkerVisibility.EXPLICIT_PUBLIC -> "public "
    }
}

internal class CodeGeneratorImpl(typeRendering: TypeRenderingStrategy = FqNames) :
    ExtensionsCodeGeneratorImpl(typeRendering), CodeGenerator {
    override fun generate(
        marker: Marker,
        interfaceMode: InterfaceGenerationMode,
        extensionProperties: Boolean,
        readDfMethod: DefaultReadDfMethod?,
    ): CodeWithConverter {
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

        return createCodeWithConverter(code, marker.name)
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

        val body = if (fieldsDeclaration.isNotBlank()) buildString {
            append(" {\n")
            append(fieldsDeclaration)
            append("\n}")
        } else ""

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
    ): CodeGenResult {
        val context = SchemaProcessor.create(name, knownMarkers, fieldNameNormalizer)
        val marker = context.process(schema, isOpen, visibility)
        val declarations = mutableListOf<Code>()
        context.generatedMarkers.forEach { itMarker ->
            declarations.add(generateInterface(itMarker, fields, readDfMethod.takeIf { marker == itMarker }))
            if (extensionProperties) {
                declarations.add(generateExtensionProperties(itMarker))
            }
        }
        val code = createCodeWithConverter(declarations.joinToString("\n\n"), marker.name)
        return CodeGenResult(code, context.generatedMarkers)
    }

    private fun generateInterfaces(
        schemas: List<Marker>,
        fields: Boolean,
    ) = schemas.map { generateInterface(it, fields) }

    private fun generateInterface(
        marker: Marker,
        fields: Boolean,
        readDfMethod: DefaultReadDfMethod? = null,
    ): Code {
        val annotationName = DataSchema::class.simpleName

        val visibility = renderTopLevelDeclarationVisibility(marker)
        val propertyVisibility = renderInternalDeclarationVisibility(marker)

        val header =
            "@$annotationName${if (marker.isOpen) "" else "(isOpen = false)"}\n${visibility}interface ${marker.name}"
        val baseInterfacesDeclaration =
            if (marker.superMarkers.isNotEmpty()) " : " + marker.superMarkers.map { it.value.name + it.value.typeArguments }
                .joinToString() else ""
        val resultDeclarations = mutableListOf<String>()

        val fieldsDeclaration = if (fields) marker.fields.map {
            val override = if (it.overrides) "override " else ""
            val columnNameAnnotation = if (it.columnName != it.fieldName.quotedIfNeeded) {
                "    @ColumnName(\"${renderStringLiteral(it.columnName)}\")\n"
            } else {
                ""
            }

            val fieldType = it.renderFieldType(asAccessor = false)
            "$columnNameAnnotation    ${propertyVisibility}${override}val ${it.fieldName.quotedIfNeeded}: $fieldType"
        }.join() else ""
        val body = if (fieldsDeclaration.isNotBlank()) buildString {
            append(" {\n")
            append(fieldsDeclaration)
            if (readDfMethod != null) {
                append("\n")
                val companionObject = buildCodeBlock {
                    add("    ")
                    indent()
                    indent()
                    add(readDfMethod.toDeclaration(marker, propertyVisibility))
                }
                append(companionObject.toString())
            }
            append("\n}")
        } else ""
        resultDeclarations.add(header + baseInterfacesDeclaration + body)
        return resultDeclarations.join()
    }
}

public fun CodeWithConverter.toStandaloneSnippet(packageName: String, additionalImports: List<String>): String =
    buildString {
        if (packageName.isNotEmpty()) {
            appendLine("package $packageName")
            appendLine()
        }
        appendLine("import org.jetbrains.kotlinx.dataframe.ColumnsContainer")
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
        appendLine(declarations)
    }

public fun CodeGenResult.toStandaloneSnippet(packageName: String, additionalImports: List<String>): String =
    code.toStandaloneSnippet(packageName, additionalImports)

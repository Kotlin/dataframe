package org.jetbrains.kotlinx.dataframe.impl.codeGen

import com.squareup.kotlinpoet.buildCodeBlock
import org.jetbrains.dataframe.impl.codeGen.CodeGenResult
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode.*
import org.jetbrains.dataframe.impl.codeGen.InterfaceGenerationMode.Enum
import org.jetbrains.dataframe.keywords.HardKeywords
import org.jetbrains.dataframe.keywords.ModifierKeywords
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
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
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.jupyter.api.Code

private fun renderNullability(nullable: Boolean) = if (nullable) "?" else ""

internal fun getRequiredMarkers(schema: DataFrameSchema, markers: Iterable<Marker>) = markers
    .filter { it.isOpen && it.schema.compare(schema).isSuperOrEqual() }

internal val charsToQuote = """[ `(){}\[\].<>'"/|\\!?@:;%^&*#$-]""".toRegex()

internal fun createCodeWithConverter(code: String, markerName: String) = CodeWithConverter(code) { "$it.cast<$markerName>()" }

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
    fun BaseField.renderFieldType(): Code
}

internal object FqNames : TypeRenderingStrategy {
    override fun renderRowTypename(markerName: String) = "${DataRow::class.qualifiedName}<$markerName>"

    override fun renderDfTypename(markerName: String) = "${ColumnsContainer::class.qualifiedName}<$markerName>"

    override fun BaseField.renderColumnType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType -> "${DataColumn::class.qualifiedName}<${fieldType.typeFqName}>"
            is FieldType.GroupFieldType -> "${ColumnGroup::class.qualifiedName}<${fieldType.markerName}>"
            is FieldType.FrameFieldType -> "${DataColumn::class.qualifiedName}<${DataFrame::class.qualifiedName}<${fieldType.markerName}>${renderNullability(fieldType.nullable)}>"
        }

    override fun BaseField.renderFieldType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType -> fieldType.typeFqName
            is FieldType.GroupFieldType -> "${DataRow::class.qualifiedName}<${fieldType.markerName}>"
            is FieldType.FrameFieldType -> "${DataFrame::class.qualifiedName}<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
        }
}

internal object ShortNames : TypeRenderingStrategy {
    override fun renderRowTypename(markerName: String): String {
        return "${DataRow::class.simpleName}<${markerName.shorten()}>"
    }

    override fun renderDfTypename(markerName: String): String {
        return "${ColumnsContainer::class.simpleName}<${markerName.shorten()}>"
    }

    override fun BaseField.renderColumnType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType -> "${DataColumn::class.simpleName}<${fieldType.typeFqName.shorten()}>"
            is FieldType.GroupFieldType -> "${ColumnGroup::class.simpleName}<${fieldType.markerName}>"
            is FieldType.FrameFieldType -> "${DataColumn::class.simpleName}<${DataFrame::class.simpleName}<${fieldType.markerName}>${renderNullability(fieldType.nullable)}>"
        }

    override fun BaseField.renderFieldType(): Code =
        when (val fieldType = fieldType) {
            is FieldType.ValueFieldType -> fieldType.typeFqName.shorten()
            is FieldType.GroupFieldType -> "${DataRow::class.simpleName}<${fieldType.markerName}>"
            is FieldType.FrameFieldType -> "${DataFrame::class.simpleName}<${fieldType.markerName}>${renderNullability(fieldType.nullable)}"
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
    private val typeRendering: TypeRenderingStrategy
) : ExtensionsCodeGenerator, TypeRenderingStrategy by typeRendering {

    fun renderStringLiteral(name: String) = name
        .replace("\\", "\\\\")
        .replace("$", "\\\$")
        .replace("\"", "\\\"")

    private fun String.removeQuotes() = this.removeSurrounding("`")

    private fun generateExtensionProperties(markers: List<Marker>) = markers.map { generateExtensionProperties(it) }

    protected fun generateExtensionProperties(marker: IsolatedMarker): Code {
        val markerName = marker.name
        val markerType = "$markerName${marker.typeArguments}"
        val visibility = renderTopLevelDeclarationVisibility(marker)
        val shortMarkerName = markerName.substring(markerName.lastIndexOf('.') + 1)
        fun generatePropertyCode(
            typeName: String,
            name: String,
            propertyType: String,
            getter: String,
            visibility: String
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

        val declarations = mutableListOf<String>()
        val dfTypename = renderDfTypename(markerType)
        val rowTypename = renderRowTypename(markerType)
        marker.fields.sortedBy { it.fieldName.quotedIfNeeded }.forEach {
            val getter = "this[\"${renderStringLiteral(it.columnName)}\"]"
            val name = it.fieldName
            val fieldType = it.renderFieldType()
            val columnType = it.renderColumnType()
            declarations.add(generatePropertyCode(dfTypename, name.quotedIfNeeded, columnType, getter, visibility))
            declarations.add(generatePropertyCode(rowTypename, name.quotedIfNeeded, fieldType, getter, visibility))
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

internal class CodeGeneratorImpl(typeRendering: TypeRenderingStrategy = FqNames) : ExtensionsCodeGeneratorImpl(typeRendering), CodeGenerator {
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

        val header = "${visibility}enum class ${marker.name}"

        val fieldsDeclaration = marker.fields.mapIndexed { i, it ->
            val isLast = i == marker.fields.size - 1
            "    ${it.fieldName.quotedIfNeeded}${if (isLast) ";" else ","}"
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
        fieldNameNormalizer: NameNormalizer
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
        fields: Boolean
    ) = schemas.map { generateInterface(it, fields) }

    private fun generateInterface(
        marker: Marker,
        fields: Boolean,
        readDfMethod: DefaultReadDfMethod? = null
    ): Code {
        val annotationName = DataSchema::class.simpleName

        val visibility = renderTopLevelDeclarationVisibility(marker)
        val propertyVisibility = renderInternalDeclarationVisibility(marker)

        val header =
            "@$annotationName${if (marker.isOpen) "" else "(isOpen = false)"}\n${visibility}interface ${marker.name}"
        val baseInterfacesDeclaration =
            if (marker.superMarkers.isNotEmpty()) " : " + marker.superMarkers.map { it.value.name }
                .joinToString() else ""
        val resultDeclarations = mutableListOf<String>()

        val fieldsDeclaration = if (fields) marker.fields.map {
            val override = if (it.overrides) "override " else ""
            val columnNameAnnotation =
                if (it.columnName != it.fieldName.quotedIfNeeded) "    @ColumnName(\"${renderStringLiteral(it.columnName)}\")\n" else ""

            val fieldType = it.renderFieldType()
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
                    add(readDfMethod.toDeclaration(marker.shortName, propertyVisibility))
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

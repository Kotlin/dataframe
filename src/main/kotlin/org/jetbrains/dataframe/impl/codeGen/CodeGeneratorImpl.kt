package org.jetbrains.dataframe.impl.codeGen

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.annotations.DataSchema
import org.jetbrains.dataframe.columns.ColumnGroup
import org.jetbrains.dataframe.columns.DataColumn
import org.jetbrains.dataframe.internal.codeGen.*
import org.jetbrains.dataframe.internal.codeGen.SchemaProcessor
import org.jetbrains.dataframe.internal.schema.DataFrameSchema
import org.jetbrains.dataframe.keywords.HardKeywords
import org.jetbrains.dataframe.keywords.ModifierKeywords
import org.jetbrains.kotlinx.jupyter.api.Code

private fun renderNullability(nullable: Boolean) = if (nullable) "?" else ""

internal fun BaseField.renderFieldType(): Code =
    when (val columnInfo = columnInfo) {
        is ValueColumn -> columnInfo.typeFqName
        is org.jetbrains.dataframe.internal.codeGen.ColumnGroup -> "${DataRow::class.qualifiedName}<$markerName>"
        is FrameColumn -> "${DataFrame::class.qualifiedName}<$markerName>${renderNullability(nullable)}"
    }

internal fun getRequiredMarkers(schema: DataFrameSchema, markers: Iterable<Marker>) = markers
    .filter { it.isOpen && it.schema.compare(schema).isSuperOrEqual() }

internal val charsToQuote = """[ `(){}\[\].<>'"/|\\!?@:;%^&*#$-]""".toRegex()

internal fun String.needsQuoting(): Boolean {
    return isBlank() ||
        first().isDigit() ||
        contains(charsToQuote) ||
        HardKeywords.VALUES.contains(this) ||
        ModifierKeywords.VALUES.contains(this)
}

internal fun String.quoteIfNeeded() = if (needsQuoting()) "`$this`" else this

internal fun List<Code>.join() = joinToString("\n")

internal open class ExtensionsCodeGeneratorImpl : ExtensionsCodeGenerator {

    private fun BaseField.renderColumnType(): Code =
        when (val columnInfo = columnInfo) {
            is ValueColumn -> "${DataColumn::class.qualifiedName}<${columnInfo.typeFqName}>"
            is org.jetbrains.dataframe.internal.codeGen.ColumnGroup -> "${ColumnGroup::class.qualifiedName}<$markerName>"
            is FrameColumn -> "${DataColumn::class.qualifiedName}<${DataFrame::class.qualifiedName}<$markerName>${renderNullability(nullable)}>"
        }

    fun renderStringLiteral(name: String) = name
        .replace("\\", "\\\\")
        .replace("$", "\\\$")
        .replace("\"", "\\\"")

    private fun String.removeQuotes() = this.removeSurrounding("`")

    private fun generateExtensionProperties(markers: List<Marker>) = markers.map { generateExtensionProperties(it) }

    protected fun generateExtensionProperties(marker: IsolatedMarker): Code {
        val markerName = marker.name
        val shortMarkerName = markerName.substring(markerName.lastIndexOf('.') + 1)
        fun generatePropertyCode(typeName: String, name: String, propertyType: String, getter: String): String {
            val jvmName = "${shortMarkerName}_${name.removeQuotes()}"
            return "val $typeName.$name: $propertyType @JvmName(\"${renderStringLiteral(jvmName)}\") get() = $getter as $propertyType"
        }

        val declarations = mutableListOf<String>()
        val dfTypename = "${DataFrameBase::class.qualifiedName}<$markerName>"
        val rowTypename = "${DataRowBase::class.qualifiedName}<$markerName>"
        marker.fields.sortedBy { it.fieldName.quotedIfNeeded }.forEach {
            val getter = "this[\"${renderStringLiteral(it.columnName)}\"]"
            val name = it.fieldName
            val fieldType = it.renderFieldType()
            val columnType = it.renderColumnType()
            declarations.add(generatePropertyCode(dfTypename, name.quotedIfNeeded, columnType, getter))
            declarations.add(generatePropertyCode(rowTypename, name.quotedIfNeeded, fieldType, getter))
        }
        return declarations.joinToString("\n")
    }

    override fun generate(marker: IsolatedMarker): CodeWithConverter {
        val code = generateExtensionProperties(marker)
        return CodeWithConverter(code) { "$it.typed<${marker.name}>()" }
    }

    private fun generateInterfaces(
        schemas: List<Marker>,
        fields: Boolean
    ) = schemas.map { generateInterface(it, fields) }

    protected fun generateInterface(
        marker: Marker,
        fields: Boolean
    ): Code {
        val annotationName = DataSchema::class.simpleName

        val header =
            "@$annotationName${if (marker.isOpen) "" else "(isOpen = false)"}\ninterface ${marker.name}"
        val baseInterfacesDeclaration =
            if (marker.baseMarkers.isNotEmpty()) " : " + marker.baseMarkers.map { it.value.name }
                .joinToString() else ""
        val resultDeclarations = mutableListOf<String>()

        val fieldsDeclaration = if (fields) marker.fields.map {
            val override = if (it.overrides) "override " else ""
            val columnNameAnnotation =
                if (it.columnName != it.fieldName.quotedIfNeeded) "\t@ColumnName(\"${renderStringLiteral(it.columnName)}\")\n" else ""

            val fieldType = it.renderFieldType()
            "$columnNameAnnotation    ${override}val ${it.fieldName.quotedIfNeeded}: $fieldType"
        }.join() else ""
        val body = if (fieldsDeclaration.isNotBlank()) "{\n$fieldsDeclaration\n}" else ""
        resultDeclarations.add(header + baseInterfacesDeclaration + body)
        return resultDeclarations.join()
    }
}

internal class CodeGeneratorImpl : ExtensionsCodeGeneratorImpl(), CodeGenerator {
    override fun generate(marker: Marker, interfaceMode: InterfaceGenerationMode, extensionProperties: Boolean): CodeWithConverter {
        val generateInterface = interfaceMode != InterfaceGenerationMode.None
        val code = when {
            generateInterface && extensionProperties -> generateInterface(marker, interfaceMode == InterfaceGenerationMode.WithFields) + "\n" + generateExtensionProperties(
                marker
            )
            generateInterface -> generateInterface(marker, interfaceMode == InterfaceGenerationMode.WithFields)
            extensionProperties -> generateExtensionProperties(marker)
            else -> ""
        }
        return CodeWithConverter(code) { "$it.typed<${marker.name}>()" }
    }

    override fun generate(
        schema: DataFrameSchema,
        name: String,
        fields: Boolean,
        extensionProperties: Boolean,
        isOpen: Boolean,
        knownMarkers: Iterable<Marker>
    ): CodeGenResult {
        val context = SchemaProcessor.create(name, knownMarkers)
        val marker = context.process(schema, isOpen)
        val declarations = mutableListOf<Code>()
        context.generatedMarkers.forEach {
            declarations.add(generateInterface(it, fields))
            if (extensionProperties) {
                declarations.add(generateExtensionProperties(it))
            }
        }
        val code = CodeWithConverter(declarations.join()) { "$it.typed<${marker.name}>()" }
        return CodeGenResult(code, context.generatedMarkers)
    }
}

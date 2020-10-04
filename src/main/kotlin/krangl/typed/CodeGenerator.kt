package krangl.typed

import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

// Public API

@Target(AnnotationTarget.CLASS)
annotation class DataFrameType(val isOpen: Boolean = true)

@Target(AnnotationTarget.PROPERTY)
annotation class ColumnType(val type: KClass<out DataCol>)

@Target(AnnotationTarget.PROPERTY)
annotation class ColumnName(val name: String)

enum class CodeGenerationMode {
    FullNames,
    ShortNames
}

data class DataFrameToListNamedStub(val df: TypedDataFrame<*>, val className: String)

data class DataFrameToListTypedStub(val df: TypedDataFrame<*>, val interfaceClass: KClass<*>)

fun <T> TypedDataFrame<T>.getScheme(name: String? = null, columnSelector: ColumnsSelector<T,*>? = null): String {
    val interfaceName = name ?: "DataRecord"
    val cols = columnSelector?.let { getColumns(it).map { this[it.name] } } ?: columns
    return CodeGenerator().generateInterfaceDeclaration(cols, interfaceName, withBaseInterfaces = false, isOpen = true)
}

interface CodeGeneratorApi {
    fun generate(df: TypedDataFrame<*>, property: KProperty<*>): List<String>
    fun generate(stub: DataFrameToListNamedStub): List<String>
    fun generate(stub: DataFrameToListTypedStub): List<String>

    fun generate(interfaze: KClass<*>): List<String>

    var mode: CodeGenerationMode

}

// Implementation

class CodeGenerator : CodeGeneratorApi {

    companion object {
        val Default: CodeGeneratorApi = CodeGenerator()
    }

    // Data Frame Schema
    private data class FieldInfo(val columnName: String, val fieldName: String, val fieldType: KType, val columnType: KType) {

        fun isSubFieldOf(other: FieldInfo) =
                columnName == other.columnName && fieldType.isSubtypeOf(other.fieldType) && columnType.isSubtypeOf(other.columnType)
    }

    private class Scheme(val values: List<FieldInfo>) {

        val byColumn: Map<String, FieldInfo> = values.associateBy { it.columnName }

        val byField: Map<String, FieldInfo> = values.associateBy { it.fieldName }

        fun contains(field: FieldInfo) = byField[field.fieldName]?.equals(field) ?: false
        fun isSuperTo(other: Scheme) =
                values.all {
                    other.byColumn[it.columnName]?.isSubFieldOf(it) ?: false
                }

        override fun equals(other: Any?): Boolean {
            val scheme = other as? Scheme ?: return false
            if (scheme.values.size != values.size) return false
            return values.all {
                val otherEntry = other.byColumn[it.columnName] ?: return false
                otherEntry.equals(it)
            }
        }

        override fun hashCode(): Int {
            return values.sortedBy { it.fieldName }.hashCode()
        }

    }

    private fun getColumnType(valueType: KType) = ColumnData::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, valueType)))

    private fun getFields(clazz: KClass<*>, withBaseTypes: Boolean): Map<String, FieldInfo> {
        val result = mutableMapOf<String, FieldInfo>()
        if (withBaseTypes)
            clazz.superclasses.forEach { result.putAll(getFields(it, withBaseTypes)) }

        result.putAll(clazz.declaredMemberProperties.mapIndexed { index, it ->
            val fieldName = it.name
            val columnName = it.findAnnotation<ColumnName>()?.name ?: fieldName
            val columnValueType = it.findAnnotation<ColumnType>()?.type?.createType() ?: it.returnType
            val columnType = getColumnType(columnValueType)
            fieldName to FieldInfo(columnName, fieldName, it.returnType, columnType)
        })
        return result
    }

    private fun getScheme(clazz: KClass<*>, withBaseTypes: Boolean) = Scheme(getFields(clazz, withBaseTypes).values.toList())

    private val charsToQuote = """[ {}()<>'"/|.\\!?@:;%^&*#$-]""".toRegex()

    private fun generateValidFieldName(name: String, index: Int, usedNames: Collection<String>): String {
        var result = name
        val needsQuote = name.contains(charsToQuote)
        if (needsQuote) {
            result = name.replace("<", "{")
                    .replace(">", "}")
                    .replace("::", " - ")
                    .replace(": ", " - ")
                    .replace(":", " - ")
                    .replace(".", " ")
                    .replace("/", "-")
                    .let { "`$it`" }
        }
        if (result.isEmpty()) result = "_$index"
        val baseName = result
        result = if (needsQuote) "`$baseName`" else baseName
        var attempt = 2
        while (usedNames.contains(result)) {
            result = if (needsQuote) "`$baseName ($attempt)`" else "${baseName}_$attempt"
            attempt++
        }
        return result
    }

    private fun getScheme(columns: Iterable<DataCol>): Scheme {
        val generatedFieldNames = mutableSetOf<String>()
        return Scheme(columns.mapIndexed { index, it ->
            val fieldName = generateValidFieldName(it.name, index, generatedFieldNames)
            generatedFieldNames.add(fieldName)
            FieldInfo(it.name, fieldName, it.type, getColumnType(it.type))
        })
    }

    private val TypedDataFrame<*>.scheme: Scheme
        get() = getScheme(columns)

    // Rendering

    override var mode = CodeGenerationMode.ShortNames

    private fun shortTypeName(type: KType) =
            if (type.arguments.isNotEmpty()) null
            else type.jvmErasure.simpleName + if (type.isMarkedNullable) "?" else ""

    private fun render(clazz: KClass<*>) = when (mode) {
        CodeGenerationMode.ShortNames -> clazz.simpleName
        CodeGenerationMode.FullNames -> clazz.qualifiedName
    }

    private fun render(type: KType) = when (mode) {
        CodeGenerationMode.FullNames -> type.toString()
        CodeGenerationMode.ShortNames -> shortTypeName(type) ?: type.toString()
    }

    fun renderColumnName(name: String) = name
            .replace("\\", "\\\\")
            .replace("$", "\\\$")
            .replace("\"", "\\\"")

    // Generated marker interfaces tracking

    private data class GeneratedMarker(val scheme: Scheme, val kclass: KClass<*>, val isOpen: Boolean)

    private val generatedMarkers = mutableListOf<GeneratedMarker>()

    private fun Scheme.getAllBaseMarkers() = generatedMarkers
            .filter { it.scheme.isSuperTo(this) }

    private fun List<GeneratedMarker>.onlyLeafs(): List<GeneratedMarker> {
        val skip = flatMap { it.kclass.allSuperclasses }.toSet()
        return filter { !skip.contains(it.kclass) }
    }

    private fun Scheme.getRequiredBaseMarkers() = generatedMarkers
            .filter { it.isOpen && it.scheme.isSuperTo(this) }

    // Code Generation

    private fun generateExtensionProperties(scheme: Scheme, markerType: String): List<String> {

        fun generatePropertyCode(typeName: String, name: String, propertyType: String, getter: String): String {
            return "val $typeName.$name: $propertyType get() = ($getter) as $propertyType"
        }

        val declarations = mutableListOf<String>()
        val dfTypename = render(TypedDataFrame::class) + "<$markerType>"
        val rowTypename = render(TypedDataFrameRow::class) + "<$markerType>"
        scheme.values.sortedBy { it.columnName }.forEach { field ->
            val getter = "this[\"${field.columnName}\"]"
            val name = field.fieldName
            val valueType = render(field.fieldType)
            val columnType = render(field.columnType)
            declarations.add(generatePropertyCode(dfTypename, name, columnType, getter))
            declarations.add(generatePropertyCode(rowTypename, name, valueType, getter))
        }
        return declarations
    }

    override fun generate(interfaze: KClass<*>): List<String> {
        val annotation = interfaze.findAnnotation<DataFrameType>() ?: return emptyList()
        val ownSet = getScheme(interfaze, withBaseTypes = false)
        val fullSet = getScheme(interfaze, withBaseTypes = true)
        val typeName = interfaze.qualifiedName!!
        val result = generateExtensionProperties(ownSet, typeName)
        generatedMarkers.add(GeneratedMarker(fullSet, interfaze, annotation.isOpen))
        return result
    }

    private val processedProperties = mutableSetOf<KProperty<*>>()

    override fun generate(df: TypedDataFrame<*>, property: KProperty<*>): List<String> {

        fun KClass<*>.implements(targetBaseMarkers: Iterable<KClass<*>>): Boolean {
            val superclasses = allSuperclasses + this
            return targetBaseMarkers.all { superclasses.contains(it) }
        }

        val markerType: String?
        var targetScheme = df.scheme
        val wasProcessedBefore = property in processedProperties
        processedProperties.add(property)
        val isMutable = property is KMutableProperty

        // maybe property is already properly typed, let's do some checks
        val currentMarkerType = getMarkerType(property.returnType)
        if (currentMarkerType != null) {
            // if property is mutable, we need to make sure that its marker type is open in order to let data frames with more columns be assignable to it
            if (!isMutable || currentMarkerType.findAnnotation<DataFrameType>()?.isOpen == true) {
                val markerScheme = getScheme(currentMarkerType, withBaseTypes = true)
                // for mutable properties we do strong typing only at the first processing, after that we allow its type to be more general than actual data frame type
                if (wasProcessedBefore || markerScheme == targetScheme) {
                    // property scheme is valid for current data frame, but we should also check that all compatible open markers are implemented by it
                    val requiredBaseMarkers = markerScheme.getRequiredBaseMarkers().map { it.kclass }
                    if (currentMarkerType.implements(requiredBaseMarkers))
                        return emptyList()
                    // use current marker scheme as a target for generation of new marker interface, so that available properties won't change
                    targetScheme = markerScheme
                }
            }
        }

        // property needs to be recreated. First, try to find existing marker for it
        val declarations = mutableListOf<String>()
        val requiredBaseMarkers = targetScheme.getRequiredBaseMarkers().map { it.kclass }
        val existingMarker = generatedMarkers.firstOrNull {
            isMutable == it.isOpen && it.scheme.equals(targetScheme) && it.kclass.implements(requiredBaseMarkers)
        }
        if (existingMarker != null) {
            markerType = existingMarker.kclass.qualifiedName
        } else {
            markerType = "DataFrameType###"
            declarations.add(this.generateInterfaceDeclaration(targetScheme, markerType, withBaseInterfaces = true, isOpen = isMutable))
        }

        val converter = "\$it.typed<$markerType>()"
        declarations.add(converter)
        return declarations
    }

    private fun getMarkerType(dataFrameType: KType) =
            when (dataFrameType.jvmErasure) {
                TypedDataFrame::class -> dataFrameType.arguments[0].type?.jvmErasure
                else -> null
            }

    private enum class FieldGenerationMode { declare, override, skip }

    private fun computeFieldGenerationModes(scheme: Scheme, requiredBaseMarkers: List<GeneratedMarker>): List<Pair<FieldInfo, FieldGenerationMode>> {
        return scheme.values.map { field ->
            val fieldName = field.fieldName
            var generationMode = FieldGenerationMode.declare
            for (baseScheme in requiredBaseMarkers) {
                val baseField = baseScheme.scheme.byField[fieldName]
                if (baseField != null) {
                    generationMode = if (baseField.fieldType == field.fieldType) FieldGenerationMode.skip
                    else if (field.fieldType.isSubtypeOf(baseField.fieldType)) {
                        generationMode = FieldGenerationMode.override
                        break
                    } else throw Exception()
                }
            }
            field to generationMode
        }
    }

    private fun generateInterfaceDeclaration(scheme: Scheme, name: String, withBaseInterfaces: Boolean, isOpen: Boolean): String {

        val markers = mutableListOf<GeneratedMarker>()
        val fields = if (withBaseInterfaces) {
            markers += scheme.getRequiredBaseMarkers().onlyLeafs()
            val generatedFields = computeFieldGenerationModes(scheme, markers)

            // try to reduce number of generated fields by implementing some additional interfaces
            val remainedFields = generatedFields.filter { it.second == FieldGenerationMode.declare }.map { it.first }.toMutableList()
            var markersAdded = false

            if (remainedFields.size > 0) {
                val availableMarkers = scheme.getAllBaseMarkers().toMutableList()
                availableMarkers -= markers

                while (remainedFields.size > 0) {
                    val bestMarker = availableMarkers.map { marker -> marker to remainedFields.count { marker.scheme.contains(it) } }.maxBy { it.second }
                    if (bestMarker != null && bestMarker.second > 0) {
                        remainedFields.removeAll { bestMarker.first.scheme.byField[it.fieldName]?.fieldType == it.fieldType }
                        markers += bestMarker.first
                        markersAdded = true
                        availableMarkers -= bestMarker.first
                    } else break
                }
            }
            if (markersAdded) computeFieldGenerationModes(scheme, markers) else generatedFields
        } else scheme.values.map { it to FieldGenerationMode.declare }

        val leafMarkers = markers.onlyLeafs()
        val header = "@DataFrameType${if(isOpen) "" else "(isOpen = false)"}\ninterface $name"
        val baseInterfacesDeclaration = if (leafMarkers.isNotEmpty()) " : " + leafMarkers.map { it.kclass.qualifiedName!! }.joinToString() else ""
        val fieldsDeclaration = fields.filter { it.second != FieldGenerationMode.skip }.map {
            val field = it.first
            val override = when (it.second) {
                FieldGenerationMode.declare -> ""
                FieldGenerationMode.override -> "override "
                FieldGenerationMode.skip -> throw Exception()
            }
            val columnNameAnnotation = if (field.columnName != field.fieldName) "\t@ColumnName(\"${renderColumnName(field.columnName)}\")\n" else ""
            val columnTypeAnnotation = if (field.columnType != getColumnType(field.fieldType)) "\t@ColumnType(${render(field.columnType)}::class)\n" else ""
            val valueType = render(field.fieldType)
            "${columnNameAnnotation}${columnTypeAnnotation}    ${override}val ${field.fieldName}: $valueType"
        }.joinToString("\n")
        val body = if (fieldsDeclaration.isNotBlank()) "{\n$fieldsDeclaration\n}" else ""
        return header + baseInterfacesDeclaration + body
    }

    internal fun generateInterfaceDeclaration(columns: Iterable<DataCol>, name: String, withBaseInterfaces: Boolean, isOpen: Boolean) = generateInterfaceDeclaration(getScheme(columns), name, withBaseInterfaces, isOpen)

    // DataFrame -> List converters

    private fun generateToListConverter(className: String, columnNames: List<String>, scheme: Scheme, interfaceName: String? = null): List<String> {
        val override = if (interfaceName != null) "override " else ""
        val baseTypes = if (interfaceName != null) " : $interfaceName" else ""
        val classDeclaration = "data class ${className}(" +
                columnNames.map {
                    val field = scheme.byColumn[it]!!
                    "${override}val ${field.fieldName}: ${render(field.fieldType)}"
                }.joinToString() + ") " + baseTypes

        val converter = "\$it.df.rows.map { $className(" +
                columnNames.map {
                    val field = scheme.byColumn[it]!!
                    "it[\"${field.columnName}\"] as ${render(field.fieldType)}"
                }.joinToString() + ")}"

        return listOf(classDeclaration, converter)
    }

    override fun generate(stub: DataFrameToListTypedStub): List<String> {
        val df = stub.df
        val dfScheme = df.scheme
        val interfaceScheme = getScheme(stub.interfaceClass, withBaseTypes = true)
        if (!interfaceScheme.isSuperTo(dfScheme))
            throw Exception()
        val interfaceName = stub.interfaceClass.simpleName!!
        val interfaceFullName = stub.interfaceClass.qualifiedName!!
        val className = interfaceName + "Impl"
        val columnNames = interfaceScheme.byColumn.keys.toList()

        return generateToListConverter(className, columnNames, dfScheme, interfaceFullName)
    }

    override fun generate(stub: DataFrameToListNamedStub) =
            generateToListConverter(stub.className, stub.df.columns.map { it.name }, stub.df.scheme, null)

}

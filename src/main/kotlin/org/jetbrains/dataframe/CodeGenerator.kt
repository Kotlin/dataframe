package org.jetbrains.dataframe

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

fun <T> TypedDataFrame<T>.schema(name: String? = null): String {
    val interfaceName = name ?: "DataRecord"
    return CodeGenerator().generateInterfaceDeclarations(columns, interfaceName, withBaseInterfaces = false, isOpen = true).joinToString("\n")
}

interface CodeGeneratorApi {
    fun generate(df: TypedDataFrame<*>, property: KProperty<*>): List<String>
    fun generate(df: TypedDataFrame<*>): List<String>
    fun generate(stub: DataFrameToListNamedStub): List<String>
    fun generate(stub: DataFrameToListTypedStub): List<String>

    fun generate(marker: KClass<*>): List<String>

    var mode: CodeGenerationMode

}

// Implementation

class CodeGenerator : CodeGeneratorApi {

    companion object {
        val Default: CodeGeneratorApi = CodeGenerator()

        private val GroupedColumnType: KClass<*> = GroupedColumnBase::class

        private val GroupedFieldType: KClass<*> = TypedDataFrameRow::class

        private val DataFrameFieldType: KClass<*> = TypedDataFrame::class

    }

    private enum class CompareResult {
        Equals,
        IsSuper,
        IsDerived,
        None;

        fun isSuperOrEqual() = this == Equals || this == IsSuper

        fun isDerivedOrEqual() = this == Equals || this == IsDerived

        fun isEqual() = this == Equals

        fun combine(other: CompareResult) =
                when (this) {
                    Equals -> other
                    None -> this
                    IsDerived -> if (other == Equals || other == IsDerived) this else None
                    IsSuper -> if (other == Equals || other == IsSuper) this else None
                }
    }

    // Data Frame Schema
    private data class FieldInfo(val fieldName: String, val columnName: String, private val type: KType?, val columnKind: ColumnKind = ColumnKind.Default, val childScheme: Scheme? = null) {

        init {
            when(columnKind) {
                ColumnKind.Default -> assert(type != null && childScheme == null)
                ColumnKind.Group -> assert(childScheme != null)
                ColumnKind.Table -> assert(childScheme != null)
            }
        }

        val columnType: KType get() = when(columnKind) {
            ColumnKind.Default -> ColumnData::class.createType(type!!)
            ColumnKind.Group -> GroupedColumnType.createType(type)
            ColumnKind.Table -> ColumnData::class.createType(DataFrameFieldType.createType(type))
        }

        val fieldType: KType get() = when(columnKind) {
            ColumnKind.Default -> type!!
            ColumnKind.Group -> GroupedFieldType.createType(type)
            ColumnKind.Table -> DataFrameFieldType.createType(type)
        }

        fun compare(other: FieldInfo): CompareResult {
            if(fieldName != other.fieldName || columnName != other.columnName || columnKind != other.columnKind) return CompareResult.None
            if(childScheme == null) {
                if(other.childScheme != null) return CompareResult.None
                if(type == other.type) return CompareResult.Equals
                if(type!!.isSubtypeOf(other.type!!)) return CompareResult.IsDerived
                if(type.isSupertypeOf(other.type)) return CompareResult.IsSuper
                return CompareResult.None
            }
            if(other.childScheme == null) return CompareResult.None
            return childScheme.compare(other.childScheme)
        }
    }

    private class Scheme(val values: List<FieldInfo>) {

        val byColumn: Map<String, FieldInfo> by lazy { values.associateBy { it.columnName } }

        val byField: Map<String, FieldInfo> by lazy { values.associateBy { it.fieldName } }

        fun contains(field: FieldInfo) = byField[field.fieldName]?.equals(field) ?: false

        fun compare(other: Scheme): CompareResult {
            if(this === other) return CompareResult.Equals
            var result = CompareResult.Equals
            values.forEach {
                val otherField = other.byColumn[it.columnName]
                if(otherField == null)
                    result = result.combine(CompareResult.IsDerived)
                else
                    result = result.combine(it.compare(otherField))
                if(result == CompareResult.None) return result
            }
            other.values.forEach {
                val thisField = byColumn[it.columnName]
                if(thisField == null) {
                    result = result.combine(CompareResult.IsSuper)
                    if (result == CompareResult.None) return result
                }
            }
            return result
        }

        override fun hashCode(): Int {
            return values.sortedBy { it.fieldName }.hashCode()
        }

    }

    private fun String.quoteIfNeeded() = if(contains(charsToQuote)) "`$this`" else this

    private fun getFields(marker: KClass<*>, withBaseTypes: Boolean): Map<String, FieldInfo> {
        val result = mutableMapOf<String, FieldInfo>()
        if (withBaseTypes)
            marker.superclasses.forEach { result.putAll(getFields(it, withBaseTypes)) }

        result.putAll(marker.declaredMemberProperties.mapIndexed { index, it ->
            val fieldName = it.name.quoteIfNeeded()
            val columnName = it.findAnnotation<ColumnName>()?.name ?: fieldName
            var valueType = it.returnType
            val valueClass = valueType.jvmErasure
            var marker: GeneratedMarker? = null
            var columnKind = when(valueClass) {
                GroupedFieldType -> ColumnKind.Group
                DataFrameFieldType -> ColumnKind.Table
                else -> ColumnKind.Default
            }
            if(columnKind != ColumnKind.Default) {
                val typeArgument = valueType.arguments[0].type!!
                if (isMarkerType(typeArgument.jvmErasure)) {
                    marker = getMarkerScheme(typeArgument.jvmErasure)
                    valueType = typeArgument
                }
                else columnKind = ColumnKind.Default
            }
            fieldName to FieldInfo(fieldName, columnName, valueType, columnKind, marker?.fullScheme)
        })
        return result
    }

    private fun getScheme(marker: KClass<*>, withBaseTypes: Boolean) = Scheme(getFields(marker, withBaseTypes).values.toList())

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
            var type: KType? = it.type
            var childScheme : Scheme? = null
            var columnKind = ColumnKind.Default
            when {
                it is GroupedColumn<*> -> {
                    childScheme = it.df.schema
                    type = null
                    columnKind = ColumnKind.Group
                }
                it is TableColumn<*> -> {
                    childScheme = it.df.schema
                    type = null
                    columnKind = ColumnKind.Table
                }
            }
            FieldInfo(fieldName, it.name, type, columnKind, childScheme)
        })
    }

    private val TypedDataFrame<*>.schema: Scheme
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

    private data class GeneratedMarker(val fullScheme: Scheme, val ownScheme: Scheme, val kclass: KClass<*>, val isOpen: Boolean)

    private val registeredMarkers = mutableMapOf<KClass<*>,GeneratedMarker>()

    private val registeredMarkerClassNames = mutableSetOf<String>()

    private fun Scheme.getAllBaseMarkers() = registeredMarkers.values
            .filter { it.fullScheme.compare(this).isSuperOrEqual() }

    private fun List<GeneratedMarker>.onlyLeafs(): List<GeneratedMarker> {
        val skip = flatMap { it.kclass.allSuperclasses }.toSet()
        return filter { !skip.contains(it.kclass) }
    }

    private fun Scheme.getRequiredBaseMarkers() = registeredMarkers.values
            .filter { it.isOpen && it.fullScheme.compare(this).isSuperOrEqual() }

    // Code Generation

    private fun generateExtensionProperties(scheme: Scheme, markerType: String): List<String> {

        fun generatePropertyCode(typeName: String, name: String, propertyType: String, getter: String): String {
            return "val $typeName.$name: $propertyType get() = $getter as $propertyType"
        }

        val declarations = mutableListOf<String>()
        val dfTypename = render(DataFrameBase::class) + "<$markerType>"
        val rowTypename = render(DataFrameRowBase::class) + "<$markerType>"
        scheme.values.sortedBy { it.columnName }.forEach { field ->
            val getter = "this[\"${field.columnName}\"]"
            val name = field.fieldName
            val fieldType = render(field.fieldType)
            val columnType = render(field.columnType)
            declarations.add(generatePropertyCode(dfTypename, name, columnType, getter))
            declarations.add(generatePropertyCode(rowTypename, name, fieldType, getter))
        }
        return declarations
    }

    private fun isMarkerType(marker: KClass<*>) = marker.hasAnnotation<DataFrameType>()

    private fun getMarkerScheme(marker: KClass<*>) =
        registeredMarkers.getOrPut(marker) {
            val annotation = marker.findAnnotation<DataFrameType>() ?: throw Exception()
            val fullSet = getScheme(marker, withBaseTypes = true)
            val ownProperties = marker.declaredMemberProperties.map { it.name.quoteIfNeeded() }.toSet()
            val ownSet = Scheme(fullSet.values.filter { ownProperties.contains(it.fieldName) })
            val simpleName = marker.simpleName!!
            registeredMarkerClassNames.add(simpleName)
            GeneratedMarker(fullSet, ownSet, marker, annotation.isOpen)
        }

    override fun generate(marker: KClass<*>): List<String> {
        val generatedMarker = getMarkerScheme(marker)
        val qualifiedName = marker.qualifiedName!!
        return generateExtensionProperties(generatedMarker.ownScheme, qualifiedName)
    }

    private val processedProperties = mutableSetOf<KProperty<*>>()

    private fun generateUniqueMarkerClassName(usedNames: Set<String> = setOf()): String {
        var id = 1
        val prefix = "DataFrameType"
        while (registeredMarkerClassNames.contains("$prefix$id") || usedNames.contains("$prefix$id"))
            id++
        return "$prefix$id"
    }

    private fun KClass<*>.implements(targetBaseMarkers: Iterable<KClass<*>>): Boolean {
        val superclasses = allSuperclasses + this
        return targetBaseMarkers.all { superclasses.contains(it) }
    }

    override fun generate(df: TypedDataFrame<*>) = generate(df.schema, false)

    override fun generate(df: TypedDataFrame<*>, property: KProperty<*>): List<String> {

        var targetScheme = df.schema
        val wasProcessedBefore = property in processedProperties
        processedProperties.add(property)
        val isMutable = property is KMutableProperty

        // maybe property is already properly typed, let's do some checks
        val currentMarkerType = getMarker(property.returnType)
        if (currentMarkerType != null) {
            // if property is mutable, we need to make sure that its marker type is open in order to let data frames with more columns be assignable to it
            if (!isMutable || currentMarkerType.findAnnotation<DataFrameType>()?.isOpen == true) {
                val markerScheme = getScheme(currentMarkerType, withBaseTypes = true)
                // for mutable properties we do strong typing only at the first processing, after that we allow its type to be more general than actual data frame type
                if (wasProcessedBefore || markerScheme.compare(targetScheme).isEqual()) {
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
        return generate(targetScheme, isMutable)
    }

    private fun generate(scheme: Scheme, isMutable: Boolean): MutableList<String> {
        val declarations = mutableListOf<String>()
        val markerType = findOrCreateMarker(scheme, isMutable, mutableSetOf(), declarations)
        val converter = "\$it.typed<$markerType>()"
        declarations.add(converter)
        return declarations
    }

    private fun findOrCreateMarker(targetScheme: Scheme, isMutable: Boolean, usedNames: MutableSet<String>, declarations: MutableList<String>): String {
        val markerType: String
        val requiredBaseMarkers = targetScheme.getRequiredBaseMarkers().map { it.kclass }
        val existingMarker = registeredMarkers.values.firstOrNull {
            (!isMutable || it.isOpen) && it.fullScheme.compare(targetScheme).isEqual() && it.kclass.implements(requiredBaseMarkers)
        }
        if (existingMarker != null) {
            markerType = existingMarker.kclass.qualifiedName!!
        } else {
            markerType = generateUniqueMarkerClassName(usedNames)
            usedNames.add(markerType)
            declarations.addAll(generateInterfaceDeclarations(targetScheme, markerType, usedNames, withBaseInterfaces = true, isOpen = isMutable))
        }
        return markerType
    }

    private fun getMarker(dataFrameType: KType) =
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
                val baseField = baseScheme.fullScheme.byField[fieldName]
                if (baseField != null) {

                    val childSchemeComparison = if(field.childScheme != null) {
                        if(baseField.childScheme != null) field.childScheme.compare(baseField.childScheme) else CompareResult.Equals
                    }
                    else if(baseField.childScheme == null) CompareResult.None else CompareResult.Equals

                    generationMode = if (baseField.fieldType == field.fieldType || (baseField.columnKind == field.columnKind && childSchemeComparison == CompareResult.Equals)) FieldGenerationMode.skip
                    else if (field.fieldType.isSubtypeOf(baseField.fieldType) || (baseField.columnKind == field.columnKind && childSchemeComparison == CompareResult.IsDerived)) {
                        generationMode = FieldGenerationMode.override
                        break
                    } else throw Exception()
                }
            }
            field to generationMode
        }
    }

    private fun generateInterfaceDeclarations(scheme: Scheme, name: String, usedNames: MutableSet<String>, withBaseInterfaces: Boolean, isOpen: Boolean): List<String> {

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
                    val bestMarker = availableMarkers.map { marker -> marker to remainedFields.count { marker.fullScheme.contains(it) } }.maxBy { it.second }
                    if (bestMarker != null && bestMarker.second > 0) {
                        remainedFields.removeAll { bestMarker.first.fullScheme.byField[it.fieldName]?.fieldType == it.fieldType }
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
        val resultDeclarations = mutableListOf<String>()

        val fieldsDeclaration = fields.filter { it.second != FieldGenerationMode.skip }.map {
            val field = it.first
            val override = when (it.second) {
                FieldGenerationMode.declare -> ""
                FieldGenerationMode.override -> "override "
                FieldGenerationMode.skip -> throw Exception()
            }
            val columnNameAnnotation = if (field.columnName != field.fieldName) "\t@ColumnName(\"${renderColumnName(field.columnName)}\")\n" else ""

            val fieldType = when(field.columnKind) {
                ColumnKind.Group -> {
                    val markerType = findOrCreateMarker(field.childScheme!!, false, usedNames, resultDeclarations)
                    "${render(GroupedFieldType)}<$markerType>"
                }
                ColumnKind.Table -> {
                    val markerType = findOrCreateMarker(field.childScheme!!, false, usedNames, resultDeclarations)
                    "${render(DataFrameFieldType)}<$markerType>"
                }
                else -> render(field.fieldType)
            }

            "${columnNameAnnotation}    ${override}val ${field.fieldName}: $fieldType"
        }.joinToString("\n")
        val body = if (fieldsDeclaration.isNotBlank()) "{\n$fieldsDeclaration\n}" else ""
        resultDeclarations.add(header + baseInterfacesDeclaration + body)
        return resultDeclarations
    }

    internal fun generateInterfaceDeclarations(columns: Iterable<DataCol>, name: String, withBaseInterfaces: Boolean, isOpen: Boolean) = generateInterfaceDeclarations(getScheme(columns), name, mutableSetOf(), withBaseInterfaces, isOpen)

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
        val dfScheme = df.schema
        val interfaceScheme = getScheme(stub.interfaceClass, withBaseTypes = true)
        if (!interfaceScheme.compare(dfScheme).isSuperOrEqual())
            throw Exception()
        val interfaceName = stub.interfaceClass.simpleName!!
        val interfaceFullName = stub.interfaceClass.qualifiedName!!
        val className = interfaceName + "Impl"
        val columnNames = interfaceScheme.byColumn.keys.toList()

        return generateToListConverter(className, columnNames, dfScheme, interfaceFullName)
    }

    override fun generate(stub: DataFrameToListNamedStub) =
            generateToListConverter(stub.className, stub.df.columns.map { it.name }, stub.df.schema, null)

}

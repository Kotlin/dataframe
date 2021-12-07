package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.BaseField
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.ExtensionsCodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.IsolatedMarker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName

@OptIn(KspExperimental::class)
internal fun renderExtensions(interfaceName: String, visibility: MarkerVisibility, properties: List<Property>): String {
    val generator = ExtensionsCodeGenerator.create()
    return generator.generate(object : IsolatedMarker {
        override val name: String = interfaceName
        override val fields: List<BaseField> = properties.map {
            val type = it.propertyType.resolve()
            val fqName = type.declaration.qualifiedName?.asString() ?: error("")
            val fieldType = when {
                fqName == "kotlin.collections.List" && type.singleTypeArgumentIsDataSchema() ||
                    fqName == DataFrameNames.DATA_FRAME -> FieldType.FrameFieldType(type.renderTypeArguments(), type.isMarkedNullable)
                type.declaration.isAnnotationPresent(DataSchema::class) -> FieldType.GroupFieldType(type.render())
                fqName == DataFrameNames.DATA_ROW -> FieldType.GroupFieldType(type.renderTypeArguments())
                else -> FieldType.ValueFieldType(type.render())
            }

            BaseFieldImpl(
                fieldName = ValidFieldName.of(it.fieldName),
                columnName = it.columnName,
                fieldType = fieldType
            )
        }

        private fun KSType.singleTypeArgumentIsDataSchema() =
            innerArguments.singleOrNull()?.type?.resolve()?.declaration?.isAnnotationPresent(DataSchema::class) ?: false

        override val visibility: MarkerVisibility = visibility
    }).declarations
}

private fun KSType.render(): String {
    val fqName = declaration.qualifiedName?.asString() ?: error("")
    return buildString {
        append(fqName)
        if (innerArguments.isNotEmpty()) {
            append("<")
            append(renderTypeArguments())
            append(">")
        }
        if (isMarkedNullable) {
            append("?")
        }
    }
}

private fun KSType.renderTypeArguments(): String = innerArguments.joinToString(", ") { render(it) }

private fun render(typeArgument: KSTypeArgument): String {
    return when (val variance = typeArgument.variance) {
        Variance.STAR -> variance.label
        Variance.INVARIANT -> renderRecursively(typeArgument.type ?: error("typeArgument.type should only be null for Variance.STAR"))
        Variance.COVARIANT, Variance.CONTRAVARIANT -> "${variance.label} ${renderRecursively(typeArgument.type ?: error("typeArgument.type should only be null for Variance.STAR"))}"
    }
}

private fun renderRecursively(typeReference: KSTypeReference): String {
    val type = typeReference.resolve()
    val fqName = type.declaration.qualifiedName?.asString() ?: error("")
    return buildString {
        append(fqName)
        if (type.innerArguments.isNotEmpty()) {
            append("<")
            val renderedArguments = type.innerArguments.joinToString(", ") { render(it) }
            append(renderedArguments)
            append(">")
        }
        if (type.isMarkedNullable) {
            append("?")
        }
    }
}

internal class Property(val columnName: String, val fieldName: String, val propertyType: KSTypeReference)

internal class BaseFieldImpl(
    override val fieldName: ValidFieldName,
    override val columnName: String,
    override val fieldType: FieldType
) : BaseField

package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractMarker
import org.jetbrains.kotlinx.dataframe.codeGen.BaseField
import org.jetbrains.kotlinx.dataframe.codeGen.ExtensionsCodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName

@OptIn(KspExperimental::class)
internal fun renderExtensions(
    declaration: KSClassDeclaration,
    interfaceName: String,
    visibility: MarkerVisibility,
    properties: List<Property>,
): String {
    val generator = ExtensionsCodeGenerator.create()
    val typeArguments = declaration.typeParameters.map {
        it.name.asString()
    }

    val typeParameters = declaration.typeParameters.map {
        buildString {
            append(it.name.asString())
            val bounds = it.bounds.toList()
            if (bounds.isNotEmpty()) {
                append(" : ")
                append(bounds.joinToString(",") { it.resolve().render() })
            }
        }
    }
    return generator.generate(object : AbstractMarker(typeParameters, typeArguments) {
        override val name: String = interfaceName
        override val fields: List<BaseField> = properties.map {
            val type = it.propertyType.resolve()
            val qualifiedTypeReference = getQualifiedTypeReference(type)
            val fieldType = when {
                qualifiedTypeReference == "kotlin.collections.List" && type.singleTypeArgumentIsDataSchema() ||
                    qualifiedTypeReference == DataFrameNames.DATA_FRAME ->
                    FieldType.FrameFieldType(
                        type.renderTypeArguments(),
                        type.isMarkedNullable
                    )

                type.declaration.isAnnotationPresent(DataSchema::class) -> FieldType.GroupFieldType(type.render())
                qualifiedTypeReference == DataFrameNames.DATA_ROW -> FieldType.GroupFieldType(type.renderTypeArguments())
                else -> FieldType.ValueFieldType(type.render())
            }

            BaseFieldImpl(
                fieldName = ValidFieldName.of(it.fieldName),
                columnName = it.columnName,
                fieldType = fieldType
            )
        }

        override val visibility: MarkerVisibility = visibility
    }).declarations
}

private fun getQualifiedTypeReference(type: KSType) = when (val declaration = type.declaration) {
    is KSTypeParameter -> declaration.name.getShortName()
    else -> declaration.getQualifiedNameOrThrow()
}

@OptIn(KspExperimental::class)
private fun KSType.singleTypeArgumentIsDataSchema() =
    innerArguments.singleOrNull()?.type?.resolve()?.declaration?.isAnnotationPresent(DataSchema::class) ?: false

fun KSType.render(): String {
    val fqTypeReference = getQualifiedTypeReference(this)
    return buildString {
        append(fqTypeReference)
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
        Variance.INVARIANT, Variance.COVARIANT, Variance.CONTRAVARIANT -> buildString {
            append(variance.label)
            if (variance.label.isNotEmpty()) {
                append(" ")
            }
            append(
                typeArgument.type?.resolve()?.render()
                    ?: error("typeArgument.type should only be null for Variance.STAR")
            )
        }
    }
}

internal class Property(val columnName: String, val fieldName: String, val propertyType: KSTypeReference)

internal class BaseFieldImpl(
    override val fieldName: ValidFieldName,
    override val columnName: String,
    override val fieldType: FieldType,
) : BaseField

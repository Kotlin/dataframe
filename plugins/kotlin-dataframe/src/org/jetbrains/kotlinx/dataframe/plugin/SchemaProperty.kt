package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.plugin.utils.Names

data class SchemaProperty(
    val marker: ConeTypeProjection,
    val propertyName: PropertyName,
    val dataRowReturnType: ConeKotlinType,
    val columnContainerReturnType: ConeKotlinType,
    val override: Boolean = false
)

data class PropertyName(val identifier: Name, val columnNameAnnotation: FirAnnotation?) {
    companion object {
        fun of(name: String): PropertyName {
            val valid = ValidFieldName.of(name)
            var columnName = false
            val identifier = if (valid.unquoted != name) {
                columnName = true
                Name.identifier(valid.unquoted)
            } else {
                Name.identifier(name)
            }
            val columnNameAnnotation: FirAnnotation? = if (columnName) {
                buildAnnotation(name)
            } else {
                null
            }
            return PropertyName(identifier, columnNameAnnotation)
        }

        fun buildAnnotation(name: String): FirAnnotation {
            return org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation {
                annotationTypeRef = buildResolvedTypeRef {
                    type = Names.COLUMN_NAME_ANNOTATION.defaultType(emptyList())
                }
                argumentMapping = buildAnnotationArgumentMapping {
                    mapping[Names.COLUMN_NAME_ARGUMENT] = buildLiteralExpression(
                        source = null,
                        kind = ConstantValueKind.String,
                        value = name,
                        setType = true
                    )
                }
            }
        }

        fun of(identifier: Name, columnNameAnnotation: FirAnnotation?): PropertyName {
            return PropertyName(identifier, columnNameAnnotation)
        }
    }
}

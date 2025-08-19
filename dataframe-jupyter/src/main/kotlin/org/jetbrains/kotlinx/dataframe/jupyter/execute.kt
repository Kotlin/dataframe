package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithTypeCastGenerator
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.VariableName
import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal fun KotlinKernelHost.execute(
    codeWithTypeCastGenerator: CodeWithTypeCastGenerator,
    expression: String,
): VariableName? {
    val code = codeWithTypeCastGenerator.declarationsWithCastExpression(expression)
    return if (code.isNotBlank()) {
        val result = execute(code)
        if (codeWithTypeCastGenerator.hasCaster) {
            result.name
        } else {
            null
        }
    } else {
        null
    }
}

internal fun KotlinKernelHost.execute(
    codeWithTypeCastGenerator: CodeWithTypeCastGenerator,
    property: KProperty<*>,
    type: KType,
): VariableName? {
    val variableName = "(${property.name}${if (property.returnType.isMarkedNullable) "!!" else ""} as $type)"
    return execute(codeWithTypeCastGenerator, variableName)
}

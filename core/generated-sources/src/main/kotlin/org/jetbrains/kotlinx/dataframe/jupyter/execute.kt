package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelHost
import org.jetbrains.kotlinx.jupyter.api.VariableName
import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal fun KotlinKernelHost.execute(codeWithConverter: CodeWithConverter, argument: String): VariableName? {
    val code = codeWithConverter.with(argument)
    return if (code.isNotBlank()) {
        val result = execute(code)
        if (codeWithConverter.hasConverter) {
            result.name
        } else {
            null
        }
    } else {
        null
    }
}

internal fun KotlinKernelHost.execute(
    codeWithConverter: CodeWithConverter,
    property: KProperty<*>,
    type: KType,
): VariableName? {
    val variableName = "(${property.name}${if (property.returnType.isMarkedNullable) "!!" else ""} as $type)"
    return execute(codeWithConverter, variableName)
}

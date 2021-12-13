package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.symbol.KSDeclaration

fun KSDeclaration.getQualifiedNameOrThrow(): String {
    return (qualifiedName ?: error("@DataSchema declaration ${simpleName.asString()} at $location must have qualified name")).asString()
}

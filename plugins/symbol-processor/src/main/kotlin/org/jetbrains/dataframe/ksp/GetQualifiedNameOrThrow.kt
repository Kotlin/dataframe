package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import org.jetbrains.kotlinx.dataframe.impl.codeGen.quoteIfNeeded

fun KSDeclaration.getQualifiedNameOrThrow(): String =
    qualifiedName
        ?.let {
            buildString {
                val qualifier = it.getQualifier()
                if (qualifier.isNotEmpty()) {
                    for (it in qualifier.split('.')) {
                        append(it.quoteIfNeeded() + '.')
                    }
                }

                append(it.getShortName().quoteIfNeeded())
            }
        }
        ?: error("@DataSchema declaration ${simpleName.asString()} at $location must have qualified name")

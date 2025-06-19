package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithTypeCastGenerator
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal interface ReplCodeGenerator {

    fun process(df: AnyFrame, property: KProperty<*>? = null): CodeWithTypeCastGenerator

    fun process(row: AnyRow, property: KProperty<*>? = null): CodeWithConverter
    fun process(row: AnyRow, property: KProperty<*>? = null): CodeWithTypeCastGenerator

    fun process(markerClass: KClass<*>): Code

    companion object {
        fun create(): ReplCodeGenerator = ReplCodeGeneratorImpl()
    }
}

internal inline fun <reified T> ReplCodeGenerator.process(): Code = process(T::class)

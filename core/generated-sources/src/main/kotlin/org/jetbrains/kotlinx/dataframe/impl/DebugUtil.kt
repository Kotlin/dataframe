package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.api.ValueCount
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.map

// Needed to attach an expanded node with lazily evaluated expressions to DataFrame debug view
@Suppress("unused")
@RequiredByIntellijPlugin
internal class Info(val df: AnyFrame)

internal class Counts(val value: Any?, val count: Int) {
    override fun toString(): String = "$value -> $count"
}

@RequiredByIntellijPlugin
internal fun DataFrame<ValueCount>.render(): List<Counts> = map { Counts(it[0], it.count) }

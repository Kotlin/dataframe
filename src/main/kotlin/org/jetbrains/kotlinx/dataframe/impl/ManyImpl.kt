package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.Many

internal class ManyImpl<out T>(val list: List<T>) : List<T> by list, Many<T> {

    override fun toString() = list.toString()

    override fun equals(other: Any?) = list.equals(other)

    override fun hashCode() = list.hashCode()
}

internal val EmptyMany: Many<Nothing> = ManyImpl(emptyList())

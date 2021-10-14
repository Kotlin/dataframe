package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.owner

public fun AnyRow.isEmpty(): Boolean = owner.columns().all { it[index] == null }
public fun AnyRow.isNotEmpty(): Boolean = !isEmpty()

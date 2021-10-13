package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.owner

public fun AnyRow.isEmpty(): Boolean = owner.columns().all { it[index] == null }
public fun AnyFrame.isEmpty(): Boolean = ncol == 0 || nrow == 0
public fun AnyRow.isNotEmpty(): Boolean = !isEmpty()
public fun AnyFrame.isNotEmpty(): Boolean = !isEmpty()

package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyFrame

public class ColumnNameGenerator(columnNames: List<String> = emptyList()) {

    private val usedNames = columnNames.toMutableSet()

    private val colNames = columnNames.toMutableList()

    public fun addUnique(preferredName: String): String {
        var name = preferredName
        var k = 1
        while (usedNames.contains(name)) {
            name = "${preferredName}${k++}"
        }
        usedNames.add(name)
        colNames.add(name)
        return name
    }

    public fun addIfAbsent(name: String) {
        if (!usedNames.contains(name)) {
            usedNames.add(name)
            colNames.add(name)
        }
    }

    public val names: List<String>
        get() = colNames

    public operator fun contains(name: String): Boolean = usedNames.contains(name)
}

internal fun AnyFrame.nameGenerator() = ColumnNameGenerator(columnNames())

internal fun nameGenerator(vararg usedNames: String) = ColumnNameGenerator(usedNames.asList())

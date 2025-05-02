package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataRow
import kotlin.reflect.KProperty

/**
 * All generated extension properties for the data schema are accessed through these special delegated properties.
 *
 * We cannot use normal extension properties due to https://youtrack.jetbrains.com/issue/KT-77202/K2-Repl-Local-Extension-Properties-are-not-supported,
 * But delegated properties should be supported in both the K1 and K2 version of the REPL.
 */
public class ColumnsContainerGeneratedPropertyDelegate<R : ColumnsContainer<*>, T>(private val name: String) {
    public operator fun getValue(thisRef: R, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return thisRef[name] as T
    }
}

public class DataRowGeneratedPropertyDelegate<R : DataRow<*>, T>(private val name: String) {
    public operator fun getValue(thisRef: R, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return thisRef[name] as T
    }
}

public class ColumnsScopeGeneratedPropertyDelegate<R : ColumnsScope<*>, T>(private val name: String) {
    public operator fun getValue(thisRef: R, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return thisRef[name] as T
    }
}

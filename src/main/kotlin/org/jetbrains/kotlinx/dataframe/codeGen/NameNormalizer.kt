package org.jetbrains.kotlinx.dataframe.codeGen

public class NameNormalizer(private val f: (String) -> String): (String) -> String by f {
    public companion object
}

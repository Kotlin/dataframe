package org.jetbrains.kotlinx.dataframe.codeGen

public interface DefaultReadDfMethod {
    public fun toDeclaration(marker: Marker, visibility: String): String

    public val additionalImports: List<String>
}

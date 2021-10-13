package org.jetbrains.kotlinx.dataframe.internal.codeGen

import org.jetbrains.kotlinx.dataframe.impl.codeGen.ExtensionsCodeGeneratorImpl

public interface ExtensionsCodeGenerator {
    public fun generate(marker: IsolatedMarker): CodeWithConverter

    public companion object {
        public fun create(): ExtensionsCodeGenerator = ExtensionsCodeGeneratorImpl()
    }
}

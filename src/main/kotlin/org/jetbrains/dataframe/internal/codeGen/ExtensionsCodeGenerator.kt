package org.jetbrains.dataframe.internal.codeGen

import org.jetbrains.dataframe.impl.codeGen.ExtensionsCodeGeneratorImpl

public interface ExtensionsCodeGenerator {
    public fun generate(marker: IsolatedMarker): CodeWithConverter

    public companion object {
        public fun create(): ExtensionsCodeGenerator = ExtensionsCodeGeneratorImpl()
    }
}

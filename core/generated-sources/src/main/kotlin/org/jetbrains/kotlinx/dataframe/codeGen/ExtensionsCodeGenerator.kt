package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.impl.codeGen.ExtensionsCodeGeneratorImpl
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ShortNames

public interface ExtensionsCodeGenerator {
    public fun generate(marker: IsolatedMarker): CodeWithConverter

    public companion object {
        /**
         * When creating extension methods for use outside the REPL, [createJvmGetterOverrides] should be `true`
         * to avoid name clashes. If inside the REPL, these will be processed in different snippets and thus overrides
         * are not needed. This is a work-around for https://youtrack.jetbrains.com/issue/KT-77305/REPL-support-local-delegated-extension-properties-with-the-same-name
         */
        public fun create(createJvmGetterOverrides: Boolean = false): ExtensionsCodeGenerator =
            ExtensionsCodeGeneratorImpl(ShortNames, createJvmGetterOverrides)
    }
}

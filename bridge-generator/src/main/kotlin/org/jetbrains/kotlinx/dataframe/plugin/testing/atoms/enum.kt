package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.plugin.*
import org.jetbrains.kotlinx.dataframe.plugin.testing.test

@Interpretable(EnumIdentity::class)
public fun enum(v: Infer): Infer {
    return v
}

public class EnumIdentity : AbstractInterpreter<Infer>() {
    internal val Arguments.v: Infer by enum()

    override fun Arguments.interpret(): Infer {
        return v
    }
}

internal fun enumTest() {
    test(id = "enum_1", enum(Infer.Type))
}

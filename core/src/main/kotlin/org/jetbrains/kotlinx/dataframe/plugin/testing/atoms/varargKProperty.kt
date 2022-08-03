package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.plugin.testing.test
import kotlin.reflect.KProperty

@Interpretable(VarargKPropertyIdentity::class)
public fun <C> varargKProperty(vararg v: KProperty<C>): Array<out KProperty<C>> {
    return v
}

public class VarargKPropertyIdentity : AbstractInterpreter<List<TypeApproximation>>() {
    internal val Arguments.v: List<TypeApproximation> by arg()
    override fun Arguments.interpret(): List<TypeApproximation> {
        return v
    }
}

internal interface KProperties {
    val col1: Int
    val col2: Int?
    val col3: Any?
}

internal fun varargKPropertyTest() {
    test(id = "varargKProperty_0", call = varargKProperty(KProperties::col1, KProperties::col2))
}

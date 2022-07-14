package org.jetbrains.kotlinx.dataframe.plugin.testing

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.plugin.*
import kotlin.reflect.KProperty

@Interpretable(KpropertyIdentity::class)
public fun <R> kproperty(v: KProperty<R>): KProperty<R> {
    return v
}

public class KpropertyIdentity : AbstractInterpreter<KPropertyApproximation>() {
    internal val Arguments.v: KPropertyApproximation by kproperty()

    override fun Arguments.interpret(): KPropertyApproximation {
        return v
    }
}

internal interface Schema {
    val i: Int
    @ColumnName("name")
    val wwff: Int
}

internal fun kpropertyTest() {
    test(id = "kproperty_1", kproperty(Schema::i))
    test(id = "kproperty_2", kproperty(Schema::wwff))
}

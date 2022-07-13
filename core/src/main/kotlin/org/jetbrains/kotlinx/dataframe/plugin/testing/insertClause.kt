package org.jetbrains.kotlinx.dataframe.plugin.testing

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.api.InsertClause
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.plugin.*
import org.jetbrains.kotlinx.dataframe.plugin.InsertClauseApproximation

@Interpretable(InsertClauseIdentity::class)
public fun <T> insertClause(v: InsertClause<T>): InsertClause<T> {
    return v
}

internal class InsertClauseIdentity : AbstractInterpreter<InsertClauseApproximation>() {
    internal val Arguments.v: InsertClauseApproximation by insertClause()

    override fun Arguments.interpret(): InsertClauseApproximation {
        return v
    }
}

internal fun insertClauseTest() {
    val df = dataFrameOf("a")(1)
    test(id = "insert_1", call = df.insert("b") { 42 })
}

internal fun main() {
    val df = dataFrameOf("a")(1)
    val insertClause = df.insert("b") { 42 }
    insertClause
}

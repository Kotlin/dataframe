package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.impl.api.predicateJoinImpl

public interface JoinedDataRow<out A, out B> : DataRow<A> {
    public val right: DataRow<B>
}

public typealias JoinExpression<A, B> = Selector<JoinedDataRow<A, B>, Boolean>

public fun <A, B> DataFrame<A>.predicateJoin(
    right: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    joinExpression: JoinExpression<A, B>
): DataFrame<A> {
    return predicateJoinImpl(right, type, addNewColumns = type.addNewColumns, joinExpression)
}

public fun <A, B> DataFrame<A>.innerPredicateJoin(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>
): DataFrame<A> = predicateJoin(right, JoinType.Inner, joinExpression)

public fun <A, B> DataFrame<A>.leftPredicateJoin(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>
): DataFrame<A> = predicateJoin(right, JoinType.Left, joinExpression)

public fun <A, B> DataFrame<A>.rightPredicateJoin(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>
): DataFrame<A> = predicateJoin(right, JoinType.Right, joinExpression)

public fun <A, B> DataFrame<A>.fullPredicateJoin(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>
): DataFrame<A> = predicateJoin(right, JoinType.Full, joinExpression)

public fun <A, B> DataFrame<A>.filterPredicateJoin(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>
): DataFrame<A> = predicateJoinImpl(right, JoinType.Inner, addNewColumns = false, joinExpression)

public fun <A, B> DataFrame<A>.excludePredicateJoin(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>
): DataFrame<A> = predicateJoinImpl(right, JoinType.Exclude, addNewColumns = false, joinExpression)

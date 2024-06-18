package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.impl.api.joinWithImpl

public interface JoinedDataRow<out A, out B> : DataRow<A> {
    public val right: DataRow<B>
}

public typealias JoinExpression<A, B> = Selector<JoinedDataRow<A, B>, Boolean>

public fun <A, B> DataFrame<A>.joinWith(
    right: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, type, addNewColumns = type.addNewColumns, joinExpression)

public fun <A, B> DataFrame<A>.innerJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWith(right, JoinType.Inner, joinExpression)

public fun <A, B> DataFrame<A>.leftJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWith(right, JoinType.Left, joinExpression)

public fun <A, B> DataFrame<A>.rightJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWith(right, JoinType.Right, joinExpression)

public fun <A, B> DataFrame<A>.fullJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWith(right, JoinType.Full, joinExpression)

public fun <A, B> DataFrame<A>.filterJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, JoinType.Inner, addNewColumns = false, joinExpression)

public fun <A, B> DataFrame<A>.excludeJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, JoinType.Exclude, addNewColumns = false, joinExpression)

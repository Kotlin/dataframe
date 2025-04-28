package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.impl.api.joinWithImpl

public interface JoinedDataRow<out A, out B> : DataRow<A> {
    public val right: DataRow<B>
}

public typealias JoinExpression<A, B> = Selector<JoinedDataRow<A, B>, Boolean>

@Refine
@Interpretable("JoinWith")
public fun <A, B> DataFrame<A>.joinWith(
    right: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, type, addNewColumns = type.addNewColumns, joinExpression)

@Refine
@Interpretable("InnerJoinWith")
public fun <A, B> DataFrame<A>.innerJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Inner, joinExpression)

@Refine
@Interpretable("LeftJoinWith")
public fun <A, B> DataFrame<A>.leftJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Left, joinExpression)

@Refine
@Interpretable("RightJoinWith")
public fun <A, B> DataFrame<A>.rightJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Right, joinExpression)

@Refine
@Interpretable("FullJoinWith")
public fun <A, B> DataFrame<A>.fullJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWith(right, JoinType.Full, joinExpression)

@Refine
@Interpretable("FilterJoinWith")
public fun <A, B> DataFrame<A>.filterJoinWith(right: DataFrame<B>, joinExpression: JoinExpression<A, B>): DataFrame<A> =
    joinWithImpl(right, JoinType.Inner, addNewColumns = false, joinExpression)

@Refine
@Interpretable("ExcludeJoinWith")
public fun <A, B> DataFrame<A>.excludeJoinWith(
    right: DataFrame<B>,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> = joinWithImpl(right, JoinType.Exclude, addNewColumns = false, joinExpression)

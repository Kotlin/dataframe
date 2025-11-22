package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.nrow

internal class ComparisonDescription(
    val rowAtIndex: Int,
    val of: String,
    val wasRemoved: Boolean?,
    val wasInserted: Boolean?,
    val afterRow: Int?,
) : DataRowSchema

/**
 * Returns a DataFrame whose rows explain the differences between dfA and dfB.
 * One must think of the set of commands in a script as being executed simultaneously
 */
internal fun <T> compareDataFramesImpl(dfA: DataFrame<T>, dfB: DataFrame<T>): DataFrame<ComparisonDescription> {
    var comparisonDf = emptyDataFrame<ComparisonDescription>()
    // compare by exploiting Myers difference algorithm
    val shortestEditScript = myersDifferenceAlgorithmImpl(dfA, dfB)
    var x: Int?
    var y: Int?
    var xPrev: Int?
    var yPrev: Int?
    for (i in 1 until shortestEditScript.size) {
        x = shortestEditScript[i].first
        y = shortestEditScript[i].second
        xPrev = shortestEditScript[i - 1].first
        yPrev = shortestEditScript[i - 1].second
        when {
            // row at index 'x-1' of dfA was removed
            xPrev + 1 == x && yPrev + 1 != y -> {
                comparisonDf = comparisonDf.concat(
                    dataFrameOf
                        (ComparisonDescription(x - 1, "dfA", true, null, null)),
                )
            }

            // row at index 'y-1' of dfB was inserted after row in position 'x-1' of dfA
            yPrev + 1 == y && xPrev + 1 != x -> {
                comparisonDf = comparisonDf.concat(
                    dataFrameOf(
                        ComparisonDescription
                            (y - 1, "dfB", null, true, x - 1),
                    ),
                )
            }
        }
    }
    return comparisonDf
}

/**
 * dfs with same schema. Returns an optimal path from origin to (N,M) in the edit graph.
 * N is dfA.nrow, M is dfB.nrow.
 * Knowing this path is knowing the differences between dfA and dfB
 * and the shortest edit script to get B from A.
 * The cost of this alg's worst case in O( (N+M)D ), D is the length of shortest edit script.
 *
 * The idea of the algorithm is the following: try to cross the edit graph making 'd' non-diagonal moves,
 * increase 'd' until you succeed.
 * Non-diagonal moves make edit script longer, while diagonal moves do not.
 *
 * snake: non-diagonal edge and then a possibly empty sequence of diagonal edges
 * D-path: a path starting at (0,0) that has exactly D non-diagonal edges
 */
internal fun <T> myersDifferenceAlgorithmImpl(dfA: DataFrame<T>, dfB: DataFrame<T>): List<Pair<Int, Int>> {
    // Return value
    val path = mutableListOf<Pair<Int, Int>>()
    // 'ses' stands for shortest edit script, next var is never returned, it is in the code
    // to show the capabilities of the algorithm
    var sesLength: Int?
    val sumOfLength = dfA.nrow + dfB.nrow
    // matrix containing the endpoint of the furthest reaching D-path ending in diagonal k
    // for each d-k couple of interest
    val v = arrayListOf<IntArray>()
    for (d in 0..sumOfLength) {
        v.add(IntArray(sumOfLength * 2 + 1))
    }
    var isOver = false
    // starting the algorithm
    // 0 position is -(M+N) position in the alg's paper -> need to normalize each access to v
    val normalizer = sumOfLength
    v[0][1 + normalizer] = 0 // fitticious
    // d is the number of non-diagonal edges
    var d = 0
    while (d <= sumOfLength && !isOver) {
        for (k in -d..d step 2) {
            var x: Int?
            // Each furthest reaching D-path ending in diagonal k
            // is built by exploiting the furthest reaching (D-1)-path ending in k-1 or (exclusive or) k+1
            if (k == -d || k != d && v[d][k - 1 + normalizer] < v[d][k + 1 + normalizer]) {
                x = v[d][k + 1 + normalizer]
            } else {
                x = v[d][k - 1 + normalizer] + 1
            }
            var y = x - k
            while (x < dfA.nrow && y < dfB.nrow && dfA[x] == dfB[y]) {
                x += 1
                y += 1
            }
            v[d][k + normalizer] = x
            // need this data in the next iteration
            if (d < sumOfLength) {
                v[d + 1][k + normalizer] = x
            }
            // Edit graph was fully crossed
            if (x >= dfA.nrow && y >= dfB.nrow) {
                isOver = true
                sesLength = d
                recoursivePathFill(path, v, d, k, normalizer, dfA, dfB)
                break
            }
        }
        // try with a longer edit script
        d++
    }
    val immutablePath = path.toList()
    return immutablePath
}

internal fun <T> recoursivePathFill(
    path: MutableList<Pair<Int, Int>>,
    v: ArrayList<IntArray>,
    d: Int,
    k: Int,
    normalizer: Int,
    dfA: DataFrame<T>,
    dfB: DataFrame<T>,
) {
    // Enlist my self
    val xCurrent = v[d][k + normalizer]
    val yCurrent = xCurrent - k
    path.add(Pair(xCurrent, yCurrent))
    // I look for endpoint I was built from, it is represented by kPrev.
    // It will be an argument of the next recoursive step.
    // Moreover, I need to enlist the points composing the snake that precedes me (it may be empty).
    if (d > 0) {
        var kPrev: Int? = null
        var xSnake: Int? = null
        if (k == -d || k != d && v[d][k - 1 + normalizer] < v[d][k + 1 + normalizer]) {
            kPrev = k + 1
            xSnake = v[d - 1][kPrev + normalizer]
        } else {
            kPrev = k - 1
            xSnake = v[d - 1][kPrev + normalizer] + 1
        }
        var ySnake = xSnake - k
        val snake = mutableListOf<Pair<Int, Int>>()
        do {
            snake.add(0, Pair(xSnake, ySnake))
            if (xSnake == xCurrent && ySnake == yCurrent) {
                if (snake.isNotEmpty()) {
                    snake.removeFirst()
                    for (e in snake) {
                        path.add(e)
                    }
                }
                recoursivePathFill(path, v, d - 1, kPrev, normalizer, dfA, dfB)
                return
            }
            if (xSnake < dfA.nrow &&
                ySnake < dfB.nrow &&
                xSnake >= 0 &&
                ySnake >= 0 &&
                dfA[xSnake] == dfB[ySnake]
            ) {
                xSnake += 1
                ySnake += 1
            }
        }
        while (xSnake <= xCurrent && ySnake <= yCurrent)
    }
    // Step base.
    // Eventually need to add diagonal edges from origin to the furthest reaching point with d=0.
    // Moreover, the path is reversed so that it can be read from left to right correctly
    if (d == 0) {
        if (path.last().first != 0 && path.last().second != 0) {
            val last = path.last()
            var x = last.first - 1
            var y = last.second - 1
            while (x >= 0 && y >= 0) {
                path.add(Pair(x, y))
                x -= 1
                y -= 1
            }
        }
        path.reverse()
        return
    }
}

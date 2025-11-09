package org.jetbrains.kotlinx.dataframe.impl.api

/**
 * dfs (will be) with same schema. Returns the path from origin to (N,M) in the edit path.
 * N is dfA.nrow, M is dfB.nrow.
 * Knowing this path is knowing the differences between dfA and dfB
 * and the shortest edit script to get B from A.
 * cost of this alg's worst case in O( (N+M)D ), D is the length of shortest edit script.
 * snake: a set of diagonal edges, possibly empty
 */
internal fun myersDifferenceAlgorithmImpl(dfA: String, dfB: String): MutableList<Pair<Int, Int>> {
    // what i want from Myers alg
    val path = mutableListOf<Pair<Int, Int>>()
    var sesLength: Int? // ses stands for shortest edit script
    // Myers algorithm, preparing
    val sumOfLength = dfA.length + dfB.length
    val v = arrayListOf<IntArray>()
    for (d in 0..sumOfLength) {
        v.add(IntArray(sumOfLength * 2 + 1))
    }
    var isOver = false
    // starting the algorithm
    // 0 position is -(M+N) position in the alg's paper -> need to normalize each access to v
    val normalizer = sumOfLength
    v[0][1 + normalizer] = 0 // fitticious
    var d = 0
    while (d <= sumOfLength && !isOver) {
        for (k in -d..d step 2) {
            var x: Int?
            if (k == -d || k != d && v[d][k - 1 + normalizer] < v[d][k + 1 + normalizer]) {
                x = v[d][k + 1 + normalizer]
            } else {
                x = v[d][k - 1 + normalizer] + 1
            }
            var y = x - k
            while (x < dfA.length && y < dfB.length && dfA[x] == dfB[y]) {
                x += 1
                y += 1
            }
            v[d][k + normalizer] = x
            // need these datas in next iteration
            if (d < sumOfLength) {
                v[d + 1][k + normalizer] = x
            }
            //
            if (x >= dfA.length && y >= dfB.length) {
                isOver = true
                val d1 = d
                recoursivePathFill(path, v, d1, k, normalizer, dfA, dfB)
                // if i am the last (not only) i am a furthest reaching endpoint
                break
            }
        }
        d++ // try with a longer edit script
    }
    return path
}

internal fun recoursivePathFill(
    path: MutableList<Pair<Int, Int>>,
    v: ArrayList<IntArray>,
    d: Int,
    k: Int,
    normalizer: Int,
    dfA: String,
    dfB: String,
) {
    // enlist my self
    val xCurrent = v[d][k + normalizer]
    val yCurrent = xCurrent - k
    path.add(Pair(xCurrent, yCurrent))
    // choose the furthest reaching endpoint that precedes me
    // To list an optimal path from (0,0) to the point Vd[k] first determine
    // whether it is at the end of a maximal snake following a vertical edge from Vd−1[k+1] or a horizontal edge
    // from Vd−1[k−1]
    if (d > 0) {
        val kTry1 = k + 1
        val kTry2 = k - 1
        val tries = listOf<Int>(kTry1, kTry2)
        for (kT in tries) {
            var xPrev = v[d - 1][kT + normalizer]
            var yPrev = xPrev - kT
            if (kT == kTry1) {
                yPrev++
            } else {
                xPrev++
            }
            val snake = mutableListOf<Pair<Int, Int>>()
            var skipThisRoundOfOuterLoop = false
            do {
                snake.add(0, Pair(xPrev, yPrev))
                if (xPrev == xCurrent && yPrev == yCurrent) {
                    if (snake.isNotEmpty()) {
                        snake.removeFirst()
                        for (e in snake) {
                            path.add(e)
                        }
                    }
                    recoursivePathFill(path, v, d - 1, kT, normalizer, dfA, dfB)
                    return
                }
                if (xPrev < dfA.length && yPrev < dfB.length && xPrev >= 0 && yPrev >= 0 && dfA[xPrev] == dfB[yPrev]) {
                    xPrev += 1
                    yPrev += 1
                } else {
                    skipThisRoundOfOuterLoop = true
                }
            }
            while (xPrev <= xCurrent && yPrev <= yCurrent && !skipThisRoundOfOuterLoop)
        }
    }
    // step base,
    // eventually need to build the snake from origin to the furthest reaching point with d=0
    // moreover the path is reversed so that it can be read from left to right correctly
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

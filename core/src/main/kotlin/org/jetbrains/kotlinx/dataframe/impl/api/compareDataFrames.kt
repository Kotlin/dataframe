package org.jetbrains.kotlinx.dataframe.impl.api

/**
 * dfs (will be) with same schema. Returns the path from origin to (N,M) in the edit graph.
 * N is dfA.nrow, M is dfB.nrow.
 * Knowing this path is knowing the differences between dfA and dfB
 * and the shortest edit script to get B from A.
 * The cost of this alg's worst case in O( (N+M)D ), D is the length of shortest edit script.
 *
 * The idea of the algorithm is the following: try to cross the edit graph making 'd' non-diagonal moves,
 * increase 'd' until you succeed.
 * Non-diagonal moves make edit script longer, while diagonal moves do not.
 *
 * snake: non-diagonal edge and then a possibly empty sequence of diagonal edges called a
 * furthest reaching D-path endpoint: The endpoint of the longest d-path
 */
internal fun myersDifferenceAlgorithmImpl(dfA: String, dfB: String): MutableList<Pair<Int, Int>> {
    // Return value
    val path = mutableListOf<Pair<Int, Int>>()
    // 'ses' stands for shortest edit script, next var is never returned, it is in the code
    // to show the capabilities of the algorithm
    var sesLength: Int?
    val sumOfLength = dfA.length + dfB.length
    // matrix containing the furthest reaching endpoints for each d
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
            // need this data in the next iteration
            if (d < sumOfLength) {
                v[d + 1][k + normalizer] = x
            }
            // Edit graph was fully crossed
            if (x >= dfA.length && y >= dfB.length) {
                isOver = true
                sesLength = d
                recoursivePathFill(path, v, d, k, normalizer, dfA, dfB)
                break
            }
        }
        // try with a longer edit script
        d++
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
    // Enlist my self
    val xCurrent = v[d][k + normalizer]
    val yCurrent = xCurrent - k
    path.add(Pair(xCurrent, yCurrent))
    // I look for the furthest reaching endpoint that precedes me, it is represented by kPrev.
    // It will be an argument of the next recoursive step.
    // The idea is the following: knowing my d and my k means knowing the f.r.e. that precedes me.
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
            if (xSnake < dfA.length &&
                ySnake < dfB.length &&
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

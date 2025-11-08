package org.jetbrains.kotlinx.dataframe.impl.api

// same schema, returns modified, added and removed rows, first try with string

internal fun compareImpl(dfA: String, dfB: String): MutableList<Pair<Int, Int>> {
    // what i want from Myers alg
    val path = mutableListOf<Pair<Int, Int>>()
    var sesLength: Int?
    // Myers algorithm, preparing
    val sum_of_length = dfA.length + dfB.length
    val v = arrayListOf<IntArray>()
    for (d in 0..sum_of_length) {
        v.add(IntArray(sum_of_length * 2 + 1))
    }
    var isOver = false
    // starting the algorithm
    // 0 position is -(M+N) position in the alg's paper -> need to normalize each access to v
    v[0][1 + sum_of_length] = 0 // fitticious
    var d = 0
    while (d <= sum_of_length && !isOver) {
        for (k in -d..d step 2) {
            var x: Int?
            if (k == -d || k != d && v[d][k - 1 + sum_of_length] < v[d][k + 1 + sum_of_length]) {
                x = v[d][k + 1 + sum_of_length]
            } else {
                x = v[d][k - 1 + sum_of_length] + 1
            }
            var y = x - k
            while (x < dfA.length && y < dfB.length && dfA[x] == dfB[y]) {
                x += 1
                y += 1
            }
            v[d][k + sum_of_length] = x
            // need these datas in next iteration
            if (d < sum_of_length) {
                v[d + 1][k + sum_of_length] = x
            }
            //
            if (x >= dfA.length && y >= dfB.length) {
                isOver = true
                val d1 = d
                recoursive_path_fill(path, v, d1, k, sum_of_length, dfA, dfB)
                // if i am the last (not only) i am a furthest reaching endpoint
                break
            }
        }
        d++ // try with a longer edit script
    }
    path.reverse()
    return path
}

internal fun recoursive_path_fill(
    path: MutableList<Pair<Int, Int>>,
    v: ArrayList<IntArray>,
    d: Int,
    k: Int,
    sum_of_length: Int,
    dfA: String,
    dfB: String,
) {
    if (d < 0) {
        return
    }
    // enlist my self
    val xCurrent = v[d][k + sum_of_length]
    val yCurrent = xCurrent - k
    path.add(Pair(xCurrent, yCurrent))
    // choose the next one to enlist (my previous' furthest reaching endpoint)
//    To list an optimal path from (0,0) to the point Vd[k] first deter
//    mine whether it is at the end of a maximal snake following a vertical edge from Vd−1[k+1] or a horizontal edge
//    from Vd−1[k−1]

    if (d > 0) {
        val kTry1 = k + 1
        val kTry2 = k - 1
        val tries = listOf<Int>(kTry1, kTry2)
        for (kT in tries) {
            var xPrev = v[d - 1][kT + sum_of_length]
            var yPrev = xPrev - kT
            if (kT == kTry1) {
                yPrev++
            } else {
                xPrev++
            }
            val snake = mutableListOf<Pair<Int, Int>>()
            var skipThisRoundOfOuterLoop = false
            do {
                snake.add(Pair(xPrev, yPrev))
                if (xPrev == xCurrent && yPrev == yCurrent) {
                    if (snake.isNotEmpty()) {
                        snake.removeLast()
                        for (e in snake) {
                            path.add(e) //da cambiare
                        }
                    }
                    recoursive_path_fill(path, v, d - 1, kT, sum_of_length, dfA, dfB)
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
}

package org.jetbrains.kotlinx.dataframe.impl.api

// same schema, returns modified, added and removed rows, first try with string

internal fun compareImpl(dfA: String, dfB: String): MutableList<Pair<Int, Int>> {
    // what i want Myers alg
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
                // if i am the last (not only) i am a furthest reachin endpoint
                path.add(Pair(0, 0))
                break
            }
        }
        d++ // try with a longer edit script
    }
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
    val dNext = d - 1
    val kNext1 = k + 1
    val kNext2 = k - 1
    val listOfPossibleNext = listOf<Int>(kNext1, kNext2)
    var x: Int?
    var y: Int?
    // I try my possible next to reach my self
    for (kCurrent in listOfPossibleNext) {
        if (kCurrent == -dNext ||
            kCurrent != dNext &&
            v[dNext][kCurrent - 1 + sum_of_length] < v[dNext][k + 1 + sum_of_length]
        ) {
            x = v[dNext][k + 1 + sum_of_length]
        } else {
            x = v[dNext][k - 1 + sum_of_length] + 1
        }
        y = x - k
        // eventual snake before me
        val snake = mutableListOf<Pair<Int, Int>>()
        while (x < dfA.length && y < dfB.length && x >= 0 && y>=0 && dfA[x] == dfB[y]) {
            x += 1
            y += 1
            snake.add(Pair(x, y))
        }
        if(snake.isNotEmpty())
           snake.removeLast() // will be eventually added to path in the next recoursive step
        if (x == xCurrent && y == yCurrent) {
            for (e in snake) {
                path.add(e)
            }
            recoursive_path_fill(path, v, dNext, kCurrent, sum_of_length, dfA, dfB)
            return
        }
    }
}

// internal fun <T> compareImpl(dfA: DataFrame<T>, dfB: DataFrame<T>): Iterable<DataRow<T>> {
//
//    //Myers algorithm
//    val sum_of_length = dfA.nrow+dfB.nrow
//    val v = IntArray(sum_of_length*2+1) //0 position is -(M+N) position in the alg's paper -> need to normalize each access to v
//    var isOver=false
//
//    v[1+sum_of_length]=0
//    var d=0
//    while(d<=sum_of_length && !isOver){
//        for(k in -d .. d step 2){
//            var x: Int?
//            var y: Int?
//            if (k==-d || k!=d && v[k-1+sum_of_length] < v[k+1+sum_of_length])
//                x = v[k+1+sum_of_length]
//            else
//                x = v[k-1+sum_of_length]
//            y = x-k
//            while (x < dfA.nrow && y<dfB.nrow && dfA[x+1].equals(dfB[x+1])){
//                x+=1
//                y+=1
//            }
//            v[k+sum_of_length]=x
//            if (x >= dfA.nrow && y>=dfB.nrow)
//                isOver=true
//        }
//        d++
//    }
// }

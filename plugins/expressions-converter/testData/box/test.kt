package org.jetbrains.kotlinx.dataframe.explainer

fun print(any: Any) {}

fun printM(pair: Pair<String, Any>) {
    val (text, obj) = pair
    println("# ${text}: $obj")
}

fun ir() {
    val df = Any()
    printM("Any()" to df)
}

fun box(): String {
    val df = Any()
    val df1 = 1 + 2
    print(df)
    print(df1)
    return "OK"
}

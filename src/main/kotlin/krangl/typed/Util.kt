package krangl.typed

import java.util.*

fun <T> LinkedList<T>.popSafe() = if (isEmpty()) null else pop()
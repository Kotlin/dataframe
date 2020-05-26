package krangl.typed.tracking

object ColumnAccessTracker {

    val lastAccessedColumn = ThreadLocal<String>()
}
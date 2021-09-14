package samples

import org.jetbrains.dataframe.*
import org.junit.Test

class Base {

    class CreateColumns {
        @Test
        fun unnamedColumnWithValues() {
            // SampleStart
            val cols = columnOf("Alice", "Bob")
            val colsFromList = listOf("Alice", "Bob").toColumn()
            // SampleEnd
        }

        @Test
        fun namedColumnWitValues() {
            // SampleStart
            val name by columnOf("Alice", "Bob")
            val col = listOf("Alice", "Bob").toColumn("name")
            // SampleEnd
        }

        @Test
        fun namedAndRenameCol() {
            // SampleStart
            val unnamedCol = columnOf("Alice", "Bob")
            val colRename = unnamedCol.rename("name")
            val colNamed = columnOf("Alice", "Bob") named "name"
            // SampleEnd
        }

        @Test
        fun namedColumnWithoutValues() {
            // SampleStart
            val name by column<String>()
            val col = column<String>("name")
            // SampleEnd
        }

        @Test
        fun colRefForTypedAccess() {
            val df = dataFrameOf("name")("Alice", "Bob")
            val name by column<String>()
            val col = column<String>("name")
            // SampleStart
            df.filter { it[name].startsWith("A") }
            df.sortBy { col }
            // SampleEnd
        }
    }
}

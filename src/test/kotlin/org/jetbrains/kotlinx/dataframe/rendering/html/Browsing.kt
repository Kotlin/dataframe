package org.jetbrains.kotlinx.dataframe.rendering.html

import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.junit.Ignore
import org.junit.Test

class Browsing {

    @Ignore
    @Test
    fun test() {
        data class Name(val firstName: String, val lastName: String?)
        data class Score(val subject: String, val value: Int)
        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val students = listOf(
            Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
            Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            Student(Name("Null", null), 100, listOf(Score("nothing", 5))),
            Student(Name("Antony", "Hover"), 20, listOf(Score("russian", 1))),
            Student(Name("Sally", "Fever"), 20, listOf(Score("art", 4), Score("math", 4), Score("biology", 3)))
        )

        val df = students.toDataFrame {
            "year of birth" from { 2021 - it.age }

            properties(depth = 2) {
//                exclude(Score::subject) // `subject` property will be skipped from object graph traversal
//                preserve<Name>() // `Name` objects will be stored as-is without transformation into DataFrame
            }

            "summary" {
                "max score" from { it.scores.maxOf { it.value } }
                "min score" from { it.scores.minOf { it.value } }
            }
        }

        df.browse()
    }
}

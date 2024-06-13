import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*

interface Name {
    val another_name: Int
    val another_name2: Int
}

interface Group {
    val test_name: DataFrame<Name>
}

interface Test {
    val some_group: DataRow<Group>
}

fun box(): String {
    val nestedDf =
        dataFrameOf("test_name")(dataFrameOf("another_name", "another_name2")(1, 2, 1, 2))
            .group { all() }
            .into("some_group")
            .cast<Test>(verify = false)
            .renameToCamelCase()

    if (nestedDf.someGroup.testName
            .first()
            .anotherName
            .first() != 1
    ) {
        return "not OK"
    }

    if (nestedDf.someGroup.testName
            .first()
            .anotherName2
            .first() != 2
    ) {
        return "not OK"
    }

    return "OK"
}

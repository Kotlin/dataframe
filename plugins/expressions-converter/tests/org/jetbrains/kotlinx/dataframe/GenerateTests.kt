package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "testData", testsRoot = "tests-gen") {
            testClass<AbstractExplainerBlackBoxCodegenTest> {
                model("box")
            }
        }
    }
}

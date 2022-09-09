package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.kotlinx.dataframe.runners.AbstractBoxTest
import org.jetbrains.kotlinx.dataframe.runners.AbstractDiagnosticTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "testData", testsRoot = "test-gen") {
            testClass<AbstractDiagnosticTest> {
                model("diagnostics")
            }

            testClass<AbstractBoxTest> {
                model("box")
            }
        }
    }
    KotlinCoreEnvironment
}

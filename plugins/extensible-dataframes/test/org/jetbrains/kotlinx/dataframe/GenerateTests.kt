package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.fir.dataframe.AbstractDataFrameBlackBoxCodegenTest
import org.jetbrains.kotlin.fir.dataframe.AbstractDataFrameDiagnosticTest
import org.jetbrains.kotlin.fir.dataframe.AbstractDataFrameInterpretationTests
import org.jetbrains.kotlin.fir.dataframe.AbstractResearchTest
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.kotlinx.dataframe.runners.AbstractBoxTest
import org.jetbrains.kotlinx.dataframe.runners.AbstractDiagnosticTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "testData", testsRoot = "test-gen") {
            testClass<AbstractDataFrameDiagnosticTest> {
                model("diagnostics")
            }

            testClass<AbstractDataFrameBlackBoxCodegenTest> {
                model("box")
            }
            testClass<AbstractDataFrameInterpretationTests> {
                model("interpretation")
            }
            testClass<AbstractResearchTest> {
                model("research")
            }
        }
    }
    KotlinCoreEnvironment
}

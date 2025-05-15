## :plugins:expressions-converter

This Kotlin Compiler plugin, used by [:core](../../core), can extract intermediate
DataFrame expressions from `@TransformDataFrameExpressions` annotated functions.

It is used to generate sample "explainer dataframe" HTML files that can be used as iFrames on the documentation website.

Annotated functions in [core/.../test/.../samples/api](../../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api)
are tested, generated, and copied over to [docs/StardustDocs/resources/snippets](../../docs/StardustDocs/resources/snippets) by
our "explainer" [plugin callback proxy](../../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/explainer),
which hooks into [the TestBase class](../../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/TestBase.kt) and
retrieves the intermediate DataFrame expressions thanks to this module.

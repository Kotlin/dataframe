## :core

This is the core of the library, published as the `dataframe-core` package.
It contains the DataFrame API and its implementation, as well as plenty of JUnit tests.

I/O operations are split off into other modules, like [:dataframe-excel](../dataframe-excel),
or [:dataframe-jdbc](../dataframe-jdbc), however, this is has not happened yet for all operations
(see [Issue #100](https://github.com/Kotlin/dataframe/issues/100)).

Operations likely to be split off in the future include:

- csv/tsv
- html
- json
- jupyter

### Korro code samples

Code samples for the documentation website reside
in [core/.../test/.../samples/api](./src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api),
and [tests/.../samples/api](../tests/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api)
and they are copied over to Markdown files in [docs/StardustDocs/topics](../docs/StardustDocs/topics)
by [Korro](https://github.com/devcrocod/korro).

### Explainer dataframes

Aside from code samples, `@TransformDataFrameExpressions` annotated test functions also generate sample
dataframe HTML files that can be used as iFrames on the documentation website.
They are tested, generated, and copied over to [docs/StardustDocs/snippets](../docs/StardustDocs/snippets) by
our "explainer" [plugin callback proxy](./src/test/kotlin/org/jetbrains/kotlinx/dataframe/explainer),
which hooks into [the TestBase class](./src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/TestBase.kt) and
retrieves the intermediate DataFrame expressions thanks to our "explainer" compiler plugin
[:plugins:expressions-converter](../plugins/expressions-converter).

We can also generate "normal" DataFrame samples for the website. This can be done using the
[OtherSamples class](./src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/OtherSamples.kt). Generated
HTML files will be stored in [docs/StardustDocs/snippets/manual](../docs/StardustDocs/snippets/manual).

### KDoc Preprocesor

The code you're working on needs to be edited in [src](src), but the KDocs are processed by the
[KDoc Preprocessor](https://github.com/Jolanrensen/docProcessorGradlePlugin) when the project is published (or the task
is run manually). The generated sources with adjusted KDocs will be overwritten
in [generated-sources](generated-sources).
See the [KDoc Preprocessing Guide](../KDOC_PREPROCESSING.md) for more information.

KDocs can also be exported to HTML, for them to be reused on the website.
Elements annotated with `@ExportAsHtml` will have their generated content be copied over to
[docs/StardustDocs/snippets/kdocs](../docs/StardustDocs/snippets/kdocs).

## :core

This is the core of the library, published as the `dataframe-core` package.
It contains the DataFrame API and its implementation, as well as plenty of JUnit tests.

I/O operations are split off into other modules, like [:dataframe-excel](../dataframe-excel), [:dataframe-jdbc](../dataframe-jdbc), or [:dataframe-json](../dataframe-json).

At the moment, these integrations are still part of the `:core` module:

- (deprecated) csv/tsv
- html

### KoDEx

The code you're working on needs to be edited in [src](src), but the KDocs are processed by
[KoDEx](https://github.com/Jolanrensen/kodex) when the project is published (or the task
is run manually). The generated sources with adjusted KDocs will be overwritten
in [generated-sources](generated-sources).
See the [KDoc Preprocessing Guide](../KDOC_PREPROCESSING.md) for more information.

KDocs can also be exported to HTML, for them to be reused on the website.
Elements annotated with `@ExportAsHtml` will have their generated content be copied over to
[docs/StardustDocs/resources/snippets/kdocs](../docs/StardustDocs/resources/snippets/kdocs).

### ~~Korro~~ (NOTE: This is being moved to [:samples](../samples))

> [Should be removed and moved to `:samples` module](https://github.com/Kotlin/dataframe/issues/898).

Tests in this module contain code samples used for import into documentation 
using [Korro](https://github.com/devcrocod/korro).


### ~~Explainer dataframes~~ (NOTE: This is being moved to [:samples](../samples))

> [Should be removed and migrated to SampleHelper](https://github.com/Kotlin/dataframe/issues/898).

Aside from code samples, `@TransformDataFrameExpressions` annotated test functions also generate sample
dataframe HTML files that can be used as iFrames on the documentation website.
They are tested, generated, and copied over to [docs/StardustDocs/resources/snippets](../docs/StardustDocs/resources/snippets) by
our "explainer" [plugin callback proxy](./src/test/kotlin/org/jetbrains/kotlinx/dataframe/explainer),
which hooks into [the TestBase class](./src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/TestBase.kt) and
retrieves the intermediate DataFrame expressions thanks to our "explainer" compiler plugin
[:plugins:expressions-converter](../plugins/expressions-converter).

We can also generate "normal" DataFrame samples for the website. This can be done using the
[OtherSamples class](./src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/OtherSamples.kt). Generated
HTML files will be stored in [docs/StardustDocs/resources/snippets/manual](../docs/StardustDocs/resources/snippets/manual).

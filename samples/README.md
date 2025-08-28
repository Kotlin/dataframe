## :tests

Code samples for the documentation website reside in [core/.../test/.../samples/api](../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api)
(for samples that depend solely on `dataframe-core`),
and [tests/.../samples/api](/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api) (for samples can depend on other I/O modules)
and they are copied over to Markdown files in [docs/StardustDocs/topics](../docs/StardustDocs/topics)
by [Korro](https://github.com/devcrocod/korro).

This module might be merged with [:core](../core): [Issue #898](https://github.com/Kotlin/dataframe/issues/898).
 
Uses Kandy samples util.
See https://github.com/Kotlin/kandy/blob/samples_util/util/kandy-samples-utils/README.md for more details.

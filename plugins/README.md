## :plugins

This folder holds our Kotlin Compiler plugins. These are meant to be used in this repo only.
These plugins are being migrated to convention plugins in [build-logic](../build-logic).

The deprecated Gradle plugin (`dataframe-gradle-plugin`) and KSP plugin (`symbol-processor`) were removed from
this repository: KSP1 is no longer compatible with Kotlin 2.3+, their last release is `1.0.0-Beta4`, and schema
generation now goes through the compiler plugin and runtime methods instead.

### [:plugins:expressions-converter](./expressions-converter)
A small Kotlin Compiler plugin that provides intermediate expressions of DataFrame
operation chains, used internally by [:core](../core) to generate "explainer dataframes" on the documentation website.

### [:plugins:public-api-modifier](./public-api-modifier)
An exploratory compiler plugin that makes `@AccessApiOverload` annotated functions internal, allowing a full-API
and a reduced-API artifact to be produced from the same source tree. It is built, but not enabled anywhere.

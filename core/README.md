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

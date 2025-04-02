## :dataframe-openapi

This **experimental** module, published as `dataframe-openapi` contains some functions to be able to use auto-generated
data schemas from OpenAPI 3.0.0 specifications. This module is a sister module to
[`dataframe-openapi-generator`](../dataframe-openapi-generator):

- `dataframe-openapi-generator` is used as a dependency of the Gradle plugin and Jupyter plugin to be able to generate
  data schemas from OpenAPI specifications. In the Gradle plugin it adds support for the `dataschemas {}` DSL and the
  `@file:ImportDataSchema()` annotation. In Jupyter, it adds support for the `importDataSchema()` function.
- `dataframe-openapi` must be used as a dependency of a user-project to be able to use the generated data schemas.

See [Import OpenAPI Schemas in Gradle project](https://kotlin.github.io/dataframe/schemasimportopenapigradle.html) and
[Import Data Schemas, e.g. from OpenAPI, in Jupyter](https://kotlin.github.io/dataframe/schemasimportopenapijupyter.html)
for more information about how to use it.

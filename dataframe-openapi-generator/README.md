## :dataframe-openapi-generator

This module, published as `dataframe-openapi-generator` contains all logic and tests for DataFrame to be able to import
OpenAPI specifications as auto-generated data schemas. This module is a sister module to
[`dataframe-openapi`](../dataframe-openapi):

- `dataframe-openapi-generator` is used as a dependency of the Gradle plugin and Jupyter plugin to be able to generate
    data schemas from OpenAPI specifications. In the Gradle plugin it adds support for the `dataschemas {}` DSL and the
    `@file:ImportDataSchema()` annotation. In Jupyter, it adds support for the `importDataSchema()` function.
- `dataframe-openapi` must be used as a dependency of a user-project to be able to use the generated data schemas.

See [Import OpenAPI Schemas in Gradle project](https://kotlin.github.io/dataframe/schemasimportopenapigradle.html) and
[Import Data Schemas, e.g. from OpenAPI, in Jupyter](https://kotlin.github.io/dataframe/schemasimportopenapijupyter.html)
for more information about how to use it.

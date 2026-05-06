# JSON + OpenAPI Example 

> **Experimental**: Support for OpenAPI 3.0 schemas is demoted to experimental
> and may change or be removed in future releases. This is because OpenAPI 3.1 (and 3.2) have
> introduced significant changes that require specialized handling.
> Follow https://github.com/Kotlin/dataframe/issues/897 for updates and please leave your feedback.

This project shows how to generate and use data schemas for JSON data that may generate a large
number of columns; avoiding memory issues by using `keyValuePaths`.
This is done using the ApisGuru dataset.
See [./.../keyValuePaths](./src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/json/keyValuePaths).

This project shows how to use `dataframe-openapi` and `dataframe-openapi-generator` to
read JSON according to the types defined in an OpenAPI specification.
Specifically, it uses the 1Password OpenAPI specification.
See [./.../openApi](./src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/json/openApi).

Visit our [documentation](https://kotlin.github.io/dataframe/openapi.html) for more
information about using OpenAPI with DataFrame.

This project uses the
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html).

We recommend using an up-to-date IntelliJ IDEA for the best experience,
as well as the latest Kotlin plugin version.

> [!WARNING]
> For proper functionality in IntelliJ IDEA requires version 2025.2 or newer.

[Download this Example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/json-openapi.zip)

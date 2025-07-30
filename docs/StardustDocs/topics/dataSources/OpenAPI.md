# OpenAPI

<web-summary>
Work with JSON data based on OpenAPI 3.0 schemas using Kotlin DataFrame — helpful for consuming structured API responses.
</web-summary>

<card-summary>
Use Kotlin DataFrame to read and write data that conforms to OpenAPI specifications. Great for API-driven data workflows.
</card-summary>

<link-summary>
Learn how to use OpenAPI 3.0 JSON schemas with Kotlin DataFrame to load and manipulate API-defined data.
</link-summary>


> **Experimental**: Support for OpenAPI 3.0.0 schemas is currently experimental 
> and may change or be removed in future releases.  
> {style="warning"}

Kotlin DataFrame provides support for reading and writing JSON data
that conforms to [OpenAPI 3.0 specifications](https://www.openapis.org).
This feature is useful when working with APIs that expose structured data defined via OpenAPI schemas.

Requires the [`dataframe-openapi` module](Modules.md#dataframe-openapi),
which **is not included** in the general [`dataframe`](Modules.md#dataframe-general) artifact.

To enable it in Kotlin Notebook, use:

```kotlin
%use dataframe(enableExperimentalOpenApi=true)
```

See [the OpenAPI guide notebook](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/json/KeyValueAndOpenApi.ipynb)
for details on how to work with OpenAPI-based data.

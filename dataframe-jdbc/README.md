## :dataframe-jdbc

This module, published as `dataframe-jdbc`, contains all logic and tests for DataFrame to be able to work with
JDBC data sources.

See [Read from SQL databases](https://kotlin.github.io/dataframe/readsqldatabases.html) for more information
about how to use it.

### Testing
For testing DBMS specific changes there are two options:
Task that should be run manually, require installed [Docker daemon](https://www.docker.com/products/docker-desktop/):
`./gradlew :dataframe-jdbc:testcontainersTest`

Same tests, but with option to run without Docker, with locally installed databases:
`./gradlew :dataframe-jdbc:localDbTest`

As a fallback, there're H2 tests in compatibility mode that always run as a part of `./gradlew :dataframe-jdbc:check`.

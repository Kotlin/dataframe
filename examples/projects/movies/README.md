# Movies Example

This project contains examples of some basic Kotlin DataFrame
operations on (modified) data from [movielens](https://movielens.org/).

This project uses the
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html).
Pipeline starts with a dataframe instance typed by @DataSchema declaration.
Each operation returns a dataframe with a new type. 
It declares properties with accurate types for all known columns. 
`split` adds new columns; `split`, `convert`, and `explode` update column types. 
Compile time schema of a dataframe can be observed by hovering on an expression in the IDE, and through properties available in code completion. 
`pivot` uses column values to create new columns - its result is not typed.

We recommend using an up-to-date IntelliJ IDEA for the best experience,
as well as the latest Kotlin plugin version.

> [!WARNING]
> For proper functionality in IntelliJ IDEA requires version 2025.2 or newer.

[Download this Example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/movies.zip)

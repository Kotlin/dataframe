# Kotlin DataFrame Compiler Kotlin Toolchain Example

An IntelliJ IDEA Kotlin Toolchain project demonstrating the use of the  
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html).

We recommend using an up-to-date IntelliJ IDEA for the best experience,
as well as the latest Kotlin plugin-, and Kotlin Toolchain plugin version.

> [!WARNING]
> For proper functionality in IntelliJ IDEA requires version 2026.2.1 or newer.

[Download Kotlin DataFrame Compiler Plugin Kotlin Toolchain Example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-dataframe-plugin-kotlin-toolchain-example.zip)

## Formatting

This Kotlin Toolchain project was set up using a custom [Ktlint](https://pinterest.github.io/ktlint/) plugin
in the [build-config](./build-config) directory.

Checking whether the code is formatted correctly can be done by calling
`./kotlin check ktlint`,
or simply: `./kotlin check` (which runs all checks, including this one).

Ktlint can also auto-format the code in most cases by calling:
`./kotlin do ktlintFormat`

# Kotlin DataFrame Compiler Kotlin Toolchain Example

An IntelliJ IDEA Kotlin Toolchain project demonstrating the use of the  
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html).

We recommend using an up-to-date IntelliJ IDEA for the best experience,
as well as the latest Kotlin plugin-, and Kotlin Toolchain plugin version.

> [!WARNING]
> For proper functionality in IntelliJ IDEA requires version 2025.2 or newer.

[Download Kotlin DataFrame Compiler Plugin Gradle Example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-dataframe-plugin-gradle-example.zip)

## Formatting

Like the Maven example, this project runs [ktlint](https://pinterest.github.io/ktlint/) through
[exec-maven-plugin](https://www.mojohaus.org/exec-maven-plugin/) to format the sources in place.
Maven binds it to the `compile` phase; the Kotlin Toolchain has no way to choose the phase yet, so run it with:

```shell
./kotlin task :kotlin-dataframe-plugin-kotlin-toolchain-example:exec-maven-plugin.exec
```

See also [Kotlin DataFrame Compiler Maven Plugin Example](../kotlin-dataframe-plugin-maven-example)

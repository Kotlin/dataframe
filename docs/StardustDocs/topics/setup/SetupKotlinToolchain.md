# Setup Kotlin DataFrame in Kotlin Toolchain

<web-summary>
Set up Kotlin DataFrame in your Kotlin Toolchain project, configure dependencies, and start using the API with full IDE support.
</web-summary>

<card-summary>
Learn how to add Kotlin DataFrame to your Kotlin Toolchain project.
</card-summary>

<link-summary>
Guide for integrating Kotlin DataFrame in a Kotlin Toolchain project, with setup instructions and example code.
</link-summary>

Kotlin DataFrame can be added as a usual Kotlin Toolchain dependency to your Kotlin project.

## Create a Kotlin project

1. In IntelliJ IDEA, select **File** | **New** | **Project**.
2. In the panel on the left, select **New Project**.
3. Name the new project and change its location, if necessary.

   > Select the **Create Git repository** checkbox to place the new project under version control. 
   > You can enable this later at any time.
   > {type="tip"}

4. From the **Language** list, select **Kotlin**.
5. Select the **Kotlin** build system.
6. From the **JDK list**, select the [JDK](https://www.oracle.com/java/technologies/downloads/) 
that you want to use in your project. The minimum supported version is JDK 8.
    * If the JDK is installed on your computer, but not defined in the IDE, select **Add JDK** 
      and specify the path to the JDK home directory.
    * If you don't have the necessary JDK on your computer, select **Download JDK**.
7. Select the **Add sample code** checkbox to create a file with a sample `"Hello World!"` application.
8. Click **Create**.

You have successfully created a project with Kotlin Toolchain.

## Setup Kotlin DataFrame

### 1. Enable the plugin

[Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) enables automatic generation of
[extension properties](extensionPropertiesApi.md) and updates [data schemas](schemas.md)
on-the-fly in Kotlin Toolchain projects, making development with Kotlin DataFrame faster,
more convenient, and fully type- and name-safe.

> Requires Kotlin 2.2.20-Beta1 or higher, Kotlin Toolchain 0.12.0+ and IntelliJ IDEA 2026.2.1 or higher.  
> { style = "note" }

To enable the plugin in your Kotlin Toolchain project, update the `settings:` block in your `module.yaml`:

```yaml
settings:
  kotlin:
    version: %compilerPluginKotlinVersion%
    dataframe: enabled
```

Doing so will enable the compiler plugin matching the Kotlin version.
It will also automatically add the latest (`%dataFrameVersion%`) [`dataframe-core`](Modules.md#dataframe-core)
dependency to your project.

In contrast to Maven and Gradle projects, this does NOT include any IO dependencies.

### 2. Add IO dependencies and specify the library version

Enabling specific [IO modules](Modules.md#io-modules) can be done quite easily in the `dependencies:` block,
as DataFrame is a Kotlin Toolchain 'built-in technology'.
Additionally, if you want a specific version of the DataFrame library,
you can do that in the `settings.kotlin.dataframe:` block:

```yaml
dependencies:
  - $kotlin.dataframe.json
  - $kotlin.dataframe.csv
  - $kotlin.dataframe.json
  - $kotlin.dataframe.geo
  - $kotlin.dataframe.arrow
  - $kotlin.dataframe.excel
  - $kotlin.dataframe.jdbc

repositories:
  # Required for dataframe-geo
  - https://repo.osgeo.org/repository/release

settings:
  kotlin:
    version: %compilerPluginKotlinVersion%
    dataframe:
      enabled: true
      version: %dataFrameVersion%
```

### Specifying dependencies without the compiler plugin

If the compiler plugin is disabled, the `$kotlin.dataframe` built-in catalog won't work. In this case,
specifying DataFrame dependencies will need to be done the same way as you would [declare any other
dependency](https://kotlin-toolchain.org/dev/user-guide/dependencies/) in Kotlin Toolchain:

```yaml
dependencies:
  - org.jetbrains.kotlinx:dataframe:%dataFrameVersion%
```

This will add the [general Kotlin DataFrame dependency](Modules.md#dataframe-general),
i.e., [core API and implementation](Modules.md#dataframe-core) as well as all
[IO modules](Modules.md#io-modules) (excluding [experimental ones](Modules.md#experimental-modules)).  
You can also add just the [core API module](Modules.md#dataframe-core)
and only the specific [modules](Modules.md) you need.

## Hello World

Let’s create your first [`DataFrame`](DataFrame.md) — a simple "Hello, World!" style example:

```kotlin
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.print

fun main() {
    val df = dataFrameOf(
        "name" to listOf("Alice", "Bob"),
        "age" to listOf(25, 30)
    )

    df.print()
}
```


## Project Example

See [the Kotlin Toolchain example project with the Kotlin DataFrame Compiler Plugin enabled on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples/projects/kotlin-dataframe-plugin-kotlin-toolchain-example).

You can also 
[download this project](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-dataframe-plugin-kotlin-toolchain-example.zip).


## Next Steps

* Once you’ve set up Kotlin DataFrame in your Kotlin Toolchain project, continue with the [](quickstart.md)
  to learn the basics of working with Kotlin DataFrame.
* Explore [detailed guides and real-world examples](Guides-And-Examples.md)
  to see how Kotlin DataFrame helps with different data tasks.
* Check out various
  [IDEA examples using Kotlin DataFrame on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples).
* Learn more about the [compiler plugin](Compiler-Plugin.md).

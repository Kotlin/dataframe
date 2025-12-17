# Setup Kotlin DataFrame in Maven

<web-summary>
Set up Kotlin DataFrame in your Maven project, configure dependencies, and start using the API with full IDE support.
</web-summary>

<card-summary>
Learn how to add Kotlin DataFrame to your Maven project.
</card-summary>

<link-summary>
Guide for integrating Kotlin DataFrame in a Maven project, with setup instructions and example code.
</link-summary>

Kotlin DataFrame can be added as a usual Maven dependency to your Kotlin project.

## Create a Kotlin project

1. In IntelliJ IDEA, select **File** | **New** | **Project**.
2. In the panel on the left, select **New Project**.
3. Name the new project and change its location, if necessary.

   > Select the **Create Git repository** checkbox to place the new project under version control. 
   > You can enable this later at any time.
   > {type="tip"}

4. From the **Language** list, select **Kotlin**.
5. Select the **Maven** build system.
6. From the **JDK list**, select the [JDK](https://www.oracle.com/java/technologies/downloads/) 
that you want to use in your project. The minimum supported version is JDK 8.
    * If the JDK is installed on your computer, but not defined in the IDE, select **Add JDK** 
      and specify the path to the JDK home directory.
    * If you don't have the necessary JDK on your computer, select **Download JDK**.
7. Select the **Add sample code** checkbox to create a file with a sample `"Hello World!"` application.
8. Click **Create**.

You have successfully created a project with Maven.

## Add Kotlin DataFrame Maven dependency

In your Maven build file (`pom.xml`), add the Kotlin DataFrame library as a dependency:

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>dataframe</artifactId>
    <version>%dataFrameVersion%</version>
</dependency>
```

This will add [general Kotlin DataFrame dependency](Modules.md#dataframe-general), 
i.e., [core API and implementation](Modules.md#dataframe-core) as well as all 
[IO modules](Modules.md#io-modules) (excluding [experimental ones](Modules.md#experimental-modules)).  
You can add only the [core API module](Modules.md#dataframe-core) 
and the specific [modules](Modules.md) you need.


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

## Kotlin DataFrame Compiler Plugin

[Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) enables automatic generation of
[extension properties](extensionPropertiesApi.md) and updates [data schemas](schemas.md)
on-the-fly in Maven projects, making development with Kotlin DataFrame faster,
more convenient, and fully type- and name-safe.

> Requires Kotlin 2.2.20-Beta1 or higher and IntelliJ IDEA 2025.3 or higher.  
> { style = "note" }

To enable the plugin in your Maven project, add it to the `plugins` section:

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>%compilerPluginKotlinVersion%</version>

    <configuration>
        <compilerPlugins>
            <plugin>kotlin-dataframe</plugin>
        </compilerPlugins>
    </configuration>

    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-dataframe</artifactId>
            <version>%compilerPluginKotlinVersion%</version>
        </dependency>
    </dependencies>
</plugin>
```

## Project Example

See [the Maven example project with the Kotlin DataFrame Compiler Plugin enabled on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples/kotlin-dataframe-plugin-maven-example).

You can also 
[download this project](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-dataframe-plugin-maven-example.zip).


## Next Steps

* Once you’ve set up Kotlin DataFrame in your Maven project, continue with the [](quickstart.md)
  to learn the basics of working with Kotlin DataFrame.
* Explore [detailed guides and real-world examples](Guides-And-Examples.md)
  to see how Kotlin DataFrame helps with different data tasks.
* Check out various
  [IDEA examples using Kotlin DataFrame on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples).
* Learn more about the [compiler plugin](Compiler-Plugin.md).

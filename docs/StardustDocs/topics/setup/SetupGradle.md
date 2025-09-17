# Setup Kotlin DataFrame in Gradle

<web-summary>
Set up Kotlin DataFrame in your Gradle project, configure dependencies, and start using the API with full IDE support.
</web-summary>

<card-summary>
Learn how to add Kotlin DataFrame to your Gradle project.
</card-summary>

<link-summary>
Guide for integrating Kotlin DataFrame in a Gradle-based project, with setup instructions and example code.
</link-summary>

Kotlin DataFrame can be added as a usual Gradle dependency 
to your Kotlin project (for now only Kotlin/JVM is supported).

## Create a Kotlin project

1. In IntelliJ IDEA, select **File** | **New** | **Project**.
2. In the panel on the left, select **New Project**.
3. Name the new project and change its location, if necessary.

   > Select the **Create Git repository** checkbox to place the new project under version control. 
   > You can enable this later at any time.
   > {type="tip"}

4. From the **Language** list, select **Kotlin**.
5. Select the **Gradle** build system.
6. From the **JDK list**, select the [JDK](https://www.oracle.com/java/technologies/downloads/) 
that you want to use in your project. The minimum supported version is JDK 8.
    * If the JDK is installed on your computer, but not defined in the IDE, select **Add JDK** 
      and specify the path to the JDK home directory.
    * If you don't have the necessary JDK on your computer, select **Download JDK**.
7. From the **Gradle DSL** list, select **Kotlin** or **Groovy**.
8. Select the **Add sample code** checkbox to create a file with a sample `"Hello World!"` application.
9. Click **Create**.

You have successfully created a project with Gradle.

## Add Kotlin DataFrame Gradle dependency

In your Gradle build file (`build.gradle` or `build.gradle.kts`), add the Kotlin DataFrame library as a dependency:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:%dataFrameVersion%'
}
```

</tab>
</tabs>

This will add [general Kotlin DataFrame dependency](Modules.md#dataframe-general), 
i.e., [core API and implementation](Modules.md#dataframe-core) as well as all 
[IO modules](Modules.md#io-modules) (excluding [experimental ones](Modules.md#experimental-modules)).  
For flexible dependencies configuration see [Custom configuration](SetupCustomGradle.md).

## Hello World

Let’s create your first [`DataFrame`](DataFrame.md) in the notebook — a simple "Hello, World!" style example:

```kotlin
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf

fun main() {
    val df = dataFrameOf(
        "name" to listOf("Alice", "Bob"),
        "age" to listOf(25, 30)
    )

    println(df)
}
```

## Kotlin DataFrame Compiler Plugin

[Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) enables automatic generation of
[extension properties](extensionPropertiesApi.md) and updates [data schemas](schemas.md)
on-the-fly in Gradle projects, making development with Kotlin DataFrame faster,
more convenient, and fully type- and name-safe.

> Requires Kotlin 2.2.20-Beta1 or higher.  
> { style = "note" }

To enable the plugin in your Gradle project, add it to the `plugins` section:

<tabs>
<tab title="Kotlin DSL">

```kotlin
plugins {
    kotlin("plugin.dataframe") version "%compilerPluginKotlinVersion%"
}
```

</tab>

<tab title="Groovy DSL">

```groovy
plugins {
    id 'org.jetbrains.kotlin.plugin.dataframe' version '%compilerPluginKotlinVersion%'
}
```

</tab>
</tabs>

Due to [this issue](https://youtrack.jetbrains.com/issue/KT-66735), incremental compilation must be disabled for now.
Add the following line to your `gradle.properties` file:

```properties
kotlin.incremental=false
```

## Next Steps

* Once you’ve set up Kotlin DataFrame in your Gradle project, continue with the [](quickstart.md)
  to learn the basics of working with Kotlin DataFrame.
* Explore [detailed guides and real-world examples](Guides-And-Examples.md)
  to see how Kotlin DataFrame helps with different data tasks.
* Check out various
  [IDEA examples using Kotlin DataFrame on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples).
* Learn more about the [compiler plugin](Compiler-Plugin.md).

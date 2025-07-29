# Setup Kotlin DataFrame in Gradle

Kotlin DataFrame can be added as usual Gradle dependency to your Kotlin project (
for now only Kotlin/JVM is supported).

## Create a Kotlin project

1. In IntelliJ IDEA, select **File** | **New** | **Project**.
2. In the panel on the left, select **New Project**.
3. Name the new project and change its location, if necessary.

   > Select the **Create Git repository** checkbox to place the new project under version control. You can enable this
   > later at any time.
   >
   {type="tip"}

4. From the **Language** list, select **Kotlin**.
5. Select the **Gradle** build system.
6. From the **JDK list**, select the [JDK](https://www.oracle.com/java/technologies/downloads/) that you want to use in
   your project. The minimum supported version is JDK 8.
    * If the JDK is installed on your computer, but not defined in the IDE, select **Add JDK** and specify the path to the
      JDK home directory.
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

This will add [general Kotlin DataFrame dependency](Modules.md#dataframe-general), i.e., 
[core API and implementation](Modules.md#dataframe-core) 
as well as all [IO modules](Modules.md#io-modules) 
(excluding [experimental ones](Modules.md#experimental-modules)). 
For flexible dependencies configuration see [Custom configuration](#custom-configuration).

### Add imports

In `src/main/kotlin/Main.kt`, add the following imports at the top of the file:

```kotlin
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.api.*
```

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

Congratulations! You have successfully used the Kotlin DataFrame library to import, manipulate and export data.


## Custom configuration

## Next steps
* Learn more about how to [import and export data](io.md)
* Learn about our different [access APIs](apiLevels.md)
* Explore the many different [operations that you can perform](operations.md)

[//]: # (title: Get started with DataFrame)

Kotlin's DataFrame library gives you the power to manipulate data in your Kotlin projects. 

This page explains how to:
* Set up the DataFrame library in an IntelliJ IDEA project with Gradle.
* Import and manipulate data.
* Export data.

To use the DataFrame library with Jupyter notebooks or DataLore, follow the instructions on our [Installation page](installation.md).

## Install Kotlin

Kotlin is included in each IntelliJ IDEA release.
Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) to start using Kotlin.

## Create Kotlin project

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
   your project.
    * If the JDK is installed on your computer, but not defined in the IDE, select **Add JDK** and specify the path to the
      JDK home directory.
    * If you don't have the necessary JDK on your computer, select **Download JDK**.
7. From the **Gradle DSL** list, select **Kotlin** or **Groovy**.
8. Select the **Add sample code** checkbox to create a file with a sample `"Hello World!"` application.
9. Click **Create**.

You have successfully created a project with Gradle.

### Update Gradle dependencies

In your Gradle build file (`build.gradle.kts`), add the DataFrame library as a dependency:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```kotlin
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:%dataFrameVersion%'
}
```

</tab>

</tabs>

### Add imports

In `src/main/kotlin/Main.kt`, add the following imports at the top of the file:

```kotlin
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.api.*
```

## Import data

Download and save our <res resource-id="movie-sample-data"> file to the root directory of your project.

Delete the `println()` functions and comments from your main function in `Main.kt`.

To import the movie sample data into a data frame and print it, inside your main function in `Main.kt`, add the following code:

```kotlin
    // Import your data to a data frame
    var df = DataFrame.read("movies.csv")

    // Print your data frame
    println(df)
```

## Manipulate data

To print some information about your data frame and sort your data, add the following additional lines of code:

```kotlin
    // Print some information about the data frame
    println(df.columnNames()) // Print column names
    println(df.count())       // Print number of rows

    // Sort your data alphabetically by title and print
    df = df.sortBy("title")
    println(df)
```

## Export data

To export the current version of your data frame in CSV format, add the following additional lines of code and run `Main.kt`.

```kotlin
    // Export your manipulated data to CSV format
    df.writeCSV("movies-by-title.csv")
```


<code-block lang="console" collapsed-title="Example terminal output" collapsible="true">
    movieId                                    title                              genres
    0 9b30aff7943f44579e92c261f3adc193                    Women in Black (1997)          Fantasy|Suspenseful|Comedy
    1 2a1ba1fc5caf492a80188e032995843e                   Bumblebee Movie (2007)        Comedy|Jazz|Family|Animation
    2 f44ceb4771504342bb856d76c112d5a6 Magical School Boy and the Rock of Wi...            Fantasy|Growing up|Magic
    3 43d02fb064514ff3bd30d1e3a7398357 Master of the Jewlery: The Company of...           Fantasy|Magic|Suspenseful
    4 6aa0d26a483148998c250b9c80ddf550 Sun Conflicts: Part IV: A Novel Espai...                             Fantasy
    5 eace16e59ce24eff90bf8924eb6a926c              The Outstanding Bulk (2008)            Fantasy|Superhero|Family
    6 ae916bc4844a4bb7b42b70d9573d05cd                       In Automata (2014)                  Horror|Existential
    7 c1f0a868aeb44c5ea8d154ec3ca295ac                    Interplanetary (2014)                   Sci-fi|Futuristic
    8 9595b771f87f42a3b8dd07d91e7cb328                         Woods Run (1994)                        Family|Drama
    9 aa9fc400e068443488b259ea0802a975                    Anthropod-Dude (2002) Superhero|Fantasy|Family|Growing up
    10 22d20c2ba11d44cab83aceea39dc00bd                       The Chamber (2003)                        Comedy|Drama
    11 8cf4d0c1bd7b41fab6af9d92c892141f       That Thing About an Iceberg (1997)        Drama|History|Family|Romance
    12 c2f3e7588da84684a7d78d6bd8d8e1f4                          Vehicles (2006)                    Animation|Family
    13 ce06175106af4105945f245161eac3c7                   Playthings Tale (1995)                    Animation|Family
    14 ee28d7e69103485c83e10b8055ef15fb                       Metal Man 2 (2010)            Fantasy|Superhero|Family
    15 c32bdeed466f4ec09de828bb4b6fc649 Surgeon Odd in the Omniverse of Crazy...     Fantasy|Superhero|Family|Horror
    16 d4a325ab648a42c4a2d6f35dfabb387f          Bad Dream on Pine Street (1984)                              Horror
    17 60ebe74947234ddcab49dea1a958faed                    The Shimmering (1980)                              Horror
    18 f24327f2b05147b197ca34bf13ae3524 Krubit: Societal Teachings for Do Man...                              Comedy
    19 2bb29b3a245e434fa80542e711fd2cee                  This is No Movie (1950)                  (no genres listed)
    [movieId, title, genres]
    20
    movieId                                    title                              genres
    0 aa9fc400e068443488b259ea0802a975                    Anthropod-Dude (2002) Superhero|Fantasy|Family|Growing up
    1 d4a325ab648a42c4a2d6f35dfabb387f          Bad Dream on Pine Street (1984)                              Horror
    2 2a1ba1fc5caf492a80188e032995843e                   Bumblebee Movie (2007)        Comedy|Jazz|Family|Animation
    3 ae916bc4844a4bb7b42b70d9573d05cd                       In Automata (2014)                  Horror|Existential
    4 c1f0a868aeb44c5ea8d154ec3ca295ac                    Interplanetary (2014)                   Sci-fi|Futuristic
    5 f24327f2b05147b197ca34bf13ae3524 Krubit: Societal Teachings for Do Man...                              Comedy
    6 f44ceb4771504342bb856d76c112d5a6 Magical School Boy and the Rock of Wi...            Fantasy|Growing up|Magic
    7 43d02fb064514ff3bd30d1e3a7398357 Master of the Jewlery: The Company of...           Fantasy|Magic|Suspenseful
    8 ee28d7e69103485c83e10b8055ef15fb                       Metal Man 2 (2010)            Fantasy|Superhero|Family
    9 ce06175106af4105945f245161eac3c7                   Playthings Tale (1995)                    Animation|Family
    10 6aa0d26a483148998c250b9c80ddf550 Sun Conflicts: Part IV: A Novel Espai...                             Fantasy
    11 c32bdeed466f4ec09de828bb4b6fc649 Surgeon Odd in the Omniverse of Crazy...     Fantasy|Superhero|Family|Horror
    12 8cf4d0c1bd7b41fab6af9d92c892141f       That Thing About an Iceberg (1997)        Drama|History|Family|Romance
    13 22d20c2ba11d44cab83aceea39dc00bd                       The Chamber (2003)                        Comedy|Drama
    14 eace16e59ce24eff90bf8924eb6a926c              The Outstanding Bulk (2008)            Fantasy|Superhero|Family
    15 60ebe74947234ddcab49dea1a958faed                    The Shimmering (1980)                              Horror
    16 2bb29b3a245e434fa80542e711fd2cee                  This is No Movie (1950)                  (no genres listed)
    17 c2f3e7588da84684a7d78d6bd8d8e1f4                          Vehicles (2006)                    Animation|Family
    18 9b30aff7943f44579e92c261f3adc193                    Women in Black (1997)          Fantasy|Suspenseful|Comedy
    19 9595b771f87f42a3b8dd07d91e7cb328                         Woods Run (1994)                        Family|Drama
</code-block>

Congratulations! You have successfully used Kotlin's DataFrame library to import, manipulate and export data.

## Next steps
* Learn more about how to [import and export data](io.md)
* Learn about our different [access APIs](apiLevels.md)
* Explore the many different [operations that you can perform](operations.md)

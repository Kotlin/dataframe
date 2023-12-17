**Release activities check-list for releases:**

0. Run code inspections (fix typos, Kotlin issues, fix code formatting, linter)
1. Write missed KDocs for new APIs
2. Update tutorials according to last code changes
3. Update README.MD according last code changes
   - update an artifact version
   - update a Kotlin version
   - update the [section](README.md#kotlin-kotlin-jupyter-openapi-arrow-and-jdk-versions) about library versions
4. Update a project version in the file `gradle.properties` (i.e. 0.9.0 -> 0.10.0)
   - For major releases: update a project version in the file [`v.list`](https://github.com/Kotlin/dataframe/blame/master/docs/StardustDocs/v.list)
   - For major releases: update a project version in the file [`main.yml`](https://github.com/Kotlin/dataframe/blob/master/.github/workflows/main.yml)
   - For major releases: update a project version in the file [`project.ihp`](https://github.com/Kotlin/dataframe/blob/master/docs/StardustDocs/project.ihp)
5. Update `libs.versions.toml` file if required 
6. Create and checkout the release branch 
7. Make last commit with release tag (_v0.1.1_ for example) to the release branch 
8. Run tests and build artifacts on TC for the commit with the release tag 
9. Deploy artifacts on MavenCentral via `Publish` task running on TC based on the commit with the release tag 
10. Check artifacts' availability on [MavenCentral](https://mvnrepository.com/artifact/org.jetbrains.kotlinx/dataframe) 
11. Check [Gradle Plugin portal availability](https://plugins.gradle.org/plugin/org.jetbrains.kotlinx.dataframe/) (usually it takes 12 hours)
12. Update a bootstrap dependency version in the `libs.versions.toml` file (only after the plugin's publication)
13. Make final testing
    - Check on Datalore with a test project (TODO: add link)
    - Check for Android with a test project (TODO: add link)
    - Check for ServerSide with a test project (TODO: add link)
14. Publish Documentation from [GitHub Action](https://github.com/Kotlin/dataframe/actions/workflows/main.yml)
15. Prepare and publish the Release Notes 
16. Create Release from the release tag on GitHub 
17. Update a KDF version in the [Kotlin Jupyter Descriptor](https://github.com/Kotlin/kotlin-jupyter-libraries/blob/master/dataframe.json). Now the Renovate bot doing this
18. Update DataFrame version in the `gradle.properties` file for the next release cycle (i.e. 0.10.0 -> 0.11.0)
19. Update deprecated functions in [deprecationMessages.kt](/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/util/deprecationMessages.kt)
    such that 
    - `Level.WARNING` messages are changed to `Level.ERROR`
    - `Level.ERROR` messages and their functions are removed.
    - Update regions in the file accordingly.
20. Update Notebook examples, both in the project as well as on Datalore.

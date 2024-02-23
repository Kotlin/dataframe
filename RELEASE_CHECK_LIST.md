**Release activities check-list for releases:**

1. Run code inspections (fix typos, Kotlin issues, fix code formatting, linter). **RC**
2. Write missed KDocs for new APIs. **RC**
3. Update tutorials according to the latest code changes.
4. Update README.MD according last code changes:
   - update an artifact version.
   - update a Kotlin version.
   - update the [section](README.md#kotlin-kotlin-jupyter-openapi-arrow-and-jdk-versions) about library versions.
5. Update a project version in the file `gradle.properties` (i.e. 0.9.0 -> 0.10.0).
   - For major releases: update a project version in the file [`v.list`](https://github.com/Kotlin/dataframe/blame/master/docs/StardustDocs/v.list)
   - For major releases: update a project version in the file [`main.yml`](https://github.com/Kotlin/dataframe/blob/master/.github/workflows/main.yml)
   - For major releases: update a project version in the file [`project.ihp`](https://github.com/Kotlin/dataframe/blob/master/docs/StardustDocs/project.ihp)
6. Update `libs.versions.toml` file if required, run `./gradlew dependencyUpdates` to check for updates. **RC**
7. Create and checkout the release branch. **RC**
8. Make last commit with release tag (_v0.1.1_ for example) to the release branch. **RC**
9. Run tests and build artifacts on TC for the commit with the release tag. **RC**
10. Deploy artifacts on Maven Central via the `Publish` task running on TC based on the commit with the release tag. **RC**
11. Check artifacts' availability on [MavenCentral](https://mvnrepository.com/artifact/org.jetbrains.kotlinx/dataframe). **RC**
12. Check [Gradle Plugin portal availability](https://plugins.gradle.org/plugin/org.jetbrains.kotlinx.dataframe/) (usually it takes 12 hours). **RC**
13. Update a bootstrap dependency version in the `libs.versions.toml` file (only after the plugin's publication). **RC**
14. Do final testing: **RC**
    - Check on Datalore with a test project (TODO: add link).
    - Check for Android with a test project (TODO: add link).
    - Check for ServerSide with a test project (TODO: add link).
15. Publish Documentation from [GitHub Action](https://github.com/Kotlin/dataframe/actions/workflows/main.yml)
16. Prepare and publish the Release Notes. **RC**
17. Create a Release from the release tag on GitHub. **RC**
18. Update a KDF version in the [Kotlin Jupyter Descriptor](https://github.com/Kotlin/kotlin-jupyter-libraries/blob/master/dataframe.json). Now the Renovate bot does this.
19. Update the DataFrame version in the `gradle.properties` file for the next release cycle (i.e. 0.10.0 -> 0.11.0)
20. Update deprecated functions in [deprecationMessages.kt](/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/util/deprecationMessages.kt)
    such that 
    - `Level.WARNING` messages are changed to `Level.ERROR`
    - `Level.ERROR` messages and their functions are removed.
    - Update regions in the file accordingly.
21. Update Notebook examples, both in the project and on Datalore.

(Activities that need to be done for **R**elease **C**andidate releases are marked as such)

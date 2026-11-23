**Release activities check-list for releases:**

## Before release  ##

1. Create and checkout a new release branch. Name it "1.1.0" for example
2. Deploy a dev version from this branch to run tests with using the `Publish Dev` task on TC
3. Test this dev version with pet projects, examples, Kandy, notebooks, etc.
4. Fix any bugs if needed in `master`, rebasing the new release branch on it

## Deploying the release ##

5. Check or set the project version in the file `gradle.properties` to the intended version you want to release (be careful! TC uses this string **EXACTLY!**). For instance setting it to "1.1.0" will release DataFrame version 1.1.0
6. Make a final commit with release tag (_v1.1.0_ for example) to the release branch
7. Run tests and build artifacts on TC for the release branch
8. Deploy artifacts on Maven Central via the `Publish` task (**directly and without pre-run dependencies**) on TC based on the release branch
9. Check artifacts' availability on [MavenCentral](https://mvnrepository.com/artifact/org.jetbrains.kotlinx/dataframe)

## After release ##

10. Checkout a new post-release branch from `master`
11. Verify that code examples from `README.md` are compiled with the latest artifact version and targeted Kotlin version. Fix it if needed
12. Update README.md according to the release code changes:
   - Update the artifact version
   - Update the Kotlin version
   - Update the [section about library versions](README.md#kotlin-kotlin-jupyter-arrow-and-jdk-versions)
13. Update the project version in these files:
   - [`v.list`](https://github.com/Kotlin/dataframe/blame/master/docs/StardustDocs/v.list)
   - [`main.yml`](https://github.com/Kotlin/dataframe/blob/master/.github/workflows/main.yml)
14. Update the dataframe example version in the `libs.versions.toml` file
15. Promote dev project examples by running `gradlew :dataframe:promoteDevExamples`, verify they are working correctly
16. (Optional) Update the DF version in the [Kotlin Jupyter Descriptor](https://github.com/Kotlin/kotlin-jupyter-libraries/blob/master/dataframe.json). Now the Renovate bot does this
17. Promote the notebook examples manually by overwriting the folders in `examples/notebooks` with those in `examples/notebooks/dev`, check they work correctly and use the latest version when writing `%use dataframe`
18. Update the DataFrame version in the `gradle.properties` file for the next release cycle (i.e. 1.0.0->1.1.0)
19. Update deprecated functions in [deprecationMessages.kt](/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/util/deprecationMessages.kt) (and related instances of this file in other modules)
    such that:
    - `Level.WARNING` messages are changed to `Level.ERROR`
    - `Level.ERROR` messages and their functions are removed
    - Update regions in the file accordingly
    - (If these are too many changes, consider splitting it into a separate PR)
20. Make a post-release PR with all these changes and merge it
21. Run `Build docs` from [GitHub Action](https://github.com/Kotlin/dataframe/actions/workflows/main.yml)
22. During or after the previous steps, prepare the [release notes](https://github.com/Kotlin/dataframe/releases) and check them with the team
23. Create a release from the release tag on GitHub
24. Update the ["What's new?" section](README.md#whats-new) in `master`
25. Promote the release! :D

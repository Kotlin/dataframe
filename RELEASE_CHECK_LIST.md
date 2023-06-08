**Release activities check-list for releases:**

0. Run code inspections (fix typos, Kotlin issues, fix code formatting, linter)
1. Write missed KDocs for new APIs
2. Remove methods deprecated in the previous release 
3. Update tutorials according last code changes
4. Update README.MD according last code changes
   - update artifact version
   - update Kotlin version
   - update the [section](README.md#kotlin-kotlin-jupyter-openapi-arrow-and-jdk-versions) about library versions
5. Create and checkout the release branch
6. Update project version in the file `gradle.properties` (i.e. 0.9.0 -> 0.10.0)
   - For major releases: update project version in the file [`v.list`](https://github.com/Kotlin/dataframe/blame/master/docs/StardustDocs/v.list)
   - For major releases: update project version in the file [`main.yml`](https://github.com/Kotlin/dataframe/blob/master/.github/workflows/main.yml)
7. Update bootstrap dependency version
8. Update`libs.versions.toml` file if required
9. Make last commit with release tag (_v0.1.1_ for example) to the release branch
10. Run tests and build artifacts on TC for the commit with the release tag
11. Deploy artifacts on MavenCentral based on the commit with the release tag 
12. Check artifacts' availability on MavenCentral 
13. Check Gradle Plugin portal availability (usually it takes 12 hours)
14. Make final testing
    - Check on Datalore with test project (TODO: add link)
    - Check for Android with test project (TODO: add link)
    - Check for serverside with test project (TODO: add link)
15. Publish Documentation from [GitHub Action](https://github.com/Kotlin/dataframe/actions/workflows/main.yml)
16. Prepare and publish the Release Notes 
17. Create Release from release tag on the GitHub 
18. Update a KDF version in the [Kotlin Jupyter Descriptor](https://github.com/Kotlin/kotlin-jupyter-libraries/blob/master/dataframe.json). Now the Renovate bot doing this 

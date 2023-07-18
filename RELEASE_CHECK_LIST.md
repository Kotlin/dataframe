**Release activities check-list for releases:**

0. Run code inspections (fix typos, Kotlin issues, fix code formatting, linter)
1. Write missed KDocs for new APIs
2. Remove methods deprecated in the previous release 
3. Update tutorials according last code changes
4. Update README.MD according last code changes
   - update artifact version
   - update Kotlin version
   - update the [section](README.md#kotlin-kotlin-jupyter-openapi-arrow-and-jdk-versions) about library versions
5. Update a project version in the file `gradle.properties` (i.e. 0.9.0 -> 0.10.0)
   - For major releases: update a project version in the file [`v.list`](https://github.com/Kotlin/dataframe/blame/master/docs/StardustDocs/v.list)
   - For major releases: update a project version in the file [`main.yml`](https://github.com/Kotlin/dataframe/blob/master/.github/workflows/main.yml)
   - For major releases: update a project version in the file [`project.ihp`](https://github.com/Kotlin/dataframe/blob/master/docs/StardustDocs/project.ihp)
6. Update `libs.versions.toml` file if required 
7. Create and checkout the release branch 
8. Make last commit with release tag (_v0.1.1_ for example) to the release branch 
9. Run tests and build artifacts on TC for the commit with the release tag 
10. Deploy artifacts on MavenCentral via `Publish` task based on the commit with the release tag 
11. Check artifacts' availability on MavenCentral 
12. Check Gradle Plugin portal availability (usually it takes 12 hours)
13. Update a bootstrap dependency version in the `libs.versions.toml` file (only after plugin's publication)
14. Make final testing
    - Check on Datalore with a test project (TODO: add link)
    - Check for Android with a test project (TODO: add link)
    - Check for ServerSide with a test project (TODO: add link)
15. Publish Documentation from [GitHub Action](https://github.com/Kotlin/dataframe/actions/workflows/main.yml)
16. Prepare and publish the Release Notes 
17. Create Release from the release tag on GitHub 
18. Update a KDF version in the [Kotlin Jupyter Descriptor](https://github.com/Kotlin/kotlin-jupyter-libraries/blob/master/dataframe.json). Now the Renovate bot doing this 
19. Update DataFrame version in gradle.properties file for next release cycle (i.e. 0.10.0 -> 0.11.0)

**Release activities check-list for releases:**

0. Run code inspections (fix typos, Kotlin issues, fix code formatting, linter)
1. Write missed KDocs for new APIs
2. Remove methods deprecated in the previous release 
3. Update tutorials according last code changes
4. Update README.MD according last code changes
   - update artifact version
   - update Kotlin version
5. Create and checkout the release branch
6. Update project version in the file `gradle.properties` (i.e. 0.9.0 -> 0.10.0)
7. Update bootstrap dependency version
8. Make last commit with release tag (_v0.1.1_ for example) to the release branch
9. Run tests and build artifacts on TC for the commit with the release tag
10. Deploy artifacts on MavenCentral based on the commit with the release tag 
11. Check artifacts' availability on MavenCentral 
12. Make final testing
    - Check on Datalore with test project (TODO: add link)
    - Check for Android with test project (TODO: add link)
    - Check for serverside with test project (TODO: add link)
13. Publish Documentation from TC 
14. Prepare and publish the Release Notes
15. Create Release from release tag on the Github



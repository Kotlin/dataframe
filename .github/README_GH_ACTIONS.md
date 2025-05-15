## GitHub Actions

While publishing and testing of the library is handled by [JetBrains TeamCity](https://www.jetbrains.com/teamcity/),
there are some CI operations that are handled by GitHub actions instead.

### Publishing Docs

The building of the documentation website in [docs](../docs), and the publishing of it along
with the search-indices is handled by the [Build Docs GH Action](./workflows/main.yml). Careful: This action replaces the entire contents
of the documentation website.

### Gradle Wrapper Validation

We run the [Gradle Wrapper Validation action](https://github.com/gradle/wrapper-validation-action)
using a [GitHub Action](./workflows/gradle-wrapper-validation.yml) as well.
This action validates the checksums of all Gradle Wrapper JAR files present in the repository and
fails if any unknown Gradle Wrapper JAR files are found.

### Auto-commit generated code

Anytime the source code changes on [master](https://github.com/Kotlin/dataframe/tree/master),
this [GitHub Action](./workflows/generated-sources-master.yml) makes sure
[`processKDocsMain`](../KDOC_PREPROCESSING.md),
and `korro` are run. If there have been any changes in either [core/generated-sources](../core/generated-sources) or
[docs/StardustDocs/resources/snippets](../docs/StardustDocs/resources/snippets), these are auto-committed to the branch, to keep
it up to date.

### Show generated code in PR

To make sure no unexpected code is auto-committed to [master](https://github.com/Kotlin/dataframe/tree/master),
this [GitHub Action](./workflows/generated-sources.yml) runs the same code-generating tasks but on a separate branch.
If there are changes, it will leave a message in the PR, informing you about the changes that will be done to the master
branch if this PR were merged.

# Contributing Guidelines

There are two main ways to contribute to the project &mdash; submitting issues and submitting
fixes/changes/improvements via pull requests.

## Submitting issues

Both bug reports and feature requests are welcome.
Submit issues [here](https://github.com/Kotlin/dataframe/issues).

* Search for existing issues to avoid reporting duplicates.
* When submitting a bug report:
    * Test it against the most recently released version. It might have already been fixed.
    * Include the code reproducing the problem or attach the link to the repository with the project that fully reproduces the problem.
    * However, don't put off reporting any weird or rarely appearing issues just because you cannot consistently
      reproduce them.
    * If the bug is in behavior, then explain what behavior you've expected and what you've got.
* When submitting a feature request:
    * Explain why you need the feature &mdash, your use case, and your domain.
    * Explaining the problem you face is more important than suggesting a solution.
      Report your issue even if you don't have any proposed solution.
    * If there is an alternative way to do what you need, show the alternative's code.


## Submitting PRs

We love PRs. Submit PRs [here](https://github.com/Kotlin/dataframe/pulls).
However, please keep in mind that maintainers will have to support the resulting code of the project,
so do familiarize yourself with the following guidelines.

* All development (both new features and bug fixes) is performed in the `master` branch.
    * Base PRs against the `master` branch.
    * PR should be linked with the issue,
      excluding minor documentation changes, adding unit tests, and fixing typos.
* If you make any code changes:
    * Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html). 
      [Ktlint](https://pinterest.github.io/ktlint/latest/) can help here.
    * [Build the project](#building) to ensure it all works and passes the tests.
* If you fix a bug:
    * Write the test that reproduces the bug.
    * Fixes without tests are accepted only in exceptional circumstances if it can be shown that writing the
      corresponding test is too hard or otherwise impractical.
* If you introduce any new public APIs:
    * All new APIs must come with documentation and tests.
    * If you plan API additions, please start by submitting an issue with the proposed API design
      to gather community feedback.
    * [Contact the maintainers](#contacting-maintainers) to coordinate any great work in advance via submitting an issue.
* If you fix documentation:
    * If you plan extensive rewrites/additions to the docs, then please [contact the maintainers](#contacting-maintainers)
      to coordinate the work in advance.
    * Also, we have a special simple [guide](https://github.com/Kotlin/dataframe/blob/master/docs/contributions.md) how to contribute in the documentation.

## PR workflow

0. The contributor builds the library locally and runs all unit tests via the Gradle task 
   `dataframe:test -Pkotlin.dataframe.debug=true` (see the ["Building"](#building) chapter).
1. The contributor submits the PR if the local build is successful and the tests are green.
2. The reviewer puts their name in the "Reviewers" section of the proposed PR at the start of the review process.
3. The reviewer leaves comments or marks the PR with the abbreviation "LGTM" (Looks good to me).
4. The contributor answers the comments or fixes the proposed PR.
5. The reviewer marks the PR with the word "LGTM."
6. The maintainer could suggest merging the `master` branch to the PR branch a few times due to changes in the `master` branch.
7. If the PR influences generated code/samples, a bot will inform about this in the PR checks.
8. The maintainer runs TeamCity builds (unit tests and examples as integration tests).
9. TeamCity writes the result (passed or not passed) to the PR checks at the bottom of the proposed PR.
10. If it is possible, maintainers share the details of the failed build with the contributor.
11. The maintainer merges the PR if all checks are successful and there is no conflict with the `master` branch.
12. The maintainer closes the PR and the issue linked to it.
13. If the PR influences generated code, a bot will auto-commit the newly generated code into the `master` branch.

## How to fix an existing issue

* If you are going to work on the existing issue:
    * Comment on the existing issue if you want to work on it.
    * Wait till it is assigned to you by [maintainers](#contacting-maintainers).
    * Ensure that the issue describes a problem and a solution that has received positive feedback. Propose a solution if there isn't any.
* If you are going to submit your first PR in this project:
    * Find tickets with the label ["good first issue"](https://github.com/Kotlin/dataframe/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22+no%3Aassignee)
      which are not assigned to somebody.
    * Learn the [`examples`](https://github.com/Kotlin/dataframe/tree/master/examples) module. Submit an interesting new example or improve documentation for one of them.
* If you are ready to participate in library design and new experiments, find tickets with the label
  ["research"](https://github.com/Kotlin/dataframe/issues?q=is%3Aissue+is%3Aopen+label%3Aresearch)
  or join our [discussions](https://github.com/Kotlin/dataframe/discussions).


## Environment requirements

* JDK >= 21 referred to by the `JAVA_HOME` environment variable.

  * Note, any version above 21 should work in theory, but JDK 21 is the only version we test with,
  so it is the recommended version.

* JDK == 11 referred to by the `JDK_11_0` environment variable or `gradle.properties`/`local.properties`.
  * This is used for testing our compiler plugins.

* We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) as the IDE. This
has the best support for Kotlin, compiler plugins, Gradle, and [Kotlin Notebook](https://kotlinlang.org/docs/kotlin-notebook-overview.html) of course.

* We recommend using the [Ktlint plugin](https://plugins.jetbrains.com/plugin/15057-ktlint) for [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).
It is able to read the `.editorconfig` file and apply the same formatting rules as [Ktlint](https://pinterest.github.io/ktlint/latest/) in the CI.

* Check out the [KDoc Preprocessor guide](KDOC_PREPROCESSING.md) to understand how to work with
[KoDEx](https://github.com/Jolanrensen/KoDEx).

## Building

This library is built with Gradle.

* Run `./gradlew build` to build. It also runs all the tests and checks the linter.
* Run `./gradlew <module>:test` to test the module you are looking at to speed
  things up during development.
* Make sure to pass the extra parameter `-Pkotlin.dataframe.debug=true` to enable debug mode. This flag will
  make sure some extra checks are run, which are important but too heavy for production.
* The parameter `-PskipKodex` allows you to skip [kdoc processing](KDOC_PREPROCESSING.md),
  making local publishing faster: `./gradlew publishToMavenLocal -PskipKodex`.
  This, however, publishes the library with "broken" KDocs, 
  so it's only meant for faster iterations during development.

You can import this project into IDEA, but you have to delegate the build actions
to Gradle (in Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Runner)

## Contacting maintainers

* If something cannot be done or doesn't work conveniently &mdash; submit an [issue](#submitting-issues).
* To attract attention to your problem, raise a question, or make a new comment, mention one of us on Github: @koperagen @Jolanrensen @zaleslaw @ileasile
* Discussions and general inquiries &mdash; use `#datascience` channel in [KotlinLang Slack](https://kotl.in/slack).

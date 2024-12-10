## :plugins:keywords-generator

This module holds a little Gradle plugin whose sole purpose is to provide
[:core](../../core) with the `generateKeywordsSrc` task.

This task, generates three enum classes: `HardKeywords`, `ModifierKeywords`, and `SoftKeywords`.
These enums together contain all restricted Kotlin keywords to be taken into account when generating our own
code in Notebooks or any of our [plugins](..). Words like "package", "fun", "suspend", etc...

As the Kotlin language can change over time, this task ensures that any changes to the language
will be reflected in our code generation.

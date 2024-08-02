# KDoc Preprocessing

You might have spotted some notations like `{@include [Something]}` in the `/** KDocs */` of DataFrame's source code.
These are special notations for the [KDoc preprocessor](https://github.com/Jolanrensen/docProcessorGradlePlugin)
that we use to generate parts of the KDoc documentation.

Kotlin libraries like DataFrame use KDoc to document their code and especially their public API. This allows users
to understand how to use the library and what to expect from it. However, writing KDoc can be a tedious task, especially
when you have to repeat the same information in multiple places. The KDoc preprocessor allows us to write the
information
only once and then include it in multiple places.

This document explains how to use the KDoc preprocessor in the DataFrame project.

## How the Processing Works

Unlike Java, Kotlin library authors
[don't have the ability to share a jar file with documentation](https://github.com/Kotlin/dokka/issues/2787). They have
to share documentation along with their `sources.jar` file which users can attach in their IDE to see the docs.
DataFrame thus uses the preprocessor in Gradle to copy and modify the source code, processing the KDoc notations,
and publishing the modified files as the `sources.jar` file.

This can be seen in action in the `core:processKDocsMain` and `core:changeJarTask` Gradle tasks in the
[core/build.gradle.kts file](core/build.gradle.kts). When you run any `publish` task in the `core` module, the
`processKDocsMain` task is executed first, which processes the KDocs in the source files and writes them to the
`generated-sources` folder. The `changeJarTask` task then makes sure that any `Jar` task in the `core` module uses the
`generated-sources` folder as the source directory instead of the normal `src` folder.

`core:processKDocsMain` can also be run separately if you just want to see the result of the KDoc processing.

To make sure the generated sources can be seen and reviewed on GitHub,
since [PR #731](https://github.com/Kotlin/dataframe/pull/731),
there's a [GitHub action](.github/workflows/generated-sources.yml) that runs the `core:processKDocsMain` task and
comments the results on the PR. After a PR is merged, [another action](.github/workflows/generated-sources-master.yml)
runs on the master branch and commits the generated sources automatically.
This way, the generated sources are always up to date with the latest changes in the code.
This means you don't have to run and commit the generated sources yourself, though it's
still okay if you do.

The processing by the KDoc preprocessor is done in multiple "waves" across the source files.
Each "wave" processes different notations and depends on the results of previous waves.
DataFrame uses
the [recommended order](https://github.com/Jolanrensen/docProcessorGradlePlugin/tree/main?tab=readme-ov-file#recommended-order-of-default-processors)
of processors, which is as follows:

- `INCLUDE_DOC_PROCESSOR`: The `@include` processor
- `INCLUDE_FILE_DOC_PROCESSOR`: The `@includeFile` processor
- `ARG_DOC_PROCESSOR`: The `@set` and `@get` / `$` processor. This runs `@set` first and then `@get` / `$`.
- `COMMENT_DOC_PROCESSOR`: The `@comment` processor
- `SAMPLE_DOC_PROCESSOR`: The `@sample` and `@sampleNoComments` processor
- `EXPORT_AS_HTML_DOC_PROCESSOR`: The `@exportAsHtmlStart` and `@exportAsHtmlEnd` tags for `@ExportAsHtml`
- `REMOVE_ESCAPE_CHARS_PROCESSOR`: The processor that removes escape characters

See the [Notation](#notation) section for more information on each of these processors.

## Previewing the Processed KDocs in IntelliJ IDEA

The preprocessor comes with an (experimental)
[IntelliJ IDEA plugin](https://github.com/Jolanrensen/docProcessorGradlePlugin?tab=readme-ov-file#intellij-plugin-alpha)
that allows you to preview the processed KDocs without having to run the Gradle task.

![image](https://github.com/Jolanrensen/docProcessorGradlePlugin/assets/17594275/7f051063-38c7-4e8b-aeb8-fa6cf14a2566)

As described in the README of the preprocessor, the plugin may not 100% match the results of the Gradle task. This is
because it uses IntelliJ to resolve references instead of Dokka. However, it should give you a good idea of what the
processed KDocs will look like, and, most importantly, it's really fast.

You can install the plugin by building the project yourself or by downloading the latest release from the
[releases page](https://github.com/Jolanrensen/docProcessorGradlePlugin/releases).
Simply look for the latest release which has the zip file attached.
If it's outdated or doesn't work on your version of IntelliJ, don't hesitate to
ping [@Jolanrensen](https://github.com/Jolanrensen)
on GitHub. This also applies if you have any issues with the IntelliJ or Gradle plugin, of course :).

## Notation

The KDoc preprocessor uses special notations in the KDocs to indicate that a certain (tag) processor should be applied
in that place.
These notations follow the Javadoc/KDoc `@tag content`/`{@tag content}` tag conventions.

Tags without `{}` are allowed, but only at the beginning of a line, like you're used to with
`@param`, `@return`, `@throws`, etc. If you want to use them in the middle of a line, or inside ` ``` ` blocks,
you should use `{}`.

Tag processors have access to any number of arguments they need, which are separated by spaces, like:

```kt
/**
 * @tag arg1 arg2 arg3 extra text
 * or {@tag arg1 arg2 arg3}
 */
```

though, most only need one or two arguments.
It's up to the tag processor what to do with excessive arguments, but most tag processors will leave them in place.

### `@include`: Including content from other KDocs

<p align="center">
  <img src="docs/imgs/include1.png" alt="include1.png" width="45%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
  <img src="docs/imgs/include2.png" alt="include2.png" width="45%"/>
</p>

The most used tag across the library is `@include [Reference]`.
This tag includes all the content of the supplied reference's KDoc in the current KDoc.
The reference can be a class, function, property, or any other documented referable entity
(type aliases are an exception, as Dokka does not support them).
The reference can be a fully qualified name or a relative name; imports and aliases are taken into account.

You cannot include something from another library at the moment.

Writing something after the include tag, like

```kt
/**
 * @include [Reference] some text
 */
```

is allowed and will remain in place. Like:

```kt
/**
 * This is from the reference. some text
 */
```

Referring to a function with the same name as the current element is allowed and will be resolved correctly
(although, the IntelliJ plugin will not resolve it correctly).
The preprocessor assumes you don't want a circular reference, as that does not work for obvious reasons.

Finally, if you include some KDoc that contains a `[reference]`, the preprocessor will replace that reference
with its fully qualified path. This is important because we cannot assume that the target file has access to
the same imports as the source file. The original name will be left in place as alias, like
`[reference][path.to.reference]`.
This is also done for references used as key in `@set` and `@get` / `$` tags.

### `@includeFile`: Including all content from a relative file

This tag is not used in the DataFrame project at the moment. It's used like:

```kt
/**
 * @includeFile (path/to/file.kt)
 */
```

and, as expected, it pastes the content of the file at the location of the tag.

Both the relative- and absolute paths are supported.

### `@set` and `@get` / `$`: Setting and getting variables

<p align="center">
  <img src="docs/imgs/arg1.png" alt="arg1.png" width="45%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
  <img src="docs/imgs/arg2.png" alt="arg2.png" width="45%"/>
</p>

Combined with `@include`, these tags are the most powerful ones available.
They allow you to create templates and fill them in with different values at the location they're included.

`@set` is used to set a variable, and `@get` / `$` is used to get the value of a variable
(with an optional default value).

What's important to note is that this processor is run **after** the `@include` processor and the variables
that are created with `@set` are only available in the current KDoc.

To form an idea of how they are processed, it's best to think of waves of processing again.

All `@set` tags are processed before any `@get` / `$` tags.
So there's no `{@set A {@get B}}` cycle, as that would not work.

For example, given the KDoc from the picture above:

```kt
/**
 * @include [Doc]
 * @set NAME Function A
 */
```

After running the `@include` processor, the intermediate state of the KDoc will be:

```kt
/**
 * This is {@get NAME default} and it does something cool
 * @set NAME Function A
 */
```

Then, all `@set` statements are processed:

```kt
/**
 * This is {@get NAME default} and it does something cool
 */
```

`NAME` is `"Function A"` now.

Then all `@get` statements are processed:

```kt
/**
 * This is Function A and it does something cool
 */
```

You can put as many `@set` and `@get` / `$` tags in a KDoc as you want, just make sure to pick unique
key names :).
I'd always recommend using a `[Reference]` as key name.
It's a good practice to keep the key names unique and refactor-safe.

Finally, you need to make sure you take the order of tags processing into account. As stated by
the [README](https://github.com/Jolanrensen/docProcessorGradlePlugin/tree/main?tab=readme-ov-file#preprocessors),
tags are processed in the following order:

* Inline tags
    * depth-first
    * top-to-bottom
    * left-to-right
* Block tags
    * top-to-bottom

This means that you can overwrite a variable by a block tag that was set by an inline tag even if the
inline tag is written below the block tag!

For example:

```kt
/**
 * $NAME
 * @set NAME a
 * {@set NAME b}
 */
```

Here, `NAME` is first set to `"b"` and the ` {@set NAME b}` part is erased from the doc.
Then `NAME` is set to `"a"` and that line disappears too.
`$NAME` is rewritten to `{@get NAME}` and then it's replaced by retrieving the value of `NAME`,
which makes the final doc look like:

```kt
/**
 * a
 *
 */
```

### `@comment`: Commenting out KDoc content

<p align="center">
  <img src="docs/imgs/comment1.png" alt="comment1.png" width="45%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
  <img src="docs/imgs/comment2.png" alt="comment2.png" width="45%"/>
</p>

Just like being able to use `//` in code to comment out lines, you can use `@comment` to comment out KDoc content.
This is useful for documenting something about the preprocessing processes that should not be visible in the
published `sources.jar`.

Anything inside a `@comment` tag block or inline tag `{}` will be removed from the KDoc when the processor is run.

### `@sample` and `@sampleNoComments`: Including code samples

<p align="center">
  <img src="docs/imgs/sample1.png" alt="sample1.png" width="45%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
  <img src="docs/imgs/sample2.png" alt="sample2.png" width="45%"/>
</p>

While this processor is not used in the DataFrame project at the moment, it can be seen as an extension
to the normal `@sample` tag. While the 'normal' `@sample [Reference]` tag shows the code from the target reference as
is,
`@sample` and `@sampleNoComments` actually copy over the code to inside a ` ```kt ``` ` (or `java`) code block in the
KDoc.

Just like [korro](https://github.com/devcrocod/korro), if `// SampleStart` or `// SampleEnd` are present in the code,
only the code between these markers will be included in the KDoc.

`@sampleNoComments` is the same as `@sample`, but it will remove all comments from the code before pasting it in the
KDoc.

### `@exportAsHtmlStart` and `@exportAsHtmlEnd`: Exporting content as HTML

See [KDoc -> WriterSide](#kdoc---writerside).

### `\`: Escape Character

The final wave of processing is the removal of escape characters.
This is done by the `REMOVE_ESCAPE_CHARS_PROCESSOR`.

The escape character `\` is used to escape the special characters `@`, `{`, `}`, `[`, `]`, `$`, and `\` itself.
Escaped characters are ignored by processors and are left in place.

This means that `/** {\@get TEST} */` will become `/** {@get TEST} */` after preprocessing instead of actually
fetching the value of `TEST`.
Similarly, `/** [Reference\] */` will not be replaced by the fully qualified path of `Reference` after it is
`@include`'d somewhere else.
This can come in handy when building difficult templates containing a lot of `[]` characters that should not be
treated as references.

### `@ExcludeFromSources` Annotation: Excluding code content from sources

<p align="center">
  <img src="docs/imgs/excludeFromSources1.png" alt="excludeFromSources.png" width="45%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
  <img src="docs/imgs/excludeFromSources2.png" alt="excludeFromSources.png" width="45%"/>
</p>

The `@ExcludeFromSources` annotation is used to exclude a class, function, or property from the `sources.jar` file.
This is useful to clean up the sources and delete interfaces or classes that are only used as KDoc 'source'.

The annotation is not a KDoc tag, but a normal Kotlin annotation that is detected by the preprocessor.

Since [v0.3.9](https://github.com/Jolanrensen/docProcessorGradlePlugin/releases/tag/v0.3.9) it's also possible to
exclude a whole file from the `sources.jar` by adding the annotation to the top of the file,
like `@file:ExcludeFromSources`.

## KDoc Preprocessor Conventions in DataFrame

## KDoc -> WriterSide

### `@ExportAsHtml`

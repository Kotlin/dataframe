## The Documentation Website

This folder holds the source code of our documentation website:
[kotlin.github.io/dataframe](https://kotlin.github.io/dataframe).

It's built using [Writerside](https://www.jetbrains.com/writerside/) and published
by a [Github Action](../.github/workflows/main.yml).
The file structure largely mirrors the default Writerside structure.
For instance, if you want to add a new page to the website, this needs to be stored as an `.md` file in the
[StardustDocs/topics](./StardustDocs/topics) folder,
and included in the [StardustDocs/d.tree](./StardustDocs/d.tree) file.

Images all README files can be stored in [docs/imgs](./imgs).

## Running the Documentation Website Locally

⚠️ IFrames are not displayed when using the Writerside plugin preview. To view them correctly, you need to run the website locally.

### Option 1: Using IntelliJ IDEA Configurations

> Requires: `unzip` utility and **Python 3** installed in the environment. May not work properly on Windows.

1. Run the `Build Docs Website` configuration.
2. Run the `Run Docs Website Locally` configuration.
3. Open [http://localhost:8000/home.html](http://localhost:8000/home.html) in your browser.
4. Shut down the server with Ctrl+C or close the terminal window.

### Option 2: Manual Steps

1. Go to `Writerside > DataFrame(d) > Export To > Build Docs Website` or find Writerside web archive build in IDEA configuration.
2. Unzip the archive located at: `./build/ws/webHelpD2-all.zip`
3. Start a local web server in the extracted folder (e.g., using Python or `http-server` utility):
   ```bash
   python3 -m http.server
   # or
   http-server
   ```
4. Open [http://localhost:8000/home.html](http://localhost:8000/home.html) in your browser.
5. Shut down the server with Ctrl+C or close the terminal window.

### Explainer dataframes
`@TransformDataFrameExpressions` annotated test functions generate sample
dataframe HTML files that can be used as iFrames on the documentation website.
They are tested, generated, and copied over to [docs/StardustDocs/resources/snippets](StardustDocs/resources/snippets) by
our "explainer" [plugin callback proxy](../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/explainer),
which hooks into [the TestBase class](../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/TestBase.kt) and
retrieves the intermediate DataFrame expressions thanks to
our "explainer" compiler plugin [:plugins:expressions-converter](../plugins/expressions-converter).

We can also generate "normal" DataFrame samples for the website. This can be done using the
[OtherSamples class](../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/OtherSamples.kt). Generated
HTML files will be stored in [docs/StardustDocs/resources/snippets/manual](StardustDocs/resources/snippets/manual).

### KDoc Preprocessor
KDocs can also be exported to HTML, for them to be reused on the website.
Elements annotated with `@ExportAsHtml` will have their generated content be copied over to
[docs/StardustDocs/resources/snippets/kdocs](StardustDocs/resources/snippets/kdocs).

### Korro code samples
Code samples for the documentation website reside in [core/.../test/.../samples/api](../core/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api)
(for samples that depend solely on `dataframe-core`),
and [tests/.../samples/api](../tests/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api) (for samples can depend on other I/O modules)
and they are copied over to Markdown files in [docs/StardustDocs/topics](./StardustDocs/topics)
by [Korro](https://github.com/devcrocod/korro).

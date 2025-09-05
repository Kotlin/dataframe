## :samples

Code samples, as well as DataFrame iframes and Kandy plot images, for the 
[documentation website](https://github.com/Kotlin/dataframe).

### Korro

Saves code samples using [Korro](https://github.com/devcrocod/korro).

To save or update samples:
* Run the `korroClean` and `korro` Gradle tasks.

**Important**: May not work correctly until the 
[migration from `:core` is finished](https://github.com/Kotlin/dataframe/issues/898).
Run Korro tasks for the whole project.

### SampleHelper

[`SampleHelper`](https://github.com/Kotlin/kandy/blob/samples_util/util/kandy-samples-utils/README.md)
allows you to save the resulting Kandy plots as SVG images and DataFrames as iframes.

Running tests in this module will save or update these samples.

**Important**:

1) If a sample has changed, verify that the change is intentional and correct.
You can track it with the Git file changes tracker in IDEA.
2) Add all iframes as resources in [this file](../docs/StardustDocs/topics/_shadow_resources.md).
Run [this script](https://github.com/Kotlin/kandy/blob/samples_util/util/kandy-samples-utils/README.md#how-to-use)
to update them.

### Notebook-To-Doc

A Kotlin notebook can be easily converted to documentation using 
[this script](https://github.com/Kotlin/kandy/blob/samples_util/util/kandy-samples-utils/README.md#how-to-use).
It produces two files: `.kt` and `.md`.
* Place the `.kt` file in the tests of this module and run it.
* Place the `.md` file in the [docs topics directory](../docs/StardustDocs/topics).
* Run the Korro tasks.

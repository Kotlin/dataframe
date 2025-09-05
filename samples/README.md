## :samples

Code samples as well as DataFrame iframes and Kandy plot images for the 
[documentation website](https://github.com/Kotlin/dataframe).

### Korro

Saves code samples using [Korro](https://github.com/devcrocod/korro).

To save / update samples:
* Run `korroClean` and `korro` gradle tasks.

**Important**: may work incorrectly until 
[migration from `:core` is not finished](https://github.com/Kotlin/dataframe/issues/898).
Run Korro tasks **for the whole project**.

### SampleHelper

[`SampleHelper`](https://github.com/Kotlin/kandy/blob/samples_util/util/kandy-samples-utils/README.md)
allows to save resulting Kandy plots as SVG images and `DataFrame`s as iframes.

Running tests in this module will save or update these samples.

**Important**: 

1) if the sample is changed, check if it is (так и надо было или нет короче переформулируй).
You can track it with the help of the git file changes tracker in IDEA.
2) All iframes should be added as resources in [this file](../docs/StardustDocs/topics/_shadow_resources.md). 
Run [this script](https://github.com/Kotlin/kandy/blob/samples_util/util/kandy-samples-utils/README.md#how-to-use)
to update them.

### Notebook-To-Doc

Kotlin notebook can be easily converted to a documentation using 
[this script](https://github.com/Kotlin/kandy/blob/samples_util/util/kandy-samples-utils/README.md#how-to-use).
It produces two files: `.kt` and `.md`. 
* Place `.kt` file in the tests in this module, and run it.
* Place `.md` file in the [docs topics directory](../docs/StardustDocs/topics).
* Run Korro tasks.

# Contributing to the documentation

Kotlin DataFrame library's documentation is built from `.md` files in `docs/StardustDocs/topics`. 
If you want to contribute to the documentation, find a suitable topic or create a new one.  
A newly created topic must be added to `docs/StardustDocs/d.tree` to become visible in the navigation tree.

Some topics include code snippets in the text. 
1. To add a snippet, find the tests associated with the topic. 
For example, for `add.md` it can be seen at the beginning of the file:
`<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->`

2. Write the code you want to include in the documentation in this file, surrounding the interesting part with comments:
```kotlin
@Test
fun add() {
    // SampleStart
    df.add("year of birth") { 2021 - age }
    // SampleEnd
}
```

3. Add a placeholder for the sample where you need it inside the topic:
```text
<!---FUN add-->

<!---END-->
```
4. Launch the `korro` gradle task. After execution, the topic should be updated with the code from the test. It's a known issue that sometimes task execution fails. In that case, try restarting the IDE or launching from a terminal.

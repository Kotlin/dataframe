# Contributing to the documentation

DataFrame documentation is built from `.md` files in `docs/StardustDocs/topics`. 
If you want to contribute to the documentation, find a suitable topic or create a new one.  
Newly created topic must be added to a `docs/StardustDocs/topics/d.tree` to become visible in a navigation tree.

Some topics include code snippets in the text. 
1. To add a snippet, find tests associated with topic. 
For example, for `add.md` it can be seen in the beginning of the file:
`<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->`

2. Write the code you want to include in the documentation in this file, surround interesting part with comments:
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
4. Launch a `korro` gradle task. After execution topic should be updated with the code from the test. It's a known issue that sometimes task execution fails. In that case try restarting IDE or launch from a terminal.

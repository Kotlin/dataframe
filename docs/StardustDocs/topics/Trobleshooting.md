# Troubleshooting

## Freezing when working with large page sizes

Some operations in the UI may lag or freeze noticeably when working with large page sizes. Examples of these operations include sorting using the column header or navigating to the next page. This occurs because the UI attempts to render all the data at once, leading to excessive allocations and garbage collection (GC) pressure.

To mitigate this issue, try the following:
- Reduce the page size to a smaller value.
- Increase the JVM heap size in the Kotlin Notebook Plugin settings.
- Tune the JVM GC settings of IntelliJ IDEA.
    - For example, adjust the `G1ReservePercent` parameter.
    - Further tuning is possible. For more detailed guidance, refer to the [Java Garbage-First Garbage Collector Tuning manual](https://docs.oracle.com/en/java/javase/17/gctuning/garbage-first-garbage-collector-tuning.html#GUID-90E30ACA-8040-432E-B3A0-1E0440AB556A).

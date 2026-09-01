# `groupBy` optimization steps

This report compares the original `groupBy` implementation with five cumulative optimization steps. The regular
[`groupBy`](src/main/kotlin/org/jetbrains/kotlinx/dataframe/impl/api/groupBy.kt) is the unchanged implementation from
`master`; every numbered implementation is a full source fork intended for review and side-by-side benchmarking.

## Changes

| Entry point | Cumulative change |
|---|---|
| `groupBy` | Original collection pipeline, dataframe permutation, and chunking |
| `groupBy1` | Build groups with a loop and `LinkedHashMap`, without row-key `Pair` objects |
| `groupBy2` | Read keys from the underlying data columns instead of `ColumnWithPath` delegation |
| `groupBy3` | Use scalar map keys for a single key column instead of allocating a list per row |
| `groupBy4` | Build group dataframes column-first with sequential source reads |
| `groupBy5` | Process independent source columns in parallel |

The numbered implementations are test-only fixtures in this benchmark commit. Each step contains all earlier changes;
compare adjacent files in this commit to see only the newly introduced mechanism.

## Benchmark setup

- Command: `./gradlew :core:testGroupByBenchmark --console=plain`
- Result: `core/build/reports/benchmarks/groupBy/2026-09-01T20.12.16.319742438/test.json`
- Allocation command: `java -jar core/build/benchmarks/test/jars/core-test-jmh-1.0.0-dev-JMH.jar
  '.*GroupByBenchmark.*' -prof gc -wi 5 -i 5 -w 1s -r 1s -f 1`
- JMH: 1 fork, 5 × 1-second warm-up iterations, 5 × 1-second measurement iterations, average time
- Runtime: OpenJDK 21.0.1
- Machine: Linux x86-64, Intel Core i7-13700KF, 16 cores / 24 hardware threads
- Data: deterministic shuffled group ids and deterministic value columns

The JSON file is an ignored build artifact. The values below are the persisted review record.

Scenario abbreviations use `rows / groups / key columns / value columns`.

## Average time

Lower is better. Values are `ms/op ± 99.9% error`.

| Scenario | Baseline | Step 1 | Step 2 | Step 3 | Step 4 | Step 5 |
|---|---:|---:|---:|---:|---:|---:|
| 10K / 10 / 1 / 4 | 0.477 ± 0.082 | 0.407 ± 0.027 | 0.396 ± 0.021 | 0.316 ± 0.008 | 0.295 ± 0.037 | 0.157 ± 0.006 |
| 100K / 10 / 1 / 4 | 4.294 ± 0.273 | 5.140 ± 0.187 | 5.148 ± 0.286 | 3.052 ± 0.153 | 2.528 ± 0.065 | 1.301 ± 0.088 |
| 100K / 1K / 1 / 4 | 13.979 ± 1.914 | 15.589 ± 8.195 | 14.411 ± 0.608 | 11.966 ± 0.415 | 6.563 ± 0.279 | 3.977 ± 0.168 |
| 100K / 1K / 3 / 4 | 19.954 ± 1.102 | 21.438 ± 0.345 | 21.976 ± 0.619 | 21.220 ± 1.309 | 12.697 ± 0.300 | 8.172 ± 0.414 |
| 100K / 1K / 1 / 32 | 73.140 ± 1.840 | 76.359 ± 6.471 | 76.856 ± 7.140 | 70.400 ± 1.520 | 37.224 ± 2.266 | 16.644 ± 4.693 |
| 1M / 1K / 1 / 4 | 155.376 ± 32.721 | 184.544 ± 37.255 | 190.048 ± 35.423 | 128.821 ± 24.579 | 33.321 ± 1.702 | 20.265 ± 1.514 |

## Speedups

Each cell is `versus baseline / versus previous step`. Values above `1.00×` are faster; values below `1.00×` are
regressions. Ratios from measurements with overlapping confidence intervals should be treated as directional.

| Scenario | Step 1 | Step 2 | Step 3 | Step 4 | Step 5 |
|---|---:|---:|---:|---:|---:|
| 10K / 10 / 1 / 4 | 1.17× / 1.17× | 1.20× / 1.03× | 1.51× / 1.26× | 1.61× / 1.07× | 3.04× / 1.88× |
| 100K / 10 / 1 / 4 | 0.84× / 0.84× | 0.83× / 1.00× | 1.41× / 1.69× | 1.70× / 1.21× | 3.30× / 1.94× |
| 100K / 1K / 1 / 4 | 0.90× / 0.90× | 0.97× / 1.08× | 1.17× / 1.20× | 2.13× / 1.82× | 3.51× / 1.65× |
| 100K / 1K / 3 / 4 | 0.93× / 0.93× | 0.91× / 0.98× | 0.94× / 1.04× | 1.57× / 1.67× | 2.44× / 1.55× |
| 100K / 1K / 1 / 32 | 0.96× / 0.96× | 0.95× / 0.99× | 1.04× / 1.09× | 1.96× / 1.89× | 4.39× / 2.24× |
| 1M / 1K / 1 / 4 | 0.84× / 0.84× | 0.82× / 0.97× | 1.21× / 1.48× | 4.66× / 3.87× | 7.67× / 1.64× |

## Allocations

The GC profiler metric is `gc.alloc.rate.norm`, converted from bytes to MiB per completed `groupBy` call. It measures
allocation churn, not peak live heap, but unlike allocation rate it is not inflated merely because a variant is faster.

| Scenario | Baseline | Step 1 | Step 2 | Step 3 | Step 4 | Step 5 |
|---|---:|---:|---:|---:|---:|---:|
| 10K / 10 / 1 / 4 | 1.821 | 1.280 | 1.280 | 0.822 | 0.621 | 0.623 |
| 100K / 10 / 1 / 4 | 16.276 | 11.241 | 11.242 | 6.664 | 5.523 | 5.524 |
| 100K / 1K / 1 / 4 | 32.798 | 27.765 | 27.765 | 23.187 | 14.175 | 14.177 |
| 100K / 1K / 3 / 4 | 40.789 | 35.755 | 35.755 | 35.755 | 23.763 | 23.766 |
| 100K / 1K / 1 / 32 | 137.165 | 132.132 | 132.132 | 127.554 | 78.811 | 73.786 |
| 1M / 1K / 1 / 4 | 181.865 | 129.810 | 129.810 | 84.034 | 61.501 | 61.502 |

Allocation reduction factors use the same `versus baseline / versus previous step` format. Values above `1.00×`
mean fewer allocated bytes.

| Scenario | Step 1 | Step 2 | Step 3 | Step 4 | Step 5 |
|---|---:|---:|---:|---:|---:|
| 10K / 10 / 1 / 4 | 1.42× / 1.42× | 1.42× / 1.00× | 2.21× / 1.56× | 2.93× / 1.32× | 2.92× / 1.00× |
| 100K / 10 / 1 / 4 | 1.45× / 1.45× | 1.45× / 1.00× | 2.44× / 1.69× | 2.95× / 1.21× | 2.95× / 1.00× |
| 100K / 1K / 1 / 4 | 1.18× / 1.18× | 1.18× / 1.00× | 1.41× / 1.20× | 2.31× / 1.64× | 2.31× / 1.00× |
| 100K / 1K / 3 / 4 | 1.14× / 1.14× | 1.14× / 1.00× | 1.14× / 1.00× | 1.72× / 1.50× | 1.72× / 1.00× |
| 100K / 1K / 1 / 32 | 1.04× / 1.04× | 1.04× / 1.00× | 1.08× / 1.04× | 1.74× / 1.62× | 1.86× / 1.07× |
| 1M / 1K / 1 / 4 | 1.40× / 1.40× | 1.40× / 1.00× | 2.16× / 1.54× | 2.96× / 1.37× | 2.96× / 1.00× |

### Constrained-heap smoke test

The 1M / 1K / 1 / 4 scenario was also run once per implementation in single-shot mode with `-Xmx192m`, no warm-up,
and one fork. This is a diagnostic for memory headroom, not a stable performance measurement.

| Baseline | Step 1 | Step 2 | Step 3 | Step 4 | Step 5 |
|---:|---:|---:|---:|---:|---:|
| **OOM** | Pass | Pass | Pass | Pass | Pass |

The baseline failed with `OutOfMemoryError: Java heap space` while creating per-row key data in `groupByImpl`.
Step 1 completed under the same heap limit, so its lower allocation pressure is valuable even on scenarios where its
average execution time regresses. The precise failure threshold depends on JVM, heap layout, input schema, and GC.

## Reading the result

- Step 1 is not a general latency win: it regresses most larger scenarios by roughly 4–19%. It does reduce allocations
  by up to 1.45× and is the first step that passes the constrained-heap case where baseline fails with OOM.
- Step 2 is neutral for allocations and mostly neutral for time. Direct key-column access primarily enables the next
  specialization rather than providing a measurable standalone benefit.
- Step 3 is a clear single-key optimization. It has no meaningful expected benefit for the three-key scenario.
- Step 4 is the first broad structural win: column-first group construction improves every representative workload,
  with the largest effect at high row or group counts; it also removes another large block of temporary allocation.
- Step 5 improves every measured scenario on this 24-thread machine. Its benefit is hardware-dependent because it
  uses the common fork-join pool; allocations are essentially unchanged except for the wide-frame measurement.
- The cumulative final result is 2.44×–7.67× faster than baseline across these scenarios.
- The cumulative final result allocates 1.72×–2.96× fewer bytes per operation and survives the demonstrated
  constrained-heap workload.

The measurements support keeping steps 1 and 3–5 conceptually: step 1 for memory pressure and as enabling structure,
steps 3–5 for both speed and memory. Step 2 remains an enabling code change without an independent measured win.

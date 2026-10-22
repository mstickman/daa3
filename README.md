# DAA Assignment 3 - Graphs: DSU, MST, SCC and Shortest Paths

## Structure

```
src/main/java/graph        Graph (adjacency list, directed + undirected), Edge
src/main/java/algorithms   DSU, Kruskal, Prim, Kosaraju, Kahn, DagShortestPath, MstResult
src/main/java/metrics      Metrics (dsuOps, edgeScans, comparisons, maxDepth)
src/main/java/benchmark    GraphGenerator (exact generator from the task), Benchmark (main)
src/test/java              JUnit 5 tests
data/                      disconnected.txt, cyclic.txt, dag.txt  (small input graphs)
results/results.csv        50 rows: 5 algorithms x 10 graphs
results/plots/             3 PNG charts
results/ratios.md          counter / formula tables and slopes (made by scripts/make_plots.py)
scripts/make_plots.py      reads results.csv, draws the plots (python3 + matplotlib + numpy)
REPORT.md                  report
```

## Input format

First line `n m directed|undirected`, then `m` lines `u v w`. Vertices are `0 .. n-1`.

## How to build and run

```
mvn clean package            # compiles and runs all JUnit tests
mvn test                     # only the tests

# benchmark (needs about 2 GB of heap for n = 100 000, m = 2 000 000)
mvn compile
java -Xmx3g -cp target/classes benchmark.Benchmark
# or:  MAVEN_OPTS="-Xmx3g" mvn compile exec:java

# plots + ratio tables
pip install matplotlib numpy
python3 scripts/make_plots.py
```

The benchmark writes `results/results.csv`. It checks itself and stops with an exception if something is wrong:
Kruskal weight must be equal to Prim weight, the topological order must be valid, Kosaraju must scan 2m edges,
Kahn and the DAG pass must scan m edges, and the 10 reference values from the assignment must match.
Same seed (`new Random(42)`) = same counters on every machine. Only `time_ms` changes.

## Reference values (printed by the benchmark)

| n / m | MST weight | SCC count | Sum of DAG distances |
|---|---|---|---|
| 1 000 / 4 000 | 147970 | 27 | 396274 |
| 1 000 / 20 000 | 30363 | 1 | 252411 |
| 3 162 / 12 648 | 468911 | 137 | 1271911 |
| 3 162 / 63 240 | 97589 | 1 | 723157 |
| 10 000 / 40 000 | 1486139 | 373 | 3941828 |
| 10 000 / 200 000 | 309550 | 1 | 2381342 |
| 31 623 / 126 492 | 4715515 | 1188 | 12551891 |
| 31 623 / 632 460 | 968517 | 1 | 7376612 |
| 100 000 / 400 000 | 15012094 | 3923 | 40096539 |
| 100 000 / 2 000 000 | 3056681 | 1 | 23512087 |

## Git

Branches: `main`, `feature/dsu`, `feature/mst`, `feature/scc`, `feature/paths`, `feature/metrics`. Release tag `v1.0` on `main`.

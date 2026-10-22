package benchmark;

import algorithms.DagShortestPath;
import algorithms.Kahn;
import algorithms.Kosaraju;
import algorithms.Kruskal;
import algorithms.Prim;
import graph.Graph;
import metrics.Metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Benchmark {

    static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    static final int[] DENSITIES = {4, 20};
    static final int RUNS = 5;

    static final long[] REF_MST = {147970, 30363, 468911, 97589, 1486139, 309550,
            4715515, 968517, 15012094, 3056681};
    static final int[] REF_SCC = {27, 1, 137, 1, 373, 1, 1188, 1, 3923, 1};
    static final long[] REF_DAG = {396274, 252411, 1271911, 723157, 3941828, 2381342,
            12551891, 7376612, 40096539, 23512087};

    interface Algo {
        long run(Metrics m);
    }

    static class Row {
        String algorithm, graph;
        int n, m;
        double timeMs;
        Metrics metrics;
        long result;
    }

    public static void main(String[] args) throws IOException {
        String outPath = args.length > 0 ? args[0] : "results/results.csv";
        List<Row> rows = new ArrayList<>();

        System.out.println("n / m | Kruskal | Prim | SCC | DAG sum | reference");
        for (int si = 0; si < SIZES.length; si++) {
            for (int di = 0; di < DENSITIES.length; di++) {
                int n = SIZES[si];
                int m = DENSITIES[di] * n;
                int idx = si * 2 + di;
                String kind = (DENSITIES[di] == 4) ? "sparse" : "dense";

                final Graph ug = GraphGenerator.undirected(n, m);
                String name = "undirected_" + kind + "_n" + n;
                Row kr = measure("Kruskal", name, ug, mt -> Kruskal.run(ug, mt).totalWeight);
                Row pr = measure("Prim", name, ug, mt -> Prim.run(ug, mt).totalWeight);
                if (kr.result != pr.result) {
                    throw new IllegalStateException("MST MISMATCH on " + name + ": Kruskal=" + kr.result
                            + " Prim=" + pr.result);
                }
                check(kr.result == REF_MST[idx], "MST reference", name, kr.result, REF_MST[idx]);
                rows.add(kr);
                rows.add(pr);

                final Graph dg = GraphGenerator.directed(n, m);
                name = "directed_" + kind + "_n" + n;
                Row sr = measure("Kosaraju", name, dg, mt -> Kosaraju.run(dg, mt).count);
                check(sr.result == REF_SCC[idx], "SCC reference", name, sr.result, REF_SCC[idx]);
                check(sr.metrics.edgeScans == 2L * m, "Kosaraju scans 2m", name, sr.metrics.edgeScans, 2L * m);
                rows.add(sr);

                final Graph ag = GraphGenerator.dag(n, m);
                name = "dag_" + kind + "_n" + n;
                Kahn.Result topo = Kahn.run(ag, new Metrics());
                check(!topo.hasCycle, "DAG has no cycle", name, 1, 1);
                check(Kahn.isValidOrder(ag, topo.order, topo.count), "topological order valid", name, 1, 1);
                int[] order = topo.order;

                Row kh = measure("Kahn", name, ag, mt -> Kahn.run(ag, mt).count);
                Row dr = measure("DagShortestPath", name, ag,
                        mt -> DagShortestPath.run(ag, order, mt).sum);
                check(kh.result == n, "Kahn order size", name, kh.result, n);
                check(dr.result == REF_DAG[idx], "DAG distances reference", name, dr.result, REF_DAG[idx]);
                check(kh.metrics.edgeScans == m, "Kahn scans m", name, kh.metrics.edgeScans, m);
                check(dr.metrics.edgeScans == m, "DAG scans m", name, dr.metrics.edgeScans, m);
                rows.add(kh);
                rows.add(dr);

                System.out.println(n + " / " + m + " | " + kr.result + " | " + pr.result + " | "
                        + sr.result + " | " + dr.result + " | all OK");
            }
        }

        writeCsv(outPath, rows);
        System.out.println("Written " + rows.size() + " rows to " + outPath);
    }

    static Row measure(String algorithm, String graphName, Graph g, Algo algo) {
        double[] times = new double[RUNS];
        Row row = new Row();
        for (int r = 0; r < RUNS; r++) {
            Metrics mt = new Metrics();
            long t0 = System.nanoTime();
            long result = algo.run(mt);
            long t1 = System.nanoTime();
            times[r] = (t1 - t0) / 1e6;
            row.metrics = mt;
            row.result = result;
        }
        Arrays.sort(times);
        row.algorithm = algorithm;
        row.graph = graphName;
        row.n = g.n;
        row.m = g.edgeCount();
        row.timeMs = times[RUNS / 2];
        return row;
    }

    static void check(boolean ok, String what, String graph, long got, long expected) {
        if (!ok) {
            throw new IllegalStateException("CHECK FAILED: " + what + " on " + graph
                    + " (got " + got + ", expected " + expected + ")");
        }
    }

    static void writeCsv(String path, List<Row> rows) throws IOException {
        File f = new File(path);
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            pw.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            for (Row r : rows) {
                pw.println(r.algorithm + "," + r.graph + "," + r.n + "," + r.m + ","
                        + String.format(java.util.Locale.US, "%.3f", r.timeMs) + ","
                        + r.metrics.dsuOps + "," + r.metrics.edgeScans + ","
                        + r.metrics.comparisons + "," + r.metrics.maxDepth + "," + r.result);
            }
        }
    }
}

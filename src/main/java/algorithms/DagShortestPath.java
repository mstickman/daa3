package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DagShortestPath {

    public static final long INF = Long.MAX_VALUE / 4;

    public static class Result {
        public long[] dist;
        public int[] parent;
        public long sum;
    }

    public static Result run(Graph g, int[] order, Metrics m) {
        int n = g.n;
        if (order.length < n) {
            throw new IllegalArgumentException("order is not complete (graph has a cycle?)");
        }

        int[] indeg = new int[n];
        for (Edge e : g.edges) {
            indeg[e.to]++;
        }

        Result res = new Result();
        res.dist = new long[n];
        res.parent = new int[n];
        Arrays.fill(res.dist, INF);
        Arrays.fill(res.parent, -1);
        for (int v = 0; v < n; v++) {
            if (indeg[v] == 0) {
                res.dist[v] = 0;
            }
        }
        for (int i = 0; i < n; i++) {
            int v = order[i];
            if (res.dist[v] == INF) {
                continue;
            }
            for (Edge e : g.adj.get(v)) {
                m.edgeScans++;
                long nd = res.dist[v] + e.weight;
                m.comparisons++;
                if (nd < res.dist[e.to]) {
                    res.dist[e.to] = nd;
                    res.parent[e.to] = v;
                }
            }
        }

        for (int v = 0; v < n; v++) {
            if (res.dist[v] != INF) {
                res.sum += res.dist[v];
            }
        }
        return res;
    }

    public static List<Integer> getPath(int[] parent, int v) {
        List<Integer> path = new ArrayList<>();
        while (v != -1) {
            path.add(v);
            v = parent[v];
        }
        Collections.reverse(path);
        return path;
    }
}

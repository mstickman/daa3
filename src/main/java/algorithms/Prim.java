package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.PriorityQueue;

public class Prim {

    public static MstResult run(Graph g, Metrics m) {
        MstResult res = new MstResult();
        boolean[] inTree = new boolean[g.n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            m.comparisons++;
            return Integer.compare(a[0], b[0]);
        });

        for (int start = 0; start < g.n; start++) {
            if (inTree[start]) {
                continue;
            }
            res.components++;
            inTree[start] = true;
            scan(g, start, inTree, pq, m);

            while (!pq.isEmpty()) {
                int[] top = pq.poll();
                int v = top[1];
                if (inTree[v]) {
                    continue;
                }
                inTree[v] = true;
                res.totalWeight += top[0];
                res.edges.add(new Edge(top[2], v, top[0]));
                scan(g, v, inTree, pq, m);
            }
        }
        return res;
    }

    private static void scan(Graph g, int v, boolean[] inTree, PriorityQueue<int[]> pq, Metrics m) {
        for (Edge e : g.adj.get(v)) {
            m.edgeScans++;
            if (!inTree[e.to]) {
                pq.add(new int[]{e.weight, e.to, v});
            }
        }
    }
}

package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

public class Kahn {

    public static class Result {
        public int[] order;
        public int count;
        public boolean hasCycle;
    }

    public static Result run(Graph g, Metrics m) {
        int n = g.n;
        int[] indeg = new int[n];
        for (Edge e : g.edges) {
            indeg[e.to]++;
        }

        int[] queue = new int[n];
        int head = 0;
        int tail = 0;
        for (int v = 0; v < n; v++) {
            if (indeg[v] == 0) {
                queue[tail++] = v;
            }
        }

        while (head < tail) {
            int v = queue[head++];
            for (Edge e : g.adj.get(v)) {
                m.edgeScans++;
                indeg[e.to]--;
                if (indeg[e.to] == 0) {
                    queue[tail++] = e.to;
                }
            }
        }

        Result res = new Result();
        res.order = queue;
        res.count = head;
        res.hasCycle = head < n;
        return res;
    }


    public static boolean isValidOrder(Graph g, int[] order, int count) {
        if (count != g.n) {
            return false;
        }
        int[] pos = new int[g.n];
        for (int i = 0; i < count; i++) {
            pos[order[i]] = i;
        }
        for (Edge e : g.edges) {
            if (pos[e.from] >= pos[e.to]) {
                return false;
            }
        }
        return true;
    }
}

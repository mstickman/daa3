package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.Arrays;
import java.util.List;

public class Kosaraju {

    public static class Result {
        public int[] comp;
        public int count;
    }

    public static Result run(Graph g, Metrics m) {
        int n = g.n;
        int[] stack = new int[n];

        boolean[] visited = new boolean[n];
        int[] next = new int[n];
        int[] finishOrder = new int[n];
        int finished = 0;

        for (int s = 0; s < n; s++) {
            if (visited[s]) {
                continue;
            }
            int top = 0;
            stack[top++] = s;
            visited[s] = true;
            if (top > m.maxDepth) {
                m.maxDepth = top;
            }
            while (top > 0) {
                int v = stack[top - 1];
                List<Edge> list = g.adj.get(v);
                if (next[v] < list.size()) {
                    Edge e = list.get(next[v]);
                    next[v]++;
                    m.edgeScans++;
                    if (!visited[e.to]) {
                        visited[e.to] = true;
                        stack[top++] = e.to;
                        if (top > m.maxDepth) {
                            m.maxDepth = top;
                        }
                    }
                } else {
                    top--;
                    finishOrder[finished++] = v;
                }
            }
        }

        Graph rg = g.reversed();
        Result res = new Result();
        res.comp = new int[n];
        Arrays.fill(res.comp, -1);
        Arrays.fill(next, 0);

        for (int i = n - 1; i >= 0; i--) {
            int s = finishOrder[i];
            if (res.comp[s] != -1) {
                continue;
            }

            int top = 0;
            stack[top++] = s;
            res.comp[s] = res.count;
            if (top > m.maxDepth) {
                m.maxDepth = top;
            }
            while (top > 0) {
                int v = stack[top - 1];
                List<Edge> list = rg.adj.get(v);
                if (next[v] < list.size()) {
                    Edge e = list.get(next[v]);
                    next[v]++;
                    m.edgeScans++;
                    if (res.comp[e.to] == -1) {
                        res.comp[e.to] = res.count;
                        stack[top++] = e.to;
                        if (top > m.maxDepth) {
                            m.maxDepth = top;
                        }
                    }
                } else {
                    top--;
                }
            }
            res.count++;
        }
        return res;
    }
}

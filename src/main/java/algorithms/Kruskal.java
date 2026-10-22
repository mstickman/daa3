package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public static MstResult run(Graph g, Metrics m) {
        MstResult res = new MstResult();

        List<Edge> sorted = new ArrayList<>(g.edges);
        sorted.sort((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.weight, b.weight);
        });

        DSU dsu = new DSU(g.n);
        for (Edge e : sorted) {
            if (res.edges.size() == g.n - 1) {
                break;
            }
            m.edgeScans++;
            if (dsu.find(e.from) != dsu.find(e.to)) {
                dsu.union(e.from, e.to);
                res.edges.add(e);
                res.totalWeight += e.weight;
            }
        }

        res.components = dsu.components();
        m.dsuOps += dsu.operations();
        return res;
    }
}

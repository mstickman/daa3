import algorithms.DagShortestPath;
import algorithms.Kahn;
import graph.Edge;
import graph.Graph;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DagPathsTest {

    // random DAG with weights from -10 to 20 (negative weights included)
    static Graph randomDag(int n, int m, Random rnd) {
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = rank[i];
            rank[i] = rank[j];
            rank[j] = tmp;
        }
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b) {
                continue;
            }
            if (rank[a] > rank[b]) {
                int tmp = a;
                a = b;
                b = tmp;
            }
            g.addEdge(a, b, rnd.nextInt(31) - 10);
        }
        return g;
    }

    // Bellman-Ford with the same sources (in-degree 0 start with 0)
    static long[] bellmanFord(Graph g) {
        int n = g.n;
        int[] indeg = new int[n];
        for (Edge e : g.edges) {
            indeg[e.to]++;
        }
        long[] dist = new long[n];
        Arrays.fill(dist, DagShortestPath.INF);
        for (int v = 0; v < n; v++) {
            if (indeg[v] == 0) {
                dist[v] = 0;
            }
        }
        for (int round = 0; round < n; round++) {
            boolean changed = false;
            for (Edge e : g.edges) {
                if (dist[e.from] != DagShortestPath.INF && dist[e.from] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[e.from] + e.weight;
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
        return dist;
    }

    @Test
    void agreesWithBellmanFordOnRandomDags() {
        Random rnd = new Random(11);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(40);
            int m = rnd.nextInt(4 * n);
            Graph g = randomDag(n, m, rnd);
            Kahn.Result topo = Kahn.run(g, new Metrics());
            assertTrue(!topo.hasCycle);

            Metrics met = new Metrics();
            DagShortestPath.Result res = DagShortestPath.run(g, topo.order, met);
            long[] bf = bellmanFord(g);
            for (int v = 0; v < n; v++) {
                assertEquals(bf[v], res.dist[v]);
            }
            assertEquals(g.edgeCount(), met.edgeScans);   // one pass, every edge once
        }
    }

    // data/dag.txt has a negative edge (2 -> 1 with weight -2)
    @Test
    void fileExampleWithNegativeWeightAndPath() throws Exception {
        Graph g = Graph.fromFile("data/dag.txt");
        Kahn.Result topo = Kahn.run(g, new Metrics());
        DagShortestPath.Result res = DagShortestPath.run(g, topo.order, new Metrics());

        long[] expected = {0, 1, 3, 7, 2, 0};
        for (int v = 0; v < g.n; v++) {
            assertEquals(expected[v], res.dist[v]);
        }
        assertEquals(13, res.sum);

        List<Integer> path = DagShortestPath.getPath(res.parent, 3);
        assertEquals(Arrays.asList(0, 2, 1, 3), path);   // 0 -> 2 -> 1 -> 3 = 3 - 2 + 6 = 7
        assertEquals(Arrays.asList(5, 4), DagShortestPath.getPath(res.parent, 4));
    }
}

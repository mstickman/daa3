import algorithms.Kosaraju;
import benchmark.GraphGenerator;
import graph.Edge;
import graph.Graph;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SccTest {

    // BFS from every vertex: reach[u][v] = true if v can be reached from u
    static boolean[][] reachFromEveryVertex(Graph g) {
        int n = g.n;
        boolean[][] reach = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> q = new ArrayDeque<>();
            reach[s][s] = true;
            q.add(s);
            while (!q.isEmpty()) {
                int v = q.poll();
                List<Edge> list = g.adj.get(v);
                for (Edge e : list) {
                    if (!reach[s][e.to]) {
                        reach[s][e.to] = true;
                        q.add(e.to);
                    }
                }
            }
        }
        return reach;
    }

    // brute force: u and v are in the same SCC if u reaches v and v reaches u
    // (BFS in the graph and BFS in the reversed graph)
    @Test
    void agreesWithBruteForceOnRandomGraphs() {
        Random rnd = new Random(3);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(199);            // n <= 200
            int m = rnd.nextInt(3 * n);
            Graph g = GraphGenerator.directed(n, m, rnd);

            boolean[][] forward = reachFromEveryVertex(g);
            boolean[][] backward = reachFromEveryVertex(g.reversed());
            Kosaraju.Result res = Kosaraju.run(g, new Metrics());

            Set<Integer> usedComponents = new HashSet<>();
            for (int u = 0; u < n; u++) {
                usedComponents.add(res.comp[u]);
                for (int v = 0; v < n; v++) {
                    boolean sameBrute = forward[u][v] && backward[u][v];
                    boolean sameKosaraju = res.comp[u] == res.comp[v];
                    assertTrue(sameBrute == sameKosaraju, "SCC differs from brute force");
                }
            }
            assertEquals(usedComponents.size(), res.count);
        }
    }

    @Test
    void cyclicFileHasThreeComponents() throws Exception {
        Graph g = Graph.fromFile("data/cyclic.txt");
        Metrics m = new Metrics();
        Kosaraju.Result res = Kosaraju.run(g, m);
        assertEquals(3, res.count);
        assertTrue(res.comp[0] == res.comp[1] && res.comp[1] == res.comp[2]);
        assertTrue(res.comp[3] == res.comp[4] && res.comp[4] == res.comp[5]);
        assertTrue(res.comp[0] != res.comp[3] && res.comp[3] != res.comp[6]);
        assertEquals(2L * g.edgeCount(), m.edgeScans);   // two passes, m edges each
    }

    @Test
    void emptyGraphAndSelfLoop() {
        assertEquals(4, Kosaraju.run(new Graph(4, true), new Metrics()).count);
        Graph g = new Graph(2, true);
        g.addEdge(0, 0, 1);     // self-loop
        g.addEdge(0, 1, 1);
        g.addEdge(0, 1, 2);     // parallel edge
        assertEquals(2, Kosaraju.run(g, new Metrics()).count);
    }
}

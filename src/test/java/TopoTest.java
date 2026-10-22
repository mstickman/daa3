import algorithms.Kahn;
import benchmark.GraphGenerator;
import graph.Graph;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopoTest {

    @Test
    void validOrderOnRandomDags() {
        Random rnd = new Random(5);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(100);
            int m = (n == 1) ? 0 : rnd.nextInt(4 * n);
            Graph g = GraphGenerator.dag(n, m, rnd);
            Metrics met = new Metrics();
            Kahn.Result r = Kahn.run(g, met);
            assertFalse(r.hasCycle);
            assertEquals(n, r.count);
            assertTrue(Kahn.isValidOrder(g, r.order, r.count), "pos[u] < pos[v] is broken");
            assertEquals(m, met.edgeScans);    // Kahn scans every edge exactly once
        }
    }

    @Test
    void cycleIsDetectedInFile() throws Exception {
        Graph g = Graph.fromFile("data/cyclic.txt");
        Kahn.Result r = Kahn.run(g, new Metrics());
        assertTrue(r.hasCycle);
        assertTrue(r.count < g.n);
        assertFalse(Kahn.isValidOrder(g, r.order, r.count));
    }

    @Test
    void cycleIsDetectedOnSmallGraphs() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 1, 1);          // cycle 1 -> 2 -> 1
        Kahn.Result r = Kahn.run(g, new Metrics());
        assertTrue(r.hasCycle);
        assertEquals(1, r.count);    // only vertex 0 could leave the queue

        Graph loop = new Graph(2, true);
        loop.addEdge(1, 1, 5);       // self-loop is a cycle too
        assertTrue(Kahn.run(loop, new Metrics()).hasCycle);
    }

    @Test
    void emptyGraphWorks() {
        Kahn.Result r = Kahn.run(new Graph(3, true), new Metrics());
        assertFalse(r.hasCycle);
        assertEquals(3, r.count);
    }
}

import algorithms.Kosaraju;
import graph.Graph;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackSafetyTest {

    // a path 0 -> 1 -> ... -> 99999: a recursive DFS would be 100 000 calls deep
    @Test
    void sccOnPathOf100000VerticesDoesNotOverflow() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) {
            g.addEdge(i, i + 1, 1);
        }
        Metrics m = new Metrics();
        Kosaraju.Result res = Kosaraju.run(g, m);
        assertEquals(n, res.count);        // every vertex is its own component
        assertEquals(n, m.maxDepth);       // our explicit stack really got that deep
    }

    // one big cycle: one component, also very deep
    @Test
    void sccOnBigCycle() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i < n; i++) {
            g.addEdge(i, (i + 1) % n, 1);
        }
        assertEquals(1, Kosaraju.run(g, new Metrics()).count);
    }
}

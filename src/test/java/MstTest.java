import algorithms.Kruskal;
import algorithms.MstResult;
import algorithms.Prim;
import benchmark.GraphGenerator;
import graph.Graph;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MstTest {

    // 100 random connected graphs: same total weight and exactly n-1 edges
    @Test
    void kruskalAndPrimGiveTheSameWeight() {
        Random rnd = new Random(7);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(60);
            int m = (n - 1) + rnd.nextInt(3 * n);
            Graph g = GraphGenerator.undirected(n, m, rnd);

            MstResult k = Kruskal.run(g, new Metrics());
            MstResult p = Prim.run(g, new Metrics());

            assertEquals(k.totalWeight, p.totalWeight);
            assertEquals(n - 1, k.edges.size());
            assertEquals(n - 1, p.edges.size());
            assertEquals(1, k.components);
            assertEquals(1, p.components);
        }
    }

    // data/disconnected.txt: 4 components, self-loop, parallel edge, isolated vertices
    @Test
    void disconnectedGraphGivesForest() throws Exception {
        Graph g = Graph.fromFile("data/disconnected.txt");
        MstResult k = Kruskal.run(g, new Metrics());
        MstResult p = Prim.run(g, new Metrics());

        assertEquals(10, k.totalWeight);
        assertEquals(10, p.totalWeight);
        assertEquals(4, k.components);
        assertEquals(4, p.components);
        assertEquals(4, k.edges.size());   // n - components = 8 - 4
        assertEquals(4, p.edges.size());
    }

    @Test
    void graphWithoutEdgesAndSingleVertex() {
        Graph g = new Graph(5, false);
        assertEquals(5, Kruskal.run(g, new Metrics()).components);
        assertEquals(5, Prim.run(g, new Metrics()).components);
        Graph one = new Graph(1, false);
        assertEquals(0, Kruskal.run(one, new Metrics()).totalWeight);
        assertEquals(1, Prim.run(one, new Metrics()).components);
    }
}

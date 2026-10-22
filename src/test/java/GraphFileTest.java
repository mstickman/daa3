import graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphFileTest {

    @Test
    void readsUndirectedFileWithSelfLoopAndParallelEdges() throws Exception {
        Graph g = Graph.fromFile("data/disconnected.txt");
        assertEquals(8, g.n);
        assertEquals(8, g.edgeCount());
        assertFalse(g.directed);
        assertEquals(0, g.adj.get(6).size());   // isolated vertex
        assertEquals(2, g.adj.get(3).size());   // 3-4 and 3-5
        assertEquals(3, g.adj.get(2).size());   // 1-2, 0-2 and the self-loop (stored once)
        assertEquals(3, g.adj.get(0).size());   // 0-1, 0-2 and the parallel edge 0-1
    }

    @Test
    void readsDirectedFiles() throws Exception {
        Graph c = Graph.fromFile("data/cyclic.txt");
        assertTrue(c.directed);
        assertEquals(7, c.n);
        assertEquals(8, c.edgeCount());
        Graph d = Graph.fromFile("data/dag.txt");
        assertEquals(6, d.n);
        assertEquals(7, d.edgeCount());
    }

    @Test
    void reversedGraphTurnsEdgesAround() throws Exception {
        Graph g = Graph.fromFile("data/dag.txt");
        Graph r = g.reversed();
        assertEquals(g.edgeCount(), r.edgeCount());
        assertEquals(2, r.adj.get(1).size());   // in g vertex 1 has 2 incoming edges: 0->1, 2->1
    }
}

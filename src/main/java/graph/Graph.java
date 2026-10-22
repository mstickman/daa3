package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final int n;
    public final boolean directed;
    public final List<List<Edge>> adj;
    public final List<Edge> edges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
    }

    public int edgeCount() {
        return edges.size();
    }

    public void addEdge(int u, int v, int w) {
        if (u < 0 || u >= n || v < 0 || v >= n) {
            throw new IllegalArgumentException("bad vertex in edge " + u + " " + v);
        }
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj.get(u).add(e);
        if (!directed && u != v) {
            adj.get(v).add(new Edge(v, u, w));
        }
    }

    public Graph reversed() {
        Graph r = new Graph(n, true);
        for (Edge e : edges) {
            r.addEdge(e.to, e.from, e.weight);
        }
        return r;
    }

    public static Graph fromFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] first = br.readLine().trim().split("\\s+");
            int n = Integer.parseInt(first[0]);
            int m = Integer.parseInt(first[1]);
            boolean directed = first[2].equals("directed");
            Graph g = new Graph(n, directed);
            int read = 0;
            String line;
            while (read < m && (line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split("\\s+");
                g.addEdge(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                read++;
            }
            return g;
        }
    }
}

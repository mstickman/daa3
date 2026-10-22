package benchmark;

import graph.Graph;

import java.util.Random;

public class GraphGenerator {

    private static int[] shuffled(int n, Random rnd) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }
        return perm;
    }

    private static int weight(Random rnd) {
        return 1 + rnd.nextInt(1000);
    }

    private static int[] twoDifferent(int n, Random rnd) {
        int a = rnd.nextInt(n);
        int b = rnd.nextInt(n);
        while (b == a) {
            b = rnd.nextInt(n);
        }
        return new int[]{a, b};
    }

    public static Graph undirected(int n, int m, Random rnd) {
        Graph g = new Graph(n, false);
        int[] perm = shuffled(n, rnd);
        for (int i = 1; i < n; i++) {
            int u = perm[rnd.nextInt(i)];
            int v = perm[i];
            g.addEdge(u, v, weight(rnd));
        }
        for (int k = n - 1; k < m; k++) {
            int[] ab = twoDifferent(n, rnd);
            g.addEdge(ab[0], ab[1], weight(rnd));
        }
        return g;
    }

    public static Graph directed(int n, int m, Random rnd) {
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            int[] ab = twoDifferent(n, rnd);
            g.addEdge(ab[0], ab[1], weight(rnd));
        }
        return g;
    }

    public static Graph dag(int n, int m, Random rnd) {
        Graph g = new Graph(n, true);
        int[] rank = shuffled(n, rnd);
        for (int k = 0; k < m; k++) {
            int[] ab = twoDifferent(n, rnd);
            int a = ab[0];
            int b = ab[1];
            if (rank[a] > rank[b]) {
                int tmp = a;
                a = b;
                b = tmp;
            }
            g.addEdge(a, b, weight(rnd));
        }
        return g;
    }

    public static Graph undirected(int n, int m) {
        return undirected(n, m, new Random(42));
    }

    public static Graph directed(int n, int m) {
        return directed(n, m, new Random(42));
    }

    public static Graph dag(int n, int m) {
        return dag(n, m, new Random(42));
    }
}

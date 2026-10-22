package algorithms;

public class DSU {
    private int[] parent;
    private int[] rank;
    private int count;

    public long findCalls;
    public long unionCalls;

    public DSU(int n) {
        makeSet(n);
    }

    public void makeSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        count = n;
        findCalls = 0;
        unionCalls = 0;
    }

    public int find(int x) {
        findCalls++;

        int root = x;
        while (parent[root] != root) {
            root = parent[root];
        }

        while (parent[x] != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    public boolean union(int x, int y) {
        int rx = find(x);
        int ry = find(y);
        if (rx == ry) {
            return false;
        }

        if (rank[rx] < rank[ry]) {
            int tmp = rx;
            rx = ry;
            ry = tmp;
        }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) {
            rank[rx]++;
        }
        count--;
        unionCalls++;
        return true;
    }

    public int components() {
        return count;
    }

    public long operations() {
        return findCalls + unionCalls;
    }
}

import algorithms.DSU;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DsuTest {

    // after random unions DSU must agree with a very simple reference (array of labels)
    @Test
    void randomUnionsAgreeWithReference() {
        Random rnd = new Random(1);
        for (int round = 0; round < 100; round++) {
            int n = 1 + rnd.nextInt(60);
            DSU dsu = new DSU(n);
            int[] label = new int[n];
            for (int i = 0; i < n; i++) {
                label[i] = i;
            }

            int ops = rnd.nextInt(100);
            for (int k = 0; k < ops; k++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                dsu.union(a, b);
                // reference: relabel the whole set of b with the label of a
                int la = label[a];
                int lb = label[b];
                for (int i = 0; i < n; i++) {
                    if (label[i] == lb) {
                        label[i] = la;
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    boolean sameRef = label[i] == label[j];
                    boolean sameDsu = dsu.find(i) == dsu.find(j);
                    assertTrue(sameRef == sameDsu, "find disagrees with reference");
                }
            }

            Set<Integer> distinct = new HashSet<>();
            for (int i = 0; i < n; i++) {
                distinct.add(label[i]);
            }
            assertEquals(distinct.size(), dsu.components());
        }
    }

    @Test
    void unionReturnsFalseForSameSetAndCountersWork() {
        DSU dsu = new DSU(4);
        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));
        assertFalse(dsu.union(0, 2));      // already in the same set
        assertEquals(2, dsu.components());
        assertEquals(2, dsu.unionCalls);   // only successful unions are counted
        assertTrue(dsu.findCalls >= 6);    // every union calls find twice
    }

    @Test
    void makeSetResetsEverything() {
        DSU dsu = new DSU(5);
        dsu.union(0, 1);
        dsu.makeSet(7);
        assertEquals(7, dsu.components());
        assertEquals(0, dsu.operations());
    }
}

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

//TODO: make a test that randomly inserts and deletes nodes, and checks invariants.

public class RBTreeTest {

    TreeMap<Integer, String> map0;
    TreeMap<Integer, String> map1;

    RBTree rb0;
    RBTree rb1;

    class MapPair {
        public Map<Integer, String> map;
        public RBTree rb;

        public MapPair(Map<Integer, String> map, RBTree rb) {
            this.map = map;
            this.rb = rb;
        }
    }

    List<MapPair> maps;

    @Before
    public void setUp() throws Throwable {
        map0 = new TreeMap<>();
        map1 = new TreeMap<>();

        for (int i = 1; i <= 9; i++) {
            String s = "" + i;
            map1.put(i, s);
        }

        rb0 = new RBTree(map0);
        rb1 = new RBTree(map1);
        rb0.checkTreeInvariants();
        rb1.checkTreeInvariants();

        maps = new ArrayList<>();
        maps.add(new MapPair(map0, rb0));
        maps.add(new MapPair(map1, rb1));
    }

    @After
    public void tearDown() throws Exception {

    }

    //TODO XXX
//    @Test
//    public void testRotate() throws Exception {
//        RBTree.RBNode node = new RBTree.RBNode(null, null, null, RBTree.Color.Black, 5, "5");
//        RBTree t = new RBTree();
//        t.insert(50, "50");
//        t.insert(100, "100");
//        t.insert(20, "20");
//        t.insert(10, "10");
//        t.insert(5, "5");
//        //t.printTree();
//        t.rootDummy.printTree();
//
//        System.out.println("***************************");
//
//        t.root().rotateRight();
//        t.rootDummy.printTree();
//
//        System.out.println("***************************");
//
//        t.root().rotateLeft();
//        t.rootDummy.printTree();
//
//        System.out.println("***************************");
//        System.out.println(t.root().left.left);
//        System.out.println("***************************");
//
//        t.root().left.left.rotateRight();
//
//        t.root().printTree();
//
//        System.out.println("***************************");
//        System.out.println(t.root().left.left);
//        System.out.println("***************************");
//
//        t.root().left.left.rotateLeft();
//
//        t.root().printTree();
//    }

    @Test
    public void testEmpty() throws Exception {
        for (MapPair pair : maps) {
            assertEquals(pair.map.isEmpty(), pair.rb.empty());
        }
    }

    @Test
    public void testSearch() throws Exception {
        assertEquals(map1.get((int) 'c'), rb1.search((int) 'c'));
        assertEquals(map1.get((int) 'z'), rb1.search((int) 'z'));
    }

    @Test
    public void testInsert() throws Throwable {
        for (MapPair pair : maps) {
            for (int i = 11; i <= 19; i++) {
                String s = "" + i;
                pair.map.put(i, s);
                pair.rb.insert(i, s);
                pair.rb.checkTreeInvariants();
                assertEquals(pair.map, pair.rb.toTreeMap());
            }
        }
    }

    @Test
    public void testDelete() throws Throwable {
        for (MapPair pair : maps) {
            for (int i = 2; i <= 5; i++) {
                pair.map.remove(i);
                pair.rb.delete(i);
                pair.rb.checkTreeInvariants();
                assertEquals(pair.map, pair.rb.toTreeMap());
            }

            for (int k : pair.rb.keysToArray()) {
                pair.rb.delete(k);
                pair.rb.checkTreeInvariants();
            }
        }
    }

    @Test
    public void testMinMax() throws Exception {
        for (MapPair pair : maps) {
            if (pair.map.isEmpty()) {
                assert pair.rb.max() == null;
                assert pair.rb.min() == null;
            } else {
                assertEquals(Collections.max(pair.map.values()), pair.rb.max());
                assertEquals(Collections.min(pair.map.values()), pair.rb.min());
            }
        }
    }

    @Test
    public void testKeysToArray() throws Exception {
        for (MapPair pair : maps) {
            List<Integer> mapKeys = new ArrayList<>(pair.map.keySet());
            int[] mapKeysArray = new int[mapKeys.size()];
            for (int i = 0; i < mapKeys.size(); i++) {
                mapKeysArray[i] = mapKeys.get(i);
            }
            int[] rbKeys = pair.rb.keysToArray();

            assertArrayEquals(mapKeysArray, rbKeys);
        }
    }

    @Test
    public void testValuesToArray() throws Exception {
        for (MapPair pair : maps) {
            List<String> mapValues = new ArrayList<>(pair.map.values());
            String[] mapValuesArray = new String[mapValues.size()];
            for (int i = 0; i < mapValues.size(); i++) {
                mapValuesArray[i] = mapValues.get(i);
            }
            String[] rbValues = pair.rb.valuesToArray();

            assertArrayEquals(mapValuesArray, rbValues);
        }
    }

    @Test
    public void testSize() throws Exception {
        for (MapPair pair : maps) {
            assertEquals(pair.map.size(), pair.rb.size());
        }
    }

    //TODO: add tests for RBNode
}
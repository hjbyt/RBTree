import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class RBTreeTest {

    Random rand;

    TreeMap<Integer, String> map0;
    TreeMap<Integer, String> map1;

    RBTree rb0;
    RBTree rb1;

    class MapPair {
        public SortedMap<Integer, String> map;
        public RBTree rb;

        public MapPair(SortedMap<Integer, String> map, RBTree rb) {
            this.map = map;
            this.rb = rb;
        }
    }

    List<MapPair> maps;

    @Before
    public void setUp() throws Throwable {
        rand = new Random();

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

    @Test
    public void testEmpty() throws Exception {
        for (MapPair pair : maps) {
            assertEquals(pair.map.isEmpty(), pair.rb.empty());
        }
    }

    @Test
    public void testSearch() throws Exception {
        assertEquals(map1.get(1), rb1.search(1));
        assertEquals(map1.get(111), rb1.search(111));
    }

    @Test
    public void testSearchFail() throws Exception {
        for (MapPair pair : maps) {
            assertEquals(null, pair.rb.search(500));
        }
    }

    @Test
    public void testInsert() throws Throwable {
        for (MapPair pair : maps) {
            for (int i = 11; i <= 19; i++) {
                String s = "" + i;
                pair.map.put(i, s);
                pair.rb.insert(i, s);
                compareAndCheck(pair.map, pair.rb);
            }
        }
    }

    @Test
    public void testInsertFail() throws Throwable {
        for (MapPair pair : maps) {
            for (int key : pair.map.keySet()) {
                assertEquals(-1, pair.rb.insert(key, "something"));
            }
        }
    }

    @Test
    public void testDelete() throws Throwable {
        for (MapPair pair : maps) {
            for (int i = 2; i <= 5; i++) {
                pair.map.remove(i);
                pair.rb.delete(i);
                compareAndCheck(pair.map, pair.rb);
            }

            for (int k : pair.rb.keysToArray()) {
                pair.map.remove(k);
                pair.rb.delete(k);
                compareAndCheck(pair.map, pair.rb);
            }
        }
    }

    public void compareAndCheck(SortedMap<Integer, String> map, RBTree rbTree) {
        rbTree.checkTreeInvariants();
        assertEquals(map, rbTree.toTreeMap());
    }

    @Test
    public void testDeleteFail() throws Throwable {
        for (MapPair pair : maps) {
            assertEquals(-1, pair.rb.delete(500));
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
        RBTree tree = new RBTree();
        assertArrayEquals(new int[0], tree.keysToArray());
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
        RBTree tree = new RBTree();
        assertArrayEquals(new String[0], tree.valuesToArray());
    }

    @Test
    public void testSize() throws Exception {
        for (MapPair pair : maps) {
            assertEquals(pair.map.size(), pair.rb.size());
            int old_size = pair.rb.size();
            for (int i = 15; i < 20; i++) {
                pair.rb.insert(i, Integer.toString(i));
                assertEquals(old_size + 1, pair.rb.size());
                old_size = pair.rb.size();
            }
            for (int key : pair.rb.keysToArray()) {
                pair.rb.delete(key);
                assertEquals(old_size - 1, pair.rb.size());
                old_size = pair.rb.size();
            }
        }
    }

    private int[] getRandomNumbers(int list_length) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < list_length; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        int[] randomized = new int[list_length];
        for (int i = 0; i < numbers.size(); i++) {
            randomized[i] = numbers.get(i);
        }
        return randomized;
    }

    @Test
    public void printMeasurements() throws Exception {
        //TODO XXX
//        for (int i = 1; i <= 10; i++) {
//            RBTree tree = new RBTree();
//            int[] numbers_to_insert = getRandomNumbers(i * 10000);
//            int color_changes = 0;
//            for (int number : numbers_to_insert) {
//                color_changes += tree.insert(number, Integer.toString(number));
//            }
//            System.out.println(Integer.toString(i) + " : " + Integer.toString(color_changes));
//        }
    }

    @Test
    public void testInsertAndDeleteMaxInt() throws Exception {
        for (MapPair pair : maps) {
            int k = Integer.MAX_VALUE;
            String s = "" + k;
            pair.map.put(k, s);
            pair.rb.insert(k, s);
            compareAndCheck(pair.map, pair.rb);

            pair.map.remove(k);
            assertThat(pair.rb.delete(k), not(-1));
            compareAndCheck(pair.map, pair.rb);

            assertThat(pair.rb.delete(k), is(-1));
        }
    }

    @Test
    public void testFuzz() throws Exception {
        final int VALUES_RANGE = 4000;
        SortedMap<Integer, String> map = map0;
        RBTree rb = rb0;

        final int INITIAL_ITEMS = 2000;
        final int FUZZ_ITERATIONS = 6000;

        // insert items
        for (int i = 0; i < INITIAL_ITEMS; i++) {
            int r = rand.nextInt(VALUES_RANGE);
            int return_value = rb.insert(r, "" + r);
            if (map.containsKey(r)) {
                assertEquals(-1, return_value);
            } else {
                map.put(r, "" + r);
            }
            rb.checkTreeInvariants();
        }

        // fuzz
        for (int i = 0; i < FUZZ_ITERATIONS; i++) {
            int r = rand.nextInt(100);
            if (r < 40) {
                // insert new
                do {
                    r = rand.nextInt(VALUES_RANGE);
                } while (map.containsKey(r));
                map.put(r, "" + r);
                assertThat(rb.insert(r, "" + r), not(-1));
                compareAndCheck(map, rb);
            } else if (r < 80) {
                // delete existing
                int k = getRandomKey(rb);
                map.remove(k);
                assertThat(rb.delete(k), not(-1));
                compareAndCheck(map, rb);
            } else if (r < 85) {
                // insert existing
                int k = getRandomKey(rb);
                assertThat(rb.insert(k, "" + k), is(-1));
                compareAndCheck(map, rb);
            } else if (r < 90) {
                // delete non-existing
                do {
                    r = rand.nextInt(VALUES_RANGE);
                } while (map.containsKey(r));
                assertThat(rb.delete(r), is(-1));
                compareAndCheck(map, rb);
            } else if (r < 94) {
                // add min/max
                r = rand.nextInt(2);
                if (r == 0) {
                    //min
                    int k = rb.minKey() - 1;
                    map.put(k, "" + k);
                    rb.insert(k, "" + k);
                    compareAndCheck(map, rb);
                } else {
                    //max
                    int k = rb.maxKey() + 1;
                    map.put(k, "" + k);
                    rb.insert(k, "" + k);
                    compareAndCheck(map, rb);
                }

            } else if (r < 98) {
                // delete min/max
                r = rand.nextInt(2);
                if (r == 0) {
                    //min
                    int k = rb.minKey();
                    map.remove(k);
                    rb.delete(k);
                    compareAndCheck(map, rb);
                } else {
                    //max
                    int k = rb.maxKey();
                    map.remove(k);
                    rb.delete(k);
                    compareAndCheck(map, rb);
                }
            } else {
                // delete root
                int k = rb.rootKey();
                map.remove(k);
                rb.delete(k);
                compareAndCheck(map, rb);
            }

        }

        // delete remaining nodes
        while (!map.isEmpty()) {
            // delete existing
            int k = getRandomKey(rb);
            map.remove(k);
            assertThat(rb.delete(k), not(-1));
            compareAndCheck(map, rb);
        }
    }

    public int getRandomKey(RBTree rb) {
        int index = rand.nextInt(rb.size());
        return rb.selectKey(index);
    }
}
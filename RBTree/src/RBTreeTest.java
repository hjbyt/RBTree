import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class RBTreeTest {

    TreeMap<Integer, String> map0;
    TreeMap<Integer, String> map1;

    RBTree rb0;
    RBTree rb1;

    class MapPair {
        public Map<Integer, String> map;
        public RBTree rb;
        public MapPair(Map<Integer, String> map,RBTree rb) {
            this.map = map;
            this.rb = rb;
        }
    }

    List<MapPair> maps;

    @Before
    public void setUp() throws Exception {
        map0 = new TreeMap<Integer, String>();
        map1 = new TreeMap<Integer, String>();

        for (char c = 'a'; c <= 'g'; c++) {
            String s = ("" + c) + c + c;
            map1.put((int)c, s);
        }

        rb0 = new RBTree(map0);
        rb1 = new RBTree(map1);

        maps = new ArrayList<MapPair>();
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
        assertEquals(map1.get((int)'c'), rb1.search((int)'c'));
        assertEquals(map1.get((int)'z'), rb1.search((int)'z'));
    }

    @Test
    public void testInsert() throws Exception {
        for (MapPair pair : maps) {
            for (char c = 'p'; c <= 'v'; c++) {
                String s = ("" + c) + c + c;
                pair.map.put((int) c, s);
                pair.rb.insert((int) c, s);
                assertEquals(pair.map, pair.rb.toTreeMap());
            }
        }
    }

    @Test
    public void testDelete() throws Exception {
        for (MapPair pair : maps) {
            for (char c = 'b'; c <= 'd'; c++) {
                pair.map.remove((int) c);
                pair.rb.delete((int) c);
                assertEquals(pair.map, pair.rb.toTreeMap());
            }
        }
    }

    @Test
    public void testMin() throws Exception {
        for (MapPair pair : maps) {
            assertEquals(Collections.min(pair.map.values()), pair.rb.min());
        }
    }

    @Test
    public void testMax() throws Exception {
        for (MapPair pair : maps) {
            assertEquals(Collections.max(pair.map.values()), pair.rb.max());
        }
    }

    @Test
    public void testKeysToArray() throws Exception {
        for (MapPair pair : maps) {
            List<Integer> mapKeys = new ArrayList<Integer>(pair.map.keySet());
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
            List<String> mapValues = new ArrayList<String>(pair.map.values());
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
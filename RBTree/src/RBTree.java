import java.util.Map;
import java.util.TreeMap;

/**
 * RBTree
 * <p/>
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 */

public class RBTree {

    private enum Color {
        Black,
        Red,
    }

    private RBNode root;
    private int size;

    //TODO: should these be fields of RBNode?
    static RBNode dummyNode;
    static RBTree nullNode;

    // Default Constructor
    public RBTree() {

    }

    public RBTree(Iterable<Map.Entry<Integer, String>> items) {
        this();
        insertItems(items);
    }

    public RBTree(Map<Integer, String> map) {
        this();
        insertItems(map);
    }

    public void insertItems(Map<Integer, String> map) {
        insertItems(map.entrySet());
    }

    public void insertItems(Iterable<Map.Entry<Integer, String>> items) {
        for (Map.Entry<Integer, String> item : items) {
            insert(item.getKey(), item.getValue());
        }
    }

    public void toMap(Map<Integer, String> map) {
        //TODO
    }

    public TreeMap<Integer, String> toTreeMap() {
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        toMap(map);
        return map;
    }

    /**
     * public boolean empty()
     * <p/>
     * returns true if and only if the tree is empty
     */
    public boolean empty() {
        return size == 0;
    }

    /**
     * public String search(int k)
     * <p/>
     * returns the value of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    public String search(int k) {
        return "42";  // to be replaced by student code
    }

    /**
     * public int insert(int k, String v)
     * <p/>
     * inserts an item with key k and value v to the red black tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were necessary.
     * returns -1 if an item with key k already exists in the tree.
     */
    public int insert(int k, String v) {
        return 42;    // to be replaced by student code
    }

    /**
     * public int delete(int k)
     * <p/>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were needed.
     * returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        return 42;    // to be replaced by student code
    }

    /**
     * public String min()
     * <p/>
     * Returns the value of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public String min() {
        return "42"; // to be replaced by student code
    }

    /**
     * public String max()
     * <p/>
     * Returns the value of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        return "42"; // to be replaced by student code
    }

    /**
     * public int[] keysToArray()
     * <p/>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        int[] arr = new int[42]; // to be replaced by student code
        return arr;              // to be replaced by student code
    }

    /**
     * public String[] valuesToArray()
     * <p/>
     * Returns an array which contains all values in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] valuesToArray() {
        String[] arr = new String[42]; // to be replaced by student code
        return arr;                    // to be replaced by student code
    }

    /**
     * public int size()
     * <p/>
     * Returns the number of nodes in the tree.
     * <p/>
     * precondition: none
     * postcondition: none
     */
    public int size() {
        return size;
    }

    private class RBNode {

        public RBNode parent;
        public RBNode left;
        public RBNode right;
        public Color color;
        public int key;
        public String item;

        public void setLeft(RBNode node) {
            //TODO
        }

        public void setRight(RBNode node) {
            //TODO
        }

        // Replace this node with another node and it's subtrees
        public void transplant(RBNode node) {
            //TODO
        }

        // Replace this node with another node, keeping this node's subtrees.
        public void replace(RBNode node) {
            //TODO
        }

        public void rotateLeft() {
            //TODO
        }

        public void rotateRight() {
            //TODO
        }

    }

}

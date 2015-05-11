import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * RBTree
 * <p>
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 */

public class RBTree {

    private enum Color {
        Black,
        Red,
    }

    private enum Direction {
        Left,
        Right,
    }

    private RBNode root;
    private int size;
    private RBNode minNode;
    private RBNode maxNode;

    //TODO: should these be fields of RBNode?
    private RBNode dummyNode;
    static RBNode nullNode;

    // Default Constructor
    public RBTree() {
        dummyNode = new RBNode();
        dummyNode.color = Color.Black;
        dummyNode.parent = dummyNode;
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
        TreeMap<Integer, String> map = new TreeMap<>();
        toMap(map);
        return map;
    }

    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */
    public boolean empty() {
        return size == 0;
    }

    /**
     * public String search(int k)
     * <p>
     * returns the value of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    public String search(int k) {
        RBNode position = getPositionByKey(k);
        if ((dummyNode != position) && (position.key != k)) {
            return null;
        }
        return position.item;
    }

    private RBNode getPositionByKey(int k) {
        RBNode current = root;
        if (root == null) {
            return dummyNode;
        }
        while (true) {
            if (current.key == k) {
                return current;
            }
            if (current.key < k) {
                if (null == current.left) {
                    return current;
                }
                current = current.left;
            } else { // current.key > k
                if (null == current.right) {
                    return current;
                }
                current = current.right;
            }
        }
    }

    private int fixupTree(RBNode toFix) {
        int colorSwitchCount = 0;
        while (toFix.color == Color.Red) {
            if (toFix.parent == toFix.parent.parent.left) {
                RBNode uncle = toFix.parent.parent.right;
                if (uncle.right.color == Color.Red) {
                    toFix.parent.color = Color.Black;
                    uncle.color = Color.Black;
                    toFix.parent.parent.color = Color.Red;
                    colorSwitchCount += 3;
                    toFix = toFix.parent.parent;
                } else {
                    if (toFix == toFix.parent.right) {
                        toFix = toFix.parent;
                        toFix.rotateLeft();
                    }
                    toFix.parent.color = Color.Black;
                    toFix.parent.parent.color = Color.Red;
                    colorSwitchCount += 2;
                    toFix.parent.parent.rotateRight();
                }
            } else {
                RBNode uncle = toFix.parent.parent.left;
                if (uncle.left.color == Color.Red) {
                    toFix.parent.color = Color.Black;
                    uncle.color = Color.Black;
                    toFix.parent.parent.color = Color.Red;
                    colorSwitchCount += 3;
                    toFix = toFix.parent.parent;
                } else {
                    if (toFix == toFix.parent.left) {
                        toFix = toFix.parent;
                        toFix.rotateRight();
                    }
                    toFix.parent.color = Color.Black;
                    toFix.parent.parent.color = Color.Red;
                    colorSwitchCount += 2;
                    toFix.parent.parent.rotateLeft();
                }
            }
        }
        return colorSwitchCount;
    }

    /**
     * public int insert(int k, String v)
     * <p>
     * inserts an item with key k and value v to the red black tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were necessary.
     * returns -1 if an item with key k already exists in the tree.
     */
    public int insert(int k, String v) {
        if (empty()) {
            root = new RBNode();
            root.parent = dummyNode;
            root.key = k;
            root.item = v;
            root.color = Color.Black;
            size++;
            return 0;
        }
        RBNode parent = getPositionByKey(k);
        if (parent.key == k) {
            return -1;
        }
        size++;
        RBNode newNode = new RBNode();
        newNode.key = k;
        newNode.item = v;
        newNode.parent = parent;
        newNode.color = Color.Red;
        if (parent.key > newNode.key) {
            parent.right = newNode;
            if (parent == minNode) {
                minNode = newNode;
            }
        } else { // parent.key < newNode.key
            parent.left = newNode;
            if (parent == maxNode) {
                maxNode = newNode;
            }
        }
        return fixupTree(parent);
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were needed.
     * returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        RBNode node = getPositionByKey(k);
        if (node.key != k) {
            // Node not found
            return -1;
        }

        return deleteNode(node);
    }

    private int deleteNode(RBNode node) {
        RBNode y = node;
        Color y_original_color = y.color;

        RBNode x;
        if (node.left == nullNode) {
            x = node.left;
            node.transplant(x);
        } else if (node.right == nullNode) {
            x = node.left;
            node.transplant(x);
        } else {
            // swap and delete predecessor;
            y = subtreeMin(node.right);
            y_original_color = y.color;
            x = y.right;
            if (y.parent == node) {
                x.parent = y;
            } else {
                y.transplant(y.right);
                y.right = node.right;
                y.right.parent = y;
            }
            node.transplant(y);
            y.left = node.left;
            y.left.parent = y;
            y.color = node.color;
        }

        if (y_original_color == Color.Black) {
            return deleteFixup(x);
        }

        return 0;
    }

//    private int deleteFixup(RBNode x) {
//        int color_switches = 0;
//
//        RBNode w;
//        while (x != root && x.color == Color.Black) {
//            if (x == x.parent.left) {
//                w = x.parent.right;
//                if (w.color == Color.Red) {
//                    w.color = Color.Black;
//                    x.parent.color = Color.Red;
//                    color_switches += 2;
//                    x.parent.rotateLeft();
//                    w = x.parent.right;
//                }
//                if (w.left.color == Color.Black && w.right.color == Color.Black) {
//                    w.color = Color.Red;
//                    color_switches += 1;
//                    x = x.parent;
//                } else {
//                    if (w.right.color == Color.Black) {
//                        w.left.color = Color.Black;
//                        w.color = Color.Red;
//                        color_switches += 2;
//                        w.rotateRight();
//                        w = x.parent.right;
//                    }
//                    w.color = x.parent.color;
//                    x.parent.color = Color.Black;
//                    w.right.color = Color.Black;
//                    color_switches += 3;
//                    x.parent.rotateLeft();
//                    x = root;
//                }
//            } else {
//                // other direction
//            }
//        }
//        x.color = Color.Black;
//        color_switches += 1;
//
//        return color_switches;
//    }

    private int deleteFixup(RBNode x) {
        int color_switches = 0;

        RBNode w;
        while (x != root && x.color == Color.Black) {
            Direction direction = x == x.parent.left ? Direction.Left : Direction.Right;
            Direction opposite = oppositeDirection(direction);

            w = x.parent.getChild(opposite);
            if (w.color == Color.Red) {
                w.color = Color.Black;
                x.parent.color = Color.Red;
                color_switches += 2;
                x.parent.rotate(direction);
                w = x.parent.getChild(opposite);
            }
            if (w.getChild(direction).color == Color.Black && w.getChild(opposite).color == Color.Black) {
                w.color = Color.Red;
                color_switches += 1;
                x = x.parent;
            } else {
                if (w.getChild(opposite).color == Color.Black) {
                    w.getChild(direction).color = Color.Black;
                    w.color = Color.Red;
                    color_switches += 2;
                    w.rotate(opposite);
                    w = x.parent.getChild(opposite);
                }
                w.color = x.parent.color;
                x.parent.color = Color.Black;
                w.getChild(opposite).color = Color.Black;
                color_switches += 3;
                x.parent.rotate(direction);
                x = root;
            }
        }
        x.color = Color.Black;
        color_switches += 1;

        return color_switches;
    }

    //XXX
    private RBNode successor(RBNode node) {
        return null;
    }

    //XXX
    private RBNode predecessor(RBNode node) {
        return null;
    }

    private RBNode subtreeMin(RBNode node) {
        while (node.left != nullNode) {
            node = node.left;
        }
        return node;
    }

    //XXX
    private RBNode subtreeMax(RBNode node) {
        while (node.right != nullNode) {
            node = node.right;
        }
        return node;
    }

    /**
     * public String min()
     * <p>
     * Returns the value of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public String min() {
        return minNode.item;
    }

    /**
     * public String max()
     * <p>
     * Returns the value of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        return maxNode.item;
    }

    private class IndexedConsumer<T> implements Consumer<T> {
        int index;
        BiConsumer<T, Integer> base;

        public IndexedConsumer(BiConsumer<T, Integer> baseFunction) {
            index = 0;
            base = baseFunction;
        }

        public void accept(T arg) {
            base.accept(arg, index);
            index++;
        }
    }

    private void walk(RBNode node, Consumer<RBNode> consumer) {
        if (null != node.left) {
            walk(node.left, consumer);
        }
        consumer.accept(node);
        if (null != node.right) {
            walk(node.right, consumer);
        }
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        int[] keys = new int[size];
        if (!empty()) {
            walk(root, new IndexedConsumer<>((node, index) -> keys[index] = node.key));
        }
        return keys;
    }

    /**
     * public String[] valuesToArray()
     * <p>
     * Returns an array which contains all values in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] valuesToArray() {
        String[] items = new String[size];
        if (!empty()) {
            walk(root, new IndexedConsumer<>((node, index) -> items[index] = node.item));
        }
        return items;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     * <p>
     * precondition: none
     * postcondition: none
     */
    public int size() {
        return size;
    }

    private Direction oppositeDirection(Direction direction) {
        if (direction == Direction.Left) {
            return Direction.Right;
        } else {
            return Direction.Left;
        }
    }

    private class RBNode {

        public RBNode parent;
        public RBNode left;
        public RBNode right;
        public Color color;
        public int key;
        public String item;

        public RBNode getChild(Direction direction) {
            if (direction == Direction.Left) {
                return left;
            } else {
                return right;
            }
        }

        public void setChild(Direction direction, RBNode node) {
            if (direction == Direction.Left) {
                setLeft(node);
            } else {
                setRight(node);
            }
        }

        public void rotate(Direction direction) {
            if (direction == Direction.Left) {
                rotateLeft();
            } else {
                rotateRight();
            }
        }

        public void setLeft(RBNode node) {
            left = node;
            node.parent = this;
        }

        public void setRight(RBNode node) {
            right = node;
            node.parent = this;
        }

        // Replace this node with another node and it's subtrees
        public void transplant(RBNode node) {
            if (this == parent.left) {
                parent.setLeft(node);
            } else {
                parent.setRight(node);
            }
        }

        // Replace this node with another node, keeping this node's subtrees.
        public void replace(RBNode node) {
            transplant(node);
            node.setLeft(left);
            node.setRight(right);
        }

        public void rotateLeft() {
            RBNode oldRight = right;
            transplant(oldRight);
            setRight(oldRight.left);
            oldRight.setLeft(this);
        }

        public void rotateRight() {
            RBNode oldLeft = left;
            transplant(oldLeft);
            setLeft(oldLeft.right);
            oldLeft.setRight(this);
        }

    }

}

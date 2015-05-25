import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// e.g. should hasLeftChild say in RBNode (it references nil, which is outside of it)
// and should successor/predecessor be in RBNode?
// or maybe should RBNode be agnostic to the order of node, which is imposed by the tree...

/**
 * RBTree
 * <p>
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 */

public class RBTree {

    static Consumer<RBNode> dummyConsumer = (a) -> {
    };

    private enum Color {
        Black,
        Red,
    }

    private enum Direction {
        Left,
        Right,
    }

    private RBNode rootDummy;
    private int size;
    private RBNode minNode;
    private RBNode maxNode;
    private RBNode nil;


    // Default Constructor
    public RBTree() {
        // Create dummy node
        rootDummy = new RBNode(Color.Black);
        //TODO: should we do something different? Because if a key with this value is inserted/deleted it would be an error.
        rootDummy.key = Integer.MAX_VALUE;
        rootDummy.parent = rootDummy;

        nil = new RBNode(Color.Black);
        nil.parent = rootDummy;
        nil.left = null;
        nil.right = null;
        rootDummy.left = nil;
        rootDummy.right = nil;
        minNode = null;
        maxNode = null;

        size = 0;
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
        walkPreOrder(root(), (node) -> map.put(node.key, node.item));
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
        RBNode node = searchNode(k);
        if (node == null) {
            return null;
        }
        return node.item;

    }

    private RBNode searchNode(int k) {
        RBNode node = getPositionByKey(k);
        if (node.key != k) {
            return null;
        }
        return node;
    }

    private RBNode getPositionByKey(int k) {
        RBNode node = rootDummy;
        while (true) {
            assert node != nil;
            assert node != null;

            if (k == node.key) {
                return node;
            }

            if (k < node.key) {
                if (node.hasLeftChild()) {
                    node = node.left;
                } else {
                    return node;
                }
            } else { // k > current.key
                if (node.hasRightChild()) {
                    node = node.right;
                } else {
                    return node;
                }
            }
        }
    }

    private int insertFixup(RBNode toFix) {
        int colorSwitchCount = 0;
        while (toFix != nil && toFix != root() && toFix != rootDummy && toFix.parent.color == Color.Red) {
            Direction direction = toFix.relationToParent();
            Direction opposite = oppositeDirection(direction);
            RBNode uncle = toFix.parent.parent.getChild(opposite);
            if (uncle.color == Color.Red) {
                toFix.parent.color = Color.Black;
                uncle.color = Color.Black;
                toFix.parent.parent.color = Color.Red;
                colorSwitchCount += 3;
                toFix = toFix.parent.parent;
            }
            else {
                if (toFix.relationToParent() == opposite) {
                    toFix = toFix.parent;
                    toFix.rotate(direction);
                }
                toFix.parent.color = Color.Black;
                toFix.parent.parent.color = Color.Red;
                colorSwitchCount += 2;
                toFix.parent.parent.rotate(opposite);
            }
        }
        root().color = Color.Black;
        colorSwitchCount += 1;
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
        RBNode parent = getPositionByKey(k);
        if (parent.key == k) {
            return -1;
        }

        RBNode node = new RBNode(Color.Red);
        node.key = k;
        node.item = v;
        node.parent = parent;
        node.left = nil;
        node.right = nil;

        if (empty()) {
            minNode = node;
            maxNode = node;
        }

        if (node.key < parent.key) {
            parent.left = node;
            if (parent == minNode) {
                minNode = node;
            }
        } else {
            assert node.key > parent.key;
            parent.right = node;
            if (parent == maxNode) {
                maxNode = node;
            }
        }

        size += 1;
        return insertFixup(node);
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
        RBNode node = searchNode(k);
        if (node == null) {
            return -1;
        }

        if (size == 1) {
            minNode = null;
            maxNode = null;
        } else if (node == minNode) {
            minNode = successor(node);
        } else if (node == maxNode) {
            maxNode = predecessor(node);
        }

        size -= 1;
        return deleteNode(node);
    }

    private int deleteNode(RBNode node) {
        RBNode y = node;
        Color y_original_color = y.color;

        RBNode x;
        if (node.left == nil) {
            x = node.right;
            node.transplant(x);
        } else if (node.right == nil) {
            x = node.left;
            node.transplant(x);
        } else {
            // swap and delete predecessor;
            y = subtreeMin(node.right);
            assert y != null;
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

    private int deleteFixup(RBNode x) {
        int color_switches = 0;

        RBNode w;
        while (x != root() && x.color == Color.Black) {
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
                x = root();
            }
        }
        x.color = Color.Black;
        color_switches += 1;


        return color_switches;
    }

    private RBNode successor(RBNode node) {
        assert node != maxNode;
        if (node.right != nil) {
            return subtreeMin(node.right);
        } else {
            while (node.relationToParent() == Direction.Right) {
                node = node.parent;
            }
            return node.parent;
        }
    }

    private RBNode predecessor(RBNode node) {
        assert node != minNode;
        if (node.left != nil) {
            return subtreeMax(node.left);
        } else {
            while (node.relationToParent() == Direction.Left) {
                node = node.parent;
            }
            return node.parent;
        }
    }

    private RBNode subtreeMin(RBNode node) {
        if (node == nil) {
            return null;
        }
        while (node.left != nil) {
            node = node.left;
        }
        return node;
    }

    private RBNode subtreeMax(RBNode node) {
        if (node == nil) {
            return null;
        }
        while (node.right != nil) {
            node = node.right;
        }
        return node;
    }

    private RBNode root() {
        return rootDummy.left;
    }

    /**
     * public String min()
     * <p>
     * Returns the value of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public String min() {
        if (minNode == null) {
            return null;
        }
        return minNode.item;
    }

    /**
     * public String max()
     * <p>
     * Returns the value of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        if (minNode == null) {
            return null;
        }
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

    private void walkPreOrder(RBNode node, Consumer<RBNode> consumer) {
        walk(node, consumer, dummyConsumer, dummyConsumer);
    }

    private void walkInOrder(RBNode node, Consumer<RBNode> consumer) {
        walk(node, dummyConsumer, consumer, dummyConsumer);
    }

    private void walkPostOrder(RBNode node, Consumer<RBNode> consumer) {
        walk(node, dummyConsumer, dummyConsumer, consumer);
    }

    private void walk(RBNode node, Consumer<RBNode> consumerPre, Consumer<RBNode> consumerIn, Consumer<RBNode> consumerPost) {
        if (node == nil) {
            return;
        }
        consumerPre.accept(node);
        walk(node.left, consumerPre, consumerIn, consumerPost);
        consumerIn.accept(node);
        walk(node.right, consumerPre, consumerIn, consumerPost);
        consumerPost.accept(node);
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        int[] keys = new int[size];
        walkInOrder(root(), new IndexedConsumer<>((node, index) -> keys[index] = node.key));
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
        walkInOrder(root(), new IndexedConsumer<>((node, index) -> items[index] = node.item));
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

    void printTree() {
        printTree(System.out);
    }

    void printTree(PrintStream stream) {
        rootDummy.printTree(stream);
    }

    // non-private for testing purposes
    void checkTreeInvariants() {
        try {
            checkTreeInvariants_();
        } catch (Throwable throwable) {
            printTree();
            throw throwable;
        }
    }

    private void checkTreeInvariants_() {
        assert !rootDummy.hasRightChild() : "rootDummy has a right child";
        assert rootDummy.color == Color.Black : "Invalid color for rootDummy";
        assert rootDummy.parent == rootDummy : "Invalid parent for rootDummy";
        assert nil.color == Color.Black : "Invalid color for nil";
        assert nil.right == null && nil.left == null : "Invalid child for nil";
        assert nil.key == 0 : "Invalid key nil";
        assert nil.item == null : "Invalid item for nil";
        assert rootDummy.key == Integer.MAX_VALUE : "Invalid key for rootDummy";
        assert rootDummy.item == null : "Invalid item for rootDummy";

        checkSubtreeInvariants(root());

        TreeMap<Integer, String> map = toTreeMap();
        assert map.size() == size() : "Incorrect size";
        assert subtreeMin(root()) == minNode : String.format("Incorrect minNode: %s != %s", subtreeMin(root()), minNode);
        assert subtreeMax(root()) == maxNode : "Incorrect maxNode";
    }

    // Returns the node black height
    private int checkSubtreeInvariants(RBNode node) {
        assert node != null : "Invalid node (null)";
        if (node == nil) {
            return 1;
        }

        assert !(node.color == Color.Red && node.parent.color == Color.Red) : "Red rule violated";

        int black_length = 0;
        if (node.color == Color.Black) {
            black_length += 1;
        }

        if (node.hasLeftChild()) {
            assert node.left.key < node.key : "Left child key not lower than node key";
        }
        if (node.hasRightChild()) {
            assert node.right.key > node.key : "Right child key not higher then node key";
        }

        int left_black_length = checkSubtreeInvariants(node.left);
        int right_black_length = checkSubtreeInvariants(node.right);
        assert left_black_length == right_black_length : "Black rule violated";
        black_length += left_black_length;

        return black_length;
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

        public RBNode(RBNode parent, RBNode left, RBNode right, Color color, int key, String item) {
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.color = color;
            this.key = key;
            this.item = item;
        }

        public RBNode(Color color) {
            this.color = color;
        }

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

        public boolean isRightChild() {
            return parent.right == this;
        }

        public boolean isLeftChild() {
            return parent.left == this;
        }

        public Direction relationToParent() {
            return isLeftChild() ? Direction.Left : Direction.Right;
        }

        public boolean hasLeftChild() {
            return left != nil;
        }

        public boolean hasRightChild() {
            return right != nil;
        }

        @Override
        public String toString() {
            String color_string = (color == Color.Black) ? "B" : "R";
            return String.format("%s-%d:%s", color_string, key, item);
        }

        //Printing adapted from http://stackoverflow.com/a/19484210
        public void printTree() {
            printTree(System.out);
        }

        public void printTree(PrintStream out) {
            if (right != null) {
                right.printTree(out, true, "");
            }
            printNodeValue(out);
            if (left != null) {
                left.printTree(out, false, "");
            }
        }

        private void printNodeValue(PrintStream out) {
            out.print(toString() + '\n');
        }

        private void printTree(PrintStream out, boolean isRight, String indent) {
            if (right != null) {
                right.printTree(out, true, indent + (isRight ? "        " : " |      "));
            }
            out.print(indent);
            if (isRight) {
                out.print(" /");
            } else {
                out.print(" \\");
            }
            out.print("----- ");
            out.print(toString() + '\n');
            if (left != null) {
                left.printTree(out, false, indent + (isRight ? " |      " : "        "));
            }
        }
    }

}

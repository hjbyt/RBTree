import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//TODO: decide which methods should be in RBNode and which shouldn't.
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
        //TODO: should we do something different?
        //      because if a key with this value is inserted/deleted it would be an error.
        rootDummy.key = Integer.MAX_VALUE;
        rootDummy.parent = rootDummy;

        nil = new RBNode(Color.Black);
        nil.parent = rootDummy;
        //TODO: shuold nil's children be null?
        nil.left = nil;
        nil.right = nil;
        rootDummy.left = nil;
        rootDummy.right = nil;
        minNode = nil;
        maxNode = nil;

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

    public RBNode searchNode(int k) {
        RBNode node = getPositionByKey(k);
        if (node.key != k) {
            return null;
        }
        return node;
    }

    private RBNode getPositionByKey(int k) {
        RBNode current = rootDummy;
        while (true) {
            if (current.key == k) {
                return current;
            }
            if (current == nil) {
                return nil.parent;
            }
            if (current.key < k) {
                current = current.left;
            } else { // current.key > k
                current = current.right;
            }
        }
    }

    private int insertFixup(RBNode toFix) {
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
        RBNode parent = getPositionByKey(k);
        if (parent.key == k) {
            return -1;
        }

        RBNode newNode = new RBNode(Color.Red);
        newNode.key = k;
        newNode.item = v;
        newNode.parent = parent;

        if (empty()) {
            minNode = newNode;
            maxNode = newNode;
        }

        if (newNode.key < parent.key) {
            parent.left = newNode;
            if (parent == minNode) {
                minNode = newNode;
            }
        } else {
            assert newNode.key > parent.key;
            parent.right = newNode;
            if (parent == maxNode) {
                maxNode = newNode;
            }
        }

        size += 1;
        return insertFixup(parent);
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

        //TODO: make sure these don't affect runtime complexity of delete
        if (size == 1) {
            minNode = nil;
            maxNode = nil;
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
            x = node.left;
            node.transplant(x);
        } else if (node.right == nil) {
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
//        while (x != rootDummy && x.color == Color.Black) {
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
//                    x = rootDummy;
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
        while (x != rootDummy && x.color == Color.Black) {
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
                x = rootDummy;
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
            return node;
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
            return node;
        }
    }

    private RBNode subtreeMin(RBNode node) {
        while (node.left != nil) {
            node = node.left;
        }
        return node;
    }

    private RBNode subtreeMax(RBNode node) {
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
        // Note: if node is nil, then node.item should be null
        return minNode.item;
    }

    /**
     * public String max()
     * <p>
     * Returns the value of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        // Note: if node is nil, then node.item should be null
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

    //TODO XXX
//    private class PrintingThrowable extends Throwable {
//        private RBTree tree;
//
//        public PrintingThrowable(Throwable cause, RBTree tree) {
//            super(cause);
//            this.tree = tree;
//        }
//
//        @Override
//        public void printStackTrace(PrintStream s) {
//            this.tree.printTree(s);
//            super.printStackTrace(s);
//        }
//    }

    private void printTree() {
        printTree(System.out);
        //TODO XXX
        //printTree(System.err);
    }

    private void printTree(PrintStream stream) {
        TreePrinter.print(this.rootDummy, stream);
    }

    // non-private for testing purposes
    void checkTreeInvariants() {
        try {
            checkTreeInvariance_();
        } catch (Throwable throwable) {
            printTree();
            throw throwable;
            //TODO XXX
//            throw new PrintingThrowable(throwable, this);
        }
    }

    private void checkTreeInvariance_() {
        if (rootDummy.hasRightChild()) {
            throw new AssertionError("rootDummy has a right child");
        }
        //TODO XXX: im not sure if the rootDummy should always stay black or not
//        if (rootDummy.color != Color.Black) {
//            throw new AssertionError("Invalid color for rootDummy");
//        }
        if (rootDummy.parent != rootDummy) {
            throw new AssertionError("Invalid parent for rootDummy");
        }
        if (nil.color != Color.Black) {
            throw new AssertionError("Invalid color for nil");
        }
        if (nil.right != nil || nil.left != nil) {
            throw new AssertionError("Invalid child for nil");
        }
        if (nil.key != 0 || nil.item != null || rootDummy.key != Integer.MAX_VALUE || rootDummy.item != null) {
            throw new AssertionError("Invalid key/item for nil/rootDummy");
        }
        checkSubtreeInvariants(rootDummy);
        TreeMap<Integer, String> map = toTreeMap();
        if (map.size() != size()) {
            throw new AssertionError("Incorrect size");
        }
        if (subtreeMin(root()) != minNode || subtreeMax(root()) != maxNode) {
            throw new AssertionError("Incorrect min/max node");
        }
    }

    // Returns the node black height
    private int checkSubtreeInvariants(RBNode node) {
        if (node == null) {
            throw new AssertionError("Invalid node (null)");
        }
        if (node == nil) {
            return 1;
        }

        if (node.color == Color.Red && node.parent.color == Color.Red) {
            throw new AssertionError("Red rule violated");
        }

        int black_length = 0;
        if (node.color == Color.Black) {
            black_length += 1;
        }

        if (node.hasLeftChild()) {
            if (node.left.key >= node.key) {
                throw new AssertionError("Left child key not lower than node key");
            }
        }
        if (node.hasRightChild()) {
            if (node.right.key <= node.key) {
                throw new AssertionError("Right child key not higher then node key");
            }
        }

        int left_black_length = checkSubtreeInvariants(node.left);
        int right_black_length = checkSubtreeInvariants(node.right);
        if (left_black_length != right_black_length) {
            throw new AssertionError("Black rule violated");
        }
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

    private class RBNode implements TreePrinter.PrintableNode {

        public RBNode parent;
        public RBNode left;
        public RBNode right;
        public Color color;
        public int key;
        public String item;

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

        public TreePrinter.PrintableNode getLeft() {
            if (this == nil) {
                return null;
            } else {
                return left;
            }
        }

        public TreePrinter.PrintableNode getRight() {
            if (this == nil) {
                return null;
            } else {
                return right;
            }
        }

        public String getText() {
            String color_string = (color == Color.Black) ? "B" : "R";
            return String.format("%s-%d:%s", color_string, key, item);
        }
    }

}


//adapted from http://stackoverflow.com/a/29704252
/**
 * Binary tree printer
 *
 * @author MightyPork
 */
class TreePrinter {
    /**
     * Node that can be printed
     */
    public interface PrintableNode {
        /**
         * Get left child
         */
        PrintableNode getLeft();


        /**
         * Get right child
         */
        PrintableNode getRight();


        /**
         * Get text to be printed
         */
        String getText();
    }

    /**
     * Print a tree
     *
     * @param root tree root node
     */
    public static void print(PrintableNode root, PrintStream stream) {
        List<List<String>> lines = new ArrayList<>();

        List<PrintableNode> level = new ArrayList<>();
        List<PrintableNode> next = new ArrayList<>();

        level.add(root);
        int nn = 1;

        int widest = 0;

        while (nn != 0) {
            List<String> line = new ArrayList<>();

            nn = 0;

            for (PrintableNode n : level) {
                if (n == null) {
                    line.add(null);

                    next.add(null);
                    next.add(null);
                } else {
                    String aa = n.getText();
                    line.add(aa);
                    if (aa.length() > widest) widest = aa.length();

                    next.add(n.getLeft());
                    next.add(n.getRight());

                    if (n.getLeft() != null) nn++;
                    if (n.getRight() != null) nn++;
                }
            }

            if (widest % 2 == 1) widest++;

            lines.add(line);

            List<PrintableNode> tmp = level;
            level = next;
            next = tmp;
            next.clear();
        }

        int perpiece = lines.get(lines.size() - 1).size() * (widest + 4);
        for (int i = 0; i < lines.size(); i++) {
            List<String> line = lines.get(i);
            int hpw = (int) Math.floor(perpiece / 2f) - 1;

            if (i > 0) {
                for (int j = 0; j < line.size(); j++) {

                    // split node
                    char c = ' ';
                    if (j % 2 == 1) {
                        if (line.get(j - 1) != null) {
                            //c = (line.get(j) != null) ? '?' : '?';
                            c = (line.get(j) != null) ? '.' : '.';
                        } else {
                            //if (j < line.size() && line.get(j) != null) c = '?';
                            if (j < line.size() && line.get(j) != null) c = '.';
                        }
                    }
                    stream.print(c);

                    // lines and spaces
                    if (line.get(j) == null) {
                        for (int k = 0; k < perpiece - 1; k++) {
                            stream.print(" ");
                        }
                    } else {

                        for (int k = 0; k < hpw; k++) {
                            //stream.print(j % 2 == 0 ? " " : "?");
                            stream.print(j % 2 == 0 ? " " : ".");
                        }
                        //stream.print(j % 2 == 0 ? "?" : "?");
                        stream.print(j % 2 == 0 ? "." : ".");
                        for (int k = 0; k < hpw; k++) {
                            //stream.print(j % 2 == 0 ? "?" : " ");
                            stream.print(j % 2 == 0 ? "." : " ");
                        }
                    }
                }
                stream.println();
            }

            // print line of numbers
            for (String f : line) {
                if (f == null) f = "";
                int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
                int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

                // a number
                for (int k = 0; k < gap1; k++) {
                    stream.print(" ");
                }
                stream.print(f);
                for (int k = 0; k < gap2; k++) {
                    stream.print(" ");
                }
            }
            stream.println();

            perpiece /= 2;
        }
    }
}
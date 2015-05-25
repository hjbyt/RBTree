import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//TODO: decide which methods should be in RBNode and which shouldn't.
// e.g. should hasLeftChild say in RBNode (it references nil, which is outside of it)
// and should successor/predecessor be in RBNode?
// or maybe should RBNode be agnostic to the order of node, which is imposed by the tree...
//TODO: write doc (use generate javadoc?
//TODO: perform and explain measurements
//TODO: send

/**
 * RBTree
 * <p>
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 */

public class RBTree {

    /**
     * static Consumer<RBNode> dummyConsumer
     * <p>
     * A consumer for RBNodes that does nothing. Is used with inside the different version
     * of the walk function
     */
    static Consumer<RBNode> dummyConsumer = (a) -> {
    };

    /**
     * private enum Color
     * <p>
     * Represents a possible color for a node in the red black tree.
     * <p>
     */
    private enum Color {
        Black,
        Red,
    }

    /**
     * private enum Direction
     * <p>
     * An enum used to represent a child's direction for a node in the tree.
     * The enum is used in order to make the Insert and Delete function direction-agnositc
     * in order to share code between the symmetric sides for edge-cases
     * <p>
     */
    private enum Direction {
        Left,
        Right,
    }

    /**
     * private RBNode rootDummy
     * <p>
     * A dummy root used (just like the dummy sentinel in Cormen)
     */
    private RBNode rootDummy;
    /**
     * private int size
     * <p>
     * A member that holds the number of nodes in the tree
     */
    private int size;
    /**
     * private RBNode minNode;
     * <p>
     * Holds the node with the minimum value - for optimizing the "min" function
     */
    private RBNode minNode;
    /**
     * private RBNode maxNode;
     * <p>
     * Holds the node with the maximum value - for optimizing the "max" function
     */
    private RBNode maxNode;
    /**
     * private RBNode nil
     * <p>
     * A dummy value used as a NULL child for all the leaves in the tree
     */
    private RBNode nil;


    /**
     * public RBTree
     * <p>
     * A default constructor for the RBTree class
     * Works at O(1).
     * <p>
     * precondition: none
     * postcondition: none
     */
    public RBTree() {
        // Create dummy node
        rootDummy = new RBNode(null, null, null, Color.Black, Integer.MAX_VALUE, null);
        //TODO: should we do something different?
        //      because if a key with this value is inserted/deleted it would be an error.

        nil = new RBNode(rootDummy, null, null, Color.Black, 0, null);
        rootDummy.left = nil;
        rootDummy.right = nil;

        minNode = null;
        maxNode = null;

        size = 0;
    }

    /**
     * public RBTree(Iterable<Map.Entry<Integer, String>> items)
     * <p>
     * A constructor for the tree that is initialized with a list of key-value pairs
     * * Works at O(n) where n is the number of key-values pairs in the iterable.
     * <p>
     * precondition: items != null
     * postcondition: none
     *
     * @param items A list of key value pairs to initialize the tree with
     */
    public RBTree(Iterable<Map.Entry<Integer, String>> items) {
        this();
        insertItems(items);
    }

    /**
     * public RBTree(Map<Integer, String> map)
     * <p>
     * A constructor for the tree that is initialized with a given map for the keys and values
     * * Works at O(n) where n is the number of key-values pairs in the map.
     * <p>
     * precondition: map != null
     * postcondition: none
     *
     * @param map A map keys and values to initialize the tree with
     */
    public RBTree(Map<Integer, String> map) {
        this();
        insertItems(map);
    }

    /**
     * public void insertItems(Map<Integer, String> map)
     * <p>
     * Inserts all the items in the map into the tree
     * * Works at O(n) where n is the number of key-values pairs in the map.
     * <p>
     * precondition: map != null
     * postcondition: none
     *
     * @param map A map keys and values to insert into the tree
     */
    public void insertItems(Map<Integer, String> map) {
        insertItems(map.entrySet());
    }

    /**
     * public void insertItems(Iterable<Map.Entry<Integer, String>> items)
     * <p>
     * Inserts all the items in the list into the tree.
     * Works at O(n) where n is the number of key-values pairs in the iterable.
     * <p>
     * precondition: map != null
     * postcondition: none
     *
     * @param items The items to insert into the tree
     */
    public void insertItems(Iterable<Map.Entry<Integer, String>> items) {
        for (Map.Entry<Integer, String> item : items) {
            insert(item.getKey(), item.getValue());
        }
    }

    /**
     * public void toMap(Map<Integer, String> map)
     * <p>
     * Inserts all the elements in the tree into the given map
     * Works at O(n) where n is the number of nodes in the tree
     * <p>
     * precondition: map != null
     * postcondition: none
     *
     * @param map The map to insert all the tree's elements into
     */
    public void toMap(Map<Integer, String> map) {
        walkPreOrder(root(), (node) -> map.put(node.key, node.item));
    }

    /**
     * public TreeMap<Integer, String> toTreeMap()
     * <p>
     * Returns a representation of the tree as a native java TreeMap
     * Works at O(n) where n is the number of nodes in the tree
     * <p>
     * precondition: none
     * postcondition: none
     *
     * @return A TreeMap that holds all the key-value pairs from the tree
     */
    public TreeMap<Integer, String> toTreeMap() {
        TreeMap<Integer, String> map = new TreeMap<>();
        toMap(map);
        return map;
    }

    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     *
     * @return Says whether the tree is empty or not
     */
    public boolean empty() {
        return size == 0;
    }

    /**
     * public String search(int k)
     * <p>
     * returns the value of an item with key k if it exists in the tree
     * otherwise, returns null
     * Works in O(logn) where n is the number of elements in the tree
     *
     * @param k The key by which to look up the value
     * @return A string if the matching key is found, or null otherwise
     */
    public String search(int k) {
        RBNode node = searchNode(k);
        if (node == null) {
            return null;
        }
        return node.item;

    }

    /**
     * private RBNode searchNode(int k)
     * <p>
     * Looks up a RBNode using a search key
     * Works in O(logn) where n is the number of nodes in the tree
     *
     * @param k The key by which to look up the node
     * @return The holding node if it's found, or null otherwise
     */
    private RBNode searchNode(int k) {
        RBNode node = getPositionByKey(k);
        if (node.key != k) {
            return null;
        }
        return node;
    }

    /**
     * private RBNode getPositionByKey(int k)
     * <p>
     * Gets the node under which to insert a node with the specified key value, or returns the node holding the value
     * Works in O(logn) where n is the number of nodes in the tree
     *
     * @param k The key to look by
     * @return The parent under which to insert the new node, or the current node if the value is already present
     */
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
            } else {
                assert k > node.key;
                if (node.hasRightChild()) {
                    node = node.right;
                } else {
                    return node;
                }
            }
        }
    }

    /**
     * public int insert(int k, String v)
     * <p>
     * inserts an item with key k and value v to the red black tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were necessary.
     * returns -1 if an item with key k already exists in the tree.
     * TODO - Add O()
     *
     * @param k The key of the new node to insert into the tree
     * @param v The new value to insert into the tree
     * @return The number of node-color changes that happened during the insert, or -1 if an error occurs
     */
    public int insert(int k, String v) {
        RBNode parent = getPositionByKey(k);
        if (parent.key == k) {
            return -1;
        }

        RBNode node = new RBNode(parent, nil, nil, Color.Red, k, v);

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
     * private int insertFixup(RBNode toFix)
     * <p>
     * Fixes the tree to retain it's red-black properties after a node was inserted
     * TODO - Add O()
     *
     * @param node The node from which to start the fix
     * @return The number of color changes made to nodes in order to maintain the red-black property
     */
    private int insertFixup(RBNode node) {
        // TODO: put a few comments here
        int colorSwitchCount = 0;
        while (node.parent.color == Color.Red) {
            Direction direction = node.parent.relationToParent();
            Direction opposite = oppositeDirection(direction);

            RBNode uncle = node.getUncle();
            if (uncle.color == Color.Red) {
                node.parent.color = Color.Black;
                uncle.color = Color.Black;
                node.parent.parent.color = Color.Red;
                colorSwitchCount += 3;
                node = node.parent.parent;
            } else {
                if (node.relationToParent() == opposite) {
                    node = node.parent;
                    node.rotate(direction);
                }
                node.parent.color = Color.Black;
                node.parent.parent.color = Color.Red;
                colorSwitchCount += 2;
                node.parent.parent.rotate(opposite);
            }
        }
        if (root().color == Color.Red) {
            root().color = Color.Black;
            colorSwitchCount += 1;
        }
        return colorSwitchCount;
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were needed.
     * returns -1 if an item with key k was not found in the tree.
     * TODO - Add O()
     *
     * @param k The key who's node we want to delete
     * @return The number of node-color changes that happened during the insert, or -1 if an error occurs
     */
    public int delete(int k) {
        RBNode node = searchNode(k);
        if (node == null) {
            return -1;
        }

        //TODO: make sure these don't affect runtime complexity of delete
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

    /**
     * private int deleteNode(RBNode node)
     * <p>
     * Deletes a node from the RBTree
     * * TODO - Add O()
     *
     * @param node The node to delete
     * @return The number of node-color changes that happened during the delete
     */
    private int deleteNode(RBNode node) {
        int color_switches = 0;

        // If has both children
        if (node.childrenCount() == 2) {
            // Place the successor instead of the node to delete
            RBNode successorNode = successor(node);
            node.key = successorNode.key;
            node.item = successorNode.item;
            // Fix maxNode if necessary
            if (successorNode == maxNode) {
                maxNode = node;
            }
            // Set the node to delete to be the successor node
            node = successorNode;
        }

        // Note: now node has 0 or 1 child

        if (node.hasChildren()) {
            RBNode child = node.hasLeftChild() ? node.left : node.right;
            // Remove node by transplanting it's child over it
            node.transplant(child);

            // Fix black-rule if needed
            if (node.color == Color.Black) {
                color_switches = deleteFixup(child);
            }
        } else { // No children
            // Fix black-rule if needed
            if (node.color == Color.Black) {
                color_switches = deleteFixup(node);
            }

            // Remove node
            node.parent.setChild(node.relationToParent(), nil);
        }

        return color_switches;
    }

    /**
     * private int deleteFixup(RBNode x)
     * <p>
     * Fixes the red-black tree to maintain it's red-black properties after a node was deleted
     * TODO - Add O()
     *
     * @param node The node from which to start the fixup-process
     * @return The number of node-color changes that happened during the delete
     */
    private int deleteFixup(RBNode node) {
        int color_switches = 0;

        while (node != root() && node.color == Color.Black) {
            Direction direction = node.relationToParent();
            Direction opposite = oppositeDirection(direction);

            RBNode brother = node.getBrother();
            if (brother.color == Color.Red) {
                // Case 1: brother is red
                // Switch colors between brother and parent, then rotate parent towards node.
                brother.color = Color.Black;
                node.parent.color = Color.Red;
                color_switches += 2;
                node.parent.rotate(direction);
                assert node.getBrother().color == Color.Black;
                // Note: now node's brother is surely black, and we will get to case 2/3/4.
            } else if (node.getNephewNear().color == Color.Black && node.getNephewFar().color == Color.Black) {
                // Case 2: brother is black, and both nephews are black.
                // Set brother's color to red, to make the black-height of node and brother the same.
                brother.color = Color.Red;
                color_switches += 1;
                node = node.parent;
                // Now if the parent is black, we move the issue to the parent.
                // If it is red, then the loop terminates, and it will be made black afterwards, and the fixing is over.
            } else if (node.getNephewFar().color == Color.Black) {
                // Case 3: brother is black, far nephew is black, and near nephew is red.
                assert node.getNephewNear().color == Color.Red;
                // Switch brother and near-nephew's colors, and rotate brother right.
                // This keeps the invariants of node's brother subtree.
                node.getNephewNear().color = Color.Black;
                brother.color = Color.Red;
                color_switches += 2;
                brother.rotate(opposite);
                // Now node's brother is red, which bring us to case 4.
            } else {
                // Case 4: brother is black, far nephew is red.
                // Change parent, brother, far-nephew colors, and rotate parent towards node.
                // This is a terminal case.
                brother.color = node.parent.color; //TODO XXX: this might not change color. should we count it ???
                node.parent.color = Color.Black; //TODO XXX: this might not change color. should we count it ???
                node.getNephewFar().color = Color.Black;
                color_switches += 3;
                node.parent.rotate(direction);
                // Set node to root, to terminate the loop, and possibly update root's color afterwards.
                node = root();
            }
        }
        // In some of cases (2,4) we might terminate when node is red.
        // in that case, changing node to black will restore the black-law.
        if (node.color == Color.Red) {
            node.color = Color.Black;
            color_switches += 1;
        }

        return color_switches;
    }

    /**
     * private RBNode successor(RBNode node)
     * <p>
     * Finds the successor to a node in the tree.
     * Works at O(logn) where n is the number of nodes in the tree
     * precondition: node != maxNode
     *
     * @param node The node who's successor we want to find
     * @return The node with the smalles key value which is still bigger than the current
     */
    private RBNode successor(RBNode node) {
        assert node != maxNode;
        if (node.hasRightChild()) {
            return subtreeMin(node.right);
        } else {
            while (node.relationToParent() == Direction.Right) {
                node = node.parent;
            }
            return node.parent;
        }
    }

    /**
     * private RBNode predecessor(RBNode node)
     * <p>
     * Finds the predecessor to a node in the tree.
     * Works at O(logn) where n is the number of nodes in the tree
     * precondition: node != minNode
     *
     * @param node The node who's predecessor we want to find
     * @return The node with the biggest key value which is still smaller than the current
     */
    private RBNode predecessor(RBNode node) {
        assert node != minNode;
        if (node.hasLeftChild()) {
            return subtreeMax(node.left);
        } else {
            while (node.relationToParent() == Direction.Left) {
                node = node.parent;
            }
            return node.parent;
        }
    }

    /**
     * private RBNode subtreeMin(RBNode node)
     * <p>
     * Finds the smallest value in the current subtree
     * Works at O(logn) where n is the number of nodes in the sub-tree
     * precondition: node != null
     *
     * @param node The head of the sub-tree on which to look for the minimum key
     * @return The node with the smallest key in the subtree. if node == nil, null is returned.
     */
    private RBNode subtreeMin(RBNode node) {
        if (node == nil) {
            return null;
        }
        while (node.hasLeftChild()) {
            node = node.left;
        }
        return node;
    }

    /**
     * private RBNode subtreeMax(RBNode node)
     * <p>
     * Finds the biggest value in the current subtree
     * Works at O(logn) where n is the number of nodes in the sub-tree
     * precondition: node != null
     *
     * @param node The head of the sub-tree on which to look for the maximum key
     * @return The node with the biggest key in the subtree. if node == nil, null is returned.
     */
    private RBNode subtreeMax(RBNode node) {
        if (node == nil) {
            return null;
        }
        while (node.hasRightChild()) {
            node = node.right;
        }
        return node;
    }

    /**
     * private RBNode root()
     * <p>
     * Returns the actual root of the tree (not the dummy root)
     * Works at O(1)
     *
     * @return The real root node of the tree
     */
    private RBNode root() {
        return rootDummy.left;
    }

    /**
     * public String min()
     * <p>
     * Returns the value of the item with the smallest key in the tree,
     * or null if the tree is empty
     * Works at O(1)
     *
     * @return The value for the node with the minimum key in the tree, or null if the tree is empty
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
     * Works at O(1)
     *
     * @return The value for the node with the maximum key in the tree, or null if the tree is empty
     */
    public String max() {
        if (minNode == null) {
            return null;
        }
        return maxNode.item;
    }

    /**
     * private class IndexedConsumer<T> implements Consumer<T>
     * <p>
     * Turns a Consumer<T> into a Consumer<T, Integer> where the int is a running counter
     */
    private class IndexedConsumer<T> implements Consumer<T> {

        int index;
        BiConsumer<T, Integer> base;

        /**
         * public IndexedConsumer(BiConsumer<T, Integer> baseFunction)
         * <p>
         * Constructor for the class
         *
         * @param baseFunction - The function to call each time we are called by the previous Consumer<T>
         */
        public IndexedConsumer(BiConsumer<T, Integer> baseFunction) {
            index = 0;
            base = baseFunction;
        }

        /**
         * public void accept(T arg)
         * <p>
         * Implements the Consumer<T> interface. Get called each time a new value is ready to be consumed,
         * passes it to the baseFunction together with the calling index
         *
         * @param arg The value to consume
         */
        public void accept(T arg) {
            base.accept(arg, index);
            index++;
        }
    }

    /**
     * private void walkPreOrder(RBNode node, Consumer<RBNode> consumer)
     * <p>
     * Applies the given function to all the nodes in the tree in pre-order
     * Works in O(n) where n is the number of nodes in the sub-tree under node
     *
     * @param node     The node to start the tree-walk from
     * @param consumer The function to run on each node
     */
    private void walkPreOrder(RBNode node, Consumer<RBNode> consumer) {
        walk(node, consumer, dummyConsumer, dummyConsumer);
    }

    /**
     * private void walkInOrder(RBNode node, Consumer<RBNode> consumer)
     * <p>
     * Applies the given function to all the nodes in the tree in order
     * Works in O(n) where n is the number of nodes in the sub-tree under node
     *
     * @param node     The node to start the tree-walk from
     * @param consumer The function to run on each node
     */
    private void walkInOrder(RBNode node, Consumer<RBNode> consumer) {
        walk(node, dummyConsumer, consumer, dummyConsumer);
    }

    /**
     * private void walkPostOrder(RBNode node, Consumer<RBNode> consumer)
     * <p>
     * Applies the given function to all the nodes in the tree in post-order
     * Works in O(n) where n is the number of nodes in the sub-tree under node
     *
     * @param node     The node to start the tree-walk from
     * @param consumer The function to run on each node
     */
    private void walkPostOrder(RBNode node, Consumer<RBNode> consumer) {
        walk(node, dummyConsumer, dummyConsumer, consumer);
    }

    /**
     * private void walk(RBNode node, Consumer<RBNode> consumerPre, Consumer<RBNode> consumerIn, Consumer<RBNode> consumerPost)
     * <p>
     * Walks the given subtree and applies the given functions in pre, post and in-order fashions
     * Works in O(n) where n is the number of nodes in the sub-tree under node
     *
     * @param node         The node to start the tree-walk from
     * @param consumerPre  The function to run on nodes in pre-order
     * @param consumerIn   The function to run on nodes in-order
     * @param consumerPost The function to run on nodes in post-order
     */
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
     *
     * @return All the keys for all the nodes in the tree
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
     *
     * @return All the values for all the nodes in the tree
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
     *
     * @return The number of elements in the tree
     */
    public int size() {
        return size;
    }

    /**
     * private Direction oppositeDirection(Direction direction)
     * <p>
     * Flips the given direction, used for making the insert and delete more symmetric
     * precondition: none
     * postcondition: none
     *
     * @param direction The direction to flip
     * @return The opposite direction from the one given
     */
    private Direction oppositeDirection(Direction direction) {
        return (direction == Direction.Left) ? Direction.Right : Direction.Left;
    }

    // Note - All the function from here on down are just for debugging or testing purpose.
    // Because of that they are package-private (so we could use them in RBTreeTest), but we don't
    // document them because they aren't required or needed for any external use

    RBNode select(int index) {
        RBNode node = minNode;
        for (int i = 0; i < index; i++) {
            node = successor(node);
        }
        return node;
    }

    int selectKey(int index) {
        return select(index).key;
    }

    int minKey() {
        assert minNode != null;
        return minNode.key;
    }

    int maxKey() {
        assert maxNode != null;
        return maxNode.key;
    }

    int rootKey() {
        return root().key;
    }

    void printTreeMinimal() {
        printTree(System.out, false, nil);
    }

    void printTree() {
        printTree(System.out);
    }

    void printTree(PrintStream stream) {
        printTree(stream, true, null);
    }

    void printTree(PrintStream stream, boolean printRootDummy, RBNode sentinel) {
        RBNode start = printRootDummy ? rootDummy : root();
        start.printTree(stream, sentinel);
    }

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
        assert rootDummy.parent == null : "Invalid parent for rootDummy";
        assert nil.color == Color.Black : "Invalid color for nil";
        assert nil.right == null && nil.left == null : "Invalid child for nil";
        assert nil.key == 0 : "Invalid key nil";
        assert nil.item == null : "Invalid item for nil";
        assert rootDummy.key == Integer.MAX_VALUE : "Invalid key for rootDummy";
        assert rootDummy.item == null : "Invalid item for rootDummy";

        checkSubtreeInvariants(root());

        TreeMap<Integer, String> map = toTreeMap();
        assert map.size() == size() : "Incorrect size";
        RBNode min = subtreeMin(root());
        RBNode max = subtreeMax(root());
        assert min == minNode : String.format("Incorrect minNode: %s != %s", min, minNode);
        assert max == maxNode : String.format("Incorrect minNode: %s != %s", max, maxNode);
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

    private class RBNode {

        RBNode parent;
        RBNode left;
        RBNode right;
        Color color;
        int key;
        String item;

        RBNode(RBNode parent, RBNode left, RBNode right, Color color, int key, String item) {
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.color = color;
            this.key = key;
            this.item = item;
        }

        RBNode getChild(Direction direction) {
            return (direction == Direction.Left) ? left : right;
        }

        void setChild(Direction direction, RBNode node) {
            if (direction == Direction.Left) {
                setLeft(node);
            } else {
                setRight(node);
            }
        }

        void rotate(Direction direction) {
            if (direction == Direction.Left) {
                rotateLeft();
            } else {
                rotateRight();
            }
        }

        void setLeft(RBNode node) {
            left = node;
            node.parent = this;
        }

        void setRight(RBNode node) {
            right = node;
            node.parent = this;
        }

        // Replace this node with another node and it's subtrees
        void transplant(RBNode node) {
            parent.setChild(relationToParent(), node);
        }

        //TODO: remove this?
        // Replace this node with another node, keeping this node's subtrees.
        void replace(RBNode node) {
            transplant(node);
            node.setLeft(left);
            node.setRight(right);
        }

        void rotateLeft() {
            RBNode oldRight = right;
            transplant(oldRight);
            setRight(oldRight.left);
            oldRight.setLeft(this);
        }

        void rotateRight() {
            RBNode oldLeft = left;
            transplant(oldLeft);
            setLeft(oldLeft.right);
            oldLeft.setRight(this);
        }

        boolean isRightChild() {
            return parent.right == this;
        }

        boolean isLeftChild() {
            return parent.left == this;
        }

        Direction relationToParent() {
            return isLeftChild() ? Direction.Left : Direction.Right;
        }

        RBNode getBrother() {
            Direction direction = relationToParent();
            return parent.getChild(oppositeDirection(direction));
        }

        RBNode getNephewNear() {
            return getBrother().getChild(relationToParent());
        }

        RBNode getNephewFar() {
            return getBrother().getChild(oppositeDirection(relationToParent()));
        }

        RBNode getUncle() {
            return parent.getBrother();
        }

        int childrenCount() {
            int count = 0;
            if (hasLeftChild()) count += 1;
            if (hasRightChild()) count += 1;
            return count;
        }

        boolean hasChildren() {
            return childrenCount() > 0;
        }

        boolean hasLeftChild() {
            return left != nil;
        }

        boolean hasRightChild() {
            return right != nil;
        }

        @Override
        public String toString() {
            if (this == nil) {
                return "nil";
            }
            if (this == rootDummy) {
                return "rootDummy";
            }
            return color == Color.Red ? String.format("<%d>", key) : "" + key;
        }

        // Printing adapted from http://stackoverflow.com/a/19484210
        void printTree(PrintStream out, RBNode sentinel) {
            if (right != sentinel) {
                right.printTree(out, sentinel, true, "");
            }
            out.println(toString());
            if (left != sentinel) {
                left.printTree(out, sentinel, false, "");
            }
        }

        void printTree(PrintStream out, RBNode sentinel, boolean isRight, String indent) {
            if (right != sentinel) {
                right.printTree(out, sentinel, true, indent + (isRight ? "        " : " |      "));
            }
            out.print(indent);
            if (isRight) {
                out.print(" /");
            } else {
                out.print(" \\");
            }
            out.print("----- ");
            out.println(toString());
            if (left != sentinel) {
                left.printTree(out, sentinel, false, indent + (isRight ? " |      " : "        "));
            }
        }
    }

}

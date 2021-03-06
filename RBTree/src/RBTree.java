/**
 * Red-Black Tree Implementation
 *
 * Authors: <Name1> <ID1> <USERNAME1>
 *          <Name2> <ID2> <USERNAME2>
 *
 */

import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * RBTree
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 */
public class RBTree {

    /**
     * A consumer for RBNodes that does nothing. Is used with inside the different version
     * of the walk function
     */
    static Consumer<RBNode> dummyConsumer = (a) -> {
    };

    /**
     * Represents a possible color for a node in the red black tree.
     */
    private enum Color {
        Black,
        Red,
    }

    /**
     * An enum used to represent a child's direction for a node in the tree.
     * The enum is used in order to make the Insert and Delete function direction-agnositc
     * in order to share code between the symmetric sides for edge-cases
     */
    private enum Direction {
        Left,
        Right,
    }

    /**
     * A dummy root used (just like the dummy sentinel in Cormen)
     */
    private RBNode rootDummy;
    /**
     * A member that holds the number of nodes in the tree
     */
    private int size;
    /**
     * Holds the node with the minimum value - for optimizing the "min" function
     */
    private RBNode minNode;
    /**
     * Holds the node with the maximum value - for optimizing the "max" function
     */
    private RBNode maxNode;
    /**
     * A dummy value used as a NULL child for all the leaves in the tree
     */
    private RBNode nil;


    /**
     * A default constructor for the RBTree class
     * Works at O(1).
     * precondition: none
     * postcondition: none
     */
    public RBTree() {
        // Create dummy node
        rootDummy = new RBNode(null, null, null, Color.Black, Integer.MAX_VALUE, null);

        nil = new RBNode(rootDummy, null, null, Color.Black, 0, null);
        rootDummy.left = nil;
        rootDummy.right = nil;

        minNode = null;
        maxNode = null;

        size = 0;
    }

    /**
     * A constructor for the tree that is initialized with a list of key-value pairs
     * * Works at O(n) where n is the number of key-values pairs in the iterable.
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
     * A constructor for the tree that is initialized with a given map for the keys and values
     * * Works at O(n) where n is the number of key-values pairs in the map.
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
     * Inserts all the items in the map into the tree
     * * Works at O(n) where n is the number of key-values pairs in the map.
     * precondition: map != null
     * postcondition: none
     *
     * @param map A map keys and values to insert into the tree
     */
    public void insertItems(Map<Integer, String> map) {
        insertItems(map.entrySet());
    }

    /**
     * Inserts all the items in the list into the tree.
     * Works at O(n) where n is the number of key-values pairs in the iterable.
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
     * Inserts all the elements in the tree into the given map
     * Works at O(n) where n is the number of nodes in the tree
     * precondition: map != null
     * postcondition: none
     *
     * @param map The map to insert all the tree's elements into
     */
    public void toMap(Map<Integer, String> map) {
        walkPreOrder(root(), (node) -> map.put(node.key, node.item));
    }

    /**
     * Returns a representation of the tree as a native java TreeMap
     * Works at O(n) where n is the number of nodes in the tree
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
     * Returns true if and only if the tree is empty
     *
     * @return Says whether the tree is empty or not
     */
    public boolean empty() {
        return size == 0;
    }

    /**
     * Returns the value of an item with key k if it exists in the tree
     * otherwise, returns null
     * Works in O(logn) where n is the number of elements in the tree
     *
     * @param k The key by which to look up the value
     * @return A string if the matching key is found, or null otherwise
     */
    public String search(int k) {
        RBNode node = searchNode(k);
        return node == null ? null : node.item;

    }

    /**
     * Looks up a RBNode using a search key
     * Works in O(logn) where n is the number of nodes in the tree
     *
     * @param k The key by which to look up the node
     * @return The holding node if it's found, or null otherwise
     */
    private RBNode searchNode(int k) {
        RBNode node = getPositionByKey(k);
        return node.key != k ? null : node;
    }

    /**
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
     * Inserts an item with key k and value v to the red black tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were necessary.
     * returns -1 if an item with key k already exists in the tree.
     * Works at O(logn)
     *
     * @param k The key of the new node to insert into the tree
     * @param v The new value to insert into the tree
     * @return The number of node-color changes that happened during the insert, or -1 if an error occurs
     */
    public int insert(int k, String v) {
        if (k == rootDummy.key) {
            return -1;
        }
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
     * Fixes the tree to retain it's red-black properties after a node was inserted
     * Works at O(logn)
     *
     * @param node The node from which to start the fix
     * @return The number of color changes made to nodes in order to maintain the red-black property
     */
    private int insertFixup(RBNode node) {
        int colorSwitchCount = 0;

        while (node.parent.color == Color.Red) {
            Direction direction = node.parent.relationToParent();
            Direction opposite = oppositeDirection(direction);

            // Fix red-rule violation
            RBNode uncle = node.getUncle();
            if (uncle.color == Color.Red) {
                // Case 1: parent and uncle are red.
                // Swap colors between black grandparent and its red children
                colorSwitchCount += setColor(node.parent, Color.Black);
                colorSwitchCount += setColor(uncle, Color.Black);
                colorSwitchCount += setColor(node.parent.parent, Color.Red);
                node = node.parent.parent;
                // Note: now the the red-rule violation is either solved, or moved two levels up.
            } else if (node.relationToParent() == opposite) {
                // Case 2: parent is red, uncle is black, and node is between parent and grandparent.
                // Move to parent and rotate left.
                // This doesn't improve our situation regarding the red-rule, but it brings us to Case 3.
                node = node.parent;
                node.rotate(direction);
            } else {
                // Case 3: parent is red, uncle is black, and parent is between node and grandparent.
                // Swap parent and grandparent colors, (fixing the red rule, but violate the black-rule)
                colorSwitchCount += setColor(node.parent, Color.Black);
                colorSwitchCount += setColor(node.parent.parent, Color.Red);
                // Rotate grandparent to fix the black-rule.
                node.parent.parent.rotate(opposite);
                // Note the red-rule and black-rule are now ok, and the loop will terminate.
            }
        }

        // Set root to black if needed
        colorSwitchCount += setColor(root(), Color.Black);

        return colorSwitchCount;
    }

    /**
     * Deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of color switches, or 0 if no color switches were needed.
     * returns -1 if an item with key k was not found in the tree.
     * Works at O(logn)
     *
     * @param k The key who's node we want to delete
     * @return The number of node-color changes that happened during the insert, or -1 if an error occurs
     */
    public int delete(int k) {
        if (k == rootDummy.key) {
            return -1;
        }
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

    /**
     * Deletes a node from the RBTree
     * * Works at O(logn)
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
     * Fixes the red-black tree to maintain it's red-black properties after a node was deleted
     * Works at O(logn)
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
                color_switches += setColor(brother, Color.Black);
                color_switches += setColor(node.parent, Color.Red);
                node.parent.rotate(direction);
                assert node.getBrother().color == Color.Black;
                // Note: now node's brother is surely black, and we will get to case 2/3/4.
            } else if (node.getNephewNear().color == Color.Black && node.getNephewFar().color == Color.Black) {
                // Case 2: brother is black, and both nephews are black.
                // Set brother's color to red, to make the black-height of node and brother the same.
                color_switches += setColor(brother, Color.Red);
                node = node.parent;
                // Now if the parent is black, we move the issue to the parent.
                // If it is red, then the loop terminates, and it will be made black afterwards, and the fixing is over.
            } else if (node.getNephewFar().color == Color.Black) {
                // Case 3: brother is black, far nephew is black, and near nephew is red.
                assert node.getNephewNear().color == Color.Red;
                // Switch brother and near-nephew's colors, and rotate brother right.
                // This keeps the invariants of node's brother subtree.
                color_switches += setColor(node.getNephewNear(), Color.Black);
                color_switches += setColor(brother, Color.Red);
                brother.rotate(opposite);
                // Now node's brother is red, which bring us to case 4.
            } else {
                // Case 4: brother is black, far nephew is red.
                // Change parent, brother, far-nephew colors, and rotate parent towards node.
                // This is a terminal case.
                color_switches += setColor(brother, node.parent.color);
                color_switches += setColor(node.parent, Color.Black);
                color_switches += setColor(node.getNephewFar(), Color.Black);
                node.parent.rotate(direction);
                // Set node to root, to terminate the loop, and possibly update root's color afterwards.
                node = root();
            }
        }
        // In some of cases (2,4) we might terminate when node is red.
        // in that case, changing node to black will restore the black-rule.
        color_switches += setColor(node, Color.Black);

        return color_switches;
    }

    /**
     * Sets the node color to the new color, and counts weather or not it was a color change or not.
     * The function is used to count color changes which actually change color, as opposed to "might" change the color
     *
     * @param node The node who's color we are trying to change
     * @return 1 if the node's color was actually changed, 0 otherwise
     */
    private int setColor(RBNode node, Color color) {
        if (node.color != color) {
            node.color = color;
            return 1;
        }
        return 0;
    }

    /**
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
     * Returns the actual root of the tree (not the dummy root)
     * Works at O(1)
     *
     * @return The real root node of the tree
     */
    private RBNode root() {
        return rootDummy.left;
    }

    /**
     * Returns the value of the item with the smallest key in the tree,
     * or null if the tree is empty
     * Works at O(1)
     *
     * @return The value for the node with the minimum key in the tree, or null if the tree is empty
     */
    public String min() {
        return minNode == null ? null : minNode.item;
    }

    /**
     * Returns the value of the item with the largest key in the tree,
     * or null if the tree is empty
     * Works at O(1)
     *
     * @return The value for the node with the maximum key in the tree, or null if the tree is empty
     */
    public String max() {
        return minNode == null ? null : maxNode.item;
    }

    /**
     * Turns a Consumer<T> into a Consumer<T, Integer> where the int is a running counter
     */
    private class IndexedConsumer<T> implements Consumer<T> {

        int index;
        BiConsumer<T, Integer> base;

        /**
         * Constructor for the class
         *
         * @param baseFunction - The function to call each time we are called by the previous Consumer<T>
         */
        public IndexedConsumer(BiConsumer<T, Integer> baseFunction) {
            index = 0;
            base = baseFunction;
        }

        /**
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
     * Returns the number of nodes in the tree.
     * precondition: none
     * postcondition: none
     *
     * @return The number of elements in the tree
     */
    public int size() {
        return size;
    }

    /**
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

    /**
     * All the function from here on down are just for debugging or testing purpose.
     * Because of that they are package-private (so we could use them in RBTreeTest), but we don't
     * document them because they aren't required or needed for any external use
     */

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

    /**
     * Represents an internal Node in the tree
     * All members and methods in this class aren't marked as either public or private
     * in order to allow them to be freely used in the RBTree methods
     */
    private class RBNode {

        RBNode parent;
        RBNode left;
        RBNode right;
        Color color;
        int key;
        String item;

        /**
         * RBNode(RBNode parent, RBNode left, RBNode right, Color color, int key, String item) {
         * A constructor for the RBNode structure that initializes all the members using the given paramenters
         *
         * @param parent The node which to set as parent
         * @param left   The node to set as the left child
         * @param right  The node to set as the right child
         * @param color  The initial color for the node
         * @param key    The node's key
         * @param item   The item to set for the node
         */
        RBNode(RBNode parent, RBNode left, RBNode right, Color color, int key, String item) {
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.color = color;
            this.key = key;
            this.item = item;
        }

        /**
         * Returns the child from the given direction. Used to make direction agnostic code
         * Works in O(1)
         *
         * @param direction The direction of the child to return
         * @return The child node in the given direction
         */
        RBNode getChild(Direction direction) {
            return (direction == Direction.Left) ? left : right;
        }

        /**
         * Set the child from the given direction. Used to make direction agnostic code
         * Works in O(1)
         *
         * @param direction The direction of the child to return
         * @param node      The node to set as the child
         */
        void setChild(Direction direction, RBNode node) {
            if (direction == Direction.Left) {
                setLeft(node);
            } else {
                setRight(node);
            }
        }

        /**
         * Rotates the node in the given direction (like done in Cormen to restore the RB properties of a tree)
         * Works in O(1)
         *
         * @param direction The direction in which to rotate the nodes
         */
        void rotate(Direction direction) {
            if (direction == Direction.Left) {
                rotateLeft();
            } else {
                rotateRight();
            }
        }

        /**
         * Sets the left child as the given node
         * Works in O(1)
         *
         * @param node The node to set as the left child
         */
        void setLeft(RBNode node) {
            left = node;
            node.parent = this;
        }

        /**
         * Sets the right child as the given node
         * Works in O(1)
         *
         * @param node The node to set as the right child
         */
        void setRight(RBNode node) {
            right = node;
            node.parent = this;
        }

        /**
         * Implements the transplant function (as described in Cormen)
         * Works in O(1)
         *
         * @param node The node to transplant
         */
        void transplant(RBNode node) {
            parent.setChild(relationToParent(), node);
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

        /**
         * All the methods from here down are just to make the code clearer
         * and are all trivial, therefore there is no need to document them thoroughly
         */

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

        /**
         * The methods from here on down are used for printing the tree
         * and aren't required for anything else than debugging, therefore
         * they too won't be thoroughly documented.
         */

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

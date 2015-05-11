import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    private RBNode minNode;
    private RBNode maxNode;

    //TODO: should these be fields of RBNode?
    static RBNode dummyNode;
    static RBNode nullNode;

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
        TreeMap<Integer, String> map = new TreeMap<>();
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
        RBNode position = getPositionByKey(k);
        if (position.key != k) {
        	return null;
        }
        return position.item;
    }
    
    private RBNode getPositionByKey(int k) {
        RBNode current = root;
        while (true) {
        	if (current.key == k) {
        		return current;
        	}
        	if (current.key > k) {
        		if (null == current.left) {
        			return current;
        		}
        		current = current.left;
        	}
        	else { // current.key < k
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
    		}
    		else {
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
     * <p/>
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
        size++;
        RBNode newNode = new RBNode();
        newNode.key = k;
        newNode.item = v;
        newNode.parent = parent;
        newNode.color = Color.Red;
        if (parent.key > newNode.key) {
        	parent.right = newNode;
        }
        else { // parent.key < newNode.key
        	parent.left = newNode;
        }
        return fixupTree(parent);
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
        return minNode.item;
    }

    /**
     * public String max()
     * <p/>
     * Returns the value of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        return maxNode.item;
    }

    private class IndexedConsumer<T> implements Consumer<T> 
    {
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
    	walk(node, consumer);
    	if (null != node.right) {
    		walk(node.right, consumer);
    	}
    }
    
    /**
     * public int[] keysToArray()
     * <p/>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
    	int[] keys = new int[size];
    	walk(root, new IndexedConsumer<RBNode>((node, index) -> keys[index++] = node.key));
    	return keys;
    }

    /**
     * public String[] valuesToArray()
     * <p/>
     * Returns an array which contains all values in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] valuesToArray() {
    	String[] items = new String[size];
    	walk(root, new IndexedConsumer<RBNode>((node, index) -> items[index++] = node.item));
    	return items;
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

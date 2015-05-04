/**
 *
 * RBTree
 *
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 *
 */

public class RBTree {

  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() {
    return false; // to be replaced by student code
  }

 /**
   * public String search(int k)
   *
   * returns the value of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {
	return "42";  // to be replaced by student code
  }

  /**
   * public int insert(int k, String v)
   *
   * inserts an item with key k and value v to the red black tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of color switches, or 0 if no color switches were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String v) {
	  return 42;	// to be replaced by student code
   }

  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of color switches, or 0 if no color switches were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   return 42;	// to be replaced by student code
   }

   /**
    * public String min()
    *
    * Returns the value of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   return "42"; // to be replaced by student code
   }

   /**
    * public String max()
    *
    * Returns the value of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   return "42"; // to be replaced by student code
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
        int[] arr = new int[42]; // to be replaced by student code
        return arr;              // to be replaced by student code
  }

  /**
   * public String[] valuesToArray()
   *
   * Returns an array which contains all values in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] valuesToArray()
  {
        String[] arr = new String[42]; // to be replaced by student code
        return arr;                    // to be replaced by student code
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   return 42; // to be replaced by student code
   }

  /**
   * public class RBNode
   *
   * If you wish to implement classes other than RBTree
   * (for example RBNode), do it in this file, not in 
   * another file.
   * This is an example which can be deleted if no such classes are necessary.
   */
  public class RBNode{

  }

}
  


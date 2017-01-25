package edu.ufl.ads.proj.rbtree.generic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import edu.ufl.ads.proj.rbtree.Color;

/**
 * Created by hsitas444 on 3/16/2016.
 */

/**
 * Implementation of RedBlack Tree
 * @param <K> Generic type of key
 * @param <V> Generic type of Value
 */
public class RBTree<K extends Comparable<K>, V>{

    RBNode<K, V> root = RBExternalNode.getInstance();
    RBNode<K, V> exNode = RBExternalNode.getInstance();

    Deserializer<K> keyDeserializer;
    Deserializer<V> valueDeserializer;

    public RBTree(Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer){
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
    }

    /**
     * Returns the root node of the red black tree
     * @return The root node of red black tree
     */
    public RBNode<K, V> getRoot(){
        return root;
    }

    /**
     * Inserts the key-value pair into the red black tree
     * @param key Key to be inserted.
     * @param value Value to be inserted.
     * @return The the newly inserted RBNode
     * @throws DuplicateKeyException if there exists a node with same key in the tree
     */
    public RBNode<K,V> insert(K key, V value) throws DuplicateKeyException{
        RBNode<K, V> newNode = new RBNode<>(key, value);
        RBNode<K, V> parent = root.parent;
        RBNode<K, V> curr = root;
        while(curr.isInternalNode()){
            parent = curr;
            int compare = newNode.key.compareTo(curr.key);
            if(compare == 0)
                throw new DuplicateKeyException(curr);
            else if(compare < 0) // if key of newNode less than key of curr node.
                curr = curr.left;
            else curr = curr.right;
        }
        newNode.parent = parent;
        if(parent == null || parent.isExternalNode()) {
            root = newNode;
            root.parent = exNode;
        }
        else if (newNode.key.compareTo(parent.key) < 0){ //if key of newNode less than key of parent node.
            parent.left = newNode;
        } else parent.right = newNode;
        newNode.left = newNode.right = exNode;
        newNode.color = Color.RED;
        insertFixColors(newNode);

        return newNode;
    }

    /**
     * Fix the colors of the nodes such the properties of RedBlackTree are obeyed.
     * @param node RedBlack node where the color fixing needs to carried out.
     */
    private void insertFixColors(RBNode<K,V> node){
        RBNode<K,V> parentsSibling;
        while(node.parent.isRed()){
            if(node.parent == node.parent.parent.left) { // node's parent is left child of grand parent, case:LYz from slides
                parentsSibling = node.parent.parent.right;
                if(parentsSibling.isRed()){
                    node.parent.color = Color.BLACK;
                    parentsSibling.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else{
                    if(node == node.parent.right){ // case LRb : first perform Left rotation with respect to parent to get LLb, then right rotation
                        node = node.parent;
                        leftRotate(node);
                    }
                    node.parent.color = Color.BLACK; //case LLb
                    node.parent.parent.color = Color.RED;
                    rightRotate(node.parent.parent);
                }


            }else {// node's parent is right child of grand parent, case:RYz from slides
                parentsSibling = node.parent.parent.left;
                if(parentsSibling.isRed()){
                    node.parent.color = Color.BLACK;
                    parentsSibling.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if(node == node.parent.left){ // case RLb : first perform right rotation with respect to parent then left rotation
                        node = node.parent;
                        rightRotate(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    leftRotate(node.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    /**
     * Returns the maximum depth of the red black tree
     * @return The maximum depth of the red black tree
     */
    public int maxDepth(){
        return maxDepth(root, 0);
    }

    /**
     * Returns the minimum value node in the subtree rooted at node
     * @param node Root of the subtree
     * @return The minimum value node in the subtree rooted at node
     */
    private RBNode<K,V> minValueNode(RBNode<K,V> node){
        while(node.left.isInternalNode())
            node = node.left;
        return node;
    }

    /**
     * Returns the maximum value node in the subtree rooted at node
     * @param node Root of the subtree
     * @return The maximum value node in the subtree rooted at node
     */
    private RBNode<K,V> maxValueNode(RBNode<K,V> node){
        while(node.right.isInternalNode())
            node = node.right;
        return node;
    }

    /**
     * Recursive function to determine the maxDepth of the tree
     * @param node Root of the subtree
     * @param count currentDepth
     * @return MaxDepth of the subtree
     */
    private int maxDepth(RBNode<K,V> node, int count){
        if(node.isExternalNode())
            return count;
        count++;
        return Math.max(maxDepth(node.left,count),maxDepth(node.right,count));
    }

    /**
     * Delete the given RBNode
     * @param delNode Node to be deleted from redblack tree
     * @return Node deleted from reb black tree
     */
    public RBNode<K, V> delete(RBNode<K, V> delNode){
        if(delNode.isInternalNode()){
            RBNode<K, V> node = delNode;
            RBNode<K, V> fixNode;
            Color nodeColor = node.color;
            if(delNode.left.isExternalNode()){ //If the left child of the node is externalNode, then make the right child the replacement node
                fixNode = delNode.right;
                replaceNode(delNode, fixNode);
            } else if (delNode.right.isExternalNode()){//If the right child of the node is externalNode, then make the left child the replacement node
                fixNode = delNode.left;
                replaceNode(delNode, fixNode);
            } else {
                //If neither left child nor right child are externalNode, then find the minimum node in right subtree and make that the replacement node
                node = minValueNode(delNode.right);
                nodeColor = node.color;
                fixNode = node.right;
                if(node.parent == delNode) // If the least node in the right subtree is the direct right child of node being deleted
                    fixNode.parent = node;
                else {
                    //Otherwise we first make the least node in the right subtree the direct right child of node being deleted.
                    //By replacing least node with the right child of the least node.
                    replaceNode(node, node.right);
                    node.right = delNode.right;
                    node.right.parent = node;
                }
                replaceNode(delNode, node);
                node.left = delNode.left;
                node.left.parent = node;
                node.color = delNode.color;
            }
            if(nodeColor == Color.BLACK) //Call fix nodes only if the color of node being deleted is black
                deleteFixColors(fixNode);
            exNode.parent = null;
            exNode.right = null;
            exNode.left = null;
            exNode.color = Color.RED;
        }
        return delNode;
    }

    public RBNode<K, V> delete(K key){
        RBNode<K, V> delNode = findNode(key);
        return delete(delNode);
    }

    /**
     * Fix the colors for the sub tree rooted at node, which would have failed red black properties
     * @param node Root of the subtree where the properties would have failed red black properties
     */
    private void deleteFixColors(RBNode<K, V> node){
        RBNode<K, V> sibling;
        while(node != root && node.isBlack()){
            if(node == node.parent.left){ //If the node is left child, counter operations performed in the corresponding else block
                sibling = node.parent.right;
                if(sibling.isRed()){
                    //Case 1: Sibling is Red, means that sibling must have a black child. So, we color sibling black and the parent red
                    //and leftrotate with respect to the parent to convert to case 2,3,4
                    sibling.color = Color.BLACK;
                    sibling.parent.color = Color.RED;
                    leftRotate(node.parent);
                    sibling = node.parent.right;
                }
                if(sibling.left.isBlack() && sibling.right.isBlack()){
                    //case 2: Both the siblings children are black, then color sibling with red and
                    //push the coloring fixing problem to the parent.
                    sibling.color = Color.RED;
                    node = node.parent;
                } else {
                    //case 3: If sibling is black and his right child is black and left child is red
                    //Switch the colors of sibling and left child of sibling and rotate right w.r.t sibling
                    //Note that we don't violate any of the red black properties while doing this.
                    //The new sibling now has left child black and right child red, which is case 4
                    if(sibling.right.isBlack()){
                        sibling.left.color = Color.BLACK;
                        sibling.color = Color.RED;
                        rightRotate(sibling);
                        sibling = node.parent.right;
                    }
                    //case 4: When sibling is black, sibling's right child is red.
                    //we can eliminate the black deficiency at node making sibling color same as its parent
                    //and making the parent black and sibling's right child black
                    //and rotating left w.r.t parent without violating any of the red black properties.
                    sibling.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    sibling.right.color = Color.BLACK;
                    leftRotate(node.parent);
                    node = root; // done fixing colors
                }
            } else{//If the node is right child, exact opposite operations when compared to corresponding "if" block above
                sibling = node.parent.left;
                if(sibling.isRed()){
                    sibling.color = Color.BLACK;
                    sibling.parent.color = Color.RED;
                    rightRotate(node.parent);
                    sibling = node.parent.left;
                }
                if(sibling.left.isBlack() && sibling.right.isBlack()){
                    sibling.color = Color.RED;
                    node = node.parent;
                } else {
                    if(sibling.left.isBlack()){
                        sibling.right.color = Color.BLACK;
                        sibling.color = Color.RED;
                        leftRotate(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    sibling.left.color = Color.BLACK;
                    rightRotate(node.parent);
                    node = root; // done fixing colors
                }
            }
        }
        node.color = Color.BLACK;
    }

    public RBNode<K,V> findNode(K key){
        return findNode(root, key);
    }

    /**
     * Recursive function to find a node with given key in the sub tree rooted by
     * currNode
     * @param currNode
     * @param key
     * @return
     */
    private RBNode<K,V> findNode(RBNode<K,V> currNode, K key){
        if(currNode.isExternalNode())
            return currNode;
        int compare = key.compareTo(currNode.key);
        if(compare == 0)
            return currNode;
        else if(compare>0) //If the key is greater than the currNode
            return findNode(currNode.right, key);
        else return findNode(currNode.left, key); //If the key is lesser than the currNode
    }

    /**
     * Initialize the RedBlack tree using a list of sorted KeyValue pairs, by building the complete Binary Search tree in inorder
     * and coloring nodes such that only last level internal nodes are red and the rest are black.
     * Time Complexity = O(n) { T(n) = 2T(n/2) + O(1) }
     * @param reader Reader to a stream which contains key-value pairs sorted by the keys field.
     *                Each key-value pair is on a separate line; keys and values are separated by a space.
     * @param size Number of key-value pairs
     */
    public void initialize(BufferedReader reader, int size) throws IOException{
        int start = 0;
        int end = size-1;
        int mid = start + (end - start)/2;
        RBNode<K,V> left;
        left = initialize(0, mid-1, reader);
        String tokens[] = reader.readLine().split(" ");
        root = new RBNode<>(keyDeserializer.parse(tokens[0]), valueDeserializer.parse(tokens[1]));
        root.color = Color.BLACK;
        root.left = left;
        if(root.left.isInternalNode())
            left.parent = root;
        root.right = initialize(mid+1, end, reader);
        if(root.right.isInternalNode())
            root.right.parent = root;
        int maxDepth;
        if(size == 1)
            maxDepth = 1;
        else maxDepth= (int)Math.ceil(Math.log(size)/Math.log(2));
        //Note that we can use the lg(size) formula to find the maxDepth as we build complete BST using algorithm above.

        //We color all the nodes at maxDepth as red. This results in all the nodes obeying red black properties.
        if(maxDepth > 1)
            recolorMaxDepth(maxDepth);

    }

    /**
     * Recursive initialize by taking the key value pairs from the reader
     * @param start Start index of the sub problem
     * @param end End index of the sub problem
     * @param reader Reader to read the input key value
     * @return Root node of the sub tree that is build.
     * @throws IOException When failure in reading occurs
     */
    private RBNode<K,V> initialize(int start, int end, BufferedReader reader) throws IOException{
        //While initializing we build a complete binary search tree from the sorted keys and
        //assign black color to all nodes and later make all nodes at max depth red
        if(start>end)
            return RBExternalNode.getInstance();
        int mid = start + (end - start)/2;
        RBNode<K,V> left;
        left = initialize(start, mid-1, reader);
        String tokens[] = reader.readLine().split(" ");
        RBNode<K,V> node = new RBNode<>(keyDeserializer.parse(tokens[0]), valueDeserializer.parse(tokens[1]));
        node.color = Color.BLACK;
        node.left = left;
        if(node.left.isInternalNode())
            node.left.parent = node;
        node.right = initialize(mid+1, end, reader);
        if(node.right.isInternalNode())
            node.right.parent = node;
        return node;


    }

    /**
     * Recolor all nodes at a given depth to red
     * @param depth The depth at which nodes should be colored to red
     */
    private void recolorMaxDepth(int depth) {
        recolorMaxDepth(root, 0, depth);
    }

    /**
     * Recolor all nodes at a given depth to red
     * @param node Current node
     * @param currDepth Current Depth
     * @param depth The depth at which nodes should be colored to red
     */
    private void recolorMaxDepth(RBNode<K,V> node, int currDepth, int depth) {
        currDepth++;
        if(currDepth == depth) {
            if(node.isInternalNode())
                node.color = Color.RED;
        }
        else{
            recolorMaxDepth(node.left, currDepth, depth);
            recolorMaxDepth(node.right, currDepth, depth);
        }
    }




    /**
     *  Rotates left(counter clockwise) with respect to x
     *      |                      |
     *      x                      y
     *     / \                    / \
     *    a  y      to           x  c
     *      / \                 / \
     *     b   c               a   b
     */
    private void leftRotate(RBNode<K,V> x){
        RBNode<K,V> y = x.right; //y is the right child of x
        x.right = y.left; //left child y becomes the right child of x.
        if(y.left.isInternalNode()) //update the parent of y.left only if it an internal node
            y.left.parent = x;
        y.parent = x.parent; //update the parent of y with parent of x
        if(x.parent.isExternalNode()) // If x was the root, then y becomes the root
            root = y;
        else if(x == x.parent.left)//If x was the left child, update the left child of x's parent to y
            x.parent.left = y;
        else x.parent.right = y;//If x was the right child, update the right child of x's parent to y
        y.left = x;
        x.parent = y;
    }

    /**
     *  Rotates right(clockwise) with respect to x
     *      |                      |
     *      x                      y
     *     / \                    / \
     *    y  c      to           a  x
     *   / \                       / \
     *  a  b                      b   c
     */
    private void rightRotate(RBNode<K,V> x){
        RBNode<K,V> y = x.left; //y is the left child of x
        x.left = y.right; //right child y becomes the left child of x.
        if(y.right.isInternalNode()) //update the parent of y.right only if it an internal node
            y.right.parent = x;
        y.parent = x.parent; //update the parent of y with parent of x
        if(x == root) // If was the root, then y becomes the root
            root = y;
        else if(x == x.parent.left)//If x was the left child, update the left child of x's parent to y
            x.parent.left = y;
        else x.parent.right = y;//If x was the right child, update the right child of x's parent to y
        y.right = x;
        x.parent = y;
    }

    /**
     * Makes pointer adjustments of the node being deleted with the replacement node
     * @param delNode Node being deleted
     * @param replaceNode Replacement node
     */
    private void replaceNode(RBNode<K,V> delNode, RBNode<K,V> replaceNode){
        if(delNode == root)
            root = replaceNode;
        else if(delNode == delNode.parent.left)
            delNode.parent.left = replaceNode;
        else delNode.parent.right = replaceNode;
        replaceNode.parent = delNode.parent;
        //If replaceNode is external node, parent of the external node is temporarily set.
        //The parent node of external node is reset after deleteFixColors
    }



    /**
     * Utility method to verify the properties of the RB tree
     * @return true if all the properties of RB tree hold, otherwise false;
     */
    public boolean verifyRBProperties(){
        int numBNodes = countBNodesToExNode();
        boolean bNodeProperty = verifyBNodeProperty(root,0,numBNodes);
        boolean rNodeProperty = verifyRedNodeProperty(root);
        boolean exNodeProperty = exNode.color == Color.BLACK && exNode.parent == null && exNode.left == null && exNode.right == null;
        boolean rootNodeProperty = root.isBlack();
        return rNodeProperty && bNodeProperty && exNodeProperty && rootNodeProperty;

    }

    /**
     * Recursive function to verify no consecutive red node property
     * @param node Current node whose no consecutive red node property needs to be verified.
     * @return true if the property holds, otherwise false;
     */
    private boolean verifyRedNodeProperty(RBNode<K, V> node){
        if(node.isInternalNode()){
            if(node.isRed() && node.parent.isRed())
                return false;
            else return verifyRedNodeProperty(node.left) && verifyRedNodeProperty(node.right);
        } else return true;

    }

    /**
     * Verifies the property that number of black nodes in the paths from root to all external nodes is same.
     * @param node Current node
     * @param currNumBNodes Number of black nodes in the path from root to current node
     * @param numBNodes Number of black nodes from root till external nodes
     * @return
     */
    private boolean verifyBNodeProperty(RBNode<K, V> node, int currNumBNodes, int numBNodes) {
        if(currNumBNodes >= numBNodes) {
            return false;
        }
        if(node.isExternalNode()){
            if(currNumBNodes+1 == numBNodes)
                return true;
            else {
                return false;
            }
        }
        else {
            currNumBNodes = node.isBlack()? currNumBNodes+1 : currNumBNodes;
            return verifyBNodeProperty(node.left, currNumBNodes, numBNodes)
               && verifyBNodeProperty(node.right, currNumBNodes, numBNodes);
        }
    }

    private int countBNodesToExNode(){
        RBNode curr = root;
        int nBNodes = 0;
        while(curr.isInternalNode()){
            if(curr.isBlack())
                nBNodes++;
            curr = curr.left;
        }
        //Count the color of external node as well.
        nBNodes++;
        return nBNodes;
    }

    private void printTree(RBNode<K, V> node,PrintStream out) throws IOException {
        if (node.right != null) {
            printTree(node.right, out, true, "");
        }
        printNodeValue(node, out);
        if (node.left != null) {
            printTree(node.left, out, false, "");
        }
    }
    private void printNodeValue(RBNode<K, V> node, PrintStream out) throws IOException {
        if(node.key == null)
            out.print("extNode//B");
        else out.print(node.key.toString()+node.color);
        out.println();
    }

    public void printTree(){
        try {
            printTree(root, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // use string and not stringbuffer on purpose as we need to change the indent at each recursion
    private void printTree(RBNode<K, V> node, PrintStream out, boolean isRight, String indent) throws IOException {
        if (node.right != null) {
            printTree(node.right, out, true, indent + (isRight ? "        " : " |      "));
        }
        out.print(indent);
        if (isRight) {
            out.print(" /");
        } else {
            out.print(" \\");
        }
        out.print("----- ");
        printNodeValue(node,out);
        if (node.left != null) {
            printTree(node.left, out, false, indent + (isRight ? " |      " : "        "));
        }
    }


}

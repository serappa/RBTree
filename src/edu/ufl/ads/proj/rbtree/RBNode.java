package edu.ufl.ads.proj.rbtree;

/**
 * Created by hsitas444 on 3/16/2016.
 */

/**
 * Class to represent a node in RedBlack Tree
 */
public class RBNode {
    int key;
    int value;
    RBNode parent;
    RBNode left;
    RBNode right;
    Color color;
    RBNode(int key, int value){
        this.key = key;
        this.value = value;
        this.color = Color.RED;
        this.parent = RBExternalNode.getInstance();
        this.left = RBExternalNode.getInstance();
        this.right = RBExternalNode.getInstance();
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getKey() {
        return key;

    }

    public int getValue() {
        return value;
    }

    public RBNode getParent() {
        return parent;
    }

    public Color getColor() {
        return color;
    }

    public RBNode getRightChild() {
        return right;
    }

    public RBNode getLeftChild() {
        return left;
    }

    public boolean isRed(){
        return !isBlack();
    }

    public boolean isBlack(){
        return color.equals(Color.BLACK);
    }

    public boolean isExternalNode(){
        return false;
    }

    public boolean isInternalNode(){
        return !isExternalNode();
    }


}

/**
 * Class to represent external nodes of RedBlack tree
 */
class RBExternalNode extends RBNode {
    private static RBExternalNode _instance = new RBExternalNode();
    public static RBExternalNode getInstance(){
        return _instance;
    }
    @Override
    public boolean isExternalNode() {
        return true;
    }
    @Override
    public boolean isBlack() {
        return true;
    }
    private RBExternalNode(){
        super(0, 0);
        this.color = Color.BLACK;
    }
}


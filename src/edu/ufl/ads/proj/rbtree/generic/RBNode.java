package edu.ufl.ads.proj.rbtree.generic;

/**
 * Created by hsitas444 on 3/16/2016.
 */

import edu.ufl.ads.proj.rbtree.Color;

/**
 * Class to represent a node in redblack tree
 * @param <K> Type of key class which extends Comparable<K>
 * @param <V> Type of value class
 */
public class RBNode<K extends Comparable<K>, V> {
    K key;
    V value;
    RBNode<K,V> parent;
    RBNode<K,V> left;
    RBNode<K,V> right;
    Color color;
    RBNode(K key, V value){
        this.key = key;
        this.value = value;
        this.color = Color.RED;
        this.parent = RBExternalNode.getInstance();
        this.left = RBExternalNode.getInstance();
        this.right = RBExternalNode.getInstance();
    }

    public void setValue(V value) {
        this.value = value;
    }

    public K getKey() {
        return key;

    }

    public V getValue() {
        return value;
    }

    public RBNode<K, V> getParent() {
        return parent;
    }

    public Color getColor() {
        return color;
    }

    public RBNode<K, V> getRightChild() {
        return right;
    }

    public RBNode<K, V> getLeftChild() {
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
class RBExternalNode<K extends Comparable<K>,V> extends RBNode<K,V>{
    private static RBExternalNode _instance = new RBExternalNode<>();
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
        super(null, null);
        this.color = Color.BLACK;
    }
}


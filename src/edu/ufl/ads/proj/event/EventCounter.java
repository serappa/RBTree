package edu.ufl.ads.proj.event;

import edu.ufl.ads.proj.rbtree.DuplicateKeyException;
import edu.ufl.ads.proj.rbtree.RBNode;
import edu.ufl.ads.proj.rbtree.RBTree;



import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by hsitas444 on 3/17/2016.
 */

/**
 * EventCounter class that implements the functionality given in the project
 */
public class EventCounter {
    private RBTree rbTree;
    public EventCounter(){
        rbTree = new RBTree();
    }

    /**
     * Initialize the event counter, by reading the eventId and count from the reader
	 * Complexity: O(n)
     * @param reader Reader to read the input key-value pair
     * @param size Number of key value pairs
     * @throws IOException, when read error occurs
     */
    public void initialize(BufferedReader reader, int size)throws IOException{
        rbTree.initialize(reader, size);
    }

    /**
     * Increase the count value of id by given count, if it is present otherwise insert and print
     * the current value of count and print the current count value of the event for the given Id.
     * @param id Id of the event
     * @param count The value by which the counter needs to be incremented.
     */
    public void increase(int id, int count){
        RBNode rbNode;
        try{
            rbNode = rbTree.insert(id, count);
        } catch (DuplicateKeyException e){
            //If the exception is thrown that means that there is already a node with the given key
            //we increase the value by count for the node.
            rbNode = e.getDuplicate();
            rbNode.setValue(count + rbNode.getValue());
        }
        System.out.println(rbNode.getValue());
    }

    /**
     * Reduce the counter of given Id by count and print the current value, if the count reduces to <=0 remove the
     * event with id from red black tree. If the node is removed print 0.
     * @param id Id of the event
     * @param count The value by which the counter needs to be decreased
     */
    public void reduce(int id, int count){
        RBNode rbNode = rbTree.findNode(id);
        if(rbNode.isInternalNode()) {
            int newCount = rbNode.getValue() - count;
            if(newCount <= 0){
                //If the node value is less than or equal to zero delete it from tree
                rbTree.delete(rbNode);
                System.out.println("0");
            } else {
                rbNode.setValue(newCount);
                System.out.println(newCount);
            }
        } else {
            //Node not found
            System.out.println("0");
        }
    }

    /**
     * Prints whether the red black properties are satisfied and also prints the maxDepth of tree
     */
    public void verify(){
        System.out.println(rbTree.verifyRBProperties() + " " + rbTree.maxDepth());
    }

    /**
     * Print the count of the given Id, if the event with given id is not found print 0
     * @param id Id of the given event
     */
    public void count(int id){
        RBNode rbNode = rbTree.findNode(id);
        if(rbNode.isInternalNode()) {
            System.out.println(rbNode.getValue());
        } else {
            //Node not found
            System.out.println("0");
        }
    }

    /**
     * Print the sum of count values of all the ids which are in range [id1, id2] (inclusive)
     * @param id1 Id1
     * @param id2 Id2
     */
    public void inrange(int id1, int id2){
        System.out.println(inrange(id1, id2, rbTree.getRoot()));
    }

    /**
     * Recursive function to find out sum of count values of all the ids which are in range [id1, id2] (inclusive)
     * Complexity = O(lg (n) +s) where s is the number of nodes between id1 and id2
     * @param id1 Id1
     * @param id2 Id2
     * @param node Root of the subtree
     * @return Sum of count values of ids in range [id1, id2] of sub-tree rooted at node
     */
    private long inrange(int id1, int id2, RBNode node){
        if(node.isExternalNode())
            return 0;
        long count=0;
        //If the node.key > id1, that means that left child may contain nodes with id > id1
        if(node.getKey() > id1)
            count += inrange(id1,id2,node.getLeftChild());
        if(id1 <= node.getKey() && node.getKey() <= id2) {
            count += node.getValue();
        }
        //If the node.key < id2, that means that right child may contain nodes with id < id2
        if(node.getKey() < id2)
            count += inrange(id1,id2,node.getRightChild());

        //Even though this recursion may appear to be O(n) {T(n) = 2T(n) + O(1)}, it is not O(n)
        //As for most of the cases either one of the top or bottom if conditions fail.
        return count;

    }

    /**
     * Prints the Id and count of the event such that Id of the event is greater than the given id and least of all such Ids
     * Prints 0 0, if no such event is found
     * Complexity = O(lg (n))
     * @param id Id for which we need to next
     */
    public void next(int id){
        RBNode minNode;
        if(rbTree.getRoot().isInternalNode()) {
            minNode = next(id, rbTree.getRoot().getKey() > id ? rbTree.getRoot() : null, rbTree.getRoot());
            if(minNode!= null && minNode.isInternalNode()) {
                System.out.println(minNode.getKey() + " " + minNode.getValue());
                return;
            }
        }System.out.println("0 0");
    }

    /**
     * Return the RBNode such that key of the node is the least and greater than the id
     * Complexity = O(lg (n))
     * @param id Id for which we need to find next
     * @param currMinNode Current minimum Node
     * @param node Root of the subtree where we want to find the minNode
     * @return The RBNode such that key of the node is the least and greater than the id, null if no such RBNode exists
     */
    private RBNode next(int id, RBNode currMinNode, RBNode node){
        //If we have reached the leaf or external node return
        if(node.isExternalNode())
            return currMinNode;
        if(node.getKey()>id){
            //update the currMinNode if the node.key is less than currMinNode.key
            if(currMinNode == null)
                currMinNode = node;
            else if(node.getKey() < currMinNode.getKey())
                currMinNode = node;
        }
        if(id < node.getKey()) //We only go the left if id is strictly less than node.key because we want to find node.key > id
            return next(id,currMinNode,node.getLeftChild());
        else return next(id,currMinNode,node.getRightChild());

    }

    /**
     * Prints the Id and count of the event such that Id of the event is lesser than the given id and greatest of all such Ids
     * Prints 0 0, if no such event is found
     * Complexity = O(lg (n))
     * @param id Id for which we need to find previous
     */
    public void previous(int id){
        RBNode maxNode;
        if(rbTree.getRoot().isInternalNode()) {
            maxNode = previous(id, rbTree.getRoot().getKey() < id ? rbTree.getRoot() : null, rbTree.getRoot());
            if(maxNode!= null && maxNode.isInternalNode()) {
                System.out.println(maxNode.getKey() + " " + maxNode.getValue());
                return;
            }
        }System.out.println("0 0");
    }


    /**
     * Return the RBNode such that key of the node is the greatest and lesser than the id
     * Complexity = O(lg (n))
     * @param id Id for which we need to find next
     * @param currMaxNode Current maximum Node
     * @param node Root of the subtree where we want to find the minNode
     * @return The RBNode such that key of the node is the greatest and lesser than the id, null if no such RBNode exists
     */
    private RBNode previous(int id, RBNode currMaxNode, RBNode node){
        if(node.isExternalNode())
            return currMaxNode;
        if(node.getKey()<id){
            if(currMaxNode == null)
                currMaxNode = node;
            else if(node.getKey() > currMaxNode.getKey())
                currMaxNode = node;
        }
        if(id > node.getKey())
            return previous(id,currMaxNode,node.getRightChild());
        else return previous(id,currMaxNode,node.getLeftChild());

    }

}

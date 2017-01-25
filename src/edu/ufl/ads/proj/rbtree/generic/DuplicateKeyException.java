package edu.ufl.ads.proj.rbtree.generic;

/**
 * Created by hsitas444 on 3/17/2016.
 */
public class DuplicateKeyException extends Exception {
    private RBNode entry;
    public DuplicateKeyException(RBNode entry) {
        this.entry = entry;
    }
    public RBNode getDuplicate(){
        return entry;
    }
}

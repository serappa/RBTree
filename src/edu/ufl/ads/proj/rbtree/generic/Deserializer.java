package edu.ufl.ads.proj.rbtree.generic;

/**
 * Created by hsitas444 on 3/17/2016.
 */
public interface Deserializer<X> {
    X parse(String data);
}

package edu.ufl.ads.proj.rbtree;

/**
 * Created by hsitas444 on 3/17/2016.
 */
public enum Color {
    RED,
    BLACK;
    public Color otherColor(){
        if(this == Color.BLACK)
            return Color.RED;
        else return Color.BLACK;
    }
}

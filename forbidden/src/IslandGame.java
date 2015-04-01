// Assignment 9
// Davis Jack
// jdavis
// Cherry Alex
// acherry

import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import javalib.colors.*;
import javalib.worldimages.*;

interface IList<T> {
    applesssfhsglkdngkdfl;gn
    
    
}

// To represent a non-empty list of T
class Cons<T> implements IList<T> {
    
    T first;
    IList<T> rest;
    
    Cons(T first, IList<T> rest) {
        
        this.first = first;
        this.rest = rest;
        
    }
    
}

// To represent an empty list of T
class Mt<T> implements IList<T> { }

// Represents a single square of the game area
class Cell {
    // represents absolute height of this cell, in feet
    double height;
    // In logical coordinates, with the origin at the top-left corner of the scren
    int x, y;
    // the four adjacent cells to this one
    Cell left, top, right, bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded;
}

class OceanCell extends Cell {
}
 
class ForbiddenIslandWorld extends World {
    // Defines an int constant
    static final int ISLAND_SIZE = 64;
    // All the cells of the game, including the ocean
    IList<Cell> board;
    // the current height of the ocean
    int waterHeight;
}
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
    
    // Add this T
    IList<T> add(T t);
    
}

// To represent a non-empty list of T
class Cons<T> implements IList<T> {
    
    T first;
    IList<T> rest;
    
    Cons(T first, IList<T> rest) {
        
        this.first = first;
        this.rest = rest;
        
    }
    
    public IList<T> add(T t) {
        return new Cons<T>(t, this);
    }
    
}

// To represent an empty list of T
class Mt<T> implements IList<T> {
    
    public IList<T> add(T t) {
        return new Cons<T>(t, this);
    }
    
}

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
    
    Cell(double height, int x, int y) {
        
        this.height = height;
        this.x = x;
        this.y = y;
        
        this.top = null;
        this.left = null;
        this.right = null;
        this.bottom = null;
        
        this.isFlooded = true;
        
    }
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
    
    ForbiddenIslandWorld(String gameMode) {
        
        this.waterHeight = 0;
        IList<Cell> board = null;
        
        if (gameMode.equals("m")) {
            board = this.makeMountain();
        }
        else if (gameMode.equals("r")) {
            board = this.makeRandom();
        }
        else  if(gameMode.equals("t")) {
            board = this.makeTerrain();
        }
        
    }
    
    IList<Cell> makeMountain() {
        
        final double MAX_HEIGHT = ISLAND_SIZE / 2;
        
        ArrayList<ArrayList<Double>> newBoard = new ArrayList<ArrayList<Double>>();
        
        for (int index1 = 0; index1 < ISLAND_SIZE; index1 += 1) {
            
            newBoard.add(new ArrayList<Double>());
            
            for (int index2 = 0; index2 < ISLAND_SIZE; index2 += 1) {
                
                newBoard.get(index1).add(MAX_HEIGHT - (Math.abs(MAX_HEIGHT - index1) + (Math.abs(MAX_HEIGHT - index2)));
                
            }
            
        }
        
        return this.arrDoubleToCell(newBoard);
        
    }
    
    IList<Cell> makeRandom() { }
    
    IList<Cell> makeTerrain() { }
    
    IList<Cell> arrDoubleToCell(ArrayList<ArrayList<Double>> toChange) {
        
        ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();
        
        for (int index1 = 0; index1 < ISLAND_SIZE; index1 += 1) {
            
            result.add(new ArrayList<Cell>());
            
            for (int index2 = 0; index2 < ISLAND_SIZE; index2 += 1) {
                
                Cell toAdd = new Cell(toChange.get(index1).get(index2), index1, index2);
                
                result.get(index1).add(toAdd);
                
                if (index1 == 0) {
                    toAdd.left = toAdd;
                }
                else {
                    toAdd.left = result.get(index1 - 1).get(index2);
                }
                
                if (index1 == ISLAND_SIZE - 1) {
                    toAdd.right = toAdd;
                }
                else {
                    toAdd.right = result.get(index1 + 1).get(index2);
                }
                
                if (index2 == 0) {
                    toAdd.top = toAdd;
                }
                else {
                    toAdd.top = result.get(index1).get(index2 - 1);
                }
                
                if(index2 == ISLAND_SIZE - 1) {
                    toAdd.bottom = toAdd;
                }
                else {
                    toAdd.bottom = result.get(index1).get(index2 + 1);
                }
                
                toAdd.isFlooded = toAdd.height <= 0;
                
            }
            
        }
        
        IList<Cell> result2 = new Mt<Cell>();
        
        for (int index3 = 0; index3 < ISLAND_SIZE; index3 += 1) {
            
            for (int index4 = 0; index4 < ISLAND_SIZE; index4 += 1) {
                
                result2.add(result.get(index3).get(index4));
                
            }
            
        }
        
        return result2;
        
    }
    
}
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

// represents a list of T
interface IList<T> {
    // Adds the given item to the front of this list
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
    // Adds the given item to the front of this list
    public IList<T> add(T item) {
        return new Cons<T>(item, this);
    }
    
}

// To represent an empty list of T
class Mt<T> implements IList<T> {
    // Adds the given item to the front of this empty list
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
    // Determines whether this is an OceanCell
    boolean isOcean() {
        return false;
    }
}

class OceanCell extends Cell {
    OceanCell(double height, int x, int y) {
        super(height, x, y);
    }
    // Determines whether this is an OceanCell
    boolean isOcean() {
        return true;
    }
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
    // Creates a mountain map
    IList<Cell> makeMountain() {
        
        final double MAX_HEIGHT = ISLAND_SIZE / 2;
        
        ArrayList<ArrayList<Double>> newBoard = new ArrayList<ArrayList<Double>>();
        
        for (int index1 = 0; index1 < ISLAND_SIZE; index1 += 1) {
            
            newBoard.add(new ArrayList<Double>());
            
            for (int index2 = 0; index2 < ISLAND_SIZE; index2 += 1) {
                
                newBoard.get(index1).add(MAX_HEIGHT - (Math.abs(MAX_HEIGHT - index1) + (Math.abs(MAX_HEIGHT - index2))));
                
            }
            
        }
        
        return this.arrDoubleToCell(newBoard);
        
    }
    // TODO
    IList<Cell> makeRandom() {
        return new Mt<Cell>();
    }
    // TODO
    IList<Cell> makeTerrain() {
        return new Mt<Cell>();
    }
    // returns an IList of Cells from the given ArrayList<ArrayList<Double>>
    IList<Cell> arrDoubleToCell(ArrayList<ArrayList<Double>> toChange) {
        
        ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();
        
        for (int index1 = 0; index1 < ISLAND_SIZE; index1 += 1) {
            
            result.add(new ArrayList<Cell>());
            
            for (int index2 = 0; index2 < ISLAND_SIZE; index2 += 1) {
                
                Cell land = new Cell(toChange.get(index1).get(index2), index1, index2);
                Cell ocean = new OceanCell(toChange.get(index1).get(index2), index1, index2);
                
                Cell tempCell;
                
                if (toChange.get(index1).get(index2) <= 0) {
                    tempCell = ocean;
                }
                else {
                    land.isFlooded = false;
                    tempCell = land;
                }
                
                result.get(index1).add(tempCell);
                
                
                
                if (index1 == 0) {
                    tempCell.left = tempCell;
                }
                else {
                    tempCell.left = result.get(index1 - 1).get(index2);
                }
                
                if (index1 == ISLAND_SIZE - 1) {
                    tempCell.right = tempCell;
                }
                else {
                    tempCell.right = result.get(index1 + 1).get(index2);
                }
                
                if (index2 == 0) {
                    tempCell.top = tempCell;
                }
                else {
                    tempCell.top = result.get(index1).get(index2 - 1);
                }
                
                if(index2 == ISLAND_SIZE - 1) {
                    tempCell.bottom = tempCell;
                }
                else {
                    tempCell.bottom = result.get(index1).get(index2 + 1);
                }
                
                
                
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

    // TODO
    public WorldImage makeImage() {
        return null;
    }
    
}
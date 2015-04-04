// Assignment 9
// Davis Jack
// jdavis
// Cherry Alex
// acherry

import java.awt.Color;
import java.util.*;

import tester.*;
import javalib.funworld.*;
import javalib.worldcanvas.CanvasPanel;
import javalib.worldimages.*;

// represents a visitor object
interface IVisitor<T, R> {
    R visit(Cons<T> c);
    R visit(Mt<T> m);
}

// represents a function that displays the cells in a list
class DisplayCellsVisitor implements IVisitor<Cell, WorldImage> {
    public WorldImage visit(Cons<Cell> c) {
        return new OverlayImages(c.first.displayCell(), c.rest.accept(this));
    }
    public WorldImage visit(Mt<Cell> m) {
        return new LineImage(new Posn(-1, -1), new Posn(-1, -1), new Color(255, 255, 255));
    }
}

// represents a list of T
interface IList<T> {
    // Adds the given item to the front of this list
    IList<T> add(T t);
    // Draws the list according to its visitor
    <R> R accept(IVisitor<T, R> visitor);
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
    // accepts a visitor object
    public <R> R accept(IVisitor<T, R> visitor) {
        return visitor.visit(this);
    }
    
}

// To represent an empty list of T
class Mt<T> implements IList<T> {
    // Adds the given item to the front of this empty list
    public IList<T> add(T t) {
        return new Cons<T>(t, this); 
    }
    // accepts a visitor object
    public <R> R accept(IVisitor<T, R> visitor) {
        return visitor.visit(this);
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
    // constructor for testing
    Cell(double height, int x, int y, boolean isFlooded) {
        this(height, x, y);
        this.isFlooded = isFlooded;
    }
    // Determines whether this is an OceanCell
    boolean isOcean() {
        return false;
    }
    // Displays this cell 
    WorldImage displayCell() {
        int sideLength = 30;
        int posnShift = sideLength / 2;
        return new RectangleImage(new Posn(this.x + posnShift, this.y + posnShift), sideLength, sideLength, this.cellColor());
    }/*
    // Displays this cell and its neighbors
    WorldImage displayNeighbors() {
        if (!(this.right == this || this.bottom == this)) {
            return new OverlayImages(this.displayCell(), 
                    new OverlayImages(this.right.displayCell(), this.bottom.displayCell()));
        }
        else if (this.right == this) {
            return new OverlayImages(this.displayCell(),
                    this.bottom.displayCell());
        }
        else if (this.bottom == this) {
            return new OverlayImages(this.displayCell(),
                    this.bottom.displayCell());
        }
        else {
            return this.displayCell();
        }
    }
    // Displays all of the cells connected to this one
    WorldImage displayAllCells() {
        if ((this.right.right == this.right) && this.bottom.bottom == this.bottom) {
            return this.displayNeighbors();
        }
        else if (!(this.right.right == this.right && this.bottom.bottom == this.bottom) {
            return new OverlayImages(this.displayNeighbors(), new OverlayImages(this.right.rightdisplayNeighbors());
        }
        else if (this.right.right)
    }*/
    // Computes this cell's color
    Color cellColor() {
        // Flooded cells range from blue to black
        if (this.isFlooded) {
            int blue = 255; // TODO
            return new Color(0, 0, 0);
        }
        // cells in danger of flooding range from green to red
        else if (this.floodDanger()) {
            int red = Math.min(255, (((int)this.height) -1));
            int green = 255 + Math.max(255, ((int)this.height + 1));
            // TODO red, green
            return new Color(red, green, 0);
        }
        else {
            // TODO find better way?
            int red = Math.min(255, (int)this.height * 10);
            int blue = Math.min(255, (int)this.height * 10);
            return new Color(red, 255, blue);
                
        }
    }
    // TODO 
    boolean floodDanger() {
        return true;
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
    // Computes this cell's color
    Color cellColor() {
        return new Color(0, 0, 120);
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
            board = this.makeMountain(false);
        }
        else if (gameMode.equals("r")) {
            board = this.makeMountain(true);
        }
        else  if(gameMode.equals("t")) {
            board = this.makeTerrain();
        }
        
    }
    // Creates a mountain map
    IList<Cell> makeMountain(boolean isRandom) {
        
        Random randy = new Random(666);
        
        final double MAX_HEIGHT = ISLAND_SIZE / 2;
        
        ArrayList<ArrayList<Double>> newBoard = new ArrayList<ArrayList<Double>>();
        
        for (int index1 = 0; index1 < ISLAND_SIZE; index1 += 1) {
            
            newBoard.add(new ArrayList<Double>());
            
            for (int index2 = 0; index2 < ISLAND_SIZE; index2 += 1) {
                
                if (!isRandom) {
                    newBoard.get(index1).add(MAX_HEIGHT - (Math.abs(MAX_HEIGHT - index1) + (Math.abs(MAX_HEIGHT - index2))));
                }
                else {
                    newBoard.get(index1).add((double)randy.nextInt(32) + 1);
                }
                
            }
            
        }
        
        return this.arrDoubleToCell(newBoard);
        
    }
    // TODO
    IList<Cell> makeTerrain() {
        return new Mt<Cell>();
    }
    // determines the top, left, right, and bottom of a the cells in this world
    void assignNeighbors(Cell tempCell, int index1, int index2, ArrayList<ArrayList<Cell>> result) {
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
    // returns an IList of Cells from the given ArrayList<ArrayList<Double>>
    IList<Cell> arrDoubleToCell(ArrayList<ArrayList<Double>> toChange) {
        
        ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();

        for (int index1 = 0; index1 <= ISLAND_SIZE; index1 += 1) {

            result.add(new ArrayList<Cell>());

            for (int index2 = 0; index2 <= ISLAND_SIZE; index2 += 1) {

                Cell land = new Cell(toChange.get(index1).get(index2), index1 + 5, index2 + 5);
                Cell ocean = new OceanCell(toChange.get(index1).get(index2), index1 + 5, index2 + 5);

                Cell tempCell;

                if (toChange.get(index1).get(index2) <= 0) {
                    tempCell = ocean;
                }
                else {
                    land.isFlooded = false;
                    tempCell = land;
                }
                
                result.get(index1).add(tempCell);
                this.assignNeighbors(tempCell, index1, index2, result);
                
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

    // Draws the World
    public WorldImage makeImage() {
        return new OverlayImages(new RectangleImage(new Posn(0, 0), 1280, 1280, new Color(255, 255, /* Real Value: 0, 0, 120 */ 255)), 
                this.board.accept(new DisplayCellsVisitor()));
    }
} 
    



class ExamplesIsland {
    ForbiddenIslandWorld nullWorld = new ForbiddenIslandWorld("not a world");
    // ForbiddenIslandWorld mountain = new ForbiddenIslandWorld("m");
    // ForbiddenIslandWorld random = new ForbiddenIslandWorld("r");
    // TODO ForbiddenIslandWorld terrain = new ForbiddenIslandWorld("t");
    ArrayList<ArrayList<Double>> arrayListD = new ArrayList<ArrayList<Double>>();
    Cell land1 = new Cell(-5, 30, 30, false);
    Cell land2 = new Cell(-10, 90, 90, true);
    Cell land3 = new Cell(50.0, 30, 90, false);
    Cell land4 = new Cell(-10, 50, 50, false);
    Cell land5 = new Cell(20.0, 60, 60, true);
    Cell land6 = new Cell(-10, 1, 1, false);
    Cell ocean1 = new OceanCell(0, 90, 30);
    Cell ocean2 = new OceanCell(0, 0, 20);
    Cell ocean3 = new OceanCell(0, 50, 50);
    Cell ocean4 = new OceanCell(0, 0, 20);
    Cell ocean5 = new OceanCell(0, 50, 0);
    Cell ocean6 = new OceanCell(0, 0, 20);
    
    Cell s = new OceanCell(0, 0, 1);
    IList<Cell> iList = new Cons<Cell>(land1, new Cons<Cell>(land2, new Cons<Cell>(land3, new Cons<Cell>(land4,
            new Cons<Cell>(land5, new Cons<Cell>(land6, new Cons<Cell>(ocean1, new Cons<Cell>(ocean2,
                    new Cons<Cell>(ocean3, new Cons<Cell>(ocean4, new Cons<Cell>(ocean5, new Cons<Cell>(ocean6,
                            new Mt<Cell>()))))))))))));
    IList<Cell> iList2 = new Cons<Cell>(land1, new Cons<Cell>(land2, new Cons<Cell>(land3, new Cons<Cell>(ocean1, new Mt<Cell>()))));
    // TODO
    void initialize() {
        
        this.land1 = new Cell(1.0, 1, 0, false);
        this.land2 = new Cell(1.0, 1, 1, false);
        this.ocean1 = new OceanCell(0, 0, 0);
        this.ocean2 = new OceanCell(0, 0, 1);     
        
        this.arrayListD.clear();
        int size = 2;
        for (int index1 = 0; index1 < size - 1; index1 += 1) {
            
            arrayListD.add(new ArrayList<Double>());
            
            for (int index2 = 0; index2 < size - 1; index2 += 1) {
                
                arrayListD.get(index1).add((double)index1);
            }
        }
    }
/*
    //tests arrDoubleToCell for the class ForbiddenIslandWorld
    void testArrDoubleToCell(Tester t) {
        this.initialize();
        t.checkExpect(this.nullWorld.arrDoubleToCell(this.arrayListD), this.iList);
    }*/
    {this.nullWorld.board = this.iList2;} 
    boolean runAnimation = this.nullWorld.bigBang(640, 640);
}

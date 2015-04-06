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

interface IFunc<T, R> {
    
    R apply(T t);
    
}

// represents a function that displays the cells in a list
class DisplayCellsVisitor implements IVisitor<Cell, WorldImage> {
    int waterLevel;
    DisplayCellsVisitor(int w) {
        this.waterLevel = w;
    }
    public WorldImage visit(Cons<Cell> c) {
        return new OverlayImages(c.first.displayCell(waterLevel), c.rest.accept(this));
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
    // Maps the given IFunc object through this list
    <R> IList<R> map(IFunc<T, R> func);
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
    
    public <R> IList<R> map(IFunc<T, R> func) {
        
        return new Cons<R>(func.apply(this.first), this.rest.map(func));
        
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
    
    public <R> IList<R> map(IFunc<T, R> func) {
        
        return new Mt<R>();
        
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
    boolean updateFloodHelp(int waterLevel) {
        return (this.height - waterLevel) <= 0;
    }
    // Displays this cell 
    WorldImage displayCell(int waterLevel) {
        int sideLength = 30;
        int posnShift = sideLength / 2;
        return new RectangleImage(new Posn(this.x + posnShift, this.y + posnShift), sideLength, sideLength, this.cellColor(waterLevel));
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
    Color cellColor(int waterLevel) {
        // Flooded cells range from blue to black
        if (this.isFlooded) {
            int b = Math.max((int)this.height - waterLevel, -120); // TODO
            return new Color(0, 0, 120 + b);
        }
        // cells in danger of flooding range from green to red
        else if (this.floodDanger(waterLevel)) {
            int red = Math.max(waterLevel - (int)this.height, -120);
            int green = Math.max((int)this.height - waterLevel, -120);
            return new Color(red, 120 + green, 0);
        }
        else {
            if ((int)this.height - waterLevel >= 12) {
                int other = Math.min(Math.max(0, ((((int)this.height - waterLevel) - 11) * (255 / 20))), 255);
                return new Color(other, 255, other);
            }
            else {
                int other = Math.min(Math.max(120, (((int)this.height - waterLevel) * (255 / 12))), 255);
                return new Color(0, other, 0);
            }
        }
    }
    // Determines whether this cell is in danger of flooding or flooded
    boolean floodDanger(int waterLevel) {
        return this.height <= waterLevel || this.isFlooded;
    }
    // Determines whether this is an OceanCell 
    boolean isOcean() { return false; }
}

class OceanCell extends Cell {
    OceanCell(int x, int y) {
        super(0, x, y);
    }
    // Determines whether this is an OceanCell
    boolean isOcean() {
        return true;
    }
    // Computes this cell's color
    Color cellColor(int waterLevel) {
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
        this.board = null;
        
        if (gameMode.equals("m")) {
            this.board = this.makeMountain(false);
        }
        else if (gameMode.equals("r")) {
            this.board = this.makeMountain(true);
        }
        else  if(gameMode.equals("t")) {
            this.board = this.makeTerrain();
        }
        
    }
    // Creates a standard map
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
    // Makes a standard island with random heights
    IList<Cell> makeTerrain() {
        
        ArrayList<ArrayList<Double>> newBoard = new ArrayList<ArrayList<Double>>();
        
        for (int index1 = 0; index1 < ISLAND_SIZE + 1; index1 += 1) {
            
            newBoard.add(new ArrayList<Double>());
            
            for (int index2 = 0; index2 < ISLAND_SIZE + 1; index2 += 1) {
                
                newBoard.get(index1).add(0.0);
                
            }
            
        }
        
        // TODO
        return new Mt<Cell>();
        
    }
    
    double calculateMiddle(double tl, double tr, double br, double bl) {
        
        double t = Math.random() * Math.abs(tl - tr) + (tl + tr) / 2;
        double r = Math.random() * Math.abs(tr - br) + (tr + br) / 2;
        double b = Math.random() * Math.abs(br - bl) + (br + bl) / 2;
        double l = Math.random() * Math.abs(tl - bl) + (tl + bl) / 2;
        
        double m = Math.random() * Math.abs(tl - br) + (tl + tr + br + bl) / 4;
        
        return m;
        
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

                Cell land = new Cell(toChange.get(index1).get(index2), index1, index2);
                Cell ocean = new OceanCell(index1, index2);

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
                this.board.accept(new DisplayCellsVisitor(this.waterHeight)));
    }
} 
    
class UpdateFlood implements IFunc<Cell, Cell> {
    
    int waterLevel;
    
    UpdateFlood(int waterLevel) { this.waterLevel = waterLevel; }
    
    public Cell apply(Cell t) {
        
        if (t.isOcean()) {
            return t;
        }
        else {
            return new Cell(t.height, t.x, t.y, t.updateFloodHelp(waterLevel));
        }
        
    }
    
}

class ArrDub2Cell implements IFunc<ArrayList<ArrayList<Double>>, IList<Cell>> {
    
    int ISLAND_SIZE;
    
    ArrDub2Cell(int ISLAND_SIZE) { 
        this.ISLAND_SIZE = ISLAND_SIZE; 
    }
    
    public IList<Cell> apply(ArrayList<ArrayList<Double>> t) {
        
        ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();

        for (int index1 = 0; index1 <= ISLAND_SIZE; index1 += 1) {

            result.add(new ArrayList<Cell>());

            for (int index2 = 0; index2 <= ISLAND_SIZE; index2 += 1) {

                Cell land = new Cell(t.get(index1).get(index2), index1, index2);
                Cell ocean = new OceanCell(index1, index2);

                Cell tempCell;

                if (t.get(index1).get(index2) <= 0) {
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
    
}

class ExamplesIsland {
    ForbiddenIslandWorld nullWorld = new ForbiddenIslandWorld("not a world");
    //ForbiddenIslandWorld mountain = new ForbiddenIslandWorld("m");
    // ForbiddenIslandWorld random = new ForbiddenIslandWorld("r");
    // TODO ForbiddenIslandWorld terrain = new ForbiddenIslandWorld("t");
    ArrayList<ArrayList<Double>> arrayListD = new ArrayList<ArrayList<Double>>();
    
    Cell c0_0 = new Cell(0, 0, 0);
    Cell c0_1 = new Cell(0, 0, 1);
    Cell c0_2 = new Cell(0, 0, 2);
    Cell c0_3 = new Cell(0, 0, 3);
    Cell c0_4 = new Cell(0, 0, 4);
    Cell c1_0 = new Cell(0, 1, 0);
    Cell c1_1 = new Cell(0, 1, 1);
    Cell c1_2 = new Cell(0, 1, 2);
    Cell c1_3 = new Cell(0, 1, 3);
    Cell c1_4 = new Cell(0, 1, 4);
    Cell c2_0 = new Cell(0, 2, 0);
    Cell c2_1 = new Cell(0, 2, 1);
    Cell c2_2 = new Cell(0, 2, 2);
    Cell c2_3 = new Cell(0, 2, 3);
    Cell c2_4 = new Cell(0, 2, 4);
    Cell c3_0 = new Cell(0, 3, 0);
    Cell c3_1 = new Cell(0, 3, 1);
    Cell c3_2 = new Cell(0, 3, 2);
    Cell c3_3 = new Cell(0, 3, 3);
    Cell c3_4 = new Cell(0, 3, 4);
    Cell c4_0 = new Cell(0, 4, 0);
    Cell c4_1 = new Cell(0, 4, 1);
    Cell c4_2 = new Cell(0, 4, 2);
    Cell c4_3 = new Cell(0, 4, 3);
    Cell c4_4 = new Cell(0, 4, 4);
    
    
    ArrayList<Cell> aL0 = new ArrayList<Cell>();
    ArrayList<Cell> aL1 = new ArrayList<Cell>();
    ArrayList<Cell> aL2 = new ArrayList<Cell>();
    ArrayList<Cell> aL3 = new ArrayList<Cell>();
    ArrayList<Cell> aL4 = new ArrayList<Cell>();
    ArrayList<ArrayList<Cell>> aLAll = new ArrayList<ArrayList<Cell>>();
    
    
    Cell landSunk1 = new Cell(-5, 0, 0, true);
    Cell landSunk2 = new Cell(-300, 100, 0, true);
    Cell landSunk3 = new Cell(-5, 0, 100, true);
    Cell landSunk4 = new Cell(-70, 100, 100, true);
    Cell landAbove1 = new Cell(1, 0, 0, false);
    Cell landAbove2 = new Cell(10, 100, 0, false);
    Cell landAbove3 = new Cell(12, 0, 100, false);
    Cell landAbove4 = new Cell(32, 100, 100, false);
    Cell landDan1 = new Cell(-10, 0, 0, false);
    Cell landDan2 = new Cell(-50, 100, 0, false);
    Cell landDan3 = new Cell(-100, 0, 100, false);
    Cell landDan4 = new Cell(-150, 100, 100, false);
    
    Cell ocean1 = new OceanCell(150, 150);
    Cell ocean2 = new OceanCell(0, 20);
    Cell ocean3 = new OceanCell(50, 50);
    Cell ocean4 = new OceanCell(0, 20);
    Cell ocean5 = new OceanCell(50, 0);
    Cell ocean6 = new OceanCell(0, 20);
    IList<Cell> iList2 = new Cons<Cell>(landSunk1, new Cons<Cell>(landSunk2, new Cons<Cell>(landSunk3, new Cons<Cell>(landSunk4, new Mt<Cell>()))));
    IList<Cell> iList3 = new Cons<Cell>(landAbove1, new Cons<Cell>(landAbove2, new Cons<Cell>(landAbove3, new Cons<Cell>(landAbove4, new Mt<Cell>()))));
    IList<Cell> iList4 = new Cons<Cell>(landDan1, new Cons<Cell>(landDan2, new Cons<Cell>(landDan3, new Cons<Cell>(landDan4, new Mt<Cell>()))));
    Cell s = new OceanCell(0, 1);
    
    
    
    Cell land7 = new Cell(0, 10, 10);
    Cell land8 = new Cell(-1, 10, 11);
    Cell land9 = new Cell(70, 10, 12);
    OceanCell ocean7 = new OceanCell(13, 13);
    IList<Cell> list1 = new Cons<Cell>(land7, new Cons<Cell>(land8,
            new Cons<Cell>(land9, new Cons<Cell>(ocean7, new Mt<Cell>()))));
    Cell land7_2 = new Cell(0, 10, 10, true);
    Cell land8_2 = new Cell(-1, 10, 11, true);
    Cell land9_2 = new Cell(70, 10, 12, false);
    IList<Cell> list1_2 = new Cons<Cell>(land7_2, new Cons<Cell>(land8_2,
            new Cons<Cell>(land9_2, new Cons<Cell>(ocean7, new Mt<Cell>()))));   
    
    IList<Cell> list2 = new Mt<Cell>();
    
    IFunc<Cell, Cell> upFld = new UpdateFlood(64);
    ArrDub2Cell ar2Dub = new ArrDub2Cell(64);
    ArrDub2Cell ar2Dub1 = new ArrDub2Cell(1);
    ArrDub2Cell ar2Dub5 = new ArrDub2Cell(5);

    // TODO
    void initialize() {
        
        this.c0_0 = new Cell(0, 0, 0);
        this.c0_1 = new Cell(0, 0, 1);
        this.c0_2 = new Cell(0, 0, 2);
        this.c0_3 = new Cell(0, 0, 3);
        this.c0_4 = new Cell(0, 0, 4);
        this.c1_0 = new Cell(0, 1, 0);
        this.c1_1 = new Cell(0, 1, 1);
        this.c1_2 = new Cell(0, 1, 2);
        this.c1_3 = new Cell(0, 1, 3);
        this.c1_4 = new Cell(0, 1, 4);
        this.c2_0 = new Cell(0, 2, 0);
        this.c2_1 = new Cell(0, 2, 1);
        this.c2_2 = new Cell(0, 2, 2);
        this.c2_3 = new Cell(0, 2, 3);
        this.c2_4 = new Cell(0, 2, 4);
        this.c3_0 = new Cell(0, 3, 0);
        this.c3_1 = new Cell(0, 3, 1);
        this.c3_2 = new Cell(0, 3, 2);
        this.c3_3 = new Cell(0, 3, 3);
        this.c3_4 = new Cell(0, 3, 4);
        this.c4_0 = new Cell(0, 4, 0);
        this.c4_1 = new Cell(0, 4, 1);
        this.c4_2 = new Cell(0, 4, 2);
        this.c4_3 = new Cell(0, 4, 3);
        this.c4_4 = new Cell(0, 4, 4);
        
        
        aL0.clear();
        aL0.add(c0_0);
        aL0.add(c0_1);
        aL0.add(c0_2);
        aL0.add(c0_3);
        aL0.add(c0_4);
        aL1.clear();
        aL1.add(c1_0);
        aL1.add(c1_1);
        aL1.add(c1_2);
        aL1.add(c1_3);
        aL1.add(c1_4);
        aL2.clear();
        aL2.add(c2_0);
        aL2.add(c2_1);
        aL2.add(c2_2);
        aL2.add(c2_3);
        aL2.add(c2_4);
        aL3.clear();
        aL3.add(c3_0);
        aL3.add(c3_1);
        aL3.add(c3_2);
        aL3.add(c3_3);
        aL3.add(c3_4);
        aL4.clear();
        aL4.add(c4_0);
        aL4.add(c4_1);
        aL4.add(c4_2);
        aL4.add(c4_3);
        aL4.add(c4_4);
        aLAll.clear();
        aLAll.add(aL0);
        aLAll.add(aL1);
        aLAll.add(aL2);
        aLAll.add(aL3);
        aLAll.add(aL4);
        
        
        for (int index1 = 0; index1 < 64; index1 += 1) {
            
            this.arrayListD.add(new ArrayList<Double>());
            
            for (int index2 = 0; index2 < 64; index2 += 1) {
                this.arrayListD.get(index1).add(0.0);
                list2.add(new Cell(0.0, index1, index2));   
            }   
        }
    }
    
    boolean testUpdateFlood(Tester t) {
        
        return t.checkExpect(this.list1.map(this.upFld), this.list1_2);
        
    } 

    /*tests arrDoubleToCell for the class ForbiddenIslandWorld
    void testArrDoubleToCell(Tester t) {
        this.initialize();
        t.checkExpect(this.ar2Dub.apply(arrayListD), this.list2);
    } */
    
    // tests assignNeighbors for the class ForbiddenIslandWorld
    void testAssignNeighbors(Tester t) {
        this.initialize();
        
        // top left corner (0, 0)
        t.checkExpect(c0_0.left, null);
        t.checkExpect(c0_0.right, null);
        t.checkExpect(c0_0.top, null);
        t.checkExpect(c0_0.bottom, null);
        
        this.ar2Dub5.assignNeighbors(c0_0, 0, 0, aLAll);
        t.checkExpect(c0_0.top, c0_0);
        t.checkExpect(c0_0.left, c0_0);
        t.checkExpect(c0_0.right, c1_0);
        t.checkExpect(c0_0.bottom, c0_1);
      
        // top right corner (4, 0)
        t.checkExpect(c4_0.left, null);
        t.checkExpect(c4_0.right, null);
        t.checkExpect(c4_0.top, null);
        t.checkExpect(c4_0.bottom, null);
        
        this.ar2Dub5.assignNeighbors(c4_0, 4, 0, aLAll);
        t.checkExpect(c4_0.top, c4_0);
        t.checkExpect(c4_0.left, c3_0);
        t.checkExpect(c4_0.right, c4_0);
        t.checkExpect(c4_0.bottom, c4_1);
        
        // bottom left corner (0, 4)
        t.checkExpect(c0_4.left, null);
        t.checkExpect(c0_4.right, null);
        t.checkExpect(c0_4.top, null);
        t.checkExpect(c0_4.bottom, null);
        
        this.ar2Dub5.assignNeighbors(c0_4, 0, 4, aLAll);
        t.checkExpect(c0_4.top, c0_3);
        t.checkExpect(c0_4.left, c0_4);
        t.checkExpect(c0_4.right, c1_4);
        t.checkExpect(c0_4.bottom, c0_4);
        
        // bottom right corner (4, 4)
        t.checkExpect(c4_4.left, null);
        t.checkExpect(c4_4.right, null);
        t.checkExpect(c4_4.top, null);
        t.checkExpect(c4_4.bottom, null);
        
        this.ar2Dub5.assignNeighbors(c4_4, 4, 4, aLAll);
        t.checkExpect(c4_4.top, c4_3);
        t.checkExpect(c4_4.left, c3_4);
        t.checkExpect(c4_4.right, c4_4);
        t.checkExpect(c4_4.bottom, c4_4);
        
        // all neighbors (2, 2)
        t.checkExpect(c2_2.left, null);
        t.checkExpect(c2_2.right, null);
        t.checkExpect(c2_2.top, null);
        t.checkExpect(c2_2.bottom, null);
        
        this.ar2Dub5.assignNeighbors(c2_2, 2, 2, aLAll);
        t.checkExpect(c2_2.top, c2_1);
        t.checkExpect(c2_2.left, c1_2);
        t.checkExpect(c2_2.right, c3_2);
        t.checkExpect(c2_2.bottom, c2_3);
        
    }
    
    {this.nullWorld.board = this.iList3;} 
    //boolean runAnimation = this.nullWorld.bigBang(640, 640);
}


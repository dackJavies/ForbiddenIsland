// Assignment 9
// Davis Jack
// jdavis
// Cherry Alex
// acherry

import java.awt.Color;
import java.util.*;

import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;

import java.util.Random;




//represents a list of T
interface IList<T> {
    // Adds the given item to the front of this list
    IList<T> add(T t);
    // Draws the list according to its visitor
    <R> R accept(IVisitor<T, R> visitor);
    // Maps the given IFunc object through this list
    <R> IList<R> map(IFunc<T, R> func);
    // Appends this list to the given list
    IList<T> append(IList<T> other);
    // creates a new Tree from this IList
    IBST<T> list2Tree(IComp<T> comp);
    // Length of this list
    int lengthT(int acc);
    int length();
}

//To represent a non-empty list of T
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
    // Maps the given IFunc object through this list
    public <R> IList<R> map(IFunc<T, R> func) {

        return new Cons<R>(func.apply(this.first), this.rest.map(func));

    }
    // appends this list to the given list
    public IList<T> append(IList<T> other) {
        return new Cons<T>(this.first, this.rest.append(other));
    }
    // Length of this list helper
    public int lengthT(int acc) {
        return this.rest.lengthT(1 + acc);
    }
    // Length of this list
    public int length() {
        return this.lengthT(0);
    }
    // creates a new Tree from this IList
    public IBST<T> list2Tree(IComp<T> comp) {
        return this.rest.list2Tree(comp).insert(comp, this.first);
    }
}

//To represent an empty list of T
class Mt<T> implements IList<T> {
    // Adds the given item to the front of this empty list
    public IList<T> add(T t) {
        return new Cons<T>(t, this); 
    }
    // accepts a visitor object
    public <R> R accept(IVisitor<T, R> visitor) {
        return visitor.visit(this);
    } 
    // Maps the given IFunc object through this list
    public <R> IList<R> map(IFunc<T, R> func) {

        return new Mt<R>();

    }
    // appends this list to the given list
    public IList<T> append(IList<T> other) {
        return other;
    }
    // Length of this list helper
    public int lengthT(int acc) { 
        return acc; 
    }
    // Length of this list
    public int length() { 
        return 0; 
    }
    // creates a new Tree from this IList
    public IBST<T> list2Tree(IComp<T> comp) {
        return new Leaf<T>();
    }
}    


//represents a Cell Binary Tree
interface IBST<T> {
    // inserts the given item into this tree
    IBST<T> insert(IComp<T> comp, T t);
    // determines whether this is a leaf
    boolean isLeaf();
    // accepts a visitor 
    <R> R accept(IVisitor<T, R> v);
}

//represents a known Cell Binary Tree
class Node<T> implements IBST<T> {
    T data;
    IBST<T> left;
    IBST<T> right;
    Node(T data, IBST<T> left, IBST<T> right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }
    // inserts an item into this tree according to the given comparator
    public IBST<T> insert(IComp<T> comp, T t) {
        if (comp.compare(this.data, t) >= 0) {
            return new Node<T>(this.data, this.left.insert(comp, t), this.right);
        }
        else {
            return new Node<T>(this.data, this.left, this.right.insert(comp, t));
        }
    }
    // determines whether this is a leaf
    public boolean isLeaf() {
        return false;
    }
    // accepts the given visitor
    public <R> R accept(IVisitor<T, R> v) {
        return v.visit(this);
    }
}

//represents an empty Binary Tree
class Leaf<T> implements IBST<T> {
    // inserts an item into this tree according to the given comparator
    public IBST<T> insert(IComp<T> comp, T t) {
        return new Node<T>(t, this, this);        
    }
    // determines whether this is a leaf
    public boolean isLeaf() {
        return true;
    }
    // accepts the given visitor
    public <R> R accept(IVisitor<T, R> v) {
        return v.visit(this);
    }
}


//this represents a comparator
interface IComp<T> {
    // == 0 : t1 == t2
    // < 0: t1 < t2
    // > 0: t1 > t2
    int compare(T t1, T t2);
}

//this represents a comparator of Cells
class CompCell implements IComp<Cell> {
    // compares based on x and y (e.g. (0, 1) < (1, 1) < (1, 2) < (2,0))
    public int compare(Cell t1, Cell t2) {
        if (t1.x > t2.x || (t1.x == t2.x && t1.y > t2.y)) {
            return 1;
        }
        else if (t1.x == t2.x && t1.y == t2.y) {
            return 0; 
        }
        else {
            return -1;
        }
    }
}


class RandCellComp implements IComp<Cell> {
    int seed;
    RandCellComp(int num) {
        this.seed = num;
    }
    RandCellComp() {
        this.seed = new Random().nextInt();
    }
    // randomly assigns number to land
    public int compare(Cell t1, Cell t2) {
        Random r = new Random(seed);
        
        if (t2.isOcean() ) {
            return (r.nextInt() / 10000000) - 500;
        }
        else {
            return r.nextInt() / 1000000;
        }
    }
}


// represents a function that converts ArrayListArrayList<Double>> to IList<Cell> 
class ArrDub2ListCell implements IFunc<ArrayList<ArrayList<Double>>, IList<Cell>> {

    // converts a height to a cell
    Cell height2Cell(double height, int x, int y) {
        if (height <= 0) {
            return new OceanCell(x, y);
        }
        else {
            return new Cell(height, x, y, false);
        }
    }
    // converts an ArrayList<Double> to a new ArrayList<Cell> 
    ArrayList<Cell> doubleArr2CellArr(ArrayList<Double> doubArr, int x) {
        ArrayList<Cell> result = new ArrayList<Cell>();
        for(int i = 0; i < doubArr.size(); i += 1) {
            result.add(this.height2Cell(doubArr.get(i), x, i));
        }
        return result;
    }
    // converts an ArrayList<ArrayList<Double>> to a new ArrayList<ArrayList<Cell>> 
    ArrayList<ArrayList<Cell>> dubArrArr2CellArrArr(ArrayList<ArrayList<Double>> doubArr) {
        ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();
        for (int i = 0; i < doubArr.size(); i += 1) {
            ArrayList<Cell> inList = this.doubleArr2CellArr(doubArr.get(i), i); 
            result.add(inList);
        }
        return result;
    }
    // assigns the given cell its neighbors
    void assignNeighbors(Cell tempCell, int index1, int index2, ArrayList<ArrayList<Cell>> result) {
        if (index1 == 0) {
            tempCell.left = tempCell;
        }
        else {
            tempCell.left = result.get(index1 - 1).get(index2);
        }
        if (index1 == result.size() - 1) {
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

        if(index2 == result.get(index1).size() - 1) {
            tempCell.bottom = tempCell;
        }
        else {
            tempCell.bottom = result.get(index1).get(index2 + 1);
        }
    }
    // assigns neighbors to all the cells in an ArrayList<ArrayList<Cell>>
    void assignAllNeighbors(ArrayList<ArrayList<Cell>> cellArr) {
        for (int i = 0; i < cellArr.size(); i += 1) {
            for (int i2 = 0; i2 < cellArr.get(i).size(); i2 += 1) {
                this.assignNeighbors(cellArr.get(i).get(i2), i, i2, cellArr);
            }
        }
    }
    // converts an ArrayList<ArrayList<Cell>> to a new IList<Cell> and assigns their neighbors
    IList<Cell> cellArrArr2cellList(ArrayList<ArrayList<Cell>> cellArr) {
        ArrayList<ArrayList<Cell>> temp = cellArr;
        this.assignAllNeighbors(temp);
        IList<Cell> result = new Mt<Cell>();
        for (int i = 0; i < temp.size(); i += 1) {
            for (int i2 = 0; i2 < temp.get(i).size(); i2 += 1) {
                result = result.append(new Cons<Cell>(temp.get(i).get(i2), new Mt<Cell>()));
            }
        }
        return result;
    }
    // converts an ArrayList<ArrayList<Double>> to a new IList<Cell>
    public IList<Cell> apply(ArrayList<ArrayList<Double>> arrDub) {
        ArrayList<ArrayList<Cell>> arrCell = this.dubArrArr2CellArrArr(arrDub);
        IList<Cell> result = this.cellArrArr2cellList(arrCell);
        return result;
    }
}




//represents a visitor object
interface IVisitor<T, R> {
    R visit(Cons<T> c);
    R visit(Mt<T> m);
    R visit(Node<T> n);
    R visit(Leaf<T> n);
}

//represents a visitor that displays the cells in a list
class DisplayCellsVisitor implements IVisitor<Cell, WorldImage> {
    IBST<Cell> board;
    int waterLevel;
    DisplayCellsVisitor(IList<Cell> board, int w) {
        this.board = board.list2Tree(new RandCellComp());
        this.waterLevel = w;
    }
    //
    public WorldImage visit(Mt<Cell> m) {
        throw new IllegalArgumentException("IList is not a valid argument");
    }
    //
    public WorldImage visit(Cons<Cell> c) {
        throw new IllegalArgumentException("IList is not a valid argument");
    }
    //
    public WorldImage visit(Node<Cell> n) {
        return new OverlayImages(n.data.displayCell(waterLevel), new OverlayImages(n.left.accept(this), n.right.accept(this)));
    }
    //
    public WorldImage visit(Leaf<Cell> n) {
        return new LineImage(new Posn(-1, -1), new Posn(-1, -1), new Color(255, 255, 255));
    }    
}

//represents a function
interface IFunc<T, R> {

    R apply(T t);

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

//Represents a single square of the game area
class Cell {
    // represents absolute height of this cell, in feet
    double height;
    // In logical coordinates, with the origin at the top-left corner of the screen
    int x, y;
    // the four adjacent cells to this one
    Cell left, top, right, bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded;
    // determines whether this Cell has a target
    boolean hasTarget;
    // determines whether this Cell has a Player
    boolean hasPlayer;

    Cell(double height, int x, int y) {

        this.height = height;
        this.x = x;
        this.y = y;

        this.top = null;
        this.left = null;
        this.right = null;
        this.bottom = null;

        this.isFlooded = false;  
        this.hasTarget = false;
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
        int sideLength = 10;
        int posnShift = sideLength / 2;
        return new RectangleImage(new Posn((this.x * sideLength) + posnShift, (this.y * sideLength) + posnShift), sideLength, sideLength, this.cellColor(waterLevel));
    }
    // Computes this cell's color
    Color cellColor(int waterLevel) {
        int bound = 100;
        // Flooded cells range from blue to black
        if (this.isFlooded) {
            int b = Math.max((int)this.height - waterLevel, -bound);
            return new Color(0, 0, bound + b);
        }
        // cells in danger of flooding range from green to red
        else if (this.belowWaterLevel(waterLevel)) {
            int red = Math.max(waterLevel - (int)this.height, -bound);
            int green = Math.max((int)this.height - waterLevel, -bound);
            return new Color(red, bound + green, 0);
        }
        // above water cells not in danger
        else {
            // blue and red from 255 - 0
            if ((int)this.height - waterLevel >= 15) {
                int other = Math.min(Math.max(0, ((((int)this.height - waterLevel) - 15) * (255 / 18))), 255);
                return new Color(other, 255, other);
            }
            // green from 255 - 120
            else {
                int other = Math.min(Math.max(bound, (((int)this.height - waterLevel) * (255 / 15))), 255);
                return new Color(0, other, 0);
            }
        }
    }
    // Determines whether this cell is in danger of flooding or flooded
    boolean belowWaterLevel(int waterLevel) {
        return this.height <= waterLevel || this.isFlooded;
    }
    // Determines whether this is on the coastline
    boolean isCoastalCell() {
        return this.left.isFlooded || this.right.isFlooded || this.right.isFlooded
                || this.right.isFlooded;
    } 
    // Determines whether this is an OceanCell 
    boolean isOcean() { return false; }

    // Is the cell in the given direction flooded?
    boolean safeHelp(String dir) {

        if (dir.equals("up")) { return this.top.isFlooded; }

        else if (dir.equals("down")) { return this.bottom.isFlooded; }

        else if (dir.equals("left")) { return this.left.isFlooded; }

        else if (dir.equals("right")) { return this.right.isFlooded; }

        else { return true; }

    }
}

class OceanCell extends Cell {
    OceanCell(int x, int y) {
        super(0, x, y, true);
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
    // The Player
    Player thePlayer;
    // Targets
    IList<Target> pieces;
    // Helicopter
    HelicopterTarget chopper;
    // Defines an int constant
    static final int ISLAND_SIZE = 64;
    // All the cells of the game, including the ocean
    IList<Cell> board;
    // the current height of the ocean
    int waterHeight;
    // is the game paused
    boolean isPaused;
    // tracks the waterTicks
    int waterTick;
    ForbiddenIslandWorld(String gameMode) {

        this.waterHeight = 0;
        this.waterTick = 0;
        this.board = null;
        this.isPaused = false;
        this.thePlayer = null;
        this.pieces = null;
        this.chopper = null;
        
        if (gameMode.equals("m")) {
            this.board = this.makeMountain(false);
        }
        else if (gameMode.equals("r")) {
            this.board = this.makeMountain(true);
        }
        else  if(gameMode.equals("t")) {
            this.board = this.makeTerrain();
        }
        
        // this.thePlayer = new Player(this.findValidLoc(), new Mt<Target>());
        // Target t1 = new Target(this.findValidLoc())
        // Target t2 = new Target(this.findValidLoc())
        // Target t2 = new Target(this.findValidLoc())
        // Target t2 = new Target(this.findValidLoc())
        // this.chopper = new HelicopterTarget(this.findValidLoc(), this.pieces) 
        
    }
    // Creates a standard map
    IList<Cell> makeMountain(boolean isRandom) {

        Random randy = new Random();

        double maxHeight = ISLAND_SIZE / 2;

        ArrayList<ArrayList<Double>> newBoard = new ArrayList<ArrayList<Double>>();

        for (int index1 = 0; index1 < ISLAND_SIZE; index1 += 1) {

            newBoard.add(new ArrayList<Double>());
        }

        for (int index1 = 0; index1 < newBoard.size(); index1 += 1) {
            for(int index2 = 0; index2 < ISLAND_SIZE; index2 += 1) {
                newBoard.get(index1).add(maxHeight - (Math.abs(maxHeight - index1) + (Math.abs(maxHeight - index2))));
            }
        }

        if (isRandom) {
            for (int index1 = 0; index1 < newBoard.size(); index1 += 1) {
                for (int index2 = 0; index2 < newBoard.get(index1).size(); index2 += 1) {
                    if (newBoard.get(index1).get(index2) > 0.0) {
                        newBoard.get(index1).set(index2, (double)randy.nextInt(32) + 1);
                    }
                }
            }
        }
        return new ArrDub2ListCell().apply(newBoard);

    }
    // Makes a realistic randomly generated world
    IList<Cell> makeTerrain() {
        int iS = ISLAND_SIZE;
        int hS = ISLAND_SIZE / 2;
        ArrayList<ArrayList<Double>> newBoard = new ArrayList<ArrayList<Double>>();

        for (int index1 = 0; index1 <= iS; index1 += 1) {

            newBoard.add(new ArrayList<Double>());

            for (int index2 = 0; index2 <= iS; index2 += 1) {
                newBoard.get(index1).add(0.0);
            }

        }

        // gives the board its heights
        this.startTerrain(newBoard, iS);

        // top left quadrant
        this.terrainProcedure(iS, newBoard, 0, 0, hS, 0, 0, hS, 
                hS, hS);
        // top right quadrant
        this.terrainProcedure(iS, newBoard, hS, 0, iS, 0, hS, 
                hS, iS, hS);
        // bottom left quadrant
        this.terrainProcedure(iS, newBoard, 0, hS, hS, 
                hS, 0, iS, hS, iS);
        // bottom right quadrant
        this.terrainProcedure(iS, newBoard, hS, hS, 
                iS, hS, hS, iS, iS, 
                iS);

        return new ArrDub2ListCell().apply(newBoard);
    }

    void startTerrain(ArrayList<ArrayList<Double>> newBoard, int size) {
        newBoard.get(0).set(0, 0.0);
        newBoard.get(0).set(size, 0.0);
        newBoard.get(size).set(0, 0.0);
        newBoard.get(size).set(size, 0.0);

        newBoard.get(size / 2).set(size / 2, 1.0);
    }

    void terrainProcedure(int size, ArrayList<ArrayList<Double>> board, int tLx, int tLy, int tRx, 
            int tRy, int bLx, int bLy, int bRx, int bRy) {
        Random ran = new Random();
        // corner heights
        double tL = board.get(tLx).get(tLy);
        double tR = board.get(tRx).get(tRy);
        double bL = board.get(bLx).get(bLy);
        double bR = board.get(bRx).get(bRy);
        // new heights
        double t = (ran.nextDouble() * size / 2) + ((tL + tR) / 2);
        double b = (ran.nextDouble() * size / 2) + ((bL + bR) / 2);
        double l = (ran.nextDouble() * size / 2) + ((tL + bL) / 2);
        double r = (ran.nextDouble() * size / 2) + ((tR + bR) / 2);
        double m = (ran.nextDouble() * size / 2) + ((tL + tR + bL + bR) / 4);

        // constants 
        int topX = (tRx + tLx) / 2;
        int bottomX = (bLx + bRx) / 2;
        int leftX = (tLx + bLx) / 2;
        int rightX = (tRx + bRx) / 2;
        int topY = (tRy + tLy) / 2;
        int bottomY = (bLy + bRy) / 2;
        int leftY = (tLy + bLy) / 2;
        int rightY = (tLy + bRy) / 2;

        // top
        board.get(topX).set(topY, t);
        // bottom
        board.get(bottomX).set(bottomY, b);
        // left
        board.get(leftX).set(leftY, l);
        // right
        board.get(rightX).set(rightY, r);
        // middle
        board.get(topX).set(leftY, m);

        // recursion
        /*if () {
      this.terrainProcedure(size, board, tLx, tLy, tRx, tRy, bLx, bLy, bRx, bRy);
      }*/
    }

    // pauses the game
    void pauseGame() {
        this.isPaused = true;
    }
    // Draws the World
    public WorldImage makeImage() {
        DisplayCellsVisitor dCVisitor = new DisplayCellsVisitor(this.board, this.waterHeight);
        if (this.isPaused) {
            return new OverlayImages(dCVisitor.board.accept(dCVisitor), new RectangleImage(
                    new Posn(0, 0), 1280, 1280, new Color(255, 0, 0, 150)));
        }
        return dCVisitor.board.accept(dCVisitor);
    }

    // Handling key presses
    public void onKeyEvent(String ke) {

        if (ke.equals("m")) {
            this.board = this.makeMountain(false);
        }
        else if (ke.equals("r")) {
            this.board = this.makeMountain(true);
        }
        else if (ke.equals("t")) {
            this.board = this.makeTerrain();
        }
        else if (ke.equals("p") && !this.isPaused) {
            this.isPaused = true;
        }
        else if (ke.equals("p")) {
            this.isPaused = false;
        }
        else if (!this.isPaused){
            this.thePlayer.movePlayer(ke);
        }
        else {
            // DO NOTHING
        }

    }

    // Handling time passage and subsequent flooding
    public void onTick() {
        if (this.waterTick >= 10) {
            this.waterHeight += 1;
            this.board = this.board.map(new UpdateFlood(this.waterHeight));
            this.waterTick = 0;
        }
        else {
            this.waterTick += 1;
        }
    }
}

// represent the player's avatar: the pilot
class Player {

    // Keeps track of position, appearance, and collected parts
    Cell location;
    WorldImage picture;
    IList<Target> inventory;

    Player(Cell location, IList<Target> inventory) {
        this.location = location;
        this.inventory = inventory;
        this.picture = new FromFileImage(new Posn(this.location.x, this.location.y), "pilot-icon.png");
        this.location.hasPlayer = true;
    }

    // Move the player left, right, up, or down with the arrow keys
    Player movePlayer(String ke) {

        if (this.safe(ke)) {
            if (ke.equals("left")) {
                this.location.hasPlayer = false;
                return new Player(this.location.left, this.inventory);
            }
            else if (ke.equals("down")) {
                this.location.hasPlayer = false;
                return new Player(this.location.bottom, this.inventory);
            }
            else if (ke.equals("right")) {
                this.location.hasPlayer = false;
                return new Player(this.location.right, this.inventory);
            }
            else if (ke.equals("up")) {
                this.location.hasPlayer = false;
                return new Player(this.location.top, this.inventory);
            }
            else {
                return this;
            }
        }
        else {
            return this;
        }
    }
    
    // Pick up the given Target
    IList<Target> pickUpTarget(Target t) {
        return t.pickedUp(this);
    }
    
    // Can this Player pick up the given Target?
    boolean touchingTarget(Target t) {
        return t.touching(this.location.x, this.location.y);
    }

    // Can the player move in the given direction?
    boolean safe(String dir) {

        return this.location.safeHelp(dir);


    }
}

// represents objects the player needs to obtain
class Target {

    // Keeps track of position
    Cell location;

    Target(Cell location) {
        this.location = location;
    }

    // Is the other object's position the same as this one's?
    boolean touching(int x, int y) {

        return this.location.x == x && this.location.y == y;

    }
    // Is this target the same as the given one?
    boolean sameTarget(Target other) {
        return this.touching(other.location.x, other.location.y);

    }
    
    // Add this Target to the Player's inventory
    IList<Target> pickedUp(Player p) {
        this.location.hasTarget = false;
        return p.inventory.add(this);
        
    }
    
}

// represents the actual helicopter. This can only be picked up
// after all the other targets have been obtained.
class HelicopterTarget extends Target {

    // A list of all other pieces
    IList<Target> pieces;
    // A picture to represent the chopper
    WorldImage picture;

    HelicopterTarget(Cell location, IList<Target> pieces) {
        super(location);
        this.pieces = pieces;
        this.picture = new FromFileImage(new Posn(this.location.x, this.location.y), "helicopter.png");
    }

    // Does the given player have all the pieces?
    boolean canBeRepaired(Player p) {
        if (this.pieces.length() != p.inventory.length()) {
            return false;
        }
        else {
            return this.pieces.accept(new TargetListVisitor(p.inventory));
        }
    }

}

//Goes through a list of Targets, seeing if each one is in the given 2nd list
class TargetListVisitor implements IVisitor<Target, Boolean> {

    IList<Target> src;

    TargetListVisitor(IList<Target> src) {
        this.src = src;
    }

    public Boolean visit(Cons<Target> c) {
        return src.accept(new TargetVisitor(c.first)) &&
                c.rest.accept(this);
    }

    public Boolean visit(Mt<Target> m) {
        return false;
    }
    // TODO
    public Boolean visit(Node<Target> n) {
        throw new IllegalArgumentException("IBST is not a valid argument");
    }

    // TODO
    public Boolean visit(Leaf<Target> n) {
        throw new IllegalArgumentException("IBST is not a valid argument");
    }
}

//Is the given Target in a list of Targets?
class TargetVisitor implements IVisitor<Target, Boolean> {

    Target toFind;

    TargetVisitor(Target toFind) {
        this.toFind = toFind;
    }
    // TODO
    public Boolean visit(Cons<Target> c) {
        return c.first.sameTarget(toFind) ||
                c.rest.accept(this);
    }
    // TODO
    public Boolean visit(Mt<Target> m) {
        return false;
    }

    // TODO
    public Boolean visit(Node<Target> n) {
        throw new IllegalArgumentException("IBST is not a valid argument");
    }

    // TODO
    public Boolean visit(Leaf<Target> n) {
        throw new IllegalArgumentException("IBST is not a valid argument");
    }

}

//represents examples and tests for the ForbiddenIslandWorld class
class ExamplesIsland {
    ForbiddenIslandWorld nullWorld = new ForbiddenIslandWorld("not a world");

    ForbiddenIslandWorld mountain = new ForbiddenIslandWorld("not a mountain yet"); 
    ForbiddenIslandWorld random = new ForbiddenIslandWorld("not a random yet");
    ForbiddenIslandWorld terrain = new ForbiddenIslandWorld("not a terrain yet");

    ArrayList<Double> arrayListD1 = new ArrayList<Double>();
    ArrayList<Double> arrayListD10 = new ArrayList<Double>();
    ArrayList<Double> arrayListD64 = new ArrayList<Double>();
    ArrayList<ArrayList<Double>> arrayListD = new ArrayList<ArrayList<Double>>();

    Cell cX0Y0 = new Cell(0, 0, 0);
    Cell cX0Y1 = new Cell(0, 0, 1);
    Cell cX0Y2 = new Cell(0, 0, 2);
    Cell cX0Y3 = new Cell(0, 0, 3);
    Cell cX0Y4 = new Cell(0, 0, 4);
    Cell cX1Y0 = new Cell(0, 1, 0);
    Cell cX1Y1 = new Cell(0, 1, 1);
    Cell cX1Y2 = new Cell(0, 1, 2);
    Cell cX1Y3 = new Cell(0, 1, 3);
    Cell cX1Y4 = new Cell(0, 1, 4);
    Cell cX2Y0 = new Cell(0, 2, 0);
    Cell cX2Y1 = new Cell(0, 2, 1);
    Cell cX2Y2 = new Cell(0, 2, 2);
    Cell cX2Y3 = new Cell(0, 2, 3);
    Cell cX2Y4 = new Cell(0, 2, 4);
    Cell cX3Y0 = new Cell(0, 3, 0);
    Cell cX3Y1 = new Cell(0, 3, 1);
    Cell cX3Y2 = new Cell(0, 3, 2);
    Cell cX3Y3 = new Cell(0, 3, 3);
    Cell cX3Y4 = new Cell(0, 3, 4);
    Cell cX4Y0 = new Cell(0, 4, 0);
    Cell cX4Y1 = new Cell(0, 4, 1);
    Cell cX4Y2 = new Cell(0, 4, 2);
    Cell cX4Y3 = new Cell(0, 4, 3);
    Cell cX4Y4 = new Cell(0, 4, 4);


    ArrayList<Cell> aL0 = new ArrayList<Cell>();
    ArrayList<Cell> aL1 = new ArrayList<Cell>();
    ArrayList<Cell> aL2 = new ArrayList<Cell>();
    ArrayList<Cell> aL3 = new ArrayList<Cell>();
    ArrayList<Cell> aL4 = new ArrayList<Cell>();
    ArrayList<ArrayList<Cell>> aLAll = new ArrayList<ArrayList<Cell>>();
    ArrayList<Double> aI0 = new ArrayList<Double>();
    ArrayList<Double> aI1 = new ArrayList<Double>();
    ArrayList<Double> aI2 = new ArrayList<Double>();
    ArrayList<Double> aI3 = new ArrayList<Double>();
    ArrayList<Double> aI4 = new ArrayList<Double>();
    ArrayList<ArrayList<Double>> aIAll = new ArrayList<ArrayList<Double>>();

    Cell c00 = new Cell(0, 0, 0);
    Cell c01 = new Cell(20, 0, 1);
    OceanCell c10 = new OceanCell(1, 0);
    Cell c11 = new Cell(-20, 0, 3);


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
    IList<Cell> iLAll = new Mt<Cell>();
    Cell s = new OceanCell(0, 1);


    Cell colorTest1 = new OceanCell(0, 0);
    Cell colorTest2 = new Cell(10, 1, 0, false);
    Cell colorTest3 = new OceanCell(2, 0);
    Cell colorTest4 = new Cell(3, 3, 0, false);
    Cell colorTest5 = new OceanCell(4, 0);
    Cell colorTest6 = new Cell(22, 5, 0, false);
    Cell colorTest7 = new Cell(20, 6, 0, false);
    Cell colorTest8 = new Cell(7, 7, 0, false);
    Cell colorTest9 = new Cell(16, 8, 0, false);
    Cell colorTest10 = new Cell(9, 9, 0, false);
    Cell colorTest11 = new OceanCell(10, 0);
    Cell colorTest12 = new Cell(9, 11, 0, false);
    Cell colorTest13 = new Cell(19, 12, 0, false);
    Cell colorTest14 = new Cell(13, 13, 0, false);
    Cell colorTest15 = new Cell(1, 14, 0, false);
    Cell colorTest16 = new Cell(10, 15, 0, false);
    Cell colorTest17 = new OceanCell(16, 0);
    Cell colorTest18 = new Cell(16, 17, 0, false);
    Cell colorTest19 = new Cell(31, 18, 0, false);
    Cell colorTest20 = new Cell(18, 19, 0, false);
    IList<Cell> theList = new Cons<Cell>(colorTest1, new Cons<Cell>(colorTest2,
            new Cons<Cell>(colorTest3, new Cons<Cell>(colorTest4, new Cons<Cell>(colorTest5,
                    new Cons<Cell>(colorTest6, new Cons<Cell>(colorTest7, new Cons<Cell>(colorTest8,
                            new Cons<Cell>(colorTest9, new Cons<Cell>(colorTest10,
                                    new Cons<Cell>(colorTest11, new Cons<Cell>(colorTest12,
                                            new Cons<Cell>(colorTest13, new Cons<Cell>(colorTest14,
                                                    new Cons<Cell>(colorTest15, new Cons<Cell>(colorTest16,
                                                            new Cons<Cell>(colorTest17, new Cons<Cell>(colorTest18,
                                                                    new Cons<Cell>(colorTest19, new Mt<Cell>())))))))))))))))))));

    Cell land7 = new Cell(0, 10, 10);
    Cell land8 = new Cell(-1, 10, 11);
    Cell land9 = new Cell(70, 10, 12);
    OceanCell ocean7 = new OceanCell(13, 13);
    IList<Cell> list1 = new Cons<Cell>(land7, new Cons<Cell>(land8,
            new Cons<Cell>(land9, new Cons<Cell>(ocean7, new Mt<Cell>()))));
    Cell land7b = new Cell(0, 10, 10, true);
    Cell land8b = new Cell(-1, 10, 11, true);
    Cell land9b = new Cell(70, 10, 12, false);
    IList<Cell> list1b = new Cons<Cell>(land7b, new Cons<Cell>(land8b,
            new Cons<Cell>(land9b, new Cons<Cell>(ocean7, new Mt<Cell>()))));   

    IList<Cell> list2 = new Mt<Cell>();

    IFunc<Cell, Cell> upFld = new UpdateFlood(64);
    ArrDub2ListCell aDLC = new ArrDub2ListCell();
    IComp<Cell> compCell = new CompCell();
    IComp<Cell> compRand = new RandCellComp();

    // initializes the examples class
    void initialize() {

        this.cX0Y0 = new Cell(1, 0, 0);
        this.cX0Y1 = new Cell(1, 0, 1);
        this.cX0Y2 = new Cell(1, 0, 2);
        this.cX0Y3 = new Cell(1, 0, 3);
        this.cX0Y4 = new Cell(1, 0, 4);
        this.cX1Y0 = new Cell(1, 1, 0);
        this.cX1Y1 = new Cell(1, 1, 1);
        this.cX1Y2 = new Cell(1, 1, 2);
        this.cX1Y3 = new Cell(1, 1, 3);
        this.cX1Y4 = new Cell(1, 1, 4);
        this.cX2Y0 = new Cell(1, 2, 0);
        this.cX2Y1 = new Cell(1, 2, 1);
        this.cX2Y2 = new Cell(1, 2, 2);
        this.cX2Y3 = new Cell(1, 2, 3);
        this.cX2Y4 = new Cell(1, 2, 4);
        this.cX3Y0 = new Cell(1, 3, 0);
        this.cX3Y1 = new Cell(1, 3, 1);
        this.cX3Y2 = new Cell(1, 3, 2);
        this.cX3Y3 = new Cell(1, 3, 3);
        this.cX3Y4 = new Cell(1, 3, 4);
        this.cX4Y0 = new OceanCell(4, 0);
        this.cX4Y1 = new OceanCell(4, 1);
        this.cX4Y2 = new OceanCell(4, 2);
        this.cX4Y3 = new OceanCell(4, 3);
        this.cX4Y4 = new OceanCell(4, 4);

        // IList cell
        IList<Cell> iL1 = new Cons<Cell>(cX4Y1,
                new Cons<Cell>(cX4Y2,
                        new Cons<Cell>(cX4Y3,
                                new Cons<Cell>(cX4Y4, new Mt<Cell>()))));
        IList<Cell> iL2 = new Cons<Cell>(cX3Y1,
                new Cons<Cell>(cX3Y2,
                        new Cons<Cell>(cX3Y3,
                                new Cons<Cell>(cX3Y4,
                                        new Cons<Cell>(cX4Y0, iL1)))));
        IList<Cell> iL3 = new Cons<Cell>(cX2Y1,
                new Cons<Cell>(cX2Y2,
                        new Cons<Cell>(cX2Y3,
                                new Cons<Cell>(cX2Y4,
                                        new Cons<Cell>(cX3Y0, iL2)))));
        IList<Cell> iL4 = new Cons<Cell>(cX1Y1,
                new Cons<Cell>(cX1Y2,
                        new Cons<Cell>(cX1Y3,
                                new Cons<Cell>(cX1Y4,
                                        new Cons<Cell>(cX2Y0, iL3)))));
        this.iLAll = new Cons<Cell>(cX0Y0,      
                new Cons<Cell>(cX0Y1,
                        new Cons<Cell>(cX0Y2,
                                new Cons<Cell>(cX0Y3,
                                        new Cons<Cell>(cX0Y4,
                                                new Cons<Cell>(cX1Y0, iL4))))));


        // array list cell
        aL0.clear();
        aL0.add(cX0Y0);
        aL0.add(cX0Y1);
        aL0.add(cX0Y2);
        aL0.add(cX0Y3);
        aL0.add(cX0Y4);
        aL1.clear();
        aL1.add(cX1Y0);
        aL1.add(cX1Y1);
        aL1.add(cX1Y2);
        aL1.add(cX1Y3);
        aL1.add(cX1Y4);
        aL2.clear();
        aL2.add(cX2Y0);
        aL2.add(cX2Y1);
        aL2.add(cX2Y2);
        aL2.add(cX2Y3);
        aL2.add(cX2Y4);
        aL3.clear();
        aL3.add(cX3Y0);
        aL3.add(cX3Y1);
        aL3.add(cX3Y2);
        aL3.add(cX3Y3);
        aL3.add(cX3Y4);
        aL4.clear();
        aL4.add(cX4Y0);
        aL4.add(cX4Y1);
        aL4.add(cX4Y2);
        aL4.add(cX4Y3);
        aL4.add(cX4Y4);
        aLAll.clear();
        aLAll.add(aL0);
        aLAll.add(aL1);
        aLAll.add(aL2);
        aLAll.add(aL3);
        aLAll.add(aL4);


        // array list double
        aI0.clear();
        aI0.add(1.0);
        aI0.add(1.0);
        aI0.add(1.0);
        aI0.add(1.0);
        aI0.add(1.0);
        aI1.clear();
        aI1.add(1.0);
        aI1.add(1.0);
        aI1.add(1.0);
        aI1.add(1.0);
        aI1.add(1.0);
        aI2.clear();
        aI2.add(1.0);
        aI2.add(1.0);
        aI2.add(1.0);
        aI2.add(1.0);
        aI2.add(1.0);
        aI3.clear();
        aI3.add(1.0);
        aI3.add(1.0);
        aI3.add(1.0);
        aI3.add(1.0);
        aI3.add(1.0);
        aI4.clear();
        aI4.add(0.0);
        aI4.add(0.0);
        aI4.add(0.0);
        aI4.add(0.0);
        aI4.add(0.0);
        aIAll.clear();
        aIAll.add(aI0);
        aIAll.add(aI1);
        aIAll.add(aI2);
        aIAll.add(aI3);
        aIAll.add(aI4);

        arrayListD1.clear();
        arrayListD10.clear();
        arrayListD64.clear();

        // arrayList with 1 element
        for (int index = 0; index < 1; index += 1) {
            this.arrayListD1.add(1.0);
        } 

        // arrayList with 10 elements
        for (int index = 0; index < 10; index += 1) {
            this.arrayListD10.add(10.0);
        }

        // arrayList with 64 elements
        for (int index = 0; index < 64; index += 1) {
            this.arrayListD64.add(64.0);
        }
    }
    // initializes the neighbors of the cells
    void initializeNeighbors() {
        aDLC.assignAllNeighbors(this.aLAll);
    }
    // initializes the Worlds
    void initializeWorlds() {
        this.nullWorld.board = null;
        this.mountain.board = this.nullWorld.makeMountain(false);
        this.random.board = this.nullWorld.makeMountain(true);
        this.terrain.board = this.nullWorld.makeTerrain();
    }

    // tests add for the class IList<T>
    void testAdd(Tester t) {
        IList<String> iS = new Cons<String>("one", new Mt<String>());
        t.checkExpect(iS.add("two"), new Cons<String>("two", new Cons<String>("one", new Mt<String>())));
    }

    // tests accept for the interfaces IList<T> and IBST<T>
    void testAccept(Tester t) {
        Mt<Cell> mT = new Mt<Cell>();
        Cons<Cell> cons = new Cons<Cell>(new Cell(5, 5, 7), mT);
        Leaf<Cell> leaf = new Leaf<Cell>();
        Node<Cell> node = new Node<Cell>(new Cell(5, 5, 7), leaf, leaf);
        DisplayCellsVisitor dCV = new DisplayCellsVisitor(cons, 0);
        t.checkExpect(leaf.accept(dCV), dCV.visit(leaf));
        t.checkExpect(node.accept(dCV), dCV.visit(node));
        t.checkException(new IllegalArgumentException("IList is not a valid argument"), cons, "accept", dCV);
        t.checkException(new IllegalArgumentException("IList is not a valid argument"), mT, "accept", dCV);
    }
    // tests apply for the class UpdateFlood
    void testUpdateFlood(Tester t) {
        Cell c1 = new Cell(66, 2, 3);
        Cell c2 = new Cell(64, 2, 3);
        Cell c3 = new Cell(55, 2, 3);
        Cell c4 = new OceanCell(2, 3);
        t.checkExpect(this.upFld.apply(c1), c1);
        t.checkExpect(this.upFld.apply(c2), new Cell(64, 2, 3, true));
        t.checkExpect(this.upFld.apply(c3), new Cell(55, 2, 3, true));
        t.checkExpect(this.upFld.apply(c4), c4);
    } 
    // tests map for the class IList<T>
    void testMap(Tester t) {
        t.checkExpect(this.list1.map(this.upFld), this.list1b);
    }

    // tests append for the IList interface
    void testAppend(Tester t) {
        IList<String> mTS = new Mt<String>();
        IList<String> i1 = new Cons<String>("happy", new Cons<String>("birthday", mTS));
        IList<String> i2 = new Cons<String>("Mr", new Cons<String>("Jones", mTS));
        IList<String> i3 = new Cons<String>("happy", new Cons<String>("birthday", 
                new Cons<String>("Mr", new Cons<String>("Jones", mTS))));
        t.checkExpect(i1.append(i2), i3);
        t.checkExpect(mTS.append(i1), i1);
        t.checkExpect(i2.append(mTS), i2);
    }

    // tests list2Tree for the IList<T> class TODO
    void testList2Tree(Tester t) {

    }
    // tests lengthT for the IList<T> class 
    void testLengthT(Tester t) {
        initializeWorlds();
        IList<Integer> list = new Cons<Integer>(2, new Cons<Integer>(3, new Mt<Integer>()));
        IList<Integer> mT = new Mt<Integer>();
        t.checkExpect(mountain.board.lengthT(0), 4096);
        t.checkExpect(list.lengthT(0), 2);
        t.checkExpect(list.lengthT(2), 4);
        t.checkExpect(mT.lengthT(0), 0);
        t.checkExpect(mT.lengthT(100), 100);
    }
    // tests length for the IList<T> class 
    void testLength(Tester t) {
        initializeWorlds();
        IList<Integer> list = new Cons<Integer>(2, new Cons<Integer>(3, new Mt<Integer>()));
        IList<Integer> mT = new Mt<Integer>();
        t.checkExpect(mountain.board.length(), 4096);
        t.checkExpect(list.length(), 2);
        t.checkExpect(mT.length(), 0);
    }
    // tests compare for the RandomCell
    void testRandCellCompare(Tester t) {
        Cell c1 = new Cell(4, 5, 4);
        Cell c2 = new Cell(4, 4, 3);
        IComp<Cell> rand = new RandCellComp();
        // its random...
        t.checkExpect(rand.compare(c1, c2) <= 0 || rand.compare(c1, c2) >= 0, true);
    }
    // tests compare in the CompCell class 
    void testCompCell(Tester t) {
        IComp<Cell> comp = new CompCell();
        Cell c1 = new Cell(4, 5, 4);
        Cell c2 = new Cell(4, 4, 3);
        Cell c3 = new Cell(9, 4, 4);
        Cell c4 = new Cell(8, 4, 5);
        Cell c5 = new Cell(9, 4, 4);
        Cell c6 = new OceanCell(3, 4);
        t.checkExpect(comp.compare(c3, c1), -1);
        t.checkExpect(comp.compare(c3, c2), 1);
        t.checkExpect(comp.compare(c3, c4), -1);
        t.checkExpect(comp.compare(c3, c6), 1);
        t.checkExpect(comp.compare(c3, c5), 0);
        t.checkExpect(comp.compare(c2, c2), 0);
    }


    // tests height2Cell for the class ArrDub2ListCell
    void testHeight2Cell(Tester t) {
        t.checkExpect(aDLC.height2Cell(2.0, 20, 40), new Cell(2, 20, 40));
        t.checkExpect(aDLC.height2Cell(10.0, 0, 40), new Cell(10, 0, 40));
        t.checkExpect(aDLC.height2Cell(-5, 20, 40), new OceanCell(20, 40));
        t.checkExpect(aDLC.height2Cell(0, 20, 45), new OceanCell(20, 45));
    }
    //tests arrDoubleToCell for the class ArrDub2ListCell
    void testArrDoubleToCell(Tester t) {
        this.initialize();
        ArrayList<Double> a1 = new ArrayList<Double>();
        ArrayList<Cell> c1 = new ArrayList<Cell>();

        a1.add(1.0);
        c1.add(new Cell(1.0, 0, 0, false));
        t.checkExpect(aDLC.doubleArr2CellArr(a1, 0), c1);

        a1.add(2.0);
        c1.add(new Cell(2.0, 0, 1));
        t.checkExpect(aDLC.doubleArr2CellArr(a1, 0), c1);

        a1.add(-5.0);
        c1.add(new OceanCell(0, 2));
        t.checkExpect(aDLC.doubleArr2CellArr(a1, 0), c1);
    }
    //tests dubArrArr2CellArrArr for the class ArrDub2ListCell
    void testHelp(Tester t) {
        this.initialize();
        ArrayList<ArrayList<Double>> aDub = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Cell>> aCell = new ArrayList<ArrayList<Cell>>();
        ArrayList<Double> a1 = new ArrayList<Double>();
        ArrayList<Double> a2 = new ArrayList<Double>();
        ArrayList<Cell> c1 = new ArrayList<Cell>();
        ArrayList<Cell> c2 = new ArrayList<Cell>();
        // empty list check
        t.checkExpect(aDub, aCell);
        t.checkExpect(aDLC.dubArrArr2CellArrArr(aDub), aCell);

        // one list with one item
        aDub.add(a1);
        aCell.add(c1);

        a1.add(1.0);
        c1.add(new Cell(1.0, 0, 0));
        t.checkExpect(aDLC.dubArrArr2CellArrArr(aDub), aCell);

        a1.add(3.0);
        a1.add(2.0);
        c1.add(new Cell(3.0, 0, 1));
        c1.add(new Cell(2.0, 0, 2));
        t.checkExpect(aDLC.dubArrArr2CellArrArr(aDub), aCell);

        // two lists plus ocean
        a2.add(5.0);
        //ocean cell
        a2.add(-6.0);
        c2.add(new Cell(5.0, 1, 0));
        c2.add(new OceanCell(1, 1));
        t.checkExpect(aDLC.dubArrArr2CellArrArr(aDub), aCell);

        // the big list
        t.checkExpect(aDLC.dubArrArr2CellArrArr(aIAll), aLAll);

    }
    // tests assignNeighbors for the class ArrDub2ListCell
    void testAssignNeighbors(Tester t) {
        this.initialize();

        // top left corner (0, 0)
        t.checkExpect(cX0Y0.left, null);
        t.checkExpect(cX0Y0.right, null);
        t.checkExpect(cX0Y0.top, null);
        t.checkExpect(cX0Y0.bottom, null);

        this.aDLC.assignNeighbors(cX0Y0, 0, 0, aLAll);
        t.checkExpect(cX0Y0.top, cX0Y0);
        t.checkExpect(cX0Y0.left, cX0Y0);
        t.checkExpect(cX0Y0.right, cX1Y0);
        t.checkExpect(cX0Y0.bottom, cX0Y1);

        // top right corner (4, 0)
        t.checkExpect(cX4Y0.left, null);
        t.checkExpect(cX4Y0.right, null);
        t.checkExpect(cX4Y0.top, null);
        t.checkExpect(cX4Y0.bottom, null);

        this.aDLC.assignNeighbors(cX4Y0, 4, 0, aLAll);
        t.checkExpect(cX4Y0.top, cX4Y0);
        t.checkExpect(cX4Y0.left, cX3Y0);
        t.checkExpect(cX4Y0.right, cX4Y0);
        t.checkExpect(cX4Y0.bottom, cX4Y1);

        // bottom left corner (0, 4)
        t.checkExpect(cX0Y4.left, null);
        t.checkExpect(cX0Y4.right, null);
        t.checkExpect(cX0Y4.top, null);
        t.checkExpect(cX0Y4.bottom, null);

        this.aDLC.assignNeighbors(cX0Y4, 0, 4, aLAll);
        t.checkExpect(cX0Y4.top, cX0Y3);
        t.checkExpect(cX0Y4.left, cX0Y4);
        t.checkExpect(cX0Y4.right, cX1Y4);
        t.checkExpect(cX0Y4.bottom, cX0Y4);

        // bottom right corner (4, 4)
        t.checkExpect(cX4Y4.left, null);
        t.checkExpect(cX4Y4.right, null);
        t.checkExpect(cX4Y4.top, null);
        t.checkExpect(cX4Y4.bottom, null);

        this.aDLC.assignNeighbors(cX4Y4, 4, 4, aLAll);
        t.checkExpect(cX4Y4.top, cX4Y3);
        t.checkExpect(cX4Y4.left, cX3Y4);
        t.checkExpect(cX4Y4.right, cX4Y4);
        t.checkExpect(cX4Y4.bottom, cX4Y4);

        // all neighbors (2, 2)
        t.checkExpect(cX2Y2.left, null);
        t.checkExpect(cX2Y2.right, null);
        t.checkExpect(cX2Y2.top, null);
        t.checkExpect(cX2Y2.bottom, null);

        this.aDLC.assignNeighbors(cX2Y2, 2, 2, aLAll);
        t.checkExpect(cX2Y2.top, cX2Y1);
        t.checkExpect(cX2Y2.left, cX1Y2);
        t.checkExpect(cX2Y2.right, cX3Y2);
        t.checkExpect(cX2Y2.bottom, cX2Y3);
    }
    // tests assignAllNeighbors for the class ArrDub2ListCell
    void testAssignAllNeighbors(Tester t) {
        this.initialize();

        // top left corner (0, 0)
        t.checkExpect(cX0Y0.left, null);
        t.checkExpect(cX0Y0.right, null);
        t.checkExpect(cX0Y0.top, null);
        t.checkExpect(cX0Y0.bottom, null);

        // top right corner (4, 0)
        t.checkExpect(cX4Y0.left, null);
        t.checkExpect(cX4Y0.right, null);
        t.checkExpect(cX4Y0.top, null);
        t.checkExpect(cX4Y0.bottom, null);

        // bottom left corner (0, 4)
        t.checkExpect(cX0Y4.left, null);
        t.checkExpect(cX0Y4.right, null);
        t.checkExpect(cX0Y4.top, null);
        t.checkExpect(cX0Y4.bottom, null);

        // bottom right corner (4, 4)
        t.checkExpect(cX4Y4.left, null);
        t.checkExpect(cX4Y4.right, null);
        t.checkExpect(cX4Y4.top, null);
        t.checkExpect(cX4Y4.bottom, null);

        // all neighbors (2, 2)
        t.checkExpect(cX2Y2.left, null);
        t.checkExpect(cX2Y2.right, null);
        t.checkExpect(cX2Y2.top, null);
        t.checkExpect(cX2Y2.bottom, null);

        this.aDLC.assignAllNeighbors(aLAll);
        t.checkExpect(cX0Y0.top, cX0Y0);
        t.checkExpect(cX0Y0.left, cX0Y0);
        t.checkExpect(cX0Y0.right, cX1Y0);
        t.checkExpect(cX0Y0.bottom, cX0Y1);

        t.checkExpect(cX4Y0.top, cX4Y0);
        t.checkExpect(cX4Y0.left, cX3Y0);
        t.checkExpect(cX4Y0.right, cX4Y0);
        t.checkExpect(cX4Y0.bottom, cX4Y1);

        t.checkExpect(cX0Y4.top, cX0Y3);
        t.checkExpect(cX0Y4.left, cX0Y4);
        t.checkExpect(cX0Y4.right, cX1Y4);
        t.checkExpect(cX0Y4.bottom, cX0Y4);

        t.checkExpect(cX4Y4.top, cX4Y3);
        t.checkExpect(cX4Y4.left, cX3Y4);
        t.checkExpect(cX4Y4.right, cX4Y4);
        t.checkExpect(cX4Y4.bottom, cX4Y4);

        t.checkExpect(cX2Y2.top, cX2Y1);
        t.checkExpect(cX2Y2.left, cX1Y2);
        t.checkExpect(cX2Y2.right, cX3Y2);
        t.checkExpect(cX2Y2.bottom, cX2Y3);
    }
    //tests cellArrArr2cellList for the class ArrDub2ListCell
    void testCellArrArr2cellList(Tester t) {
        this.initialize();
        this.initializeNeighbors();
        t.checkExpect(aDLC.cellArrArr2cellList(aLAll), iLAll);
    }
    //tests apply for the class ArrDub2ListCell
    void testApply(Tester t) {
        this.initialize();
        this.initializeNeighbors();
        t.checkExpect(aDLC.apply(this.aIAll), this.iLAll);

    }    
    // tests visit(Cons) for the class DisplayVisitor  
    void testDisplayVisitC(Tester t) {
        Mt<Cell> mT = new Mt<Cell>();
        Cons<Cell> cons = new Cons<Cell>(new Cell(5, 5, 7), mT);    
        DisplayCellsVisitor dCV = new DisplayCellsVisitor(cons, 0);
        t.checkException(new IllegalArgumentException("IList is not a valid argument"), dCV, "visit", cons);

    }
    // tests visit(MT) for the class DisplayVisitor 
    void testDisplayVisitM(Tester t) {
        Mt<Cell> mT = new Mt<Cell>();
        DisplayCellsVisitor dCV = new DisplayCellsVisitor(mT, 0);
        t.checkException(new IllegalArgumentException("IList is not a valid argument"), dCV, "visit", mT);
    }
    // tests visit(Node) for the class DisplayVisitor 
    void testDisplayVisitN(Tester t) {
        Cons<Cell> cons = new Cons<Cell>(new Cell(5, 5, 7), new Mt<Cell>());
        Leaf<Cell> leaf = new Leaf<Cell>();
        Node<Cell> node = new Node<Cell>(new Cell(5, 5, 7), leaf, leaf);
        DisplayCellsVisitor dCV = new DisplayCellsVisitor(new Mt<Cell>(), 0);
        t.checkExpect(dCV.visit(node), new OverlayImages(node.data.displayCell(0), 
                new OverlayImages(new LineImage(new Posn(-1, -1), new Posn(-1, -1), 
                        new Color(255, 255, 255)),
                        new LineImage(new Posn(-1, -1), new Posn(-1, -1), 
                                new Color(255, 255, 255)))));

    }
    // tests visit(Leaf) for the class DisplayVisitor 
    void testDisplayVisitL(Tester t) {
        Cons<Cell> cons = new Cons<Cell>(new Cell(5, 5, 7), new Mt<Cell>());
        Leaf<Cell> leaf = new Leaf<Cell>();
        DisplayCellsVisitor dCV = new DisplayCellsVisitor(new Mt<Cell>(), 0);
        t.checkExpect(dCV.visit(leaf), new LineImage(new Posn(-1, -1), new Posn(-1, -1), 
                new Color(255, 255, 255)));
    }
    // tests cellColor for the class Cell and OceanCell
    void testCellColor(Tester t) {
        Cell c1 = new Cell(-1, 2, 3, false);
        Cell c2 = new Cell(-1, 2, 3, true);
        Cell c3 = new Cell(0, 1, 2, true);
        Cell c4 = new Cell(1, 2, 3);
        Cell c5 = new Cell(30, 5, 4);
        Cell c6 = new OceanCell(0, 0);
        t.checkExpect(c1.cellColor(0), new Color(1, 99, 0));
        t.checkExpect(c2.cellColor(0), new Color(0, 0, 99));
        t.checkExpect(c3.cellColor(0), new Color(0, 0, 100));
        t.checkExpect(c4.cellColor(0), new Color(0, 100, 0));
        t.checkExpect(c5.cellColor(0), new Color(210, 255, 210));
        t.checkExpect(c6.cellColor(0), new Color(0, 0, 120));
    }
    // tests displayCell for the class Cell 
    void testDisplayCell(Tester t) {
        Cell c1 = new Cell(-1, 2, 3, false);
        Cell c2 = new Cell(-1, 2, 3, true);
        Cell c3 = new Cell(0, 1, 2, true);
        Cell c4 = new Cell(1, 2, 3);
        Cell c5 = new Cell(30, 5, 4);
        Cell c6 = new OceanCell(0, 0);
        t.checkExpect(c1.displayCell(0), new RectangleImage(new Posn(25, 35), 10, 10, c1.cellColor(0)));
        t.checkExpect(c2.displayCell(0), new RectangleImage(new Posn(25, 35), 10, 10, c2.cellColor(0)));
        t.checkExpect(c3.displayCell(0), new RectangleImage(new Posn(15, 25), 10, 10, c3.cellColor(0)));
        t.checkExpect(c4.displayCell(0), new RectangleImage(new Posn(25, 35), 10, 10, c4.cellColor(0)));
        t.checkExpect(c5.displayCell(0), new RectangleImage(new Posn(55, 45), 10, 10, c5.cellColor(0)));
        t.checkExpect(c6.displayCell(0), new RectangleImage(new Posn(5, 5), 10, 10, c6.cellColor(0)));
    }
    // tests floodDanger for the class Cell TODO
    void testFloodDanger(Tester t) {

    }
    // tests isOcean for the class Cell and OceanCell 
    void testIsOcean(Tester t) {
        t.checkExpect(new Cell(5, 2, 3).isOcean(), false);
        t.checkExpect(new Cell(-1, 2, 3).isOcean(), false);
        t.checkExpect(new Cell(0, 2, 3).isOcean(), false);
        t.checkExpect(new OceanCell(2, 3).isOcean(), true);
    }

    // tests updateFloodHelp  for the class Cell TODO
    void testFloodHelp(Tester t) {

    }


    // tests makeMountain for the class ForbiddenIslandWorld TODO
    void testMakeMountain(Tester t) {

    }
    // tests makeRandom for the class ForbiddenIslandWorld TODO
    void testMakeRandom(Tester t) {

    }
    // tests makeTerrain for the class ForbiddenIslandWorld TODO
    void testMakeTerrain(Tester t) {

    }
    // tests terrainProcedure for the class ForbiddenIslandWorld TODO
    void testTerrainProcedure(Tester t) {

    }
    // tests pauseGame for the class ForbiddenIslandWorld TODO
    void testPauseGame(Tester t) {
        ForbiddenIslandWorld fake = new ForbiddenIslandWorld("fake");
        t.checkExpect(fake.isPaused, false);
        fake.pauseGame();
        t.checkExpect(fake.isPaused, true);
    }
    // tests makeImage for the class ForbiddenIslandWorld TODO
    void testMakeImage(Tester t) {

    }
    // tests onKeyEvent for the class ForbiddenIslandWorld TODO
    void testOnKeyEvent(Tester t) {
        // key press M
        t.checkExpect(this.nullWorld.board, null);
        
    }
    // tests onTick for the class ForbiddenIslandWorld TODO
    void testOnTick(Tester t) {

    }

    // tests movePlayer for the class Player TODO
    void testMovePlayer(Tester t) {

    }
    // tests safe for the class Player TODO
    void testSafe(Tester t) {

    }

    // tests touching for the class Target TODO
    void testTouching(Tester t) {

    }
    // tests sameTarget for the class Target TODO
    void testSameTarget(Tester t) {

    }

    // tests canBeRepaired for the class HelicopterTarget TODO
    void testCanBeRepaired(Tester t) {

    }
    // tests canBeRepaired(Player) for the class HelicopterTarget TODO
    void testCanBeRepairedP(Tester t) {

    }

    // tests visit(Cons) for the class TargetListVisitor TODO
    void testTargetListVisitC(Tester t) {

    }
    // tests visit(Mt) for the class TargetListVisitor TODO
    void testTargetListVisitM(Tester t) {

    }
    // tests visit(Node) for the class TargetListVisitor TODO
    void testTargetListVisitN(Tester t) {

    }
    // tests visit(Leaf) for the class TargetListVisitor TODO
    void testTargetListVisitL(Tester t) {

    }

    // tests visit(Cons) for the class TargetVisitor TODO
    void testTargetVisitC(Tester t) {

    }
    // tests visit(Mt) for the class TargetVisitor TODO
    void testTargetVisitM(Tester t) {

    }
    // tests visit(Node) for the class TargetVisitor TODO
    void testTargetVisitN(Tester t) {

    }
    // tests visit(Leaf) for the class TargetVisitor TODO
    void testTargetVisitL(Tester t) {

    }

    // tests isLeaf in the IBST interface
    void testIsLeaf(Tester t) {
        IBST<String> sL = new Leaf<String>();
        IBST<String> n1 = new Node<String>("hi", sL, sL);
        IBST<String> n2 = new Node<String>("bye", n1, sL);
        IBST<String> n3 = new Node<String>("so", n1, sL);
        t.checkExpect(sL.isLeaf(), true);
        t.checkExpect(n1.isLeaf(), false);
        t.checkExpect(n2.isLeaf(), false);
        t.checkExpect(n3.isLeaf(), false);
    }

    // tests insert in the IBST interface
    void testInsert(Tester t) {
        IComp<Cell> comp = new CompCell();
        Cell c1 = new Cell(9, 0, 0);
        Cell c2 = new Cell(4, 0, 1);
        Cell c3 = new Cell(3, 1, 0);
        Cell c4 = new OceanCell(1, 1);
        IBST<Cell> sC = new Leaf<Cell>();
        IBST<Cell> n0 = new Node<Cell>(c4, sC, sC);
        IBST<Cell> n1 = new Node<Cell>(c2, sC, sC);
        IBST<Cell> n2 = new Node<Cell>(c3, sC, sC);
        IBST<Cell> n3 = new Node<Cell>(c1, n1, n2);
        IBST<Cell> n2a = new Node<Cell>(c3, sC, n0);
        IBST<Cell> n3a = new Node<Cell>(c1, n1, n2a);
        t.checkExpect(n3.insert(comp, c4), n3a);
    }

    // runs big bang
    void testRunGame(Tester t) {
        this.initializeWorlds();
        //this.random.bigBang(640, 640);
    }

}











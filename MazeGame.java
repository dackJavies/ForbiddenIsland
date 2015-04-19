// Assignment 10
// Cherry Alexander
// acherry
// Davis Jack
// jdavis

import tester.*;

import java.awt.Canvas;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javalib.colors.*;
import javalib.impworld.*;
import javalib.worldimages.*;

// represents a list
interface IList<T> extends Iterable<T> {
    // Computes the size of this list
    int length();
    // creates a new list with the given item added to the front
    IList<T> addToFront(T item);
    // creates a new list with the given item added to the front
    IList<T> addToBack(T item);
    // map the given IFunc over the entire list
    <R> IList<R> map(IFunc<T,R> func);
    // append this list onto the given one
    IList<T> append(IList<T> other);
    // creates a new Tree from this IList
    ITST<T> list2Tree(IComp<T> comp);
    // reverse this list
    IList<T> rev();
    // helps to reverse this list
    IList<T> revT(IList<T> acc);
    // Is this list empty?
    boolean isEmpty();
    // Accept the given Visitor
    <R> R accept(IVisitor<T, R> v);
    // determines whether the given item is in this list
    boolean contains(T t);
    // returns a copy of this list without any duplicate items
    IList<T> removeDups();
}

// represents a function object that takes an A and returns an R
interface IFunc<A, R> {
    // Apply the function
    R apply(A a);
}

// represents a function that converts a number to a String
class ToString implements IFunc<Integer, String> {
    public String apply(Integer i) {
        return (String)i.toString();
    }
}


// Iterator for IList<T>
class IListIterator<T> implements Iterator<T> {

    IList<T> src;

    IListIterator(IList<T> src) { this.src = src; }
    // does this iterator have an iterator?
    public boolean hasNext() {

        return !this.src.isEmpty();
    }
    // gets the next out of this Iterator
    public T next() {

        if (!this.hasNext()) {
            throw new RuntimeException();
        }

        Cons<T> sourceAsCons = (Cons<T>)this.src;
        T result = sourceAsCons.first;
        this.src = sourceAsCons.rest;
        return result;

    }
    // does nothing
    public void remove() {

        throw new RuntimeException("Do not use this method, please");

    }

}

// represents a non-empty list
class Cons<T> implements IList<T> {
    T first;
    IList<T> rest;
    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }
    // Computes the size of this list
    public int length() {
        return 1 + this.rest.length();
    }
    // creates a new list with the given item added to the front
    public IList<T> addToFront(T item) {
        return new Cons<T>(item, this);
    }
    // creates a new list with the given item added to the back
    public IList<T> addToBack(T item) {
        return new Cons<T>(this.first, this.rest.addToBack(item));
    }
    // map the given function over the entire list
    public <R> IList<R> map(IFunc<T, R> func) {
        return new Cons<R>(func.apply(this.first), this.rest.map(func));
    }
    // appends this list onto the given one
    public IList<T> append(IList<T> other) {
        return new Cons<T>(this.first, this.rest.append(other));
    }
    // creates a new Tree from this IList
    public ITST<T> list2Tree(IComp<T> comp) {
        ITST<T> result = new Leaf<T>();
        for(T t: this) {
            result = result.insert(comp, t);
        }
        return result;
    }
    // reverses this list
    public IList<T> rev() {
        return this.revT(new Mt<T>());
    }
    // helps reverse this list
    public IList<T> revT(IList<T> acc) {
        return this.rest.revT(new Cons<T>(this.first, acc));
    }
    // Is this list empty?
    public boolean isEmpty() { return false; }
    // gets the iterator for this IList
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
    // accepts a Visitor for this IList
    public <R> R accept(IVisitor<T, R> v) {
        return v.visit(this);
    }
    // determines whether the given item is in this list
    public boolean contains(T t) {
        return this.first == t || this.rest.contains(t);
    }
    // returns a copy of this list without any duplicate items
    public IList<T> removeDups() {
        if(this.rest.contains(this.first)) {
            return this.rest.removeDups();
        }
        else {
            return new Cons<T>(this.first, this.rest.removeDups());
        }
    }
} 

// represents an empty list
class Mt<T> implements IList<T> {
    // Computes the size of this list
    public int length() {
        return 0;
    }
    // creates a new list with the given item added to the front
    public IList<T> addToFront(T item) {
        return new Cons<T>(item, this);
    }
    // creates a new list with the given item added to the back
    public IList<T> addToBack(T item) {
        return new Cons<T>(item, this);
    }
    // map the given function over the entire list
    public <R> IList<R> map(IFunc<T, R> func) {
        return new Mt<R>();
    }
    // appends this list onto the given one
    public IList<T> append(IList<T> other) {
        return other;
    }
    
    // creates a new Tree from this IList
    public ITST<T> list2Tree(IComp<T> comp) {
        return new Leaf<T>();
    }    
    // reverses this list
    public IList<T> rev() {
        return this;
    }
    // helps reverse this list
    public IList<T> revT(IList<T> acc) {
        return acc;
    }
    // Is this list empty?
    public boolean isEmpty() { return true; }
    // gets this lists iterator
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
    // accepts a visitor
    public <R> R accept(IVisitor<T, R> v) {
        return v.visit(this);
    }
    // determines whether the given item is in this list
    public boolean contains(T t) {
        return false;
    }
    // returns a copy of this list without any duplicate items
    public IList<T> removeDups() {
        return this;
    }
    
}

//represents the deque collection of items
class Deque<T> {
    Sentinel<T> header;
    // initializes the deque with a new Sentinel
    Deque() {
        this.header = new Sentinel<T>();
    }
    // initializes the deque with the given Sentinel
    Deque(Sentinel<T> header) {
        this.header = header;
    }
    // counts the number of nodes in this deque
    int size() {
        return this.header.next.countNodes();
    }
    // EFFECTS: Mutates the header and first item of the Deque's prev and next field
    // adds a node to the beginning of the deque
    void addAtHead(T t) {
        new Node<T>(t, this.header.next, this.header);
    }
    // EFFECTS: Mutates the header and last item of the Deque's prev and next field
    // adds a node to the beginning of the deque
    void addAtTail(T t) {
        new Node<T>(t, this.header, this.header.prev);
    }
    // EFFECTS: Mutates the header and first item of the Deque's prev and next field
    // adds a node to the beginning of the deque
    T removeFromHead() {
        if (!this.header.next.isNode()) {
            throw new RuntimeException("cannot remove first item from empty list");
        }
        else {
            T temp = ((Node<T>)(this.header.next)).data;
            this.header.next = this.header.next.next;
            this.header.next.prev = this.header;
            return temp;
        }
    }
    // EFFECTS: Mutates the header and last item of the Deque's prev and next field
    // adds a node to the beginning of the deque
    T removeFromTail() {
        if (!this.header.prev.isNode()) {
            throw new RuntimeException("cannot remove last item from empty list");
        }
        else {
            T temp = ((Node<T>)(this.header.prev)).data;
            this.header.prev = this.header.prev.prev;
            this.header.prev.next = this.header;
            return temp;
        }
    }
    // removes the given node from the deque
    void removeNode(ANode<T> n) {
        if (n.isNode()) {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        }
    }
}

//represents a node in a deck
abstract class ANode<T> {
    ANode<T> next;
    ANode<T> prev;
    ANode(ANode<T> next, ANode<T> prev) {
        this.next = next;
        this.prev = prev;
    }
    // EFFECTS: Mutates the prev or next field
    // updates this node with a new prev or next node
    void updateSelf(ANode<T> n, boolean isPrev) {
        if (isPrev) {
            this.prev = n;
        }
        else {
            this.next = n;
        }
    }
    // determines whether this node is a non-header node
    boolean isNode() {
        return true;
    }
    // counts how many nodes come after this one inclusively
    int countNodes() {
        return 1 + this.next.countNodes();
    }
}

//represents the header node of a deque
class Sentinel<T> extends ANode<T> {
    Sentinel() {
        super(null, null);
        this.next = this;
        this.prev = this;
    }
    // updates this sentinel with a new prev or next node
    void updateSelf(ANode<T> n, boolean isPrev) {
        if (isPrev) {
            this.prev = n;
        }
        else {
            this.next = n;
        }
    }
    // determines that this is not a non-header node
    boolean isNode() {
        return false;
    }
    // counts how many nodes come after this one inclusively
    int countNodes() {
        return 0;
    } 
}

//represents a non-header node of a deque
class Node<T> extends ANode<T> {
    T data;
    // creates a node with no connecting nodes
    Node(T data) {
        super(null, null);
        this.data = data;
    }
    // EFFECTS: Mutates the prev and next's prev and next field
    // creates a node with connecting nodes
    Node(T data, ANode<T> next, ANode<T> prev) {
        super(null, null);
        this.data = data;
        if (next == null) {
            throw new IllegalArgumentException("next node cannot be null");
        }
        else if (prev == null) {
            throw new IllegalArgumentException("prev node cannot be null");
        }
        else {
            this.next = next;
            this.prev = prev;
            this.next.updateSelf(this, true);
            this.prev.updateSelf(this, false);
        }
    }
}

//represents a Stack
//Used for Depth First Search
class Stack<T> {

    Deque<T> contents;
    Stack(Deque<T> contents) { 
        this.contents = contents; 
    }
    // Add an item to the head of the list
    void push(T item) {
        this.contents.addAtHead(item);
    }
    // determines whether this list is empty
    boolean isEmpty() {
        return this.contents.size() == 0;
    }
    // Removes and returns the head of the list
    T pop() {
        return this.contents.removeFromHead();
    }
}

// represents a Queue
// Used for Breadth First Search
class Queue<T> {

    Deque<T> contents;

    Queue(Deque<T> contents) {
        this.contents = contents;
    }

    // Adds an item to the tail of this list
    void enqueue(T item) {
        this.contents.addAtTail(item);
    }
    // determines whether this list is empty
    boolean isEmpty() {
        return this.contents.size() == 0;
    }

    // Removes and returns the head of the list
    T dequeue() {
        return this.contents.removeFromHead();
    }
}

//represents a Cell Binary Tree
interface ITST<T> {
    // inserts the given item into this tree
    ITST<T> insert(IComp<T> comp, T t);
    // determines whether this is a leaf
    boolean isLeaf();
    // accepts a visitor 
    <R> R accept(IVisitor<T, R> v);
}

//represents a known Cell Binary Tree
class TTNode<T> implements ITST<T> {
    T data;
    ITST<T> left;
    ITST<T> middle;
    ITST<T> right;
    TTNode(T data, ITST<T> left, ITST<T> middle, ITST<T> right) {
        this.data = data;
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
    // inserts an item into this tree according to the given comparator
    public ITST<T> insert(IComp<T> comp, T t) {
        if (comp.compare(this.data, t) > 0) {
            return new TTNode<T>(this.data, this.left.insert(comp, t), this.middle, this.right);
        }
        else if (comp.compare(this.data, t) < 0) {
            return new TTNode<T>(this.data, this.left, this.middle, this.right.insert(comp, t));
        }
        else {
            return new TTNode<T>(this.data, this.left, this.middle.insert(comp, t), this.right);
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
class Leaf<T> implements ITST<T> {
    // inserts an item into this tree according to the given comparator
    public ITST<T> insert(IComp<T> comp, T t) {
        return new TTNode<T>(t, this, this, this);        
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

//this compares two Edges randomly
class RandEdge implements IComp<Edge> {
    // compares
    public int compare(Edge e1, Edge e2) {
        Random r = new Random();
        if (r.nextInt(1000) < 333) {
            return -1;
        }
        else if (r.nextInt(1000) > 666) {
            return 1;
        }
        else {
            return 0;
        }
    }
}

//this compares two Edges randomly
class RandVert implements IComp<Vertex> {
    // compares
  public int compare(Vertex e1, Vertex e2) {
      Random r = new Random();
      if (r.nextInt(1000) < 333) {
          return -1;
      }
      else if (r.nextInt(1000) > 666) {
          return 1;
      }
      else {
          return 0;
      }
  }
}

// this compares two items according to the given boolean
class SwitcherComp<T> implements IComp<T> {
    boolean switcher;
    SwitcherComp(boolean switcher) {
        this.switcher = switcher;
    }
    // compares
    public int compare(T t1, T t2) {
        if (this.switcher) {
            return 1;
        }
        else {
            return -1;
        }
    }
    
}

//this represents a comparator of Cells
class CompVert implements IComp<Vertex> {
    // compares based on x and y (e.g. (0, 1) < (1, 1) < (1, 2) < (2,0))
    public int compare(Vertex t1, Vertex t2) {
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

//represents a visitor object
interface IVisitor<T, R> {
    R visit(Cons<T> c);
    R visit(Mt<T> m);
    R visit(TTNode<T> n);
    R visit(Leaf<T> n);
}

//represents a visitor that displays the cells in a list
class DisplayWallVisitor implements IVisitor<Edge, WorldImage> {
    DisplayWallVisitor() {
    }
    // visits an empty
    public WorldImage visit(Mt<Edge> m) {
        throw new IllegalArgumentException("IList is not a valid argument");
    }
    // visits a cons
    public WorldImage visit(Cons<Edge> c) {
        throw new IllegalArgumentException("IList is not a valid argument");
    }
    // visits a BTNode
    public WorldImage visit(TTNode<Edge> n) {
        return new OverlayImages(n.data.displayWall(), 
            new OverlayImages(n.left.accept(this), 
                    new OverlayImages(n.middle.accept(this), n.right.accept(this))));
    }
    // visits a Leaf
    public WorldImage visit(Leaf<Edge> n) {
        return new LineImage(new Posn(-1, -1), new Posn(-1, -1), new Color(255, 255, 255));
    }    
}

//represents a visitor that displays the edges in a list
class DisplayEdgeVisitor implements IVisitor<Edge, WorldImage> {
    boolean visibleEdges;
    DisplayEdgeVisitor(boolean visibleEdges) {
        this.visibleEdges = visibleEdges;
    }
    // visits an empty
    public WorldImage visit(Mt<Edge> m) {
        throw new IllegalArgumentException("IList is not a valid argument");
    }
    // visits a cons
    public WorldImage visit(Cons<Edge> c) {
        throw new IllegalArgumentException("IList is not a valid argument");
    }
    // visits a BTNode
    public WorldImage visit(TTNode<Edge> n) {
        return new OverlayImages(n.data.displayEdge(visibleEdges),
                new OverlayImages(n.left.accept(this), 
                        new OverlayImages(n.middle.accept(this), n.right.accept(this))));
    }
    // visits a Leaf
    public WorldImage visit(Leaf<Edge> n) {
        return new LineImage(new Posn(-1, -1), new Posn(-1, -1), new Color(255, 255, 255));
    }    
}

// represents a maze cell
class Vertex {

    IList<Edge> edges;
    boolean wasSearched;
    boolean correctPath;
    boolean startVert;
    boolean endVert;

    Integer x;
    Integer y;

    Vertex(int x, int y) {
        this.edges = new Mt<Edge>();
        this.wasSearched = false;
        this.correctPath = false;
        this.startVert = false;
        this.endVert= false;

        this.x = x;
        this.y = y;
    }

    // Add an Edge with an entered weight 
    void addEdge(Vertex other, int opt) {
        Edge toAdd = new Edge(this, other, opt);
        this.edges = this.edges.addToBack(toAdd);
        other.edges = other.edges.addToBack(toAdd);
    }
    // displays the maze cell
    WorldImage displayCell() {
        int sideLength = 10;
        int posnShift = 5;
        Color c = new Color(205, 205, 205);
        if (this.startVert) {
            c = new Color(0, 160, 0);
        }
        if (this.correctPath) {
            c = new Color(65, 86, 197);
        }
        
        else if (this.wasSearched) {
            c = new Color(56, 176, 222);
        }
        if (this.endVert) {
            c = new Color(160, 0, 160);
        }
        return new RectangleImage(new Posn((this.x * sideLength) + posnShift, 
                (this.y * sideLength) + posnShift), 10, 10, c);
    }
    // displays the maze cell as a search head
    WorldImage displayHead() {
        int sideLength = 10;
        int posnShift = 5;
        Color c = new Color(65, 86, 197);
        return new RectangleImage(new Posn((this.x * sideLength) + posnShift, 
                (this.y * sideLength) + posnShift), 10, 10, c);
    }
}

// represents an edge of the maze graph
class Edge {
    Vertex from;
    Vertex to;
    int weight;
    Edge(Vertex from, Vertex to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
    // displays this edge 
    WorldImage displayEdge(boolean visibleLine) {
        int sideLength = 10;
        int posnShift = sideLength / 2;
        int toX = (this.to.x * sideLength) + posnShift;
        int toY = (this.to.y * sideLength) + posnShift;
        int fromX = (this.from.x * sideLength) + posnShift;
        int fromY = (this.from.y * sideLength) + posnShift;
        if (visibleLine) {
        return new LineImage(new Posn(fromX, fromY), new Posn(toX, toY), new Color(255, 0, 0));
        }
        else {
            return new OverlayImages(this.from.displayCell(), this.to.displayCell());
        }
    }
    WorldImage displayWall() {
        Color c = new Color(90, 100, 90);
        int sideLength = 10;
        int posnShift = sideLength / 2;
        int toX = (this.to.x * sideLength) + posnShift;
        int toY = (this.to.y * sideLength) + posnShift;
        int fromX = (this.from.x * sideLength) + posnShift;
        int fromY = (this.from.y * sideLength) + posnShift;
        // next to each other horizontally
        Posn p2 = new Posn((toX + fromX) / 2, toY + 5);
        Posn p1 = new Posn((toX + fromX) / 2, toY - 5);
        // next to each other vertically
        Posn p4 = new Posn(toX + 5, (toY + fromY) / 2);
        Posn p3 = new Posn(toX - 5, (toY + fromY) / 2);
        // connected horizontally
        if (fromY == toY) {
            return new LineImage(p1, p2, c);
        }
        // connected vertically
        else if (fromX == toX) {
            return new LineImage(p3, p4, c);
        }
        // connected otherwise
        else {
            throw new RuntimeException("There is an edge connecting two non-adjacent vertices");
        }
    }
}

// represents the gameWorld
class MazeWorld extends World {
    // Size of the game
    int gameSizeX;
    int gameSizeY;
    // GAMEMODES:
    // 0 = manual
    // 1 = depth-first search
    // 2 = breadth-first search
    // 3 = displayEdges
    int gameMode;
    IList<Edge> board;
    HashMap<String, String> representatives;
    IList<Edge> unUsed;
    IList<Vertex> searchHeads;

    MazeWorld(int gameSizeX, int gameSizeY) {
        this.gameSizeX = gameSizeX;
        this.gameSizeY = gameSizeY;
        this.gameMode = 0;
        this.board = new Mt<Edge>();
        this.representatives = new HashMap<String, String>();
        IList<Vertex> searchHeads = new Mt<Vertex>();
        
        ArrayList<ArrayList<Vertex>> blankCells = this.createGrid();
        this.addEdges(blankCells);
        IList<Edge> b = this.vertexToEdge(blankCells);
        this.board = b;
        this.unUsed = this.board;
        
    }
    
    // Change an IList<T> into an ArrayList<T>
    <T> ArrayList<T> iListToArr(IList<T> toChange) {
        
        ArrayList<T> result = new ArrayList<T>();
        
        for(T t: toChange) {
            result.add(t);
        }
        
        return result;
        
    }

    // Create a grid of blank Vertices
    ArrayList<ArrayList<Vertex>> createGrid() {
        ArrayList<ArrayList<Vertex>> result = new ArrayList<ArrayList<Vertex>>();

        for(int i = 0; i < gameSizeX; i += 1) {

            result.add(new ArrayList<Vertex>());

        }

        for(int i = 0; i < gameSizeX; i += 1) {

            for(int i2 = 0; i2 < gameSizeY; i2 += 1) {

                result.get(i).add(new Vertex(i, i2));

            }

        }
        if (result.size() > 0) {
        result.get(0).get(0).startVert = true;
        result.get(this.gameSizeX - 1).get(this.gameSizeY - 1).endVert = true;
        this.searchHeads = new Cons<Vertex>(result.get(0).get(0), new Mt<Vertex>());
        }
        return result;

    }

    // Add edges to the given ArrayList<ArrayList<Vertex>>
    void addEdges(ArrayList<ArrayList<Vertex>> grid) {
        Random randy = new Random();

        // Connections to the left/right
        for(int i = 1; i < grid.size(); i += 1) {

            for(int i2 = 0; i2 < grid.get(i).size(); i2 += 1) {

                grid.get(i).get(i2).addEdge(grid.get(i - 1).get(i2), 
                        randy.nextInt(10000));

            }

        }

        // Connections to the top/bottom
        for(int i3 = 0; i3 < grid.size(); i3 += 1) {

            for(int i4 = 1; i4 < grid.get(i3).size(); i4 += 1) {

                grid.get(i3).get(i4).addEdge(grid.get(i3).get(i4 - 1), 
                        randy.nextInt(10000));
            }

        }

    }

    // Add edges to the given ArrayList<ArrayList<Vertex>> (overloaded for testing)
    void addEdges(ArrayList<ArrayList<Vertex>> grid, int r) {

        // Connections to the left/right
        for(int i = 1; i < grid.size(); i += 1) {

            for(int i2 = 0; i2 < grid.get(i).size(); i2 += 1) {

                grid.get(i).get(i2).addEdge(grid.get(i - 1).get(i2), 1);

            }

        }

        // Connections to the top/bottom
        for(int i3 = 0; i3 < grid.size(); i3 += 1) {

            for(int i4 = 1; i4 < grid.get(i3).size(); i4 += 1) {

                grid.get(i3).get(i4).addEdge(grid.get(i3).get(i4 - 1), 1);

            }

        }

    }


    // Convert a 2D ArrayList of Vertices to a 1D IList of Edges
    IList<Edge> vertexToEdge(ArrayList<ArrayList<Vertex>> grid) {

        //ArrayList<IList<Edge>> listOfLists = new ArrayList<IList<Edge>>();
        IList<Edge> edges = new Mt<Edge>();
        
        // While we're still working with Vertices, populate the
        // HashMap we need for Kruskel's alrgorithm
        for(Integer i = 0; i < grid.size(); i += 1) {
            
            for(Integer i2 = 0; i2 < grid.get(i).size(); i2 += 1) {
                
                // In the HashMap, vertices are represented with their
                // coordinates in the format: x-y
                // i.e. (1, 1) => "1-1"
                String toPut = i.toString() + "-" + i2.toString();
                
                // Each key's value is initialized to itself
                // in accordance with the Union/Find data structure.
                this.representatives.put(toPut, toPut);
                
            }
            
        }

        
        for(int i = 0; i < grid.size(); i += 1) {

            for(int i2 = 0; i2 < grid.get(i).size(); i2 += 1) {

                for (Edge e: grid.get(i).get(i2).edges) {
                    
                    if (e.from == grid.get(i).get(i2)) {
                        edges = edges.addToFront(e);
                    }
                    
                }

            }
        }
        
        return edges;//.removeDups();
        //return edges.accept(new RemDupsVisitor1<Edge>());

    }
    
    // Do two elements in the Union/Find hashmap have the same value?
    // i.e. Will the connection create a cycle in the tree?
    boolean cycle(HashMap<String, String> uf, String v1, String v2) {
        return uf.get(v1).equals(uf.get(v2));
    }
     
    // Implement Union/Find data structure while applying
    // Kruskel's algorithm.
    // EFFECT: mutates the edge lists in each Vertex in the given ArrayList
    IList<Edge> kruskel(ArrayList<Edge> grid, HashMap<String, String> representatives) {
        IList<Edge> blackList = new Mt<Edge>();
        IList<Edge> finalList = new Mt<Edge>();
        String toFind1;
        String toFind2;

        for(Edge e: grid) {
            
            // Convert both vectors' positions into strings
            toFind1 = e.from.x.toString() + "-" + e.from.y.toString();
            toFind2 = e.to.x.toString() + "-" + e.to.y.toString();
            
            // If this next connection will create a cycle, discard it
            // i.e. don't include it in the final list
            if (this.cycle(representatives, toFind1, toFind2)) {
                
                blackList = blackList.addToFront(e);
                
            }
            // Otherwise, add it to the final list and adjust the hashmap.
            // Adjusting the hashmap would mean changing the value associated
            // with the key toFind2 to that of the key toFind1.
            else {
                
                finalList.addToBack(e);
                representatives.put(toFind2, representatives.get(toFind1));
                
            }
            
        }
        
        // Save the unused edge list for rendering purposes
        this.unUsed = blackList;
        
        // Returns the list of actual edges.
        return finalList;
    }
    // Draws the World
    public WorldImage makeImage() {
        IComp<Edge> ranE = new RandEdge(); 
        ITST<Edge> boardTree = this.board.list2Tree(ranE);
        ITST<Edge> unUsedTree = this.unUsed.list2Tree(ranE);
        DisplayEdgeVisitor dEVisitor = 
                new DisplayEdgeVisitor(this.gameMode == 3);
        DisplayWallVisitor dWVisitor = 
                new DisplayWallVisitor();
        
        return new OverlayImages(boardTree.accept(dEVisitor),
                    unUsedTree.accept(dWVisitor));
    }
    
    // key handler TODO
    public void onKeyEvent(String s) {
        if (s.equals("m") && !(this.gameMode == 0)) {
            this.gameMode = 0;
        }
        else if (s.equals("e") && !(this.gameMode == 3)) {
            this.gameMode = 3;
        }
        else if (s.equals("d") && !(this.gameMode == 1)) {
            this.gameMode = 1;
        }
        else if (s.equals("b") && !(this.gameMode == 2)) {
            this.gameMode = 2;
        }
    }
         
}

// examples and tests for the MazeWorld
class ExamplesMaze {
    MazeWorld maze0 = new MazeWorld(0, 0);
    MazeWorld maze5 = new MazeWorld(5, 5);
    MazeWorld maze3 = new MazeWorld(3, 3);
    MazeWorld maze2 = new MazeWorld(2, 2);


    ArrayList<Vertex> aV0 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV1 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV2 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV3 = new ArrayList<Vertex>();
    ArrayList<Vertex> aV4 = new ArrayList<Vertex>();
    ArrayList<ArrayList<Vertex>> aVFinal = new ArrayList<ArrayList<Vertex>>();
    ArrayList<ArrayList<Vertex>> aVCopy = new ArrayList<ArrayList<Vertex>>();

    // for neighbors
    ArrayList<Vertex> aVN0 = new ArrayList<Vertex>();
    ArrayList<Vertex> aVN1 = new ArrayList<Vertex>();
    ArrayList<Vertex> aVN2 = new ArrayList<Vertex>();
    ArrayList<ArrayList<Vertex>> aVN = new ArrayList<ArrayList<Vertex>>();
    // for neighbors 2
    ArrayList<Vertex> aVN00 = new ArrayList<Vertex>();
    ArrayList<Vertex> aVN01 = new ArrayList<Vertex>();
    ArrayList<Vertex> aVN02 = new ArrayList<Vertex>();
    ArrayList<ArrayList<Vertex>> aVNB = new ArrayList<ArrayList<Vertex>>();

    // List test lists
    IList<Integer> mTI = new Mt<Integer>();
    IList<Integer> listI1 = new Cons<Integer>(1, new Cons<Integer>(2, 
            new Cons<Integer>(3, new Cons<Integer>(4, mTI))));
    IList<Integer> listI2 = new Cons<Integer>(5, this.listI1);
    IList<Integer> listI2B = new Cons<Integer>(1, new Cons<Integer>(2, 
            new Cons<Integer>(3, new Cons<Integer>(4, new Cons<Integer>(5, mTI)))));

    // Function objects
    ToString tS = new ToString();

    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(0, 0);
    Vertex v3 = new Vertex(0, 0);
    Vertex v4 = new Vertex(0, 0);
    Vertex v5 = new Vertex(0, 0);
    Vertex v6 = new Vertex(0, 0);
    Vertex v7 = new Vertex(0, 0);
    Vertex v8 = new Vertex(0, 0);
    Vertex v9 = new Vertex(0, 0);

    Vertex v01 = new Vertex(0, 0);
    Vertex v02 = new Vertex(0, 0);
    Vertex v03 = new Vertex(0, 0);
    Vertex v04 = new Vertex(0, 0);
    Vertex v05 = new Vertex(0, 0);
    Vertex v06 = new Vertex(0, 0);
    Vertex v07 = new Vertex(0, 0);
    Vertex v08 = new Vertex(0, 0);
    Vertex v09 = new Vertex(0, 0);

    // v2 
    Edge e2to1 = new Edge(v1, v1, 0);
    // v3
    Edge e3to2 = new Edge(v1, v1, 0);
    // v4
    Edge e4to1 = new Edge(v1, v1, 0);
    // v5
    Edge e5to2 = new Edge(v1, v1, 0);
    Edge e5to4 = new Edge(v1, v1, 0);
    // v6 
    Edge e6to3 = new Edge(v1, v1, 0);
    Edge e6to5 = new Edge(v1, v1, 0);
    // v7 
    Edge e7to4 = new Edge(v1, v1, 0);
    // v8
    Edge e8to5 = new Edge(v1, v1, 0);
    Edge e8to7 = new Edge(v1, v1, 0);
    // v9 
    Edge e9to6 = new Edge(v1, v1, 0);
    Edge e9to8 = new Edge(v1, v1, 0);
    IList<Edge> mTE = new Mt<Edge>();
    // row 1
    IList<Edge> l1 = new Mt<Edge>();
    IList<Edge> l2 = new Mt<Edge>();
    IList<Edge> l3 = new Mt<Edge>();
    // row 2
    IList<Edge> l4 = new Mt<Edge>();
    IList<Edge> l5 = new Mt<Edge>();
    IList<Edge> l6 = new Mt<Edge>();
    // row 3
    IList<Edge> l7 = new Mt<Edge>();
    IList<Edge> l8 = new Mt<Edge>();
    IList<Edge> l9 = new Mt<Edge>();

    void initialize() {

        this.aV0.clear();
        this.aV0.add(new Vertex(0, 0));
        aV0.get(0).startVert = true;
        this.aV0.add(new Vertex(0, 1));
        this.aV0.add(new Vertex(0, 2));
        this.aV0.add(new Vertex(0, 3));
        this.aV0.add(new Vertex(0, 4));
        this.aV1.clear();
        this.aV1.add(new Vertex(1, 0));
        this.aV1.add(new Vertex(1, 1));
        this.aV1.add(new Vertex(1, 2));
        this.aV1.add(new Vertex(1, 3));
        this.aV1.add(new Vertex(1, 4));
        this.aV2.clear();
        this.aV2.add(new Vertex(2, 0));
        this.aV2.add(new Vertex(2, 1));
        this.aV2.add(new Vertex(2, 2));
        this.aV2.add(new Vertex(2, 3));
        this.aV2.add(new Vertex(2, 4));
        this.aV3.clear();
        this.aV3.add(new Vertex(3, 0));
        this.aV3.add(new Vertex(3, 1));
        this.aV3.add(new Vertex(3, 2));
        this.aV3.add(new Vertex(3, 3));
        this.aV3.add(new Vertex(3, 4));
        this.aV4.clear();
        this.aV4.add(new Vertex(4, 0));
        this.aV4.add(new Vertex(4, 1));
        this.aV4.add(new Vertex(4, 2));
        this.aV4.add(new Vertex(4, 3));
        this.aV4.add(new Vertex(4, 4));
        aV4.get(4).endVert = true;
        this.aVFinal.clear();
        this.aVFinal.add(aV0);
        this.aVFinal.add(aV1);
        this.aVFinal.add(aV2);
        this.aVFinal.add(aV3);
        this.aVFinal.add(aV4);



        v1 = new Vertex(0, 0);
        v2 = new Vertex(0, 1);
        v3 = new Vertex(0, 2);
        v4 = new Vertex(1, 0);
        v5  = new Vertex(1, 1); // (1, 0)?
        v6 = new Vertex(1, 2);
        v7 = new Vertex(2, 0);
        v8 = new Vertex(2, 1);
        v9 = new Vertex(2, 2);

        // for neighbors
        aVN0.clear();
        aVN0.add(v1);
        aVN0.add(v2);
        aVN0.add(v3);
        aVN1.clear();
        aVN1.add(v4);
        aVN1.add(v5);
        aVN1.add(v6);
        aVN2.clear();
        aVN2.add(v7);
        aVN2.add(v8);
        aVN2.add(v9);
        aVN.clear();
        aVN.add(aVN0);
        aVN.add(aVN1);
        aVN.add(aVN2);

        v01 = new Vertex(0, 0);
        v02 = new Vertex(0, 1);
        v03 = new Vertex(0, 2);
        v04 = new Vertex(1, 0);
        v05 = new Vertex(1, 1);
        v06 = new Vertex(1, 2); // (1,1)?
        v07 = new Vertex(2, 0);
        v08 = new Vertex(2, 1);
        v09 = new Vertex(2, 2);

        // for neighbors
        aVN00.clear();
        aVN00.add(v01);
        aVN00.add(v02);
        aVN00.add(v03);
        aVN01.clear();
        aVN01.add(v04);
        aVN01.add(v05);
        aVN01.add(v06);
        aVN02.clear();
        aVN02.add(v07);
        aVN02.add(v08);
        aVN02.add(v09);
        aVNB.clear();
        aVNB.add(aVN00);
        aVNB.add(aVN01);
        aVNB.add(aVN02);

        this.aVCopy.clear();
        for(int i = 0; i < aVFinal.size(); i += 1) {
            aVCopy.add(aVFinal.get(i)); 
        }
    }

    // initializes Vertices in aVCopy 
    void initializeV() {
        this.initialize();
        // v2 
        e2to1 = new Edge(v2, v1, 1);
        // v3
        e3to2 = new Edge(v3, v2, 1);
        // v4
        e4to1 = new Edge(v4, v1, 1);
        // v5
        e5to2 = new Edge(v5, v2, 1);
        e5to4 = new Edge(v5, v4, 1);
        // v6 
        e6to3 = new Edge(v6, v3, 1);
        e6to5 = new Edge(v6, v5, 1);
        // v7 
        e7to4 = new Edge(v7, v4, 1);
        // v8
        e8to5 = new Edge(v8, v5, 1);
        e8to7 = new Edge(v8, v7, 1);
        // v9 
        e9to6 = new Edge(v9, v6, 1);
        e9to8 = new Edge(v9, v8, 1);
        mTE = new Mt<Edge>();
        // row 1
        l1 = new Cons<Edge>(e4to1, new Cons<Edge>(e2to1 , mTE));
        l2 = new Cons<Edge>(e5to2, new Cons<Edge>(e2to1, new Cons<Edge>(e3to2, mTE)));
        l3 = new Cons<Edge>(e6to3, new Cons<Edge>(e3to2, mTE));
        // row 2
        l4 = new Cons<Edge>(e4to1, new Cons<Edge>(e7to4, new Cons<Edge>(e5to4, mTE)));
        l5 = new Cons<Edge>(e5to2, new Cons<Edge>(e8to5, new Cons<Edge>(e5to4, 
                new Cons<Edge>(e6to5, mTE))));
        l6 = new Cons<Edge>(e6to3, new Cons<Edge>(e9to6, new Cons<Edge>(e6to5, mTE)));
        // row 3
        l7 = new Cons<Edge>(e7to4, new Cons<Edge>(e8to7, mTE));
        l8 = new Cons<Edge>(e8to5, new Cons<Edge>(e8to7, new Cons<Edge>(e9to8, mTE)));
        l9 = new Cons<Edge>(e9to6, new Cons<Edge>(e9to8, mTE));

        this.v1.edges = l1;
        this.v2.edges = l2;
        this.v3.edges = l3;

        this.v4.edges = l4;
        this.v5.edges = l5;
        this.v6.edges = l6;

        this.v7.edges = l7;
        this.v8.edges = l8;
        this.v9.edges = l9;
    }
    // tests length for the interface IList<T>  
    void testLength(Tester t) {
        t.checkExpect(mTI.length(), 0);
        t.checkExpect(listI1.length(), 4);
        t.checkExpect(listI2.length(), 5);
    }
    // tests addToFront for the interface IList<T> 
    void testAddToFront(Tester t) {
        t.checkExpect(mTI.addToFront(2), new Cons<Integer>(2, mTI));
        t.checkExpect(listI1.addToFront(5), listI2);
    }
    // tests addToBack for the interface IList<T>
    void testAddToBack(Tester t) {
        t.checkExpect(mTI.addToBack(2), new Cons<Integer>(2, mTI));
        t.checkExpect(listI1.addToBack(5), listI2B);
    }
    // tests append for the interface IList<T>
    void testAppend(Tester t) {
        IList<Integer> iz = new Cons<Integer>(1, new Cons<Integer>(3, 
                new Mt<Integer>()));
        t.checkExpect(mTI.append(iz), iz);
        t.checkExpect(listI1.append(iz), new Cons<Integer>(1, new Cons<Integer>(2,
                new Cons<Integer>(3, new Cons<Integer>(4, iz)))));
    }
    // tests revT for the interface IList<T>
    void testRevT(Tester t) {
        IList<Integer> iA = new Cons<Integer>(4, new Cons<Integer>(3,
                new Cons<Integer>(2, new Cons<Integer>(1, new Mt<Integer>()))));
        t.checkExpect(this.mTI.revT(mTI), this.mTI);
        t.checkExpect(this.mTI.revT(iA), iA);
        t.checkExpect(iA.revT(mTI), this.listI1);
    }
    // tests rev for the interface IList<T>
    void testRev(Tester t) {
        IList<Integer> iA = new Cons<Integer>(4, new Cons<Integer>(3,
                new Cons<Integer>(2, new Cons<Integer>(1, new Mt<Integer>()))));
        t.checkExpect(this.mTI.rev(), this.mTI);
        t.checkExpect(iA.rev(), this.listI1);
    }
    // tests isEmpty for the interface IList<T>
    void isEmpty(Tester t) {
        t.checkExpect(this.mTI.isEmpty(), true);
        t.checkExpect(this.listI1.isEmpty(), false);
    }
    // tests apply for the function ToString
    void testToString(Tester t) {

        t.checkExpect(tS.apply(2), "2");
        t.checkExpect(tS.apply(-3), "-3");
    }
    // tests map
    void testMap(Tester t) {
        t.checkExpect(listI1.map(tS), new Cons<String>("1",
                new Cons<String>("2", new Cons<String>("3", 
                        new Cons<String>("4", new Mt<String>())))));
        t.checkExpect(mTI.map(tS), new Mt<String>());
    }
    // tests createGrid for the class MazeWorld
    void testCreateGrid(Tester t) {
        this.initialize();
        t.checkExpect(maze5.createGrid(), this.aVFinal);
        t.checkExpect(maze0.createGrid(), new ArrayList<ArrayList<Vertex>>());
    }
    // tests addEdge for the class Vertex
    void testAddEdge(Tester t) {
        this.initialize();
        t.checkExpect(v01.edges, new Mt<Edge>());
        v01.addEdge(v02, 1);
        t.checkExpect(v01.edges, new Cons<Edge>(
                new Edge(v01, v02, 1), new Mt<Edge>()));
        t.checkExpect(v02.edges, new Cons<Edge>(
                new Edge(v01, v02, 1), new Mt<Edge>()));
        t.checkExpect(v03.edges, new Mt<Edge>());
        v01.addEdge(v03, 1);
        t.checkExpect(v02.edges, new Cons<Edge>(
                new Edge(v01, v02, 1), new Mt<Edge>()));
        t.checkExpect(v01.edges, new Cons<Edge>(
                new Edge(v01, v02, 1), 
                new Cons<Edge>(new Edge(v01, v03, 1), new Mt<Edge>())));
        t.checkExpect(v03.edges, new Cons<Edge>(new Edge(v01, v03, 1), new Mt<Edge>()));
        v03.addEdge(v02, 1);
        t.checkExpect(v01.edges, new Cons<Edge>(
                new Edge(v01, v02, 1), new Cons<Edge>(new Edge(v01, v03, 1), 
                        new Mt<Edge>())));
        t.checkExpect(v02.edges, new Cons<Edge>(
                new Edge(v01, v02, 1), new Cons<Edge>(new Edge(v03, v02, 1),
                        new Mt<Edge>())));
        t.checkExpect(v03.edges, new Cons<Edge>(
                new Edge(v01, v03, 1), new Cons<Edge>(new Edge(v03, v02, 1),
                        new Mt<Edge>())));
        t.checkExpect(v01.edges.length(), 2);
        t.checkExpect(v01.edges.length(), 2);
        t.checkExpect(v01.edges.length(), 2);
    }
    // tests isLeaf in the IBST interface
    void testIsLeaf(Tester t) {
        ITST<String> sL = new Leaf<String>();
        ITST<String> n1 = new TTNode<String>("hi", sL, sL, sL);
        ITST<String> n2 = new TTNode<String>("bye", n1, sL, sL);
        ITST<String> n3 = new TTNode<String>("so", n1, sL, sL);
        t.checkExpect(sL.isLeaf(), true);
        t.checkExpect(n1.isLeaf(), false);
        t.checkExpect(n2.isLeaf(), false);
        t.checkExpect(n3.isLeaf(), false);
    }
    // tests apply for the IFunc interface TODO
    // tests apply for the IPred interface TODO
    // tests hasNext for the IListIterator TODO
    // tests next for the IListIterator TODO
    // tests remove for the IListIterator TODO
    // tests insert in the IBST interface
    void testInsert(Tester t) {
        IComp<Vertex> comp = new CompVert();
        Vertex c1 = new Vertex(0, 0);
        Vertex c2 = new Vertex(0, 1);
        Vertex c3 = new Vertex(1, 0);
        Vertex c4 = new Vertex(1, 1);
        ITST<Vertex> sC = new Leaf<Vertex>();
        ITST<Vertex> n0 = new TTNode<Vertex>(c4, sC, sC, sC);
        ITST<Vertex> n1 = new TTNode<Vertex>(c2, sC, sC, sC);
        ITST<Vertex> n2 = new TTNode<Vertex>(c3, sC, sC, sC);
        ITST<Vertex> n3 = new TTNode<Vertex>(c1, n1, sC, n2);
        ITST<Vertex> n2a = new TTNode<Vertex>(c3, sC, sC, n0);
        ITST<Vertex> n3a = new TTNode<Vertex>(c1, n1, sC, n2a);
        t.checkExpect(n3.insert(comp, c4), n3a);
    }
    // tests accept for the interfaces IList<T> and IBST<T> 
    void testAccept(Tester t) {
        Vertex v1 = new Vertex(0, 0);
        Vertex v2 = new Vertex(0, 1);
        Vertex v3 = new Vertex(1, 0);
        Vertex v4 = new Vertex(1, 1);
        Edge e1 = new Edge(v1, v2, 0);
        Edge e2 = new Edge(v1, v3, 0);
        Edge e3 = new Edge(v3, v4, 0);
        Mt<Edge> mT = new Mt<Edge>();
        Cons<Edge> cons = new Cons<Edge>(e1, mT);
        Leaf<Edge> leaf = new Leaf<Edge>();
        TTNode<Edge> node1 = new TTNode<Edge>(e1, leaf, leaf, leaf);
        TTNode<Edge> node2 = new TTNode<Edge>(e2, node1, leaf, leaf);
        DisplayEdgeVisitor dEV = new DisplayEdgeVisitor(true);
        t.checkExpect(leaf.accept(dEV), dEV.visit(leaf));
        t.checkExpect(node2.accept(dEV), dEV.visit(node2));
        t.checkException(
                new IllegalArgumentException("IList is not a valid argument"), cons, "accept", dEV);
        t.checkException(
                new IllegalArgumentException("IList is not a valid argument"), mT, "accept", dEV);
    }
    // tests list2tree for the interface IList TODO
    void testList2Tree(Tester t) {

    }
    // tests addEdges for the class MazeWorld 
    void testAddEdges(Tester t) {
        this.initialize();
        this.initializeV();
        this.maze2.addEdges(aVNB, 1);
        t.checkExpect(this.aVNB.get(0).get(0).edges.length(), 2);
        t.checkExpect(this.aVNB.get(0).get(1).edges.length(), 3);
        t.checkExpect(this.aVNB.get(0).get(2).edges.length(), 2);
        t.checkExpect(this.aVNB.get(1).get(0).edges.length(), 3);
        t.checkExpect(this.aVNB.get(1).get(1).edges.length(), 4);
        t.checkExpect(this.aVNB.get(1).get(2).edges.length(), 3);
        t.checkExpect(this.aVNB.get(2).get(0).edges.length(), 2);
        t.checkExpect(this.aVNB.get(2).get(1).edges.length(), 3);
        t.checkExpect(this.aVNB.get(2).get(2).edges.length(), 2);
        t.checkExpect(this.aVNB, aVN);
    }
    // tests displayCell TODO
    void testDisplayCell(Tester t) {
        Vertex vA = new Vertex(0, 0);
        Vertex vB = new Vertex(0, 1);
        vB.correctPath = true;
        Vertex vC = new Vertex(1, 1);
        vC.wasSearched = true;
        t.checkExpect(vA.displayCell(), new RectangleImage(new Posn(5, 5), 10, 10, new Color(205, 205, 205)));
        t.checkExpect(vB.displayCell(), new RectangleImage(new Posn(5, 15), 10, 10, new Color(65, 86, 197)));
        t.checkExpect(vC.displayCell(), new RectangleImage(new Posn(15, 15), 10, 10, new Color(56, 176, 222)));
    }
    // tests displayEdge TODO
    void testDisplayEdge(Tester t) {
        Vertex vA = new Vertex(0, 0);
        Vertex vB = new Vertex(1, 0);
        Vertex vC = new Vertex(1, 1);
        Edge eA = new Edge(vA, vB,  0);
        Edge eB = new Edge(vB, vC, 3);
        Edge eC = new Edge(vA, vC, 50935);
        // horizontally connected
        t.checkExpect(eA.displayEdge(true), new LineImage(new Posn(5, 5), 
                        new Posn(15, 5), new Color(255, 0, 0)));
        t.checkExpect(eA.displayEdge(false), new OverlayImages(vA.displayCell(), vB.displayCell()));
        // vertically connected
        t.checkExpect(eB.displayEdge(true), new LineImage(new Posn(15, 5), 
                        new Posn(15, 15), new Color(255, 0, 0)));
        t.checkExpect(eB.displayEdge(false), new OverlayImages(vB.displayCell(), vC.displayCell()));
    }
    // tests displayWall TODO
    void testDisplayWall(Tester t) {
        Vertex vA = new Vertex(0, 0);
        Vertex vB = new Vertex(1, 0);
        Vertex vC = new Vertex(1, 1);
        Edge eA = new Edge(vA, vB,  0);
        Edge eB = new Edge(vB, vC, 3);
        Edge eC = new Edge(vA, vC, 50935);
        // horizontally connected
        t.checkExpect(eA.displayWall(), new LineImage(new Posn(10, 0), new Posn(10, 10),
                new Color(90, 100, 90)));
        // vertically connected
        t.checkExpect(eB.displayWall(), new LineImage(new Posn(10, 10), new Posn(20, 10),
                new Color(90, 100, 90)));
    }    
    // tests DisplayEdgeVisitor TODO
    void testDisplayEdgeVisitor(Tester t) {
        
    }
    // tests DisplayWallVisitor TODO
    void testDisplayWallVisitor(Tester t) {
        
    }
    
    // tests removeDups for the IList interface
    void testRemoveDups(Tester t) {
        IList<Integer> emptyList = new Mt<Integer>();
        IList<Integer> listy1 = new Cons<Integer>(2, new Cons<Integer>(3,
                new Cons<Integer>(3, new Cons<Integer>(3, new Cons<Integer>(6,
                        new Cons<Integer>(7, new Cons<Integer>(2, emptyList)))))));
        IList<Integer> listy2 = new Cons<Integer>(3, new Cons<Integer>(6,
                        new Cons<Integer>(7, new Cons<Integer>(2, emptyList))));
        t.checkExpect(emptyList.removeDups(), emptyList);
        t.checkExpect(listy2.removeDups(), listy2);
        t.checkExpect(listy1.removeDups(), listy2);
    }
    
    // tests vertexToEdge in MazeWorld TODO
    void testVertexToEdge(Tester t) {
        
        this.initialize();
        this.initializeV();
        
        //t.checkExpect(maze3.vertexToEdge(aVN).length(), 12);
        
        IList<Edge> testList = new Cons<Edge>(e4to1, new Cons<Edge>(
                e2to1, new Cons<Edge>(e5to2, new Cons<Edge>(e3to2, 
                        new Cons<Edge>(e6to3, new Cons<Edge>(e7to4,
                                new Cons<Edge>(e5to4, new Cons<Edge>(e8to5,
                                        new Cons<Edge>(e6to5, new Cons<Edge>(e9to6,
                                                new Cons<Edge>(e8to7, new Cons<Edge>(
                                                        e9to8, new Mt<Edge>()))))))))))));
        
        //t.checkExpect(maze3.vertexToEdge(aVN), testList);
        
    }
    
    void testIListToArr(Tester t) {
        
        IList<Integer> testList = new Mt<Integer>();
        ArrayList<Integer> answer = new ArrayList<Integer>();
        
        for(int i = 0; i < 6000; i += 1) {
            testList = testList.addToBack(i);
            answer.add(i);
        }
        
        t.checkExpect(maze0.iListToArr(testList), answer);
        
    }
    
    void donttestKruskel(Tester t) { // TODO
        
        Vertex A = new Vertex(0, 0);
        Vertex B = new Vertex(1, 0);
        Vertex C = new Vertex(2, 0);
        Vertex D = new Vertex(0, 1);
        Vertex E = new Vertex(1, 1);
        Vertex F = new Vertex(2, 1);
        
        Edge ec = new Edge(E, C, 15);
        Edge cd = new Edge(C, D, 25);
        Edge ab = new Edge(A, B, 30);
        Edge be = new Edge(B, E, 35);
        Edge bc = new Edge(B, C, 40);
        Edge fd = new Edge(F, D, 50);
        Edge ae = new Edge(A, E, 50);
        Edge bf = new Edge(B, F, 50);
        
        A.edges = new Cons<Edge>(ab, new Mt<Edge>());
        B.edges = new Cons<Edge>(ab, new Cons<Edge>(be,
                new Cons<Edge>(bc, new Cons<Edge>(bf,
                        new Mt<Edge>()))));
        C.edges = new Cons<Edge>(ec, new Cons<Edge>(cd,
                new Cons<Edge>(bc, new Mt<Edge>())));
        D.edges = new Cons<Edge>(cd, new Cons<Edge>(fd,
                new Mt<Edge>()));
        E.edges = new Cons<Edge>(ec, new Cons<Edge>(be,
                new Cons<Edge>(ae, new Mt<Edge>())));
        F.edges = new Cons<Edge>(fd, new Cons<Edge>(bf,
                new Mt<Edge>()));
        
        ArrayList<Edge> edgeList = new ArrayList<Edge>();
        edgeList.add(ec);
        edgeList.add(cd);
        edgeList.add(ab);
        edgeList.add(be);
        edgeList.add(bc);
        edgeList.add(fd);
        edgeList.add(ae);
        edgeList.add(bf);
        
        HashMap<String, String> uf = new HashMap<String, String>();
        uf.put("0-0", "0-0");
        uf.put("1-0", "1-0");
        uf.put("2-0", "2-0");
        uf.put("0-1", "0-1");
        uf.put("1-1", "1-1");
        uf.put("2-1", "2-1");
        
        IList<Edge> answer = new Cons<Edge>(ec, new Cons<Edge>(cd,
                new Cons<Edge>(ab, new Cons<Edge>(be, new Cons<Edge>(bc,
                        new Cons<Edge>(fd, new Cons<Edge>(ae, new Cons<Edge>(
                                bf, new Mt<Edge>()))))))));
        
        t.checkExpect(maze0.kruskel(edgeList, uf), answer);
        
    }
    
    // runs the animation
    void testRunMaze(Tester t) {
        MazeWorld maze100x60Edge = new MazeWorld(100, 60);
        maze100x60Edge.gameMode = 3;
        MazeWorld maze100x60Wall = new MazeWorld(100, 60);
        t.checkExpect(maze2.board.length(), 4);
        t.checkExpect(this.maze0.board.length(), 0);
        t.checkExpect(this.maze2.board.length(), 4);
        t.checkExpect(this.maze3.board.length(), 12);
        //t.checkExpect(maze100x60Edge.board.length(), 11840);
        
        //maze100x60Edge.bigBang(1000, 600);
        maze100x60Wall.bigBang(1000, 600);
    }
}
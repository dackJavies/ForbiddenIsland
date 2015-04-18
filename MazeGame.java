// Assignment 10
// Cherry Alexander
// acherry
// Davis Jack
// jdavis

import tester.*;

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
    IBST<T> list2Tree(IComp<T> comp);
    // reverse this list
    IList<T> rev();
    // helps to reverse this list
    IList<T> revT(IList<T> acc);
    // Is this list empty?
    boolean isEmpty();
}

// represents a function object that takes an A and returns an R
interface IFunc<A, R> {
    // Apply the function
    R apply(A a);
}

// represents a predicate object that operates on T's
interface IPred<T> {
    // Apply the function
    boolean apply(T t);
}

// represents a function that returns the x of a posn
class ToString implements IFunc<Integer, String> {
    public String apply(Integer i) {
        return (String)i.toString();
    }
}

// represents a predicate that filters out duplicate Edges
class NoDups<Edge> implements IPred<Edge> {

    Edge toCompare;

    NoDups(Edge toCompare) { this.toCompare = toCompare; }

    public boolean apply(Edge e) {

        return this.toCompare.equals(e);

    }

}

class IListIterator<T> implements Iterator<T> {

    IList<T> src;

    IListIterator(IList<T> src) { this.src = src; }

    public boolean hasNext() {

        return !this.src.isEmpty();

    }

    public T next() {

        if (!this.hasNext()) {
            throw new RuntimeException();
        }

        Cons<T> sourceAsCons = (Cons<T>)this.src;
        T result = sourceAsCons.first;
        this.src = sourceAsCons.rest;
        return result;

    }

    public void remove() {

        throw new RuntimeException("What are you doing with your life?");

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
    public IBST<T> list2Tree(IComp<T> comp) {
        return this.rest.list2Tree(comp).insert(comp, this.first);
    }
    // reverses this list
    public IList<T> rev() {
        return this.revT(new Mt<T>());
    }

    public IList<T> revT(IList<T> acc) {
        return this.rest.revT(new Cons<T>(this.first, acc));
    }

    // Is this list empty?
    public boolean isEmpty() { return false; }

    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
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
    public IBST<T> list2Tree(IComp<T> comp) {
        return new Leaf<T>();
    }
    // reverses this list
    public IList<T> rev() {
        return this;
    }

    public IList<T> revT(IList<T> acc) {
        return acc;
    }

    // Is this list empty?
    public boolean isEmpty() { return true; }

    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
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
    // Kinda self-explanatory
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

    boolean isEmpty() {
        return this.contents.size() == 0;
    }

    // Removes and returns the head of the list
    T dequeue() {
        return this.contents.removeFromHead();
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
class BTNode<T> implements IBST<T> {
  T data;
  IBST<T> left;
  IBST<T> right;
  BTNode(T data, IBST<T> left, IBST<T> right) {
      this.data = data;
      this.left = left;
      this.right = right;
  }
  // inserts an item into this tree according to the given comparator
  public IBST<T> insert(IComp<T> comp, T t) {
      if (comp.compare(this.data, t) >= 0) {
          return new BTNode<T>(this.data, this.left.insert(comp, t), this.right);
      }
      else {
          return new BTNode<T>(this.data, this.left, this.right.insert(comp, t));
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
      return new BTNode<T>(t, this, this);        
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
  public int compare(Edge e1, Edge e2) {
      Random r = new Random();
      return r.nextInt();
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
  R visit(BTNode<T> n);
  R visit(Leaf<T> n);
}

//represents a visitor that displays the cells in a list
class DisplayWallVisitor implements IVisitor<Edge, WorldImage> {
  IBST<Edge> board;
  DisplayWallVisitor(IList<Edge> board) {
      IComp<Edge> ran = new RandEdge(); 
      this.board = board.list2Tree(ran);
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
  public WorldImage visit(BTNode<Edge> n) {
      return new RectangleImage(null, 0, 0, new Color(0, 0, 0));/* TODO 
    new OverlayImages(n.data.displayEdge(waterLevel), 
            new OverlayImages(n.left.accept(this), n.right.accept(this)));*/
  }
  // visits a Leaf
  public WorldImage visit(Leaf<Edge> n) {
      return new LineImage(new Posn(-1, -1), new Posn(-1, -1), new Color(255, 255, 255));
  }    
}

//represents a visitor that displays the cells in a list
class DisplayCellVisitor implements IVisitor<Edge, WorldImage> {
  IBST<Edge> board;
  DisplayCellVisitor(IList<Edge> board) {
      IComp<Edge> ran = new RandEdge(); 
      this.board = board.list2Tree(ran);
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
  public WorldImage visit(BTNode<Edge> n) {
      return new RectangleImage(null, 0, 0, new Color(0, 0, 0));/* TODO 
  new OverlayImages(n.data.displayEdge(waterLevel), 
          new OverlayImages(n.left.accept(this), n.right.accept(this)));*/
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

    int x;
    int y;

    Vertex(int x, int y) {
        this.edges = new Mt<Edge>();
        this.wasSearched = false;
        this.correctPath = false;

        this.x = x;
        this.y = y;
    }

    // Add an Edge with an entered weight 
    void addEdge(Vertex other, int opt) {
        Edge toAdd = new Edge(this, other, opt);
        this.edges = this.edges.addToBack(toAdd);
        other.edges = other.edges.addToBack(toAdd);
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
}

// represents the gameWorld
class MazeWorld extends World {
    // Size of the game
    int gameSizeX;
    int gameSizeY;
    IList<Edge> board;
    IList<Edge> workList;

    MazeWorld(int gameSizeX, int gameSizeY) {
        this.gameSizeX = gameSizeX;
        this.gameSizeY = gameSizeY;
        this.board = new Mt<Edge>();
        this.workList = new Mt<Edge>();
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

        return result;

    }
    /* TODO edit the next method until tests work then fix this with those fixes
    // Add edges to the given ArrayList<ArrayList<Vertex>>
    void addEdges(ArrayList<ArrayList<Vertex>> grid) {
        Random randy = new Random();

        // Connections to the left/right
        for(int i = 1; i < grid.size(); i += 1) {

            for(int i2 = 0; i2 < grid.get(i).size(); i2 += 1) {

                grid.get(i).get(i2).addEdge(grid.get(i - 1).get(i2), 
                        Math.abs(randy.nextInt() / 10000));

            }

        }

        // Connections to the top/bottom
        for(int i3 = 0; i3 < grid.size(); i3 += 1) {

            for(int i4 = 0; i4 < grid.get(i3).size(); i4 += 1) {

                grid.get(i3).get(i4).addEdge(grid.get(i3).get(i4 - 1), 
                        Math.abs(randy.nextInt() / 10000));

            }

        }

    }
     */ 

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


    /* // Convert a 2D ArrayList of Vertices to a 1D ArrayList of Edges
    ArrayList<Edge> vertexToEdge(ArrayList<ArrayList<Vertex>> grid) {

        ArrayList<IList<Edge>> listOfLists = new ArrayList<IList<Edge>>();
        IList<Edge> edges = new Mt<Edge>();

        // Copy all Vertices' Edge lists in grid into listOfLists
        for(int i = 0; i < grid.size(); i += 1) {

            for(int i2 = 0; i2 < grid.get(i).size(); i2 += 1) {

                listOfLists.add(grid.get(i).get(i2).edges);

            }
        }

        edges = this.vertexToEdgeHelp(listOfLists);

    }

    // Concatenates all the lists in the given ArrayList<Edge> into an IList<Edge>
    IList<Edge> vertexToEdgeHelp(ArrayList<IList<Edge>> listOfLists) {

        IList<Edge> edges = new Mt<Edge>();

        // Append those lists into one large IList<Edge>
        for(IList<Edge> e: listOfLists) {

            edges = edges.append(e);

        }

        return edges;

    }
     */
    // Implement Union/Find data structure while applying
    // Kruskel's algorithm.
    // EFFECT: mutates the edge lists in each Vertex in the given ArrayList
    ArrayList<ArrayList<Vertex>> kruskel(ArrayList<ArrayList<Vertex>> grid) {
        HashMap<String, String> representatives = new HashMap<String, String>();
        ArrayList<ArrayList<Vertex>> worklist = grid;

        // populate hashmap
        for(Integer i = 0; i < grid.size(); i += 1) {

            for(Integer i2 = 0; i2 < grid.get(i).size(); i2 += 1) {

                // Vertices are represented as their coordinates separated
                // by a dash. i.e. (1, 1) is 1-1.
                String toPut = i.toString() + "-" + i2.toString();

                // All values are initialized the same value as the key
                representatives.put(toPut, toPut);

            }

        }

        while(worklist.size() > 0) {



        }

        return grid; //THIS IS A STUB: TODO
    }

    // Draws the world TODO
    public WorldImage makeImage() {
        return null;
        /*
         * // Draws the World
    public WorldImage makeImage() {
        DisplayCellVisitor dCVisitor = 
                new DisplayCellVisitor(this.board);
        DisplayWallVisitor dWVisitor = 
                new DisplayWallVisitor(this.board);
        return new OverlayImages(dCVisitor.board.accept(dCVisitor),
                     dWVisitor.accept(this.unusedVertices));
    }
         */
        
    }

}


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
        IBST<String> sL = new Leaf<String>();
        IBST<String> n1 = new BTNode<String>("hi", sL, sL);
        IBST<String> n2 = new BTNode<String>("bye", n1, sL);
        IBST<String> n3 = new BTNode<String>("so", n1, sL);
        t.checkExpect(sL.isLeaf(), true);
        t.checkExpect(n1.isLeaf(), false);
        t.checkExpect(n2.isLeaf(), false);
        t.checkExpect(n3.isLeaf(), false);
    }

    // tests insert in the IBST interface
    void testInsert(Tester t) {
        IComp<Vertex> comp = new CompVert();
        Vertex c1 = new Vertex(0, 0);
        Vertex c2 = new Vertex(0, 1);
        Vertex c3 = new Vertex(1, 0);
        Vertex c4 = new Vertex(1, 1);
        IBST<Vertex> sC = new Leaf<Vertex>();
        IBST<Vertex> n0 = new BTNode<Vertex>(c4, sC, sC);
        IBST<Vertex> n1 = new BTNode<Vertex>(c2, sC, sC);
        IBST<Vertex> n2 = new BTNode<Vertex>(c3, sC, sC);
        IBST<Vertex> n3 = new BTNode<Vertex>(c1, n1, n2);
        IBST<Vertex> n2a = new BTNode<Vertex>(c3, sC, n0);
        IBST<Vertex> n3a = new BTNode<Vertex>(c1, n1, n2a);
        t.checkExpect(n3.insert(comp, c4), n3a);
    }
    // tests accept for the interfaces IList<T> and IBST<T> TODO
    void testAccept(Tester t) {
      /*  Mt<Vertex> mT = new Mt<Vertex>();
        Cons<Vertex> cons = new Cons<Vertex>(new Vertex(5, 7), mT);
        Leaf<Vertex> leaf = new Leaf<Vertex>();
        Node<Vertex> node = new Node<Vertex>(new Vertex(5, 7), leaf, leaf);
        DisplayVertexsVisitor dCV = new DisplayVertexsVisitor(cons, 0);
        t.checkExpect(leaf.accept(dCV), dCV.visit(leaf));
        t.checkExpect(node.accept(dCV), dCV.visit(node));
        t.checkException(
                new IllegalArgumentException("IList is not a valid argument"), cons, "accept", dCV);
        t.checkException(
                new IllegalArgumentException("IList is not a valid argument"), mT, "accept", dCV);*/
    }
    // tests addEdges for the class MazeWorld TODO
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
}
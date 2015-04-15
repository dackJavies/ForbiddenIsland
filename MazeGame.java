// Assignment 10
// Cherry Alexander
// acherry
// Davis Jack
// jdavis

import tester.*;

import java.util.ArrayList;
import java.util.HashMap;

import javalib.colors.*;
import javalib.impworld.*;
import javalib.worldimages.*;

// represents a list
interface IList<T> {
    // Computes the size of this list
    int length();
    // creates a new list with the given item added to the front
    IList<T> add(T item);
    // map the given IFunc over the entire list
    <R> IList<R> map(IFunc<T,R> func);
}

// represents a function object that takes an A and returns an R
interface IFunc<A, R> {
    // Apply the function
    R apply(A a);
}

// represents a function that returns the x of a posn
class ToString implements IFunc<Integer, String> {
    public String apply(Integer i) {
        return (String)i.toString();
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
    public IList<T> add(T item) {
        return new Cons<T>(item, this);
    }
    // map the given function over the entire list
    public <R> IList<R> map(IFunc<T, R> func) {
        return new Cons<R>(func.apply(this.first), this.rest.map(func));
    }
} 

// represents an empty list
class Mt<T> implements IList<T> {
    // Computes the size of this list
    public int length() {
        return 0;
    }
    // creates a new list with the given item added to the front
    public IList<T> add(T item) {
        return new Cons<T>(item, this);
    }
    // map the given function over the entire list
    public <R> IList<R> map(IFunc<T, R> func) {
        return new Mt<R>();
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

// represents a maze cell
class Cell {
    boolean wasSearched;
    boolean correctPath;
    // convenience constructor for testing
    Cell(boolean wasSearched, boolean correctPath) {
        this.wasSearched = wasSearched;
        this.correctPath = correctPath;
    }
    Cell() {
        this.wasSearched = false;
        this.correctPath = false;
    }
}

// represents a vertex of the tree
class Vertex<T> {
    IList<Edge<T>> edges;
    boolean wasSearched;
    boolean correctPath;
    Vertex(IList<Edge<T>> edges) {
        this.edges = edges;
        this.wasSearched = false;
        this.correctPath = false;
    }
}

// represents an edge of the tree
class Edge<T> {
    Vertex<T> from;
    Vertex<T> to;
    int weight;
    Edge(Vertex<T> from, Vertex<T> to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }    
}

class MazeWorld extends World {
    HashMap<String, String> representatives;
    IList<Edge<Cell>> worldEdges;
    IList<Edge<Cell>> workList;
    MazeWorld() {
        this.representatives = new HashMap<String, String>();
        this.workList = new Mt<Edge<Cell>>();
    }

    // Draws the world TODO
    public WorldImage makeImage() {
        return null;
    }

}


class ExamplesMaze {
    // List test lists
    IList<Integer> mTI = new Mt<Integer>();
    IList<Integer> listI1 = new Cons<Integer>(1, new Cons<Integer>(2, 
            new Cons<Integer>(3, new Cons<Integer>(4, mTI))));
    IList<Integer> listI2 = new Cons<Integer>(5, this.listI1);
    
    // Function objects
    ToString tS = new ToString();
    
    // tests length for the interface IList<T> TODO 
    void testLength(Tester t) {
        t.checkExpect(mTI.length(), 0);
        t.checkExpect(listI1.length(), 4);
        t.checkExpect(listI2.length(), 5);
    }
    // tests add for the interface IList<T> TODO
    void testAdd(Tester t) {
        t.checkExpect(mTI.add(2), new Cons<Integer>(2, mTI));
        t.checkExpect(listI1.add(5), listI2);
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
}
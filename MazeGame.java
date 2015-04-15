// Assignment 10
// Cherry Alexander
// acherry
// Davis Jack
// jdavis

import tester.*;

import java.util.ArrayList;
import java.util.HashMap;

import javalib.colors.*;
import javalib.impworld.World;
import javalib.worldimages.WorldImage;

// represents a list
interface IList<T> {
    // Computes the size of this list
    int length();
    // creates a new list with the given item added to the front
    public IList<T> add(T item);
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
    // Disclaimer: Deque tests taken from old deque homework

    Deque<String> deque1 = new Deque<String>();
    Sentinel<String> sHeader1 = new Sentinel<String>();
    ANode<String> abc = new Node<String>("abc", sHeader1, sHeader1);
    ANode<String> bcd = new Node<String>("bcd", sHeader1, abc);
    ANode<String> cde = new Node<String>("cde", sHeader1, bcd);
    ANode<String> def = new Node<String>("def", sHeader1, cde);
    Deque<String> deque2 = new Deque<String>(sHeader1);
    Sentinel<String> sHeader2 = new Sentinel<String>();
    ANode<String> cat = new Node<String>("cat", sHeader2, sHeader2);
    ANode<String> bob = new Node<String>("bob", sHeader2, cat);
    ANode<String> apple = new Node<String>("apple", sHeader2, bob);
    ANode<String> dog = new Node<String>("dog", sHeader2, apple);
    ANode<String> car = new Node<String>("car", sHeader2, dog);
    Deque<String> deque3 = new Deque<String>(sHeader2);

    // initializes the Deques
    void initialize() {
        this.deque1 = new Deque<String>();
        this.sHeader1 = new Sentinel<String>();
        this.abc = new Node<String>("abc", sHeader1, sHeader1);
        this.bcd = new Node<String>("bcd", sHeader1, abc);
        this.cde = new Node<String>("cde", sHeader1, bcd);
        this.def = new Node<String>("def", sHeader1, cde);
        this.deque2 = new Deque<String>(sHeader1);
        this.sHeader2 = new Sentinel<String>();
        this.cat = new Node<String>("cat", sHeader2, sHeader2);
        this.bob = new Node<String>("bob", sHeader2, cat);
        this.apple = new Node<String>("apple", sHeader2, bob);
        this.dog = new Node<String>("dog", sHeader2, apple);
        this.car = new Node<String>("car", sHeader2, dog);
        this.deque3 = new Deque<String>(sHeader2);
    }

    // tests isNode for the ANode class
    void testIsNode(Tester t) {
        this.initialize();
        t.checkExpect(this.deque1.header.isNode(), false);
        t.checkExpect(this.deque2.header.isNode(), false);
        t.checkExpect(this.deque2.header.next.isNode(), true);
        t.checkExpect(this.deque2.header.prev.isNode(), true);
    }

    // tests countNodes for the Deque class
    void testCountNodes(Tester t) {
        this.initialize();
        t.checkExpect(this.sHeader1.countNodes(), 0);
        t.checkExpect(this.abc.countNodes(), 4);
        t.checkExpect(this.cat.countNodes(), 5);
        t.checkExpect(this.bob.countNodes(), 4);
        t.checkExpect(this.cde.countNodes(), 2);
    }
    // tests size for the Deque class
    void testSize(Tester t) {
        this.initialize();
        t.checkExpect(this.deque1.size(), 0);
        t.checkExpect(this.deque2.size(), 4);
        t.checkExpect(this.deque3.size(), 5);
    }

    // tests addAtHead for the Deque class
    void testAddAtHead(Tester t) {
        Sentinel<String> s1 = new Sentinel<String>();
        Node<String> n1 = new Node<String>("car", s1, s1);
        Node<String> n2 = new Node<String>("dog", n1, s1);
        Node<String> n3 = new Node<String>("apple", n2, s1);
        Node<String> n4 = new Node<String>("bob", n3, s1);
        Node<String> n5 = new Node<String>("cat", n4, s1);
        Node<String> n6 = new Node<String>("card", n5, s1);
        Deque<String> d1 = new Deque<String>(s1);

        Sentinel<String> s2 = new Sentinel<String>();
        Node<String> n7 = new Node<String>("call", s2, s2);  
        Deque<String> d2 = new Deque<String>(s2);

        Sentinel<String> s3 = new Sentinel<String>();
        Node<String> nA = new Node<String>("def", s3, s3);
        Node<String> nB = new Node<String>("cde", nA, s3);
        Node<String> nC = new Node<String>("bcd", nB, s3);
        Node<String> nD = new Node<String>("abc", nC, s3);
        Node<String> nZ = new Node<String>("frog", nD, s3);
        Deque<String> d3 = new Deque<String>(s3);

        Sentinel<String> s4 = new Sentinel<String>();
        Node<String> nE = new Node<String>("def", s4, s4);
        Node<String> nF = new Node<String>("cde", nE, s4);
        Node<String> nG = new Node<String>("bcd", nF, s4);
        Node<String> nH = new Node<String>("abc", nG, s4);
        Node<String> nI = new Node<String>("frog", nH, s4);
        Node<String> nJ = new Node<String>("bark", nI, s4);
        Deque<String> d4 = new Deque<String>(s4);

        this.initialize();
        t.checkExpect(this.deque1, new Deque<String>());
        this.deque1.addAtHead("call");
        t.checkExpect(this.deque1, d2);
        t.checkExpect(this.deque2.header.next, this.abc);
        this.deque2.addAtHead("frog");
        t.checkExpect(this.deque2, d3);
        t.checkExpect(this.deque2.header.next, nZ);
        this.deque2.addAtHead("bark");
        t.checkExpect(this.deque2, d4);
        t.checkExpect(this.deque3.header.next, this.cat);
        this.deque3.addAtHead("card");
        t.checkExpect(this.deque3, d1);

    }

    // tests addAtTail for the Deque class
    void testAddAtTail(Tester t) {
        Sentinel<String> s1 = new Sentinel<String>();
        Node<String> n1 = new Node<String>("car", s1, s1);
        Node<String> n2 = new Node<String>("dog", n1, s1);
        Node<String> n3 = new Node<String>("apple", n2, s1);
        Node<String> n4 = new Node<String>("bob", n3, s1);
        Node<String> n5 = new Node<String>("cat", n4, s1);
        Node<String> n6 = new Node<String>("card", s1, n1);
        Deque<String> d1 = new Deque<String>(s1);

        Sentinel<String> s2 = new Sentinel<String>();
        Node<String> n7 = new Node<String>("call", s2, s2);  
        Deque<String> d2 = new Deque<String>(s2);

        Sentinel<String> s3 = new Sentinel<String>();
        Node<String> n0 = new Node<String>("frog", s3, s3);
        Node<String> nA = new Node<String>("def", n0, s3);
        Node<String> nB = new Node<String>("cde", nA, s3);
        Node<String> nC = new Node<String>("bcd", nB, s3);
        Node<String> nD = new Node<String>("abc", nC, s3);
        Deque<String> d3 = new Deque<String>(s3);

        Sentinel<String> s4 = new Sentinel<String>();
        Node<String> nE = new Node<String>("def", s4, s4);
        Node<String> nF = new Node<String>("cde", nE, s4);
        Node<String> nG = new Node<String>("bcd", nF, s4);
        Node<String> nH = new Node<String>("abc", nG, s4);
        Node<String> nI = new Node<String>("frog", s4, nE);
        Node<String> nJ = new Node<String>("bark", s4, nI);
        Deque<String> d4 = new Deque<String>(s4);

        this.initialize();
        t.checkExpect(this.deque1, new Deque<String>());
        this.deque1.addAtTail("call");
        t.checkExpect(this.deque1, d2);
        t.checkExpect(this.deque2.header.prev, this.def);
        this.deque2.addAtTail("frog");
        t.checkExpect(this.deque2, d3);
        t.checkExpect(this.deque2.header.prev, n0);
        this.deque2.addAtTail("bark");
        t.checkExpect(this.deque2, d4);
        t.checkExpect(this.deque3.header.prev, this.car);
        this.deque3.addAtTail("card");
        t.checkExpect(this.deque3, d1);
    }

    // tests removeFromHead for the Deque class
    void testRemoveFromHead(Tester t) {
        Sentinel<String> s1 = new Sentinel<String>();
        Node<String> n1 = new Node<String>("car", s1, s1);
        Node<String> n2 = new Node<String>("dog", n1, s1);
        Node<String> n3 = new Node<String>("apple", n2, s1);
        Node<String> n4 = new Node<String>("bob", n3, s1);
        Deque<String> d1 = new Deque<String>(s1);

        Sentinel<String> s3 = new Sentinel<String>();
        Node<String> nA = new Node<String>("def", s3, s3);
        Node<String> nB = new Node<String>("cde", nA, s3);
        Node<String> nC = new Node<String>("bcd", nB, s3);
        Deque<String> d3 = new Deque<String>(s3);

        Sentinel<String> s4 = new Sentinel<String>();
        Node<String> nD = new Node<String>("def", s4, s4);
        Node<String> nE = new Node<String>("cde", nD, s4);
        Deque<String> d4 = new Deque<String>(s4);

        this.initialize();
        t.checkExpect(this.deque1, new Deque<String>());
        t.checkException(new RuntimeException("cannot remove first item from empty list"), 
                this.deque1, "removeFromHead");
        t.checkExpect(this.deque2.header.next, this.abc);
        this.deque2.removeFromHead();
        t.checkExpect(this.deque2, d3);
        t.checkExpect(this.deque2.header.next, nC);
        this.deque2.removeFromHead();
        t.checkExpect(this.deque2, d4);
        t.checkExpect(this.deque2.header.next, nE);
        t.checkExpect(this.deque3.header.next, this.cat);
        this.deque3.removeFromHead();
        t.checkExpect(this.deque3, d1);
    }

    // tests removeFromTail for the Deque class
    void testRemoveFromTail(Tester t) {
        Sentinel<String> s1 = new Sentinel<String>();
        Node<String> n2 = new Node<String>("dog", s1, s1);
        Node<String> n3 = new Node<String>("apple", n2, s1);
        Node<String> n4 = new Node<String>("bob", n3, s1);
        Node<String> n5 = new Node<String>("cat", n4, s1);
        Deque<String> d1 = new Deque<String>(s1);

        Sentinel<String> s3 = new Sentinel<String>();
        Node<String> nB = new Node<String>("cde", s3, s3);
        Node<String> nC = new Node<String>("bcd", nB, s3);
        Node<String> nD = new Node<String>("abc", nC, s3);
        Deque<String> d3 = new Deque<String>(s3);

        Sentinel<String> s4 = new Sentinel<String>();
        Node<String> nG = new Node<String>("bcd", s4, s4);
        Node<String> nH = new Node<String>("abc", nG, s4);
        Deque<String> d4 = new Deque<String>(s4);

        this.initialize();
        t.checkExpect(this.deque1, new Deque<String>());
        t.checkException(new RuntimeException("cannot remove last item from empty list"), 
                this.deque1, "removeFromTail");
        t.checkExpect(this.deque2.header.prev, this.def);
        this.deque2.removeFromTail();
        t.checkExpect(this.deque2, d3);
        t.checkExpect(this.deque2.header.prev, nB);
        this.deque2.removeFromTail();
        t.checkExpect(this.deque2, d4);
        t.checkExpect(this.deque2.header.prev, nG);
        t.checkExpect(this.deque3.header.prev, this.car);
        this.deque3.removeFromTail();
        t.checkExpect(this.deque3, d1);
    }

    // tests removeNode for the deque class
    void testRemoveNode(Tester t) {
        Sentinel<String> s1 = new Sentinel<String>();
        Node<String> n2 = new Node<String>("dog", s1, s1);
        Node<String> n3 = new Node<String>("apple", n2, s1);
        Node<String> n4 = new Node<String>("bob", n3, s1);
        Node<String> n5 = new Node<String>("cat", n4, s1);
        Deque<String> d1 = new Deque<String>(s1);

        Sentinel<String> s3 = new Sentinel<String>();
        Node<String> nA = new Node<String>("def", s3, s3);
        Node<String> nB = new Node<String>("cde", nA, s3);
        Node<String> nC = new Node<String>("bcd", nB, s3);
        Deque<String> d3 = new Deque<String>(s3);

        this.initialize();
        t.checkExpect(this.deque2.header.next, this.abc);
        this.deque2.removeNode(this.abc);
        t.checkExpect(this.deque2.header.next, nC);
        t.checkExpect(this.deque3.header.prev, this.car);
        this.deque3.removeNode(this.car);
        t.checkExpect(this.deque3.header.prev, n2);
    }
    // tests length for the interface IList<T> TODO 
    void testLength(Tester t) {

    }
    // tests add for the interface IList<T> TODO
    void testAdd(Tester t) {

    }
}
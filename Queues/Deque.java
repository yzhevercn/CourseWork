/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node first;
    private Node last;
    private int n;

    private class Node {
        Item item;
        Node next;
        Node last;
    }

    public Deque() {
        n = 0;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.last = null;

        if (isEmpty()) {
            first.next = null;
            last = first;
        }
        else {
            first.next = oldfirst;
            oldfirst.last = first;
        }
        n++;
    }

    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty()) {
            last.last = null;
            first = last;
        }
        else {
            last.last = oldlast;
            oldlast.next = last;
        }
        n++;
    }

    public Item removeFirst() {
        if (isEmpty())
            throw new NoSuchElementException("Trying to remove element from an empty queue.");
        Item item = first.item;
        first = first.next;
        if (first != null) first.last = null;
        else last = null;
        n--;
        return item;
    }

    public Item removeLast() {
        if (isEmpty())
            throw new NoSuchElementException("Trying to remove element from an empty queue.");
        Item item = last.item;
        last = last.last;
        if (last != null) last.next = null;
        else first = null;
        n--;
        return item;
    }

    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return (current != null) && (n != 0);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    public static void main(String[] args) {
        // int n = 0;
        // int k = Integer.parseInt(args[0]);
        // if (k <= 0) throw new IllegalArgumentException("check k, bro");
        // String item = StdIn.readString();
        Deque<Integer> deque = new Deque<Integer>();
        // deque.addFirst(1);
        // deque.removeFirst();
        for (int i :
                deque) {
            StdOut.println(i);
        }
    }
}

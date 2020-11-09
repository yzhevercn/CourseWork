/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int n;
    private Item[] a = (Item[]) new Object[1];

    public RandomizedQueue() {
        n = 0;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    private void resize(int max) {
        Item[] temp = (Item[]) new Object[max];
        for (int i = 0; i < n; i++) temp[i] = a[i];
        a = temp;
    }

    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();
        if (a.length == n) resize(2 * a.length);
        a[n++] = item;
    }

    public Item dequeue() {
        if (n == 0) throw new NoSuchElementException();
        int removeLoc = StdRandom.uniform(n);
        Item item = a[removeLoc];
        a[removeLoc] = a[--n];
        a[n] = null;
        if (n > 0 && n == a.length / 4) resize(a.length / 2);
        return item;
    }

    public Item sample() {
        if (n == 0) throw new NoSuchElementException();
        int sampleLoc = StdRandom.uniform(n);
        Item item = a[sampleLoc];
        return item;
    }

    private Item[] shuffleArray() {
        Item[] s = (Item[]) new Object[n];
        for (int i = 0; i < n; i++) {
            s[i] = a[i];
        }
        StdRandom.shuffle(s);
        return s;
    }

    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {
        private int i = 0;
        private final Item[] sa = shuffleArray();

        public boolean hasNext() {
            return i < n;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return sa[i++];
        }
    }


    public static void main(String[] args) {
        int n = 9;
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            for (int b : queue)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
        }
    }
}

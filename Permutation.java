/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int n = 0;
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<String>();

        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            queue.enqueue(item);
            n++;
        }

        if ((k > n) || (k < 0))
            throw new IllegalArgumentException("k out of range.");
        for (int i = 0; i < k; i++) {
            StdOut.println(queue.dequeue());
        }

    }
}

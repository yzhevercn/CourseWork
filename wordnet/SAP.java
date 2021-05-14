/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SAP {
    private final Digraph G;
    private final int numV;

    // constructor take a digraph
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Check if the input G is null!");
        this.G = new Digraph(G);
        this.numV = G.V();
    }


    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= numV)
            throw new IllegalArgumentException(
                    "vertex " + v + " is not between 0 and " + (numV - 1));
    }

    // check if any argument in Iterable<> is invalid
    private void validateVertex(Iterable<Integer> v) {
        if (v == null) throw new IllegalArgumentException("argument is null!");
        // check input is null, use Integer instead of int
        for (Integer i : v) {
            if (i == null) throw new IllegalArgumentException("argument is null!");
            validateVertex(i);
        }
    }


    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return sap(v, w)[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return sap(v, w)[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertex(v);
        validateVertex(w);
        return sap(v, w)[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertex(v);
        validateVertex(w);
        return sap(v, w)[1];
    }

    private int[] sap(Iterable<Integer> v, Iterable<Integer> w) {
        // start bfs from all vertices in v and w AT ONCE
        // there is no need to bsf all vertices one by one
        // bfs all v in digraph to record shortest distance
        Queue<Integer> qv = new Queue<Integer>();
        boolean[] marked = new boolean[this.numV];
        int[] distV = new int[this.numV];
        Arrays.fill(distV, -1);
        Arrays.fill(marked, false);
        for (int i : v) {
            marked[i] = true;
            qv.enqueue(i);
            distV[i] = 0;
        }
        while (!qv.isEmpty()) {
            int vv = qv.dequeue();
            for (int vs : G.adj(vv)) {
                if (!marked[vs]) {
                    marked[vs] = true;
                    qv.enqueue(vs);
                    // step by step far from v
                    distV[vs] = distV[vv] + 1;
                }
            }
        }

        // bfs all w in digraph
        // you have to bfs all the digraph from v and w respectively
        // if not, you can not ensure that the path you get is the shortest one
        // it often happens when the common ancestor is v or w itself
        Arrays.fill(marked, false);
        int[] distW = new int[this.numV];
        Arrays.fill(distW, -1);
        Queue<Integer> qw = new Queue<Integer>();
        for (int i : w) {
            marked[i] = true;
            qw.enqueue(i);
            distW[i] = 0;
        }
        while (!qw.isEmpty()) {
            int ww = qw.dequeue();
            for (int ws : G.adj(ww)) {
                if (!marked[ws]) {
                    qw.enqueue(ws);
                    marked[ws] = true;
                    distW[ws] = distW[ww] + 1;
                }
            }
        }

        // result[0] for the shortest distance, result[1] for the common ancestor
        int[] result = { -1, -1 };
        for (int i = 0; i < this.numV; i++) {
            if (distV[i] != -1 && distW[i] != -1) {
                int curDist = distV[i] + distW[i];
                if (curDist < result[0] || result[0] == -1) {
                    result[0] = curDist;
                    result[1] = i;
                }
            }
        }

        return result;
    }

    private int[] sap(int v, int w) {
        Queue<Integer> qv = new Queue<Integer>();
        Queue<Integer> qw = new Queue<Integer>();
        qv.enqueue(v);
        qw.enqueue(w);
        return sap(qv, qw);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}

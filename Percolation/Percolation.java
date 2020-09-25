/* *****************************************************************************
 *  Name:              Z.H Yu
 *  Coursera User ID:  zhyu
 *  Last modified:     9/24/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int numNode;
    private final int size;
    private int openSite = 0;
    private boolean[][] nodeState;
    private final WeightedQuickUnionUF oneDimNode;
    private final WeightedQuickUnionUF oneDimNode1;


    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                    "index " + n + " is outside prescribed range.");
        }
        size = n;
        numNode = n * n + 2;
        nodeState = new boolean[n][n];
        oneDimNode = new WeightedQuickUnionUF(numNode);
        oneDimNode1 = new WeightedQuickUnionUF(numNode);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                nodeState[i][j] = false;
        }
    }

    private int getNode(int row, int col) {
        int inRow = row - 1;
        int inCol = col - 1;
        return (inRow * size) + (inCol + 1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        int inRow = row - 1;
        int inCol = col - 1;
        int leftNode, rightNode, upNode, downNode;
        int targetNode = getNode(row, col);
        if (inRow >= size || inCol >= size || inRow < 0 || inCol < 0) {
            throw new IllegalArgumentException(
                    "index(" + row + "," + col + ") is outside prescribed range.");
        }

        if (!nodeState[inRow][inCol]) {
            nodeState[inRow][inCol] = true;
            openSite += 1;
        }
        else {
            return;
        }

        if (inCol > 0) {
            leftNode = targetNode - 1;
            if (isOpen(row, col - 1)) {
                oneDimNode.union(leftNode, targetNode);
                oneDimNode1.union(leftNode, targetNode);
            }
        }

        if (inCol < (size - 1)) {
            rightNode = targetNode + 1;
            if (isOpen(row, col + 1)) {
                oneDimNode.union(rightNode, targetNode);
                oneDimNode1.union(rightNode, targetNode);
            }
        }

        if (inRow > 0) {
            upNode = targetNode - size;
            if (isOpen(row - 1, col)) {
                oneDimNode.union(upNode, targetNode);
                oneDimNode1.union(upNode, targetNode);
            }
        }
        else {
            oneDimNode.union(0, targetNode);
            oneDimNode1.union(0, targetNode);
        }

        if (inRow < (size - 1)) {
            downNode = targetNode + size;
            if (isOpen(row + 1, col)) {
                oneDimNode.union(downNode, targetNode);
                oneDimNode1.union(downNode, targetNode);
            }
        }
        else {
            oneDimNode.union((numNode - 1), targetNode);
        }

    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        int inRow = row - 1;
        int inCol = col - 1;
        if (inRow >= size || inCol >= size || inRow < 0 || inCol < 0) {
            throw new IllegalArgumentException(
                    "index(" + row + "," + col + ") is outside prescribed range.");
        }
        return nodeState[inRow][inCol];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        int inRow = row - 1;
        int inCol = col - 1;
        if (inRow >= size || inCol >= size || inRow < 0 || inCol < 0) {
            throw new IllegalArgumentException(
                    "index(" + row + "," + col + ") is outside prescribed range.");
        }
        int targetNode = getNode(row, col);
        return (oneDimNode1.find(targetNode) == oneDimNode1.find(0));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSite;
    }

    // does the system percolate?
    public boolean percolates() {
        return (oneDimNode.find(0) == oneDimNode.find(numNode - 1));
    }

    // test client
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        if (n <= 0) {
            throw new IllegalArgumentException("n is outside prescribed range.");
        }
        Percolation per = new Percolation(n);

        while (!in.isEmpty()) {
            int q = in.readInt();
            int p = in.readInt();
            if (per.isOpen(p, q)) continue;
            per.open(p, q);
            StdOut.println("[" + p + ", " + q + "] opened.");
        }

        // if (per.percolates()) {
        //     StdOut.println("Percolate!");
        // }
        // else {
        //     StdOut.println("Failed!");
        // }

    }
}

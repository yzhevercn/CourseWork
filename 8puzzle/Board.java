/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final int n;
    private final int blankRow;
    private final int blankCol;
    private final int[][] curBoard;
    private final int hammingDistance;
    private final int manhattanDistance;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("argument is null!");
        }
        int[] blank = { -1, -1 };
        int ham = 0, man = 0;

        this.n = tiles.length;
        this.curBoard = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.curBoard[i][j] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    blank[0] = i;
                    blank[1] = j;
                }
                else {
                    if (tiles[i][j] != i * n + j + 1) {
                        ham++;

                        int jGoal = (tiles[i][j] - 1) % n;
                        int iGoal = (tiles[i][j] - jGoal - 1) / n;
                        if (iGoal * jGoal < 0)
                            throw new IllegalArgumentException("wtf goal index less than 0???");
                        man += Math.abs(jGoal - j) + Math.abs(iGoal - i);
                    }
                }
            }
        }

        this.blankRow = blank[0];
        this.blankCol = blank[1];
        this.hammingDistance = ham;
        this.manhattanDistance = man;

    }

    // string representation of this board
    public String toString() {
        StringBuilder strBoard = new StringBuilder();
        strBoard.append(Integer.toString(n) + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) strBoard.append(" " + Integer.toString(curBoard[i][j]));
            strBoard.append("\n");
        }
        return strBoard.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hammingDistance == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;
        Board that = (Board) y;
        if (this.dimension() != that.dimension()) return false;
        if (this.hammingDistance != that.hammingDistance) return false;
        if (this.manhattanDistance != that.manhattanDistance) return false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.curBoard[i][j] != that.curBoard[i][j]) return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        LinkedQueue<Board> boardList = new LinkedQueue<Board>();
        int row = blankRow;
        int col = blankCol;
        if (row < 0 || row > n || col < 0 || col > n) {
            throw new IllegalArgumentException(
                    "index(" + row + "," + col + ") is outside prescribed range.");
        }

        // exchange if exist right node
        if (col < n - 1) {
            Board neighborBoard = new Board(
                    this.exchangeEle(curBoard, row, col, row, col + 1));
            boardList.enqueue(neighborBoard);
        }
        if (col > 0) {
            // exchange left node
            Board neighborBoard = new Board(
                    this.exchangeEle(curBoard, row, col, row, col - 1));
            boardList.enqueue(neighborBoard);
        }

        // exchange if exist down node
        if (row < n - 1) {
            Board neighborBoard = new Board(
                    this.exchangeEle(curBoard, row + 1, col, row, col));
            boardList.enqueue(neighborBoard);
        }
        if (row > 0) {
            // exchange up node
            Board neighborBoard = new Board(
                    this.exchangeEle(curBoard, row, col, row - 1, col));
            boardList.enqueue(neighborBoard);
        }

        return boardList;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int i1 = 0, j1 = 0;
        if (blankRow == 0 && blankCol == 0) j1 = 1;
        for (int i2 = 0; i2 < n; i2++)
            for (int j2 = 0; j2 < n; j2++) {
                if ((i2 != blankRow || j2 != blankCol) && (i2 != i1 || j2 != j1)) {
                    Board twinBoard = new Board(exchangeEle(curBoard, i1, j1, i2, j2));
                    return twinBoard;
                }
            }
        return this;
    }

    private int[][] exchangeEle(int[][] a, int i1, int j1, int i2, int j2) {
        int[][] b = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                b[i][j] = a[i][j];
        int swap = b[i1][j1];
        b[i1][j1] = b[i2][j2];
        b[i2][j2] = swap;

        return b;
    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) tiles[i][j] = in.readInt();
        }
        Board initial = new Board(tiles);
        for (Board i : initial.neighbors())
            StdOut.println(i.manhattan());
        Board test = initial.twin();
        int i = 0;
        while (!test.equals(initial)) {
            test = initial.twin();
            i++;
        }
        StdOut.println(i);

    }
}

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {
    // solve 8-puzzle using MinPriorityQueue

    private final BoardObject solveNode; // point to the last node of solution (the ordered board)
    private boolean isSolvable = false;

    // find a solution to the initial board (using the A*algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Input board is null!");
        Board twin = initial.twin();
        BoardObject initialObject = new BoardObject(initial, null, 0);
        BoardObject twinObject = new BoardObject(twin, null, 0);

        // compare points according to their man-distance + moves
        Comparator<BoardObject> mOrder = new MOrder();
        MinPQ<BoardObject> searchPQ = new MinPQ<BoardObject>(1, mOrder);
        MinPQ<BoardObject> searchPQTwin = new MinPQ<BoardObject>(1, mOrder);

        searchPQ.insert(initialObject);
        searchPQTwin.insert(twinObject);

        BoardObject searchNode = searchPQ.delMin();
        BoardObject searchNodeTwin = searchPQTwin.delMin();

        while (!searchNode.board.isGoal()) {
            if (searchNodeTwin.board.isGoal()) break;

            for (Board neighbor : searchNode.board.neighbors()) {
                if (searchNode.previous == null || !neighbor.equals(searchNode.previous.board)) {
                    BoardObject newNode = new BoardObject(neighbor, searchNode,
                                                          searchNode.moves + 1);
                    searchPQ.insert(newNode);
                }

            }

            for (Board neighbor : searchNodeTwin.board.neighbors()) {
                if (searchNodeTwin.previous == null || !neighbor
                        .equals(searchNodeTwin.previous.board)) {
                    BoardObject newNode = new BoardObject(neighbor, searchNodeTwin,
                                                          searchNodeTwin.moves + 1);
                    searchPQTwin.insert(newNode);
                }
            }

            searchNode = searchPQ.delMin();
            searchNodeTwin = searchPQTwin.delMin();

        }

        this.solveNode = searchNode;
        if (searchNode.board.isGoal()) this.isSolvable = true;
        if (searchNodeTwin.board.isGoal()) this.isSolvable = false;

    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return this.isSolvable;
    }

    // min number of moves to solve initial board; -1 for unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        else return solveNode.moves;
    }

    // sequence of boards in a shortest solution; null if  unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;

        BoardObject curNode = solveNode;
        Stack<Board> solution = new Stack<Board>();
        while (curNode != null) {
            solution.push(curNode.board);
            curNode = curNode.previous;
        }
        return solution;
    }

    private static class MOrder implements Comparator<BoardObject> {
        // override comparator, compare two board by their priority
        public int compare(BoardObject x, BoardObject y) {
            return Integer.compare(x.manPriority - y.manPriority, 0);
        }
    }

    private class BoardObject {
        // Node in the Game tree, iterable
        private final Board board;
        private final BoardObject previous;
        private final int moves;
        private final int manPriority;

        public BoardObject(Board board, BoardObject previous, int moves) {
            this.board = board;
            this.previous = previous;
            this.moves = moves;
            this.manPriority = board.manhattan() + moves;
        }

    }


    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        // int[][] tiles = { { 1, 6, 4 }, { 0, 3, 5 }, { 8, 2, 7 } };
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);
        StdOut.println(solver.moves());

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        }
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);

        }

    }
}

/* *****************************************************************************
 *  Name:              Z.H Yu
 *  Coursera User ID:  zhyu
 *  Last modified:     9/24/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private final double[] perThreshold;
    private final int t;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("index is outside prescribed range.");
        }

        t = trials;
        int ranNum, row, col;
        perThreshold = new double[trials];

        for (
                int i = 0;
                i < trials; i++) {
            Percolation per = new Percolation(n);
            while (!per.percolates()) {
                ranNum = StdRandom.uniform(0, (n * n));
                row = ranNum / n + 1;
                col = ranNum % n + 1;
                if (!per.isOpen(row, col)) {
                    per.open(row, col);
                }
            }
            perThreshold[i] = (double) per.numberOfOpenSites() / (n * n);
        }

    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(perThreshold);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(perThreshold);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double conLo = mean() - CONFIDENCE_95 * stddev() / (Math.sqrt(t));
        return conLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double conHi = mean() + CONFIDENCE_95 * stddev() / (Math.sqrt(t));
        return conHi;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        Stopwatch timer = new Stopwatch();
        PercolationStats test = new PercolationStats(n, trials);

        double main = test.mean();
        double stddev = test.stddev();
        double confidenceLo = test.confidenceLo();
        double confidenceHi = test.confidenceHi();
        double sumTime = timer.elapsedTime();

        StdOut.printf("mean\t\t\t = %.10f\n", main);
        StdOut.printf("stddev\t\t\t = %.10f\n", stddev);
        StdOut.printf("95%% confidence interval  = [%.10f, %.10f]\n", confidenceLo, confidenceHi);
    }
}

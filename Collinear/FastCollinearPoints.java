/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    private int lineN = 0;
    private LineSegment[] lineSegments = new LineSegment[1];
    private Point[] endPoints = new Point[1];
    private double[] endSlopes = new double[1];


    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        int[] pointsIndex = new int[points.length];
        for (int i = 0; i < points.length; i++) pointsIndex[i] = i;

        for (int i = 0; i < points.length; i++)
            for (int j = i; j > 0 && less(points[pointsIndex[j]], points[pointsIndex[j - 1]]);
                 j--) {
                exch(pointsIndex, j, j - 1);
            }

        for (int i = 0; i < points.length - 1; i++) {
            if (points[pointsIndex[i]].compareTo(points[pointsIndex[i + 1]]) == 0)
                throw new NullPointerException("Duplicate points!");
        }

        for (int p = 1; p < points.length - 2; p++) {
            Point oriPoint = points[pointsIndex[p - 1]];
            int[] index = new int[points.length - p];
            double[] slopes = new double[index.length];

            for (int i = 0; i < index.length; i++) {
                index[i] = i;
                slopes[i] = oriPoint.slopeTo(points[pointsIndex[i + p]]);
            }

            for (int i = 1; i < slopes.length; i++) {
                for (int j = i;
                     j > 0 && less(slopes[index[j]], slopes[index[j - 1]]);
                     j--) {
                    exch(index, j, j - 1);
                }
            }

            int collinearCount = 2;
            for (int i = 0; i < index.length - 1; i++) {
                if (slopes[index[i]] == slopes[index[i + 1]]) {
                    collinearCount++;
                }
                else {
                    if (collinearCount >= 4) {
                        add(oriPoint, points[pointsIndex[index[i] + p]]);
                    }
                    collinearCount = 2;
                }
            }
            if (collinearCount >= 4) {
                add(oriPoint, points[pointsIndex[index[index.length - 1] + p]]);
            }

        }

    }

    private static boolean less(Point v, Point w) {
        return v.compareTo(w) < 0;
    }

    private static boolean less(double x, double y) {
        return x < y;
    }

    private static void exch(int[] a, int i, int j) {
        int p = a[i];
        a[i] = a[j];
        a[j] = p;
    }

    private void add(Point p, Point q) {
        if (check(q, p.slopeTo(q))) {
            LineSegment newline = new LineSegment(p, q);
            if (lineN == lineSegments.length) {
                LineSegment[] aLine = new LineSegment[lineN * 2];
                Point[] bPoint = new Point[lineN * 2];
                double[] cSlope = new double[lineN * 2];

                for (int i = 0; i < lineN; i++) {
                    aLine[i] = lineSegments[i];
                    bPoint[i] = endPoints[i];
                    cSlope[i] = endSlopes[i];
                }
                lineSegments = aLine;
                endPoints = bPoint;
                endSlopes = cSlope;
            }
            endPoints[lineN] = q;
            endSlopes[lineN] = p.slopeTo(q);
            lineSegments[lineN++] = newline;

        }

    }

    // the number of line segments
    public int numberOfSegments() {
        return lineN;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] a = new LineSegment[lineN];
        for (int i = 0; i < lineN; i++) {
            a[i] = lineSegments[i];
        }
        return a;
    }

    private boolean check(Point p, double slope) {
        for (int i = 0; i < lineN; i++) {
            if (p.equals(endPoints[i]) && slope == endSlopes[i])
                return false;
        }
        return true;

    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        if (n == 0) throw new IllegalArgumentException("argument is 0");
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            String str1 = in.readString();
            if (str1.equals("null")) {
                throw new NullPointerException("argument is null");
            }
            String str2 = in.readString();

            int x = Integer.parseInt(str1);
            int y = Integer.parseInt(str2);

            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        // StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.println(collinear.numberOfSegments());
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        // StdDraw.show();

    }
}

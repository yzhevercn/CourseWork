/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {

    private int lineN = 0;
    private LineSegment[] lineSegments = new LineSegment[1];

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        Point p, q, r, s;
        double pqSlope, qrSlope, rsSlope;
        int[] pointsIndex = new int[points.length];
        for (int i = 0; i < points.length; i++) pointsIndex[i] = i;

        for (int i = 0; i < points.length; i++)
            for (int j = i; j > 0 && less(points[pointsIndex[j]], points[pointsIndex[j - 1]]);
                 j--) {
                exch(pointsIndex, j, j - 1);
            }

        for (int i = 0; i < points.length - 1; i++) {
            if (points[pointsIndex[i]].compareTo(points[pointsIndex[i + 1]]) == 0) {
                throw new IllegalArgumentException("Duplicate points!");
            }
        }

        for (int i = 0; i < points.length - 3; i++) {
            p = points[pointsIndex[i]];
            for (int j = i + 1; j < points.length - 2; j++) {
                q = points[pointsIndex[j]];
                pqSlope = p.slopeTo(q);
                for (int k = j + 1; k < points.length - 1; k++) {
                    r = points[pointsIndex[k]];
                    qrSlope = q.slopeTo(r);
                    if (qrSlope == pqSlope) {
                        for (int h = k + 1; h < points.length; h++) {
                            s = points[pointsIndex[h]];
                            rsSlope = r.slopeTo(s);
                            if (rsSlope == qrSlope) {
                                add(p, s);
                            }
                        }
                    }
                }
            }
        }
    }

    private void add(Point p, Point q) {
        LineSegment newline = new LineSegment(p, q);
        if (lineN == lineSegments.length) {
            LineSegment[] a = new LineSegment[lineN * 2];
            for (int i = 0; i < lineN; i++) a[i] = lineSegments[i];
            lineSegments = a;
        }

        lineSegments[lineN++] = newline;

    }

    private static boolean less(Point v, Point w) {
        return v.compareTo(w) < 0;
    }

    private static void exch(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    // the number of line segments
    public int numberOfSegments() {
        return lineN;
    }

    // // the line segments
    public LineSegment[] segments() {
        LineSegment[] a = new LineSegment[lineN];
        for (int i = 0; i < lineN; i++) a[i] = lineSegments[i];
        return a;
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

        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        // StdDraw.show();

        BruteCollinearPoints bcp = new BruteCollinearPoints(points);
        StdOut.println(bcp.numberOfSegments());
        for (LineSegment segment : bcp.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        // StdDraw.show();
    }
}

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.TreeSet;

public class PointSET {
    // Brute method , o(N). Nothing needs to say.
    private final TreeSet<Point2D> pointsTree;

    // construct an empty set of points
    public PointSET() {
        pointsTree = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointsTree.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointsTree.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Inserting a NULL point!");
        pointsTree.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Searching a NULL point!");
        return pointsTree.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : pointsTree) {
            StdDraw.point(p.x(), p.y());
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Searching point of a NULL rect!");
        Stack<Point2D> insidePoints = new Stack<Point2D>();
        for (Point2D p : pointsTree) {
            if (rect.contains(p)) insidePoints.push(p);
        }
        return insidePoints;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Searching a NULL point!");
        if (this.isEmpty()) return null;
        Point2D neatestPoint = pointsTree.first();
        for (Point2D q : pointsTree) {
            if (q.distanceSquaredTo(p) < neatestPoint.distanceSquaredTo(p)) neatestPoint = q;
        }
        return neatestPoint;
    }

    public static void main(String[] args) {
        PointSET ps = new PointSET();
        In in = new In(args[0]);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            ps.insert(p);
        }
        ps.draw();
        StdOut.println("Size of set: " + ps.size());

    }
}

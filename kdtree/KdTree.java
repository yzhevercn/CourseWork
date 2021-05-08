/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class KdTree {
    private int n;
    private Node root;

    private static class Node {
        private final Point2D p;      // the point
        private final RectHV rect;
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    // construct an empty set of points
    public KdTree() {
        // root = new Node();
        n = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.n == 0;
    }

    // number of points in the set
    public int size() {
        return this.n;
    }

    //  add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Inserting a null point!");
        if (!contains(p)) {
            // root.rect is Rect(0,0,1,1), namely the whole space
            root = insert(root, p, true, 0.0, 0.0, 1.0, 1.0);
            this.n++;
        }
    }

    private Node insert(Node node, Point2D p, boolean isVertical, double x0, double y0, double x1,
                        double y1) {
        if (node == null) return new Node(p, new RectHV(x0, y0, x1, y1));

        // choose direction of child node : left-bottom or right-top
        double val0 = isVertical ? p.x() : p.y();
        double val1 = isVertical ? node.p.x() : node.p.y();
        if (val0 < val1)
            // the rect of node and node.lb share the same (xmin, ymin)
            node.lb = insert(node.lb, p, !isVertical, x0, y0,
                             isVertical ? node.p.x() : node.rect.xmax(),
                             isVertical ? node.rect.ymax() : node.p.y());
        else
            // the rect of node and node.lb share the same (xmax, ymax)
            node.rt = insert(node.rt, p, !isVertical, isVertical ? node.p.x() : node.rect.xmin(),
                             isVertical ? node.rect.ymin() : node.p.y(),
                             x1, y1);

        return node;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Searching a null point!");
        return contains(root, p, false);
    }

    private boolean contains(Node node, Point2D p, boolean isVertical) {
        // normally traverse the binary tree
        // notice: isVertical needs to reverse once contain() is called - it's a 2d-tree.
        if (node == null) return false;
        if (p.equals(node.p)) return true;
        isVertical = !isVertical;

        double val0 = isVertical ? p.x() : p.y();
        double val1 = isVertical ? node.p.x() : node.p.y();
        if (val0 < val1) return contains(node.lb, p, isVertical);
        else return contains(node.rt, p, isVertical);

    }

    // draw all points to standard draw
    public void draw() {
        draw(root, false);
    }

    private void draw(Node node, boolean isVertical) {

        if (node == null) return;
        isVertical = !isVertical;

        if (isVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius(0.01);
            StdDraw.point(node.p.x(), node.p.y());
            StdDraw.setPenRadius();
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius(0.01);
            StdDraw.point(node.p.x(), node.p.y());
            StdDraw.setPenRadius();
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }

        draw(node.lb, isVertical);
        draw(node.rt, isVertical);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Searching a null rect!");
        LinkedList<Point2D> points = new LinkedList<Point2D>();
        range(root, rect, true, points);

        return points;
    }

    private void range(Node node, RectHV rect, boolean isVertical, LinkedList<Point2D> points) {

        // check only whether the query rectangle intersects the splitting line segment:
        // if it does, then recursively search both subtrees;
        // otherwise, recursively search the one subtree where points intersecting the query rectangle could be.
        if (node == null) return;
        if (rect.contains(node.p)) points.add(node.p);

        double nodeVal = isVertical ? node.p.x() : node.p.y();
        double rectMin = isVertical ? rect.xmin() : rect.ymin();
        double rectMax = isVertical ? rect.xmax() : rect.ymax();
        if (rectMin < nodeVal) range(node.lb, rect, !isVertical, points);
        if (rectMax >= nodeVal) range(node.rt, rect, !isVertical, points);

    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Searching a NULL point!");
        if (isEmpty()) return null;

        return nearest(p, root, true, root.p);
    }

    private Point2D nearest(Point2D p, Node node, boolean isVertical, Point2D nearestPoint) {

        if (node == null) return nearestPoint;

        double val0 = isVertical ? p.x() : p.y();
        double val1 = isVertical ? node.p.x() : node.p.y();

        // next is the child node which leads to leaf
        // other, obviously, is the other child node
        Node next = (val0 < val1) ? node.lb : node.rt;
        Node other = (val0 < val1) ? node.rt : node.lb;

        double curDist = p.distanceSquaredTo(node.p);
        if (curDist < nearestPoint.distanceSquaredTo(p))
            nearestPoint = node.p;
        // consider the best node which lead to leaf as the local optimum result
        nearestPoint = nearest(p, next, !isVertical, nearestPoint);
        if (nearestPoint == null) nearestPoint = node.p;

        // if the closest point discovered so far is closer than the distance between the query point and the rectangle corresponding to a node,
        // then there is no need to explore that node (or its subtrees)
        if ((other != null) && (other.rect.distanceSquaredTo(p) < nearestPoint
                .distanceSquaredTo(p)))
            nearestPoint = nearest(p, other, !isVertical, nearestPoint);

        return nearestPoint;
    }

    public static void main(String[] args) {
        KdTree kt = new KdTree();
        In in = new In(args[0]);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kt.insert(p);
        }

        Point2D tmp = new Point2D(0.84, 0.56);
        StdOut.println(kt.nearest(tmp));
        kt.draw();
        StdDraw.point(tmp.x(), tmp.y());
    }
}

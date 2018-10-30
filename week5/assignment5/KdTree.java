import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.LinkedList;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private static final double XMIN = 0;
    private static final double YMIN = 0;
    private static final double XMAX = 1;
    private static final double YMAX = 1;

    private Node root;

    public KdTree() { // construct an empty set of points
    }

    private class Node {
        private Point2D point;
        private int N;
        private Node lt;
        private Node gte;
        private int depth;

        private double xmin;
        private double ymin;
        private double xmax;
        private double ymax;

        public Node(Point2D point, int depth, double xmin, double ymin, double xmax, double ymax) {
            this.point = point;
            this.N = 1;
            this.depth = depth;
            this.xmin = xmin;
            this.ymin = ymin;
            this.xmax = xmax;
            this.ymax = ymax;
        }
    }

    public boolean isEmpty() { // is the set empty?
        return size() == 0;
    }

    public int size() { // number of points in the set
        return size(root);
    }

    private int size(Node n) {
        return n != null ? n.N : 0;
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        if (p == null)
            throw new IllegalArgumentException();

        if (root == null)
            root = new Node(p, 0, XMIN, YMIN, XMAX, YMAX);
        else
            root = insert(p, root);
    }

    private Node insert(Point2D p, Node n) {
        if (p.equals(n.point))
            return n;
        double cmp = n.depth % 2 == 0 ? p.x() - n.point.x() : p.y() - n.point.y();
        if (cmp < 0) { // go lt
            if (n.lt != null) { // go down tree
                if (n.depth % 2 == 0)
                    assert n.lt.point.x() < n.point.x();
                else
                    assert n.lt.point.y() < n.point.y();
                n.lt = insert(p, n.lt);
            } else { // crete new node; end recursion
                if (n.depth % 2 == 0)
                    n.lt = new Node(p, n.depth + 1, n.xmin, n.ymin, n.point.x(), n.ymax);
                else
                    n.lt = new Node(p, n.depth + 1, n.xmin, n.ymin, n.xmax, n.point.y());
            }

        } else if (cmp >= 0) { // go gte
            if (n.gte != null) { // go down tree
                if (n.depth % 2 == 0)
                    assert n.gte.point.x() >= n.point.x();
                else
                    assert n.gte.point.y() >= n.point.y();
                n.gte = insert(p, n.gte);
            } else { // crete new node; end recursion
                if (n.depth % 2 == 0)
                    n.gte = new Node(p, n.depth + 1, n.point.x(), n.ymin, n.xmax, n.ymax);
                else
                    n.gte = new Node(p, n.depth + 1, n.xmin, n.point.y(), n.xmax, n.ymax);
            }
        }

        n.N = 1 + size(n.lt) + size(n.gte);

        return n;
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        if (p == null)
            throw new IllegalArgumentException();

        Node n = root;
        while (n != null) {
            if (p.equals(n.point))
                return true;

            double cmp = n.depth % 2 == 0 ? p.x() - n.point.x() : p.y() - n.point.y();
            if (cmp < 0)
                n = n.lt;
            else if (cmp >= 0)
                n = n.gte;
        }
        return false;
    }

    public void draw() { // draw all points to standard draw
        LinkedList<Node> toDraw = new LinkedList<>();
        toDraw.add(root);
        while (!toDraw.isEmpty()) {
            Node n = toDraw.remove();
            if (n == null)
                continue;

            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.001);
            n.point.draw();
            // StdDraw.setPenRadius(0.002);
            // if (n.depth % 2 == 0) {
            // StdDraw.setPenColor(StdDraw.RED);
            // StdDraw.line(n.point.x(), n.ymin, n.point.x(), n.ymax);
            // } else {
            // StdDraw.setPenColor(StdDraw.BLUE);
            // StdDraw.line(n.xmin, n.point.y(), n.xmax, n.point.y());
            // }
            // StdOut.printf("size: %d depth:%d\n", size(), n.depth);

            toDraw.add(n.lt);
            toDraw.add(n.gte);
        }
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        if (rect == null)
            throw new IllegalArgumentException();

        return range(rect, root);

    }

    private LinkedList<Point2D> range(RectHV rect, Node n) {
        LinkedList<Point2D> points = new LinkedList<>();
        if (n == null)
            return points;

        if (rect.contains(n.point))
            points.add(n.point);
        if (n.lt != null) {
            RectHV ltRegion = new RectHV(n.lt.xmin, n.lt.ymin, n.lt.xmax, n.lt.ymax);
            if (rect.intersects(ltRegion))
                points.addAll(range(rect, n.lt));
        }
        if (n.gte != null) {
            RectHV gteRegion = new RectHV(n.gte.xmin, n.gte.ymin, n.gte.xmax, n.gte.ymax);
            if (rect.intersects(gteRegion))
                points.addAll(range(rect, n.gte));
        }
        return points;
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new IllegalArgumentException();

        if (root == null)
            return null;

        return nearest(p, root, Double.POSITIVE_INFINITY).point;
    }

    private PointAndDistance nearest(Point2D p, Node n, double bestDist) { // a nearest neighbor in the set to
                                                                           // point p; null if the set is empty

        Point2D bestPoint = null;

        // check if n's point is better than bestPoint
        double dist = p.distanceSquaredTo(n.point);
        if (dist < bestDist) {
            bestDist = dist;
            bestPoint = n.point;
        }

        // get children
        RectHV ltRegion = null;
        RectHV gteRegion = null;
        double ltDist = Double.POSITIVE_INFINITY;
        double gteDist = Double.POSITIVE_INFINITY;
        if (n.lt != null) {
            ltRegion = new RectHV(n.lt.xmin, n.lt.ymin, n.lt.xmax, n.lt.ymax);
            ltDist = ltRegion.distanceSquaredTo(p);
        }
        if (n.gte != null) {
            gteRegion = new RectHV(n.gte.xmin, n.gte.ymin, n.gte.xmax, n.gte.ymax);
            gteDist = gteRegion.distanceSquaredTo(p);
        }

        // order children
        Node first;
        Node second;
        double firstDist;
        double secondDist;
        if (ltDist < gteDist) {
            first = n.lt;
            second = n.gte;
            firstDist = ltDist;
            secondDist = gteDist;
        } else {
            first = n.gte;
            second = n.lt;
            firstDist = gteDist;
            secondDist = ltDist;
        }

        PointAndDistance challenger = null;
        // try first
        if (firstDist < bestDist) {
            challenger = nearest(p, first, bestDist);
            if (challenger.point != null) {
                bestDist = challenger.dist;
                bestPoint = challenger.point;
            }
        }

        // try second
        if (secondDist < bestDist) {
            challenger = nearest(p, second, bestDist);
            if (challenger.point != null) {
                bestDist = challenger.dist;
                bestPoint = challenger.point;
            }
        }

        return new PointAndDistance(bestPoint, bestDist);
    }

    private class PointAndDistance {
        public Point2D point;
        public double dist;

        public PointAndDistance(Point2D point, double dist) {
            this.point = point;
            this.dist = dist;
        }
    }

    private Point2D nearest_by_pq(Point2D p) { // a nearest neighbor in the set to point p; null if the set is
        // empty
        if (p == null)
            throw new IllegalArgumentException();

        Point2D bestPoint = null;
        double bestDist = Double.POSITIVE_INFINITY;
        if (root == null)
            return bestPoint;

        // DistComparator comparator = new DistComparator(p);
        LinkedList<Node> toExplore = new LinkedList<Node>();
        // PriorityQueue<Node> toExplore = new PriorityQueue<Node>(1, comparator);
        toExplore.add(root);
        while (!toExplore.isEmpty()) {
            Node n = toExplore.remove();

            RectHV searchRegion = new RectHV(n.xmin, n.ymin, n.xmax, n.ymax);
            if (searchRegion.distanceSquaredTo(p) >= bestDist)
                continue;

            // check if n's point is better than bestPoint
            double dist = p.distanceSquaredTo(n.point);
            if (dist < bestDist) {
                bestDist = dist;
                bestPoint = n.point;
                if (p.equals(n.point))
                    break;
            }

            // add regions to toExplore
            if (n.lt != null) {
                RectHV ltRegion = new RectHV(n.lt.xmin, n.lt.ymin, n.lt.xmax, n.lt.ymax);
                toExplore.add(n.lt);
            }

            if (n.gte != null) {
                RectHV gteRegion = new RectHV(n.gte.xmin, n.gte.ymin, n.gte.xmax, n.gte.ymax);
                toExplore.add(n.gte);
            }

        }

        return bestPoint;
    }

    private class DistComparator implements Comparator<Node> {
        Point2D p;

        public DistComparator(Point2D p) {
            this.p = p;
        }

        public int compare(Node n, Node m) {
            double diff = p.distanceSquaredTo(n.point) - p.distanceSquaredTo(m.point);
            if (diff < 0)
                return -1;
            else if (diff == 0)
                return 0;
            else if (diff > 0)
                return 1;
            else
                throw new UnknownError(String.format("Bad difference: %s", diff));
        }

        public boolean equals(Object n, Object m) {
            return compare((Node) n, (Node) m) == 0;
        }
    }

    public static void main(String[] args) {// unit testing of the methods (optional)
    }
}

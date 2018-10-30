import java.util.LinkedList;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {
    private TreeSet<Point2D> points;

    public PointSET() { // construct an empty set of points
        points = new TreeSet<>();
    }

    public boolean isEmpty() { // is the set empty?
        return points.isEmpty();
    }

    public int size() { // number of points in the set
        return points.size();
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        points.add(p);
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        return points.contains(p);
    }

    public void draw() { // draw all points to standard draw«»
        for (Point2D p : points)
            p.draw();
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        LinkedList<Point2D> inRange = new LinkedList<>();
        for (Point2D p : points)
            if (rect.contains(p))
                inRange.add(p);
        return inRange;
    }

    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty
        Point2D bestPoint = null;
        double bestDistanceSquared = Double.POSITIVE_INFINITY;
        for (Point2D q : points) {
            double dist = p.distanceSquaredTo(q);
            if (dist < bestDistanceSquared) {
                bestDistanceSquared = dist;
                bestPoint = q;
            }
        }
        return bestPoint;
    }

    public static void main(String[] args) {// unit testing of the methods (optional)
    }
}

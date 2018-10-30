
/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Comparator;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Point implements Comparable<Point> {

    private final int x; // x-coordinate of this point
    private final int y; // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param x the <em>x</em>-coordinate of the point
     * @param y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point to standard
     * draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point. Formally, if
     * the two points are (x0, y0) and (x1, y1), then the slope is (y1 - y0) / (x1 -
     * x0). For completeness, the slope is defined to be +0.0 if the line segment
     * connecting the two points is horizontal; Double.POSITIVE_INFINITY if the line
     * segment is vertical; and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1)
     * are equal.
     *
     * @param that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        if (this.x == that.x && this.y == that.y) // same point
            return Double.NEGATIVE_INFINITY;
        else if (this.x == that.x) // vertical
            return Double.POSITIVE_INFINITY;
        else if (this.y == that.y) // horizontal
            return +0.0;
        else // general case

        // ensure positive zero
        if (that.x > this.x)
            return (that.y - this.y) / (1.0 * (that.x - this.x));
        else
            return (this.y - that.y) / (1.0 * (this.x - that.x));
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate. Formally,
     * the invoking point (x0, y0) is less than the argument point (x1, y1) if and
     * only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument point (x0
     *         = x1 and y0 = y1); a negative integer if this point is less than the
     *         argument point; and a positive integer if this point is greater than
     *         the argument point
     */
    public int compareTo(Point that) {
        if (this.y == that.y)
            return this.x - that.x;
        else
            return this.y - that.y;
    }

    /**
     * Compares two points by the slope they make with this point. The slope is
     * defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new SlopeOrder();
    }

    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            double m1 = slopeTo(p1);
            double m2 = slopeTo(p2);
            if (m1 < m2)
                return -1;
            else if (m1 == m2)
                return 0;
            else if (m1 > m2)
                return 1;
            else
                throw new RuntimeException(String.format("Unknown case: m1 = %f m2 = %f", slopeTo(p1), slopeTo(p2)));
        }
    }

    /**
     * Returns a string representation of this point. This method is provide for
     * debugging; your program should not rely on the format of the string
     * representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    private static void testPoint() {
        Point p1;
        Point p2;
        Point p3;
        Point p4;
        Point p5;
        Point p6;
        Point p7;
        Point p8;
        Point p9;
        Point p10;
        Point p11;
        Point p12;
        Point p13;

        // slopeTo
        p1 = new Point(3, 4);
        p2 = new Point(3, 4);
        assert p1.slopeTo(p1) == Double.NEGATIVE_INFINITY;
        assert p1.slopeTo(p2) == Double.NEGATIVE_INFINITY;
        p2 = new Point(3, -1);
        assert p1.slopeTo(p2) == Double.POSITIVE_INFINITY;
        p2 = new Point(-1, 4);
        assert p1.slopeTo(p2) == +0.0;
        p2 = new Point(1, -2);
        assert p1.slopeTo(p2) == 3.0;
        StdOut.println("slopeTo: PASSED");

        // compareTo
        p1 = new Point(3, 4);
        p2 = new Point(3, 4);
        assert p1.compareTo(p2) == 0;
        p2 = new Point(-1, 4);
        assert p1.compareTo(p2) > 0;
        p2 = new Point(4, 2);
        assert p1.compareTo(p2) > 0;
        p2 = new Point(4, 5);
        assert p1.compareTo(p2) < 0;
        p1 = new Point(0, 0);
        p2 = new Point(0, 1);
        p3 = new Point(0, 1);
        p4 = new Point(1, 1);
        p5 = new Point(1, 2);
        Point[] pts_nat_shuffled = { p4, p2, p1, p5, p3 };
        Point[] pts_nat_ordered = { p1, p2, p3, p4, p5 };
        Arrays.sort(pts_nat_shuffled);
        assert Arrays.equals(pts_nat_shuffled, pts_nat_ordered);
        StdOut.println("compareTo: PASSED");

        // slopeOrder
        p1 = new Point(0, 0); // -inf
        p2 = new Point(-1, 10); // -10
        p3 = new Point(2, -1); // -0.5
        p4 = new Point(-2, 1); // -0.5
        p5 = new Point(1, 0); // 0
        p6 = new Point(1, 0); // 0
        p7 = new Point(-1, 0); // 0
        p8 = new Point(2, 1); // 0.5
        p9 = new Point(2, 1); // 0.5
        p10 = new Point(-2, -1); // 0.5
        p11 = new Point(-1, -2); // 2
        p12 = new Point(0, 1); // +inf
        p13 = new Point(0, -1); // +inf
        Comparator<Point> compare_p1 = p1.slopeOrder();
        Comparator<Point> compare_p5 = p5.slopeOrder();
        assert compare_p1.compare(p1, p1) == 0;
        assert compare_p1.compare(p1, p2) < 0;
        assert compare_p1.compare(p3, p2) > 0;
        assert compare_p1.compare(p6, p7) == 0;
        assert compare_p1.compare(p1, p12) < 0;
        assert compare_p5.compare(p12, p13) < 0;
        assert p5.slopeOrder().compare(p2, p1) < 0;
        Point[] pts_slope_shuffled = { p5, p11, p6, p12, p7, p13, p8, p1, p9, p3, p10, p2, p4 };
        Point[] pts_slope_ordered = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 };
        Arrays.sort(pts_slope_shuffled, compare_p1);
        // for (Point p : pts_slope_shuffled)
        // StdOut.printf("%.2f\n", p1.slopeTo(p));
        assert Arrays.equals(pts_slope_shuffled, pts_slope_ordered);
        StdOut.println("slopeOrder: PASSED");

        StdOut.println("***ALL TESTS PASSED***");
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        testPoint();
    }
}

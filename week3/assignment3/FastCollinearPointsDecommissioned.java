import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;

public class FastCollinearPointsDecommissioned {

    private ArrayList<LineSegment> segs = new ArrayList<>();

    public FastCollinearPointsDecommissioned(Point[] points) // finds all line segments containing >=4 points
    {
        // argument validation
        if (points == null)
            throw new IllegalArgumentException("null argument");
        Point[] pts = Arrays.copyOf(points, points.length);
        for (int i = 0; i < pts.length; i++) {
            if (pts[i] == null)
                throw new IllegalArgumentException("null point");
        }
        Arrays.sort(pts);
        for (int i = 0; i < pts.length; i++) {
            if (i > 0 && pts[i].compareTo(pts[i - 1]) == 0)
                throw new IllegalArgumentException(String.format("duplicate point %", pts[i]));
        }

        // initialize stack
        Stack<Point> resizePoints = new Stack<>();
        for (Point point : pts) {
            resizePoints.push(point);
        }

        // hold popped points
        Stack<Point> usedPoints = new Stack<>();

        Point p;
        Point q;
        final int MIN_LEN = 4; // fewest number of points allowed in a segment
        assert MIN_LEN >= 3; // require that segments aren't trivial

        int passes = 0;
        while (resizePoints.size() > 0) {
            p = resizePoints.pop();
            Comparator<Point> comp = p.slopeOrder();
            resizePoints.sort(comp);
            boolean hasBigSegment = false;

            // look for groups with same slope wrt p
            int i = resizePoints.size() - 1;
            while (i + 1 >= MIN_LEN - 1) // MIN_LEN-1= #pts needed for a seg, i+1= longest seg still possible
            {
                passes++;

                int j = i - 1;
                assert j >= 0;
                assert j + 1 + 1 >= MIN_LEN - 1;
                q = resizePoints.get(i);
                while (j >= 0) {
                    if (comp.compare(q, resizePoints.get(j)) == 0) {
                        j--;
                    } else {
                        break;
                    }
                }
                assert j >= -1;
                j += 1;

                // j is now the last index of the currect slope value
                assert j >= 0;
                assert comp.compare(q, resizePoints.get(j)) == 0;
                assert j > 0 ? comp.compare(q, resizePoints.get(j - 1)) > 0 : true;

                // add segment if long enough
                if (i - j + 1 >= MIN_LEN - 1) {
                    // put collinear points in an array
                    Point[] col = new Point[i - j + 1 + 1];
                    col[0] = p;
                    assert col.length >= MIN_LEN;
                    for (int e = col.length - 1; e >= 1; e--) {
                        assert j + e - 1 <= i;
                        assert j + e - 1 >= j;
                        col[e] = resizePoints.get(j + e - 1);
                    }

                    // exclude subsegments
                    boolean noEarlierPointIsCollinear = true;
                    for (Point used : usedPoints) {
                        assert p.slopeTo(col[col.length - 1]) == p.slopeTo(col[col.length - 2]);
                        if (comp.compare(col[col.length - 1], used) == 0) {
                            noEarlierPointIsCollinear = false;
                            break;
                        }
                    }

                    if (noEarlierPointIsCollinear) {
                        hasBigSegment |= col.length > MIN_LEN;
                        // natural sort collinear array and form seg from extremes
                        Arrays.sort(col);
                        segs.add(new LineSegment(col[0], col[col.length - 1]));
                        // StdOut.print(String.format("%d\n", col.length));
                    }
                }

                // move i to the first index of the next slope value
                i = j - 1;
            }
            if (hasBigSegment)
                usedPoints.push(p);
        }
        StdOut.println(passes);
    }

    public int numberOfSegments() // the number of line segments
    {
        return segs.size();
    }

    public LineSegment[] segments() // the line segments
    {
        return segs.toArray(new LineSegment[0]);

    }

    public static void main(String[] args) // client
    {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        int border = 1000;
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(-border, 32768 + border);
        StdDraw.setYscale(-border, 32768 + border);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPointsDecommissioned collinear = new FastCollinearPointsDecommissioned(points);
        for (LineSegment segment : collinear.segments()) {
            // StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
        StdOut.printf("segs: %d\n", collinear.numberOfSegments());
        StdOut.println("Done.");
    }

}

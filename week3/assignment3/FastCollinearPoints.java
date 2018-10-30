import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;

public class FastCollinearPoints {

    private ArrayList<LineSegment> segs = new ArrayList<>();

    public FastCollinearPoints(Point[] points) // finds all line segments containing >=4 points
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

        final int MIN_LEN = 4; // fewest number of points allowed in a segment
        assert MIN_LEN >= 3; // require that segments aren't trivial

        Stack<Point> haveBigSegments = new Stack<>(); // keep track of the few points that are part of segments bigger
                                                      // than MIN_LEN

        int passes = 0;
        for (int h = 0; h <= pts.length - MIN_LEN; h++) {
            Comparator<Point> comp = pts[h].slopeOrder();
            Arrays.sort(pts, h + 1, pts.length, comp);
            boolean hasBigSegment = false;

            // look for groups with same slope wrt p
            // then, make a line segment from the group if big enough/not redundant
            int i = h + 1;
            while (i <= pts.length - (MIN_LEN - 1)) {
                assert pts.length - i + 1 >= MIN_LEN;
                passes++;
                // find boundary of current slope-group
                int j = i + 1;
                while (j < pts.length) {
                    if (comp.compare(pts[i], pts[j]) != 0) {
                        break;
                    }
                    j++;

                }

                // j is now the first index of the next slope value, or j=pts.length
                assert j <= pts.length;
                assert j < pts.length ? comp.compare(pts[i], pts[j]) < 0 : true;
                assert comp.compare(pts[i], pts[j - 1]) == 0;

                // if this same-slope group is big enough, make a line segment
                if (j - i + 1 >= MIN_LEN) {
                    hasBigSegment |= j - i + 1 > MIN_LEN;
                    // check if this is a subsegment of an earlier segment
                    boolean noEarlierPointIsCollinear = true;
                    for (Point hasBig : haveBigSegments) {
                        noEarlierPointIsCollinear &= !(comp.compare(hasBig, pts[i]) == 0);
                        assert (comp.compare(hasBig, pts[i]) == 0) == (comp.compare(hasBig, pts[j - 1]) == 0);
                    }

                    // now make the segment, if it was not found to be a subsegment
                    if (noEarlierPointIsCollinear) {
                        // find the points at the extremes of the line
                        Arrays.sort(pts, i, j);
                        Point min = pts[h].compareTo(pts[i]) < 0 ? pts[h] : pts[i];
                        Point max = pts[h].compareTo(pts[j - 1]) > 0 ? pts[h] : pts[j - 1];
                        segs.add(new LineSegment(min, max));
                        // StdOut.print(j - i + 1 > MIN_LEN ?String.format("%d\n", j - i + 1): "");

                        // Point[] col = new Point[j - i + 1];
                        // for (int k = 0; k < j - i; k++) {
                        // col[k] = pts[i + k];
                        // }
                        // col[col.length - 1] = pts[h];
                        // Arrays.sort(col);
                        // segs.add(new LineSegment(col[0], col[col.length - 1]));
                    }
                }

                assert j - i >= 1;
                assert pts[h].slopeTo(pts[i]) == pts[h].slopeTo(pts[j - 1]); // the boundaries I'm using have the same
                                                                             // slope wrt h
                assert j < pts.length ? pts[h].slopeTo(pts[i]) < pts[h].slopeTo(pts[j]) : true; // beyond the boundary
                                                                                                // the
                                                                                                // slope is bigger
                assert pts[i - 1] != pts[h] ? pts[h].slopeTo(pts[i]) > pts[h].slopeTo(pts[i - 1]) : true; // before the
                                                                                                          // boundary
                                                                                                          // the slope
                                                                                                          // is smaller
                i = j; // move i to beginning of next slope-group
            }

            if (hasBigSegment)
                haveBigSegments.add(pts[h]);
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            // StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
        StdOut.printf("segs: %d\n", collinear.numberOfSegments());
        StdOut.println("Done.");
    }

}

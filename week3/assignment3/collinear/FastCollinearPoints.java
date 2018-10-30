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

        for (int h = 0; h <= pts.length - MIN_LEN; h++) {
            Comparator<Point> comp = pts[h].slopeOrder();
            Arrays.sort(pts, h + 1, pts.length, comp);
            boolean hasBigSegment = false;

            // look for groups with same slope wrt p
            // then, make a line segment from the group if big enough/not redundant
            int i = h + 1;
            while (i <= pts.length - (MIN_LEN - 1)) {
                // find boundary of current slope-group
                int j = i + 1;
                while (j < pts.length) {
                    if (comp.compare(pts[i], pts[j]) != 0)
                        break;
                    j++;
                }
                // j is now the first index of the next slope value, or j=pts.length

                // if this same-slope group is big enough, make a line segment
                if (j - i + 1 >= MIN_LEN) {
                    hasBigSegment |= j - i + 1 > MIN_LEN;
                    // check if this is a subsegment of an earlier segment
                    boolean noEarlierPointIsCollinear = true;
                    for (Point hasBig : haveBigSegments)
                        noEarlierPointIsCollinear &= !(comp.compare(hasBig, pts[i]) == 0);

                    // now make the segment, if it was not found to be a subsegment
                    if (noEarlierPointIsCollinear) {
                        // find the points at the extremes of the line
                        Arrays.sort(pts, i, j);
                        Point min = pts[h].compareTo(pts[i]) < 0 ? pts[h] : pts[i];
                        Point max = pts[h].compareTo(pts[j - 1]) > 0 ? pts[h] : pts[j - 1];
                        segs.add(new LineSegment(min, max));
                    }
                }

                // move i to beginning of next slope-group
                i = j;
            }

            if (hasBigSegment)
                haveBigSegments.add(pts[h]);
        }
    }

    public int numberOfSegments() // the number of line segments
    {
        return segs.size();
    }

    public LineSegment[] segments() // the line segments
    {
        return segs.toArray(new LineSegment[0]);
    }
}

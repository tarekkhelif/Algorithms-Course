import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private ArrayList<LineSegment> segs = new ArrayList<>();

    public BruteCollinearPoints(Point[] points) // finds all line segments containing 4 points
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

        Point p;
        Point q;
        Point r;
        Point s;

        // int groups = 0;
        // int lines = 0;
        for (int i = 0; i < pts.length; i++) {
            p = pts[i];
            for (int j = i + 1; j < pts.length; j++) {
                q = pts[j];
                for (int k = j + 1; k < pts.length; k++) {
                    r = pts[k];
                    for (int m = k + 1; m < pts.length; m++) {
                        s = pts[m];

                        if (collinear(p, q, r) && collinear(q, r, s)) {
                            Point[] onLine = { p, q, r, s };
                            Arrays.sort(onLine);
                            segs.add(new LineSegment(onLine[0], onLine[onLine.length - 1]));
                            // lines++;
                        }
                        // groups++;
                    }
                }
            }
        }
        // StdOut.printf("groups: %d\nlines: %d\n", groups, lines);

    }

    private boolean collinear(Point p1, Point p2, Point p3) // points collinear?
    {
        if (p1.slopeTo(p2) == Double.NEGATIVE_INFINITY || p2.slopeTo(p3) == Double.NEGATIVE_INFINITY)
            return true;
        else
            return p1.slopeTo(p2) == p2.slopeTo(p3);
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

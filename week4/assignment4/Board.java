import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final int[] blocks;
    private final int N;
    private boolean distCached;
    private int hammingDist;
    private int manhattanDist;
    private int zeroRow = -1;
    private int zeroCol = -1;

    public Board(int[][] blocks) { // construct a board from an n-by-n array of blocks (where blocks[i][j] = block
                                   // in row i, column j)
        validateArg(blocks);

        // copy data into 1d array
        N = blocks.length;
        this.blocks = new int[N * N];
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++) {
                this.blocks[i(r, c)] = blocks[r][c];
                if (blocks[r][c] == 0) {
                    zeroRow = r;
                    zeroCol = c;
                }
            }

        if (zeroRow == -1 || zeroCol == -1)
            throw new IllegalArgumentException("board must contain zero");
    }

    public int dimension() { // board dimension n
        return N;
    }

    private void calcDistances() { // loop through once to calculate both distances
        int hdist = 0;
        int mdist = 0;
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++) {
                int i = i(r, c);
                if (blocks[i] != 0) { // don't count the blank square
                    hdist += blocks[i] - 1 == i ? 0 : 1;
                    mdist += Math.abs(r(blocks[i] - 1) - r) + Math.abs(c(blocks[i] - 1) - c);
                }
            }
        this.hammingDist = hdist;
        this.manhattanDist = mdist;
        this.distCached = true;
    }

    public int hamming() { // number of blocks out of place
        if (!distCached)
            calcDistances();
        return hammingDist;
    }

    public int manhattan() { // sum of Manhattan distances between blocks and goal
        if (!distCached)
            calcDistances();
        return manhattanDist;
    }

    public boolean isGoal() { // is this board the goal board?
        return hamming() == 0;
    }

    public Board twin() { // a board that is obtained by exchanging any pair of blocks
        assert dimension() >= 2;
        int a = 0;
        int b = 0;
        while (blocks[i(0, a)] == 0) {
            a++;
        }
        while (blocks[i(1, b)] == 0) {
            b++;
        }

        int[][] newTwin = to2D(blocks);
        exchange(newTwin, 0, a, 1, b);
        return new Board(newTwin);
    }

    private int[][] to2D(int[] arr1D) {
        if (arr1D.length != N * N)
            throw new IllegalArgumentException("length 1D array must be N*N");

        int[][] arr2D = new int[N][N];
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++)
                arr2D[r][c] = arr1D[i(r, c)];
        return arr2D;
    }

    public boolean equals(Object y) { // does this board equal y?
        if (!(y instanceof Board))
            return false;
        return Arrays.equals(blocks, ((Board) y).blocks);
    }

    public Iterable<Board> neighbors() { // all neighboring boards
        return new Neighbors();
    }

    private class Neighbors implements Iterable<Board> {
        public Iterator<Board> iterator() {
            return new NeighborIterator();
        }
    }

    private class NeighborIterator implements Iterator<Board> {
        // above, left, below, right
        private final boolean[] neighborExistence = { zeroRow > 0, zeroCol > 0, zeroRow < N - 1, zeroCol < N - 1 };
        private int nextNeighbor;

        public NeighborIterator() {
            nextNeighbor = neighborAtOrAfter(0);
        }

        private int neighborAtOrAfter(int i) {
            while (i < neighborExistence.length && !neighborExistence[i])
                i++;
            return i;
        }

        public boolean hasNext() {
            return nextNeighbor < neighborExistence.length;
        }

        public Board next() {
            if (!hasNext())
                throw new NoSuchElementException();

            int[][] neighborBlocks2D = to2D(blocks);
            switch (nextNeighbor) {
            case 0: // above
                exchange(neighborBlocks2D, zeroRow, zeroCol, zeroRow - 1, zeroCol);
                break;
            case 1: // left
                exchange(neighborBlocks2D, zeroRow, zeroCol, zeroRow, zeroCol - 1);
                break;
            case 2: // below
                exchange(neighborBlocks2D, zeroRow, zeroCol, zeroRow + 1, zeroCol);
                break;
            case 3: // right
                exchange(neighborBlocks2D, zeroRow, zeroCol, zeroRow, zeroCol + 1);
                break;
            default:
                throw new UnknownError();
            }

            nextNeighbor = neighborAtOrAfter(nextNeighbor + 1);

            return new Board(neighborBlocks2D);

        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static void validateArg(int[][] arg) {
        if (arg == null)
            throw new IllegalArgumentException("Blocks cannot be null");
        for (int r = 0; r < arg.length; r++) {
            if (arg[r].length != arg.length)
                throw new IllegalArgumentException("blocks must be square");
        }
    }

    private int r(int i) { // convert 1d array index to 2d array row
        return i / N;
    }

    private int c(int i) { // convert 1d array index to 2d array col
        return i % N;
    }

    private int i(int r, int c) { // convert 2d array row, col to 1d array index
        return r * N + c;
    }

    private static void exchange(int[] arr, int a, int b) // trade places in 1D array
    {
        assert a < arr.length && b < arr.length;

        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    private static void exchange(int[][] arr, int r1, int c1, int r2, int c2) // trade places in 2D array
    {
        assert r1 < arr.length && r2 < arr.length;
        assert c1 < arr[r1].length && c2 < arr[r2].length;

        int temp = arr[r1][c1];
        arr[r1][c1] = arr[r2][c2];
        arr[r2][c2] = temp;

    }

    public String toString() { // string representation of this board (in the output format specified below)
        String output = String.valueOf(N);
        String w = String.valueOf((int) Math.log10(N * N - 1) + 1 + 2); // max # of digits + 2 spaces
        for (int r = 0; r < N; r++) {
            String rowString = "\n ";
            for (int c = 0; c < N; c++)
                rowString += String.format("%-" + w + "d", blocks[i(r, c)]);
            output += rowString;
        }

        return output;
    }

    private static void printBoardInfo(Board b, Board ref, String name) {
        StdOut.println();
        StdOut.println(name);
        StdOut.println("= input   : " + b.equals(ref));
        StdOut.println("isGoal    : " + b.isGoal());
        StdOut.println("dimension : " + b.dimension());
        StdOut.println("hamming   : " + b.hamming());
        StdOut.println("manhattan : " + b.manhattan());
        StdOut.println(b.toString());
    }

    public static void main(String[] args) { // unit tests (not graded)

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // test equality
        assert !initial.equals(initial.twin());
        assert initial.equals(initial.twin().twin());

        // Print board and neighbors
        StdOut.println("INPUT");
        printBoardInfo(initial, initial, "Initial ");
        printBoardInfo(initial.twin(), initial, "Twin ");

        StdOut.println("\n\nNEIGHBORS");
        Iterator<Board> NeighbsIter = initial.neighbors().iterator();
        int i = 0;
        while (NeighbsIter.hasNext()) {
            i++;
            Board b = NeighbsIter.next();
            printBoardInfo(b, initial, "Neighbor " + i);
            printBoardInfo(b.twin(), initial, "Twin " + i);
        }

    }
}

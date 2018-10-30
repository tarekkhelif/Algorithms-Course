
// import java.util.ArrayList;
import java.util.Arrays;
// import java.util.List;
// import java.util.HashSet;
import java.util.Comparator;
import java.util.Iterator;

import edu.princeton.cs.algs4.MinPQ;

public class Solver {
    private final MinPQ<Node> pq;
    private final MinPQ<Node> pq_twin;
    private final Node solution; // can be a solution to the initial node or its twin
    private final boolean solvable;

    public Solver(Board initial) { // find a solution to the initial board (using the A* algorithm)
        if (initial == null)
            throw new IllegalArgumentException("argument cannot be null");

        pq = new MinPQ<Node>(Node.BY_MANHATTAN); // choose manhattan priority
        pq_twin = new MinPQ<Node>(Node.BY_MANHATTAN); // choose manhattan priority

        pq.insert(new Node(initial, null));
        pq_twin.insert(new Node(initial.twin(), null));

        Node min;
        Node min_twin;
        while (true) {
            // find the new minima and check if either is the goal
            min = pq.delMin();
            min_twin = pq_twin.delMin();
            if (min.getBoard().isGoal()) {
                solution = min;
                solvable = true;
                break;
            } else if (min_twin.getBoard().isGoal()) {
                solution = min_twin;
                solvable = false;
                break;
            }

            // min has been removed and is not the goal. now, add its neighbors to the pq
            updatePQ(pq, min);
            updatePQ(pq_twin, min_twin);

        }
    }

    private static void updatePQ(MinPQ<Node> pq, Node min) {

        // HashSet<Board> neighbors = new HashSet<Board>();
        // for (Board neighbor : min.getBoard().neighbors())
        // neighbors.add(neighbor);

        // Node ancestor = min;
        // while (neighbors.size() > 0 && ancestor.predecessor != null) {
        // ancestor = ancestor.predecessor;
        // ArrayList<Board> toRemove = new ArrayList<>(neighbors.size());
        // for (Board neighbor : neighbors)
        // if (ancestor.getBoard().equals(neighbor))
        // toRemove.add(neighbor);
        // for (Board duplicate : toRemove) {
        // neighbors.remove(duplicate);
        // }

        // }

        // for (Board neighbor : neighbors)
        // pq.insert(new Node(neighbor, min));

        for (Board neighbor : min.getBoard().neighbors())
            if (!(min.predecessor != null && neighbor.equals(min.predecessor.getBoard())))
                pq.insert(new Node(neighbor, min));
    }

    private static class Node implements Iterable<Board> {
        private static final Comparator<Node> BY_HAMMING = new HammingComparator();
        private static final Comparator<Node> BY_MANHATTAN = new ManhattanComparator();

        private final Board board;
        private final Node predecessor;
        private final int numAncestors;
        private final int hammingPriority;
        private final int manhattanPriority;
        private Board[] descentPath;

        public Node(Board board, Node predecessor) {
            this.board = board;
            this.predecessor = predecessor;
            this.numAncestors = predecessor != null ? predecessor.getNumAncestors() + 1 : 0;
            this.hammingPriority = numAncestors + board.hamming();
            this.manhattanPriority = numAncestors + board.manhattan();
        }

        public Board getBoard() {
            return board;
        }

        public Node getPredecessor() {
            return predecessor;
        }

        public int getNumAncestors() {
            return numAncestors;
        }

        private static class HammingComparator implements Comparator<Node> {
            public int compare(Node a, Node b) {
                int comp = a.getHammingPriority() - b.getHammingPriority();
                if (comp == 0)
                    comp = a.getBoard().hamming() - b.getBoard().hamming();
                if (comp == 0)
                    comp = a.getManhattanPriority() - b.getManhattanPriority();
                if (comp == 0)
                    comp = a.getBoard().manhattan() - b.getBoard().manhattan();
                return comp != 0 ? comp : a.getBoard().hamming() - b.getBoard().hamming();
            }
        }

        private static class ManhattanComparator implements Comparator<Node> {
            public int compare(Node a, Node b) {
                int comp = a.getManhattanPriority() - b.getManhattanPriority();
                if (comp == 0)
                    comp = a.getBoard().manhattan() - b.getBoard().manhattan();
                if (comp == 0)
                    comp = a.getHammingPriority() - b.getHammingPriority();
                if (comp == 0)
                    comp = a.getBoard().hamming() - b.getBoard().hamming();
                return comp;
            }
        }

        public int getHammingPriority() {
            return hammingPriority;
        }

        public int getManhattanPriority() {
            return manhattanPriority;
        }

        public Iterator<Board> iterator() {
            if (descentPath == null) {
                descentPath = new Board[getNumAncestors() + 1];
                Node curr = Node.this;
                descentPath[descentPath.length - 1] = curr.board;
                for (int i = descentPath.length - 1 - 1; i >= 0; i--) {
                    curr = curr.predecessor;
                    descentPath[i] = curr.board;
                }
                assert curr.predecessor == null;
            }
            return Arrays.asList(descentPath).iterator();
        }

    }

    public boolean isSolvable() { // is the initial board solvable?
        return solvable;
    }

    public int moves() { // min number of moves to solve initial board; -1 if unsolvable
        return isSolvable() ? solution.getNumAncestors() : -1;
    }

    public Iterable<Board> solution() { // sequence of boards in a shortest solution; null if unsolvable
        return solvable ? solution : null;
    }

    public static void main(String[] args) { // solve a slider puzzle (given below)

    }
}

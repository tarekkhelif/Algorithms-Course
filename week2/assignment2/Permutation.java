import java.util.Iterator;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<>();

        int num = Integer.parseInt(args[0]);
        assert num >= 0;

        while (!StdIn.isEmpty()) {
            queue.enqueue(StdIn.readString());
        }
        assert queue.size() >= num;

        Iterator iter = queue.iterator();
        for (int i = 0; i < num; i++) {
            StdOut.println(iter.next());
        }

    }
}

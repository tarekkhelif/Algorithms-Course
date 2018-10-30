import java.util.Iterator;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] queue;
    private int size;

    public RandomizedQueue() // construct an empty randomized queue
    {
        queue = (Item[]) new Object[1];
    }

    public boolean isEmpty() // is the randomized queue empty?
    {
        return size() == 0;
    }

    public int size() // return the number of items on the randomized queue
    {
        return size;
    }

    public void enqueue(Item item) // add the item
    {
        if (item == null)
            throw new IllegalArgumentException();

        // copy to new array of twice the size if full
        if (size() == queue.length) {
            Item[] oldQueue = queue;
            queue = (Item[]) new Object[oldQueue.length * 2];
            for (int i = 0; i < oldQueue.length; i++) {
                queue[i] = oldQueue[i];
            }
        }

        queue[size()] = item;
        size++;
    }

    public Item dequeue() // remove and return a random item
    {
        if (isEmpty())
            throw new java.util.NoSuchElementException();

        // choose an element to remove, put the last element in its place
        int selection = StdRandom.uniform(size());
        int last = size() - 1;
        Item removed = queue[selection];
        queue[selection] = queue[last];
        queue[last] = null;

        // copy to new array of half the size if a quarter full
        if (size() == queue.length / 4) {
            Item[] oldQueue = queue;
            queue = (Item[]) new Object[oldQueue.length / 2];
            for (int i = 0; i < queue.length; i++) {
                queue[i] = oldQueue[i];
            }
        }

        size--;
        return removed;
    }

    public Item sample() // return a random item (but do not remove it)
    {
        if (isEmpty())
            throw new java.util.NoSuchElementException();

        return queue[StdRandom.uniform(size())];
    }

    public Iterator<Item> iterator() // return an independent iterator over items
    // in random order
    {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {

        private int[] order;
        private int used;

        public RandomizedQueueIterator() {
            order = new int[size()];
            for (int i = 0; i < order.length; i++)
                order[i] = i;
            used = 0;

        }

        public boolean hasNext() {
            return used < order.length;
        }

        public Item next() {
            if (!hasNext())
                throw new java.util.NoSuchElementException();

            int i = used + StdRandom.uniform(order.length - used);
            int selection = order[i];
            order[i] = order[used];
            order[used] = selection;
            used++;

            return queue[selection];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) // unit testing (optional)
    {
        RandomizedQueue<Integer> queue;
        int test1 = 1;
        int test2 = 2;

        queue = new RandomizedQueue<>();
        assert queue.isEmpty();
        assert queue.size() == 0;
        queue.enqueue(test1);
        assert !queue.isEmpty();
        assert queue.size() == 1;
        assert ((int) queue.dequeue()) == test1;
        assert queue.isEmpty();
        assert queue.size() == 0;

        queue.enqueue(test1);
        assert !queue.isEmpty();
        assert queue.size() == 1;
        queue.enqueue(test2);
        assert !queue.isEmpty();
        assert queue.size() == 2;

    }
}

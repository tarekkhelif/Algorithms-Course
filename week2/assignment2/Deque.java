import java.util.Iterator;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Deque<Item> implements Iterable<Item> {

    private class Node {
        public Item item;
        public Node next;
        public Node previous;

        public Node(Item item, Node next, Node previous) {
            this.item = item;
            this.next = next;
            this.previous = previous;
        }
    }

    private class DequeIterator implements Iterator<Item> {
        private Node curr; // the item the client most recently recieved

        public boolean hasNext() {
            return (curr == null && !isEmpty()) || (curr != null && curr.next != null);
        }

        public Item next() {
            if (!hasNext())
                throw new java.util.NoSuchElementException();

            curr = curr == null ? first : curr.next;
            return curr.item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private Node first;
    private Node last;
    private int size = 0;

    public Deque() // construct an empty deque
    {
        first = null;
        last = null;
    }

    public boolean isEmpty() // is the deque empty?
    {
        return size() == 0;
    }

    public int size() // return the number of items on the deque
    {
        return size;
    }

    public void addFirst(Item item) // add the item to the front
    {
        if (item == null)
            throw new IllegalArgumentException();

        first = new Node(item, first, null);
        if (isEmpty()) {
            assert last == null;
            assert first.next == null;
            assert first.previous == null;
            last = first;
        } else {
            first.next.previous = first;
        }
        size++;

    }

    public void addLast(Item item) // add the item to the end
    {
        if (item == null)
            throw new IllegalArgumentException();

        last = new Node(item, null, last);
        if (isEmpty()) {
            assert first == null;
            assert last.next == null;
            assert last.previous == null;
            first = last;
        } else {
            last.previous.next = last;
        }
        size++;

    }

    public Item removeFirst() // remove and return the item from the front
    {
        Item removed;

        switch (size()) {
        case 0:
            throw new java.util.NoSuchElementException();
        case 1:
            removed = first.item;
            first = null;
            last = null;
            size--;
            assert size() == 0;
            break;
        default:
            removed = first.item;
            first = first.next;
            first.previous = null;
            size--;
            assert size() >= 0;
            assert first != null;
            assert last != null;
            assert size() == 1 ? first == last : true;
            break;
        }

        return removed;
    }

    public Item removeLast() // remove and return the item from the end
    {
        Item removed;

        switch (size()) {
        case 0:
            throw new java.util.NoSuchElementException();
        case 1:
            removed = last.item;
            first = null;
            last = null;
            size--;
            assert size() == 0;
            break;
        default:
            assert size() >= 2;
            assert first != null;
            assert last != null;
            assert first != last;
            assert size() == 2 ? first.next == last : true;
            assert size() == 2 ? last.previous == first : true;
            assert first.previous == null;
            assert last.next == null;
            removed = last.item;
            last = last.previous;
            last.next = null;
            size--;
            assert size() >= 0;
            assert first != null;
            assert last != null;
            assert size() == 1 ? first == last : true;
            break;
        }

        return removed;
    }

    public Iterator<Item> iterator() // return an iterator over items in order from front to end
    {
        return new DequeIterator();
    }

    private static void runTests() // tests
    {

        int test1 = 4;
        int test2 = 7;
        Iterator iter;

        Deque<Integer> deque = new Deque<>();

        assert deque.isEmpty() == true;
        assert deque.size() == 0;
        iter = deque.iterator();
        assert iter.hasNext() == false;

        deque.addFirst(test1);
        assert deque.isEmpty() == false;
        assert deque.size() == 1;
        iter = deque.iterator();
        assert iter.hasNext() == true;
        assert ((int) iter.next()) == test1;

        assert deque.removeFirst() == test1;
        assert deque.isEmpty() == true;
        assert deque.size() == 0;
        iter = deque.iterator();
        assert iter.hasNext() == false;

        deque.addLast(test1);
        deque.addFirst(test2);
        assert deque.isEmpty() == false;
        assert deque.size() == 2;
        iter = deque.iterator();
        assert iter.hasNext() == true;
        assert ((int) iter.next()) == test2;
        assert iter.hasNext() == true;
        assert ((int) iter.next()) == test1;
        assert iter.hasNext() == false;

        assert deque.removeLast() == test1;
        assert deque.isEmpty() == false;
        assert deque.size() == 1;
        iter = deque.iterator();
        assert iter.hasNext() == true;
        assert ((int) iter.next()) == test2;
    }

    public static void main(String[] args) // unit testing (optional)
    {
        runTests();
    }
}

import random


class Node:

    def __init__(self, k, v):
        self.k = k
        self.v = v
        self.l = None
        self.r = None
        self.N = 1


class BinarySearchTree:
    root = None
    NOT_GIVEN = "NOT_GIVEN"

    def __init__(self, arr=None):
        if arr:
            for k, v in arr:
                self.root = self.put(k, v)

    def invariant(self, n):
        if n.l == None and n.r == None:
            return True
        elif n.l == None:
            assert n.r != None
            return n.k < n.r.k
        elif n.r == None:
            assert n.l != None
            return n.l.k < n.k
        else:
            return n.l.k < n.k < n.r.k

    def validateInvariant(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root

        if n == None:
            return True

        return self.invariant(n) and self.validateInvariant(n.l) and self.validateInvariant(n.r)

    def validateSize(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            _, allMatch = self.validateSize(self.root)
            return allMatch

        if n == None:
            return 0, True
        else:
            lcount, lmatches = self.validateSize(n.l)
            rcount, rmatches = self.validateSize(n.r)
            count = 1 + lcount + rcount
            matches = n.N == count
            return count, matches and lmatches and rmatches

    def manualSize(self, n):
        if n == None:
            return 0
        else:
            return 1 + self.manualSize(n.l) + self.manualSize(n.r)

    def put(self, k, v, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            self.root = self.put(k, v, self.root)
            return self.root
        if n == None:
            return Node(k, v)

        assert self.invariant(n)

        if k < n.k:
            n.l = self.put(k, v, n.l)
        elif k > n.k:
            n.r = self.put(k, v, n.r)
        else:  # k == n.k
            assert k == n.k
            n.v = v

        assert self.invariant(n)

        n.N = self.size(n.l) + self.size(n.r) + 1

        return n

    def min(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root

        if n == None:
            print("WARNING: min was called on an empty tree")
            return None

        assert self.invariant(n)

        if n.l == None:
            return n

        return self.min(n.l)

    def max(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root

        if n == None:
            print("WARNING: max was called on an empty tree")
            return None

        assert self.invariant(n)

        if n.r == None:
            return n

        return self.max(n.r)

    def deleteMin(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root

        if n == None:
            print("WARNING: deleteMin was called on an empty tree")
            return None

        assert self.invariant(n)

        if n.l == None:  # n is min
            return n.r

        n.l = self.deleteMin(n.l)
        if n.l != None:
            n.l.N = self.size(n.l.l) + self.size(n.l.r) + 1

        assert self.invariant(n)

        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    def deleteMax(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root

        if n == None:
            print("WARNING: deleteMax was called on an empty tree")
            return None

        assert self.invariant(n)

        if n.r == None:  # n is max
            return n.l

        n.r = self.deleteMax(n.r)
        if n.r != None:
            n.r.N = self.size(n.r.l) + self.size(n.r.r) + 1

        assert self.invariant(n)

        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    def _predecessor_delete(self, k, n):
        if n == None:
            return None

        assert self.invariant(n)

        if k < n.k:
            n.l = self._predecessor_delete(k, n.l)
        elif k > n.k:
            n.r = self._predecessor_delete(k, n.r)
        else:  # k == n.k: # replace with *prececessor*
            if n.r == None:
                return n.l
            if n.l == None:
                return n.r
            new_n = self.max(n.l)
            assert self.invariant(new_n)
            assert new_n.k < n.k  # is a prececessor
            assert new_n.r == None  # is a local max
            n.l = self.deleteMax(n.l)
            new_n.l = n.l
            new_n.r = n.r
            new_n.N = self.size(new_n.l) + self.size(new_n.r) + 1
            assert self.size(new_n) == self.size(n) - 1

            n = new_n
            assert self.invariant(n)
        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    def _successor_delete(self, k, n):
        if n == None:
            return None

        assert self.invariant(n)

        if k < n.k:
            n.l = self._successor_delete(k, n.l)
        elif k > n.k:
            n.r = self._successor_delete(k, n.r)
        else:  # k == n.k: # replace with *successor*
            if n.r == None:
                return n.l
            if n.l == None:
                return n.r
            new_n = self.min(n.r)
            assert self.invariant(new_n)
            assert new_n.k > n.k  # is a successor
            assert new_n.l == None  # is a local min
            n.r = self.deleteMin(n.r)
            new_n.r = n.r
            new_n.l = n.l
            new_n.N = self.size(new_n.l) + self.size(new_n.r) + 1
            assert self.size(new_n) == self.size(n) - 1

            n = new_n
            assert self.invariant(n)
        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    # Hibbard deletion. Randomly choose successor delete or predecessor delete
    def delete(self, k, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root

        if n == None:
            print("WARNING: delete was called on an empty tree")
            return None

        assert self.invariant(n)

        if random.getrandbits(1):
            return self._successor_delete(k, n)
        else:
            return self._predecessor_delete(k, n)

    def size(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root

        return n.N if n else 0

    def internal_path(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root
        return 0 if n == None else self.size(n) + self.internal_path(n.l) + self.internal_path(n.r)

    def avg_path(self, n=NOT_GIVEN):
        if n == BinarySearchTree.NOT_GIVEN:
            n = self.root
        return 0 if n == None else self.internal_path(n) / self.size(n)

    def __repr__(self):
        return BinarySearchTree.repr(self.root)

    @staticmethod
    def repr(node):
        if node == None:
            return "*"
        return "{n}({l}, {r})".format(n=str(node.k), l=BinarySearchTree.repr(node.l), r=BinarySearchTree.repr(node.r))


def testConstructor():
    arr = [(x, 31 % x) for x in (random.randint(1, 12) for x in range(120000))]
    bst = BinarySearchTree(arr)
    print(bst)


def experimentDeletion(N=1000):
    import math

    data = list(range(N))
    random.shuffle(data)

    bst = BinarySearchTree((k, v)
                           for v, k in enumerate(data) if random.getrandbits(1))
    # bst = BinarySearchTree()
    # for i in range(0, 1000 * N):
    #     bst.put(random.randint(0, N / 2 - 1) * 2, i)
    print(f"no delete:")
    print(f"size: {bst.size()}")
    print(f"avg path  : {bst.avg_path()}")
    print(f"ideal path: {math.log2(bst.size())}\n")

    # bst_succ = BinarySearchTree((k, v) for v, k in enumerate(data))
    bst_succ = BinarySearchTree()
    for i in range(0, 1000 * N):
        bst_succ.put(random.randint(0, N - 1), i)
        bst_succ._successor_delete(random.randint(0, N - 1), bst_succ.root)
    print(f"successor delete:")
    print(f"size: {bst_succ.size()}")
    print(f"avg path  : {bst_succ.avg_path()}")
    print(f"ideal path: {math.log2(bst_succ.size())}\n")

    # bst_pred = BinarySearchTree((k, v) for v, k in enumerate(data))
    bst_pred = BinarySearchTree()
    for i in range(0, 1000 * N):
        bst_pred.put(random.randint(0, N - 1), i)
        bst_pred._predecessor_delete(random.randint(0, N - 1), bst_pred.root)
    print(f"predecessor delete:")
    print(f"size: {bst_pred.size()}")
    print(f"avg path  : {bst_pred.avg_path()}")
    print(f"ideal path: {math.log2(bst_pred.size())}\n")

    # bst_rand = BinarySearchTree((k, v) for v, k in enumerate(data))
    bst_rand = BinarySearchTree()
    for i in range(0, 1000 * N):
        bst_rand.put(random.randint(0, N - 1), i)
        bst_rand.delete(random.randint(0, N - 1))
    print(f"random delete:")
    print(f"size: {bst_rand.size()}")
    print(f"avg path  : {bst_rand.avg_path()}")
    print(f"ideal path: {math.log2(bst_rand.size())}\n")


def testValidity(N=1000):
    bst = BinarySearchTree()
    for i in range(0, 100 * N):
        bst.put(random.randint(0, N - 1), i)
        bst.delete(random.randint(0, N - 1))

    print(f"satisfies invariant: {bst.validateInvariant()}")
    print(f"sizes correct      : {bst.validateSize()}")


def main():
    # experimentDeletion(10**2)
    # experimentDeletion(10**3)
    # experimentDeletion(10**4)

    testValidity(10000)


if __name__ == "__main__":
    main()

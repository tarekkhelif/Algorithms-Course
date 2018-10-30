import random


class Node:

    def __init__(self, k, v, N):
        self.k = k
        self.v = v
        self.l = None
        self.r = None
        self.N = N


class BST:
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

    def manualSize(self, n):
        if n == None:
            return 0
        else:
            return 1 + self.manualSize(n.l) + self.manualSize(n.r)

    def put(self, k, v, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            self.root = self.put(k, v, self.root)
            return self.root
        if n == None:
            return Node(k, v, 1)
        if k < n.k:
            n.l = self.put(k, v, n.l)
        elif k > n.k:
            n.r = self.put(k, v, n.r)
        else:  # k == n.k
            n.v = v
        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    def min(self, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        if n == None:
            print("WARNING: min was called on an empty tree")
            return None
        if n.l == None:
            return n
        return self.min(n.l)

    def max(self, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        if n == None:
            print("WARNING: max was called on an empty tree")
            return None
        if n.r == None:
            return n
        return self.max(n.r)

    def deleteMin(self, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        if n == None:
            print("WARNING: deleteMin was called on an empty tree")
            return None
        if n.l == None:  # n is min
            return n.r
        n.l = self.deleteMin(n.l)
        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    def deleteMax(self, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        if n == None:
            print("WARNING: deleteMax was called on an empty tree")
            return None
        if n.r == None:  # n is max
            return n.l
        n.r = self.deleteMax(n.r)
        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    def _predecessor_delete(self, k, n):
        if n == None:
            return None
        if k < n.k:
            n.l = self._predecessor_delete(k, n.l)
        elif k > n.k:
            n.r = self._predecessor_delete(k, n.r)
        else:  # k == n.k: # replace with *prececessor*
            if n.r == None:
                return n.l
            if n.l == None:
                return n.r
            old = n
            n = self.max(old.l)
            n.l = self.deleteMax(old.l)
            n.r = old.r
        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    def _successor_delete(self, k, n):
        if n == None:
            return None
        if k < n.k:
            n.l = self._successor_delete(k, n.l)
        elif k > n.k:
            n.r = self._successor_delete(k, n.r)
        else:  # k == n.k: # replace with *successor*
            if n.r == None:
                return n.l
            if n.l == None:
                return n.r
            old = n
            n = self.min(old.r)
            n.r = self.deleteMin(old.r)
            n.l = old.l
        n.N = self.size(n.l) + self.size(n.r) + 1
        return n

    # Hibbard deletion. Randomly choose successor delete or predecessor delete
    def delete(self, k, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        if n == None:
            print("WARNING: delete was called on an empty tree")
            return None
        if random.getrandbits(1):
            return self._successor_delete(k, n)
        else:
            return self._predecessor_delete(k, n)

    def size(self, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        return n.N if n else 0

    def internal_path(self, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        return 0 if n == None else self.size(n) + self.internal_path(n.l) + self.internal_path(n.r)

    def avg_path(self, n=NOT_GIVEN):
        if n == BST.NOT_GIVEN:
            n = self.root
        return 0 if n == None else self.internal_path(n) / self.size(n)

    def __repr__(self):
        return BST.repr(self.root)

    @staticmethod
    def repr(node):
        if node == None:
            return "*"
        return "{n}({l}, {r})".format(n=str(node.k), l=BST.repr(node.l), r=BST.repr(node.r))


def testConstructor():
    arr = [(x, 31 % x) for x in (random.randint(1, 12) for x in range(120000))]
    bst = BST(arr)
    print(bst)


def experimentDeletion(N=1000):
    import math

    data = list(range(N))
    random.shuffle(data)

    bst = BST((k, v) for v, k in enumerate(data) if random.getrandbits(1))
    # bst = BST()
    # for i in range(0, (N/2)**2):
    #     bst.put(random.randint(0, N / 2 - 1) * 2, i)
    print(f"no delete:")
    print(f"size: {bst.size()}")
    print(f"avg path  : {bst.avg_path()}")
    print(f"ideal path: {math.log2(bst.size())}\n")

    # bst_succ = BST((k, v) for v, k in enumerate(data))
    bst_succ = BST()
    for i in range(0, 10**6):
        bst_succ.put(random.randint(0, N - 1), i)
        bst_succ._successor_delete(random.randint(0, N - 1), bst_succ.root)
    print(f"successor delete:")
    print(f"size: {bst_succ.size()}")
    print(f"avg path  : {bst_succ.avg_path()}")
    print(f"ideal path: {math.log2(bst_succ.size())}\n")

    # bst_pred = BST((k, v) for v, k in enumerate(data))
    bst_pred = BST()
    for i in range(0, 10**6):
        bst_pred.put(random.randint(0, N - 1), i)
        bst_pred._predecessor_delete(random.randint(0, N - 1), bst_pred.root)
    print(f"predecessor delete:")
    print(f"size: {bst_pred.size()}")
    print(f"avg path  : {bst_pred.avg_path()}")
    print(f"ideal path: {math.log2(bst_pred.size())}\n")

    # bst_rand = BST((k, v) for v, k in enumerate(data))
    bst_rand = BST()
    for i in range(0, 10**6):
        bst_rand.put(random.randint(0, N - 1), i)
        bst_rand.delete(random.randint(0, N - 1))
    print(f"random delete:")
    print(f"size: {bst_rand.size()}")
    print(f"avg path  : {bst_rand.avg_path()}")
    print(f"ideal path: {math.log2(bst_rand.size())}\n")


def main():
    experimentDeletion(2 * 10**2)


if __name__ == "__main__":
    main()

import edu.princeton.cs.algs4.In;

public class WordNet {

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        In in = new In(synsets);

    }
 
    // do unit testing of this class
    public static void main(String[] args) {}
 
    // returns all WordNet nouns
    public Iterable<String> nouns() {}
 
    // is the word a WordNet noun?
    public boolean isNoun(String word) {}
 
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {}
 
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {}
 }

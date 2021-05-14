/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class WordNet {
    private final SAP wordSap;
    private final HashMap<String, Bag<Integer>> wordMap;
    private final HashMap<Integer, String> idMap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        wordMap = new HashMap<String, Bag<Integer>>();
        idMap = new HashMap<Integer, String>();
        In synIn = new In(synsets);
        In hypIn = new In(hypernyms);
        try {
            Queue<Integer> ids = new Queue<Integer>();

            while (!synIn.isEmpty()) {
                String line = synIn.readLine();
                String[] fields = line.split(",");
                String[] words = fields[1].split(" ");

                int id = Integer.parseInt(fields[0]);
                idMap.put(id, fields[1]);
                for (String s : words) {
                    Bag<Integer> curId = (!wordMap.containsKey(s)) ? new Bag<Integer>() :
                                         wordMap.get(s);
                    curId.add(id);
                    wordMap.put(s, curId);
                }
                ids.enqueue(id);
            }

            Digraph g = new Digraph(ids.size());

            while (!hypIn.isEmpty()) {
                String line = hypIn.readLine();
                String[] fields = line.split(",");
                int v = Integer.parseInt(fields[0]);
                for (int i = 1; i < fields.length; i++) {
                    g.addEdge(v, Integer.parseInt(fields[i]));
                }
            }
            wordSap = new SAP(g);
            checkDAG(g);

        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in WordNet structure", e);
        }
    }

    // check if input digraph is a DAG
    private void checkDAG(Digraph g) {
        int numRoot = 0;
        for (int i = 0; i < g.V(); i++) {
            if (g.outdegree(i) == 0) numRoot++;
        }
        if (numRoot != 1)
            throw new IllegalArgumentException(
                    "there is no root or more than one root in digraph!");
        DirectedCycle dc = new DirectedCycle(g);
        if (dc.hasCycle()) throw new IllegalArgumentException("Digraph is not a DAG!");
    }

    // return all WordNet nouns
    public Iterable<String> nouns() {
        return new ArrayList<>(wordMap.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("null argument!");
        return wordMap.containsKey(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("invalid argument!");

        return wordSap.length(wordMap.get(nounA), wordMap.get(nounB));
    }

    // a synset that is common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("invalid argument!");
        int id = wordSap.ancestor(wordMap.get(nounA), wordMap.get(nounB));

        return idMap.get(id);
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        StdOut.println(wordnet.distance("Brown_Swiss", "barrel_roll"));
    }

}


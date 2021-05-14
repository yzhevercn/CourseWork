/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;
    
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("null argument!");
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException("null argument!");
        int[] sum = new int[nouns.length];
        for (int i = 0; i < sum.length; i++) sum[i] = 0;
        for (int i = 0; i < nouns.length; i++) {
            for (int j = 0; j < nouns.length; j++) {
                sum[i] += wordnet.distance(nouns[i], nouns[j]);
            }
        }
        int maxIndex = 0;
        for (int i = 0; i < sum.length; i++) {
            if (sum[i] > sum[maxIndex]) maxIndex = i;
        }
        return nouns[maxIndex];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

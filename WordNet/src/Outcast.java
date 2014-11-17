public class Outcast {
	private WordNet wordnet;

	public Outcast(WordNet wordnet) {
		// constructor takes a WordNet object
		this.wordnet = wordnet;
	}
	public String outcast(String[] nouns) {
		// given an array of WordNet nouns, return an outcast
		//calculate minimum distance between each word and all the rest
		int maxdist = 0;
		String maxdistword = null;

		for (int i = 0; i < nouns.length ; i++) {
			int totaldist = 0;
			for (int j = 0; j< nouns.length ; j++) {
				if (i == j)
					continue;
				totaldist += wordnet.distance(nouns[i], nouns[j]);
			}
			if (totaldist > maxdist) {
				maxdist = totaldist;
				maxdistword = nouns[i];
			}
		}
		return maxdistword;
	}

	public static void main(String[] args) {
		String synset = "testing/synsets.txt"; //synsets.txt is the complete
		String hypernym = "testing/hypernyms.txt";

		String input = "apple pear peach banana lime lemon blueberry strawberry mango watermelon potato";
		String[] words = input.split(" ");

		WordNet wordnet = new WordNet(synset, hypernym);
		Outcast outcast = new Outcast(wordnet);

		System.out.println(input + " : " + outcast.outcast(words));
		//	    for (int t = 0; t < words.length; t++) {
		//	        In in = new In(args[t]);
		//	        String[] nouns = in.readAllStrings();
		//	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		//   }
	}
}

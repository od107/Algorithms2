import java.util.HashMap;
import java.util.Map;

public class WordNet {
	//throwing of all these exceptions might not be necessary
	private Map<String, Integer> wordmap = new HashMap<String, Integer>();
	private Map<Integer, String> reverseMap = new HashMap<Integer, String>();
	private SAP sap; 

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms) {
	   if (synsets == null || hypernyms == null)
		   throw new java.lang.NullPointerException();

	   In synsetIn = new In(synsets);
	   In hypernymIn = new In(hypernyms);
	   String nbrOfVertices = null;
	   
	   while (synsetIn.hasNextLine()) {
		   String[] fields = synsetIn.readLine().split(",");
		   wordmap.put(fields[1], Integer.parseInt(fields[0]));
		   reverseMap.put(Integer.parseInt(fields[0]), fields[1]);
		   nbrOfVertices = fields[0];
	   }
//	   System.out.println(wordmap);
	   
	   //need to get the hypernyms in the DAG
	   // use input stream constructor but first determine the number of vertices and edges
	   Digraph DG = new Digraph(1 + Integer.parseInt(nbrOfVertices));
	   while (hypernymIn.hasNextLine()) {
		   String[] fields = hypernymIn.readLine().split(",");
		   for(int i = 1; i < fields.length ; i++) {
			   DG.addEdge(Integer.parseInt(fields[0]), Integer.parseInt(fields[i]));
		   }
	   }
	   if (!isDAG(DG))
		   throw new java.lang.IllegalArgumentException();
	   
//	   System.out.println(DG);
	   sap = new SAP(DG);
   }
   
   private boolean isDAG(Digraph DG){
	   //TODO implement this method
	   
	   return true;
   }

   // returns all WordNet nouns
   public Iterable<String> nouns() {
	   return wordmap.keySet();
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word) {
//	   if (word == null)
//		   throw new java.lang.NullPointerException();
	   return wordmap.containsKey(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB) {
//	   if (nounA == null || nounB == null)
//		   throw new java.lang.NullPointerException();
//	   if (!isNoun(nounA) || !isNoun(nounB))
//		   throw new java.lang.IllegalArgumentException();
	   int intA = wordmap.get(nounA);
	   int intB = wordmap.get(nounB);
	   return sap.length(intA, intB);
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB) {
//	   if (nounA == null || nounB == null)
//		   throw new java.lang.NullPointerException();
//	   if (!isNoun(nounA) || !isNoun(nounB))
//		   throw new java.lang.IllegalArgumentException();
	   int intA = wordmap.get(nounA);
	   int intB = wordmap.get(nounB);
	   int result = sap.ancestor(intA, intB);
	   return reverseMap.get(result);
   }

   // do unit testing of this class
   public static void main(String[] args) {
	   String synset = "testing/synsets15.txt"; //synsets.txt is the complete
	   String hypernym = "testing/hypernymsTree15.txt";
	   
	   WordNet WN = new WordNet(synset,hypernym);
	   System.out.println("is this noun in the list? " + WN.isNoun("boo"));
	   System.out.println("distance: " + WN.distance("a","m"));
	   System.out.println("the common ancestor is: " + WN.sap("a", "m"));
   }
}
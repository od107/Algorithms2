import java.util.*;

public class WordNet {
	//throwing of all these exceptions might not be necessary
	private Map<String, List<Integer>> wordmap = new HashMap<String, List<Integer>>();
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
		   reverseMap.put(Integer.parseInt(fields[0]), fields[1]);
		   
		   String[] synset = fields[1].split(" ");
		   for (int i = 0; i < synset.length ; i++) {
			   List<Integer> list;
			   
			   if (wordmap.containsKey(synset[i])) 
				   list = wordmap.get(synset[i]);
			   else 
				   list = new ArrayList<Integer>();
			   
			   list.add(Integer.parseInt(fields[0]));
			   wordmap.put(synset[i], list);
		   }
		   
		   
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
	   if (!isDAGwSingleRoot(DG))
		   throw new java.lang.IllegalArgumentException();
	   
//	   System.out.println("number of vertices:" + DG.V());
//	   System.out.println("number of edges:" + DG.E());
	   sap = new SAP(DG);
   }
   
   private boolean isDAGwSingleRoot(Digraph DG){
	   //TODO implement this method
	   //check for cycles DFS, see if you encounter yourself
	   DirectedCycle cycle = new DirectedCycle(DG);
	   if (cycle.hasCycle())
		   return false;
	   
	   return true;
   }

   // returns all WordNet nouns
   public Iterable<String> nouns() {
	   return wordmap.keySet();
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word) {
	   if (word == null)
		   throw new java.lang.NullPointerException();
	   return wordmap.containsKey(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB) {
//	   if (nounA == null || nounB == null)
//		   throw new java.lang.NullPointerException();
	   if (!isNoun(nounA) || !isNoun(nounB))
		   throw new java.lang.IllegalArgumentException();
	   List<Integer> intA = wordmap.get(nounA);
	   List<Integer> intB = wordmap.get(nounB);
	   return sap.length(intA, intB);
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB) {
//	   if (nounA == null || nounB == null)
//		   throw new java.lang.NullPointerException();
	   if (!isNoun(nounA) || !isNoun(nounB))
		   throw new java.lang.IllegalArgumentException();
	   List<Integer> intA = wordmap.get(nounA);
	   List<Integer> intB = wordmap.get(nounB);
	   int result = sap.ancestor(intA, intB);
	   return reverseMap.get(result);
   }

   // do unit testing of this class
   public static void main(String[] args) {
	   String synset = "testing/synsets.txt"; //synsets.txt is the complete
	   String hypernym = "testing/hypernyms.txt";
	   
	   String wordA = "Brown_Swiss";
	   String wordB = "barrel_roll";
	   
	   WordNet WN = new WordNet(synset,hypernym);
	   System.out.println("is this noun in the list? " + WN.isNoun(wordA));
	   System.out.println("distance: " + WN.distance(wordA,wordB));
	   System.out.println("the common ancestor is: " + WN.sap(wordA,wordB));
   }
}
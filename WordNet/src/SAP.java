import java.util.*;

public class SAP {
	private Digraph DG;

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {
	   DG = new Digraph(G);
	   
	   Map<Integer, int[]> map = BFS(9);
//	   for( int[] vc : map.values()) {
//		   System.out.println("");
//	   }
	   System.out.println(map.values());
   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) {
	   
	   return -1;
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w) {
	   
	   return -1;
   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w) {
	   
	   return -1;
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	   
	   return -1;
   }
   
   private Map<Integer, int[]> BFS(int v) { //visited and count is in the array
//	   List<Integer> list = new ArrayList<Integer>();
	   Map<Integer, int[]> map = new HashMap<Integer, int[]>();
	   java.util.Queue<Integer> q = new LinkedList<Integer>();
	   int count = 0;
	   
	   q.add(v); //the first element is not in the map
	   while(!q.isEmpty()) {
		   int current = q.remove();
		   
		   if (!map.containsKey(current)) {
			   int[] vc = new int[2];
			   vc[0] = 1; // mark as visited
			   vc[1] = count;
			   map.put(current, vc);
		   }
		   
		   count++; // when exactly to increment? 
		   
		   for(int adj : DG.adj(current)) {
			   if (map.containsKey(adj)) { //this might replace the vc[0]?
//				   int[] vc = map.get(adj);
//				   if (vc[0] == 0)  {//not yet visited
//					   q.add(adj);
//					   vc[0] = 1; // mark as visited
//					   vc[1] = count;
//				   }  
			   }
			   else {
//				   int[] vc = new int[2];
				   q.add(adj);
//				   vc[0] = 1; // mark as visited
//				   vc[1] = count;
//				   map.put(adj, vc);
			   }
		   }
		   
	   }
	  
	   
	   return map;
   }

   // do unit testing of this class
   public static void main(String[] args) {
	    In in = new In("testing/digraph1.txt"); //args[0]);
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);

	    
//	    while (!StdIn.isEmpty()) {
//	        int v = StdIn.readInt();
//	        int w = StdIn.readInt();
//	        int length   = sap.length(v, w);
//	        int ancestor = sap.ancestor(v, w);
//	        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//	    }
   }
}
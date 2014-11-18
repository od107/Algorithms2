import java.util.*;
// ugly cast in length and ancestor is needed because the assignment
// prohibits deviating from the API which includes external classes

public class SAP {
	private Digraph DG;

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {
	   DG = new Digraph(G);
   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) { //these methods don't work if it is not a DAG
	   List<Integer> vlist = new ArrayList<Integer>();
	   vlist.add(v);
	   List<Integer> wlist = new ArrayList<Integer>();
	   wlist.add(w);
	   return length(vlist,wlist);
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w) {
	   List<Integer> vlist = new ArrayList<Integer>();
	   vlist.add(v);
	   List<Integer> wlist = new ArrayList<Integer>();
	   wlist.add(w);
	   return ancestor(vlist,wlist);
   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w) {
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   ArrayList<Object> result = BFS(v, map);
	   Map<Integer, Integer> mapv = (Map<Integer, Integer>) result.get(0);
	   result = BFS(w, mapv);
	   int[] sol = (int[]) result.get(1);
	   if (sol[0] == -1)
		   return -1;
	   return sol[1];
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   ArrayList<Object> result = BFS(v, map);
	   Map<Integer, Integer> mapv = (Map<Integer, Integer>) result.get(0);
	   result = BFS(w, mapv);
	   int[] sol = (int[]) result.get(1);
	   if (sol[0] == -1)
		   return -1;
	   return sol[0];
   }
   
   private ArrayList<Object> BFS(Iterable<Integer> list, Map<Integer, Integer> mapv) { //BFS until reaching path of v
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   java.util.Queue<Integer> q = new LinkedList<Integer>();
	   ArrayList<Object> result = new ArrayList<Object>();
	   int[] sol = {-1, Integer.MAX_VALUE};
	   
	   for (int v : list) {
		   q.add(v);
		   map.put(v, 0);
	   }
	   
	   while(!q.isEmpty()) {
		   int current = q.remove();
		   
		   if (mapv.containsKey(current)) { //vertex already visited
			   if ((map.get(current) + mapv.get(current)) < sol[1]) {
				   sol[0] = current;
				   sol[1] = map.get(current) + mapv.get(current);
			   }
		   }
			   
		   for(int adj : DG.adj(current)) {
			   if (!map.containsKey(adj)) { 
				   map.put(adj, map.get(current) + 1);
				   q.add(adj);
			   }
		   }
	   }
	   result.add(map);
	   result.add(sol);
	   return result;
   }
   
   // do unit testing of this class
   public static void main(String[] args) {
	    In in = new In("testing/digraph3.txt"); 
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);
	    
	    while (!StdIn.isEmpty()) {
	        int v = StdIn.readInt();
	        int w = StdIn.readInt();
	        int length   = sap.length(v, w);
	        int ancestor = sap.ancestor(v, w);
	        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
	    }
   }
}
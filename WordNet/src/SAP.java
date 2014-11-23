import java.util.*;

public class SAP {
	private Digraph digraph;
	
	private static class Result {
		public final Map<Integer,Integer> markedMap;
		public final int distance;
		public final int ancestor;
		
		public Result(Map<Integer,Integer> map, int dist, int anc) {
			markedMap = map;
			distance = dist;
			ancestor = anc;
		}
	}

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {
	   digraph = new Digraph(G);
   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) { 
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
	   Result result = breadthFirstSearch(v, map);
	   Map<Integer, Integer> mapv = result.markedMap;
	   result = breadthFirstSearch(w, mapv);
	   if (result.ancestor == -1)
		   return -1;
	   return result.distance;
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   Result result = breadthFirstSearch(v, map);
	   Map<Integer, Integer> mapv = result.markedMap;
	   result = breadthFirstSearch(w, mapv);
	   return result.ancestor;
   }
   
   private Result breadthFirstSearch(Iterable<Integer> list, Map<Integer, Integer> mapv) { 
	   // first visit: build distance map from v
	   // second visit: calculate shortest distance from w to map + return nearest ancestor
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   java.util.Queue<Integer> q = new LinkedList<Integer>();
	   int ancestor = -1;
	   int distance = Integer.MAX_VALUE;
	   
	   for (int v : list) {
		   q.add(v);
		   map.put(v, 0);
	   }
	   
	   while(!q.isEmpty()) {
		   int current = q.remove();
		   
		   if (mapv.containsKey(current)) { //vertex already visited
			   if ((map.get(current) + mapv.get(current)) < distance) {
				   ancestor = current;
				   distance = map.get(current) + mapv.get(current);
			   }
		   }
			   
		   for(int adj : digraph.adj(current)) {
			   if (!map.containsKey(adj)) { 
				   map.put(adj, map.get(current) + 1);
				   q.add(adj);
			   }
		   }
	   }
	   Result result = new Result(map, distance, ancestor);
	   return result;
   }
   
   // do unit testing of this class
   public static void main(String[] args) {
	    In in = new In("testing/digraph2.txt"); 
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
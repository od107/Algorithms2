import java.util.*;

public class SAP {
	private Digraph DG;

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {
	   DG = new Digraph(G);
	   
//	   test BFS
//	   List<Integer> v = new ArrayList<Integer>();
//	   v.add(12);
//	   v.add(2);
//	   v.add(3);
//	   Map<Integer, Integer> map = BFS(v);
//	   for( int i=0; i< DG.V();i++) {
//		   System.out.println("key: " + i + "\tvalue: " + map.get(i));
//	   }
//	   System.out.println(map.values());

   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) { //these methods don't work if it is not a DAG
	   Map<Integer, Integer> mapv = BFS(v);
	   int[] sol = BFS2(w, mapv);
	   if (sol[0] == -1)
		   return -1;
	   return sol[1];
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w) {
	   Map<Integer, Integer> mapv = BFS(v);
	   int[] sol = BFS2(w, mapv);
	   if (sol[0] == -1)
		   return -1;
	   return sol[0];
   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w) {
	   Map<Integer, Integer> mapv = BFS(v);
	   int[] sol = BFS2(w, mapv);
	   if (sol[0] == -1)
		   return -1;
	   return sol[1];
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
	   Map<Integer, Integer> mapv = BFS(v);
	   int[] sol = BFS2(w, mapv);
	   if (sol[0] == -1)
		   return -1;
	   return sol[0];
   }
   
   private Map<Integer, Integer> BFS(int v) { 
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   java.util.Queue<Integer> q = new LinkedList<Integer>();
	   
	   q.add(v);
	   map.put(v, 0);
	   
	   while(!q.isEmpty()) {
		   int current = q.remove();
		   
		   for(int adj : DG.adj(current)) {
			   if (!map.containsKey(adj)) { 
				   map.put(adj, map.get(current) + 1);
				   q.add(adj);
			   }
		   }
	   }
	   return map;
   }
   
   private int[] BFS2(int v, Map<Integer, Integer> mapv) { //BFS until reaching path of v
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   java.util.Queue<Integer> q = new LinkedList<Integer>();
	   int[] sol = {-1, Integer.MAX_VALUE};
	   
	   q.add(v);
	   map.put(v, 0);
	   
	   while(!q.isEmpty()) {
		   int current = q.remove();
		   
		   if (mapv.containsKey(current)) { //vertex already visited
			   if ((map.get(current) + mapv.get(current)) < sol[1]) {
				   sol[0] = current;
				   sol[1] = map.get(current) + mapv.get(current);
			   }
			   else //if vertex is already visited with a better value do not add neighbours
				   continue; // early stopping
		   }
			   
		   for(int adj : DG.adj(current)) {
			   if (!map.containsKey(adj)) { 
				   map.put(adj, map.get(current) + 1);
				   q.add(adj);
			   }
		   }
	   }
	   return sol;
   }

   private Map<Integer, Integer> BFS(Iterable<Integer> list) { 
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   java.util.Queue<Integer> q = new LinkedList<Integer>();

	   for(int v : list) {
		   q.add(v);
		   map.put(v, 0);
	   }

	   while(!q.isEmpty()) {
		   int current = q.remove();

		   for(int adj : DG.adj(current)) {
			   if (!map.containsKey(adj)) { // || map.get(adj) > map.get(current)) { 
				   map.put(adj, map.get(current) + 1);
				   q.add(adj);
			   }
		   }
	   }
	   return map;   
   }

   private int[] BFS2(Iterable<Integer> list, Map<Integer, Integer> mapv) { //BFS until reaching path of v
	   Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   java.util.Queue<Integer> q = new LinkedList<Integer>();
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
			   else //if vertex is already visited with a better value do not add neighbours
				   continue; // early stopping
		   }
			   
		   for(int adj : DG.adj(current)) {
			   if (!map.containsKey(adj)) { 
				   map.put(adj, map.get(current) + 1);
				   q.add(adj);
			   }
		   }
	   }
	   return sol;
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
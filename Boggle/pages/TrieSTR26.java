/** 	adaptation of the TrieST class provided in the algs4 package
 * 		now with radix R = 26 because in our case we only use letters A-Z
*/
public class TrieSTR26<Value> {

	    private static final int R = 26;        //reduced Radix


	    private Node root;      // root of trie
	    private int N;          // number of keys in trie
	    private String lastPrefix; //store the last prefix Search
	    private Node lastPrefixNode;

	    // R-way trie node
	    private static class Node {
	        private Object val;
	        private Node[] next = new Node[R];
	    }

	    public TrieSTR26() {
	    }

	   /**
	     * Initializes an empty string symbol table.
	     */

	    /**
	     * Returns the value associated with the given key.
	     * @param key the key
	     * @return the value associated with the given key if the key is in the symbol table
	     *     and <tt>null</tt> if the key is not in the symbol table
	     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
	     */
	    public Value get(String key) {
	        Node x = get(root, key, 0);
	        if (x == null) return null;
	        return (Value) x.val;
	    }

	    /**
	     * Does this symbol table contain the given key?
	     * @param key the key
	     * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
	     *     <tt>false</tt> otherwise
	     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
	     */
	    public boolean contains(String key) {
	        return get(key) != null;
	    }

	    private Node get(Node x, String key, int d) {
	        if (x == null) return null;
	        if (d == key.length()) return x;
	        int c = key.charAt(d) - 65; //A is the 65th number
	        return get(x.next[c], key, d+1);
	    }

	    /**
	     * Inserts the key-value pair into the symbol table, overwriting the old value
	     * with the new value if the key is already in the symbol table.
	     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
	     * @param key the key
	     * @param val the value
	     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
	     */
	    public void put(String key, Value val) {
	        if (val == null) delete(key);
	        else root = put(root, key, val, 0);
	    }

	    private Node put(Node x, String key, Value val, int d) {
	        if (x == null) x = new Node();
	        if (d == key.length()) {
	            if (x.val == null) N++;
	            x.val = val;
	            return x;
	        }
	        int c = key.charAt(d) - 65;
	        x.next[c] = put(x.next[c], key, val, d+1);
	        return x;
	    }

	    /**
	     * Returns the number of key-value pairs in this symbol table.
	     * @return the number of key-value pairs in this symbol table
	     */
	    public int size() {
	        return N;
	    }

	    /**
	     * Is this symbol table empty?
	     * @return <tt>true</tt> if this symbol table is empty and <tt>false</tt> otherwise
	     */
	    public boolean isEmpty() {
	        return size() == 0;
	    }

	    /**
	     * Returns all keys in the symbol table as an <tt>Iterable</tt>.
	     * To iterate over all of the keys in the symbol table named <tt>st</tt>,
	     * use the foreach notation: <tt>for (Key key : st.keys())</tt>.
	     * @return all keys in the sybol table as an <tt>Iterable</tt>
	     */
	    public Iterable<String> keys() {
	        return keysWithPrefix("");
	    }

	    /**
	     * New method returns boolean value that indicates code with <tt>prefix</tt> exists.
	     * @param prefix the prefix
	     * @return boolean value that indicates code with <tt>prefix</tt> exist,
	     *     as an iterable
	     */
	    //TODO: exploit the fact that subsequent searches can start from the node of the previous search 
	    //optimalised version (works but is not faster than original ~ 1270 boards per sec
	    public boolean keyWithPrefixExist(String prefix) {
	    	if(lastPrefix != null && prefix.startsWith(lastPrefix)){ //instead of testing for this we can always start the search there...
	    		Node search = get(lastPrefixNode, prefix, lastPrefix.length()); 
	    		if(search == null)
	    			return false;
	    		lastPrefix = prefix;
	    		lastPrefixNode = search;
	    		return true;
	    	}
	    	else {
	    		Node search = get(root, prefix, 0);
	    		if(search == null) //search from the root
	    			return false;
	    		lastPrefix = prefix;
	    		lastPrefixNode = search;
	    		return true;
	    	}
	    }
	    
	    //working version (1300 boards per sec @ work and home)
//	    public boolean keyWithPrefixExist(String prefix) {
//	    	if(get(root, prefix, 0) == null) 
//	        	return false;
//	        return true;
//	    }
	    /**
	     * Returns all of the keys in the set that start with <tt>prefix</tt>.
	     * @param prefix the prefix
	     * @return all of the keys in the set that start with <tt>prefix</tt>,
	     *     as an iterable
	     */
	    public Iterable<String> keysWithPrefix(String prefix) {
	        Queue<String> results = new Queue<String>();
	        Node x = get(root, prefix, 0);
	        collect(x, new StringBuilder(prefix), results);
	        return results;
	    }

	    private void collect(Node x, StringBuilder prefix, Queue<String> results) {
	        if (x == null) return;
	        if (x.val != null) results.enqueue(prefix.toString());
	        for (char c = 0; c < R; c++) {
	            prefix.append(c);
	            collect(x.next[c], prefix, results);
	            prefix.deleteCharAt(prefix.length() - 1);
	        }
	    }

	    /**
	     * Returns all of the keys in the symbol table that match <tt>pattern</tt>,
	     * where . symbol is treated as a wildcard character.
	     * @param pattern the pattern
	     * @return all of the keys in the symbol table that match <tt>pattern</tt>,
	     *     as an iterable, where . is treated as a wildcard character.
	     */
	    public Iterable<String> keysThatMatch(String pattern) {
	        Queue<String> results = new Queue<String>();
	        collect(root, new StringBuilder(), pattern, results);
	        return results;
	    }

	    private void collect(Node x, StringBuilder prefix, String pattern, Queue<String> results) {
	        if (x == null) return;
	        int d = prefix.length();
	        if (d == pattern.length() && x.val != null)
	            results.enqueue(prefix.toString());
	        if (d == pattern.length())
	            return;
	        char c = pattern.charAt(d);
	        if (c == '.') {
	            for (char ch = 0; ch < R; ch++) {
	                prefix.append(ch);
	                collect(x.next[ch], prefix, pattern, results);
	                prefix.deleteCharAt(prefix.length() - 1);
	            }
	        }
	        else {
	            prefix.append(c);
	            collect(x.next[c], prefix, pattern, results);
	            prefix.deleteCharAt(prefix.length() - 1);
	        }
	    }

	    /**
	     * Returns the string in the symbol table that is the longest prefix of <tt>query</tt>,
	     * or <tt>null</tt>, if no such string.
	     * @param query the query string
	     * @throws NullPointerException if <tt>query</tt> is <tt>null</tt>
	     * @return the string in the symbol table that is the longest prefix of <tt>query</tt>,
	     *     or <tt>null</tt> if no such string
	     */
	    public String longestPrefixOf(String query) {
	        int length = longestPrefixOf(root, query, 0, 0);
	        return query.substring(0, length);
	    }

	    // returns the length of the longest string key in the subtrie
	    // rooted at x that is a prefix of the query string,
	    // assuming the first d character match and we have already
	    // found a prefix match of length length
	    private int longestPrefixOf(Node x, String query, int d, int length) {
	        if (x == null) return length;
	        if (x.val != null) length = d;
	        if (d == query.length()) return length;
	        char c = query.charAt(d);
	        return longestPrefixOf(x.next[c], query, d+1, length);
	    }

	    /**
	     * Removes the key from the set if the key is present.
	     * @param key the key
	     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
	     */
	    public void delete(String key) {
	        root = delete(root, key, 0);
	    }

	    private Node delete(Node x, String key, int d) {
	        if (x == null) return null;
	        if (d == key.length()) {
	            if (x.val != null) N--;
	            x.val = null;
	        }
	        else {
	            char c = key.charAt(d);
	            x.next[c] = delete(x.next[c], key, d+1);
	        }

	        // remove subtrie rooted at x if it is completely empty
	        if (x.val != null) return x;
	        for (int c = 0; c < R; c++)
	            if (x.next[c] != null)
	                return x;
	        return null;
	    }

	    /**
	     * Unit tests the <tt>TrieSET</tt> data type.
	     */
	    public static void main(String[] args) {

	        // build symbol table from standard input
	        TrieST<Integer> st = new TrieST<Integer>();
	        for (int i = 0; !StdIn.isEmpty(); i++) {
	            String key = StdIn.readString();
	            st.put(key, i);
	        }

	        // print results
	        if (st.size() < 100) {
	            StdOut.println("keys(\"\"):");
	            for (String key : st.keys()) {
	                StdOut.println(key + " " + st.get(key));
	            }
	            StdOut.println();
	        }

	        StdOut.println("longestPrefixOf(\"shellsort\"):");
	        StdOut.println(st.longestPrefixOf("shellsort"));
	        StdOut.println();

	        StdOut.println("keysWithPrefix(\"shor\"):");
	        for (String s : st.keysWithPrefix("shor"))
	            StdOut.println(s);
	        StdOut.println();

	        StdOut.println("keysThatMatch(\".he.l.\"):");
	        for (String s : st.keysThatMatch(".he.l."))
	            StdOut.println(s);
	    }
	}

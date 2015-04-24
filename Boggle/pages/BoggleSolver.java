import java.util.*;

public class BoggleSolver
{
	TrieST<Integer> dict; //this is 256-way trie: not ideal, create custom 26-way trie?
	int[] points = {0,0,0,1,1,2,3,5,11}; //points gained for words of certain length
	
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
    	dict = new TrieST<Integer>();
    	for(String word : dictionary) {
    		if (word.length() > 2) {
    			if(word.length() < 8) 
    				dict.put(word, points[word.length()]);
    			else
    				dict.put(word, 11);
    		}
    	}
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
    	Set<String> wordlist = new HashSet<String>();
    	
    	boolean[][] visited = new boolean[board.cols()][board.rows()];
    	
    	for(int i = 0; i < board.cols(); i++) {
    		for (int j=0; j < board.rows(); j++) { //start from all possible points
    			int row = i;
    			int col = j;
    			//TODO: create dfs
    			
    		}
    	}
    	
    	return wordlist;
    }
    
    private void dfs(int row, int col,HashSet wordlist) {
    	
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
    	if(dict.contains(word))
    		return dict.get(word);
    	else
    		return 0;
    }
    
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
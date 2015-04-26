import java.util.*;

public class BoggleSolver
{
	//TODO: the performance is not up to par:
	// create testing methods and try to improve performance
//	private TrieSTR26<Integer> dict; //improved memory by using 26 radix Trie
	private TST<Integer> dict;
	private int[] points = {0,0,0,1,1,2,3,5,11}; //points gained for words of certain length
	
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
//    	dict = new TrieSTR26<Integer>();
    	dict = new TST<Integer>();
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
    	Set<String> boggleWords = new HashSet<String>();
    	
    	boolean[][] visited = new boolean[board.cols()][board.rows()];
    	
    	for(int i = 0; i < board.cols(); i++) {
    		for (int j=0; j < board.rows(); j++) { //start from all possible points
    			emptyBool(visited);
    			String word = "";
    			findwords(i,j,boggleWords, visited,word, board);
    		}
    	}
    	return boggleWords;
    }
    
    private void emptyBool(boolean[][] array) {
    	for(int i = 0; i < array.length; i++) {
    		for (int j=0; j < array[0].length; j++) {
    			array[i][j] = false;
    		}
    	}
    }
    
    private void findwords(int col, int row, Set<String> wordlist,boolean[][] visited, 
    		String word, BoggleBoard board) {
    	if(String.valueOf(board.getLetter(row,col)).equals("Q"))
    		word += "QU";
    	else
    		word += board.getLetter(row, col);
    	visited[col][row] = true;
    	if(dict.contains(word))
    		wordlist.add(word);
    	
    	
//    	Queue<String> possibleWords = (Queue<String>) dict.keysWithPrefix(word);
    	Queue<String> possibleWords = (Queue<String>) dict.prefixMatch(word);
    	if(!possibleWords.isEmpty()) {
//    	if(dict.keyswithPrefixExist(word)) { //TODO: work on this one
    		for(int i=-1;i<2;i++) {
    			for(int j=-1;j<2;j++) {
    				if(col + i >= 0 && col + i < board.cols() 
    						&& row + j >= 0 && row + j < board.rows()
    						&& visited[col+i][row+j] != true) {
    					findwords(col+i, row+j,wordlist,visited, word, board);
    				}
    			}
    		}
    	}
    	visited[col][row] = false;
    }
    
    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
    	if(dict.contains(word))
    		return (Integer) dict.get(word);
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
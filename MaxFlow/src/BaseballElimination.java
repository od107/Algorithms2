import java.util.*;

public class BaseballElimination {
	// this collection could just as well be an arraylist but a map has a small performance advantage
	private Map<String, Short> teams = new HashMap<String, Short>();
	private short[] w, l, r; //wins, losses, remaining
	private short[][] g; // games left to play


	public BaseballElimination(String filename) {
		// create a baseball division from given filename in format specified below
		In file = new In(filename);
		
		short size = file.readShort();

		w = new short[size];
		r = new short[size];
		l = new short[size];
		g = new short[size][size];
		
		for(short i=0; !file.isEmpty(); i++) {
			teams.put(file.readString(), i);
			w[i] = file.readShort();
			l[i] = file.readShort();
			r[i] = file.readShort();
			for(short j=0; j < size ; j++) {
				g[i][j] = file.readShort();
			}
		}
	}
	public int numberOfTeams() {
		// number of teams
		return teams.size();
	}
	public Iterable<String> teams() {
		// all teams
		return teams.keySet();
	}
	public int wins(String team) {
		if (teams.get(team) == null)
			throw new java.lang.IllegalArgumentException("Team does not exist");
		// number of wins for given team
		return w[teams.get(team)];
	}
	public int losses(String team) {
		if (teams.get(team) == null)
			throw new java.lang.IllegalArgumentException("Team does not exist");
		// number of losses for given team
		return l[teams.get(team)];
	}
	public int remaining(String team) {
		if (teams.get(team) == null)
			throw new java.lang.IllegalArgumentException("Team does not exist");
		// number of remaining games for given team
		return r[teams.get(team)];
	}
	public int against(String team1, String team2) {
		if (teams.get(team1) == null || teams.get(team2) == null)
			throw new java.lang.IllegalArgumentException("Team does not exist");
		// number of remaining games between team1 and team2
		return g[teams.get(team1)][teams.get(team2)];
	}
	public boolean isEliminated(String team) {
		if (teams.get(team) == null)
			throw new java.lang.IllegalArgumentException("Team does not exist");
		if (trivialElimination(team))
			return true;
		else if (nonTrivialElimination(team))
			return true;
		
		return false;
	}
	
	private boolean trivialElimination(String team) {
		assert team != null;
		
		short index = teams.get(team);
		
		for (int i=0; i< teams.size() ; i++) {
			if (i != index && w[i] > w[index] + r[index])
				return true;
		}
		return false;
	}
	
	private boolean nonTrivialElimination(String team) {
		assert team != null;
		
		FlowNetwork fn = this.createFlowNetwork(team);
		
		//FordFulkerson FF = 
		new FordFulkerson(fn,0,fn.V()-1);
		
		for(FlowEdge edge : fn.adj(0)) {
			if (edge.flow() != edge.capacity())
				return true;
		}
		return false;
	}
	
	private FlowNetwork createFlowNetwork(String team) {
		assert team != null;
		// create flow network
		//we do create the vertices for the teams under consideration 
		//but their capacity will be set to 0 according to g

		int nbrOfTeams = teams.size(); // including team x
		int nbrOfGames = ((nbrOfTeams * nbrOfTeams) - nbrOfTeams) / 2; //not including team x
		int t = nbrOfGames + nbrOfTeams + 1; 
		short teamX = teams.get(team);
		FlowNetwork fn = new FlowNetwork(2 + nbrOfGames + nbrOfTeams);
		
		int gameNumber = 1;
		for (int i=0; i < g.length; i++) {
			for(int j=0; j < g.length; j++) {
				if (i != teamX && j != teamX && j > i) { //or i > j?
					//connect s to game vertices
					fn.addEdge(new FlowEdge(0,gameNumber,g[i][j])); 
					//connect game vertices to team vertices
					fn.addEdge(new FlowEdge(gameNumber,nbrOfGames + i + 1,Double.POSITIVE_INFINITY));
					fn.addEdge(new FlowEdge(gameNumber,nbrOfGames + j + 1,Double.POSITIVE_INFINITY));
					gameNumber++;
				}
			}
		}
		
		// connect team vertices to t
		for (int i=0; i < nbrOfTeams; i++) {
			if (i != teamX) {
				int weight = w[teamX] + r[teamX] - w[i];
				fn.addEdge(new FlowEdge(i + nbrOfGames + 1,t,weight));
			}
		}
		return fn;
	}
	
	public Iterable<String> certificateOfElimination(String team) {
		if (teams.get(team) == null)
			throw new java.lang.IllegalArgumentException("Team does not exist");
		if (trivialElimination(team))
			return trivialEliminationCertificate(team);
		else if(nonTrivialElimination(team))
			return nontrivialEliminationCertificate(team);
		else {
			return null;
//			List<String> empty = new ArrayList<String>();
//			empty.add("Team has not been eliminated");
//			return empty;
		}
	}
	
	private Iterable<String> trivialEliminationCertificate(String team) {
		assert team != null;
		
		List<String> cert = new ArrayList<String>();
		short index = teams.get(team);
		
		for (String opponent : teams()) {
			if (index != teams.get(opponent) && w[teams.get(opponent)] > w[index] + r[index])
				cert.add(opponent);
		}
		return cert;
	}
	
	private Iterable<String> nontrivialEliminationCertificate(String team) {
		assert team != null;
		
		List<String> cert = new ArrayList<String>();
		short index = teams.get(team);
		FlowNetwork fn = this.createFlowNetwork(team);
		
		FordFulkerson FF = new FordFulkerson(fn,0,fn.V()-1);
		//find min-cut
		int nbrOfTeams = teams.size();
		
		for (String opponent : teams()) {
			if (index != teams.get(opponent) && FF.inCut(fn.V()-nbrOfTeams-1+teams.get(opponent)))
				cert.add(opponent);
		}
		
		return cert;
	}
	
	public static void main(String[] args) {
	    BaseballElimination division = new BaseballElimination(args[0]);
	    for (String team : division.teams()) {
	        if (division.isEliminated(team)) {
	            StdOut.print(team + " is eliminated by the subset R = { ");
	            for (String t : division.certificateOfElimination(team)) {
	                StdOut.print(t + " ");
	            }
	            StdOut.println("}");
	        }
	        else {
	            StdOut.println(team + " is not eliminated");
	        }
	    }
	}
}

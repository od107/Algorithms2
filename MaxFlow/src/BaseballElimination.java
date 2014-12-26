import java.util.*;

public class BaseballElimination {
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
		// number of wins for given team
		return w[teams.get(team)];
	}
	public int losses(String team) {
		// number of losses for given team
		return l[teams.get(team)];
	}
	public int remaining(String team) {
		// number of remaining games for given team
		return r[teams.get(team)];
	}
	public int against(String team1, String team2) {
		// number of remaining games between team1 and team2
		return g[teams.get(team1)][teams.get(team2)];
	}
	public boolean isEliminated(String team) {
		// TODO: is given team eliminated?
		if (trivialElimination(team))
			return true;
		
		return false;
	}
	
	private boolean trivialElimination(String team) {
		short index = teams.get(team);
		
		for (int i=0; i< teams.size() ; i++) {
			if (i != index && w[i] > w[index] + r[index])
				return true;
		}
		return false;
	}
	
	public Iterable<String> certificateOfElimination(String team) {
		// TODO: subset R of teams that eliminates given team; null if not eliminated
		if (trivialElimination(team))
			return trivialEliminationCertificate(team);
		return null;
	}
	
	private Iterable<String> trivialEliminationCertificate(String team) {
		List<String> cert = new ArrayList<String>();
		short index = teams.get(team);
		
		for (String opponent : teams()) {
			if (index != teams.get(opponent) && w[teams.get(opponent)] > w[index] + r[index])
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

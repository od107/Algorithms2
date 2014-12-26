import java.util.*;

public class BaseballElimination {
	private Map<String, Integer> teams = new HashMap<String, Integer>();
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
		
		for(int i=0; !file.isEmpty(); i++) {
			teams.put(file.readString(), i);
			w[i] = file.readShort();
			l[i] = file.readShort();
			r[i] = file.readShort();
			for(int j=0; j < size ; j++) {
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
		return false;
	}
	public Iterable<String> certificateOfElimination(String team) {
		// TODO: subset R of teams that eliminates given team; null if not eliminated
		return teams.keySet();
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

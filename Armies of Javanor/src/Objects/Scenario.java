package Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Scenario implements Serializable {

	private static List<Player> players = new ArrayList<>();
	private static int currentTurn;
	private static int duration;
	private static int mapSize;
	private static Tile[][] grid;
	
	public static Player player(int index) {
		
		return players.get(index);
		
	}
	
	public static int numPlayers() {
		
		return players.size();
		
	}
	
	public static int currentTurn() {
		
		return currentTurn;
		
	}
	
	public static int duration() {
		
		return duration;
		
	}
	
	public static int mapSize() {
		
		return mapSize;
		
	}
	
	public static Tile grid(int x, int y) {
		
		return grid[x][y];
		
	}
	
	public static void setMapSize(int value) {
		
		grid = new Tile[value][value];
		mapSize = value;
		
		for(int i = 0; i < mapSize; i++) {
			for(int j = 0; j < mapSize; j++) {
				grid[i][j] = new Tile();
			}
		}
		
	}
	
	public static void addPlayer(Player player) {
		
		players.add(player);
		
	}
	
	public static void removePlayer(Player player) {
		
		if(players.contains(player)) {
			players.remove(player);
		}
		
	}
	
	public static void resetTurns() {
		
		currentTurn = 1;
		
	}
	
	public static void nextTurn() {
		
		currentTurn++;
		
	}
	
	public static void setDuration(int r_duration) {
		
		duration = r_duration;
		
	}
	
	//Attributes for save data only
	private List<Player> playersSave = new ArrayList<>();
	private int currentTurnSave;
	private int durationSave;
	private int mapSizeSave;
	private Tile[][] gridSave;
	
	//Method used only for loading data from a file back into the scenario
	public static void transfer(Scenario temp) {
		
		players = temp.playersSave;
		currentTurn = temp.currentTurnSave;
		duration = temp.durationSave;
		grid = temp.gridSave;
		mapSize = temp.mapSizeSave;
		
	}
	
	//Constructor used only for saving data from the scenario to a file
	public Scenario() {
		
		playersSave = players;
		currentTurnSave = currentTurn;
		durationSave = duration;
		gridSave = grid;
		mapSizeSave = mapSize;
		
	}
	
}

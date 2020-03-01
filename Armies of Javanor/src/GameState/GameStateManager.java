package GameState;

import java.util.ArrayList;

public class GameStateManager {
	
	private ArrayList<GameState> gameStates;
	private int currentState;
	
	public static final int MENUSTATE = 0;
	public static final int SCENARIOSTATE = 1;
	public static final int SUMMONSTATE = 2;
	public static final int INITIALIZESTATE = 3;
	public static final int UNPACKSTATE = 4;
	public static final int MENUOPTIONSSTATE = 5;
	
	public GameStateManager () {
		
		gameStates = new ArrayList<GameState> ();
		
		currentState = UNPACKSTATE; //Determines where the program starts from
		gameStates.add (new MenuState(this));
		gameStates.add(new ScenarioState(this));
		gameStates.add(new SummonState(this));
		gameStates.add(new InitializeState(this));
		gameStates.add(new UnpackState(this));
		gameStates.add(new MenuOptionsState(this));
				
	}
	
	public void setState (int state) {
		currentState = state;
	}
	
	//Added by me to see if it is possible to transfer attributes between states
	public GameState getState (int state) {
		return gameStates.get(state);
	}

	public void draw (java.awt.Graphics2D g) {
		gameStates.get (currentState).draw (g);
	}
	
	public void keyPressed (int k) {
		gameStates.get (currentState).keyPressed (k);
	}
	
}
package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.TreeSet;

import Main.GamePanel;
import Objects.Player;
import Objects.Scenario;
import Utilities.Images;
import Utilities.MapManager;
import Utilities.EnumVariation.God;

public class MenuOptionsState extends GameState {
	
	private int currentChoice = 0;
	private String[] options = {
			"Battlefield", "Duration", "To Arms!!!"
	};
	
	private Color titleColor;
	private Font titleFont;
	
	private Font font;
	
	private int turnLimit = 15;
	private String currentMap = "map1.txt";

	public MenuOptionsState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		try {
			
			titleColor = new Color (128, 0, 0);
			titleFont = new Font (Font.SANS_SERIF, Font.PLAIN, 56 * GamePanel.SCALE);
			
			font = new Font (Font.DIALOG, Font.PLAIN, 24 * GamePanel.SCALE);
			
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		//draw background
		g.drawImage(Images.titlescreen, 0, 0, null);
		
		
		//draw title
		g.setColor (titleColor);
		g.setFont (titleFont);
		g.drawString ("War Preparations", 80, 70);
		
		//draw menu options
		g.setFont (font);
		for (int i = 0; i < options.length; i++) {
			
			if (i == currentChoice) {
				g.setColor (Images.cayenneBlue);
			}
			
			else {
				g.setColor (Images.aldorRed);
			}
			
			if(i == 0) {
				if(currentMap.equals("map1.txt")) {
					g.drawString(options[i] + " - Cayenne Standoff", 10, 150 + i * 30 * GamePanel.SCALE);
				}
				else if(currentMap.equals("map2.txt")) {
					g.drawString(options[i] + " - Aldor Outskirts", 10, 150 + i * 30 * GamePanel.SCALE);
				}
			}
			else if(i == 1) {
				if(turnLimit == 75) {
					g.drawString(options[i] + " - Infinite", 10, 150 + i * 30 * GamePanel.SCALE);
				}
				else {
					g.drawString(options[i] + " - " + turnLimit, 10, 150 + i * 30 * GamePanel.SCALE);
				}
			}
			else {
				g.drawString(options[i], 10, 150 + i * 30 * GamePanel.SCALE);
			}
			
		}
		
	}
	
	//Sets game settings MAY NEED MODIFICATION
	private void setUpGame() {
		
		//Default God set
		Set<God> defaultGods = new TreeSet<>();
		defaultGods.add(God.ASSEMBLY);
		defaultGods.add(God.SWIFT);
		defaultGods.add(God.JAVA);
		defaultGods.add(God.PYTHON);
		
		//Add the players
		Scenario.addPlayer(new Player(defaultGods)); //Player 1
		Scenario.addPlayer(new Player(defaultGods)); //Player 2
		
		//Defaults to turn 0 to start the game (sets game length as well)
		Scenario.resetTurns();
		Scenario.setDuration(turnLimit);
		
		//Small scale battles are size 20
		Scenario.setMapSize(20);
		
		//Loads the map
		try {
			MapManager.loadDefault(currentMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void select() {
		
		if(currentChoice == 2) {
			
			//Set game settings
			setUpGame();
			
			//Change to the battle :)
			gsm.setState(GameStateManager.SCENARIOSTATE);
			
		}
		
	}

	public void keyPressed(int k) {
		
		if (k == KeyEvent.VK_SPACE) {
			select();
		}
		if (k == KeyEvent.VK_UP) {
			currentChoice--;
			if (currentChoice == -1) {
				currentChoice = options.length - 1;
			}
		}
		if (k == KeyEvent.VK_DOWN) {
			currentChoice++;
			if (currentChoice == options.length) {
				currentChoice = 0;
			}
		}
		if(k == KeyEvent.VK_LEFT) {
			if(currentChoice == 0) {
				currentMap = "map1.txt";
			}
			else if(currentChoice == 1) {
				if(turnLimit != 15) {
					turnLimit -= 15;
				}
			}
		}
		if(k == KeyEvent.VK_RIGHT) {
			if(currentChoice == 0) {
				currentMap = "map2.txt";
			}
			else if(currentChoice == 1) {
				if(turnLimit != 75) {
					turnLimit += 15;
				}
			}
		}
		if(k == KeyEvent.VK_BACK_SPACE) {
			gsm.setState(GameStateManager.MENUSTATE);
		}
		
	}
	
}

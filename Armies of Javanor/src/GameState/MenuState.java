package GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import Main.GamePanel;
import Utilities.Images;
import Utilities.MapManager;

public class MenuState extends GameState {
	
	private int currentChoice = 0;
	private String[] options = {
			"Start", "Load", "Quit"
	};
	
	private Color titleColor;
	private Font titleFont;
	
	private Font font;
	
	public MenuState (GameStateManager gsm) {
		
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
	
	public void draw (Graphics2D g) {
		
		//draw background
		g.drawImage(Images.titlescreen, 0, 0, null);
		
		//TEMP REMOVE L8ER
		Images.corrinSpeech(g);
		
		//draw title
		g.setColor (titleColor);
		g.setFont (titleFont);
		g.drawString ("Armies of Javanor", 80, 70);
		
		//draw menu options
		g.setFont (font);
		for (int i = 0; i < options.length; i++) {
			
			if (i == currentChoice) {
				g.setColor (Images.cayenneBlue);
			}
			
			else {
				g.setColor (Images.aldorRed);
			}
			
			g.drawString (options[i], 10, 150 + i * 30 * GamePanel.SCALE);
			
			//Reserving the program rights
			g.setColor (Color.BLUE);
			g.setFont (font);
			g.drawString ("2019 Adrian Recchi", 85, 425);
		}
		
	}
	
	private void select () {
		
		//Begin the game
		if(currentChoice == 0) {
			
			gsm.setState(GameStateManager.MENUOPTIONSSTATE);
			
		}
		
		//Load existing game
		else if(currentChoice == 1) {
			
			try {
				MapManager.load();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			gsm.setState(GameStateManager.SCENARIOSTATE);
			
		}
		
		//Quit game
		else if(currentChoice == options.length - 1) {
			
			System.exit(0);
			
		}
	}
	
	public void keyPressed (int k) {
		if (k == KeyEvent.VK_SPACE) {
			select ();
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
	}

}
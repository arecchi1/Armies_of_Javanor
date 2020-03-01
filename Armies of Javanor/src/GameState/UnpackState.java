package GameState;

import java.awt.Graphics2D;

public class UnpackState extends GameState {

	public UnpackState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
	}
	
	public void draw(Graphics2D g) {
		
		//Displays friendly message :)
		g.setColor(java.awt.Color.BLACK);
		g.fillRect(0, 0, Main.GamePanel.WIDTH, Main.GamePanel.HEIGHT);
		g.setFont(Utilities.Images.bigFont);
		g.setColor(java.awt.Color.WHITE);
		g.drawString("Unpacking Resources...", Main.GamePanel.WIDTH/3, Main.GamePanel.HEIGHT/3);
		
		//Sets to initializeState
		gsm.setState(GameStateManager.INITIALIZESTATE);
		
	}

	public void keyPressed(int k) {
		
		//Left empty intentionally
		
	}

}

package GameState;

import java.awt.Graphics2D;

import Utilities.Images;

public class InitializeState extends GameState {
	
	public InitializeState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
	}

	//Initializes all images by drawing them to nothing (causes lag)
	public void draw(Graphics2D g) {
		
		for(int i = 0; i < Images.imageList.size(); i++) {
			g.drawImage(Images.imageList.get(i), 0, 0, null);
		}
		
		//Reverts to the next stage
		gsm.setState(GameStateManager.MENUSTATE);
		
	}

	public void keyPressed(int k) {
		
		//Left empty intentionally
		
	}

}

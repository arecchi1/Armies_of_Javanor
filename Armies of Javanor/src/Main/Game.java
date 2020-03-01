package Main;

import javax.swing.JFrame;

public class Game {
	
	public static void main (String[] args) {
		
		Utilities.Images.initialize(); //Initialize images for game (may cause momentary lag at startup)
		Utilities.FrameRate.initialize(); //Begins the frameRate
		JFrame window = new JFrame ("Armies of Javanor");
		window.setContentPane (new GamePanel ());
		window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		window.setResizable (false);
		window.pack ();
		window.setVisible (true);
		//window.setLocationRelativeTo (null); //makes the window appear in center of screen
		
	}

}
package Utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import GameState.ScenarioState;
import Main.GamePanel;

public final class Images {

	//Image.SCALE_SMOOTH
	
	//Used in the brief lag fixing process
	public static java.util.ArrayList<Image> imageList = new java.util.ArrayList<>();
	
	//Custom Fonts
	public static Font gameFont;
	public static Font bigFont;
	
	//Custom Colors
	public static Color cayenneBlue;
	public static Color aldorRed;
	public static Color terrainPanel;
	public static Color terrainPanel2;
	public static Color terrainPanel3;
	public static Color terrainPanel4;
	public static Color selectPanel;
	public static Color movePanel;
	public static Color attackPanel;
	
	//Pathfinder
	private static String path = "images/";
	
	//Background images
	private static BufferedImage titlescreenDefault;
	private static BufferedImage summonscreenDefault;
	private static BufferedImage corrinDefault;
	
	//Tile images
	private static BufferedImage plainDefault;
	private static BufferedImage trackDefault;
	
	//Usable images (call these instead)
	public static Image titlescreen;
	public static Image summonscreen;
	public static Image corrin;
	public static Image plain;
	public static Image track;
	
	public static void initialize() {
		
		//Initialize Fonts
		gameFont = new Font(Font.DIALOG, Font.PLAIN, 20 * GamePanel.SCALE);
		bigFont = new Font(Font.DIALOG, Font.BOLD, 30 * GamePanel.SCALE);
		
		//Initialize Colors
		cayenneBlue = new Color(77, 77, 255);
		aldorRed = new Color(255, 77, 77);
		terrainPanel = new Color(77, 153, 255);
		terrainPanel2 = new Color(77, 200, 255);
		terrainPanel3 = new Color(255, 153, 77);
		terrainPanel4 = new Color(255, 200, 77);
		selectPanel = new Color(44, 160, 44);
		movePanel = new Color(153, 204, 77);
		attackPanel = new Color(153, 77, 0);
		
		//Initialize default images
		try {
			titlescreenDefault = ImageIO.read(new File(path + "titlescreen.png"));
			summonscreenDefault = ImageIO.read(new File(path + "starsummon.png"));
			corrinDefault = ImageIO.read(new File(path + "corrin.png"));
			plainDefault = ImageIO.read(new File(path + "plain.png"));
			trackDefault = ImageIO.read(new File(path + "track.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Initializes resized/usable images and registers them to the lag arraylist
		corrin = corrinDefault;
		imageList.add(corrin);
		titlescreen = registerBackground(titlescreenDefault);
		summonscreen = registerBackground(summonscreenDefault);
		plain = registerSquare(plainDefault);
		track = registerSquare(trackDefault);
		
	}
	
	//Registers background images
	private static Image registerBackground(BufferedImage imageDefault) {
		
		Image image = imageDefault.getScaledInstance(GamePanel.WIDTH, GamePanel.HEIGHT, Image.SCALE_SMOOTH);
		imageList.add(image);
		return image;
		
	}
	
	//Registers terrain andunit images
	private static Image registerSquare(BufferedImage imageDefault) {
		
		Image image = imageDefault.getScaledInstance(ScenarioState.SCALE, ScenarioState.SCALE, 0);
		imageList.add(image);
		return image;
		
	}
	
	public static void corrinSpeech(Graphics2D g) {
		
		g.setColor(Images.cayenneBlue);
		g.fillRect(0, 600, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.setColor(Color.BLUE);
		g.fillRect(0, 600, GamePanel.WIDTH, 10);
		g.setColor(Color.BLACK);
		g.drawLine(0, 600, GamePanel.WIDTH, 600);
		g.drawLine(0, 610, GamePanel.WIDTH, 610);
		g.drawImage(Images.corrin, 800, 330, null);
		
	}
	
}

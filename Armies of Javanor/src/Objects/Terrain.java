package Objects;

import java.awt.Image;
import java.io.Serializable;

import Utilities.EnumVariation;
import Utilities.EnumVariation.TerrainType;
import Utilities.Images;

@SuppressWarnings("serial")
public class Terrain implements Serializable {

	private String name;
	private TerrainType type;
	private double defense;
	private double cost;
	private boolean controlled;
	private boolean contested;
	private int currentControl;
	private int maxControl;
	private int ownership;
	private int fundGeneration;
	private transient Image image;
	
	public Terrain() {
		
		//Creates Plain terrain objects by default
		name = "Plain";
		type = TerrainType.PLAIN;
		defense = 0;
		cost = 1;
		noControl(this);
		image = Images.plain;
		
	}
	
	public Terrain clone() {
		
		Terrain temp = new Terrain();
		temp.name = this.name;
		temp.type = this.type;
		temp.defense = this.defense;
		temp.cost = this.cost;
		temp.controlled = this.controlled;
		temp.contested = this.contested;
		temp.currentControl = this.currentControl;
		temp.maxControl = this.maxControl;
		temp.ownership = this.ownership;
		temp.fundGeneration = this.fundGeneration;
		return temp;
		
	}
	
	private static void noControl(Terrain terrain) {
		
		terrain.controlled = false;
		terrain.contested = false;
		terrain.currentControl = EnumVariation.SAFE;
		terrain.maxControl = EnumVariation.SAFE + 1; //Prevents a possible error that makes non-capturable terrain capturable
		terrain.ownership = EnumVariation.SAFE;
		terrain.fundGeneration = EnumVariation.SAFE;
		
	}
	
	public String name() {
		
		return name;
		
	}
	
	public TerrainType type() {
		
		return type;
		
	}
	
	public double defense() {
		
		return defense;
		
	}
	
	public double cost() {
		
		return cost;
		
	}
	
	public boolean controlled() {
		
		return controlled;
		
	}
	
	public boolean contested() {
		
		return contested;
		
	}
	
	public int currentControl() {
		
		return currentControl;
		
	}
	
	public int maxControl() {
		
		return maxControl;
		
	}
	
	public int ownership() {
		
		return ownership;
		
	}
	
	public int fundGeneration() {
		
		return fundGeneration;
		
	}
	
	public Image graphic() {
		
		return image;
		
	}
	
	public void setGraphic(Image image) {
		
		this.image = image;
		
	}
	
	public void setContested(boolean contested) {
		
		this.contested = contested;
		
	}
	
	public void gainControl(int value) {
		
		currentControl += value;
		
		if(currentControl > maxControl) {
			currentControl = maxControl;
		}
		
	}
	
	public void resetControl() {
		
		currentControl = 0;
		
	}
	
	public void setType(TerrainType r_type) {
		
		type = r_type;
		
	}
	
	public void setOwnership(int owner) {
		
		ownership = owner;
		
	}
	
	//XXX Pre-generated Terrain
	public static Terrain newPlain() {
		
		return new Terrain();
		
	}
	
	public static Terrain newForest() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Forest";
		terrain.type = TerrainType.FOREST;
		terrain.defense = 0.2;
		terrain.cost = 2;
		noControl(terrain);
		
		return terrain;
		
	}
	
	public static Terrain newTrack() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Track";
		terrain.type = TerrainType.TRACK;
		terrain.defense = 0;
		terrain.cost = 1/1.5;
		noControl(terrain);
		terrain.image = Images.track;
		
		return terrain;
		
	}
	
	public static Terrain newSwamp() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Swamp";
		terrain.type = TerrainType.SWAMP;
		terrain.defense = 0.1;
		terrain.cost = 1/0.33;
		noControl(terrain);
		
		return terrain;
		
	}
	
	public static Terrain newMountain() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Mountain";
		terrain.type = TerrainType.MOUNTAIN;
		terrain.defense = 0.4;
		terrain.cost = Double.MAX_VALUE;
		noControl(terrain);
		
		return terrain;
		
	}
	
	public static Terrain newSea() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Sea";
		terrain.type = TerrainType.SEA;
		terrain.defense = 0;
		terrain.cost = Double.MAX_VALUE;
		noControl(terrain);
		
		return terrain;
		
	}
	
	public static Terrain newVillage() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Village";
		terrain.type = TerrainType.VILLAGE;
		terrain.defense = 0.2;
		terrain.cost = 1;
		terrain.controlled = true;
		terrain.contested = false;
		terrain.currentControl = 0;
		terrain.maxControl = 10;
		terrain.ownership = EnumVariation.SAFE; //Nobody owns it upon creation
		terrain.fundGeneration = 25;
		
		return terrain;
		
	}

	public static Terrain newTower() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Tower";
		terrain.type = TerrainType.TOWER;
		terrain.defense = 0.3;
		terrain.cost = 1;
		terrain.controlled = true;
		terrain.contested = false;
		terrain.currentControl = 0;
		terrain.maxControl = 15;
		terrain.ownership = EnumVariation.SAFE; //Nobody owns it upon creation
		terrain.fundGeneration = 75;
		
		return terrain;
		
	}
	
	public static Terrain newPortal() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Portal";
		terrain.type = TerrainType.PORTAL;
		terrain.defense = 0;
		terrain.cost = 1;
		terrain.controlled = true;
		terrain.contested = false;
		terrain.currentControl = 0;
		terrain.maxControl = 10;
		terrain.ownership = EnumVariation.SAFE; //Nobody owns it upon creation
		terrain.fundGeneration = 50;
		
		return terrain;
		
	}
	
	public static Terrain newBurningForest() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Burning Forest";
		terrain.type = TerrainType.BURNINGFOREST;
		terrain.defense = 0;
		terrain.cost = 1;
		noControl(terrain);
		
		return terrain;
		
	}
	
	public static Terrain newGrowingPlain() {
		
		Terrain terrain = new Terrain();
		
		terrain.name = "Growing Plain";
		terrain.type = TerrainType.GROWINGPLAIN;
		terrain.defense = 0;
		terrain.cost = 1;
		noControl(terrain);
		
		return terrain;
		
	}
	
}

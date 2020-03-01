package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import Objects.Scenario;
import Objects.Terrain;
import Objects.Unit;

public class MapManager {

	//Loading pre-set maps
	public static void loadDefault(String fileName) throws FileNotFoundException {
		
		Scanner scanner = new Scanner(new File(fileName));
		while(scanner.hasNextLine()) {
			String[] mapData = scanner.nextLine().split(",");
			//Plain is created by default
			//TrAck
			if(Integer.parseInt(mapData[3]) == 1) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newTrack());
			}
			//Forest
			if(Integer.parseInt(mapData[3]) == 2) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newForest());
			}
			//Village
			if(Integer.parseInt(mapData[3]) == 7) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newVillage());
			}
			//Swamp
			if(Integer.parseInt(mapData[3]) == 3) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newSwamp());
			}
			//Mountain
			if(Integer.parseInt(mapData[3]) == 4) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newMountain());
			}
			//Sea
			if(Integer.parseInt(mapData[3]) == 5) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newSea());
			}
			//Tower
			if(Integer.parseInt(mapData[3]) == 8) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newTower());
			}
			//Portal
			if(Integer.parseInt(mapData[3]) == 9 || Integer.parseInt(mapData[3]) == 14 || Integer.parseInt(mapData[3]) == 15) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setTerrain(Terrain.newPortal());
			}
			//Nomad chieftain placed by default (player 1)
			if(Integer.parseInt(mapData[2]) == 1) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setUnit(Unit.newNomadChieftain(0));
			}
			//Nomad chieftain placed by default (player 2)
			if(Integer.parseInt(mapData[2]) == 2) {
				Scenario.grid(Integer.parseInt(mapData[0]), Integer.parseInt(mapData[1])).setUnit(Unit.newNomadChieftain(1));
			}
		}
		scanner.close();
		
	}
	
	//Loading for saved game file
	public static void load() throws FileNotFoundException, IOException, ClassNotFoundException {
		
		Scenario temp = new Scenario();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("save_data.dat"));
		temp = (Scenario) in.readObject();
		in.close();
		Scenario.transfer(temp);
		
		//FIXME IMAGES NOT SAVED THROUGH SERIALIZEABLE!!! (not fully implemented yet)
		for(int i = 0; i < Scenario.mapSize(); i++) {
			for(int j = 0; j < Scenario.mapSize(); j++) {
				//Terrain graphic fixes
				switch(Scenario.grid(i, j).terrain().type()) {
				case BURNINGFOREST:
					break;
				case FOREST:
					break;
				case GROWINGPLAIN:
					break;
				case PLAIN:
					Scenario.grid(i, j).terrain().setGraphic(Images.plain);
					break;
				case PORTAL:
					break;
				case TOWER:
					break;
				case TRACK:
					Scenario.grid(i, j).terrain().setGraphic(Images.track);
					break;
				case VILLAGE:
					break;
				default:
					break;
				
				}
			}
		}
		
	}
	
	//Saves the current battle
	public static void save() throws FileNotFoundException, IOException  {
		
		Scenario temp = new Scenario();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("save_data.dat"));
		out.writeObject(temp);
		out.close();
		
	}
	
}

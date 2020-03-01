package GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Main.GamePanel;
import Objects.Scenario;
import Objects.StatusBlock;
import Objects.Terrain;
import Objects.Tile;
import Objects.Unit;
import Utilities.*;
import Utilities.EnumVariation.Ability;
import Utilities.EnumVariation.Condition;
import Utilities.EnumVariation.Phase;
import Utilities.EnumVariation.TerrainType;
import Utilities.EnumVariation.TileStatus;
import Utilities.EnumVariation.UnitID;
import Utilities.EnumVariation.UnitStatus;
import Utilities.EnumVariation.UnitType;

public class ScenarioState extends GameState {
	
	//Scale to use when magnifying graphics
	public static final int SCALE = 40;
	
	//GridManipulator interfaces
	private GridManipulator inRange; //Inflicts INRANGE tile status
	private GridManipulator fear; //Inflicts FEAR tile status
	private GridManipulator confusion; //Inflicts CONFUSION tile status
	private GridManipulator smoke; //Inflicts SMOKE tile status
	private GridManipulator morale; //Inflicts MORALE tile status
	private GridManipulator aura; //Inflicts AURA tile status
	private GridManipulator heal; //Heals the intended target (NOT FROM A STRUCTURE)
	private GridManipulator poisonTreatment; //Removes poison status from a unit
	private GridManipulator raiseDead; //Turns bone pile into a skeleton
	
	//Runnable tasks for thread pools
	private Runnable attackChecker; //Checks the attack range of a particular unit
	private Runnable attackClearer; //Removes attack range titles so they don't block out the grid
	private Runnable statusSurfer; //Surfs through grid and spawns off additional threads if status inflictors are found
	private Runnable statusUnitManager; //Inflicts & cures statuses on units if they are on a status tile
	private Runnable contestedChecker; //Checks if a structure is being contested by an enemy
	private Runnable captureChecker; //Checks progress being made on capturing
	private Runnable conditionRestore; //Restores all friendly units so they may take their actions again (every turn)
	private Runnable adjacentSeeker; //Seeks units that have abilities affecting adjacent spaces
	private Runnable spotSeeker; //Seeks units that have abilities affecting the tile they are standing on
	private Runnable structureSeeker; //Looks for structures belonging to the current player to seek out conditions
	private Runnable damageSeeker; //Looks for units that are in a state of taking damage (rotting, poison, etc)
	private Runnable deathSeeker; //Kills off units if hp is less than 0 and replaces them with bone pile
	private Runnable mapAlterer; //In moving phase looks for units that change terrain, otherwise changes terrain
	private Runnable mapAltererUndo; //Reverts burning forest back to forest and growing plain back to plain
	
	//Cursors for navigating the grid
	private int xCursor;
	private int yCursor;
	private int xTemp;
	private int yTemp;
	
	//Phase
	private Phase phase;
	
	//Player's turn
	private int player;
	public int player() { return player; }
	
	//Fatigue used by the unit currently moving (not part of its stats)
	private double fatigue;
	
	//Fake fighters used for making a battle forecast
	private Tile fakeFighter1;
	private Tile fakeFighter2;
	
	//Variable for temporary storing capture progress (if user decides to take back move and was capturing a structure)
	private int captureProgressTemp;
	
	//XXX Constructor
	public ScenarioState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		//Initiate cursors
		xCursor = 0;
		yCursor = 0;
		xTemp = 0;
		yTemp = 0;
		
		//Initiate phase
		phase = Phase.SELECT;
		
		//Initiate player turn
		player = 0;
		
		//Initiate fatigue
		fatigue = 0;
		
		//Initiate fake fighters
		fakeFighter1 = null;
		fakeFighter2 = null;
		
		//Initiate temporary capture progress
		captureProgressTemp = 0;
		
		//Declare GridManipulator interfaces
		inRange = (x, y, ownership)->{
			Scenario.grid(x, y).inflict(TileStatus.INRANGE, ownership);
		};
		fear = (x, y, ownership)->{
			Scenario.grid(x, y).inflict(TileStatus.FEAR, ownership);
		};
		confusion = (x, y, ownership)->{
			Scenario.grid(x, y).inflict(TileStatus.CONFUSION, ownership);
		};
		smoke = (x, y, ownership)->{
			Scenario.grid(x, y).inflict(TileStatus.SMOKE, ownership);
		};
		morale = (x, y, ownership)->{
			Scenario.grid(x, y).inflict(TileStatus.MORALE, ownership);
		};
		aura = (x, y, ownership)->{
			Scenario.grid(x, y).inflict(TileStatus.AURA, ownership);
		};
		heal = (x, y, ownership)->{
			if(Scenario.grid(x, y).unit().ownership() == ownership) {
				Scenario.grid(x, y).unit().heal(3);
			}
		};
		poisonTreatment = (x, y, ownership)->{
			if(Scenario.grid(x, y).unit().ownership() == ownership) {
				Scenario.grid(x, y).unit().cure(UnitStatus.POISON);
			}
		};
		raiseDead = (x, y, ownership)->{
			if(Scenario.grid(x, y).hasStatus(TileStatus.BONES)) {
				Scenario.grid(x, y).setUnit(Unit.newSkeleton(ownership));
				Scenario.grid(x, y).cure(TileStatus.BONES, EnumVariation.SAFE);
			}
			if(Scenario.grid(x, y).unit().ability() == Ability.ROT && Scenario.grid(x, y).unit().ownership() == ownership) {
				Scenario.grid(x, y).unit().heal(Scenario.grid(x, y).unit().maxHP());
			}
		};
		
		//Declare runnable tasks for thread pools
		attackChecker = ()->{
			rangeFinder(xCursor, yCursor, Scenario.grid(xCursor, yCursor).unit().minRange(), Scenario.grid(xCursor, yCursor).unit().maxRange(), EnumVariation.SAFE, inRange);
		};
		attackClearer = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Scenario.grid(i, j).cure(TileStatus.INRANGE, EnumVariation.SAFE);
				}
			}
		};
		statusSurfer = ()->{
			//Cleans the grid of status effects first
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Tile tile = Scenario.grid(i, j);
					//Clean the status effects on tiles
					for(int k = 0; k < Scenario.numPlayers(); k++) {
						tile.cure(TileStatus.FEAR, k);
						tile.cure(TileStatus.CONFUSION, k);
						tile.cure(TileStatus.SMOKE, k);
						tile.cure(TileStatus.MORALE, k);
						tile.cure(TileStatus.AURA, k);
					}
				}
			}
			
			//Applies statuses if found
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Unit unit = Scenario.grid(i, j).unit();
					final int k = i;
					final int l = j;
					//CONDITIONS FOR ADDITIONAL ABILITIES GO HERE
					if(unit.ability() == Ability.FEAR) {
						rangeFinder(k, l, 1, 2, unit.ownership(), fear);
					}
					if(unit.ability() == Ability.CONFUSION) {
						rangeFinder(k, l, 1, 2, unit.ownership(), confusion);
					}
					if(unit.ability() == Ability.SMOKE) {
						rangeFinder(k, l, 0, 2, unit.ownership(), smoke);
					}
					if(unit.ability() == Ability.MORALE) {
						rangeFinder(k, l, 1, 2, unit.ownership(), morale);
					}
					if(unit.ability() == Ability.NECROPOLIS) {
						rangeFinder(k, l, 1, 2, unit.ownership(), aura);
					}
					//if(Scenario.grid(i, j).unit().ability() == something) executor.submit(()->{rangeFinder(i, j, 1, Scenario.grid(i, j).unit().maxRange(), ownership, GridManipulator);});
					//executor.shutdown();
				}
			}
		};
		statusUnitManager = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Tile tile = Scenario.grid(i, j);
					if(tile.unit().id() != null) {
						
						//Fear
						boolean foundFear = false;
						for(int k = 0; k < tile.statusSize(); k++) {
							StatusBlock block = tile.statusBlock(k);
							if(block.status() == TileStatus.FEAR && block.ownership() != tile.unit().ownership()) {
								foundFear = true;
								break;
							}
						}
						if(foundFear) {
							if(tile.unit().ability() != Ability.FEARLESS) {
								tile.unit().inflict(UnitStatus.FEAR);
							}
						}
						else {
							tile.unit().cure(UnitStatus.FEAR);
						}
						
						//Confusion
						boolean foundConfusion = false;
						for(int k = 0; k < tile.statusSize(); k++) {
							StatusBlock block = tile.statusBlock(k);
							if(block.status() == TileStatus.CONFUSION && block.ownership() != tile.unit().ownership()) {
								foundConfusion = true;
								break;
							}
						}
						if(foundConfusion) {
							tile.unit().inflict(UnitStatus.CONFUSION);
						}
						else {
							tile.unit().cure(UnitStatus.CONFUSION);
						}
						
						//Smoke
						boolean foundSmoke = false;
						for(int k = 0; k < tile.statusSize(); k++) {
							StatusBlock block = tile.statusBlock(k);
							if(block.status() == TileStatus.SMOKE && block.ownership() == tile.unit().ownership()) {
								foundSmoke = true;
								break;
							}
						}
						if(foundSmoke) {
							tile.unit().inflict(UnitStatus.SMOKE);
						}
						else {
							tile.unit().cure(UnitStatus.SMOKE);
						}
						
						//Morale
						boolean foundMorale = false;
						for(int k = 0; k < tile.statusSize(); k++) {
							StatusBlock block = tile.statusBlock(k);
							if(block.status() == TileStatus.MORALE && block.ownership() == tile.unit().ownership()) {
								foundMorale = true;
								break;
							}
						}
						if(foundMorale) {
							tile.unit().inflict(UnitStatus.MORALE);
						}
						else {
							tile.unit().cure(UnitStatus.MORALE);
						}
						
					}
					
				}
			}
		};
		contestedChecker = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					boolean canCapture = Scenario.grid(i, j).unit().ability() == Ability.CAPTURE;
					boolean controllableStructure = Scenario.grid(i, j).terrain().controlled();
					int structureOwnership = Scenario.grid(i, j).terrain().ownership();
					if(canCapture && controllableStructure && structureOwnership != Scenario.grid(i, j).unit().ownership() && phase == Phase.SELECT) {
						Scenario.grid(i, j).terrain().setContested(true);
					}
					else{
						if(controllableStructure && Scenario.grid(i, j).unit().id() == null) {
							Scenario.grid(i, j).terrain().setContested(false);
							Scenario.grid(i, j).terrain().resetControl();
						}
					}
				}
			}
		};
		captureChecker = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					if(Scenario.grid(i, j).terrain().contested() && Scenario.grid(i, j).unit().ownership() == player) {
						Scenario.grid(i, j).terrain().gainControl(Scenario.grid(i, j).unit().currentHP());
					}
					if(Scenario.grid(i, j).terrain().currentControl() >= Scenario.grid(i, j).terrain().maxControl() && Scenario.grid(i, j).terrain().ownership() != Scenario.grid(i, j).unit().ownership()) {
						Scenario.grid(i, j).terrain().setOwnership(Scenario.grid(i, j).unit().ownership());
						Scenario.grid(i, j).terrain().setContested(false);
						Scenario.grid(i, j).unit().hurt(2); //Fixes glitch that heals unit immediately after capturing
					}
				}
			}
		};
		conditionRestore = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					if(Scenario.grid(i, j).unit().ownership() == player) {
						Scenario.grid(i, j).unit().setCondition(Condition.FULL);
					}
				}
			}
		};
		adjacentSeeker = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Unit unit = Scenario.grid(i, j).unit();
					
					//Heal friendly units
					if(unit.ability() == Ability.HEALBURY && unit.ownership() == player) {
						rangeFinder(i, j, 1, 1, unit.ownership(), poisonTreatment);
						rangeFinder(i, j, 1, 1, unit.ownership(), heal);
					}
					
					//Raise bone pile and heal friendly units with rot ability
					if(unit.ability() == Ability.RAISE && unit.ownership() == player) {
						rangeFinder(i, j, 1, 1, unit.ownership(), raiseDead);
					}
					
				}
			}
		};
		spotSeeker = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Unit unit = Scenario.grid(i, j).unit();
					
					//Bury bones
					if(unit.ability() == Ability.HEALBURY && unit.ownership() == player && Scenario.grid(i, j).hasStatus(TileStatus.BONES)) {
						unit.levelUp();
						Scenario.grid(i, j).cure(TileStatus.BONES, EnumVariation.SAFE);
					}
					
					//Aura tiles with bones become skeletons
					if(Scenario.grid(i, j).hasStatus(TileStatus.AURA, player)) {
						raiseDead.manipulate(i, j, player);
					}
					
				}
			}
		};
		structureSeeker = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Terrain terrain = Scenario.grid(i, j).terrain();
					Unit unit = Scenario.grid(i, j).unit();
					
					//Generates funds for every friendly structure found
					if(terrain.ownership() == player) {
						Scenario.player(player).earnFunds(terrain.fundGeneration());
					}
					
					//Heals friendly units 
					if(terrain.ownership() == unit.ownership() && unit.id() != null && terrain.ownership() == player) {
						unit.heal(2);
					}
					
					//Damages enemy units that do no have the capture ability (portal only)
					if(terrain.ownership() != unit.ownership() && unit.id() != null && terrain.ownership() == player && terrain.type() == TerrainType.PORTAL && unit.ability() != Ability.CAPTURE) {
						unit.hurt(2);
					}
					
				}
			}
		};
		damageSeeker = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Unit unit = Scenario.grid(i, j).unit();
					
					//Units that rot (skeletons/tombwalkers) lose health for every turn
					if((unit.ability() == Ability.ROT || unit.ability() == Ability.MORTFLESH) && unit.ownership() == player) {
						if(!Scenario.grid(i, j).hasStatus(TileStatus.AURA, player) || unit.ownership() != player) {
							unit.hurt(1);
						}
					}
					
					//IMPORTANT: Prevents fatal error from occurring that only runs loop once
					if(unit.id() != null) {
						//Units that are poisoned also lose health (stacks with rot)
						if(unit.hasStatus(UnitStatus.POISON) && unit.ownership() == player) {
							unit.hurt(1);
						}
					}
					
				}
			}
		};
		deathSeeker = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					if(Scenario.grid(i, j).unit().currentHP() <= 0 && Scenario.grid(i, j).unit().id() != null) {
						Scenario.grid(i, j).setUnit(Unit.noUnit());
						Scenario.grid(i, j).inflict(TileStatus.BONES, EnumVariation.SAFE);
						
						//Cleans up status effects in case unit was causing something
						ExecutorService cleaner = Executors.newCachedThreadPool();
						cleaner.submit(statusSurfer);
						cleaner.shutdown();
						try {
							cleaner.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		mapAlterer = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Tile tile = Scenario.grid(i, j);
					
					if(phase == Phase.MOVE) {
						//Burn down a forest
						if(tile.unit().ability() == Ability.PYROMANIAC && tile.terrain().type() == TerrainType.FOREST) {
							tile.setTerrain(Terrain.newBurningForest());
						}
						//Overgrow a plain
						else if(tile.unit().ability() == Ability.FORESTATION && tile.terrain().type() == TerrainType.PLAIN) {
							tile.setTerrain(Terrain.newGrowingPlain());
						}
					}
					
					else {
						//Convert burning forest to plain
						if(tile.terrain().type() == TerrainType.BURNINGFOREST) {
							tile.setTerrain(Terrain.newPlain());
						}
						//Convert growing plain to forest
						else if(tile.terrain().type() == TerrainType.GROWINGPLAIN) {
							tile.setTerrain(Terrain.newForest());
						}
					}
				}
			}
		};
		mapAltererUndo = ()->{
			for(int i = 0; i < Scenario.mapSize(); i++) {
				for(int j = 0; j < Scenario.mapSize(); j++) {
					Tile tile = Scenario.grid(i, j);
					
					//Revert burning forest to forest
					if(tile.terrain().type() == TerrainType.BURNINGFOREST) {
						tile.setTerrain(Terrain.newForest());
					}
					//Revert growing plain to plain
					else if(tile.terrain().type() == TerrainType.GROWINGPLAIN) {
						tile.setTerrain(Terrain.newPlain());
					}
				}
			}
		};
		
	}
	
	//XXX Graphics
	public void draw (Graphics2D g) {
		
		//Set font
		g.setFont(Images.gameFont);
		
		//Clear screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		
		for(int i = 0; i < Scenario.mapSize(); i++) {
			for(int j = 0; j < Scenario.mapSize(); j++) {
				
				//FIXME Displays the terrain graphics (Graphics are temporary until images are placed in)
				//Plain
				if(Scenario.grid(i, j).terrain().type() == TerrainType.PLAIN) {
					g.drawImage(Scenario.grid(i, j).terrain().graphic(), i*SCALE, j*SCALE, null);
				}
				
				//Track
				else if(Scenario.grid(i, j).terrain().type() == TerrainType.TRACK) {
					g.drawImage(Scenario.grid(i, j).terrain().graphic(), i*SCALE, j*SCALE, null);
				}
				
				//Forest
				else if(Scenario.grid(i, j).terrain().type() == TerrainType.FOREST) {
					g.setColor(Color.YELLOW);
					g.fillRect(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//Swamp
				else if(Scenario.grid(i,j).terrain().type() == TerrainType.SWAMP) {
					g.setColor(Color.GRAY);
					g.fillRect(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//Mountain
				else if(Scenario.grid(i,j).terrain().type() == TerrainType.MOUNTAIN) {
					g.setColor(Color.ORANGE);
					g.fillRect(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//Sea
				else if(Scenario.grid(i,j).terrain().type() == TerrainType.SEA) {
					g.setColor(Color.BLUE);
					g.fillRect(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//Village
				else if(Scenario.grid(i, j).terrain().type() == TerrainType.VILLAGE) {
					g.setColor(Color.RED);
					g.fillRect(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//Tower
				else if(Scenario.grid(i, j).terrain().type() == TerrainType.TOWER) {
					g.setColor(Color.MAGENTA);
					g.fillRect(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//Portal
				else if(Scenario.grid(i, j).terrain().type() == TerrainType.PORTAL) {
					g.setColor(Color.CYAN);
					g.fillRect(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//XXX Displays the unit graphics (Graphics are temporary until images are placed in)
				//Barbarian Chieftain
				if(Scenario.grid(i,j).unit().id() == UnitID.NOMADCHIEFTAIN) {
					g.setColor(Color.ORANGE);
					g.fillOval(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//FIXME RANDOM UNIT REMOVE L8ER
				else if(Scenario.grid(i,j).unit().id() != UnitID.NOMADCHIEFTAIN && Scenario.grid(i,j).unit().id() != null) {
					g.setColor(Color.BLUE);
					g.fillOval(i*SCALE, j*SCALE, SCALE, SCALE);
				}
				
				//XXX Displays the tile status graphics (Graphics are temporary until images are placed in)
				//FIXME NEEDS BETTER GRAPHICS
				if(Scenario.grid(i, j).hasStatus(TileStatus.INRANGE)) {
					g.setColor(Color.RED);
					g.fillOval(i*SCALE, j*SCALE, SCALE-10, SCALE-10);
				}
				
				//FIXME NEEDS BETTER GRAPHICS
				if(Scenario.grid(i, j).hasStatus(TileStatus.MORALE)) {
					g.setColor(Color.BLACK);
					g.fillOval(i*SCALE, j*SCALE, SCALE-10, SCALE-10);
				}
				
				//FIXME NEEDS BETTER GRAPHICS
				if(Scenario.grid(i, j).hasStatus(TileStatus.SMOKE)) {
					g.setColor(Color.GRAY);
					g.fillOval(i*SCALE, j*SCALE, SCALE-10, SCALE-10);
				}
				
				//FIXME NEEDS BETTER GRAPHICS
				if(Scenario.grid(i, j).hasStatus(TileStatus.CONFUSION)) {
					g.setColor(Color.MAGENTA);
					g.fillOval(i*SCALE, j*SCALE, SCALE-10, SCALE-10);
				}
				
				//FIXME NEEDS BETTER GRAPHICS
				if(Scenario.grid(i, j).hasStatus(TileStatus.FEAR)) {
					g.setColor(Color.CYAN);
					g.fillOval(i*SCALE, j*SCALE, SCALE-10, SCALE-10);
				}
				
				//FIXME NEEDS BETTER GRAPHICS
				if(Scenario.grid(i, j).hasStatus(TileStatus.AURA)) {
					g.setColor(Color.PINK);
					g.fillOval(i*SCALE, j*SCALE, SCALE-10, SCALE-10);
				}
				
				//FIXME NEEDS BETTER GRAPHICS
				if(Scenario.grid(i, j).hasStatus(TileStatus.BONES)) {
					g.setColor(Color.WHITE);
					g.fillOval(i*SCALE, j*SCALE, SCALE-10, SCALE-10);
				}
				
				//Displays unit ownership on map
				if(Scenario.grid(i, j).unit().id() != null) {
					if(Scenario.grid(i, j).unit().ownership() == 0) {
						g.setColor(Color.CYAN);
					}
					else if(Scenario.grid(i, j).unit().ownership() == 1) {
						g.setColor(Color.RED);
					}
					g.fillRect(i*SCALE, j*SCALE+25, SCALE/2, SCALE);
				}
				
			}
		}
		
		//Displays the cursor (Graphics are temporary until images are placed in)
		g.setColor(Color.BLUE);
		g.drawRect(xCursor*SCALE, yCursor*SCALE, SCALE, SCALE);
		
		//Graphics for unit/terrain information on right side of screen
		g.setColor(Images.terrainPanel);
		g.fillRect(Scenario.mapSize()*SCALE+10, 0, GamePanel.WIDTH, Scenario.mapSize()/2*SCALE);
		g.setColor(Images.terrainPanel2);
		g.fillRect(Scenario.mapSize()*SCALE+10+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 0, GamePanel.WIDTH, Scenario.mapSize()/2*SCALE);
		g.setColor(Images.terrainPanel3);
		g.fillRect(Scenario.mapSize()*SCALE+10, 160, GamePanel.WIDTH, Scenario.mapSize()/2*SCALE);
		g.setColor(Images.terrainPanel4);
		g.fillRect(Scenario.mapSize()*SCALE+10+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 160, GamePanel.WIDTH, Scenario.mapSize()/2*SCALE);
		if(phase == Phase.ATTACK) {
			g.setColor(Images.attackPanel);
			g.fillRect(Scenario.mapSize()*SCALE+10, Scenario.mapSize()/2*SCALE, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
		else {
			g.setColor(Images.movePanel);
			g.fillRect(Scenario.mapSize()*SCALE+10, Scenario.mapSize()/2*SCALE, GamePanel.WIDTH, GamePanel.HEIGHT);
			g.setColor(Images.selectPanel);
			g.fillRect(Scenario.mapSize()*SCALE+10+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, Scenario.mapSize()/2*SCALE, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
		g.setColor(Color.BLACK);
		g.fillRect(Scenario.mapSize()*SCALE, 0, 10, GamePanel.HEIGHT);
		g.drawRect(Scenario.mapSize()*SCALE, 0, 0, Scenario.mapSize()*SCALE);
		g.drawLine(Scenario.mapSize()*SCALE+10, 0, Scenario.mapSize()*SCALE+10, GamePanel.HEIGHT);
		g.drawRect(Scenario.mapSize()*SCALE+10, Scenario.mapSize()/2*SCALE, GamePanel.WIDTH-Scenario.mapSize()*SCALE, 0);
		g.drawRect(Scenario.mapSize()*SCALE+10+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 0, 0, Scenario.mapSize()/2*SCALE);
		for(int i = 0; i < 13; i++) {
			g.drawLine(Scenario.mapSize()*SCALE+10, 30*i+40, GamePanel.WIDTH, 30*i+40);
		}
		
		//Terrain Panel statistics
		Terrain tps = Scenario.grid(xCursor, yCursor).terrain();
		String[] terrainPanelStats = {
			"Terrain", "Defense", "Production Value", "Ownership", "Progress"
		}; 
		g.setColor(Color.WHITE);
		for(int i = 0; i < terrainPanelStats.length; i++) {
			g.drawString(terrainPanelStats[i] + ":", Scenario.mapSize()*SCALE+20, 30*i+30);
		}
		String[] terrainPanelStats2 = {
				tps.name(), tps.defense()+"", tps.fundGeneration()+"", tps.ownership()+"", tps.currentControl()+ " - " + tps.maxControl()
		};
		g.setColor(Color.WHITE);
		for(int i = 0; i< terrainPanelStats2.length; i++) {
			if(i == 1) {
				terrainPanelStats2[i] = Integer.toString(((int)(Double.parseDouble(terrainPanelStats2[i])*100)))+"%";
			}
			else if(i == 2 && Integer.parseInt(terrainPanelStats2[i]) == EnumVariation.SAFE) {
				g.setColor(Color.GRAY);
				terrainPanelStats2[i] = "N/A";
			}
			else if(i == 3) {
				if(!tps.controlled()) {
					g.setColor(Color.GRAY);
					terrainPanelStats2[i] = "N/A";
				}
				else if(tps.controlled() && tps.ownership() == 0) {
					g.setColor(Images.cayenneBlue);
					terrainPanelStats2[i] = "Cayenne";
				}
				else if(tps.controlled() && tps.ownership() == 1) {
					g.setColor(Images.aldorRed);
					terrainPanelStats2[i] = "Aldor";
				}
				else {
					g.setColor(Color.WHITE);
					terrainPanelStats2[i] = "Neutral";
				}
			}
			else if(i == 4) {
				if(!tps.contested()) {
					if(tps.controlled()) {
						g.setColor(Color.GREEN);
						terrainPanelStats2[i] = "Safe";
					}
					else {
						g.setColor(Color.GRAY);
						terrainPanelStats2[i] = "N/A";
					}
				}
				else {
					if(FrameRate.frame() % 2 == 0) {
						g.setColor(Color.WHITE);
					}
					else {
						g.setColor(Color.RED);
					}
				}
			}
			g.drawString(terrainPanelStats2[i], Scenario.mapSize()*SCALE+20+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 30*i+30);
		}
		
		//Army Panel statistics
		String[] armyPanelStats = {
				"Player Turn", "Current Funds", "Funds Generated", "Income", "Friendly Villages", "Friendly Towers", "Friendly Portals", "Day"
		};
		for(int i = 0; i < armyPanelStats.length; i++) {
			g.setColor(Color.WHITE);
			g.drawString(armyPanelStats[i] + ":", Scenario.mapSize()*SCALE+20, 30*i+180);
			
			String currentInfo = "";
			
			if(i == 0) {
				if(player == 0) {
					g.setColor(Images.cayenneBlue);
					currentInfo = "Cayenne";
				}
				else {
					g.setColor(Images.aldorRed);
					currentInfo = "Aldor";
				}
			}
			else if(i == 1) {
				g.setColor(Color.WHITE);
				currentInfo = Integer.toString(Scenario.player(player).funds());
			}
			else if(i == 2) {
				g.setColor(Color.WHITE);
				currentInfo = Integer.toString(Scenario.player(player).fundsGenerated());
			}
			//Similar code duplication incoming for next 4
			else if(i == 3) {
				int grandTotal = 0;
				for(int k = 0; k < Scenario.mapSize(); k++) {
					for(int l = 0; l < Scenario.mapSize(); l++) {
						if(Scenario.grid(k, l).terrain().ownership() == player) {
							grandTotal += Scenario.grid(k, l).terrain().fundGeneration();
						}
					}
				}
				currentInfo = Integer.toString(grandTotal);
			}
			else if(i == 4) {
				int grandTotal = 0;
				for(int k = 0; k < Scenario.mapSize(); k++) {
					for(int l = 0; l < Scenario.mapSize(); l++) {
						if(Scenario.grid(k, l).terrain().ownership() == player && Scenario.grid(k, l).terrain().type() == TerrainType.VILLAGE) {
							grandTotal++;
						}
					}
				}
				currentInfo = Integer.toString(grandTotal);
			}
			else if(i == 5) {
				int grandTotal = 0;
				for(int k = 0; k < Scenario.mapSize(); k++) {
					for(int l = 0; l < Scenario.mapSize(); l++) {
						if(Scenario.grid(k, l).terrain().ownership() == player && Scenario.grid(k, l).terrain().type() == TerrainType.TOWER) {
							grandTotal++;
						}
					}
				}
				currentInfo = Integer.toString(grandTotal);
			}
			else if(i == 6) {
				int grandTotal = 0;
				for(int k = 0; k < Scenario.mapSize(); k++) {
					for(int l = 0; l < Scenario.mapSize(); l++) {
						if(Scenario.grid(k, l).terrain().ownership() == player && Scenario.grid(k, l).terrain().type() == TerrainType.PORTAL) {
							grandTotal++;
						}
					}
				}
				currentInfo = Integer.toString(grandTotal);
			}
			else if(i == 7) {
				if(Scenario.duration() == 75) {
					currentInfo = Integer.toString(Scenario.currentTurn()) + " / ???";
				}
				else {
					currentInfo = Integer.toString(Scenario.currentTurn()) + " / " + Integer.toString(Scenario.duration());
				}
			}
			
			g.drawString(currentInfo, Scenario.mapSize()*SCALE+20+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 30*i+180);
		}
		
		//Unit Panel statistics
		Unit ups = Scenario.grid(xCursor, yCursor).unit();
		String[] unitPanelStatistics = {
			"Combatant", "Type", "Strength", "Range", "Speed", "Ability", "Condition", "Allegiance"
		};
		if(phase != Phase.ATTACK) {
			g.setColor(Color.BLACK);
			g.drawLine(Scenario.mapSize()*SCALE+210, 0, Scenario.mapSize()*SCALE+210, GamePanel.HEIGHT);
			for(int i = 0; i < 21; i++) {
				g.drawLine(Scenario.mapSize()*SCALE+10, 30*i+40, GamePanel.WIDTH, 30*i+40);
			}
			g.setColor(Color.WHITE);
			for(int i = 0; i < unitPanelStatistics.length; i++) {
				if(i == 6 && phase == Phase.MOVE) {
					unitPanelStatistics[i] = "Fatigue";
				}
				g.drawString(unitPanelStatistics[i] + ":", Scenario.mapSize()*SCALE+20, 30*i+420);
			}
			String currentInfo = "";
			for(int i = 0; i < unitPanelStatistics.length; i++) {
				g.setColor(Color.WHITE);
				if(ups.id() == null) {
					g.setColor(Color.GRAY);
					currentInfo = "N/A";
				}
				else {
					switch(i) {
					case 0:
						currentInfo = ups.name();
						break;
					case 1:
						switch(ups.type()) {
						case SKIRMISHER:
							currentInfo = "Skirmisher";
							break;
						case WARRIOR:
							currentInfo = "Warrior";
							break;
						case RANGER:
							currentInfo = "Ranger";
							break;
						case MAGE:
							currentInfo = "Mage";
							break;
						case FLIER:
							currentInfo = "Flier";
							break;
						case HELPER:
							currentInfo = "Helper";
							break;
						case TITAN:
							currentInfo = "Titan";
							break;
						}
						break;
					case 2:
						currentInfo = Integer.toString(ups.currentHP()) + " - " + Integer.toString(ups.maxHP());
						break;
					case 3:
						currentInfo = Integer.toString(ups.minRange()) + " - " + Integer.toString(ups.maxRange());
						break;
					case 4:
						currentInfo = Integer.toString((int)ups.speed());
						break;
					case 5:
						g.setColor(Color.MAGENTA);
						if(ups.ability() == null) {
							g.setColor(Color.WHITE);
							currentInfo = "No Ability";
						}
						else {
							switch(ups.ability()) {
							case CAPTURE:
								currentInfo = "Capture";
								break;
							case HEALBURY:
								currentInfo = "Miracle";
								break;
							case RAISE:
								currentInfo = "Corpse Animator";
								break;
							case FEAR:
								currentInfo = "Abomination";
								break;
							case CONFUSION:
								currentInfo = "Hallucination";
								break;
							case SMOKE:
								currentInfo = "Smoke Screen";
								break;
							case ANTIAIR:
								currentInfo = "Sky Scourge";
								break;
							case ROT:
								currentInfo = "Rotting Flesh";
								break;
							case FEARLESS:
								currentInfo = "Unstoppable Destiny";
								break;
							case MORALE:
								currentInfo = "Spirit of Light";
								break;
							case PYROMANIAC:
								currentInfo = "Pyromaniac";
								break;
							case FORESTATION:
								currentInfo = "Forest Haven";
								break;
							case OCEANDWELLER:
								currentInfo = "Marine Raider";
								break;
							case FORESTDWELLER:
								currentInfo = "Forest Raider";
								break;
							case FORESTPREDATOR:
								currentInfo = "Forest Predator";
								break;
							case MORTFLESH:
								currentInfo = "Plague Flesh";
								break;
							case NECROPOLIS:
								currentInfo = "Necropolis";
								break;
							case BIOSYSTEM:
								currentInfo = "Biologically Immortal";
								break;
							case VENOM:
								currentInfo = "Venom Touch";
								break;
							}
						}
						break;
					case 6:
						if(phase == Phase.SELECT) {
							if(ups.ownership() == player) {
								switch(ups.condition()) {
								case FULL:
									g.setColor(Color.GREEN);
									currentInfo = "Ready to move";
									break;
								case HALF:
									g.setColor(Color.YELLOW);
									currentInfo = "Ready to fight";
									break;
								case DONE:
									g.setColor(Color.RED);
									currentInfo = "Exhausted";
									break;
								}
							}
							else {
								g.setColor(Color.WHITE);
								currentInfo = "Disobedient";
							}
						}
						else {
							currentInfo = String.format("%.2f", fatigue);
						}
						break;
					case 7:
						if(ups.ownership() == 0) {
							g.setColor(Images.cayenneBlue);
							currentInfo = "Cayenne";
						}
						else {
							g.setColor(Images.aldorRed);
							currentInfo = "Aldor";
						}
						break;
					}
				}
				g.drawString(currentInfo, Scenario.mapSize()*SCALE+20+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 30*i+420);
			}
		}
		else { //XXX Battle Forecast
			if(Scenario.grid(xCursor, yCursor).unit().id() != null) {
				fakeFighter1 = Scenario.grid(xTemp, yTemp).clone();
				fakeFighter2 = Scenario.grid(xCursor, yCursor).clone();
				
				if(canFight(fakeFighter1, fakeFighter2, Math.abs(xCursor - xTemp) + Math.abs(yCursor - yTemp), false)) {
					int[] dmgNumbers = fight(fakeFighter1, fakeFighter2, Math.abs(xCursor - xTemp) + Math.abs(yCursor - yTemp)); //make this return int array with dmg results
					g.setColor(Color.BLACK);
					g.drawLine(Scenario.mapSize()*SCALE+210, 0, Scenario.mapSize()*SCALE+210, GamePanel.HEIGHT);
					for(int i = 0; i < 21; i++) {
						g.drawLine(Scenario.mapSize()*SCALE+10, 30*i+40, GamePanel.WIDTH, 30*i+40);
					}
					//Your stats
					g.setColor(Color.GREEN);
					g.drawString("You", Scenario.mapSize()*SCALE+20, 420);
					String[] yourStats = {
						fakeFighter1.unit().name(),
						"Type",
						fakeFighter1.unit().currentHP()+" - "+fakeFighter1.unit().maxHP(),
						Integer.toString(dmgNumbers[0]),
						"ADVNEUDIS"
					};
					for(int i = 0; i < yourStats.length; i++) {
						g.setColor(Color.WHITE);
						if(i == 1) {
							switch(fakeFighter1.unit().type()) {
							case SKIRMISHER:
								yourStats[i] = "Skirmisher";
								break;
							case WARRIOR:
								yourStats[i] = "Warrior";
								break;
							case RANGER:
								yourStats[i] = "Ranger";
								break;
							case MAGE:
								yourStats[i] = "Mage";
								break;
							case FLIER:
								yourStats[i] = "Flier";
								break;
							case HELPER:
								yourStats[i] = "Helper";
								break;
							case TITAN:
								yourStats[i] = "Titan";
								break;
							}
						}
						else if(i == 2 && fakeFighter1.unit().currentHP() <= 0) {
							g.setColor(Color.RED);
							yourStats[i] = "Dead";
						}
						else if(i == 4) {
							int adv = fakeFighter1.unit().advantage(fakeFighter2.unit());
							if(adv == 2 || adv == 1) {
								g.setColor(Color.GREEN);
								yourStats[i] = "Advantage";
							}
							else if(adv == 0 || adv == -1) {
								g.setColor(Color.YELLOW);
								yourStats[i] = "Fair";
							}
							else if(adv == -2) {
								g.setColor(Color.RED);
								yourStats[i] = "Disadvantage";
							}
						}
						g.drawString(yourStats[i], Scenario.mapSize()*SCALE+20, 30*i+450);
					}
					//Enemy stats
					g.setColor(Color.RED);
					g.drawString("Foe", Scenario.mapSize()*SCALE+20+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 420);
					String[] foeStats = {
							fakeFighter2.unit().name(),
							"Type",
							fakeFighter2.unit().currentHP()+" - "+fakeFighter2.unit().maxHP(),
							Integer.toString(dmgNumbers[1]),
							"ADVNEUDIS"
					};
					for(int i = 0; i < foeStats.length; i++) {
						g.setColor(Color.WHITE);
						if(i == 1) {
							switch(fakeFighter1.unit().type()) {
							case SKIRMISHER:
								foeStats[i] = "Skirmisher";
								break;
							case WARRIOR:
								foeStats[i] = "Warrior";
								break;
							case RANGER:
								foeStats[i] = "Ranger";
								break;
							case MAGE:
								foeStats[i] = "Mage";
								break;
							case FLIER:
								foeStats[i] = "Flier";
								break;
							case HELPER:
								foeStats[i] = "Helper";
								break;
							case TITAN:
								foeStats[i] = "Titan";
								break;
							}
						}
						else if(i == 2 && fakeFighter2.unit().currentHP() <= 0) {
							g.setColor(Color.RED);
							foeStats[i] = "Dead";
						}
						else if(i == 4) {
							int adv = fakeFighter2.unit().advantage(fakeFighter1.unit());
							if(adv == 2 || adv == 1) {
								g.setColor(Color.GREEN);
								foeStats[i] = "Advantage";
							}
							else if(adv == 0 || adv == -1) {
								g.setColor(Color.YELLOW);
								foeStats[i] = "Fair";
							}
							else if(adv == -2) {
								g.setColor(Color.RED);
								foeStats[i] = "Disadvantage";
							}
						}
						g.drawString(foeStats[i], Scenario.mapSize()*SCALE+20+(GamePanel.WIDTH-Scenario.mapSize()*SCALE)/2, 30*i+450);
					}
				}
			}
		}
		
	}
	
	//XXX Controls
	public void keyPressed (int k) {
		
		if(k == KeyEvent.VK_DOWN) {
			
			if(yCursor < Scenario.mapSize()-1) {
				
				if(phase == Phase.MOVE) {
					if(canMove(xCursor, yCursor+1)) {
						moveForward(xCursor, yCursor+1);
						yCursor++;
					}
				}
				else {
					yCursor++;
				}
				
			}
			
		}
		
		if(k == KeyEvent.VK_UP) {
			
			if(yCursor > 0) {
				
				if(phase == Phase.MOVE) {
					if(canMove(xCursor, yCursor-1)) {
						moveForward(xCursor, yCursor-1);
						yCursor--;
					}
				}
				else {
					yCursor--;
				}
				
			}
			
		}
		
		if(k == KeyEvent.VK_RIGHT) {
			
			if(xCursor < Scenario.mapSize()-1) {
				
				if(phase == Phase.MOVE) {
					if(canMove(xCursor+1, yCursor)) {
						moveForward(xCursor+1, yCursor);
						xCursor++;
					}
				}
				else {
					xCursor++;
				}
				
			}
			
		}
		
		if(k == KeyEvent.VK_LEFT) {
			
			if(xCursor > 0) {
				
				if(phase == Phase.MOVE) {
					if(canMove(xCursor-1, yCursor)) {
						moveForward(xCursor-1, yCursor);
						xCursor--;
					}
				}
				else {
					xCursor--;
				}
				
			}
			
		}
		
		if(k == KeyEvent.VK_SPACE) {
			
			performAction();
			
		}
		
		if(k == KeyEvent.VK_ENTER) {
			
			optionsEnd();
			
		}
		
		if(k == KeyEvent.VK_BACK_SPACE) {
			
			if(phase == Phase.MOVE) {
				
				moveBack();
				phase = Phase.SELECT;
				ExecutorService executor = Executors.newCachedThreadPool();
				executor.submit(mapAltererUndo);
				executor.shutdown();
				
			}
			
			else if(phase == Phase.ATTACK) {
				
				xTemp = 0;
				yTemp = 0;
				phase = Phase.SELECT;
				
			}
			
		}
		
		if(k == KeyEvent.VK_ESCAPE) {
			
			optionsSave();
			
		}
		
		//Thread pool called for tasks
		try {
			serviceThreadPool();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	//Selects location, confirms movement, and executes attack
	private void performAction() {
		
		if(phase == Phase.SELECT) {
			
			boolean check1 = Scenario.grid(xCursor, yCursor).unit().ownership() == player;
			boolean check2 = Scenario.grid(xCursor, yCursor).unit().condition() == Condition.FULL;
			boolean check3 = Scenario.grid(xCursor, yCursor).unit().condition() == Condition.HALF;
			boolean check4 = Scenario.grid(xCursor, yCursor).terrain().ownership() == player;
			boolean check5 = Scenario.grid(xCursor, yCursor).terrain().type() == TerrainType.PORTAL;
			boolean check6 = Scenario.grid(xCursor, yCursor).unit().id() == null;
			
			if(check1 && check2) {
				xTemp = xCursor;
				yTemp = yCursor;
				
				//Fear is inflicted on the unit you are trying to move
				if(Scenario.grid(xCursor, yCursor).unit().hasStatus(UnitStatus.FEAR)) {
					fatigue = 0;
				}
				else {
					fatigue = Scenario.grid(xCursor, yCursor).unit().speed();
				}
				captureProgressTemp = Scenario.grid(xCursor, yCursor).terrain().currentControl();
				phase = Phase.MOVE;
			}
			
			else if(check1 && check3) {
				xTemp = xCursor;
				yTemp = yCursor;
				
				phase = Phase.ATTACK;
				ExecutorService executor = Executors.newCachedThreadPool();
				executor.submit(attackChecker);
				executor.shutdown();
			}
			
			//Selected a portal that belongs to current player
			else if(check4 && check5 && check6) {
				((SummonState) gsm.getState(GameStateManager.SUMMONSTATE)).link(Scenario.grid(xCursor, yCursor), player);
				gsm.setState(GameStateManager.SUMMONSTATE);
			}
			
		}
		
		else if(phase == Phase.MOVE) {
			
			xTemp = 0;
			yTemp = 0;
			fatigue = 0;
			Scenario.grid(xCursor, yCursor).unit().setCondition(Condition.HALF);
			phase = Phase.SELECT;
			
		}
		
		else if(phase == Phase.ATTACK) {
			
			int distance = Math.abs(xCursor - xTemp) + Math.abs(yCursor - yTemp);
			
			//Unit is in-range and meets the fighting requirements
			if(Scenario.grid(xCursor, yCursor).hasStatus(TileStatus.INRANGE) && canFight(Scenario.grid(xTemp, yTemp), Scenario.grid(xCursor, yCursor), distance, false)) {
				fight(Scenario.grid(xTemp, yTemp), Scenario.grid(xCursor, yCursor), distance);
				Scenario.grid(xTemp, yTemp).unit().setCondition(Condition.DONE);
				xTemp = 0;
				yTemp = 0;
				phase = Phase.SELECT;
			}
			
		}
		
	}
	
	//Checks to see if movement is possible by the unit
	private boolean canMove(int xFuture, int yFuture) {
		
		boolean returner = false;
		Terrain terrain = Scenario.grid(xFuture, yFuture).terrain();
		double terrainCost = terrain.cost();
		Unit unit = Scenario.grid(xCursor, yCursor).unit();
		
		//Fliers always move at same speed
		if(unit.type() == UnitType.FLIER) {
			terrainCost = 1;
		}
		//Units with pyromaniac or forestation abilities (pyrelord & ent) move at cost of 1 through forests and same with forest dwellers and predators
		if((unit.ability() == Ability.FORESTATION || unit.ability() == Ability.PYROMANIAC || unit.ability() == Ability.FORESTDWELLER || unit.ability() == Ability.FORESTPREDATOR) && (terrain.type() == TerrainType.FOREST)) {
			terrainCost = 1;
		}
		//Units with marine raider abilities can move through sea at only 1 cost
		if(unit.ability() == Ability.OCEANDWELLER && terrain.type() == TerrainType.SEA) {
			terrainCost = 1;
		}
		
		//Movement is possible
		if((fatigue - terrainCost >= 0) && (Scenario.grid(xFuture, yFuture).unit().id() == null)) {
			
			fatigue -= terrainCost;
			returner = true;
			
		}
		
		return returner;
		
	}
	
	//Moves a unit to the desired coordinates
	private void moveForward(int x, int y) {
		
		Scenario.grid(x, y).setUnit(Scenario.grid(xCursor, yCursor).unit());
		Scenario.grid(xCursor, yCursor).setUnit(Unit.noUnit());
		
	}
	
	//Resets the unit to its original location if a mistake was made while moving
	private void moveBack() {
		
		fatigue = 0;
		
		//Unit actually moved from its original spot
		if((xCursor != xTemp) || (yCursor != yTemp)) {
			Scenario.grid(xTemp, yTemp).setUnit(Scenario.grid(xCursor, yCursor).unit());
			Scenario.grid(xCursor, yCursor).setUnit(Unit.noUnit());
			Scenario.grid(xTemp, yTemp).terrain().gainControl(captureProgressTemp);
		}
		
	}
	
	//XXX Conditions for starting a fight (assuming the defender is already within the attacker's range)
	private boolean canFight(Tile attackerPosition, Tile defenderPosition, int distance, boolean countering) {
		
		Unit attacker = attackerPosition.unit();
		Unit defender = defenderPosition.unit();
		boolean returner = false;
		
		//Attacker is not on the same team as defender and attacker is not targeting nothing
		if(attacker.ownership() != defender.ownership() && defender.id() != null) {
			
			//Attacker is attacking close-combat
			if(distance == 1) {
			
				//Defender is a flying type
				if(defender.type() == UnitType.FLIER) {
					
					//Attacker is the one starting the fight
					if(countering == false) {
						
						//Attacker can fight the flier (attacker is either a ranger, mage, or has an anti-air ability (hobgoblin, etc))
						if(attacker.type() == UnitType.FLIER || attacker.type() == UnitType.RANGER || attacker.type() == UnitType.MAGE || attacker.ability() == Ability.ANTIAIR) {
							returner = true;
						}
						
					}
					
					//Attacker is retaliating
					else {
						
						if(attacker.minRange() <= distance && attacker.maxRange() >= distance) {
							returner = true;
						}
						
					}
					
				}
				
				//Defender is not a flier
				else {
					
					if(attacker.minRange() <= distance && attacker.maxRange() >= distance) {
						return true;
					}
					
				}
			
			}
			
			//Attacker is attacking from a distance
			else if(distance > 1) {
				
				//Attacker is able to meet distance requirements (requirements are guarenteed to meet if attacker is engaging, may not meet requirements if attacker is counter-attacking
				if(attacker.minRange() <= distance && attacker.maxRange() >= distance) {
					
					//Defender is not shielded in a smoke screen and not hiding in a forest
					if(!defender.hasStatus(UnitStatus.SMOKE) && defenderPosition.terrain().type() != TerrainType.FOREST) {
						returner = true;
					}
					
				}
				
			}
			
		}
		
		return returner;
		
	}
	
	//XXX Units engage in a fight
	private int[] fight(Tile attacker, Tile defender, int distance) {
		
		int[] dmgResults = new int[2]; //Damage results to be returned when calling this method
		int dmgGiven = 0; //Attacker attacking Defender
		int dmgTaken = 0; //Defender attacking Attacker
		double advantageAttackerMultiplier = 0.5; //0.5 is equal fight, higher if the unit attacking has advantage, lower otherwise
		double advantageDefenderMultiplier = 0.5; //0.5 is equal fight, higher if the unit attacking has advantage, lower otherwise
		double defenseAttackerMultiplier = 1; //1 means no defense is given, it should only go below 1 if defense is present
		double defenseDefenderMultiplier = 1; //1 means no defense is given, it should only go below 1 if defense is present
		final int GREAT = 2;
		final int GOOD = 1;
		final int NEUTRAL = 0;
		final int BAD = -1;
		final int TERRIBLE = -2;
		
		//Defense multiplier assignment
		//Defender is confused so defense of his tile is ignored
		if(defender.unit().hasStatus(UnitStatus.CONFUSION)) {
			defenseDefenderMultiplier = 1;
		}
		else {
			defenseDefenderMultiplier = 1 - defender.terrain().defense();
		}
		//Attacker is confused so defense of his tile is ignored
		if(attacker.unit().hasStatus(UnitStatus.CONFUSION)) {
			defenseAttackerMultiplier = 1;
		}
		else {
			defenseAttackerMultiplier = 1 - attacker.terrain().defense();
		}
		
		//Attacker multiplier
		if(attacker.unit().advantage(defender.unit()) == GREAT) {
			advantageAttackerMultiplier = 0.9;
		}
		else if(attacker.unit().advantage(defender.unit()) == GOOD) {
			advantageAttackerMultiplier = 0.9;
		}
		else if(attacker.unit().advantage(defender.unit()) == NEUTRAL) {
			advantageAttackerMultiplier = 0.5;
		}
		else if(attacker.unit().advantage(defender.unit()) == BAD) {
			advantageAttackerMultiplier = 0.5;
		}
		else if(attacker.unit().advantage(defender.unit()) == TERRIBLE) {
			advantageAttackerMultiplier = 0.25;
		}
		
		//Defender multiplier
		if(defender.unit().type() == UnitType.FLIER && attacker.unit().ability() == Ability.ANTIAIR) {
			advantageDefenderMultiplier = 0.5;
		}
		else if(defender.unit().advantage(attacker.unit()) == GREAT) {
			advantageDefenderMultiplier = 0.9;
		}
		else if(defender.unit().advantage(attacker.unit()) == GOOD) {
			advantageDefenderMultiplier = 0.9;
		}
		else if(defender.unit().advantage(attacker.unit()) == NEUTRAL) {
			advantageDefenderMultiplier = 0.5;
		}
		else if(defender.unit().advantage(attacker.unit()) == BAD) {
			advantageDefenderMultiplier = 0.5;
		}
		else if(defender.unit().advantage(attacker.unit()) == TERRIBLE) {
			advantageDefenderMultiplier = 0.25;
		}
		
		//Attacker engages
		dmgGiven = (int) ((attacker.unit().currentHP() * advantageAttackerMultiplier) * defenseDefenderMultiplier);
		if(dmgGiven < 1) {
			dmgGiven = 1;
		}
		defender.unit().hurt(dmgGiven);
		dmgResults[0] = dmgGiven;
		//Attacker can poison and defender is not protected against this (BioSystem)
		if((attacker.unit().ability() == Ability.FORESTPREDATOR || attacker.unit().ability() == Ability.MORTFLESH || attacker.unit().ability() == Ability.VENOM) && defender.unit().ability() != Ability.BIOSYSTEM) {
			defender.unit().inflict(UnitStatus.POISON);
		}
		
		//Defender engages (assuming he's not already dead)
		if(defender.unit().currentHP() > 0) {
			
			//Defender can counter-attack
			if(canFight(defender, attacker, distance, true)) {
				dmgTaken = (int) ((defender.unit().currentHP() * advantageDefenderMultiplier) * defenseAttackerMultiplier);
				if(dmgTaken < 1) {
					dmgTaken = 1;
				}
				attacker.unit().hurt(dmgTaken);
				dmgResults[1] = dmgTaken;
				//Defender can poison and attacker is not protected against this (BioSystem)
				if((defender.unit().ability() == Ability.FORESTPREDATOR || defender.unit().ability() == Ability.MORTFLESH || defender.unit().ability() == Ability.VENOM) && attacker.unit().ability() != Ability.BIOSYSTEM) {
					attacker.unit().inflict(UnitStatus.POISON);
				}
			}
			
		}
		
		//Defender is dead and attacker gains a level
		else {
			
			attacker.unit().levelUp();
			
		}
		
		//Attacker is dead and defender gains a level
		if(attacker.unit().currentHP() <= 0) {
			
			defender.unit().levelUp();
			
		}
		
		return dmgResults;
		
	}
	
	private void beginNextPlayer() throws InterruptedException {
		
		if(player < Scenario.numPlayers() - 1) {
			player++;
		}
		else {
			player = 0;
			Scenario.nextTurn();
		}
		
		//XXX Game ends due to conditions being met
		if(Scenario.currentTurn() > Scenario.duration() || defeated()) {
			gsm.setState(GameStateManager.MENUSTATE);
		}
		
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(captureChecker);
		service.submit(conditionRestore);
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		ExecutorService service2 = Executors.newCachedThreadPool();
		service2.submit(spotSeeker);
		service2.submit(damageSeeker);
		service2.shutdown();
		service2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		ExecutorService service3 = Executors.newCachedThreadPool();
		service3.submit(adjacentSeeker);
		service3.submit(structureSeeker);
		service3.shutdown();
		service3.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
	}
	
	private boolean defeated() {
		
		boolean returner = true;
		
		for(int i = 0; i < Scenario.mapSize(); i++) {
			for(int j = 0; j < Scenario.mapSize(); j++) {
				if(Scenario.grid(i, j).unit().ownership() == player) {
					returner = false;
				}
				if(Scenario.grid(i, j).terrain().type() == TerrainType.PORTAL && Scenario.grid(i, j).terrain().ownership() == player) {
					returner = false;
				}
			}
		}
		
		return returner;
		
	}
	
	//XXX Occurrences that happen every time a key is pressed (not exactly a thread)
	public void serviceThreadPool() throws InterruptedException {
		
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(statusSurfer);
		service.submit(contestedChecker);
		if(phase != Phase.ATTACK) {
			service.submit(attackClearer);
		}
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		ExecutorService service2 = Executors.newCachedThreadPool();
		service2.submit(mapAlterer);
		service2.submit(statusUnitManager);
		service2.submit(deathSeeker);
		service2.shutdown();
		service2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
	}
	
	//Advanced algorithm that finds spaces on a grid that are within a specified range
	private void rangeFinder(int k, int l, int minRange, int range, int ownership, GridManipulator manipulator) {
		
		//Specify the areas of the grid to search within
		int iLower = k - range;
		if(iLower < 0)
			iLower = 0;
		int iUpper = k + range;
		if(iUpper >= Scenario.mapSize())
			iUpper = Scenario.mapSize() - 1;
		int jLower = l - range;
		if(jLower < 0)
			jLower = 0;
		int jUpper = l + range;
		if(jUpper >= Scenario.mapSize())
			jUpper = Scenario.mapSize() - 1;
		
		//Surfs through the specified region within the grid
		for(int i = iLower; i <= iUpper; i++) {
			for(int j = jLower; j <= jUpper; j++) {
				if(((Math.abs(i - k) + Math.abs(j - l)) <= range) && (Math.abs(i - k) + Math.abs(j - l) >= minRange)) {
					manipulator.manipulate(i, j, ownership);
				}
			}
		}
		
	}
	
	private void optionsEnd() {
		
		if(phase == Phase.SELECT) {
			Scenario.player(player).earnFunds(120); //$120 automatically given for player ending their turn
			try {
				beginNextPlayer(); //Next turn begins here
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void optionsSave() {
		
		try {
			MapManager.save();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
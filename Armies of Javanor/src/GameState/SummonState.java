package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import Main.GamePanel;
import Objects.Scenario;
import Objects.Tile;
import Objects.Unit;
import Utilities.EnumVariation;
import Utilities.EnumVariation.God;
import Utilities.Images;

public class SummonState extends GameState {
	
	private Tile tile; //Reference to a tile from the grid
	private int player; //The current player who is summoning
	private int yourFavour; //Used for favour checking conditions
	
	//Lists used for displaying units that player wants to summon
	private List<Unit> listAssembly;
	private List<Unit> listSwift;
	private List<Unit> listJava;
	private List<Unit> listPython;
	
	//Current selection by the user
	private int currentChoice;
	private List<Unit> currentList;

	public SummonState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		//Initialize default values for the tile and player references
		tile = null;
		player = EnumVariation.SAFE;
		yourFavour = EnumVariation.SAFE;
		
		//Initialize Assembly list
		listAssembly = new ArrayList<>();
		listAssembly.add(Unit.newNomad(EnumVariation.SAFE));
		listAssembly.add(Unit.newBull(EnumVariation.SAFE));
		listAssembly.add(Unit.newCrocodile(EnumVariation.SAFE));
		listAssembly.add(Unit.newChocobo(EnumVariation.SAFE));
		listAssembly.add(Unit.newThief(EnumVariation.SAFE));
		listAssembly.add(Unit.newAviansie(EnumVariation.SAFE));
		listAssembly.add(Unit.newRiftGazer(EnumVariation.SAFE));
		
		//Initialize Swift list
		listSwift = new ArrayList<>();
		listSwift.add(Unit.newSpearman(EnumVariation.SAFE));
		listSwift.add(Unit.newCleric(EnumVariation.SAFE));
		listSwift.add(Unit.newDrone(EnumVariation.SAFE));
		listSwift.add(Unit.newElf(EnumVariation.SAFE));
		listSwift.add(Unit.newCrusader(EnumVariation.SAFE));
		listSwift.add(Unit.newValkyrie(EnumVariation.SAFE));
		listSwift.add(Unit.newArachne(EnumVariation.SAFE));
		listSwift.add(Unit.newFaery(EnumVariation.SAFE));
		listSwift.add(Unit.newHero(EnumVariation.SAFE));
		listSwift.add(Unit.newOracle(EnumVariation.SAFE));
		
		//Initialize Java list
		listJava = new ArrayList<>();
		listJava.add(Unit.newWerewolf(EnumVariation.SAFE));
		listJava.add(Unit.newDeathGuide(EnumVariation.SAFE));
		listJava.add(Unit.newSkeleton(EnumVariation.SAFE));
		listJava.add(Unit.newDarkKnight(EnumVariation.SAFE));
		listJava.add(Unit.newLilith(EnumVariation.SAFE));
		listJava.add(Unit.newSorcerer(EnumVariation.SAFE));
		listJava.add(Unit.newTombWalker(EnumVariation.SAFE));
		listJava.add(Unit.newGargoyle(EnumVariation.SAFE));
		listJava.add(Unit.newLich(EnumVariation.SAFE));
		listJava.add(Unit.newPyrophidian(EnumVariation.SAFE));
		
		//Initialize Python list
		listPython = new ArrayList<>();
		listPython.add(Unit.newGoblin(EnumVariation.SAFE));
		listPython.add(Unit.newGoblinPriest(EnumVariation.SAFE));
		listPython.add(Unit.newDraconian(EnumVariation.SAFE));
		listPython.add(Unit.newHobgoblin(EnumVariation.SAFE));
		listPython.add(Unit.newDraconianLegionary(EnumVariation.SAFE));
		listPython.add(Unit.newDraconianShaman(EnumVariation.SAFE));
		listPython.add(Unit.newHomunculus(EnumVariation.SAFE));
		listPython.add(Unit.newOrk(EnumVariation.SAFE));
		listPython.add(Unit.newSeadevil(EnumVariation.SAFE));
		listPython.add(Unit.newDragon(EnumVariation.SAFE));
		
		//Initialize default values for choice and current list
		reset();
		
	}
	
	public void link(Tile tile, int player) {
		
		this.tile = tile;
		this.player = player;
		setYourFavour();
		
	}
	
	//XXX Graphics
	public void draw(Graphics2D g) {
		
		//Set font
		g.setFont(Images.gameFont);
		
		//Clear screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		
		//Fill background with star image thing
		g.drawImage(Images.summonscreen, 0, 0, null);
		
		//Create outer boarder
		int BOARDERSCALER = 200;
		g.setColor(Color.BLACK);
		g.drawRect(BOARDERSCALER, BOARDERSCALER, GamePanel.WIDTH-BOARDERSCALER*2, GamePanel.HEIGHT-BOARDERSCALER*2);
		
		//Create inner boarder
		BOARDERSCALER += 20;
		g.drawRect(BOARDERSCALER, BOARDERSCALER, GamePanel.WIDTH-BOARDERSCALER*2, GamePanel.HEIGHT-BOARDERSCALER*2);
		
		//Fill inside of boarders
		//Left boarder
		BOARDERSCALER -= 20;
		g.fillRect(BOARDERSCALER, 0, 21, GamePanel.HEIGHT);
		//Right boarder
		BOARDERSCALER += 20;
		g.fillRect(GamePanel.WIDTH-BOARDERSCALER, 0, 21, GamePanel.HEIGHT);
		//Top boarder
		BOARDERSCALER -= 20;
		g.fillRect(BOARDERSCALER, BOARDERSCALER, GamePanel.WIDTH-BOARDERSCALER*2, 21);
		//Bottom boarder
		BOARDERSCALER += 20;
		g.fillRect(BOARDERSCALER, GamePanel.HEIGHT-BOARDERSCALER, GamePanel.WIDTH-BOARDERSCALER*2, 21);
		
		//Fill inner colours with affiliate colours
		//Cayenne
		if(player == 0) {
			g.setColor(Images.cayenneBlue);
		}
		//Aldor
		else {
			g.setColor(Images.aldorRed);
		}
		g.fillRect(BOARDERSCALER, 0, GamePanel.WIDTH-BOARDERSCALER*2, BOARDERSCALER-20);
		g.fillRect(BOARDERSCALER, GamePanel.HEIGHT-BOARDERSCALER+20, GamePanel.WIDTH-BOARDERSCALER*2, BOARDERSCALER-20);
		
		//Fill inner summon picture
		if(!canSummon()) {
			g.setColor(Color.GRAY);
		}
		else {
			g.setColor(Color.GREEN);
		}
		g.fillRect(BOARDERSCALER, BOARDERSCALER, GamePanel.WIDTH-BOARDERSCALER*2, GamePanel.HEIGHT-BOARDERSCALER*2);
		
		//Create line of division at bottom of screen
		g.setColor(Color.BLACK);
		g.drawLine(GamePanel.WIDTH/2, GamePanel.HEIGHT-BOARDERSCALER, GamePanel.WIDTH/2, GamePanel.HEIGHT);
		
		//Create 8 lines at bottom of screen
		for(int i = 0; i < 8; i++) {
			g.drawLine(BOARDERSCALER, 25*(i+1)+(GamePanel.HEIGHT-BOARDERSCALER+20), GamePanel.WIDTH-BOARDERSCALER, 25*(i+1)+(GamePanel.HEIGHT-BOARDERSCALER+20));
		}
		
		//Display unit information
		g.setColor(Color.WHITE);
		String[] unitInfo = {
				"Name:", "Type:", "Strength:", "Range:", "Speed:", "Ability:", "Favour:", "Cost:"
		};
		for(int i = 0; i < unitInfo.length; i++) {
			g.drawString(unitInfo[i], BOARDERSCALER+10, 25*(i+1)+(GamePanel.HEIGHT-BOARDERSCALER+20)-5);
		}
		String[] listInfo = {
				currentList.get(currentChoice).name(),
				"TYPE",
				Integer.toString(currentList.get(currentChoice).maxHP()),
				currentList.get(currentChoice).minRange() + " - " + currentList.get(currentChoice).maxRange(),
				Integer.toString((int)currentList.get(currentChoice).speed()),
				"ABILITY",
				Integer.toString(currentList.get(currentChoice).favour()),
				Integer.toString(currentList.get(currentChoice).cost())
		};
		for(int i = 0; i < listInfo.length; i++) {
			g.setColor(Color.YELLOW);
			if(listInfo[i].equals("ABILITY")) {
				if(currentList.get(currentChoice).ability() != null) {
					g.setColor(Color.MAGENTA);
					switch(currentList.get(currentChoice).ability()) {
					case CAPTURE:
						listInfo[i] = "Capture";
						break;
					case HEALBURY:
						listInfo[i] = "Miracle";
						break;
					case RAISE:
						listInfo[i] = "Corpse Animator";
						break;
					case FEAR:
						listInfo[i] = "Abomination";
						break;
					case CONFUSION:
						listInfo[i] = "Hallucination";
						break;
					case SMOKE:
						listInfo[i] = "Smoke Screen";
						break;
					case ANTIAIR:
						listInfo[i] = "Sky Scourge";
						break;
					case ROT:
						listInfo[i] = "Rotting Flesh";
						break;
					case FEARLESS:
						listInfo[i] = "Unstoppable Destiny";
						break;
					case MORALE:
						listInfo[i] = "Spirit of Light";
						break;
					case PYROMANIAC:
						listInfo[i] = "Pyromaniac";
						break;
					case FORESTATION:
						listInfo[i] = "Forest Haven";
						break;
					case OCEANDWELLER:
						listInfo[i] = "Marine Raider";
						break;
					case FORESTDWELLER:
						listInfo[i] = "Forest Raider";
						break;
					case FORESTPREDATOR:
						listInfo[i] = "Forest Predator";
						break;
					case MORTFLESH:
						listInfo[i] = "Plague Flesh";
						break;
					case NECROPOLIS:
						listInfo[i] = "Necropolis";
						break;
					case BIOSYSTEM:
						listInfo[i] = "Biologically Immortal";
						break;
					case VENOM:
						listInfo[i] = "Venom Touch";
						break;
					}
				}
				else {
					g.setColor(Color.GRAY);
					listInfo[i] = "No Ability";
				}
			}
			else if(listInfo[i].equals("TYPE")) {
				switch(currentList.get(currentChoice).type()) {
				case SKIRMISHER:
					listInfo[i] = "Skirmisher";
					break;
				case WARRIOR:
					listInfo[i] = "Warrior";
					break;
				case RANGER:
					listInfo[i] = "Ranger";
					break;
				case MAGE:
					listInfo[i] = "Mage";
					break;
				case FLIER:
					listInfo[i] = "Flier";
					break;
				case HELPER:
					listInfo[i] = "Helper";
					break;
				case TITAN:
					listInfo[i] = "Titan";
					break;
				}
			}
			else if(i == 6) {
				if(yourFavour >= currentList.get(currentChoice).favour()) {
					g.setColor(Color.GREEN);
				}
				else {
					g.setColor(Color.RED);
				}
			}
			else if(i == 7) {
				if(Scenario.player(player).funds() >= currentList.get(currentChoice).cost()) {
					g.setColor(Color.GREEN);
				}
				else {
					g.setColor(Color.RED);
				}
			}
			g.drawString(listInfo[i], GamePanel.WIDTH/2+10,25*(i+1)+(GamePanel.HEIGHT-BOARDERSCALER+20)-5);
		}
		
		//Draw your funds and favour at top of screen
		g.setColor(Color.ORANGE);
		g.drawString("Funds - " + Scenario.player(player).funds(), BOARDERSCALER+10, BOARDERSCALER/5);
		g.drawString("Favour - " + yourFavour, BOARDERSCALER+10, BOARDERSCALER/5+20);
		
		//Draw God text on top of screen
		g.setFont(Images.bigFont);
		g.setColor(Color.YELLOW);
		String titleText = "";
		if(currentList.get(currentChoice).god() == God.ASSEMBLY) {
			titleText = "Assembly";
		}
		else if(currentList.get(currentChoice).god() == God.SWIFT) {
			titleText = "Swift";
		}
		else if(currentList.get(currentChoice).god() == God.JAVA) {
			titleText = "Java";
		}
		else if(currentList.get(currentChoice).god() == God.PYTHON) {
			titleText = "Python";
		}
		g.drawString(titleText, (GamePanel.WIDTH/2)-(titleText.length()*7), BOARDERSCALER/2);
		
	}
	
	private void reset() {
		
		currentChoice = 0;
		currentList = listAssembly;
		if(player != EnumVariation.SAFE) {
			setYourFavour();
		}
		
	}
	
	private boolean canSummon() {
		
		//Return value
		boolean returner = false;
		
		//Player stats
		int funds = Scenario.player(player).funds();
		int favour = 0;
		switch(currentList.get(currentChoice).god()) {
		case ASSEMBLY:
			favour = Scenario.player(player).favourAssembly();
			break;
		case JAVA:
			favour = Scenario.player(player).favourJava();
			break;
		case PYTHON:
			favour = Scenario.player(player).favourPython();
			break;
		case SWIFT:
			favour = Scenario.player(player).favourSwift();
			break;
		}
		
		//Unit stats
		int cost = currentList.get(currentChoice).cost();
		int requiredFavour = currentList.get(currentChoice).favour();
		
		if(funds >= cost && favour >= requiredFavour) {
			returner = true;
		}
		
		return returner;
		
	}
	
	private void confirmSummon() {
		
		if(canSummon()) {
			
			switch(currentList.get(currentChoice).god()) {
			case ASSEMBLY:
				if(Scenario.player(player).favourAssembly() == currentList.get(currentChoice).favour())
					Scenario.player(player).raiseFavour(God.ASSEMBLY);
				break;
			case JAVA:
				if(Scenario.player(player).favourJava() == currentList.get(currentChoice).favour()) {
					Scenario.player(player).raiseFavour(God.JAVA);
					Scenario.player(player).raiseFavour(God.ASSEMBLY);
				}
				break;
			case PYTHON:
				if(Scenario.player(player).favourPython() == currentList.get(currentChoice).favour()) {
					Scenario.player(player).raiseFavour(God.PYTHON);
					Scenario.player(player).raiseFavour(God.ASSEMBLY);
				}
				break;
			case SWIFT:
				if(Scenario.player(player).favourSwift() == currentList.get(currentChoice).favour()) {
					Scenario.player(player).raiseFavour(God.SWIFT);
					Scenario.player(player).raiseFavour(God.ASSEMBLY);
				}
				break;
			}
			
			Scenario.player(player).removeFunds(currentList.get(currentChoice).cost());
			tile.setUnit(currentList.get(currentChoice).clone());
			tile.unit().setOwnership(player);
			reset();
			try {
				((ScenarioState) gsm.getState(GameStateManager.SCENARIOSTATE)).serviceThreadPool();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gsm.setState(GameStateManager.SCENARIOSTATE);
			
		}
		
	}
	
	private void setYourFavour() {
		
		//Changes the displayed favour level based on what unit is being displayed
		if(currentList.get(currentChoice).god() == God.ASSEMBLY) {
			yourFavour = Scenario.player(player).favourAssembly();
		}
		else if(currentList.get(currentChoice).god() == God.SWIFT) {
			yourFavour = Scenario.player(player).favourSwift();
		}
		else if(currentList.get(currentChoice).god() == God.JAVA) {
			yourFavour = Scenario.player(player).favourJava();
		}
		else if(currentList.get(currentChoice).god() == God.PYTHON) {
			yourFavour = Scenario.player(player).favourPython();
		}
		
	}

	public void keyPressed(int k) {
		
		if(k == KeyEvent.VK_DOWN) {
			
			currentChoice = 0;
			
			if(currentList == listAssembly) {
				currentList = listSwift;
			}
			else if(currentList == listSwift) {
				currentList = listJava;
			}
			else if(currentList == listJava) {
				currentList = listPython;
			}
			else if(currentList == listPython) {
				currentList = listAssembly;
			}
			
		}
		
		if(k == KeyEvent.VK_UP) {
			
			currentChoice = 0;
			
			if(currentList == listAssembly) {
				currentList = listPython;
			}
			else if(currentList == listSwift) {
				currentList = listAssembly;
			}
			else if(currentList == listJava) {
				currentList = listSwift;
			}
			else if(currentList == listPython) {
				currentList = listJava;
			}
			
		}
		
		if(k == KeyEvent.VK_RIGHT) {
			
			if(currentChoice < currentList.size() - 1) {
				currentChoice++;
			}
			else {
				currentChoice = 0;
			}
			
		}
		
		if(k == KeyEvent.VK_LEFT) {
			
			if(currentChoice == 0) {
				currentChoice = currentList.size() - 1;
			}
			else {
				currentChoice--;
			}
			
		}
		
		if(k == KeyEvent.VK_SPACE) {
			
			confirmSummon();
			
		}
		
		if(k == KeyEvent.VK_BACK_SPACE) {
			
			reset();
			gsm.setState(GameStateManager.SCENARIOSTATE);
			
		}
		
		setYourFavour();
		
	}
	
}

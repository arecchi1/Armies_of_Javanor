package Objects;

import java.awt.Image;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import Utilities.EnumVariation;
import Utilities.EnumVariation.Ability;
import Utilities.EnumVariation.Condition;
import Utilities.EnumVariation.God;
import Utilities.EnumVariation.UnitID;
import Utilities.EnumVariation.UnitStatus;
import Utilities.EnumVariation.UnitType;

@SuppressWarnings("serial")
public class Unit implements Serializable {

	private UnitID id;
	private String name;
	private UnitType type;
	private int currentHP;
	private int maxHP;
	private int minRange;
	private int maxRange;
	private int cost;
	private double speed;
	private Condition condition;
	private int ownership;
	private int favour;
	private God god;
	private Ability ability;
	private Set<UnitStatus> status;
	private transient Image image;
	
	public Unit() {
		
		id = null;
		name = null;
		type = null;
		currentHP = EnumVariation.SAFE;
		maxHP = EnumVariation.SAFE;
		minRange = EnumVariation.SAFE;
		maxRange = EnumVariation.SAFE;
		cost = EnumVariation.SAFE;
		speed = EnumVariation.SAFE;
		condition = null;
		ownership = EnumVariation.SAFE;
		favour = EnumVariation.SAFE;
		god = null;
		ability = null;
		status = null;
		
	}
	
	public Unit clone() {
		
		Unit temp = new Unit();
		temp.id = this.id;
		temp.name = this.name;
		temp.type = this.type;
		temp.currentHP = this.currentHP;
		temp.maxHP = this.maxHP;
		temp.minRange = this.minRange;
		temp.maxRange = this.maxRange;
		temp.cost = this.cost;
		temp.speed = this.speed;
		temp.condition = this.condition;
		temp.ownership = this.ownership;
		temp.favour = this.favour;
		temp.god = this.god;
		temp.ability = this.ability;
		temp.status = new TreeSet<>();
		temp.status.addAll(this.status);
		return temp;
		
	}
	
	public UnitID id() {
		
		return id;
		
	}
	
	public String name() {
		
		return name;
		
	}
	
	public UnitType type() {
		
		return type;
		
	}
	
	public int currentHP() {
		
		return currentHP;
		
	}
	
	public int maxHP() {
		
		return maxHP;
		
	}
	
	public int minRange() {
		
		return minRange;
		
	}
	
	public int maxRange() {
		
		return maxRange;
		
	}
	
	public int cost() {
		
		return cost;
		
	}
	
	public double speed() {
		
		return speed;
		
	}
	
	public Condition condition() {
		
		return condition;
		
	}
	
	public int ownership() {
		
		return ownership;
		
	}
	
	public int favour() {
		
		return favour;
		
	}
	
	public God god() {
		
		return god;
		
	}
	
	public Ability ability() {
		
		return ability;
		
	}
	
	public boolean hasStatus(UnitStatus r_status) {
		
		return status.contains(r_status);
		
	}
	
	public void heal(int value) {
		
		currentHP += value;
		
		if(currentHP > maxHP) {
			currentHP = maxHP;
		}
		
	}
	
	public void hurt(int value) {
		
		currentHP -= value;
		
		if(currentHP < 0) {
			currentHP = 0;
		}
		
	}
	
	public void levelUp() {
		
		maxHP++;
		currentHP++;
		
	}
	
	public void setOwnership(int ownership) {
		
		this.ownership = ownership;
		
	}
	
	public void setCondition(Condition condition) {
		
		this.condition = condition;
		
	}
	
	public void inflict(UnitStatus r_status) {
		
		status.add(r_status);
		
	}
	
	public void cure(UnitStatus r_status) {
		
		if(this.hasStatus(r_status)) {
			status.remove(r_status);
		}
		
	}
	
	//Unit advantage comparision (values can be -2, -1, 0, 1, 2) (higher number means greater advantage)
	public int advantage(Unit unit) {
		
		int returner = 0;
		
		//Skirmisher engaging
		if(this.type() == UnitType.SKIRMISHER) {
			
			if(unit.type() == UnitType.SKIRMISHER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.WARRIOR) {
				returner = -2;
			}
			else if(unit.type() == UnitType.RANGER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.MAGE) {
				returner = -1;
			}
			else if(unit.type() == UnitType.FLIER) {
				returner = -2;
			}
			else if(unit.type() == UnitType.HELPER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.TITAN) {
				returner = -1;
			}
			
		}
		
		//Warrior engaging
		else if(this.type() == UnitType.WARRIOR) {
			
			if(unit.type() == UnitType.SKIRMISHER) {
				returner = 2;
			}
			else if(unit.type() == UnitType.WARRIOR) {
				returner = 0;
			}
			else if(unit.type() == UnitType.RANGER) {
				returner = 2;
			}
			else if(unit.type() == UnitType.MAGE) {
				returner = -1;
			}
			else if(unit.type() == UnitType.FLIER) {
				//EXCEPTION: An anti-air warrior can deal 90% dmg on flier, but will receive 50% upon counter-attack as opposed to 90%
				if(this.ability() == Ability.ANTIAIR) {
					returner = 1;
				}
				else{
					returner = -2;
				}
			}
			else if(unit.type() == UnitType.HELPER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.TITAN) {
				returner = -1;
			}
			
		}
		
		//Ranger engaging
		else if(this.type() == UnitType.RANGER) {
			
			if(unit.type() == UnitType.SKIRMISHER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.WARRIOR) {
				returner = -2;
			}
			else if(unit.type() == UnitType.RANGER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.MAGE) {
				returner = 2;
			}
			else if(unit.type() == UnitType.FLIER) {
				returner = 1;
			}
			else if(unit.type() == UnitType.HELPER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.TITAN) {
				returner = -1;
			}
			
		}
		
		//Mage engaging
		else if(this.type() == UnitType.MAGE) {
			
			if(unit.type() == UnitType.SKIRMISHER) {
				returner = 1;
			}
			else if(unit.type() == UnitType.WARRIOR) {
				returner = 1;
			}
			else if(unit.type() == UnitType.RANGER) {
				returner = -2;
			}
			else if(unit.type() == UnitType.MAGE) {
				returner = -2;
			}
			else if(unit.type() == UnitType.FLIER) {
				returner = 1;
			}
			else if(unit.type() == UnitType.HELPER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.TITAN) {
				returner = -1;
			}
			
		}
		
		//Flier engaging
		else if(this.type() == UnitType.FLIER) {
			
			if(unit.type() == UnitType.SKIRMISHER) {
				returner = 2;
			}
			else if(unit.type() == UnitType.WARRIOR) {
				returner = 2;
			}
			else if(unit.type() == UnitType.RANGER) {
				returner = -1;
			}
			else if(unit.type() == UnitType.MAGE) {
				returner = 1;
			}
			else if(unit.type() == UnitType.FLIER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.HELPER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.TITAN) {
				returner = -1;
			}
			
		}
		
		//Helper engaging
		else if(this.type() == UnitType.HELPER) {
			
			if(unit.type() == UnitType.SKIRMISHER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.WARRIOR) {
				returner = 0;
			}
			else if(unit.type() == UnitType.RANGER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.MAGE) {
				returner = 0;
			}
			else if(unit.type() == UnitType.FLIER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.HELPER) {
				returner = 0;
			}
			else if(unit.type() == UnitType.TITAN) {
				returner = 0;
			}
			
		}
		
		//Titan engaging
		else if(this.type() == UnitType.TITAN) {
			
			if(unit.type() == UnitType.SKIRMISHER) {
				returner = 1;
			}
			else if(unit.type() == UnitType.WARRIOR) {
				returner = 1;
			}
			else if(unit.type() == UnitType.RANGER) {
				returner = 1;
			}
			else if(unit.type() == UnitType.MAGE) {
				returner = 1;
			}
			else if(unit.type() == UnitType.FLIER) {
				returner = 1;
			}
			else if(unit.type() == UnitType.HELPER) {
				returner = 1;
			}
			else if(unit.type() == UnitType.TITAN) {
				returner = 1;
			}
			
		}
		
		//Overrides everything else if the attacker has a morale boost
		if(this.hasStatus(UnitStatus.MORALE)) {
			returner = 1;
		}
		
		return returner;
		
	}
	
	//TODO static classes for spawning pre-generated units
	
	public static Unit noUnit() {
		
		return new Unit();
		
	}
	
	public static Unit newNomadChieftain(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.NOMADCHIEFTAIN;
		unit.name = "Nomad Chieftain";
		unit.type = UnitType.SKIRMISHER;
		unit.currentHP = 10;
		unit.maxHP = 10;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 0;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 0;
		unit.god = God.ASSEMBLY;
		unit.ability = Ability.CAPTURE;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newNomad(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.NOMAD;
		unit.name = "Nomad";
		unit.type = UnitType.SKIRMISHER;
		unit.currentHP = 5;
		unit.maxHP = 5;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 50;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.ASSEMBLY;
		unit.ability = Ability.CAPTURE;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newBull(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.BULL;
		unit.name = "Bull";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 175;
		unit.speed = 4;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.ASSEMBLY;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newCrocodile(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.CROCODILE;
		unit.name = "Crocodile";
		unit.type = UnitType.SKIRMISHER;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 150;
		unit.speed = 3;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.ASSEMBLY;
		unit.ability = Ability.OCEANDWELLER; //TODO OceanDweller ability needs to be programmed
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newChocobo(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.CHOCOBO;
		unit.name = "Chocobo";
		unit.type = UnitType.SKIRMISHER;
		unit.currentHP = 15;
		unit.maxHP = 15;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 150;
		unit.speed = 10;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.ASSEMBLY;
		unit.ability = Ability.FORESTDWELLER;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newThief(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.THIEF;
		unit.name = "Thief";
		unit.type = UnitType.RANGER;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 2;
		unit.maxRange = 3;
		unit.cost = 200;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.ASSEMBLY;
		unit.ability = Ability.VENOM;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newAviansie(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.AVIANSIE;
		unit.name = "Aviansie";
		unit.type = UnitType.FLIER;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 400;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.ASSEMBLY;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newRiftGazer(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.RIFTGAZER;
		unit.name = "Rift Gazer";
		unit.type = UnitType.MAGE;
		unit.currentHP = 30;
		unit.maxHP = 30;
		unit.minRange = 3;
		unit.maxRange = 5;
		unit.cost = 450;
		unit.speed = 4;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 4;
		unit.god = God.ASSEMBLY;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newSpearman(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.SPEARMAN;
		unit.name = "Spearman";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 10;
		unit.maxHP = 10;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 100;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.SWIFT;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newCleric(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.CLERIC;
		unit.name = "Cleric";
		unit.type = UnitType.HELPER;
		unit.currentHP = 10;
		unit.maxHP = 10;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 150;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.SWIFT;
		unit.ability = Ability.HEALBURY;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newDrone(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.DRONE;
		unit.name = "Drone";
		unit.type = UnitType.FLIER;
		unit.currentHP = 5;
		unit.maxHP = 5;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 100;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.SWIFT;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newElf(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.ELF;
		unit.name = "Elf";
		unit.type = UnitType.RANGER;
		unit.currentHP = 25;
		unit.maxHP = 25;
		unit.minRange = 2;
		unit.maxRange = 4;
		unit.cost = 300;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.SWIFT;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newCrusader(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.CRUSADER;
		unit.name = "Crusader";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 25;
		unit.maxHP = 25;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 250;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.SWIFT;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newValkyrie(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.VALKYRIE;
		unit.name = "Valkyrie";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 30;
		unit.maxHP = 30;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 300;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.SWIFT;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newArachne(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.ARACHNE;
		unit.name = "Arachne";
		unit.type = UnitType.SKIRMISHER;
		unit.currentHP = 30;
		unit.maxHP = 30;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 300;
		unit.speed = 8;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.SWIFT;
		unit.ability = Ability.FORESTPREDATOR;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newFaery(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.FAERY;
		unit.name = "Faery";
		unit.type = UnitType.HELPER;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 300;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 4;
		unit.god = God.SWIFT;
		unit.ability = Ability.MORALE;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newHero(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.HERO;
		unit.name = "Hero";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 40;
		unit.maxHP = 40;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 400;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 4;
		unit.god = God.SWIFT;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newOracle(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.ORACLE;
		unit.name = "Oracle";
		unit.type = UnitType.TITAN;
		unit.currentHP = 45;
		unit.maxHP = 45;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 700;
		unit.speed = 4;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 5;
		unit.god = God.SWIFT;
		unit.ability = Ability.FORESTATION;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newWerewolf(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.WEREWOLF;
		unit.name = "Werewolf";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 10;
		unit.maxHP = 10;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 100;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.JAVA;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newDeathGuide(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.DEATHGUIDE;
		unit.name = "Death Guide";
		unit.type = UnitType.HELPER;
		unit.currentHP = 10;
		unit.maxHP = 10;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 150;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.JAVA;
		unit.ability = Ability.RAISE;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newSkeleton(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.SKELETON;
		unit.name = "Skeleton";
		unit.type = UnitType.SKIRMISHER;
		unit.currentHP = 15;
		unit.maxHP = 15;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 100;
		unit.speed = 5;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.JAVA;
		unit.ability = Ability.ROT;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newDarkKnight(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.DARKKNIGHT;
		unit.name = "Dark Knight";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 25;
		unit.maxHP = 25;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 250;
		unit.speed = 5;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.JAVA;
		unit.ability = Ability.FEARLESS;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newLilith(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.LILITH;
		unit.name = "Lilith";
		unit.type = UnitType.FLIER;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 450;
		unit.speed = 8;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.JAVA;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newSorcerer(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.SORCERER;
		unit.name = "Sorcerer";
		unit.type = UnitType.MAGE;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 3;
		unit.maxRange = 5;
		unit.cost = 250;
		unit.speed = 3;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.JAVA;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newTombWalker(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.TOMBWALKER;
		unit.name = "Tomb Walker";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 35;
		unit.maxHP = 35;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 300;
		unit.speed = 4;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.JAVA;
		unit.ability = Ability.MORTFLESH;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newGargoyle(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.GARGOYLE;
		unit.name = "Gargoyle";
		unit.type = UnitType.FLIER;
		unit.currentHP = 30;
		unit.maxHP = 30;
		unit.minRange = 1;
		unit.maxRange = 3;
		unit.cost = 600;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 4;
		unit.god = God.JAVA;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newLich(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.LICH;
		unit.name = "Lich";
		unit.type = UnitType.HELPER;
		unit.currentHP = 50;
		unit.maxHP = 50;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 700;
		unit.speed = 3;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 4;
		unit.god = God.JAVA;
		unit.ability = Ability.NECROPOLIS;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newPyrophidian(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.PYROPHIDIAN;
		unit.name = "Pyrophidian";
		unit.type = UnitType.TITAN;
		unit.currentHP = 50;
		unit.maxHP = 50;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 750;
		unit.speed = 3;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 5;
		unit.god = God.JAVA;
		unit.ability = Ability.PYROMANIAC;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newGoblin(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.GOBLIN;
		unit.name = "Goblin";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 5;
		unit.maxHP = 5;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 50;
		unit.speed = 5;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.PYTHON;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newGoblinPriest(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.GOBLINPRIEST;
		unit.name = "Goblin Priest";
		unit.type = UnitType.HELPER;
		unit.currentHP = 10;
		unit.maxHP = 10;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 150;
		unit.speed = 5;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.PYTHON;
		unit.ability = Ability.CONFUSION;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newDraconian(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.DRACONIAN;
		unit.name = "Draconian";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 150;
		unit.speed = 3;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 1;
		unit.god = God.PYTHON;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newHobgoblin(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.HOBGOBLIN;
		unit.name = "Hobgoblin";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 15;
		unit.maxHP = 15;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 150;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.PYTHON;
		unit.ability = Ability.ANTIAIR;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newDraconianLegionary(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.DRACONIANLEGIONARY;
		unit.name = "Draconian Legionary";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 25;
		unit.maxHP = 25;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 225;
		unit.speed = 4;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 2;
		unit.god = God.PYTHON;
		unit.ability = Ability.BIOSYSTEM;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newDraconianShaman(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.DRACONIANSHAMAN;
		unit.name = "Draconian Shaman";
		unit.type = UnitType.HELPER;
		unit.currentHP = 20;
		unit.maxHP = 20;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 300;
		unit.speed = 3;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.PYTHON;
		unit.ability = Ability.SMOKE;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newHomunculus(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.HOMUNCULUS;
		unit.name = "Homunculus";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 40;
		unit.maxHP = 40;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 500;
		unit.speed = 3;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 3;
		unit.god = God.PYTHON;
		unit.ability = Ability.FEAR;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newOrk(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.ORK;
		unit.name = "Ork";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 30;
		unit.maxHP = 30;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 275;
		unit.speed = 5;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 4;
		unit.god = God.PYTHON;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newSeadevil(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.SEADEVIL;
		unit.name = "Seadevil";
		unit.type = UnitType.WARRIOR;
		unit.currentHP = 30;
		unit.maxHP = 30;
		unit.minRange = 1;
		unit.maxRange = 1;
		unit.cost = 300;
		unit.speed = 4;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 4;
		unit.god = God.PYTHON;
		unit.ability = Ability.OCEANDWELLER; //TODO OceanDweller ability needs to be programmed
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
	public static Unit newDragon(int owner) {
		
		Unit unit = new Unit();
		
		unit.id = UnitID.DRAGON;
		unit.name = "Dragon";
		unit.type = UnitType.FLIER;
		unit.currentHP = 40;
		unit.maxHP = 40;
		unit.minRange = 1;
		unit.maxRange = 3;
		unit.cost = 800;
		unit.speed = 6;
		unit.condition = Condition.FULL;
		unit.ownership = owner;
		unit.favour = 5;
		unit.god = God.PYTHON;
		unit.ability = null;
		unit.status = new TreeSet<>();
		
		return unit;
		
	}
	
}

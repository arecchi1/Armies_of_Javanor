package Objects;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import Utilities.EnumVariation.God;

@SuppressWarnings("serial")
public class Player implements Serializable {

	private int funds;
	private int fundsGenerated;
	private int favourAssembly;
	private int favourSwift;
	private int favourJava;
	private int favourPython;
	private Set<God> gods;
	
	public Player(Set<God> set) {
		
		funds = 200;
		fundsGenerated = 0;
		favourAssembly = 1;
		favourSwift = 1;
		favourJava = 1;
		favourPython = 1;
		gods = new TreeSet<>();
		gods.addAll(set); //Gods that are chosen by the player when starting the game
		
	}
	
	public int funds() {
		
		return funds;
		
	}
	
	public int fundsGenerated() {
		
		return fundsGenerated;
		
	}
	
	public int favourAssembly() {
		
		return favourAssembly;
		
	}
	
	public int favourSwift() {
		
		return favourSwift;
		
	}
	
	public int favourJava() {
		
		return favourJava;
		
	}
	
	public int favourPython() {
		
		return favourPython;
		
	}
	
	public void earnFunds(int funds) {
		
		this.funds += funds;
		fundsGenerated += funds;
		
	}
	
	public void removeFunds(int funds) {
		
		this.funds -= funds;
		
		if(this.funds < 0) {
			this.funds = 0;
		}
		
	}
	
	public boolean hasGod(God god) {
		
		return gods.contains(god);
		
	}
	
	public void raiseFavour(God god) {
		
		switch(god) {
		case ASSEMBLY:
			favourAssembly++;
			if(favourAssembly > 5)
				favourAssembly = 5;
			break;
		case SWIFT:
			favourSwift++;
			if(favourSwift > 5)
				favourSwift = 5;
			break;
		case JAVA:
			favourJava++;
			if(favourJava > 5)
				favourJava = 5;
			break;
		case PYTHON:
			favourPython++;
			if(favourPython > 5)
				favourPython = 5;
			break;
		}
		
	}
	
}

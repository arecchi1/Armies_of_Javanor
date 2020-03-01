package Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Utilities.EnumVariation.TileStatus;

@SuppressWarnings("serial")
public class Tile implements Serializable {

	private Terrain terrain;
	private Unit unit;
	private List<StatusBlock> status;
	
	public Tile() {
		
		terrain = Terrain.newPlain();
		unit = Unit.noUnit();
		status = new ArrayList<>();
		
	}
	
	public Tile clone() {
		
		Tile temp = new Tile();
		temp.terrain = this.terrain.clone();
		temp.unit = this.unit.clone();
		temp.status = new ArrayList<>();
		for(int i = 0; i < this.status.size(); i++) {
			temp.status.add(this.status.get(i));
		}
		return temp;
		
	}
	
	public Terrain terrain() {
		
		return terrain;
		
	}
	
	public Unit unit() {
		
		return unit;
		
	}
	
	public StatusBlock statusBlock(int index) {
		
		return status.get(index);
		
	}
	
	public int statusSize() {
		
		return status.size();
		
	}
	
	public void setTerrain(Terrain terrain) {
		
		this.terrain = terrain;
		
	}
	
	public void setUnit(Unit unit) {
		
		this.unit = unit;
		
	}
	
	public boolean hasStatus(TileStatus r_status) {
		
		boolean returner = false;
		
		for(int i = 0; i < status.size(); i++) {
			//The != null part prevents a random crash in the game thread which is extremely unlikely to happen
			if(status.get(i).status() == r_status && status.get(i).status() != null) {
				returner = true;
			}
		}
		
		return returner;
		
	}
	
	public boolean hasStatus(TileStatus r_status, int r_ownership) {
		
		boolean returner = false;
		
		for(int i = 0; i < status.size(); i++) {
			if(status.get(i).status() == r_status && status.get(i).ownership() == r_ownership) {
				returner = true;
			}
		}
		
		return returner;
		
	}		
	
	public void inflict(TileStatus r_status, int r_ownership) {
		
		boolean found = false;
		
		for(int i = 0; i < status.size(); i++) {
			if(status.get(i).status() == r_status && status.get(i).ownership() == r_ownership) {
				found = true;
			}
		}
		
		if(!found) {
			status.add(new StatusBlock(r_status, r_ownership));
		}
		
	}
	
	public void cure(TileStatus r_status, int r_ownership) {
		
		for(int i = 0; i < status.size(); i++) {
			if(status.get(i).status() == r_status && status.get(i).ownership() == r_ownership) {
				status.remove(i);
			}
		}
		
	}
	
}

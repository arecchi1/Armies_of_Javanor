package Objects;

import java.io.Serializable;

import Utilities.EnumVariation;
import Utilities.EnumVariation.TileStatus;

@SuppressWarnings("serial")
public class StatusBlock implements Serializable {

	private int ownership;
	private TileStatus status;
	
	public StatusBlock() {
		
		ownership = EnumVariation.SAFE;
		status = null;
		
	}
	
	public StatusBlock(TileStatus status, int ownership) {
		
		this.ownership = ownership;
		this.status = status;
		
	}
	
	public int ownership() {
		
		return ownership;
		
	}
	
	public TileStatus status() {
		
		return status;
		
	}
	
	public void setOwnership(int ownership) {
		
		this.ownership = ownership;
		
	}
	
	public void setStatus(TileStatus status) {
		
		this.status = status;
		
	}
	
}

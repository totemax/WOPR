package game.entities;

public class PlayerMovement {

	MapLocation from;
	
	MapLocation to;
	
	public PlayerMovement(MapLocation from, MapLocation to){
		this.from = from;
		this.to = to;
	}
	
	public MapLocation getFrom(){
		return this.from;
	}
	
	public MapLocation getTo(){
		return this.to;
	}
	
}

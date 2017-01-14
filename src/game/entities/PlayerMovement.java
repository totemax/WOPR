package game.entities;

public class PlayerMovement {

	MapLocation from;
	
	MapLocation to;
	
	double score;
	
	public PlayerMovement(MapLocation from, MapLocation to, double score){
		this.from = from;
		this.to = to;
		this.score = score;
	}
	
	public MapLocation getFrom(){
		return this.from;
	}
	
	public MapLocation getTo(){
		return this.to;
	}
	
	public double getScore() {
		return this.score;
	}
}

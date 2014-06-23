package game.platform;

import math.geom.Point2i;

public class Player extends Entity {

	static int width = 10;
	static int height = 50;
	
	int health = 10;
	
	public Player(Point2i pos) {
		super(pos, width, height);
		this.gravity = true;
		this.solid = false;
	}
	

}

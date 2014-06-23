package game.platform;

import math.geom.Point2i;

public class Block extends Entity {

	
	public Block(Point2i pos, int width, int height) {
		super(pos, width, height);
		gravity = false;
		solid = false;
	}

	public Block(Point2i pos, int width, int height, int texture) {
		super(pos, width, height, texture);
		gravity = false;
		solid = false;
	}
	
	public void step(Player player) {
		
	}

}

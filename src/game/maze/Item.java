package game.maze;

import lib.game.AbstractEntity3d;
import math.geom.Point2i;

public abstract class Item extends AbstractEntity3d {

	Point2i mapPos;
	
	public Item(int x, int y) {
		super(x*Maze.HALLWIDTH, 1.0f, y*Maze.HALLWIDTH, 0.0f);
		this.mapPos = new Point2i(x, y);
	}
	
	public abstract void pickedUp(Player player);
	
	@Override
	public abstract void draw();

	@Override
	public void update() {

	}

}

package tileWorld.tiles;

import tileWorld.BoundRect;

public abstract class Tile {

	public String name = "Tile";
	
	public Tile below, above, north, south, east, west;
	public static final float xlength = 1.0f, zlength = 1.0f, height = 0.5f;
	private BoundRect bounds;
	
	private boolean breakable;
	private boolean solid;
	
	
	public Tile(BoundRect bounds) {
		this.bounds = bounds;
	}
	
	public abstract void render();
	public abstract void walk();
	public abstract void specialClick();
	
	
	public BoundRect getBounds() {
		return bounds;
	}
	public boolean isBreakable() {
		return breakable;
	}
	public boolean isSolid() {
		return solid;
	}
	
}

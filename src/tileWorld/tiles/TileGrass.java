package tileWorld.tiles;

import static org.lwjgl.opengl.GL11.*;
import graphics.Color3f;
import lib.game.RenderUtils;
import tileWorld.Assets;
import tileWorld.BoundRect;


public class TileGrass extends Tile {

	public TileGrass(BoundRect bounds) {
		super(bounds);
		name = "TileGrass";
	}

	@Override
	public void walk() {
		
	}

	@Override
	public void specialClick() {
		
	}

}

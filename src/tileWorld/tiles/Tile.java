package tileWorld.tiles;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import graphics.Color3f;
import lib.game.RenderUtils;
import tileWorld.Assets;
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
	
	public void render() {
		glPushMatrix();
		glTranslatef(getBounds().getCenter().x, getBounds().getCenter().y, getBounds().getCenter().z);
//		RenderUtils.applyColor(Color3f.black);
//		RenderUtils.drawLineCube(Tile.zlength);
		RenderUtils.applyColor(Color3f.white);
		RenderUtils.bindTexture(Assets.getTexture(name));
		glBegin(GL_QUADS); {
			if (above==null) {
				//top
				glTexCoord2f(0.0f, 0.0f); glVertex3f(-xlength / 2, height / 2, -zlength / 2);
				glTexCoord2f(0.0f, 1.0f); glVertex3f(-xlength / 2, height / 2, zlength / 2);
				glTexCoord2f(1.0f, 1.0f); glVertex3f(xlength / 2, height / 2, zlength / 2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(xlength / 2, height / 2, -zlength / 2);
			}
			if (below == null) {
				//bottom
				glTexCoord2f(0.0f, 0.0f); glVertex3f(-xlength / 2, -height / 2, zlength / 2);
				glTexCoord2f(0.0f, 1.0f); glVertex3f(-xlength / 2, -height / 2, -zlength / 2);
				glTexCoord2f(1.0f, 1.0f); glVertex3f(xlength / 2, -height / 2, -zlength / 2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(xlength / 2, -height / 2, zlength / 2);
			}
			if (west==null) {
				//left
				RenderUtils.applyColor(Color3f.red);
				glTexCoord2f(0.0f, 0.0f); glVertex3f(-xlength / 2, -height / 2, -zlength / 2);
				glTexCoord2f(0.0f, 0.5f); glVertex3f(-xlength / 2, -height / 2, zlength / 2);
				glTexCoord2f(1.0f, 0.5f); glVertex3f(-xlength / 2, height / 2, zlength / 2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(-xlength / 2, height / 2, -zlength / 2);
			}
			if (east==null) {
				//right
				RenderUtils.applyColor(Color3f.green);
				glTexCoord2f(0.0f, 0.0f); glVertex3f(xlength / 2, -height / 2, zlength / 2);
				glTexCoord2f(0.0f, 0.5f); glVertex3f(xlength / 2, -height / 2, -zlength / 2);
				glTexCoord2f(1.0f, 0.5f); glVertex3f(xlength / 2, height / 2, -zlength / 2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(xlength / 2, height / 2, zlength / 2);
			}
			if (south==null) {
				//front
				RenderUtils.applyColor(Color3f.blue);
				glTexCoord2f(0.0f, 0.0f); glVertex3f(xlength / 2, -height / 2, -zlength / 2);
				glTexCoord2f(0.0f, 0.5f); glVertex3f(-xlength / 2, -height / 2, -zlength / 2);
				glTexCoord2f(1.0f, 0.5f); glVertex3f(-xlength / 2, height / 2, -zlength / 2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(xlength / 2, height / 2, -zlength / 2);
			}
			if (north==null) {
				//back
				RenderUtils.applyColor(Color3f.black);
				glTexCoord2f(0.0f, 0.0f); glVertex3f(-xlength / 2, -height / 2, zlength / 2);
				glTexCoord2f(0.0f, 0.5f); glVertex3f(xlength / 2, -height / 2, zlength / 2);
				glTexCoord2f(1.0f, 0.5f); glVertex3f(xlength / 2, height / 2, zlength / 2);
				glTexCoord2f(1.0f, 0.0f); glVertex3f(-xlength / 2, height / 2, zlength / 2);
			}
			
		} glEnd();
		glPopMatrix();
	}
	
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

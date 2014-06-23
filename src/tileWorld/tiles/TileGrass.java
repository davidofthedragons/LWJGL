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
	public void render() {
		glPushMatrix();
		glTranslatef(getBounds().getCenter().x, getBounds().getCenter().y, getBounds().getCenter().z);
		RenderUtils.applyColor(Color3f.white);
//		RenderUtils.drawLineCube(Tile.xlength);
//		RenderUtils.bindTexture(Assets.getTexture(name));
//		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS); {
			//top
			glTexCoord2f(0.0f, 0.0f); glVertex3f(-xlength/2, height/2, -zlength/2);
			glTexCoord2f(0.0f, 1.0f); glVertex3f(-xlength/2, height/2, zlength/2);
			glTexCoord2f(1.0f, 1.0f); glVertex3f(xlength/2, height/2, zlength/2);
			glTexCoord2f(1.0f, 0.0f); glVertex3f(xlength/2, height/2, -zlength/2);
			//front
			glTexCoord2f(0.0f, 0.0f); glVertex3f(-xlength/2, -height/2, -zlength/2);
			glTexCoord2f(0.0f, 0.5f); glVertex3f(-xlength/2, -height/2, zlength/2);
			glTexCoord2f(1.0f, 0.5f); glVertex3f(-xlength/2, height/2, zlength/2);
			glTexCoord2f(1.0f, 0.0f); glVertex3f(-xlength/2, height/2, -zlength/2);
		} glEnd();
		glPopMatrix();
	}

	@Override
	public void walk() {
		
	}

	@Override
	public void specialClick() {
		
	}

}

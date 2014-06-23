package tests;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;

import lib.game.AbstractGame;
import lib.game.RenderUtils;

public class TextureTest extends AbstractGame {

	String textureLoc = "res/Java.png";
	int texID;
	String textureLoc2 = "res/wall2.png";
	int texID2;
	
	public TextureTest() throws LWJGLException {
		super("Testing custom texture loading", 600, 600);
		start();
	}

	@Override
	public void init() {
		RenderUtils.initStd2d(width, height);
		texID = RenderUtils.loadTexture(textureLoc, true);
		texID2 = RenderUtils.loadTexture(textureLoc2, true);
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		RenderUtils.clearScreen();
		RenderUtils.bindTexture(texID);
		glBegin(GL_QUADS); {
			glTexCoord2f(0.0f, 0.0f); glVertex2i(50, 50);
			glTexCoord2f(0.0f, 1.0f); glVertex2i(50, 100);
			glTexCoord2f(1.0f, 1.0f); glVertex2i(100, 100);
			glTexCoord2f(1.0f, 0.0f); glVertex2i(100, 50);
		} glEnd();
		RenderUtils.bindTexture(texID2);
		glBegin(GL_QUADS); {
			glTexCoord2f(0.0f, 0.0f); glVertex2i(150, 150);
			glTexCoord2f(0.0f, 1.0f); glVertex2i(150, 200);
			glTexCoord2f(1.0f, 1.0f); glVertex2i(200, 200);
			glTexCoord2f(1.0f, 0.0f); glVertex2i(200, 150);
		} glEnd();
	}

	@Override
	public void processInput() {
		
	}

	public static void main(String[] args) {
		try {
			new TextureTest();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

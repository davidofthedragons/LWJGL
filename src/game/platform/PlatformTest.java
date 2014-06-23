package game.platform;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;

import lib.game.AbstractGame;
import lib.game.RenderUtils;

public class PlatformTest extends AbstractGame {

	Player player;
	ArrayList<Block> blocks = new ArrayList<Block>();
	
	
	public PlatformTest() throws LWJGLException {
		super("Platform Test", 1000, 600, false);
		start();
	}

	@Override
	public void init() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, 0.0, height, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
		
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public void processInput() {
		
	}

	public static void main(String[] args) {
		try {
			new PlatformTest();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

package driving;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.util.glu.GLU;

import lib.game.AbstractGame;

public class Driving extends AbstractGame {

	static boolean fullScreen = false;
	
	
	public Driving() throws LWJGLException {
		super("Driving", 1000, 600, fullScreen);
		start();
	}

	@Override
	public void init() { //TODO init
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(75, (float)width/(float)height, 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	}

	@Override
	public void update() { //TODO update
		
	}

	@Override
	public void render() { //TODO render
		
	}

	@Override
	public void processInput() { //TODO processInput
		
	}

	public static void main(String[] args) {
		try {
			new Driving();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

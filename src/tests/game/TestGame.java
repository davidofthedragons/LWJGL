package tests.game;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import lib.game.*;
import math.geom.Point2d;

import static org.lwjgl.opengl.GL11.*;

public class TestGame extends AbstractGame {
	
	TestEntity e = new TestEntity(0, 0);
	
	public TestGame() throws LWJGLException {
		super("Test Game", 600, 600);
		setfps(100);
		start();
	}

	@Override
	public void init() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
	}

	@Override
	public void update() {
		e.update();
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		e.draw();
		/*glBegin(GL_QUADS);
			glVertex2d(0, 0);
			glVertex2d(100, 0);
			glVertex2d(100, 100);
			glVertex2d(0, 100);
		glEnd();*/
	}

	@Override
	public void processInput() {
		//e.setPos(new Point2d(Mouse.getX(), Mouse.getY()));
		e.aim(new Point2d(Mouse.getX(), height-Mouse.getY()));
		while(Keyboard.next()) {
			switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_W:
				if (Keyboard.getEventKeyState()) e.getMoveVect().setj(-1.0);
				else e.getMoveVect().setj(0.0);
				break;
			case Keyboard.KEY_A:
				if (Keyboard.getEventKeyState()) e.getMoveVect().seti(-1.0);
				else e.getMoveVect().seti(0.0);
				break;
			case Keyboard.KEY_D:
				if (Keyboard.getEventKeyState()) e.getMoveVect().seti(1.0);
				else e.getMoveVect().seti(0.0);
				break;
			case Keyboard.KEY_S:
				if (Keyboard.getEventKeyState()) e.getMoveVect().setj(1.0);
				else e.getMoveVect().setj(0.0);
				break;
			}
		}
	}
	
	public static void main(String args[]) throws LWJGLException {
		new TestGame();
	}

}

package tests;

import static lib.game.RenderUtils.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

import lib.game.AbstractGame;
import lib.game.Camera;
import math.geom.Vector3f;

public class CameraTest extends AbstractGame {

	Camera camera = new Camera(new Vector3f(0.0f, 5.0f, 0.0f), -3.0f, 0.0f, 0.0f);
	float camSpeed = 0.1f;
	float rotSpeed = 0.25f;
	boolean movingForward = false;
	boolean movingBackward = false;
	boolean movingRight = false;
	boolean movingLeft = false;
	boolean movingUp = false;
	boolean movingDown = false;
	
	private final int KEY_FORWARD = Keyboard.KEY_W;
	private final int KEY_BACKWARD = Keyboard.KEY_S;
	private final int KEY_LEFT = Keyboard.KEY_A;
	private final int KEY_RIGHT = Keyboard.KEY_D;
	private final int KEY_UP = Keyboard.KEY_SPACE;
	private final int KEY_DOWN = Keyboard.KEY_LSHIFT;
	private final int KEY_CLOSE = Keyboard.KEY_ESCAPE;
	private final int KEY_RESET = Keyboard.KEY_RETURN;
	
	public CameraTest() throws LWJGLException {
		super("Camera Test", 600, 600, true);
		start();
	}

	@Override
	public void init() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(75, (float)width/(float)height, 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glClearColor(0.4f, 0.5f, 1.0f, 0.0f);
        //glEnable(GL_CULL_FACE);
        //System.out.println(glGetString(GL_VERSION));
        camera.setBoundsx(-90.0f, 90.0f);
        Mouse.setGrabbed(true);
	}

	@Override
	public void update() {
		if(movingForward) {
			camera.moveForward(camSpeed);
		}
		if(movingBackward) {
			camera.moveBackward(camSpeed);
		}
		if(movingLeft) {
			camera.moveLeft(camSpeed);
		}
		if(movingRight) {
			camera.moveRight(camSpeed);
		}
		if(movingUp) {
			camera.moveUp(camSpeed);
		}
		if(movingDown) {
			camera.moveDown(camSpeed);
		}
	}
	
	public void applyCameraTransform() {
		camera.applyTransform();
	}

	@Override
	public void render() {
		clearScreen();
		glPushMatrix();
		applyCameraTransform();
		glBegin(GL_QUADS); {
			glColor3f(0.5f, 0.0f, 0.5f);
			glVertex3f(-10.0f, 0.0f, 0.0f);
			glVertex3f(10.0f, 0.0f, 0.0f);
			glVertex3f(10.0f, 0.0f, -20.0f);
			glVertex3f(-10.0f, 0.0f, -20.0f);
		} glEnd();
		glPushMatrix();
		glTranslatef(0.0f, 5.0f, 0.0f);
		glColor3f(0.0f, 1.0f, 0.0f);
		drawLineCube(1.0f);
		glPopMatrix();
		glPushMatrix(); {
			glTranslatef(0.0f, 1.0f, 0.0f);
			drawLineCube(1.0f);
			glTranslatef(0.0f, 1.0f, 0.0f);
			drawLineCube(1.0f);
		} glPopMatrix();
		glPopMatrix();
	}

	@Override
	public void processInput() {
		while(Mouse.next()) {
			camera.rotX(-Mouse.getDY()*rotSpeed);
			camera.rotY(Mouse.getDX()*rotSpeed);
		}
		
		while(Keyboard.next()) {
			switch (Keyboard.getEventKey()) {
			case KEY_CLOSE:
				stop();
				break;
			case KEY_FORWARD:
				if(Keyboard.getEventKeyState()) {
					movingForward = true;
					movingBackward = false;
				}
				else movingForward = false;
				break;
			case KEY_BACKWARD:
				if(Keyboard.getEventKeyState()) {
					movingBackward = true;
					movingForward = false;
				}
				else movingBackward = false;
				break;
			case KEY_LEFT:
				if(Keyboard.getEventKeyState()) {
					movingLeft = true;
					movingRight = false;
				}
				else movingLeft = false;
				break;
			case KEY_RIGHT:
				if(Keyboard.getEventKeyState()) {
					movingRight = true;
					movingLeft = false;
				}
				else movingRight = false;
				break;
			case KEY_UP:
				if(Keyboard.getEventKeyState()) {
					movingUp = true;
					movingDown = false;
				}
				else movingUp = false;
				break;
			case KEY_DOWN:
				if(Keyboard.getEventKeyState()) {
					movingDown = true;
					movingUp = false;
				}
				else movingDown = false;
				break;
			case KEY_RESET:
				camera = new Camera(new Vector3f(0.0f, 5.0f, 0.0f), -3.0f, 0.0f, 0.0f);
				break;
			}
		}
		
	}
	
	public static void main(String[] args) {
		try {
			new CameraTest();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

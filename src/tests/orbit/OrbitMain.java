package tests.orbit;

import static lib.game.RenderUtils.asFloatBuffer;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import lib.game.AbstractGame;
import lib.game.Camera;
import lib.game.RenderUtils;
import lib.game.path.OvalPath;
import math.geom.Point3f;

public class OrbitMain extends AbstractGame {

	Camera camera = new Camera();
	float moveSpeed = 0.25f;
	float rotSpeed = 0.25f;
	
	Planet earth;
	Planet sun;
	
	private final int KEY_CLOSE = Keyboard.KEY_ESCAPE;
	private final int KEY_FORWARD = Keyboard.KEY_W;
	private final int KEY_BACKWARD = Keyboard.KEY_S;
	private final int KEY_LEFT = Keyboard.KEY_A;
	private final int KEY_RIGHT = Keyboard.KEY_D;
	private final int KEY_UP = Keyboard.KEY_SPACE;
	private final int KEY_DOWN = Keyboard.KEY_LSHIFT;
	private final int KEY_SPEED_DECREASE = Keyboard.KEY_DOWN;
	private final int KEY_SPEED_INCREASE = Keyboard.KEY_UP;
	
	boolean movingForward, movingBackward, movingRight, movingLeft, movingUp, movingDown;
	
	public OrbitMain() throws LWJGLException {
		super("Orbit", 700, 700, false);
		start();
	}

	@Override
	public void init() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(75, (float)Display.getWidth()/(float)Display.getHeight(), 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_TEXTURE_2D);
        initLighting();
        Mouse.setGrabbed(true);
        camera.setBoundsx(-90, 90);
        
        earth = new Planet("earth", new OvalPath("xz", 5.0f, 5.0f, new Point3f(0.0f, 0.0f, 0.0f)));
        sun = new Planet("sun");
	}

	public void initLighting() {
		glShadeModel(GL_SMOOTH);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glLightModel(GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{1.0f, 1.0f, 01.0f, 1f}));
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE);
	}
	
	@Override
	public void update() {
		earth.update();
		
		if(movingForward) {
			camera.moveForward(moveSpeed);
		}
		if(movingBackward) {
			camera.moveBackward(moveSpeed);
		}
		if(movingLeft) {
			camera.moveLeft(moveSpeed);
		}
		if(movingRight) {
			camera.moveRight(moveSpeed);
		}
		if(movingUp) {
			camera.moveUp(moveSpeed);
		}
		if(movingDown) {
			camera.moveDown(moveSpeed);
		}
	}

	@Override
	public void render() {
		RenderUtils.clearScreen();
		glPushMatrix();
		camera.applyTransform();
		glLight(GL_LIGHT0, GL_POSITION, asFloatBuffer(new float[]{
				0.0f, 0.0f, 0.0f, 1.0f}));
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		earth.draw();
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
			case KEY_SPEED_DECREASE:
				moveSpeed -= 0.01f;
				break;
			case KEY_SPEED_INCREASE:
				moveSpeed += 0.01f;
				break;
			}
		}
	}

	public static void main(String[] args) {
		try {
			new OrbitMain();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

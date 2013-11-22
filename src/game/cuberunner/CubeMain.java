package game.cuberunner;

import static org.lwjgl.opengl.GL11.*;
import static lib.game.RenderUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

import lib.game.*;
import math.geom.Vector3f;

public class CubeMain extends AbstractGame {
	
	private static boolean devmode = true;
	
	public static float CSIZE = Cube.SIZE-.5f;
	
	private int fov = 75;
	
	private Random rand = new Random();
	
	private double speed = 0.5;
	private int chunkSize = 10;
	private float collision = -Cube.SIZE*2;
	private float lBound = chunkSize*Cube.SIZE*3, rBound = chunkSize*Cube.SIZE*-3;
	private float horizon = -150;
	
	private static float easy = 0.005f, medium = 0.015f, hard = 0.0325f;
	private static float density = hard;
	
	private Vector3f cameraTransform = new Vector3f();
	private float cameraSpeed = 0.2f;
	
	private boolean movingRight = false;
	private boolean movingLeft = false;
	
	private final int KEY_RIGHT = Keyboard.KEY_RIGHT;
	private final int KEY_LEFT = Keyboard.KEY_LEFT;
	
	private ArrayList<Cube> cubes = new ArrayList<Cube>();
	

	public CubeMain() throws LWJGLException {
		super("CubeRunner", 800, 600, true);
		start();
	}

	@Override
	public void init() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, (float)width/(float)height, 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        Mouse.setGrabbed(true);
        generateChunk(0.0f, 0.0f, horizon);
		generateChunk(-chunkSize*Cube.SIZE, 0.0f, horizon);
	}
	
	float counter = 0.0f;
	float distance = 0.0f;

	@Override
	public void update() {
		if(movingRight && cameraTransform.geti()-cameraSpeed>rBound)
			cameraTransform.seti(cameraTransform.geti()-cameraSpeed);
		if(movingLeft && cameraTransform.geti()+cameraSpeed<lBound) cameraTransform.seti(cameraTransform.geti()+cameraSpeed);
		//System.out.println("camerax = " + cameraTransform.geti());
		for(int i=0; i<cubes.size(); i++) {
			cubes.get(i).update();
			//System.out.println("cubes[" + i + "].getx() = " + cubes.get(i).getx());
			if(cubes.get(i).getz()>=collision) {
				//System.out.println("Passed cube " + i);
				if(cubes.get(i).checkCollision(-cameraTransform.geti())) {
					System.out.println("Hit by cube " + i);
					//System.out.println("Cube z: " + cubes.get(i).getz());
					//System.out.println("Camera x: " + cameraTransform.geti());
					gameOver();
				}
				if(cubes.get(i).getz()>=0) cubes.remove(i);
				//cubes.get(i).setz(horizon);
			}
		}
		distance += speed;
		counter += speed;
		if(counter>=(chunkSize*Cube.SIZE)) {
			generateChunk(0.0f, 0.0f, horizon);
			generateChunk(-chunkSize*Cube.SIZE, 0.0f, horizon);
			generateChunk(chunkSize*Cube.SIZE, 0.0f, horizon);
			generateChunk(chunkSize*Cube.SIZE*2, 0.0f, horizon);
			generateChunk(-chunkSize*Cube.SIZE*2, 0.0f, horizon);
			generateChunk(-chunkSize*Cube.SIZE*3, 0.0f, horizon);
			counter = 0.0f;
		}
		System.out.println((int)distance);
	}
	
	public void setUp3d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, (float)width/(float)height, 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	}
	public void setUp2d() {
		initStd2d(width, height);
	}

	@Override
	public void render() {
		clearScreen();
		setUp3d();
		glPushMatrix();
		
		if(movingRight) glRotatef(-2.0f, 0.0f, 0.0f, 1.0f);
		if(movingLeft) glRotatef(2.0f, 0.0f, 0.0f, 1.0f);
		
		glTranslatef(0.0f, 0.0f, -75);
		glRotatef(3, 1.0f, 0.0f, 0.0f);
		glTranslatef(0.0f, 0.0f, 75);
		
		glPushMatrix();
		glColor3f(1.0f, 0.0f, 0.0f);
		glTranslatef(-CSIZE/2, 0.0f, -CSIZE*2);
		Cube.drawLineCube(CSIZE);
		glPopMatrix();
		
		glTranslatef(cameraTransform.geti(), cameraTransform.getj(), cameraTransform.getk());
		for(int i=0; i<cubes.size(); i++) {
			cubes.get(i).draw();
		}
		
		glPopMatrix();
		
	}

	@Override
	public void processInput() {
		while(Keyboard.next()) {
			switch(Keyboard.getEventKey()) {
			case KEY_RIGHT:
				if (Keyboard.getEventKeyState()) {
					movingRight = true;
					movingLeft = false;
				} else movingRight = false;
				break;
			case KEY_LEFT:
				if (Keyboard.getEventKeyState()) {
					movingLeft = true;
					movingRight = false;
				} else movingLeft = false;
				break;
			}
		}
	}
	
	public void generateChunk(float x, float y, float z) { 
		for(int i=0; i<chunkSize; i++) {
			for(int j=0; j<chunkSize; j++) {
				if(rand.nextFloat()<density) cubes.add(new Cube(x+Cube.SIZE*i, y, z+Cube.SIZE*j, speed));
			}
		}
	}
	
	public void gameOver() {
		System.out.println("Game Over");
		stop();
	}
	
	public static void main(String args[]) {
		if (!devmode) {
			String os = System.getProperty("os.name");
			System.out.println(os);
			if (os.contains("Windows"))
				System.setProperty("org.lwjgl.librarypath", new File(
						"lib/natives/win").getAbsolutePath());
			else if (os.contains("Mac"))
				System.setProperty("org.lwjgl.librarypath", new File(
						"lib/natives/mac").getAbsolutePath());
			else if (os.contains("Linux"))
				System.setProperty("org.lwjgl.librarypath", new File(
						"lib/natives/linux").getAbsolutePath());
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("easy"))
					density = easy;
				else if (args[0].equalsIgnoreCase("medium"))
					density = medium;
				else if (args[0].equalsIgnoreCase("hard"))
					density = hard;
			}
		}
		try {
			new CubeMain();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

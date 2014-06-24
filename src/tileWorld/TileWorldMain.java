package tileWorld;

import static lib.game.RenderUtils.asFloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import graphics.Color3f;

import java.io.File;
import java.util.ArrayList;

import lib.game.AbstractGame;
import lib.game.Camera;
import lib.game.GameState;
import lib.game.RenderUtils;
import math.geom.Point3f;
import math.geom.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import tileWorld.entity.Entity;
import tileWorld.entity.Player;
import tileWorld.tiles.Tile;

public class TileWorldMain extends AbstractGame {
	
	/* TODO list
	 * -fix sides
	 * -add jumping/crouching
	 * -gravity/collision detection
	 * -3rd person
	 * -more blocks
	 * -sounds
	 * -lighting
	 */

	static final String VERSION = "Alpha v0.2";
	
	static boolean fullScreen = true;
	static boolean mouseGrabbed = true;
	
	float rotSpeed = .1f;
	
	static final int KEY_FORWARD = Keyboard.KEY_W;
	static final int KEY_RIGHT = Keyboard.KEY_A;
	static final int KEY_LEFT = Keyboard.KEY_D;
	static final int KEY_BACKWARD = Keyboard.KEY_S;
	static final int KEY_JUMP = Keyboard.KEY_SPACE;
	static final int KEY_CROUCH = Keyboard.KEY_LSHIFT;
	static final int KEY_PAUSE = Keyboard.KEY_ESCAPE;
	static final int KEY_TOGGLE_FLYING = Keyboard.KEY_LCONTROL;
	boolean movingForward, movingBackward, movingRight, movingLeft, movingUp, movingDown;
	
	
	int FOV = 70;
	
	Camera camera;
	GameState state = GameState.CONTINUE;
	
	Player player = new Player(new Point3f());
	boolean flying = true;
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	float jump = 0.2f;
	float gravity = 0.01f;
	
	ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();
	
	GenBasic gen = new GenBasic(718);
	
	
	public TileWorldMain() throws LWJGLException {
		super("TileWorld " + VERSION, 1000, 650, fullScreen);
		start();
	}

	@Override
	public void init() { //TODO init
		printMessage("Loading assets...");
		Assets.loadTextures(new File("res/tileWorld/textures/textureList"));
		printMessage("Initializing OpenGL...");
		glClearColor(0.4f, 0.5f, 1.0f, 0.0f);
		glEnable(GL_TEXTURE_2D);
//		glEnable(GL_LIGHTING);
//		glEnable(GL_LIGHT0);
//		glShadeModel(GL_SMOOTH);
//		glLightModel(GL_LIGHT_MODEL_LOCAL_VIEWER, RenderUtils.asFloatBuffer(new float[]{0.7f, 0.7f, 0.7f, 1.0f}));
//		glLight(GL_LIGHT0, GL_DIFFUSE, RenderUtils.asFloatBuffer(new float[]{0.6f, 0.6f, 0.6f, 1.0f}));
//		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
//		glEnable(GL_COLOR_MATERIAL);
//		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		camera = new Camera(new Vector3f(0.0f, 5.0f, 0.0f), -3.0f, 180.0f, 0.0f);
		camera.setBoundsx(-90.0f, 90.0f);
		entities.add(player);
//		camera.moveUp(1.0f);
        Mouse.setGrabbed(mouseGrabbed);
        printMessage("Loading chunks...");
		loadedChunks.add(gen.genChunk(0.0f, 0.0f));
		printMessage("Ready");
		
	}
	
	private void init3d() { //TODO init3d
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(FOV, (float)Display.getWidth()/(float)Display.getHeight(), 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glClearDepth(1.0);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
//		enableLighting();
	}
	
	@SuppressWarnings("unused")
	private void enableLighting() { //TODO lighting
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glShadeModel(GL_SMOOTH);
		glLightModel(GL_LIGHT_MODEL_LOCAL_VIEWER, asFloatBuffer(new float[]{0.7f, 0.7f, 0.7f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{0.6f, 0.6f, 0.6f, 1f}));
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
	}

	@Override
	public void update() { //TODO update
		switch(state) {
		case CONTINUE:
			camera.moveUp(player.yVelocity);
			player.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj()-player.eyeHeight, camera.getPos().getk());
			if(!flying) {
				if(player.pos.y>loadedChunks.get(0).getGroundHeight(player.pos)) player.yVelocity -= gravity;
				else player.yVelocity = 0;
			}
			
			
//			camera.move(player.velocity);
			if(movingForward) camera.moveForward(player.speed);
			if(movingBackward) camera.moveBackward(player.speed);
			if(movingLeft) camera.moveRight(player.speed);
			if(movingRight) camera.moveLeft(player.speed);
			if(movingUp && flying) camera.moveUp(player.speed);
			if(movingDown && flying) camera.moveDown(player.speed);
			break;
		case MENU:
			break;
		case PAUSE:
			break;
		}
	}

	@Override
	public void render() { //TODO render
		RenderUtils.clearScreen();
		switch(state) {
		case CONTINUE:
			init3d();
			glPushMatrix();
			camera.applyTransform();
//			RenderUtils.applyColor(Color3f.black);
//			glPushMatrix();
//			glTranslatef(0.0f, Tile.height, 0.0f);
//			RenderUtils.drawLineCube(Tile.height);
//			glPopMatrix();
			for(Chunk c : loadedChunks) {
				for(Tile t : c.tiles) {
					t.render();
				}
			}
			
			glPopMatrix();
			break;
		case MENU:
			break;
		case PAUSE:
			break;
		default:
			break;
		}
		
		
	}

	@Override
	public void processInput() { //TODO processInput
		
		while (Mouse.next()) {
			camera.rotX(-Mouse.getEventDY() * rotSpeed);
			camera.rotY(Mouse.getEventDX() * rotSpeed);
			player.angle += Mouse.getEventDX() * rotSpeed;
		}
		while (Keyboard.next()) {
			switch (state) {
			case CONTINUE:
				switch(Keyboard.getEventKey()) {
				case KEY_PAUSE:
					if(Keyboard.getEventKeyState()) stop();
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
				case KEY_JUMP:
					if(Keyboard.getEventKeyState()) {
						movingUp = true;
						movingDown = false;
						if(!flying)player.yVelocity = jump;
					}
					
					else movingUp = false;
					break;
				case KEY_CROUCH:
						if(Keyboard.getEventKeyState()) {
							movingDown = true;
							movingUp = false;
						}
						else movingDown = false;
					break;
				case KEY_TOGGLE_FLYING:
					if(Keyboard.getEventKeyState()) flying = !flying;
				}
				break;
			case MENU:
				break;
			case PAUSE:
				break;

			}
		}
	}
	
	public void printMessage(String message) {
		System.out.println("[INFO] " + message);
	}

	public static void main(String[] args) {
		try {
			new TileWorldMain();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

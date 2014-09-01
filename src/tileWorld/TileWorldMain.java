package tileWorld;

import static lib.game.RenderUtils.asFloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import graphics.Color3f;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

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
import tileWorld.entity.ParticleEmitter;
import tileWorld.entity.Player;
import tileWorld.gen.GenChunks;
import tileWorld.gen.GenHeightMap;
import tileWorld.tiles.Tile;

public class TileWorldMain extends AbstractGame {
	
	/* TODO list
	 * -add crouching
	 * -collision detection
	 * -3rd person
	 * -more blocks
	 * -sounds
	 * -lighting
	 * -proper terrain gen
	 * -infinite world
	 * -save world
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
//	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	float jump = 0.17f;
	float gravity = 0.01f;
	
//	ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();
//	Chunk playerChunk;
	
	GenBasic gen = new GenBasic(718);
	Random rand = new Random();
	
	
	public TileWorldMain() throws LWJGLException {
		super("TileWorld " + VERSION, 1000, 650, fullScreen);
		start();
	}

	@Override
	public void init() { //TODO init
		printMessage("Loading assets...");
		Assets.load("res/tileWorld/");
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
		World.entities.add(player);
		ParticleEmitter pe = new ParticleEmitter(new Point3f(5.0f, 5.0f, 5.0f), new Vector3f(0.0f, 1.0f, 0.0f), 5,
				new Vector3f(1.0f, 0.0f, 0.0f), 0.25f, 10000, 75, 5, 0.1f, 0.05f, rand);
		pe.setColors(Color3f.red, Color3f.black, new Color3f(1.0f, 0.25f, 0.0f));
//		entities.add(pe);
//		camera.moveUp(1.0f);
		Mouse.setGrabbed(mouseGrabbed);
		printMessage("Loading chunks...");
//		loadedChunks.add(gen.genChunk(0.0f, 0.0f));
		int[][] map = GenHeightMap.genHills(Chunk.SIZE*4, Chunk.SIZE*4, 850, 2, 8, 10, rand);
//		int[][] map = GenHeightMap.genFractal(Chunk.SIZE*4, Chunk.SIZE*4, rand, 05, 10);
		World.loadedChunks.add(GenChunks.genChunk(0, 0, map, 5, 3));
		World.loadedChunks.add(GenChunks.genChunk(Chunk.SIZE, 0, map, 5, 3));
		World.loadedChunks.add(GenChunks.genChunk(0, Chunk.SIZE, map, 5, 3));
		World.loadedChunks.add(GenChunks.genChunk(Chunk.SIZE, Chunk.SIZE, map, 5, 3));
//		World.loadedChunks.add(GenChunks.genChunk(Chunk.SIZE*2, 0, map, 5, 3));
//		World.loadedChunks.add(GenChunks.genChunk(Chunk.SIZE*2, Chunk.SIZE, map, 5, 3));
//		loadedChunks.add(gen.genChunk(Chunk.SIZE*Tile.xlength, Chunk.SIZE*Tile.zlength));
//		World.loadedChunks.add(GenChunks.genChunk(Chunk.SIZE, 0, GenHeightMap.genFault(Chunk.SIZE*4, Chunk.SIZE*4, 200, 1, -5, 15, rand), 5, 3));
//		World.loadedChunks.add(GenChunks.genChunk(Chunk.SIZE, Chunk.SIZE, GenHeightMap.genFractal(17, 17, rand, 1, 10), 1, 3));
//		playerChunk = loadedChunks.get(0);
//		Assets.saveChunk(playerChunk);
		printMessage("Saving Chunks");
		World.saveChunks();
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
			float groundHeight = World.getGroundHeight(player.pos);
			moveUp(player.yVelocity);
			if(player.pos.y < groundHeight) {
				player.pos.y = groundHeight;
				camera.setPos(new Vector3f(player.pos.x, player.pos.y+player.eyeHeight, player.pos.z));
			}
			if(!flying) {
				if(player.pos.y>groundHeight) {
					player.yVelocity -= gravity;
				}
				else player.yVelocity = 0;
			} else player.yVelocity = 0;
//			moveUp(player.yVelocity);
			//if(playerChunk.hitsTile(player.pos)) {
//				moveDown(player.yVelocity);
//				moveUp(player.pos.y-playerChunk.getGroundHeight(player.pos));// = playerChunk.getGroundHeight(player.pos) + 0.1f;
//				player.yVelocity = 0;
			//} //else moveUp(player.yVelocity);
//			movingDown = true;
			
//			camera.move(player.velocity);
			if(movingForward) {
				moveForward(player.speed);
				if(World.hitsTile(player.bounds, 3)) moveBackward(player.speed);
			}
			else if(movingBackward) {
				moveBackward(player.speed);
				if(World.hitsTile(player.bounds, 3)) moveForward(player.speed);
			}
			if(movingLeft) {
				moveLeft(player.speed);
				if(World.hitsTile(player.bounds, 3)) moveRight(player.speed);
			}
			else if(movingRight) {
				moveRight(player.speed);
				if(World.hitsTile(player.bounds, 3)) moveLeft(player.speed);
			}
			if(movingUp && flying) {
				moveUp(player.speed);
				if(World.hitsTile(player.bounds, 3)) moveDown(player.speed);
			}
			else if(movingDown && flying) {
				moveDown(player.speed);
				if(World.hitsTile(player.bounds, 3)) moveUp(player.speed);
			}
//			for(Chunk c : World.loadedChunks) if(c.getBounds().contains(player.pos)) playerChunk = c;
			
			for(Entity e : World.entities) {
				e.update();
			}
			
			break;
		case MENU:
			break;
		case PAUSE:
			break;
		}
	}
	
	private void moveForward(float dist) {
		camera.moveForward(dist);
		player.setPos(new Point3f(camera.getPos().geti(), camera.getPos().getj()-player.eyeHeight, camera.getPos().getk()));
	}
	private void moveBackward(float dist) {
		camera.moveBackward(dist);
		player.setPos(new Point3f(camera.getPos().geti(), camera.getPos().getj()-player.eyeHeight, camera.getPos().getk()));
	}
	private void moveLeft(float dist) {
		camera.moveRight(dist);
		player.setPos(new Point3f(camera.getPos().geti(), camera.getPos().getj()-player.eyeHeight, camera.getPos().getk()));
	}
	private void moveRight(float dist) {
		camera.moveLeft(dist);
		player.setPos(new Point3f(camera.getPos().geti(), camera.getPos().getj()-player.eyeHeight, camera.getPos().getk()));
	}
	private void moveUp(float dist) {
		camera.moveUp(dist);
		player.setPos(new Point3f(camera.getPos().geti(), camera.getPos().getj()-player.eyeHeight, camera.getPos().getk()));
	}
	private void moveDown(float dist) {
		camera.moveDown(dist);
		player.setPos(new Point3f(camera.getPos().geti(), camera.getPos().getj()-player.eyeHeight, camera.getPos().getk()));
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
			for(Chunk c : World.loadedChunks) {
//				for(Tile t : c.tiles) {
//					t.render();
//				}
				for(int x=0; x<Chunk.SIZE; x++) {
					for(int y=0; y<Chunk.HEIGHT; y++) {
						for(int z=0; z<Chunk.SIZE; z++) {
							if(c.get(x, y, z) != null)
								c.get(x, y, z).render();
						}
					}
				}
			}
			for(Entity e : World.entities) {
				e.render();
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

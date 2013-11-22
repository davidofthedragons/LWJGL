package game.maze;

import static org.lwjgl.opengl.GL11.*;
import static lib.game.RenderUtils.*;

import java.util.ArrayList;

import lib.game.AbstractGame;
import lib.game.Camera;
import math.geom.Point2i;
import math.geom.Point3d;
import math.geom.Point3f;
import math.geom.Vector3d;
import math.geom.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;


public class MazeMain extends AbstractGame {

	private Camera camera = new Camera();
	private float camSpeed = 0.1f;
	private float rotSpeed = 0.1f;
	private boolean movingForward = false;
	private boolean movingBackward = false;
	private boolean movingRight = false;
	private boolean movingLeft = false;
	private boolean movingUp = false;
	private boolean movingDown = false;
	
	private final int KEY_FORWARD = Keyboard.KEY_W;
	private final int KEY_BACKWARD = Keyboard.KEY_S;
	private final int KEY_LEFT = Keyboard.KEY_A;
	private final int KEY_RIGHT = Keyboard.KEY_D;
	private final int KEY_UP = Keyboard.KEY_SPACE;
	private final int KEY_DOWN = Keyboard.KEY_LSHIFT;
	private final int KEY_CLOSE = Keyboard.KEY_ESCAPE;
	private final int KEY_RESET = Keyboard.KEY_RETURN;
	
	private final int BUTTON_SHOOT = 0;
	
	private boolean shooting = false;
	
	public final static float HALLWIDTH = 4.0f;
	public final static float HALLHEIGHT = 5.0f;
	
	Maze maze;
	ArrayList<BasicEnemy> enemies = new ArrayList<BasicEnemy>();
	
	private String[] levels = {"res/map0.png", "res/map0.png", "res/map1.png"};
	private int levelIndex = 0;
	
	Texture wall;
	
	private int playerHealth = 20;
	
	public MazeMain() throws LWJGLException {
		super("Maze", 1000, 500, true);
		start();
	}

	@Override
	public void init() { //TODO: init
        glClearColor(0.4f, 0.5f, 1.0f, 0.0f);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glShadeModel(GL_SMOOTH);
        glLightModel(GL_LIGHT_MODEL_LOCAL_VIEWER, asFloatBuffer(new float[]{0.7f, 0.7f, 0.7f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{0.6f, 0.6f, 0.6f, 1f}));
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        //System.out.println(glGetString(GL_VERSION));
        
        wall = loadTexture("res/wall.png");
        
        camera.setBoundsx(-90.0f, 90.0f);
        Mouse.setGrabbed(true);
        loadNextLevel();
        findInitPlayer();
	}
	
	public void loadNextLevel() {
		if((maze = Maze.loadMaze(levels[levelIndex])) == null) stop();
		findInitPlayer();
		enemies.clear();
		maze.bullets.clear();
		for(int i=0; i<maze.MAZEX; i++) {
			for(int j=0; j<maze.MAZEZ; j++) {
				if(maze.get(i, j) == MazeType.ENEMY) {
					enemies.add(new BasicEnemy(i*HALLWIDTH, 0.1f, j*HALLWIDTH, 0.025f, 1));
				}
			}
		}
	}
	
	public void onGoal() {
		System.out.println("Success!");
		levelIndex++;
		if(levelIndex >= levels.length) {
			endGame();
			return;
		}
		loadNextLevel();
	}
	public void endGame() {
		System.out.println("You Win!!");
		stop();
	}
	
	public void findInitPlayer() {
		for(int i=0; i<maze.MAZEX; i++) {
        	for(int j=0; j<maze.MAZEZ; j++) {
        		if(maze.get(i, j) == MazeType.PLAYER) {
        			//camera.move(new Vector3f((i+0.5f)*HALLHEIGHT, 0.0f, (j+0.5f)*HALLHEIGHT));
        			camera.setPos(new Vector3f(HALLWIDTH*i+HALLWIDTH/2, 2.0f, HALLWIDTH*j+HALLWIDTH/2));
        			System.out.println("Found player at (" + i + "," + j + ")");
        			return;
        		}
        	}
        }
	}
	
	private void init3d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(75, (float)Display.getWidth()/(float)Display.getHeight(), 0.001f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_LIGHTING);
		glClearDepth(1.0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	}
	private void init2d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_LIGHTING);
	}

	@Override
	public void update() { //TODO: Fighting mechanics; Fix enemies
		//System.out.println("Updating " + bullets.size() + " bullets");
		if(shooting) {
			maze.bullets.add(new Bullet(new Vector3f(camera.getLookVect().geti(), 0.0f, camera.getLookVect().getk()),
					new Point3f(getPlayerPos().x, 2.0f, getPlayerPos().z)));
			camera.setXaxisrot(camera.getXaxisrot()-1f);
			//System.out.println("Bullet at " + getPlayerPos().toString());
		}
		for(int i=0; i<maze.bullets.size(); i++) {
			if(!maze.bullets.get(i).isActive()) {
				maze.bullets.remove(i);
				if(maze.bullets.size()==0) break;
			}
			maze.bullets.get(i).update();
		}
		//System.out.println("Updating " + enemies.size() + " enemies");
		for(int i=0; i<enemies.size(); i++) {
			if(!enemies.get(i).isAlive()) {
				enemies.remove(i);
				if(enemies.size()==0) break;
				if(i==enemies.size()) break;
			}
			if(enemies.get(i).isShooting()) {
				Vector3f v = toVector3f(enemies.get(i).getMoveVect());
				Point3f p = toPoint3f(enemies.get(i).getPos());
				//p.y = 2.0f;
				//p.x += v.getUnitVector().geti();
				//p.z += v.getUnitVector().getk();
				maze.bullets.add(new Bullet(v, p));
			}
			//System.out.println("Calculating hits for enemy " + i);
			for(int j=0; j<maze.bullets.size(); j++) {
				//System.out.println("Calculating hit");
				if(maze.bullets.get(j).isActive()) {
					if(enemies.get(i).hits(maze.bullets.get(j), maze))  {
						maze.bullets.get(j).active=false;
					}
				}
			}
			if(enemies.get(i).hasLineOfSight(getPlayerPos(), maze)) {
				//enemies.get(i).aim(new Point3d(camera.getPos().geti(), 0.1f, camera.getPos().getk()));
				enemies.get(i).aimTo(getPlayerPos());
				enemies.get(i).update();
			}
		}
		if(movingForward) {
			camera.moveForward(camSpeed);
			if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveBackward(camSpeed);
		}
		if(movingBackward) {
			camera.moveBackward(camSpeed);
			if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveForward(camSpeed);
		}
		if(movingLeft) {
			camera.moveLeft(camSpeed);
			if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveRight(camSpeed);
		}
		if(movingRight) {
			camera.moveRight(camSpeed);
			if(maze.get(findPlayer().x, findPlayer().y) == MazeType.WALL) camera.moveLeft(camSpeed);
		}
		if(movingUp) {
			camera.moveUp(camSpeed);
		}
		if(movingDown) {
			camera.moveDown(camSpeed);
		}
		Point2i p = findPlayer();
		if(maze.get(p.x, p.y) == MazeType.GOAL) onGoal();
		//System.out.println("Player position: (" + p.x + ", " + p.y + ")");
	}
	
	public void applyCameraTransform() {
		camera.applyTransform();
	}

	@Override
	public void render() {
		clearScreen();
		init3d();
		glPushMatrix();
		applyCameraTransform();
		
		glLight(GL_LIGHT0, GL_POSITION, asFloatBuffer(new float[]{
				camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk(), 1.0f}));
		glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0f);
		glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 10f);
		
		glColor3f(0.7f, 0.7f, 0.7f);
		glBegin(GL_QUADS); {
			//Floor
			glNormal3f(0.0f, -1.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, HALLWIDTH*maze.MAZEZ);
			glVertex3f(HALLWIDTH*maze.MAZEX, 0.0f, HALLWIDTH*maze.MAZEZ);
			glVertex3f(HALLWIDTH*maze.MAZEX, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			//Ceiling
			glNormal3f(0.0f, 1.0f, 0.0f);
			glVertex3f(0.0f, HALLHEIGHT, 0.0f);
			glVertex3f(HALLWIDTH*maze.MAZEX, HALLHEIGHT, 0.0f);
			glVertex3f(HALLWIDTH*maze.MAZEX, HALLHEIGHT, HALLWIDTH*maze.MAZEZ);
			glVertex3f(0.0f, HALLHEIGHT, HALLWIDTH*maze.MAZEZ);
		} glEnd();
		for(Bullet b : maze.bullets) {
			b.draw();
		}
		/*glColor3f(1.0f, 0.0f, 0.0f);
		glBegin(GL_LINES); {
			Point3f p = getPlayerPos();
			System.out.println(p.toString());
			glVertex3f(p.x, 2.0f, p.z);
			glVertex3f(p.x + camera.getLookVect().geti()*100.0f, 2.0f, p.z + camera.getLookVect().getk()*100.0f);
			
		}glEnd();*/
		for(int i=0; i<maze.MAZEX; i++) {
			for(int j=0; j<maze.MAZEZ; j++) {
				glPushMatrix();
				glTranslatef(HALLWIDTH*i, 0.0f, HALLWIDTH*j);
				if(maze.get(i, j) == MazeType.WALL) {
					wall.bind();
					glColor3f(0.0f, 0.0f, 0.0f);
					//drawLineCube(HALLWIDTH);
					
					glBegin(GL_QUADS); {
						glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
						if(maze.get(i, j-1) != MazeType.WALL) { //FRONT
							glNormal3f(0.0f, 0.0f, -1.0f);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(HALLWIDTH, HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(HALLWIDTH, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, 0.0f);
							
						}
						if(maze.get(i, j+1) != MazeType.WALL) { //BACK
							glNormal3f(0.0f, 0.0f, 1.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, HALLWIDTH);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(HALLWIDTH, 0.0f, HALLWIDTH);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(HALLWIDTH, HALLHEIGHT, HALLWIDTH);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, HALLHEIGHT, HALLWIDTH);
							
							
						}
						if(maze.get(i-1, j) != MazeType.WALL) { //LEFT
							glNormal3f(-1.0f, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, 0.0f);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(0.0f, 0.0f, HALLWIDTH);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(0.0f, HALLHEIGHT, HALLWIDTH);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, HALLHEIGHT, 0.0f);
						}
						if(maze.get(i+1, j) != MazeType.WALL) { //RIGHT
							glNormal3f(1.0f, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(HALLWIDTH, HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(HALLWIDTH, HALLHEIGHT, HALLWIDTH);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(HALLWIDTH, 0.0f, HALLWIDTH);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(HALLWIDTH, 0.0f, 0.0f);
							
						}
					} glEnd();
				}
				else if(maze.get(i, j)==MazeType.GOAL) {
					glColor3f(0.0f, 1.0f, 0.0f);
					drawLineCube(2.0f);
				}
				else if(maze.get(i, j)==MazeType.ENEMY) {
					glColor3f(1.0f, 0.0f, 0.0f);
					//drawLineCube(2.0f);
				}
				glPopMatrix();
			}
		}
		
		for(int i=0; i<enemies.size(); i++) {
			enemies.get(i).draw();
		}
		glPopMatrix();
		init2d();
		glColor3f(0.0f, 1.0f, 0.0f);
		glBegin(GL_LINES); { //crosshairs
			glVertex2i(width/2-5, height/2);
			glVertex2i(width/2+5, height/2);
			glVertex2i(width/2, height/2-5);
			glVertex2i(width/2, height/2+5);
		} glEnd();
		glBegin(GL_QUADS); {
			glVertex2i(0, 0);
			glVertex2i(50, 0);
			glVertex2i(50, 50);
			glVertex2i(0, 50);
			int startx = width/2;
			int starty = height/2;
			int step = 5;
			Point2i pPos = findPlayer();
			for(int i=0; i<5; i++) {
				for(int j=0; j<5; j++) {
					switch(maze.get(pPos.x + (i-2), pPos.y + (j-2))) {
					case SPACE:
						glColor3f(0.5f, 0.5f, 0.5f);
						break;
					case WALL:
						glColor3f(0.8f, 0.8f, 0.8f);
						break;
					}
					glVertex2i(startx + step*i, starty + step*j);
					glVertex2i(startx + step*i + step, starty + step*j);
					glVertex2i(startx + step*i + step, starty + step*j + step);
					glVertex2i(startx + step*i, starty + step*j + step);
				}
			}
		} glEnd();
	}

	@Override
	public void processInput() {
		shooting = false;
		while(Mouse.next()) {
			if(Mouse.getEventButton() == BUTTON_SHOOT) {
				if(Mouse.getEventButtonState()) {
					shooting = true;
				}
			}
			camera.rotX(-Mouse.getEventDY()*rotSpeed);
			camera.rotY(Mouse.getEventDX()*rotSpeed);
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
				findInitPlayer();
				break;
			}
		}
	}
	
	public static Point3f toPoint3f(Point3d p) {
		return new Point3f((float)p.x, (float)p.y, (float)p.z);
	}
	public static Vector3f toVector3f(Vector3d v)  {
		return new Vector3f((float)v.geti(), (float)v.getj(), (float)v.getk());
	}
	
	@Override
	public void kill() {
		wall.release();
		super.kill();
	}
	
	public Point2i findPlayer() {
		return findPoint(getPlayerPos());
	}
	
	public Point3f getPlayerPos() {
		return new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	
	public static Point2i findPoint(Point3f p) {
		Point2i p1 = new Point2i();
		p1.x = (int) Math.round((p.x-HALLWIDTH/2)/HALLWIDTH);
		p1.y = (int) Math.round((p.z-HALLWIDTH/2)/HALLWIDTH);
		return p1;
	}
	
	public static void main(String[] args) {
		try {
			new MazeMain();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

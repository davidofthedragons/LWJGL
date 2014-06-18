package game.maze;

import static org.lwjgl.opengl.GL11.*;
import static lib.game.RenderUtils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.game.*;
import lib.text.TextRenderer;
import math.geom.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;


/*TODO: planned features:
 * - Gun items- different damages, multiple shots, etc
 * - More powerup items- super speed, invinciblity, etc
 * - on-screen text...
 * - fix invisible enemy bug
 * - fix lighting bugs
 *   - shaders
 *   - custom texture loading
 */

public class MazeMain extends AbstractGame {

	public static boolean devMode = true;
	private static String version = "Beta v0.2";
	
	private static boolean fullScreen = true;
	private static boolean mouseGrabbed = true;
	
	private TextRenderer textRenderer;
	
	private float FOV = 75;
	private Camera camera = new Camera();
	private float camSpeed = 0.13f;
	private float rotSpeed = 0.10f;
	private boolean movingForward = false;
	private boolean movingBackward = false;
	private boolean movingRight = false;
	private boolean movingLeft = false;
	private boolean movingUp = false;
	private boolean movingDown = false;
	
	private boolean flying = false;
	
	private final int KEY_FORWARD = Keyboard.KEY_W;
	private final int KEY_BACKWARD = Keyboard.KEY_S;
	private final int KEY_LEFT = Keyboard.KEY_A;
	private final int KEY_RIGHT = Keyboard.KEY_D;
	private final int KEY_UP = Keyboard.KEY_SPACE;
	private final int KEY_DOWN = Keyboard.KEY_LSHIFT;
	private final int KEY_CLOSE = Keyboard.KEY_ESCAPE;
	private final int KEY_RESET = Keyboard.KEY_RETURN;
	private final int KEY_TOGGLE_CHEATS = Keyboard.KEY_C;
	
	private final int BUTTON_SHOOT = 0;
	
	private boolean shooting = false;
	private boolean hit = false;
	
	
	Maze maze;
	Player player;
	
	private String[] levels = {"res/map-test.png", "res/map0.png", "res/map1.png", "res/map2.png"/*, "res/map-mult1.png", "res/map-mult2.png"*/};
	private int levelIndex = 1;
	
//	Texture wall;
	int wallTexture;
	Model wallModel;
	int wallDisplayList;
	
	public MazeMain() throws LWJGLException {
		super("Maze " + version, 1000, 500, fullScreen);
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
        
//        wall = loadTexture("res/wall.png");
        wallTexture = RenderUtils.loadTexture("res/wall.png", true);
        
        try {
			textRenderer = new TextRenderer(new File("res/font1.jpg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        player = new Player(new Point3f(), 20, 50);
        player.getEncoding();
        camera.setBoundsx(-90.0f, 90.0f);
        Mouse.setGrabbed(mouseGrabbed);
        loadNextLevel();
        findInitPlayer();
	}
	
	public void loadNextLevel() {
		if((maze = Maze.loadMaze(levels[levelIndex])) == null) stop();
		findInitPlayer();
		maze.enemies.clear();
		maze.bullets.clear();
		for(int i=0; i<maze.MAZEX; i++) {
			for(int j=0; j<maze.MAZEZ; j++) {
				if(maze.get(i, j) == MazeType.ENEMY) {
					maze.enemies.add(new BasicEnemy(i*Maze.HALLWIDTH, 1.0f, j*Maze.HALLWIDTH, 0.05f, 3));
				}
			}
		}
	}
	
	public void onGoal() {
		System.out.println("Success!");
		levelIndex++;
		if(levelIndex >= levels.length) {
			gameOver("You win!");
			return;
		}
		loadNextLevel();
	}
	
	public void findInitPlayer() {
		for(int i=0; i<maze.MAZEX; i++) {
        	for(int j=0; j<maze.MAZEZ; j++) {
        		if(maze.get(i, j) == MazeType.PLAYER) {
        			//camera.move(new Vector3f((i+0.5f)*Maze.HALLHEIGHT, 0.0f, (j+0.5f)*Maze.HALLHEIGHT));
        			player.pos = new Point3f(Maze.HALLWIDTH*i+Maze.HALLWIDTH/2, 2.0f, Maze.HALLWIDTH*j+Maze.HALLWIDTH/2);
        			camera.setPos(new Vector3f(Maze.HALLWIDTH*i+Maze.HALLWIDTH/2, 2.0f, Maze.HALLWIDTH*j+Maze.HALLWIDTH/2));
        			System.out.println("Found player at (" + i + "," + j + ")");
        			return;
        		}
        	}
        }
	}
	
	private void init3d() {
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
        enableLighting();
	}
	
	private void enableLighting() {
		glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glShadeModel(GL_SMOOTH);
        glLightModel(GL_LIGHT_MODEL_LOCAL_VIEWER, asFloatBuffer(new float[]{0.7f, 0.7f, 0.7f, 1f}));
		glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(new float[]{0.6f, 0.6f, 0.6f, 1f}));
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
	}
	private void init2d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_LIGHTING);
		glDisable(GL_LIGHT0);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDisable(GL_CULL_FACE);
		glDisable(GL_COLOR_MATERIAL);
	}

	@Override
	public void update() { //TODO: update
		//System.out.println("Updating " + bullets.size() + " bullets");
		if(player.health <= 0) gameOver("You Died.");
		if(shooting && player.ammo > 0) {
			maze.bullets.add(new Bullet(new Vector3f(camera.getLookVect().geti(), 0.0f, camera.getLookVect().getk()),
					new Point3f(getPlayerPos().x, 2.0f, getPlayerPos().z)));
			player.ammo--;
			System.out.println("Ammo left: " + player.ammo);
//			camera.setXaxisrot(camera.getXaxisrot()-1f);
			//System.out.println("Bullet at " + getPlayerPos().toString());
		}
		for(int i=0; i<maze.bullets.size(); i++) {
			if(!maze.bullets.get(i).isActive()) {
				maze.bullets.remove(i);
				if(maze.bullets.size()==0) break;
			}
			if(i == maze.bullets.size()) break;
			maze.bullets.get(i).update();
		}
		//System.out.println("Updating " + enemies.size() + " enemies");
		for(int i=0; i<maze.enemies.size(); i++) {
//			System.out.println("Updating enemies[" + i + "]");
//			System.out.println("Checking alive");
			if(!maze.enemies.get(i).isAlive()) {
				maze.enemies.remove(i);
				if(maze.enemies.size()==0) break;
				if(i==maze.enemies.size()) break;
			}
//			System.out.println("Checking shooting");
			if(maze.enemies.get(i).isShooting()) {
				Vector3f v = toVector3f(maze.enemies.get(i).getMoveVect());
				Point3f p = toPoint3f(maze.enemies.get(i).getPos());
				//p.y = 2.0f;
				//p.x += v.getUnitVector().geti();
				//p.z += v.getUnitVector().getk();
				maze.bullets.add(new Bullet(v, p));
			}
			//System.out.println("Calculating hits for enemy " + i);
			
			for(Bullet b : maze.bullets) {
				//System.out.println("Calculating hit");
				if(b.isActive()) {
					if(hits(b, maze, toPoint3f(maze.enemies.get(i).getPos()), maze.enemies.get(i).size))  {
						maze.enemies.get(i).hurt(1);
						b.active=false;
					}
				}
			}
//			System.out.println("Checking line of sight and stuff");
			if(maze.enemies.get(i).hasLineOfSight(getPlayerPos(), maze)) {
				//maze.enemies.get(i).aim(new Point3d(camera.getPos().geti(), 0.1f, camera.getPos().getk()));
				maze.enemies.get(i).aimTo(getPlayerPos());
				maze.enemies.get(i).update();
			}
		}
		hit = false;
		for(Bullet b : maze.bullets) {
			if (b.isActive()) {
				if (hits(b, maze, player.pos, player.size)) {
					player.health--;
					hit = true;
					System.out.println("Player Health: " + player.health);
					b.active = false;
				}
			}
			
		}
		
		if(movingForward) {
			movePlayerForward();
		}
		if(movingBackward) {
			movePlayerBackward();
		}
		if(movingLeft) {
			movePlayerLeft();
		}
		if(movingRight) {
			movePlayerRight();
		}
		if(movingUp) {
			if(flying) camera.moveUp(camSpeed);
		}
		if(movingDown) {
			if(flying) camera.moveDown(camSpeed);
		}
		Point2i p = findPlayer();
		if(maze.get(p.x, p.y) == MazeType.GOAL) onGoal();
		if(maze.getItem(p.x, p.y) != null) {
			Item item = maze.getItem(p.x, p.y);
			maze.setItem(p.x, p.y, null);
			item.pickedUp(player);
		}
		//System.out.println("Player position: (" + p.x + ", " + p.y + ")");
	}
	
	public void movePlayerLeft() {
		camera.moveLeft(camSpeed);
		Point2i p = findPlayer();
		if((maze.get(p.x, p.y) == MazeType.WALL && !flying) || !maze.isInBounds(p)) camera.moveRight(camSpeed);
		player.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	public void movePlayerRight() {
		camera.moveRight(camSpeed);
		Point2i p = findPlayer();
		if((maze.get(p.x, p.y) == MazeType.WALL && !flying) || !maze.isInBounds(p)) camera.moveLeft(camSpeed);
		player.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	public void movePlayerForward() {
		camera.moveForward(camSpeed);
		Point2i p = findPlayer();
		if((maze.get(p.x, p.y) == MazeType.WALL && !flying) || !maze.isInBounds(p)) camera.moveBackward(camSpeed);
		player.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	public void movePlayerBackward(){
		camera.moveBackward(camSpeed);
		Point2i p = findPlayer();
		if((maze.get(p.x, p.y) == MazeType.WALL && !flying) || !maze.isInBounds(p)) camera.moveForward(camSpeed);
		player.pos = new Point3f(camera.getPos().geti(), camera.getPos().getj(), camera.getPos().getk());
	}
	
	public boolean hits(Bullet b, Maze m, Point3f hitPos, float size) {
		Point3f pos = new Point3f(b.origin.x, b.origin.y, b.origin.z);
		while(Math.abs(hitPos.x-pos.x)>1 || Math.abs(hitPos.y-pos.y)>1 || Math.abs(hitPos.z-pos.z)-1>1) {
			pos.x += b.v.geti(); pos.y += b.v.getj(); pos.z += b.v.getk();
			Point2i p = MazeMain.findPoint(pos);
			Point2i epos = MazeMain.findPoint(hitPos);
			if(p.x == epos.x && p.y == epos.y) {
				return true;
			}//*/
			if(pos.x>hitPos.x && pos.x<hitPos.x+size && pos.y>hitPos.z && pos.y<hitPos.z+size) {
				return true;
			}
			if(m.get(p.x, p.y) == MazeType.WALL) {
				//System.out.println("Wall.");
				return false;
			}
			if(p.x<0 || p.x>m.MAZEX || p.y<0 || p.y>m.MAZEZ) return false;
		}
		return false;
	}
	
	public void applyCameraTransform() {
		camera.applyTransform();
	}

	@Override
	public void render() { //TODO: render3d
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
			glVertex3f(0.0f, 0.0f, Maze.HALLWIDTH*maze.MAZEZ);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, 0.0f, Maze.HALLWIDTH*maze.MAZEZ);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			//Ceiling
			glNormal3f(0.0f, 1.0f, 0.0f);
			glVertex3f(0.0f, Maze.HALLHEIGHT, 0.0f);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, Maze.HALLHEIGHT, 0.0f);
			glVertex3f(Maze.HALLWIDTH*maze.MAZEX, Maze.HALLHEIGHT, Maze.HALLWIDTH*maze.MAZEZ);
			glVertex3f(0.0f, Maze.HALLHEIGHT, Maze.HALLWIDTH*maze.MAZEZ);
		} glEnd();
		for(Bullet b : maze.bullets) {
			b.draw();
		}
		for(int i=0; i<maze.enemies.size(); i++) {
			maze.enemies.get(i).draw();
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
				glTranslatef(Maze.HALLWIDTH*i, 0.0f, Maze.HALLWIDTH*j);
				if(maze.get(i, j) == MazeType.WALL) {
//					wall.bind();
					glEnable(GL_TEXTURE_2D);
					RenderUtils.bindTexture(wallTexture);
//					glColor3f(0.0f, 0.0f, 0.0f);
					//drawLineCube(Maze.HALLWIDTH);
					glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
					
					glBegin(GL_QUADS); {
						glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
						if(maze.get(i, j-1) != MazeType.WALL) { //FRONT
							glNormal3f(0.0f, 0.0f, -1.0f);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, 0.0f);
							
						}
						
						if(maze.get(i, j+1) != MazeType.WALL) { //BACK
							glNormal3f(0.0f, 0.0f, 1.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							
						}
						
						if(maze.get(i-1, j) != MazeType.WALL) { //LEFT
							glNormal3f(-1.0f, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(0.0f, 0.0f, 0.0f);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(0.0f, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(0.0f, Maze.HALLHEIGHT, 0.0f);
						}
						if(maze.get(i+1, j) != MazeType.WALL) { //RIGHT
							glNormal3f(1.0f, 0.0f, 0.0f);
							glTexCoord2f(0.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, 0.0f);
							glTexCoord2f(1.0f, 1.0f); glVertex3f(Maze.HALLWIDTH, Maze.HALLHEIGHT, Maze.HALLWIDTH);
							glTexCoord2f(1.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, Maze.HALLWIDTH);
							glTexCoord2f(0.0f, 0.0f); glVertex3f(Maze.HALLWIDTH, 0.0f, 0.0f);
							
						}
					} glEnd();
					glDisable(GL_TEXTURE_2D);
				}
				else if(maze.get(i, j)==MazeType.GOAL) {
					glColor3f(0.0f, 1.0f, 0.0f);
					drawLineCube(2.0f);
				}
				else {
					if(maze.getItem(i, j) != null) maze.getItem(i, j).draw();
				}
//				else if(maze.get(i, j)==MazeType.ENEMY) {
//					glColor3f(1.0f, 0.0f, 0.0f);
//					//drawLineCube(2.0f);
//				}
				glPopMatrix();
			}
		}
		
		
		glPopMatrix();
		init2d(); //TODO render 2d
//		textRenderer.renderText("Health " + player.health, new Point2f(0.0f, 0.0f), 20.0f, 15.0f);
		glColor3f(1.0f, 0.0f, 0.0f);
		glBegin(GL_LINES); { //crosshairs
			glVertex2i(width/2-5, height/2);
			glVertex2i(width/2+4, height/2);
			glVertex2i(width/2, height/2-5);
			glVertex2i(width/2, height/2+4);
//			glVertex2i(width/2-50, height/2 - 50);
//			glVertex2i(width/2 + 50, height/2 - 50);
//			glVertex2i(width/2 + 50, height/2 + 50);
//			glVertex2i(width/2 - 50, height/2 + 50);
		} glEnd();
//		wall.bind();
		glBegin(GL_QUADS); { //Minimap
			int step = 25;
			Point2i pPos = findPlayer();
			int width = 7, height = 7;
			int startx = this.width - 50 - step*width - 10;
			int starty = 4*this.height/6;
			
			glColor3f(0.4f, 0.4f, 0.4f);
			glVertex2f(startx - 5, starty - 5);
			glVertex2f(startx - 5, starty + height*step + 5);
			glVertex2f(startx + width*step + 5, starty + height*step + 5);
			glVertex2f(startx + width*step + 5, starty - 5);
			
			for(int i=0; i<width; i++) {
				for(int j=0; j<height; j++) {
					switch(maze.get(pPos.x + (i-width/2), pPos.y + (j-height/2))) {
					case SPACE:
						glColor3f(0.3f, 0.3f, 0.3f);
						break;
					case WALL:
						glColor3f(0.5f, 0.5f, 0.5f);
						break;
					case GOAL:
						glColor3f(0.0f, 0.0f, 1.0f);
						break;
					case ENEMY:
						glColor3f(1.0f, 0.0f, 0.0f);
						break;
					case ITEM_HEALTHBONUS:
						if(maze.getItem(pPos.x + (i-width/2), pPos.y + (j-height/2)) != null)
							glColor3f(1.0f, 0.0f, 1.0f);
						else glColor3f(0.3f, 0.3f, 0.3f);
						break;
					case ITEM_AMMOBONUS:
						if(maze.getItem(pPos.x + (i-width/2), pPos.y + (j-height/2)) != null)
							glColor3f(0.0f, 1.0f, 1.0f);
						else glColor3f(0.3f, 0.3f, 0.3f);
						break;
					default:
						glColor3f(0.3f, 0.3f, 0.3f);
						break;
					}
					if(pPos.x + (i-width/2) == pPos.x && pPos.y + (j-height/2) == pPos.y) glColor3f(0.0f, 1.0f, 0.0f);
					glVertex2i(startx + step*i, starty + step*j);
					glVertex2i(startx + step*i + step, starty + step*j);
					glVertex2i(startx + step*i + step, starty + step*j + step);
					glVertex2i(startx + step*i, starty + step*j + step);
				}
			}
			
		} glEnd();
		
		glBegin(GL_QUADS); { //Health bar
			glColor3f(0.4f, 0.4f, 0.4f);
			float startx = 50;
			float starty = 50;
			float healthBarTotalSize = 150;
			float healthBarHeight = 25;
			float healthFraction = (float)(player.health) / (float)(player.startHealth);
			glVertex2f(startx - 5, starty - 5);
			glVertex2f(startx - 5, starty + healthBarHeight + 5);
			glVertex2f(startx + healthBarTotalSize + 5, starty + healthBarHeight + 5);
			glVertex2f(startx + healthBarTotalSize + 5, starty - 5);
			
			glColor3f(0.2f, 0.2f, 0.2f);
			glVertex2f(startx, starty);
			glVertex2f(startx, starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize, starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize, starty);
			
			glColor3f(0.0f, 1.0f, 0.0f);
			glVertex2f(startx, starty);
			glVertex2f(startx, starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize*(healthFraction), starty + healthBarHeight);
			glVertex2f(startx + healthBarTotalSize*(healthFraction), starty);
		} glEnd();
		
		glBegin(GL_QUADS); { //Ammo Bar
			float ammoBarWidth = 25;
			float ammoBarHeight = 150;
			float startx = width - 50 - ammoBarWidth - 10;
			float starty = 50;
			float ammoFraction = (float)(player.ammo) / (float)(player.maxAmmo);
			glColor3f(0.4f, 0.4f, 0.4f);
			glVertex2f(startx - 5, starty - 5);
			glVertex2f(startx - 5, starty + ammoBarHeight + 5);
			glVertex2f(startx + ammoBarWidth + 5, starty + ammoBarHeight + 5);
			glVertex2f(startx + ammoBarWidth + 5, starty - 5);
			
			glColor3f(0.2f, 0.2f, 0.2f);
			glVertex2f(startx, starty);
			glVertex2f(startx, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty);
			
			glColor3f(1.0f, 0.0f, 0.0f);
			glVertex2f(startx, starty + ammoBarHeight - ammoBarHeight*ammoFraction);
			glVertex2f(startx, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty + ammoBarHeight);
			glVertex2f(startx + ammoBarWidth, starty + ammoBarHeight - ammoBarHeight*ammoFraction);
			
//			float step = 5;
//			float istep = player.maxAmmo / 25;
//			glColor3f(1.0f, 0.0f, 0.0f);
//			for(int i=0; i<player.ammo; i += istep) {
//				glVertex2f(startx, starty + 2*step*(i/istep));
//				glVertex2f(startx, starty + 2*step*(i/istep) + step);
//				glVertex2f(startx + 25, starty + 2*step*(i/istep) + step);
//				glVertex2f(startx + 25, starty + 2*step*(i/istep));
//			}
			
		} glEnd();
	}

	@Override
	public void processInput() { //TODO processInput
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
			case KEY_TOGGLE_CHEATS:
				if (Keyboard.getEventKeyState()) {
					System.out.println("cheats are " + !flying);
					flying = !flying;
				}
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
	
	public void gameOver(String message) {
		System.out.println(message);
		stop();
	}
	
	@Override
	public void kill() {
//		wall.release();
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
		p1.x = (int) Math.round((p.x-Maze.HALLWIDTH/2)/Maze.HALLWIDTH);
		p1.y = (int) Math.round((p.z-Maze.HALLWIDTH/2)/Maze.HALLWIDTH);
		return p1;
	}
	
	public static void main(String[] args) {
		if (!devMode) {
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
		}
		try {
			new MazeMain();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

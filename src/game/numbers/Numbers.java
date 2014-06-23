package game.numbers;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import lib.game.*;
import math.geom.Point2i;

public class Numbers extends AbstractGame { //2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048

	public static final int KEY_UP = Keyboard.KEY_UP;
	public static final int KEY_DOWN = Keyboard.KEY_DOWN;
	public static final int KEY_RIGHT = Keyboard.KEY_RIGHT;
	public static final int KEY_LEFT = Keyboard.KEY_LEFT;
	public static final int KEY_EXIT = Keyboard.KEY_ESCAPE;
	
	Random rand = new Random();
	
	int w = 4, h = 4;
	
	int[][] grid = new int[w][h];
	
	int numberTex;
	
	public Numbers() throws LWJGLException {
		super("2048 Clone", 600, 600);
		start();
	}

	@Override
	public void init() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
		glLoadIdentity();
		
		numberTex = RenderUtils.loadTexture("res/Numbers.png", true);
		
		addRandom();
		addRandom();
		for(int i=0; i<h; i++) {
			for(int j=0; j<w; j++) {
				System.out.print(get(j, i) + " ");
			}
			System.out.println();
		}
	}
	
	public int get(int i, int j) {
		if(i>=0 && i<w && j>=0 && j<h) return grid[i][j];
		else return -1;
	}
	
	public void set(int i, int j, int num) {
		if(i>=0 && i<w && j>=0 && j<h) grid[i][j] = num;
	}
	
	public void move(Dir dir) {
		switch(dir) {
		case UP:
			for(int k=0; k<w; k++) {
				for(int i=0; i<w; i++) {
					for(int j=0; j<h; j++) {
						if(get(i, j-1) == 0) {
							set(i, j-1, get(i, j));
							set(i, j, 0);
						}
					}
				}
			}
			break;
		case DOWN:
			for(int k=0; k<w; k++) {
				for(int i=0; i<w; i++) {
					for(int j=h-2; j>=0; j--) {
						if(get(i, j+1) == 0) {
							set(i, j+1, get(i, j));
							set(i, j, 0);
						}
					}
				}
			}
			break;
		case RIGHT:
			for(int k=0; k<w; k++) {
				for(int i=w-2; i>=0; i--) {
					for(int j=0; j<h; j++) {
						if(get(i+1, j) == 0) {
							set(i+1, j, get(i, j));
							set(i, j, 0);
						}
					}
				}
			}
			break;
		case LEFT:
			for(int k=0; k<w; k++) {
				for(int i=1; i<w; i++) {
					for(int j=0; j<h; j++) {
						if(get(i-1, j) == 0) {
							set(i-1, j, get(i, j));
							set(i, j, 0);
						}
					}
				}
			}
			break;
		}
	}
	public void combine(Dir dir) {
		switch(dir) {
		case UP:
			for(int i=0; i<w; i++) {
				for(int j=1; j<h; j++) {
					if(get(i, j-1) == get(i, j)) {
						set(i, j-1, 2*get(i, j));
						set(i, j, 0);
						continue;
					}
				}
			}
			break;
		case DOWN:
			for(int i=w-1; i>=0; i--) {
				for(int j=h-2; j>=0; j--) {
					if(get(i, j+1) == get(i, j)) {
						set(i, j+1, 2*get(i, j));
						set(i, j, 0);
						continue;
					}
				}
			}
			break;
		case RIGHT:
			for(int i=w-2; i>=0; i--) {
				for(int j=h-1; j>=0; j--) {
					if(get(i+1, j) == get(i, j)) {
						set(i+1, j, 2*get(i, j));
						set(i, j, 0);
						continue;
					}
				}
			}
			break;
		case LEFT:
			
			for(int i=1; i<w; i++) {
				for(int j=0; j<h; j++) {
					if(get(i-1, j) == get(i, j)) {
						set(i-1, j, 2*get(i, j));
						set(i, j, 0);
						continue;
					}
				}
			}
			break;
		}
	}
	
	public void moveUp() {
		move(Dir.UP);
		combine(Dir.UP);
		move(Dir.UP);
		addRandom();
	}
	public void moveDown() {
		move(Dir.DOWN);
		combine(Dir.DOWN);
		move(Dir.DOWN);
		addRandom();
	}
	public void moveRight() {
		move(Dir.RIGHT);
		combine(Dir.RIGHT);
		move(Dir.RIGHT);
		addRandom();
	}
	public void moveLeft() {
		move(Dir.LEFT);
		combine(Dir.LEFT);
		move(Dir.LEFT);
		addRandom();
	}
	
	public void addRandom() {
		if(isFull()) return;
		int numInsert = 2*(rand.nextInt(2)+1);
		while(true) {
			int x = rand.nextInt(w);
			int y = rand.nextInt(h);
			if(get(x, y) == 0) {set(x, y, numInsert); break;} 
		}
	}
	
	public boolean hasMoves() {
		for(int i=0; i<w; i++) {
			for(int j=0; j<h; j++) {
				if(get(i, j) == 0) return true;
				if(get(i+1, j) == get(i, j) || get(i+1, j) == 0) return true;
				if(get(i, j+1) == get(i, j) || get(i, j+1) == 0) return true;
			}
		}
		return false;
	}
	
	public boolean checkWin() {
		for(int i=0; i<w; i++) {
			for(int j=0; j<h; j++){
				if(get(i, j) == 2048) return true;
			}
		}
		return false;
	}
	
	public boolean isFull() {
		for(int i=0; i<w; i++) {
			for(int j=0; j<h; j++) {
				if(get(i, j) == 0) return false;
			}
		}
		return true;
	}

	@Override
	public void update() {
		if(checkWin()) {
			System.out.println("You Won!");
			stop();
		}
		if(!hasMoves()) {
			System.out.println("Game Over");
			stop();
		}
	}

	@Override
	public void render() {
		RenderUtils.clearScreen();
		int deltaw = width/w;
		int deltah = height/h;
		glBegin(GL_QUADS); {
			for(int i=0; i<w; i++) {
				for(int j=0; j<h; j++) {
					if(get(i, j) == 0) continue;
					glColor3f(0.5f, 0.5f, 0.5f);
					switch(get(i, j)) {
					case 2:
						glColor3f(1.0f, 1.0f, 1.0f);
						break;
					case 4:
						glColor3f(1.0f, 0.0f, 0.0f);
						break;
					case 8:
						glColor3f(1.0f, 0.549f, 0.0f);
						break;
					case 16:
						glColor3f(1.0f, 0.843f, 0.0f);
						break;
					case 32:
						glColor3f(0.0f, 1.0f, 0.0f);
						break;
					case 64:
						glColor3f(0.0f, 0.980f, 0.604f);
						break;
					case 128:
						glColor3f(0.0f, 0.0f, 1.0f);
						break;
					case 256:
						glColor3f(0.282f, 0.389f, 0.545f);
						break;
					case 512:
						glColor3f(0.502f, 0.0f, 0.502f);
						break;
					case 1024:
						glColor3f(0.729f, 0.333f, 0.827f);
						break;
					case 2048:
						glColor3f(1.0f, 0.0f, 1.0f);
						break;
					}
					// (log2(g[i][j])-1)/11: get fraction of 11 for tex coords
					double texStart = (Math.log(get(i, j)) / Math.log(2.0) - 1.0) / 11.0; 
//					System.out.println(texStart*11);
					double texDelta = 1.0/11.0;
					glTexCoord2d(texStart+texDelta, 1.0);	glVertex2i(i*deltaw+deltaw, j*deltah+deltah);
					glTexCoord2d(texStart, 1.0);			glVertex2i(i*deltaw, j*deltah+deltah);
					glTexCoord2d(texStart, 0.0);			glVertex2i(i*deltaw, j*deltah);
					glTexCoord2d(texStart+texDelta, 0.0);	glVertex2i(i*deltaw+deltaw, j*deltah);
					
					
				}
			}
		} glEnd();
	}

	@Override
	public void processInput() {
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				switch(Keyboard.getEventKey()) {
				case KEY_UP:
					moveUp();
					break;
				case KEY_DOWN:
					moveDown();
					break;
				case KEY_RIGHT:
					moveRight();
					break;
				case KEY_LEFT:
					moveLeft();
					break;
				case KEY_EXIT:
					kill();
					break;
				}
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			new Numbers();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	private enum Dir {UP, DOWN, RIGHT, LEFT}
}

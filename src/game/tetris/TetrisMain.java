package game.tetris;


import static org.lwjgl.opengl.GL11.*;
import static lib.game.RenderUtils.*;

import java.util.ArrayList;
import java.util.Random;

import graphics.Color3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import lib.game.AbstractEntity;
import lib.game.AbstractGame;
import math.geom.Point2i;

public class TetrisMain extends AbstractGame {
	
	Random rand = new Random();
	int frameCount=0;
	int tickAmount = 50;
	int score = 0;
	
	final int KEY_RIGHT = Keyboard.KEY_RIGHT;
	final int KEY_LEFT = Keyboard.KEY_LEFT;
	final int KEY_ROTATE = Keyboard.KEY_UP;
	final int KEY_DOWN = Keyboard.KEY_DOWN;
	final int KEY_PAUSE = Keyboard.KEY_SPACE;
	boolean movingDown = false;
	
	boolean paused = true;
	
	int gridSize = 20;
	int gmaxx, gmaxy;
	
	Square[][] grid;
	
	int[][] s1 = {{0,0,0},
				  {0,0,0},
				  {1,1,1}}; //Line
	int[][] s2 = {{0,0,0},
				  {0,1,0},
				  {1,1,1}}; //Center thingy
	int[][] s3 = {{1,0,0},
				  {1,0,0},
				  {1,1,0}}; //L
	int[][] s4 = {{0,0,0},
				  {1,1,0},
				  {1,1,0}}; //Square
	int[][] s5 = {{1,1,0},
				  {0,1,1},
				  {0,0,0}}; //Diagonal l
	int[][] s6 = {{0,0,1},
			  	  {0,0,1},
			  	  {0,1,1}}; //Backwards L
	int[][] s7 = {{0,1,1},
				  {1,1,0},
				  {0,0,0}}; //Diagonal r
	
	Shape[] shapelib = {
		new Shape(s1, 0, 2, 2, 2),
		new Shape(s2, 0, 2, 1, 2),
		new Shape(s3, 0, 1, 0, 2),
		new Shape(s4, 0, 1, 1, 2),
		new Shape(s5, 0, 2, 0, 1),
		new Shape(s6, 1, 2, 0, 2),
		new Shape(s7, 0, 2, 0, 1)
	};
	
	Shape currentShape;
	
	public TetrisMain() throws LWJGLException {
		super("Tetris", 600, 600);
		gmaxx = width/gridSize;
		gmaxy = height/gridSize;
		grid = new Square[gmaxx][gmaxy];
		start();
	}

	@Override
	public void init() {
		initStd2d(width, height);
		for(int i=0; i<gmaxx; i++) {
			for(int j=0; j<gmaxy; j++) {
				grid[i][j] = null;
			}
		}
		currentShape = new Shape(shapelib[rand.nextInt(shapelib.length)], gmaxx/2, 0);
	}

	@Override
	public void update() {
		if (!paused) {
			if (frameCount == tickAmount) {
				frameCount = 0;
				tick();
			}
			frameCount++;
		}
	}
	
	public void tick() {
		if(!currentShape.moveDown()) {
			currentShape.stop();
			currentShape = new Shape(shapelib[rand.nextInt(shapelib.length)], gmaxx/2, 0);
		}
		checkRows();
	}
	
	public void checkRows() {
		for (int j=0; j<gmaxy; j++) {
			boolean fullRow = true;
			for (int i = 0; i < gmaxx; i++) {
				if (grid[i][j] == null) {
					fullRow = false;
					break;
				}
			}
			if (fullRow) {
				//System.out.println(fullRow);
				score++;
				System.out.println(score);
				for (int k = gmaxy - 1; k > 0; k--) {
					for (int i = 0; i < gmaxx; i++) {
						grid[i][k] = grid[i][k - 1];
					}
				}
				for (int i = 0; i < gmaxx; i++) {
					grid[i][0] = null;
				}
			}
		}
		
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		currentShape.draw();
		for(int i=0; i<gmaxx; i++) {
			for(int j=0; j<gmaxy; j++) {
				if(grid[i][j]!=null) grid[i][j].draw(i, j);
			}
		}
	}

	@Override
	public void processInput() {
		if(paused) {
			while(Keyboard.next()) {
				if(Keyboard.getEventKey()==KEY_PAUSE && Keyboard.getEventKeyState()) paused = false;
			}
			return;
		}
		while(Keyboard.next()) {
			switch(Keyboard.getEventKey()) {
			case KEY_RIGHT:
				if (Keyboard.getEventKeyState()) {
					currentShape.moveRight();
				}
				break;
			case KEY_LEFT:
				if (Keyboard.getEventKeyState()) {
					currentShape.moveLeft();
				}
				break;
			case KEY_DOWN:
				if (Keyboard.getEventKeyState()) {
					movingDown = true;
				} else movingDown = false;
				break;
			case KEY_ROTATE:
				if (Keyboard.getEventKeyState()) {
					currentShape.rotate();
				}
				break;
			case KEY_PAUSE:
				if (Keyboard.getEventKeyState()) {
					paused = true;
				}
				break;
			}
			if(movingDown) currentShape.moveDown();
		}
	}
	
	private class Shape extends AbstractEntity {

		Square[][] formation = new Square[3][3];
		int[][] form;
		Color3f color;
		public int gx, gy; //Grid x and y
		int lBound, rBound, uBound, bBound;
		
		public Shape(int[][] form) {
			super(0.0);
			this.form = form;
		}
		
		public Shape(int[][] form, int l, int r, int u, int b) {
			this(form);
			setBounds(u, b, r, l);
		}
		
		public Shape(Shape s, int gx, int gy) {
			super(0.0);
			color = new Color3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
			setgx(gx);
			setgy(gy);
			for(int i=0; i<3; i++) {
				for(int j=0; j<3; j++) {
					if(s.form[i][j]==1) {
						formation[i][j] = new Square(color);
					}
				}
			}
			lBound = s.lBound;
			rBound = s.rBound;
			uBound = s.uBound;
			bBound = s.bBound;
		}
		
		public void setBounds(int l, int r, int u, int b) {
			lBound = l; rBound = r; uBound = u; bBound = b;
		}
		
		public void setgx(int gx) {
			this.gx=gx;
			x = gx*gridSize;
		}
		public void setgy(int gy) {
			this.gy=gy;
			y = gy*gridSize;
		}
		public void setGridPos(Point2i p) {
			setgx(p.x);
			setgy(p.y);
		}
		public int getgx() {return gx;}
		public int getgy() {return gy;}
		public Point2i getGridPos() {return new Point2i(gx, gy);}
		
		@Override
		public void draw() {
			//System.out.println("Draw");
			for(int i=0; i<3; i++) {
				for(int j=0; j<3; j++) {
					if (formation[i][j]!=null) {
						formation[i][j].draw(gx+i, gy+j);
					}
					else {
						glColor3f(1.0f, 1.0f, 1.0f);
						glBegin(GL_QUADS);
							glVertex2d((gx+i)*gridSize, (gy+j)*gridSize);
							glVertex2d((gx+i)*gridSize + gridSize, (gy+j)*gridSize);
							glVertex2d((gx+i)*gridSize + gridSize, (gy+j)*gridSize + gridSize);
							glVertex2d((gx+i)*gridSize, (gy+j)*gridSize + gridSize);
						glEnd();
					}
				}
			}
		}

		@Override
		public void update() {}
		
		public void rotate() {
			//System.out.println("Rotate");
			Square[][] hold = new Square[3][3];
			hold[0][0] = formation[0][2];
			hold[0][1] = formation[1][2];
			hold[0][2] = formation[2][2];
			hold[1][0] = formation[0][1];
			hold[1][2] = formation[2][1];
			hold[1][1] = formation[1][1];
			hold[2][0] = formation[0][0];
			hold[2][1] = formation[1][0];
			hold[2][2] = formation[2][0];
			formation = hold;
			int hl = lBound, hr = rBound, hu = uBound, hb = bBound;
			uBound = hl;
			rBound = hu;
			bBound = hr;
			lBound = hb;
		}
		
		public boolean checkScreenBounds(int gx, int gy) {
			if(gx+rBound>=gmaxx) return false;
			if(gx+lBound<=0) return false;
			if(gy+bBound>=gmaxy) return false;
			if(gy+uBound<=0) return false;
			return true;
		}
		
		public boolean moveRight() {
			/*if (gx>=gmaxx-4) {
				for (int i = 0; i < 3; i++) {
					if (formation[2][i] != null)
						if(gx > gmaxx - 3) return false;
				}
				for (int i = 0; i < 3; i++) {
					if (formation[1][i] != null)
						if(gx > gmaxx - 2) return false;
				}
				if(gx > gmaxx-1) return false;
			}*/
			if(checkScreenBounds(gx+1, gy))
				setgx(getgx()+1);
			return true;
		}
		
		public boolean moveLeft() {
			if(checkScreenBounds(gx-1, gy)) setgx(getgx()-1);
			else return false;
			return true;
		}
		
		public boolean moveDown() {
			if(!checkGrid(gx, gy+1)) return false;
			setgy(getgy()+1);
			//System.out.println("Move Down");
			return true;
		}
		
		public void stop() {
			setToGrid();
		}
		
		public void setToGrid() {
			try {
				for(int i=0; i<3; i++) {
					for(int j=0; j<3; j++) {
						if(formation[i][j] != null)
							grid[gx+i][gy+j] = formation[i][j];
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		
		private boolean checkGrid(int gx, int gy) {
			if (gy>=gmaxy-4) {
				for (int i = 0; i < 3; i++) {
					if (formation[i][2] != null)
						if(gy > gmaxy - 3) return false;
				}
				for (int i = 0; i < 3; i++) {
					if (formation[i][1] != null)
						if(gy > gmaxy - 2) return false;
				}
				if(gy > gmaxy-1) return false;
			}
			for(int i=0; i<3; i++) {
				for(int j=0; j<3; j++) {
					if(gx+i>=gmaxx || gy+j>=gmaxy) continue;
					if(grid[gx+i][gy+j]!=null && formation[i][j]!=null) return false;
				}
			}
			
			return true;
		}
	}
	
	private class Square  {
		
		Color3f color;
		
		public Square(Color3f color) {
			this.color=color;
		}

		public void draw(int gx, int gy) {
			glColor3f(color.r, color.g, color.b);
			glBegin(GL_QUADS);
				glVertex2d(gx*gridSize, gy*gridSize);
				glVertex2d(gx*gridSize + gridSize, gy*gridSize);
				glVertex2d(gx*gridSize + gridSize, gy*gridSize + gridSize);
				glVertex2d(gx*gridSize, gy*gridSize + gridSize);
			glEnd();
		}
		
	}
	
	public static void main(String[] args) {
		try {
			new TetrisMain();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}

package game.maze;

import graphics.Color3f;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import math.geom.Point2i;
import math.geom.Point3f;

public class Maze {

	public final static float HALLWIDTH = 4.0f;
	public final static float HALLHEIGHT = 5.0f;
	
	private MazeType[][] maze;
	public int MAZEX, MAZEZ;
	
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public ArrayList<BasicEnemy> enemies = new ArrayList<BasicEnemy>();
	private Item[][] items;

	public ArrayList<Point2i> spawnPoints = new ArrayList<Point2i>();
	
	public Maze(MazeType[][] maze, int MAZEX, int MAZEZ) {
		this.maze = maze;
		this.MAZEX = MAZEX;
		this.MAZEZ = MAZEZ;
		items = new Item[MAZEX][MAZEZ];
		respawnItems();
	}
	public Maze() {}
	
	public MazeType get(int x, int z) {
		if(x<MAZEX && x>=0 && z<MAZEZ && z>=0) return maze[x][z];
		else return MazeType.SPACE;
	}
	public Item getItem(int x, int z) {
		if(x<MAZEX && x>=0 && z<MAZEZ && z>=0) return items[x][z];
		else return null;
	}
	public void setItem(int x, int z, Item item) {
		if(x<MAZEX && x>=0 && z<MAZEZ && z>=0) items[x][z] = item;
	}
	
	public boolean isInBounds(Point2i p) {
		return p.x>=0 && p.x<MAZEX && p.y>=0 && p.y<MAZEZ;
	}
	
	public void respawnItems() {
		for(int i=0; i<MAZEX; i++) {
			for(int j=0; j<MAZEZ; j++) {
				switch(get(i, j)) {
				case ITEM_HEALTHBONUS:
					items[i][j] = new HealthBonusItem(i, j);
					break;
				case ITEM_AMMOBONUS:
					items[i][j] = new AmmoBonusItem(i, j);
					break;
				default:
					items[i][j] = null;
					break;
				}
			}
		}
	}
	
	public static Maze loadMaze(String fileName) {
		int MAZEX, MAZEZ;
		System.out.println("Loading map: " + fileName);
		try {
			BufferedImage img = ImageIO.read(new File(fileName));
			MAZEX = img.getWidth();
			MAZEZ = img.getHeight();
			MazeType[][] maze = new MazeType[MAZEX][MAZEZ];
			Raster r = img.getRaster();
			Color3f wall = new Color3f(0.0f, 0.0f, 0.0f);
			Color3f player = new Color3f(0.0f, 0.0f, 255.0f);
			Color3f enemy = new Color3f(255.0f, 0.0f, 0.0f);
			Color3f goal = new Color3f(0.0f, 255.0f, 0.0f);
			Color3f item_healthbonus = new Color3f(255.0f, 0.0f, 255.0f);
			Color3f item_ammobonus = new Color3f(0.0f, 255.0f, 255.0f);
			float[] c = new float[4];
			for(int i=0; i<img.getWidth(); i++) {
				for(int j=0; j<img.getHeight(); j++) {
					c = r.getPixel(i, j, c);
					//System.out.println("c[" + i + "][" + j + "] = " + c[0] + "," + c[1] + "," + c[2] + "," + c[3]);
					if(c[0]==wall.r && c[1]==wall.g && c[2]==wall.b) maze[i][j] = MazeType.WALL;
					else if(c[0]==player.r && c[1]==player.g && c[2]==player.b) maze[i][j] = MazeType.PLAYER;
					else if(c[0]==enemy.r && c[1]==enemy.g && c[2]==enemy.b) maze[i][j] = MazeType.ENEMY;
					else if(c[0]==goal.r && c[1]==goal.g && c[2]==goal.b) maze[i][j] = MazeType.GOAL;
					else if(c[0]==item_healthbonus.r && c[1]==item_healthbonus.g && c[2]==item_healthbonus.b) maze[i][j] = MazeType.ITEM_HEALTHBONUS;
					else if(c[0]==item_ammobonus.r && c[1]==item_ammobonus.g && c[2]==item_ammobonus.b) maze[i][j] = MazeType.ITEM_AMMOBONUS;
					else maze[i][j] = MazeType.SPACE;
				}
			}
			return new Maze(maze, MAZEX, MAZEZ);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String createDataPacket() {
		String s = MAZEX + " " + MAZEZ + " ";
		for(int i=0; i<MAZEX*MAZEZ; i++) {
			if(get(i/MAZEX, i%MAZEX) == MazeType.WALL) s += 1;
			else s += 0;
		}
		return s;
	}
	
	public void findSpawns() {
		for(int i=0; i<MAZEX; i++) {
        	for(int j=0; j<MAZEZ; j++) {
        		if(get(i, j) == MazeType.PLAYER) {
        			//camera.setPos(new Vector3f(HALLWIDTH*i+HALLWIDTH/2, 2.0f, HALLWIDTH*j+HALLWIDTH/2));
        			//printInfo("Found player at (" + i + "," + j + ")");
        			spawnPoints.add(new Point2i(i, j));
        			return;
        		}
        	}
        }
	}
	
}

package game.maze;

import graphics.Color3f;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Maze {

	private MazeType[][] maze;
	public int MAZEX, MAZEZ;
	
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	
	public Maze(MazeType[][] maze, int MAZEX, int MAZEZ) {
		this.maze = maze;
		this.MAZEX = MAZEX;
		this.MAZEZ = MAZEZ;
	}
	public Maze() {}
	
	public MazeType get(int x, int z) {
		if(x<MAZEX && x>=0 && z<MAZEZ && z>=0) return maze[x][z];
		else return MazeType.SPACE;
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
			float[] c = new float[4];
			for(int i=0; i<img.getWidth(); i++) {
				for(int j=0; j<img.getHeight(); j++) {
					c = r.getPixel(i, j, c);
					//System.out.println("c[" + i + "][" + j + "] = " + c[0] + "," + c[1] + "," + c[2] + "," + c[3]);
					if(c[0]==wall.r && c[1]==wall.g && c[2]==wall.b) maze[i][j] = MazeType.WALL;
					else if(c[0]==player.r && c[1]==player.g && c[2]==player.b) maze[i][j] = MazeType.PLAYER;
					else if(c[0]==enemy.r && c[1]==enemy.g && c[2]==enemy.b) maze[i][j] = MazeType.ENEMY;
					else if(c[0]==goal.r && c[1]==goal.g && c[2]==goal.b) maze[i][j] = MazeType.GOAL;
					else maze[i][j] = MazeType.SPACE;
				}
			}
			return new Maze(maze, MAZEX, MAZEZ);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

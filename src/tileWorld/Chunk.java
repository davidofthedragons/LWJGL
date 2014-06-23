package tileWorld;

import java.io.File;
import java.util.ArrayList;

import math.geom.Point3f;
import tileWorld.tiles.Tile;

public class Chunk {

	public static final int SIZE = 16;
	
	 ArrayList<Tile> tiles = new ArrayList<Tile>();
//	Tile[][][] tiles = new Tile[SIZE][SIZE][SIZE];
	private BoundArea bounds;
	
	public Chunk(Tile[][][] tiles) {
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				for(int k=0; k<SIZE; k++) {
					if(tiles[i][j][k]==null) continue;
//					System.out.println("Adding tile " + tiles[i][j][k].name + " at " + tiles[i][j][k].getBounds().getCenter());
					if(i>0 && tiles[i-1][j][k]!=null) tiles[i][j][k].west = tiles[i-1][j][k];
					if(i<tiles.length-1 && tiles[i+1][j][k]!=null) tiles[i][j][k].east = tiles[i+1][j][k];
					if(k<tiles[0][0].length-1 && tiles[i][j][k+1]!=null) tiles[i][j][k].north = tiles[i][j][k+1];
					if(k>0 && tiles[i][j][k-1]!=null) tiles[i][j][k].south = tiles[i][j][k-1];
					if(j<tiles[0].length && tiles[i][j+1][k]!=null) tiles[i][j][k].above = tiles[i][j+1][k];
					if(j>0 && tiles[i][j-1][k]!=null) tiles[i][j][k].below = tiles[i][j-1][k];
					this.tiles.add(tiles[i][j][k]);
					
				}
			}
		}
	}
	
	public Tile get(Point3f pos) {
		for(Tile t : tiles) {
			if(t.getBounds().contains(pos)) return t;
		}
		return null;
	}
	public void addTile(Tile t) {
		tiles.add(t);
	}
	
	public static Chunk load(File f) {
		return null;
	}

}

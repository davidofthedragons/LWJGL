package tileWorld;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import math.geom.Point3f;
import math.geom.Point3i;
import tileWorld.tiles.Tile;
import utils.Utils;

public class Chunk implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int SIZE = 16;
	public static final int HEIGHT = 128;
	
//	ArrayList<Tile> tiles = new ArrayList<Tile>();
	public Tile[][][] tileMap = new Tile[SIZE][HEIGHT][SIZE];
	private BoundRect bounds;
	
	public Chunk(Tile[][][] tiles, BoundRect bounds) {
		this.tileMap = tiles;
		this.bounds = bounds;
		calcNeighbors();
	}
	
	public void calcNeighbors() {
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<HEIGHT; j++) {
				for(int k=0; k<SIZE; k++) {
					if(tileMap[i][j][k]==null) continue;
					if(i>0 && tileMap[i-1][j][k]!=null) tileMap[i][j][k].west = tileMap[i-1][j][k];
					if(i<tileMap.length-1 && tileMap[i+1][j][k]!=null) tileMap[i][j][k].east = tileMap[i+1][j][k];
					if(k<tileMap[0][0].length-1 && tileMap[i][j][k+1]!=null) tileMap[i][j][k].north = tileMap[i][j][k+1];
					if(k>0 && tileMap[i][j][k-1]!=null) tileMap[i][j][k].south = tileMap[i][j][k-1];
					if(j<tileMap[0].length && tileMap[i][j+1][k]!=null) tileMap[i][j][k].above = tileMap[i][j+1][k];
					if(j>0 && tileMap[i][j-1][k]!=null) tileMap[i][j][k].below = tileMap[i][j-1][k];
//					this.tiles.add(tileMap[i][j][k]);
				}
			}
		}
	}
	
	public Tile get(Point3f pos) {
		int x = (int) (Utils.round(pos.x, Tile.xlength) - bounds.ba.x);
		int y = (int) (Utils.round(pos.y, Tile.height) - bounds.ba.y);
		int z = (int) (Utils.round(pos.z, Tile.zlength) - bounds.ba.z);
		return get(x, y, z);
//		for(Tile t : tiles) {
//			if(t.getBounds().contains(pos)) return t;
//		}
//		return null;
	}
	
	public Tile get(int x, int y, int z) {
		return (x>=0 && x<SIZE && y>=0 && y<HEIGHT && z>=0 && z<SIZE)? tileMap[x][y][z] : null;
	}
	
	public float getGroundHeight(Point3f pos) {
//		for(Tile t : tiles) {
//			if(!t.getBounds().contains(new Point3f(pos.x, t.getBounds().getCenter().y, pos.z))) continue;
//			if(t.getBounds().getta().y > groundHeight && t.getBounds().getta().y<=pos.y+1.0f) groundHeight = t.getBounds().getta().y;
//		}
		Point3i ap = getArrayPos(pos);
		Tile topTile = tileMap[ap.x][0][ap.z];
		for(int i=1; i<HEIGHT; i++) {
			if(get(ap.x, i, ap.z) != null) topTile = get(ap.x, i, ap.z);
			else break;
		}
		return (topTile != null)? topTile.getBounds().ta.y : 0.0f;
	}
	
	public Point3i getArrayPos(Point3f p) {
		int x = (int) (Utils.round(p.x - bounds.ba.x, Tile.xlength-1));
		int y = (int) (Utils.round(p.y - bounds.ba.y, Tile.height-1));
		int z = (int) (Utils.round(p.z - bounds.ba.z, Tile.zlength-1));
		return new Point3i(x, y, z);
	}
	
	public void addTile(Tile t) {
		Point3i ap = getArrayPos(t.getBounds().getCenter());
		tileMap[ap.x][ap.y][ap.z] = t;
	}
	
	public void addTile(Tile tileType, int x, int y, int z) {
		Tile t = tileType;
		if(this.bounds == null) System.out.println("bounds is null");
		t.setBounds(new BoundRect(new Point3f(this.bounds.ba.x+Tile.xlength*x, this.bounds.ba.y+Tile.height*y, this.bounds.ba.z+Tile.zlength*z),
				Tile.xlength, Tile.height, Tile.zlength));
		if(x>0 && tileMap[x-1][y][z]!=null) t.west = tileMap[x-1][y][z];
		if(x<tileMap.length-1 && tileMap[x+1][y][z]!=null) t.east = tileMap[x+1][y][z];
		if(z<tileMap[0][0].length-1 && tileMap[x][y][z+1]!=null) t.north = tileMap[x][y][z+1];
		if(z>0 && tileMap[x][y][z-1]!=null) t.south = tileMap[x][y][z-1];
		if(y<tileMap[0].length && tileMap[x][y+1][z]!=null) t.above = tileMap[x][y+1][z];
		if(y>0 && tileMap[x][y-1][z]!=null) t.below = tileMap[x][y-1][z];
		tileMap[x][y][z] = t;
//		this.tiles.add(t);
	}
	
	public static Chunk load(File f) {
		return null;
	}
	
	public BoundRect getBounds() {
		return bounds;
	}
	
//	public boolean hitsTile(Point3f pos) {
//		pos.y += 0.1f;
//		for(Tile t : tiles) {
//			if(t.getBounds().contains(pos)) return true;
//		}
//		return false;
//	}
	
	public boolean hitsTile(BoundRect bounds, int checkRadius) {
		int x = (int) (Utils.round(bounds.ba.x-this.bounds.ba.x, Tile.xlength)/Tile.xlength);
		int y = (int) (Utils.round(bounds.ba.y-this.bounds.ba.y, Tile.height)/Tile.height);
		int z = (int) (Utils.round(bounds.ba.z-this.bounds.ba.z, Tile.zlength)/Tile.zlength);
		if (x>0 && x<SIZE-1 && y>0 && y<HEIGHT-1 && z>0 && z<SIZE-1) {
			if(tileMap[x][y][z]!=null && tileMap[x][y][z].getBounds().collides(bounds)) return true;
			for (int i=0; i<checkRadius; i++) {
				for (int j=-2; j<5; j++) { //j might check for blocks within player's height- find a better way to do this
					if(y+j <= 0 || y+j >= HEIGHT-1) continue;
					if (x + i < SIZE - 1)
						if (tileMap[x + i][y+j][z] != null && tileMap[x+i][y+j][z].getBounds().collides(bounds))
							return true;
					if (x - i > 0)
						if (tileMap[x - i][y+j][z] != null && tileMap[x-i][y+j][z].getBounds().collides(bounds))
							return true;
					if (y+i < HEIGHT-1)
						if (tileMap[x][y+j+i][z] != null && tileMap[x][y+j+i][z].getBounds().collides(bounds))
							return true;
//					if (y - i > 0)
//						if (tileMap[x][y+j-i][z] != null && tileMap[x][y+j-i][z].getBounds().collides(bounds))
//							return true;
					if (z + i < SIZE - 1)
						if (tileMap[x][y+j][z + i] != null && tileMap[x][y+j][z + i].getBounds().collides(bounds))
							return true;
					if (z - i > 0)
						if (tileMap[x][y+j][z - i] != null && tileMap[x][y+j][z - i].getBounds().collides(bounds))
							return true;
					if (x + i < SIZE - 1 && z + i < SIZE - 1)
						if (tileMap[x + i][y+j][z + i] != null && tileMap[x + i][y+j][z + i].getBounds().collides(bounds))
							return true;
					if (x - i > 0 && z + i < SIZE - 1)
						if (tileMap[x - i][y+j][z + i] != null && tileMap[x - i][y+j][z + i].getBounds().collides(bounds))
							return true;
					if (x + i < SIZE - 1 && z - i > 0)
						if (tileMap[x + i][y+j][z - i] != null && tileMap[x + i][y+j][z - i].getBounds().collides(bounds))
							return true;
					if (x - i > 0 && z - i > 0)
						if (tileMap[x - i][y+j][z - i] != null && tileMap[x - i][y+j][z - i].getBounds().collides(bounds))
							return true;
				}
				
			}
		} else return false;
		return false;
	}
	
}

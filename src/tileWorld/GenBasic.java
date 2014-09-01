package tileWorld;

import java.util.Random;

import math.geom.Point3f;
import tileWorld.tiles.Tile;
import tileWorld.tiles.*;

public class GenBasic {

	Random rand = new Random();
	
	public GenBasic(int seed) {
		rand = new Random(seed);
	}

	public Chunk genChunk(float startx, float startz) {
//		Chunk chunk = new Chunk();
		Tile[][][] tiles = new Tile[Chunk.SIZE][Chunk.HEIGHT][Chunk.SIZE];
		for(int i=0; i<Chunk.SIZE; i++) {
			for(int j=0; j<Chunk.SIZE; j++) {
				for(int k=0; k<Chunk.SIZE; k++) {
					tiles[i][j][k] = null;
				}
			}
		}
		/*for(int i=0; i<Chunk.SIZE; i++) {
			for(int j=0; j<Chunk.SIZE; j++) {
//				System.out.println(i + ", " + j);
				TileGrass t = new TileGrass(new BoundRect(new Point3f(startx+(i*Tile.xlength), Tile.height, startz+(j*Tile.zlength)),
						new Point3f(startx+(i*Tile.xlength)+Tile.xlength, 0.0f, startz+(j*Tile.zlength)+Tile.zlength)));
				System.out.println("Adding tile " + t.name + " at " + t.getBounds().getbc());
				tiles[i][0][j] = t;
			}
		}*/
		for(int i=0; i<Chunk.SIZE; i++) {
			for(int j=0; j<Chunk.SIZE; j++) {
				
				Tile t = new TileStone(new BoundRect(new Point3f(startx+(i*Tile.xlength), 0.0f, startz+(j*Tile.zlength)),
						Tile.xlength, Tile.height, Tile.zlength));
//				System.out.println("Adding tile " + t.name + " at " + t.getBounds().getCenter());
				tiles[i][0][j] = t;
			}
		}
		for(int i=0; i<Chunk.SIZE/2; i++) {
			for(int j=0; j<Chunk.SIZE/2; j++) {
				Tile t = new TileStone(new BoundRect(new Point3f(startx+(i*Tile.xlength), Tile.height, startz+(j*Tile.zlength)),
						Tile.xlength, Tile.height, Tile.zlength));
				System.out.println("Adding tile " + t.name + " at " + t.getBounds().getCenter());
				tiles[i][1][j] = t;
			}
		}
		for(int i=0; i<Chunk.SIZE/4; i++) {
			for(int j=0; j<Chunk.SIZE/4; j++) {
				Tile t = new TileStone(new BoundRect(new Point3f(startx+(i*Tile.xlength), Tile.height*2, startz+(j*Tile.zlength)),
						Tile.xlength, Tile.height, Tile.zlength));
				tiles[i][2][j] = t;
			}
		}
		for(int i=0; i<Chunk.SIZE/8; i++) {
			for(int j=0; j<Chunk.SIZE/8; j++) {
				Tile t = new TileGrass(new BoundRect(new Point3f(startx+(i*Tile.xlength), Tile.height*3, startz+(j*Tile.zlength)),
						Tile.xlength, Tile.height, Tile.zlength));
				System.out.println("Adding tile " + t.name + " at " + t.getBounds().getCenter());
				tiles[i][3][j] = t;
			}
		}
		Chunk chunk = new Chunk(tiles, new BoundRect(new Point3f(startx, 0.0f, startz),
				Chunk.SIZE*Tile.xlength, Chunk.SIZE*Tile.height, Chunk.SIZE*Tile.zlength));
		return chunk;
	}
	
}

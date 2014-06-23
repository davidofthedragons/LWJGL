package tileWorld;

import java.util.Random;

import math.geom.Point3f;
import tileWorld.tiles.Tile;
import tileWorld.tiles.TileGrass;

public class GenBasic {

	Random rand = new Random();
	
	public GenBasic(int seed) {
		rand = new Random(seed);
	}

	public Chunk genChunk(float startx, float startz) {
//		Chunk chunk = new Chunk();
		Tile[][][] tiles = new Tile[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
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
				
				TileGrass t = new TileGrass(new BoundRect(new Point3f(startx+(i*Tile.xlength), 0.0f, startz+(j*Tile.zlength)),
						Tile.xlength, Tile.height, Tile.zlength));
//				System.out.println("Adding tile " + t.name + " at " + t.getBounds().getta());
				tiles[i][0][j] = t;
			}
		}
		Chunk chunk = new Chunk(tiles);
		return chunk;
	}
	
}

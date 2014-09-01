package tileWorld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Scanner;

import lib.game.RenderUtils;

public class Assets {

	public static String saveLoc = "res/tileWorld/saves/BasicWorld";
	private static HashMap<String, Integer> tileIDs = new HashMap<String, Integer>();
	private static HashMap<Integer, String> tileNames = new HashMap<Integer, String>();
	private static HashMap<String, Integer> textures = new HashMap<String, Integer>();
	private static HashMap<String, Integer> displayLists = new HashMap<String, Integer>();
	
	public static boolean load(String assetsRoot) {
		return loadTextures(new File(assetsRoot + "assets/textures/textureList"))
				&& loadTileIDs(new File(assetsRoot + "assets/tileIDs"));
	}
	
	public static int loadTexture(String name, String location) {
		if(textures.containsKey(name)) return textures.get(name);
		int texid = RenderUtils.loadTexture(location, true);
		textures.put(name, texid);
		return texid;
	}
	
	public static int getTexture(String name) {
		return textures.get(name);
	}
	
	public static boolean loadTextures(File textureList) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(textureList);
			String line;
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				System.out.println(line);
				String name = line.split(":")[0];
				String fileName = line.split(":")[1];
				loadTexture(name, fileName);
			}
			scanner.close();
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean loadTileIDs(File tileIDList) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(tileIDList);
			String line;
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
//				System.out.println(line);
				Integer id = new Integer(line.split(":")[0]);
				String name = line.split(":")[1];
				tileIDs.put(name, id);
				tileNames.put(id, name);
			}
			scanner.close();
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int getTileID(String name) {
		System.out.println(name);
		return tileIDs.get(name);
	}
	
	public static String getTilename(int id) {
		return tileNames.get(id);
	}
	
	public static void saveChunk(Chunk chunk) {
		FileOutputStream fout;
		try {
			File file = new File(saveLoc + "/level_" + chunk.getBounds().ba.x + "_" + chunk.getBounds().ba.z + ".sav");
			File dir = new File(saveLoc);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			if(!file.exists()) {
				file.createNewFile();
			}
			ChunkSaved cs = new ChunkSaved(chunk);
			fout = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(cs);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Chunk loadChunk(File chunkFile) {
		Chunk chunk = null;
		FileInputStream fin;
		try {
			fin = new FileInputStream(chunkFile);
			ObjectInputStream in = new ObjectInputStream(fin);
			chunk = (Chunk) in.readObject();
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return chunk;
	}
}

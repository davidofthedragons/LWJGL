package tileWorld;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import lib.game.RenderUtils;

public class Assets {

	private static HashMap<String, Integer> textures = new HashMap<String, Integer>();
	private static HashMap<String, Integer> displayLists = new HashMap<String, Integer>();
	
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
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
}

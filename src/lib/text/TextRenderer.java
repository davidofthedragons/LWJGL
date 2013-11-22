package lib.text;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import math.geom.Point2d;
import math.geom.Point2f;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class TextRenderer {

	String fileName = "res/TextTexture2.png";
	String text = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890`-=[]\\;',./~!@#$%^&()_+{}|:\"<>?* \n";
	Texture tex;
	
	public TextRenderer() throws FileNotFoundException, IOException {
		tex = TextureLoader.getTexture("png", new FileInputStream(new File(fileName)));
	}
	public TextRenderer(String fileName) throws FileNotFoundException, IOException {
		this.fileName=fileName;
		tex = TextureLoader.getTexture("png", new FileInputStream(new File(fileName)));
	}
	
	public void renderText(String s, double x, double y, double size) {
		tex.bind();
		glBegin(GL_QUADS);
		double end = 252.0/256.0;
		for(int i=0; i<s.length(); i++) {
			Point2d loc = new Point2d((9.0/end)*(text.indexOf(s.charAt(i)%28)), (19.0/256.0)*(text.indexOf(s.charAt(i))%28));
			glTexCoord2d(loc.x, loc.y);
			glVertex2d(x+(size*i), y);
			glTexCoord2d(loc.x+(9.0/256), loc.y);
			glVertex2d(x+(size*i)+size, y);
			glTexCoord2d(loc.x+(9.0/256), loc.y+(19.0/256.0));
			glVertex2d(x+(size*i)+size, y+size);
			glTexCoord2d(loc.x, loc.y+(19.0/256.0));
			glVertex2d(x+(size*i), y+size);
		}
		glTexCoord2d(0.0, 0.0);
		glVertex2d(100, 100);
		glTexCoord2d(9.0/256.0, 0.0);
		glVertex2d(300, 100);
		glTexCoord2d(9.0/256.0, 19.0/256.0);
		glVertex2d(300, 300);
		glTexCoord2d(0.0, 19.0/256.0);
		glVertex2d(100, 300);
		glEnd();
	}
}

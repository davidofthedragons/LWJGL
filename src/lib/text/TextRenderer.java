package lib.text;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import math.geom.Point2d;
import math.geom.Point2f;
import math.geom.Point2i;
import math.geom.Point3f;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * Class providing methods for drawing text in OpenGL
 * @author David Gardner
 *
 */
public class TextRenderer {
	
	Texture texture;
	
	final float TEX_WIDTH = 256;
	final float TEX_HEIGHT = 256;
	final float CHAR_WIDTH = TEX_WIDTH/16;
	final float CHAR_HEIGHT = TEX_HEIGHT/14;
	
	/**
	 * Loads the font
	 * @param textureFile image for the font
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public TextRenderer(File textureFile) throws FileNotFoundException, IOException {
		texture = TextureLoader.getTexture("jpg", new FileInputStream(textureFile));
	}
	
	/**
	 * Draws text at the given coordinates
	 * @param text the string to draw
	 * @param pos the position of the text
	 * @param fontWidth width of the characters
	 * @param fontHeight height of the characters
	 */
	public void renderText(String text, Point2f pos, float fontWidth, float fontHeight) {
		glEnable(GL_TEXTURE_2D);
		texture.bind();
		glPushAttrib(GL_COLOR_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		glPushMatrix();
		glTranslatef(pos.x, pos.y, 0.0f);
		
		int returns = 0;
		int textPos = 0;
		for(int i=0; i<text.length(); i++) {
			glPushMatrix();
			Point2f texCoord = new Point2f(((text.charAt(i)-32)%16)*CHAR_WIDTH,
					TEX_HEIGHT-((text.charAt(i)-32)/16)*CHAR_HEIGHT);
			if(text.charAt(i) == '\n') {
				returns++;
				textPos = 0;
				continue;
			}
			int line = (text.charAt(i)-32)/16 + 1;
			int row = (text.charAt(i)-32)%16;
			Point2f topLeft = new Point2f(row*CHAR_WIDTH, /*TEX_HEIGHT-*/line*CHAR_HEIGHT);
			glTranslatef(textPos*fontWidth, returns*fontHeight, 0.0f);
			glBegin(GL_QUADS); {
				
				glTexCoord2f(topLeft.x/((float)TEX_WIDTH), topLeft.y/TEX_HEIGHT);
		        glVertex2f(0, fontHeight);
		        glTexCoord2f( topLeft.x/((float)TEX_WIDTH), (topLeft.y-CHAR_HEIGHT)/TEX_HEIGHT);
		        glVertex2f(0, 0);
		        glTexCoord2f((topLeft.x+CHAR_WIDTH)/((float)TEX_WIDTH), (topLeft.y-CHAR_HEIGHT)/TEX_HEIGHT);
		        glVertex2f(fontWidth, 0);
		        glTexCoord2f((topLeft.x+CHAR_WIDTH)/((float)TEX_WIDTH), topLeft.y/TEX_HEIGHT);
		        glVertex2f(fontWidth, fontHeight);
				
//				glVertex2f(pos.x + i*fontWidth, pos.y); 
//				glTexCoord2f(texCoord.x/TEX_WIDTH, texCoord.y/TEX_HEIGHT);
//				
//				glVertex2f(pos.x + i*fontWidth + fontWidth, pos.y); 
//				glTexCoord2f((texCoord.x + CHAR_WIDTH)/TEX_WIDTH, texCoord.y/TEX_HEIGHT);
//				
//				glVertex2f(pos.x + i*fontWidth + fontWidth, pos.y + fontHeight); 
//				glTexCoord2f((texCoord.x + CHAR_WIDTH)/TEX_WIDTH, (texCoord.y + CHAR_HEIGHT)/TEX_HEIGHT);
//				
//				glVertex2f(pos.x + i*fontWidth, pos.y + fontHeight); 
//				glTexCoord2f(texCoord.x/TEX_WIDTH, (texCoord.y + CHAR_HEIGHT)/TEX_HEIGHT);
			}glEnd();
			glPopMatrix();
			textPos++;
		}
		glDisable(GL_TEXTURE_2D);
		glPopMatrix();
		glPopAttrib();
	}
	
	/**
	 * Draws text at the given coordinates
	 * @param text the string to draw
	 * @param pos the position of the text
	 * @param fontWidth width of the characters
	 * @param fontHeight height of the characters
	 */
	public void renderText(String text, Point2i pos, int fontWidth, int fontHeight) {
		Point2f p = new Point2f(pos.x, pos.y);
		renderText(text, p, fontWidth, fontHeight);
	}
	
	/**
	 * Draws text at the given coordinates
	 * @param text the string to draw
	 * @param pos the position of the text
	 * @param fontWidth width of the characters
	 * @param fontHeight height of the characters
	 */
	public void renderText(String text, Point2d pos, double fontWidth, double fontHeight) {
		Point2d p = new Point2d(pos.x, pos.y);
		renderText(text, p, fontWidth, fontHeight);
	}
	
	
	
	/*
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
	//*/
}

package lib.game;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class RenderUtils {
	
	public static void clearScreen() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static FloatBuffer asFloatBuffer(float[] values) {
		FloatBuffer b = BufferUtils.createFloatBuffer(values.length);
		b.put(values);
		b.flip();
		return b;
	}
	
	public static void initStd2d(int width, int height) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
	}
	
	public static Texture loadTexture(String fileName) {
		try {
			return TextureLoader.getTexture("png", new FileInputStream(new File(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void drawLineCube(float SIZE) {
		glBegin(GL_LINES); {
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, SIZE, 0.0f);   //Front
			glVertex3f(SIZE, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			
			glVertex3f(0.0f, 0.0f, SIZE);
			glVertex3f(SIZE, 0.0f, SIZE);
			glVertex3f(SIZE, 0.0f, SIZE);
			glVertex3f(SIZE, SIZE, SIZE);
			glVertex3f(SIZE, SIZE, SIZE);   //Back
			glVertex3f(0.0f, SIZE, SIZE);
			glVertex3f(0.0f, SIZE, SIZE);
			glVertex3f(0.0f, 0.0f, SIZE);
			
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, SIZE);
			
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, SIZE);   //Sides
			
			glVertex3f(SIZE, SIZE, 0.0f);
			glVertex3f(SIZE, SIZE, SIZE);
			
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, SIZE);//*/
		} glEnd();
	}
	
}

package lib.game;

import static org.lwjgl.opengl.GL11.*;
import graphics.Color3f;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * A set of static convenience methods for LWJGL
 * 
 * @author David Gardner
 * 
 */
public class RenderUtils {

	/**
	 * Clears the color buffer and the depth buffer of the OpenGL context
	 */
	public static void clearScreen() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Convenience method to construct a FloatBuffer from the given array
	 * 
	 * @param values
	 *            the array from which to construct the FloatBuffer
	 * @return the FloatBuffer
	 */
	public static FloatBuffer asFloatBuffer(float[] values) {
		FloatBuffer b = BufferUtils.createFloatBuffer(values.length);
		b.put(values);
		b.flip();
		return b;
	}

	/**
	 * Initializes standard 2D screen with the given width and height. (0,0) is
	 * in the upper left corner
	 * 
	 * @param width
	 *            width of the screen
	 * @param height
	 *            height of the screen
	 */
	public static void initStd2d(int width, int height) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
	}

	/**
	 * Constructs a SlickUtil texture from the given png file
	 * 
	 * @param fileName
	 *            name of the png file
	 * @return the SlickUtil Texture object
	 */
	public static Texture loadTexture(String fileName) {
		try {
			return TextureLoader.getTexture("png", new FileInputStream(
					new File(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int loadTexture(String fileName, boolean alpha) {
		int BYTES_PER_PIXEL = 3;
		if (alpha)
			BYTES_PER_PIXEL = 4;
		BufferedImage image = RenderUtils.loadImage(fileName);

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0,
				image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()
				* image.getHeight() * BYTES_PER_PIXEL); // 4 for RGBA, 3 for RGB

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				if(alpha) buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
															// Only for RGBA
			}
		}

		buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS

		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:

		int textureID = glGenTextures(); // Generate texture ID
		glBindTexture(GL_TEXTURE_2D, textureID); // Bind texture ID

		// Setup wrap mode
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Send texel data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(),
				image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		// Return the texture ID so we can bind it later again
		return textureID;
	}
	
	public static int loadTexture(BufferedImage image, boolean alpha) {
		int BYTES_PER_PIXEL = 3;
		if (alpha)
			BYTES_PER_PIXEL = 4;

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0,
				image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()
				* image.getHeight() * BYTES_PER_PIXEL); // 4 for RGBA, 3 for RGB

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				if(alpha) buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
															// Only for RGBA
			}
		}

		buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS

		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:

		int textureID = glGenTextures(); // Generate texture ID
		glBindTexture(GL_TEXTURE_2D, textureID); // Bind texture ID

		// Setup wrap mode
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Send texel data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(),
				image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		// Return the texture ID so we can bind it later again
		return textureID;
	}
	
	public static void bindTexture(int texID) {
		glBindTexture(GL_TEXTURE_2D, texID);
	}
	
	public static void releaseTexture(int texID) {
		glDeleteTextures(texID);
	}

	public static BufferedImage loadImage(String fileName) {
		try {
			return ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static int displayListID = 0;

	public static int nextDisplayListID() {
		return displayListID++;
	}

	/**
	 * Draws a wireframe cube of the given size
	 * 
	 * @param SIZE
	 *            size of the cube to draw
	 */
	public static void drawLineCube(float SIZE) {
		glBegin(GL_LINES);
		{
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, SIZE, 0.0f); // Front
			glVertex3f(SIZE, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);

			glVertex3f(0.0f, 0.0f, SIZE);
			glVertex3f(SIZE, 0.0f, SIZE);
			glVertex3f(SIZE, 0.0f, SIZE);
			glVertex3f(SIZE, SIZE, SIZE);
			glVertex3f(SIZE, SIZE, SIZE); // Back
			glVertex3f(0.0f, SIZE, SIZE);
			glVertex3f(0.0f, SIZE, SIZE);
			glVertex3f(0.0f, 0.0f, SIZE);

			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, SIZE);

			glVertex3f(SIZE, 0.0f, 0.0f);
			glVertex3f(SIZE, 0.0f, SIZE); // Sides

			glVertex3f(SIZE, SIZE, 0.0f);
			glVertex3f(SIZE, SIZE, SIZE);

			glVertex3f(0.0f, SIZE, 0.0f);
			glVertex3f(0.0f, SIZE, SIZE);// */
		}
		glEnd();
	}
	
	public static void applyColor(Color3f color) {
		glColor3f(color.r, color.g, color.b);
	}

}

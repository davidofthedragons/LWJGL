package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import lib.text.TextRenderer;
import math.geom.Point2f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;

public class QuadTest {

	boolean closeRequested = false;
	double width = 600;
	double height = 600;
	int fps = 60;
	
	int mouseX = 0;
	int mouseY = 0;
	
	Texture javaTex;
	TextRenderer text;
	
	public QuadTest() throws LWJGLException, FileNotFoundException, IOException {
		
		init();
		while(!closeRequested) {
			closeRequested = Display.isCloseRequested();
			processInput();
			renderScene();
			Display.update();
			Display.sync(fps);
		}
		onClose();
		
	}
	
	private void init() throws LWJGLException, FileNotFoundException, IOException {
		Display.setDisplayMode(new DisplayMode((int)width, (int)height));
		Display.setTitle("Fun with shapes");
		Display.setFullscreen(true);
		Display.create();
		
		//glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 1.0, -1.0);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
		glLoadIdentity();
		/*glLoadIdentity();
		glShadeModel(GL_SMOOTH);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1.0f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);*/
		javaTex = loadTexture("Java");
		text = new TextRenderer(new File("res/font1.jpg"));
	}
	int count = 0;
	private void renderScene() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
//		glColor3f(0.0f, 1.0f, 0.0f);
		drawSquare(mouseX-50, (float)height-mouseY-50, 0, 100, javaTex);
		text.renderText(count + "", new Point2f(0.0f, 0.0f), 30.0f, 50.0f);
		count++;
	}
	
	private void drawSquare(float sx, float sy, float sz, float size, Texture t) {
		t.bind();
		//glColor3f(0.0f, 1.0f, 0.0f);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0);
			glVertex3f(sx, sy, sz);
			glTexCoord2f(1, 0);
			glVertex3f(sx+size, sy, sz);
			glTexCoord2f(1, 1);
			glVertex3f(sx+size, sy+size, sz);
			glTexCoord2f(0, 1);
			glVertex3f(sx, sy+size, sz);
		glEnd();
	}
	
	private void processInput() {
		//System.out.println("Mouse at position (" + Mouse.getX() + ", " + Mouse.getY() + ")");
		
		if(Mouse.isButtonDown(0)) {
			//System.out.println("Left mouse button is down");
			mouseX = Mouse.getX();
			mouseY = Mouse.getY();
		}
		while(Keyboard.next()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
				if(Keyboard.getEventKeyState()) System.out.println("Space bar down");
				else System.out.println("Space bar up");
			}
			else if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				closeRequested = true;
			}
			
		}
	}
	
	private void onClose() {
		Display.destroy();
	}
	
	private Texture loadTexture(String name) {
		try {
			return TextureLoader.getTexture("png", new FileInputStream(new File("res/" + name + ".png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) throws LWJGLException, FileNotFoundException, IOException {
		new QuadTest();
	}

}
